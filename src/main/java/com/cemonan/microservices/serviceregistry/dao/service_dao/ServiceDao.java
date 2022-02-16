package com.cemonan.microservices.serviceregistry.dao.service_dao;

import com.cemonan.microservices.serviceregistry.domain.Service;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface ServiceDao {
    Service save(Service service);
    Service getServiceById(UUID id);
    Service getServiceByNameAndVersionAndIpAndPort(String name, String version, String ip, String port);
    Service update(Service service);
    void delete(Service service);
    List<Service> getAllServices();
    List<Service> getAllByNameOrderByUsedCount(String name, Sort sort);
}
