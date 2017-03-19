package ru.javaops.masterjava.web;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import ru.javaops.masterjava.xml.schema.FlagType;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class UploadFileServlet extends HttpServlet {

    // upload settings
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter writer = resp.getWriter();
        writer.println("File was uploaded!");
        writer.println("");

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        try {
            List<FileItem> formItems = upload.parseRequest(req);
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                        Set<User> users = getUsers(item);
                        writer.println("File contains following users:");
                        writer.println("");
                        for (User user : users) {
                            System.out.println(String.format("%s/%s/%s", user.getValue(), user.getEmail(), user.getFlag().value()));
                            writer.println(String.format("%s/%s/%s", user.getValue(), user.getEmail(), user.getFlag().value()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            writer.println("Incorrect file  - message:");
            writer.println(e.getMessage());
        }
        writer.flush();
    }


    private Set<User> getUsers(FileItem fileItem) throws Exception {

        try (InputStream is = fileItem.getInputStream()) {

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
