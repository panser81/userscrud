package com.gmail.spanteleyko.web.services;

import com.gmail.spanteleyko.web.exceptions.UserDeleteException;
import com.gmail.spanteleyko.web.models.UserDTO;

import java.sql.SQLException;
import java.util.List;

public interface UserService {
    public List<UserDTO> getUsers() throws SQLException;
    public UserDTO getUser(int id);
    public boolean hasUser(int id);
    public UserDTO add(UserDTO userDTO);
    public UserDTO update(UserDTO userDTO);
    public boolean delete(int id) throws UserDeleteException;
}
