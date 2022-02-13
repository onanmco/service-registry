package com.cemonan.microservices.serviceregistry.lib;

import com.cemonan.microservices.serviceregistry.pojo.Service;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServiceRegistryTest extends ServiceRegistry {
    public ServiceRegistryTest() {
        super();
    }

    public List<Service> getCandidateServices(String name, String version) {
        return this.getCandidates(name, version);
    }
}
