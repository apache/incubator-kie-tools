package org.uberfire.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
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

        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        try {

            FileItem uploadedItem = null;
            Path path = null;

            List items = upload.parseRequest(request);
            Iterator it = items.iterator();
            while (it.hasNext()) {
                FileItem item = (FileItem) it.next();
                if (!item.isFormField()) {
                    uploadedItem = item;
                } else if (item.isFormField() && item.getFieldName().equals("path")) {

                    path = ioService.get(new URI(item.getString()));
                }

            }

            // SAVE
            File tempFile = File.createTempFile("uploadedFile", null);
            FileOutputStream tempFOS = new FileOutputStream(tempFile);
            IOUtils.copy(uploadedItem.getInputStream(), tempFOS);
            tempFOS.flush();
            tempFOS.close();

            if (!ioService.exists(path)) {
                ioService.createFile(path);
            }

            ioService.write(path, IOUtils.toByteArray(uploadedItem.getInputStream()));

            uploadedItem.getInputStream().close();

        } catch (FileUploadException e) {
            logError(e);
        } catch (URISyntaxException e) {
            logError(e);
        }
    }

    private void logError(Throwable e) {
        logger.error("Failed to upload a file.", e);
    }
}
