package com.gmail.spanteleyko.web.repositories;

import com.gmail.spanteleyko.web.exceptions.TableDeleteException;
import com.gmail.spanteleyko.web.repositories.models.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserRepository {
    boolean checkAndCreateDbTables(Connection connection);
    List<User> get(Connection connection);
    User get(Connection connection, int id);
    User add(Connection connection, User user) throws SQLException;
    boolean delete(Connection connection, int id) throws TableDeleteException;
    User update(Connection connection, User user) throws SQLException;
    boolean hasUser(Connection connection, int id) throws SQLException;
}
