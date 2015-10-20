package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.server.util.FileServletUtil;

import static java.lang.String.*;

public class FileDownloadServlet
        extends BaseFilteredServlet {

    private static final Logger logger = LoggerFactory.getLogger( FileDownloadServlet.class );

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doGet( HttpServletRequest request,
                          HttpServletResponse response )
            throws ServletException, IOException {

        try {

            //See https://bugzilla.redhat.com/show_bug.cgi?id=1202926
            final String encodedPath = FileServletUtil.encodeFileNamePart( request.getParameter( "path" ) );
            final URI uri = new URI( encodedPath );

            if ( !validateAccess( uri, response ) ) {
                return;
            }

            final Path path = ioService.get( uri );

            byte[] bytes = ioService.readAllBytes( path );

            response.setHeader( "Content-Disposition",
                                format( "attachment; filename=%s;", path.getFileName().toString() ) );

            response.setContentType( "application/octet-stream" );

            response.getOutputStream().write(
                    bytes,
                    0,
                    bytes.length );

        } catch ( final Exception e ) {
            logger.error( "Failed to download a file.", e );
        }

    }
}
