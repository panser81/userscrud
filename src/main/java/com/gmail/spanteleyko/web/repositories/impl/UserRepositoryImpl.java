package com.gmail.spanteleyko.web.repositories.impl;

import com.gmail.spanteleyko.web.constants.UserConstants;
import com.gmail.spanteleyko.web.exceptions.TableDeleteException;
import com.gmail.spanteleyko.web.repositories.UserRepository;
import com.gmail.spanteleyko.web.repositories.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = LogManager.getLogger(UserRepositoryImpl.class);
    private static UserRepository userRepository = null;


    public static synchronized UserRepository getInstance() {
        if (userRepository == null) {
            userRepository = new UserRepositoryImpl();
        }

        return userRepository;
    }

    @Override
    public List<User> get(Connection connection) {
        List<User> usersList = new ArrayList<>();
        String sql = """
                select u.id, u.username, u.age, u.is_active, ui.address, ui.telephone 
                from "USER" u 
                inner join USER_INFORMATION ui on u.id=ui.user_id;""";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                User user = getUser(resultSet);

                usersList.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return usersList;
    }

    @Override
    public User get(Connection connection, int userId) {
        String sql = """
                select u.id, u.username, u.age, u.is_active, ui.address, ui.telephone 
                from "USER" u inner join USER_INFORMATION ui on u.id=ui.user_id where u.id=?""";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = getUser(resultSet);

                    return user;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public User add(Connection connection, User user) throws SQLException {

        String sql = """
                with user_insert as (
                insert into "USER" (username, is_active, password, age) 
                values(?, ?, ?, ?) 
                RETURNING id) 
                insert into USER_INFORMATION (user_id, address, telephone) 
                values 
                ((select id from user_insert), ?, ?);
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setShort(2, user.getIsActive());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setInt(4, user.getAge());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getTelephone());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected");
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no Id obtained");
                }
            }
        } catch (SQLException e) {
            throw new SQLException(e.getMessage());
        }
        return user;
    }

    public boolean delete(Connection connection, int id) throws TableDeleteException {
        String sql = "delete from USER_INFORMATION where user_id = ?;";
        int rows = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);

            rows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new TableDeleteException(e.getMessage());
        }

        if (rows == 0) {
            return false;
        }

        sql = "delete from \"USER\" where id = ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql,
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, id);

            rows = preparedStatement.executeUpdate();

            return rows > 0;
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new TableDeleteException(e.getMessage());
        }
    }

    @Override
    public User update(Connection connection, User user) throws SQLException {
        String sql = """
                update "USER" set username = ?, is_active = ?, password = ?, age = ? where id = ?;
                """;

        int affectedRows = 0;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setShort(2, user.getIsActive());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setInt(4, user.getAge());
            preparedStatement.setInt(5, user.getId());

            affectedRows = preparedStatement.executeUpdate();


        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new SQLException(e);
        }

        if (affectedRows > 0) {
            sql = "update USER_INFORMATION set address = ?, telephone = ? where user_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, user.getAddress());
                preparedStatement.setString(2, user.getTelephone());
                preparedStatement.setInt(3, user.getId());

                affectedRows = preparedStatement.executeUpdate();
            } catch (SQLException e) {
                logger.error(e.getMessage(), e);
                throw new SQLException(e);
            }
        }

        if (affectedRows == 0) {
            throw new SQLException("Error cannot update user information");
        }
        return user;
    }

    @Override
    public boolean hasUser(Connection connection, int id) {

        String sql = """
                SELECT COUNT(id) 
                FROM "USER" 
                WHERE id = ?;
                """;
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            ResultSet existsUserTable = preparedStatement.executeQuery();

            boolean hasUser = false;

            while (existsUserTable.next()) {
                hasUser = existsUserTable.getInt(1) == 1;
            }

            return hasUser;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public boolean checkAndCreateDbTables(Connection connection) {
        String existsUserTableSql = """
                SELECT COUNT(table_name) 
                FROM information_schema.tables 
                WHERE table_schema LIKE 'public' 
                AND  table_type LIKE 'BASE TABLE' AND table_name = ?;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(existsUserTableSql)) {
            String userTableName = "USER";

            preparedStatement.setString(1, userTableName);
            ResultSet existsUserTable = preparedStatement.executeQuery();

            boolean hasUserTable = false;

            while (existsUserTable.next()) {
                hasUserTable = existsUserTable.getInt(1) == 1;
            }

            if (hasUserTable) {
                deleteDbTables(connection);
            }

            return createDbTables(connection);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    private boolean createDbTables(Connection connection) {
        String sql = """
                CREATE TABLE "USER" 
                            (id SERIAL PRIMARY KEY,
                            username VARCHAR(40),
                            password VARCHAR(40),
                            is_active SMALLINT,
                            age INTEGER);
                            CREATE TABLE USER_INFORMATION 
                            (user_id INTEGER UNIQUE NOT NULL,
                            address VARCHAR(100),
                            telephone VARCHAR(40)); 
                            ALTER TABLE USER_INFORMATION 
                            ADD CONSTRAINT FK_User_UserInformation FOREIGN KEY(user_id) 
                            REFERENCES "USER" (id);
                """;
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            System.out.println("Created table in given database...");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            logger.info(sql);
        }

        return false;
    }

    private boolean deleteDbTables(Connection connection) {
        try (Statement statement = connection.createStatement()) {

            String sql = """
                    drop table user_information;
                    drop table "USER";
                    """;

            statement.executeUpdate(sql);

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    private User getUser(ResultSet resultSet) throws SQLException {
        User user = new User();
        int id = resultSet.getInt(UserConstants.ID_COLUMN_NAME);
        user.setId(id);

        int age = resultSet.getInt(UserConstants.AGE_COLUMN_NAME);
        user.setAge(age);

        String username = resultSet.getString(UserConstants.USERNAME_COLUMN_NAME);
        user.setUsername(username);

        short isActive = resultSet.getShort(UserConstants.ISACTIVE_DB_COLUMN_NAME);
        user.setIsActive(isActive);

        String address = resultSet.getString(UserConstants.ADDRESS_COLUMN_NAME);
        user.setAddress(address);

        String telephone = resultSet.getString(UserConstants.TELEPHONE_COLUMN_NAME);
        user.setTelephone(telephone);

        return user;
    }
}
