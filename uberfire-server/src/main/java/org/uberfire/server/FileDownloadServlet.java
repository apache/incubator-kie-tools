package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class FileDownloadServlet
        extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(FileDownloadServlet.class);

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            Path path = ioService.get(new URI(request.getParameter("path")));

            byte[] bytes = ioService.readAllBytes(path);

            response.setHeader("Content-Disposition",
                    String.format("attachment; filename=%s;", path.getFileName().toString()));

            response.setContentType("application/octet-stream");

            response.getOutputStream().write(
                    bytes,
                    0,
                    bytes.length);

        } catch (URISyntaxException e) {
            logger.error("Failed to download a file.", e);
        }

    }
}
