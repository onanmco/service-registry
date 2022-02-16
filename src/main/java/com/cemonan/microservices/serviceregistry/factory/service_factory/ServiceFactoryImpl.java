package com.cemonan.microservices.serviceregistry.factory.service_factory;

import com.cemonan.microservices.serviceregistry.domain.Service;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class ServiceFactoryImpl implements ServiceFactory {

    private final Faker faker;
    private final Random random;

    public ServiceFactoryImpl() {
        this.faker = new Faker();
        this.random = new Random();
    }

    @Override
    public Service create() {
        Service service = new Service();
        service.setName(faker.app().name());
        service.setVersion(faker.app().version());
        service.setIp(faker.internet().ipV4Address());
        service.setPort(getRandomPortNumber());
        service.setTimestamp(Instant.now().getEpochSecond());
        service.setUsedCount(0L);
        return service;
    }

    @Override
    public List<Service> create(int n) {
        List<Service> services = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            services.add(this.create());
        }
        return services;
    }

    private String getRandomPortNumber() {
        String port = "";
        while(port.length() < 4) {
            port += getRandomIntegerInclusive(1, 9);
        }
        return port;
    }

    private int getRandomIntegerInclusive(int min, int max) {
        return (int) Math.floor(random.nextDouble() * (max - min + 1) + min);
    }
}
