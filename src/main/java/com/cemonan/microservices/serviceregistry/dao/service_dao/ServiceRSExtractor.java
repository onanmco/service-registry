package com.cemonan.microservices.serviceregistry.dao.service_dao;

import com.cemonan.microservices.serviceregistry.domain.Service;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ServiceRSExtractor implements ResultSetExtractor<List<Service>> {
    @Override
    public List<Service> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Service> services = new ArrayList<>();

        while (rs.next()) {
            Service service = new Service();

            service.setId(UUID.fromString(rs.getString("id")));
            service.setName(rs.getString("name"));
            service.setVersion(rs.getString("version"));
            service.setIp(rs.getString("ip"));
            service.setPort(rs.getString("port"));
            service.setTimestamp(rs.getLong("timestamp"));
            service.setUsedCount(rs.getLong("count"));

            services.add(service);
        }

        return services;
    }
}
