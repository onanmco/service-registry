package com.cemonan.microservices.serviceregistry;

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
	@Autowired
	ServiceRegistry serviceRegistry;

	@Value("${service.registry.default.timeout}")
	private String defaultTimeout;

	@Test
	@Order(1)
	public void testCreateService() {
		int countBefore = serviceRegistry.getServices().size();

		UUID addedServiceId1 = serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");
		serviceRegistry.add("Service 1", "1.1.1", "localhost", "3001");
		serviceRegistry.add("Service 1", "1.1.2", "localhost", "3002");
		UUID addedServiceId2 = serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");

		int countAfter = serviceRegistry.getServices().size();

		assertThat(countAfter).isGreaterThan(countBefore);
		assertThat(addedServiceId1).isEqualTo(addedServiceId2);
	}

	@Test
	@Order(2)
	public void testDeleteService() {
		int countBefore = serviceRegistry.getServices().size();

		serviceRegistry.add("Service 1", "1.1.3", "localhost", "3003");
		serviceRegistry.add("Service 1", "1.1.4", "localhost", "3004");
		UUID addedServiceId1 = serviceRegistry.add("Service 1", "1.1.5", "localhost", "3005");
		UUID addedServiceId2 = serviceRegistry.add("Service 1", "1.1.3", "localhost", "3003");

		UUID deletedServiceId1 =serviceRegistry.delete("Service 1", "1.1.5", "localhost", "3005");
		UUID deletedServiceId2 = serviceRegistry.delete("Service 1", "1.1.3", "localhost", "3003");

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

		serviceRegistry.add(UUID.randomUUID().toString(), "1.1.0", "localhost", "3000");

		int count2 = serviceRegistry.getServices().size();

		assertThat(count2).isEqualTo(count1 + 1);

		try {
			Thread.sleep((Integer.parseInt(defaultTimeout) + 1) * 1000);
		} catch(Exception ex) {}

		serviceRegistry.add(UUID.randomUUID().toString(), "1.1.0", "localhost", "3000");

		int count3 = serviceRegistry.getServices().size();
		assertThat(count3).isEqualTo(count1 + 1);
	}
}
