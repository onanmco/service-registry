package com.cemonan.microservices.serviceregistry.dao;

import com.cemonan.microservices.serviceregistry.domain.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.UUID;

@Component
public class ServiceDaoImpl extends Dao implements ServiceDao {

    private final ResultSetExtractor<List<Service>> resultSetExtractor;

    @Autowired
    public ServiceDaoImpl(EntityManagerFactory emf, ResultSetExtractor<List<Service>> resultSetExtractor) {
        super(emf);
        this.resultSetExtractor = resultSetExtractor;
    }

    @Override
    public Service save(Service service) {
        UUID id = UUID.randomUUID();
        jdbcTemplate.update(
                "INSERT INTO services (id, `name`, version, ip, port, `timestamp`, `count`) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)",
                id.toString(),
                service.getName(),
                service.getVersion(),
                service.getIp(),
                service.getPort(),
                service.getTimestamp(),
                service.getUsedCount()
        );

        return this.getServiceById(id);
    }

    @Override
    public Service getServiceById(UUID id) {
        List<Service> services = jdbcTemplate.query(
                "SELECT * FROM services WHERE id = ?",
                resultSetExtractor,
                id.toString()
        );

        if (services.size() == 0) {
            return null;
        }

        return services.get(0);
    }

    @Override
    public Service getServiceByNameAndVersionAndIpAndPort(String name, String version, String ip, String port) {
        List<Service> services = jdbcTemplate.query(
                "SELECT * FROM services WHERE `name` = ? AND version = ? AND ip = ? AND port = ?",
                resultSetExtractor,
                name, version, ip, port
        );

        if (services.size() == 0) {
            return null;
        }

        return services.get(0);
    }

    @Override
    public Service update(Service service) {
        jdbcTemplate.update(
                "UPDATE services SET `name` = ?, version = ?, ip = ?, port = ?, `timestamp` = ?, `count` = ? WHERE id = ?",
                service.getName(),
                service.getVersion(),
                service.getIp(),
                service.getPort(),
                service.getTimestamp(),
                service.getUsedCount(),
                service.getId().toString()
        );

        return this.getServiceById(service.getId());
    }

    @Override
    public void delete(Service service) {
        jdbcTemplate.update(
                "DELETE FROM services WHERE id = ?",
                service.getId().toString()
        );
    }

    @Override
    public List<Service> getAllServices() {
        return jdbcTemplate.query(
                "SELECT * FROM services",
                resultSetExtractor
        );
    }

    @Override
    public List<Service> getAllByNameOrderByUsedCount(String name, Sort sort) {
        String direction = sort.getOrderFor("count").getDirection().name();
        return jdbcTemplate.query(
                "SELECT * FROM services WHERE `name` = ? ORDER BY `count` " + direction,
                resultSetExtractor,
                name
        );
    }

    @Override
    protected Class<?> getEntityClass() {
        return Service.class;
    }
}
