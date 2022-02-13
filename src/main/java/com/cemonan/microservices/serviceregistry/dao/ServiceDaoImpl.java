package com.cemonan.microservices.serviceregistry.dao;

import com.cemonan.microservices.serviceregistry.pojo.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import java.util.List;

@Component
public class ServiceDaoImpl extends Dao implements ServiceDao {

    @Autowired
    ResultSetExtractor<List<Service>> resultSetExtractor;

    @Override
    public Service save(Service service) {
        jdbcTemplate.update(
                "INSERT INTO services (`name`, version, ip, port, `timestamp`, `count`) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
                service.getName(),
                service.getVersion(),
                service.getIp(),
                service.getPort(),
                service.getTimestamp(),
                service.getUsedCount()
        );

        Long id = this.getLastInsertId();

        return this.getServiceById(id);
    }

    @Override
    public Service getServiceById(Long id) {
        List<Service> services = jdbcTemplate.query(
                "SELECT * FROM services WHERE id = ?",
                resultSetExtractor,
                id
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
                service.getId()
        );

        return this.getServiceById(service.getId());
    }

    @Override
    public void delete(Service service) {
        jdbcTemplate.update(
                "DELETE FROM services WHERE id = ?",
                service.getId()
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
