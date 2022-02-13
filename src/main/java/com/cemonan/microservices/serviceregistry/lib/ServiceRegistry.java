package com.cemonan.microservices.serviceregistry.lib;

import com.cemonan.microservices.serviceregistry.pojo.Service;
import com.vdurmont.semver4j.Semver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class ServiceRegistry {
    @Value("${service.registry.default.timeout}")
    private String defaultTimeout;

    private Map<String, Service> services;

    public ServiceRegistry() {
        this.services = new HashMap<String, Service>();
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public Service get(String name, String version) {
        this.cleanup();
        List<Service> candidates = this.getCandidates(name, version);
        if (candidates.size() == 0) {
            return null;
        }
        return candidates.get((int) Math.floor(Math.random() * candidates.size()));
    }

    protected List<Service> getCandidates(String name, String version) {
        List<Service> candidates = new ArrayList<>();
        for (String serviceId : this.services.keySet()) {
            Service service = this.services.get(serviceId);
            if (name.equals(service.getName()) && this.isServiceSatisfiesVersion(version, service.getVersion())) {
                candidates.add(service);
            }
        }
        return candidates;
    }

    private Boolean isServiceSatisfiesVersion(String providedVersion, String currentVersion) {
        Semver semver = new Semver(currentVersion, Semver.SemverType.NPM);
        return semver.satisfies(providedVersion);
    }

    public String add(String name, String version, String ip, String port) {
        this.cleanup();
        String serviceId = String.join("/", Arrays.asList(name, version, ip, port));
        long now = Instant.now().getEpochSecond();
        Service existingService = this.services.get(serviceId);
        if (existingService != null) {
            existingService.setTimestamp(now);
            return serviceId;
        }
        Service service = new Service(name, version, ip, port, now);
        this.services.put(serviceId, service);
        return serviceId;
    }

    public String delete(String name, String version, String ip, String port) {
        String serviceId = String.join("/", Arrays.asList(name, version, ip, port));
        Service existingService = this.services.get(serviceId);
        if (existingService == null) {
            return null;
        }
        this.services.remove(serviceId);
        return serviceId;
    }

    public void cleanup() {
        Long now = Instant.now().getEpochSecond();
        Iterator<Map.Entry<String, Service>> it = this.services.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Service> entry = it.next();
            Service service = entry.getValue();
            String serviceId = entry.getKey();
            if (service.getTimestamp() + Integer.parseInt(defaultTimeout) < now) {
                it.remove();
                System.out.println(
                        "Service with serviceId: "
                        + serviceId +
                        " has been removed from the registry because of that service has been expired."
                );
            }
        }
    }
}
