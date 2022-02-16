package com.cemonan.microservices.serviceregistry.lib;

import com.cemonan.microservices.serviceregistry.dao.service_dao.ServiceDao;
import com.cemonan.microservices.serviceregistry.domain.Service;
import com.vdurmont.semver4j.Semver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class ServiceRegistry {

    private final ServiceDao serviceDao;

    @Value("${service.registry.default.timeout}")
    private String defaultTimeout;

    @Autowired
    public ServiceRegistry(ServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    public List<Service> getServices() {
        this.cleanup();
        return serviceDao.getAllServices();
    }

    public Service get(String name, String version) {
        this.cleanup();
        List<Service> allServices = serviceDao.getAllByNameOrderByUsedCount(name, Sort.by(Sort.Direction.ASC, "count"));
        List<Service> candidates = this.getCandidates(allServices, version);
        if (candidates.size() == 0) {
            return null;
        }
        Service service = candidates.get(0);
        service.setUsedCount(service.getUsedCount() + 1L);
        return serviceDao.update(service);
    }

    protected List<Service> getCandidates(List<Service> services, String version) {
        List<Service> candidates = new ArrayList<>();
        for (Service service : services) {
            if (isServiceSatisfiesVersion(version, service.getVersion())) {
                candidates.add(service);
            }
        }
        return candidates;
    }

    private Boolean isServiceSatisfiesVersion(String providedVersion, String currentVersion) {
        Semver semver = new Semver(currentVersion, Semver.SemverType.NPM);
        return semver.satisfies(providedVersion);
    }

    public UUID add(String name, String version, String ip, String port) {
        this.cleanup();
        long now = Instant.now().getEpochSecond();
        Service existingService = serviceDao.getServiceByNameAndVersionAndIpAndPort(name, version, ip, port);
        if (existingService != null) {
            existingService.setTimestamp(now);
            return existingService.getId();
        }
        Service service = new Service();
        service.setName(name);
        service.setVersion(version);
        service.setIp(ip);
        service.setPort(port);
        service.setTimestamp(now);
        service.setUsedCount(0L);
        Service savedService = serviceDao.save(service);
        return savedService.getId();
    }

    public UUID delete(String name, String version, String ip, String port) {
        Service existingService = serviceDao.getServiceByNameAndVersionAndIpAndPort(name, version, ip, port);
        if (existingService == null) {
            return null;
        }
        serviceDao.delete(existingService);
        return existingService.getId();
    }

    public void cleanup() {
        Long now = Instant.now().getEpochSecond();

        List<Service> allServices = serviceDao.getAllServices();

        for (Service service : allServices) {
            if (service.getTimestamp() + Integer.parseInt(defaultTimeout) < now) {
                serviceDao.delete(service);
                System.out.println(
                        String.format("Service with id: %s has been removed from the registry because it's been expired.", service.getId())
                );
            }
        }
    }
}
