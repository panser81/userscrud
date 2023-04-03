package com.gmail.spanteleyko.web;

import com.gmail.spanteleyko.web.models.UserDTO;
import com.gmail.spanteleyko.web.services.UserService;
import com.gmail.spanteleyko.web.services.impl.UserServiceImpl;

import javax.servlet.http.*;
import javax.ws.rs.core.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ListServlet", value = "/users")
public class ListUserServlet extends HttpServlet {

    private UserService userService;

    public ListUserServlet() throws ClassNotFoundException, SQLException {
        userService = UserServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);

        String headerHtml = """
                <html>
                    <body>
                        <h2>Users</h2>
                        <p><a href='create.html'>create user</a></p>
                        <table>
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>User name</th>
                                    <th>Age</th>
                                    <th>Is active</th>
                                    <th>Address</th>
                                    <th>Telephone</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                """;

        try (PrintWriter writer = response.getWriter()) {
            writer.println(headerHtml);

            try {
                List<UserDTO> users = userService.getUsers();

                users.forEach(userDTO -> {

                    String rowHtml = """
                            <tr>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>%s</td>
                                <td>
                                    <a href='users/edit?id=%s'>Edit</a>
                                    <a href='delete.html?id=%s'>Delete</a>
                                </td>
                            </tr>
                            """
                            .formatted(
                                    userDTO.getId(),
                                    userDTO.getUsername(),
                                    userDTO.getAge(),
                                    userDTO.getIsActive(),
                                    userDTO.getAddress(),
                                    userDTO.getTelephone(),
                                    userDTO.getId(),
                                    userDTO.getId());

                    writer.println(rowHtml);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

            String footerHtml = """
                                </tbody>
                            </table>
                        </body>
                    </html>
                    """;
            writer.println(footerHtml);
        }
    }
}
