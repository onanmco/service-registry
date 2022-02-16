package com.cemonan.microservices.serviceregistry.factory.service_factory;

import com.cemonan.microservices.serviceregistry.domain.Service;

import java.util.List;

public interface ServiceFactory {
    Service create();
    List<Service> create(int n);
}
