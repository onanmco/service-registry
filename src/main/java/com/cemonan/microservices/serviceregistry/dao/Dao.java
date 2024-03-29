package com.cemonan.microservices.serviceregistry.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;
import javax.persistence.metamodel.Metamodel;

public abstract class Dao {

    private final EntityManagerFactory entityManagerFactory;
    protected final JdbcTemplate jdbcTemplate;

    @Autowired
    public Dao(EntityManagerFactory entityManagerFactory, JdbcTemplate jdbcTemplate) {
        this.entityManagerFactory = entityManagerFactory;
        this.jdbcTemplate = jdbcTemplate;
    }

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
