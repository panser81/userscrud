package com.gmail.spanteleyko.web.repositories;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionRepository {
    Connection getConnection() throws SQLException;
}
