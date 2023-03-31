package com.gmail.spanteleyko.web.services.impl;

import com.gmail.spanteleyko.web.exceptions.TableDeleteException;
import com.gmail.spanteleyko.web.exceptions.UserDeleteException;
import com.gmail.spanteleyko.web.repositories.ConnectionRepository;
import com.gmail.spanteleyko.web.repositories.UserRepository;
import com.gmail.spanteleyko.web.repositories.impl.ConnectionRepositoryImpl;
import com.gmail.spanteleyko.web.repositories.impl.UserRepositoryImpl;
import com.gmail.spanteleyko.web.models.UserDTO;
import com.gmail.spanteleyko.web.repositories.models.User;
import com.gmail.spanteleyko.web.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);
    private static UserService userService = null;
    private UserRepository userRepository = UserRepositoryImpl.getInstance();
    private ConnectionRepository connectionRepository = ConnectionRepositoryImpl.getInstance();

    private UserServiceImpl() throws ClassNotFoundException, SQLException {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);

            try {
            //    userRepository.checkAndCreateDbTables(connection);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage(), e);
            }

        }
    }

    public static synchronized UserService getInstance() throws ClassNotFoundException, SQLException {
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }

    @Override
    public List<UserDTO> getUsers() {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);
            List<User> users = userRepository.get(connection);
            return convert(users);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Override
    public UserDTO getUser(int id) {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);
            return convert(userRepository.get(connection, id));
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean hasUser(int id) {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);
            return userRepository.hasUser(connection, id);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return false;
    }


    @Override
    public UserDTO add(UserDTO userDTO) {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);

            User updatedUser = null;
            User user = convert(userDTO);
            try {
                updatedUser = userRepository.add(connection, user);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage(), e);
            }

            return convert(updatedUser);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    private List<UserDTO> convert(List<User> users) {
        List<UserDTO> usersDTO = new ArrayList<>();

        users.forEach(user -> {
            usersDTO.add(convert(user));
        });

        return usersDTO;
    }

    private User convert(UserDTO userDTO) {
        User user = new User();

        user.setId(userDTO.getId());
        user.setAge(userDTO.getAge());
        user.setAddress(userDTO.getAddress());
        user.setTelephone(userDTO.getTelephone());
        user.setPassword(userDTO.getPassword());
        user.setUsername(userDTO.getUsername());
        user.setIsActive(userDTO.getIsActive());

        return user;
    }

    private UserDTO convert(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setAge(user.getAge());
        userDTO.setAddress(user.getAddress());
        userDTO.setTelephone(user.getTelephone());
        userDTO.setPassword(user.getPassword());
        userDTO.setUsername(user.getUsername());
        userDTO.setIsActive(user.getIsActive());

        return userDTO;
    }

    @Override
    public UserDTO update(UserDTO userDTO) {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);
            User user = convert(userDTO);
            User updatedUser = null;

            try {
                updatedUser = userRepository.update(connection, user);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage(), e);
            }
            return convert(updatedUser);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean delete(int id) throws UserDeleteException {
        try (Connection connection = connectionRepository.getConnection()) {
            connection.setAutoCommit(false);

            Boolean idDeleted = false;

            try {
                idDeleted = userRepository.delete(connection, id);
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                logger.error(e.getMessage(), e);
            } catch (TableDeleteException e) {
                logger.error(e.getMessage(), e);
                return false;
            }
            return idDeleted;

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e.getMessage(), e);
            throw new UserDeleteException(e.getMessage());
        }
    }
}
