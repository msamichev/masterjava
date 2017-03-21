package ru.javaops.masterjava.web;


import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@WebServlet("/upload-file")
@MultipartConfig
public class UploadFileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();
        writer.println("File was uploaded!");
        writer.println("");

        Part filePart = req.getPart("uploadFile"); // Retrieves <input type="file" name="file">


        try {
            Set<User> users = getUsers(filePart);
            writer.println("File contains following users:");
            writer.println("");
            for (User user : users) {
                System.out.println(String.format("%s/%s/%s", user.getValue(), user.getEmail(), user.getFlag().value()));
                writer.println(String.format("%s/%s/%s", user.getValue(), user.getEmail(), user.getFlag().value()));
            }
        } catch (Exception e) {
            writer.println("Incorrect file  - message:");
            writer.println(e.getMessage());
        }
        writer.flush();
    }


    private Set<User> getUsers(Part part) throws Exception {

        try (InputStream is = part.getInputStream()) {

            Set<User> users = new HashSet<>();
            StaxStreamProcessor processor = new StaxStreamProcessor(is);

            while (processor.doUntil(XMLEvent.START_ELEMENT, "User")) {
                User user = new User();
                user.setFlag(FlagType.fromValue(processor.getAttribute("flag")));
                user.setEmail(processor.getAttribute("email"));
                user.setValue(processor.getText());
                users.add(user);
            }
            return users;
        }
    }
}
