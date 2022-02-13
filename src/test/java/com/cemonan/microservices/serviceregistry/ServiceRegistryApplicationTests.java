package com.cemonan.microservices.serviceregistry;

import com.cemonan.microservices.serviceregistry.lib.ServiceRegistry;
import com.cemonan.microservices.serviceregistry.pojo.Service;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
		assertThat(countBefore).isEqualTo(0);

		String addedServiceId1 = serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");
		serviceRegistry.add("Service 1", "1.1.1", "localhost", "3001");
		serviceRegistry.add("Service 1", "1.1.2", "localhost", "3002");
		String addedServiceId2 = serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");

		int countAfter = serviceRegistry.getServices().size();
		assertThat(countAfter).isEqualTo(3);

		assertThat(addedServiceId1).isEqualTo(addedServiceId2);
	}

	@Test
	@Order(2)
	public void testDeleteService() {
		int countBefore = serviceRegistry.getServices().size();

		serviceRegistry.add("Service 1", "1.1.3", "localhost", "3003");
		serviceRegistry.add("Service 1", "1.1.4", "localhost", "3004");
		String addedServiceId1 = serviceRegistry.add("Service 1", "1.1.5", "localhost", "3005");
		String addedServiceId2 = serviceRegistry.add("Service 1", "1.1.3", "localhost", "3003");

		String deletedServiceId1 =serviceRegistry.delete("Service 1", "1.1.5", "localhost", "3005");
		String deletedServiceId2 = serviceRegistry.delete("Service 1", "1.1.3", "localhost", "3003");

		int countAfter = serviceRegistry.getServices().size();
		assertThat(countAfter).isEqualTo(countBefore + 1);

		assertThat(addedServiceId1).isEqualTo(deletedServiceId1);
		assertThat(addedServiceId2).isEqualTo(deletedServiceId2);
	}

	@Test
	@Order(3)
	public void testCleanupOnTimeout() {
		serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");
		serviceRegistry.add("Service 1", "1.1.1", "localhost", "3001");
		serviceRegistry.add("Service 1", "1.1.2", "localhost", "3002");

		try {
			Thread.sleep((Integer.parseInt(defaultTimeout) + 1) * 1000);
		} catch(Exception ex) {}

		serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");

		int count2 = serviceRegistry.getServices().size();
		assertThat(count2).isEqualTo(1);
	}

	@Test
	@Order(4)
	void testCandidates() {
		serviceRegistry.add("Service 1", "1.1.0", "localhost", "3000");
		serviceRegistry.add("Service 1", "1.1.1", "localhost", "3001");
		serviceRegistry.add("Service 1", "1.1.2", "localhost", "3002");
		serviceRegistry.add("Service 1", "1.3.1", "localhost", "3003");
		serviceRegistry.add("Service 1", "1.5.2", "localhost", "3005");

		List<Service> candidates = serviceRegistry.getCandidates("Service 1", "1");

		assertThat(candidates.size()).isEqualTo(5);
	}
}
