package com.iknow.iflowtracksysproxy.respository;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Slf4j
@Repository
public class MilesUpdateRepository {

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:oracle:thin:@172.16.11.33:1521:HDFTST");
        ds.setUsername("HDF_SND");
        ds.setPassword("HDF_SND");

        this.jdbcTemplate = new JdbcTemplate(ds);

        log.info("Miles Oracle datasource initialized");
    }

    public int updateSupplierAndContact(Long ordersId, Long supplierId, Long contactId) throws SQLException {
        log.info("DB URL = {}", ((com.zaxxer.hikari.HikariDataSource) jdbcTemplate.getDataSource()).getJdbcUrl());

        String sql = """
                    UPDATE HDF_SND.ORDERS
                    SET SUPPLIER_ID = ?,
                        CONTACT_ID  = ?
                    WHERE ORDERS_ID = ?
                """;

        int updatedRows = jdbcTemplate.update(
                sql,
                supplierId,
                contactId,
                ordersId
        );

        log.info("Miles update affected row count = {}", updatedRows);
        return updatedRows;

    }
}
