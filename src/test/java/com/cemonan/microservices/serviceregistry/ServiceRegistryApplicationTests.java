package com.cemonan.microservices.serviceregistry;

import com.cemonan.microservices.serviceregistry.domain.Service;
import com.cemonan.microservices.serviceregistry.factory.service_factory.ServiceFactory;
import com.cemonan.microservices.serviceregistry.lib.ServiceRegistry;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class ServiceRegistryApplicationTests {

	private final ServiceRegistry serviceRegistry;
	private final ServiceFactory serviceFactory;

	@Value("${service.registry.default.timeout}")
	private String defaultTimeout;

	@Autowired
	ServiceRegistryApplicationTests(ServiceRegistry serviceRegistry, ServiceFactory serviceFactory) {
		this.serviceRegistry = serviceRegistry;
		this.serviceFactory = serviceFactory;
	}

	@Test
	@Order(1)
	public void testCreateService() {
		int countBefore = serviceRegistry.getServices().size();

		Service service1 = serviceFactory.create();
		Service service2 = serviceFactory.create();
		Service service3 = serviceFactory.create();

		UUID addedServiceId1 = serviceRegistry.add(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());
		serviceRegistry.add(service2.getName(), service2.getVersion(), service2.getIp(), service2.getPort());
		serviceRegistry.add(service3.getName(), service3.getVersion(), service3.getIp(), service3.getPort());
		UUID addedServiceId2 = serviceRegistry.add(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());

		int countAfter = serviceRegistry.getServices().size();

		assertThat(countAfter).isGreaterThan(countBefore);
		assertThat(addedServiceId1).isEqualTo(addedServiceId2);
	}

	@Test
	@Order(2)
	public void testDeleteService() {
		int countBefore = serviceRegistry.getServices().size();

		Service service1 = serviceFactory.create();
		Service service2 = serviceFactory.create();
		Service service3 = serviceFactory.create();

		serviceRegistry.add(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());
		serviceRegistry.add(service2.getName(), service2.getVersion(), service2.getIp(), service2.getPort());
		UUID addedServiceId1 = serviceRegistry.add(service3.getName(), service3.getVersion(), service3.getIp(), service3.getPort());
		UUID addedServiceId2 = serviceRegistry.add(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());

		UUID deletedServiceId1 = serviceRegistry.delete(service3.getName(), service3.getVersion(), service3.getIp(), service3.getPort());
		UUID deletedServiceId2 = serviceRegistry.delete(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());

		int countAfter = serviceRegistry.getServices().size();
		assertThat(countAfter).isEqualTo(countBefore + 1);

		assertThat(addedServiceId1).isEqualTo(deletedServiceId1);
		assertThat(addedServiceId2).isEqualTo(deletedServiceId2);
	}

	@Test
	@Order(3)
	public void testCleanupOnTimeout() {
		try {
			Thread.sleep((Integer.parseInt(defaultTimeout) + 1) * 1000);
		} catch(Exception ex) {}

		int count1 = serviceRegistry.getServices().size();

		Service service1 = serviceFactory.create();

		serviceRegistry.add(service1.getName(), service1.getVersion(), service1.getIp(), service1.getPort());

		int count2 = serviceRegistry.getServices().size();

		assertThat(count2).isEqualTo(count1 + 1);

		try {
			Thread.sleep((Integer.parseInt(defaultTimeout) + 1) * 1000);
		} catch(Exception ex) {}

		Service service2 = serviceFactory.create();

		serviceRegistry.add(service2.getName(), service2.getVersion(), service2.getIp(), service2.getPort());


		int count3 = serviceRegistry.getServices().size();
		assertThat(count3).isEqualTo(count1 + 1);
	}
}
