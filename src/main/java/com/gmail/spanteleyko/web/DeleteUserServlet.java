package com.gmail.spanteleyko.web;

import com.gmail.spanteleyko.web.constants.UserConstants;
import com.gmail.spanteleyko.web.exceptions.UserNotExistsException;
import com.gmail.spanteleyko.web.services.UserService;
import com.gmail.spanteleyko.web.services.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "DeleteServlet", value = "/users/delete")
public class DeleteUserServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(DeleteUserServlet.class);

    private UserService userService;

    public DeleteUserServlet() throws SQLException, ClassNotFoundException {
        userService = UserServiceImpl.getInstance();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter(UserConstants.ID_COLUMN_NAME);

        int parsedId = 0;
        try {
            parsedId = Integer.parseInt(id);

            if (userService.hasUser(parsedId) == false) {
                throw new UserNotExistsException("Database doesn't have user with the id");
            }

            userService.delete(parsedId);

            response.sendRedirect("../users");

        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            response.sendRedirect("../error.html");

        } catch (UserNotExistsException e) {
            logger.error(e.getMessage(), e);
            response.sendRedirect("../error.html");
        }


    }
}
