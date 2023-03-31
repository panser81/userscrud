package com.gmail.spanteleyko.web;

import com.gmail.spanteleyko.web.constants.UserConstants;
import com.gmail.spanteleyko.web.exceptions.UserNotExistsException;
import com.gmail.spanteleyko.web.models.UserDTO;
import com.gmail.spanteleyko.web.services.UserService;
import com.gmail.spanteleyko.web.services.impl.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import static com.gmail.spanteleyko.web.helpers.UserValidate.validate;

@WebServlet(name = "EditServlet", value = "/users/edit")
public class EditUserServlet extends HttpServlet {

    private static final Logger logger = LogManager.getLogger(EditUserServlet.class);
    private UserService userService;

    public EditUserServlet() throws SQLException, ClassNotFoundException {
        userService = UserServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);
        String id = request.getParameter(UserConstants.ID_COLUMN_NAME);

        int parsedId = 0;
        try {
            parsedId = Integer.parseInt(id);

            if (userService.hasUser(parsedId) == false) {
                throw new UserNotExistsException("Database doesn't have user with the id");
            }

            UserDTO userDTO = userService.getUser(parsedId);

            String html = """
                 <html>
                     <body>
                         <h2>Edit user</h2>
                         <form action="../users/edit" method="post">
                             <input type="hidden" name="id" value="%s"/>
                             <label for="username">User Name</label>
                             <input type="text" id="username" name="username" value="%s"><br/>
                             <label for="password">Password</label>
                             <input type="password" id="password" name="password"> <br/>
                             <label for="age">Age</label>
                             <input type="number" id="age" name="age" value="%s" ><br/>
                             <label for="age">Is active</label>
                             <input type="checkbox" id= "isactive" name="isactive"><br/>
                             <label for="address">Address</label>
                             <label for="address">Address</label>
                             <input type="text" id="address" name="address" value="%s"><br/>
                             <label for="telephone">Telephone</label>
                             <input type="text" id="telephone" name="telephone" value="%s"><br/>
                             <input type="submit" value="Save">
                         </form>
                     </body>
                 </html>
                 """.formatted(userDTO.getId(), userDTO.getUsername(), userDTO.getAge(), userDTO.getAddress(), userDTO.getTelephone());

            try (PrintWriter writer = response.getWriter()) {
                writer.println(html);
            }
            response.sendRedirect("users");

        } catch (NumberFormatException e) {
            logger.error(e.getMessage(), e);
            response.sendRedirect("../error.html");

        } catch (UserNotExistsException e) {
            logger.error(e.getMessage(), e);
            response.sendRedirect("../error.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        UserDTO userDTO = getUserDTO(request);

        validate(userDTO);

        userService.update(userDTO);

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

        String isActive = request.getParameter(UserConstants.ISACTIVE_COLUMN_NAME);

        userDTO.setIsActive((short) (isActive == "on" ? 1 : 0));

        String address = request.getParameter(UserConstants.ADDRESS_COLUMN_NAME);
        userDTO.setAddress(address);

        String telephone = request.getParameter(UserConstants.TELEPHONE_COLUMN_NAME);
        userDTO.setTelephone(telephone);

        try {
            int parsedId = Integer.parseInt(request.getParameter(UserConstants.ID_COLUMN_NAME));

            userDTO.setId(parsedId);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Illegal id number");
        }

        return userDTO;
    }
}
