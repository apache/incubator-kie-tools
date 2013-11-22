package org.uberfire.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
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
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class FileUploadServlet
        extends HttpServlet {

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
            IOUtils.copy( uploadedItem.getInputStream(), tempFOS );
            tempFOS.flush();
            tempFOS.close();

            ioService.createFile(path);
            final OutputStream outputStream = ioService.newOutputStream( path );
            IOUtils.copy(new FileInputStream(tempFile),
                    outputStream);

            uploadedItem.getInputStream().close();

        } catch (FileUploadException e) {
            //TODO
            //throw new RulesRepositoryException( e );
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
