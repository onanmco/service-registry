package com.cemonan.microservices.serviceregistry.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import java.util.Map;

@Component
public abstract class Dao {

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public Dao() {}

    abstract protected Class<?> getEntityClass();

    private EntityManager getEntityManager() {
        return this.entityManagerFactory.createEntityManager();
    }

    private String getTableName() {
        Table tableNameAnnotation = this.getEntityClass().getAnnotation(Table.class);

        if (tableNameAnnotation != null) {
            return tableNameAnnotation.name();
        }

        EntityManager em = this.getEntityManager();
        Metamodel metaModel = em.getMetamodel();

        return metaModel
                .entity(this.getEntityClass())
                .getName()
                .toUpperCase();
    }

    public Long getLastInsertId() {
        return jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
        );
    }
}
