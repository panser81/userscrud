package com.gmail.spanteleyko.web;

import com.gmail.spanteleyko.web.constants.UserConstants;
import com.gmail.spanteleyko.web.models.UserDTO;
import com.gmail.spanteleyko.web.services.UserService;
import com.gmail.spanteleyko.web.services.impl.UserServiceImpl;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

import static com.gmail.spanteleyko.web.helpers.UserValidate.validate;

@WebServlet(name = "AddUserServlet", value = "/users/add")
public class AddUserServlet extends HttpServlet {

    private UserService userService;

    public AddUserServlet() throws ClassNotFoundException, SQLException {
        userService = UserServiceImpl.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO userDTO = getUserDTO(request);

        validate(userDTO);

        userService.add(userDTO);

        response.sendRedirect("../users");
    }

    private UserDTO getUserDTO(HttpServletRequest request) {
        UserDTO userDTO = new UserDTO();

        String username = request.getParameter(UserConstants.USERNAME_COLUMN_NAME);
        userDTO.setUsername(username);

        String password = request.getParameter(UserConstants.PASSWORD_COLUMN_NAME);
        userDTO.setPassword(password);

        String age = request.getParameter(UserConstants.AGE_COLUMN_NAME);
        userDTO.setAge(Integer.parseInt(age));

        boolean isActive = request.getParameter(UserConstants.ISACTIVE_COLUMN_NAME) != null;

        userDTO.setIsActive((short)(isActive ? 1 : 0));

        String address = request.getParameter(UserConstants.ADDRESS_COLUMN_NAME);
        userDTO.setAddress(address);

        String telephone = request.getParameter(UserConstants.TELEPHONE_COLUMN_NAME);
        userDTO.setTelephone(telephone);

        return userDTO;
    }


}
