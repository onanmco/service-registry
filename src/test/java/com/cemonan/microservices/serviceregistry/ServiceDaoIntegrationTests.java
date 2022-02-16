package com.cemonan.microservices.serviceregistry;

import com.cemonan.microservices.serviceregistry.dao.service_dao.ServiceDao;
import com.cemonan.microservices.serviceregistry.domain.Service;
import com.cemonan.microservices.serviceregistry.factory.service_factory.ServiceFactory;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ComponentScan(basePackages = {"com.cemonan.microservices.serviceregistry.dao", "com.cemonan.microservices.serviceregistry.factory"})
public class ServiceDaoIntegrationTests {

    private final ServiceDao serviceDao;
    private final ServiceFactory serviceFactory;
    private final Faker faker;

    @Autowired
    public ServiceDaoIntegrationTests(ServiceDao serviceDao, ServiceFactory serviceFactory) {
        this.serviceDao = serviceDao;
        this.serviceFactory = serviceFactory;
        this.faker = new Faker();
    }

    @Test
    void testCreateService() {
        int countBefore = serviceDao.getAllServices().size();

        Service service = serviceFactory.create();
        Service savedService = serviceDao.save(service);

        assertThat(savedService).isNotNull();

        int countAfter = serviceDao.getAllServices().size();

        assertThat(countAfter).isGreaterThan(countBefore);

        Service fetchedService = serviceDao.getServiceById(savedService.getId());

        assertThat(savedService).isEqualTo(fetchedService);
    }

    @Test
    void testDeleteService() {
        int countBefore = serviceDao.getAllServices().size();

        Service service = serviceFactory.create();
        Service savedService = serviceDao.save(service);

        serviceDao.delete(savedService);

        int countAfter = serviceDao.getAllServices().size();

        assertThat(countBefore).isEqualTo(countAfter);

        Service fetchedService = serviceDao.getServiceById(savedService.getId());

        assertThat(fetchedService).isNull();
    }

    @Test
    void testUpdateService() {
        Service service = serviceFactory.create();
        Service savedService = serviceDao.save(service);

        service.setId(savedService.getId());

        String newName = faker.app().name();

        service.setName(newName);

        Service updatedService = serviceDao.update(service);

        assertThat(savedService.getName()).isNotEqualTo(updatedService.getName());
    }
}
