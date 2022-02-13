package com.cemonan.microservices.serviceregistry.dao;

import com.cemonan.microservices.serviceregistry.pojo.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ServiceDao {
    Service save(Service service);
    Service getServiceById(Long id);
    Service getServiceByNameAndVersionAndIpAndPort(String name, String version, String ip, String port);
    Service update(Service service);
    void delete(Service service);
    List<Service> getAllServices();
    List<Service> getAllByNameOrderByUsedCount(String name, Sort sort);
}
