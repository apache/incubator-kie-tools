package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.uberfire.io.IOService;

public class FileUploadServlet
        extends BaseUploadServlet {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            if ( request.getParameter( "path" ) != null ) {
                writeFile( ioService, ioService.get( new URI( request.getParameter( "path" ) ) ), getFileItem( request ) );

                writeResponse( response, "OK" );
            } else if ( request.getParameter( "folder" ) != null ) {
                writeFile( ioService,
                           ioService.get( new URI( request.getParameter( "folder" ) + "/" + request.getParameter( "fileName" ) ) ),
                           getFileItem( request ) );

                writeResponse( response, "OK" );
            }

        } catch ( FileUploadException e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        } catch ( URISyntaxException e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        }
    }
}
