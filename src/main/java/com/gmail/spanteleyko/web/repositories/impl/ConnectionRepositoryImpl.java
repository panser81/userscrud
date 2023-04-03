package com.gmail.spanteleyko.web.repositories.impl;

import com.gmail.spanteleyko.web.repositories.ConnectionRepository;
import com.gmail.spanteleyko.web.utils.PropertyUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import static com.gmail.spanteleyko.web.constants.Constant.*;

public class ConnectionRepositoryImpl implements ConnectionRepository {

    private static ConnectionRepository connectionRepository;
    private static HikariDataSource dataSource;


    public static ConnectionRepository getInstance() throws ClassNotFoundException {
        if (connectionRepository == null) {
            connectionRepository = new ConnectionRepositoryImpl();

            PropertyUtil propertyUtil = new PropertyUtil();

            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl(propertyUtil.getProperty(DATABASE_URL));
            hikariConfig.setUsername(propertyUtil.getProperty(DATABASE_USERNAME));
            hikariConfig.setPassword(propertyUtil.getProperty(DATABASE_PASSWORD));
            Class.forName(POSTGRESQL_DRIVER);
            dataSource = new HikariDataSource(hikariConfig);
        }

        return connectionRepository;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
