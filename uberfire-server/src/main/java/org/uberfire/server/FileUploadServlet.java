package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class FileUploadServlet
        extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FileUploadServlet.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            if (request.getParameter("path") != null) {
                writeFile(ioService.get(new URI(request.getParameter("path"))), getFileItem(request));

                writeResponse(response, "OK");
            } else if (request.getParameter("folder") != null) {
                writeFile(
                        ioService.get(new URI(request.getParameter("folder") + "/" + request.getParameter("fileName"))),
                        getFileItem(request));

                writeResponse(response, "OK");
            }

        } catch (FileUploadException e) {
            logError(e);
            writeResponse(response, "FAIL");
        } catch (URISyntaxException e) {
            logError(e);
            writeResponse(response, "FAIL");
        }
    }

    private FileItem getFileItem(HttpServletRequest request) throws FileUploadException {
        Iterator iterator = getServletFileUpload().parseRequest(request).iterator();
        while (iterator.hasNext()) {
            FileItem item = (FileItem) iterator.next();
            if (!item.isFormField()) {
                return item;
            }
        }
        return null;
    }

    private void writeResponse(HttpServletResponse response, String ok) throws IOException {
        response.setContentType("text/html");
        response.getWriter().write(ok);
    }

    private ServletFileUpload getServletFileUpload() {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");
        return upload;
    }

    private void writeFile(Path path, FileItem uploadedItem) throws IOException {
        if (!ioService.exists(path)) {
            ioService.createFile(path);
        }

        ioService.write(path, IOUtils.toByteArray(uploadedItem.getInputStream()));

        uploadedItem.getInputStream().close();
    }

    private void logError(Throwable e) {
        logger.error("Failed to upload a file.", e);
    }
}
