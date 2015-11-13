/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.server.util.FileServletUtil;

public class FileUploadServlet
        extends BaseUploadServlet {

    private static final String PARAM_PATH = "path";
    private static final String PARAM_FOLDER = "folder";
    private static final String PARAM_FILENAME = "fileName";

    private static final String RESPONSE_OK = "OK";
    private static final String RESPONSE_FAIL = "FAIL";

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Override
    protected void doPost( HttpServletRequest request,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            if ( request.getParameter( PARAM_PATH ) != null ) {

                //See https://bugzilla.redhat.com/show_bug.cgi?id=1202926
                final String encodedPath = FileServletUtil.encodeFileNamePart( request.getParameter( PARAM_PATH ) );
                final URI uri = new URI( encodedPath );

                final FileItem fileItem = getFileItem( request );

                finalizeResponse(response, fileItem, uri);

            } else if ( request.getParameter( PARAM_FOLDER ) != null ) {

                //See https://bugzilla.redhat.com/show_bug.cgi?id=1091204
                //If the User-provided file name has an extension use that; otherwise use the same extension as the original (OS FileSystem) extension
                String targetFileName;
                final FileItem fileItem = getFileItem( request );
                final String originalFileName = fileItem.getName();
                final String providedFileName = request.getParameter( PARAM_FILENAME );
                if ( providedFileName.contains( "." ) ) {
                    targetFileName = providedFileName;
                } else {
                    targetFileName = providedFileName + getExtension( originalFileName );
                }

                //See https://bugzilla.redhat.com/show_bug.cgi?id=1202926
                targetFileName = FileServletUtil.encodeFileName( targetFileName );

                final URI uri = new URI( request.getParameter( PARAM_FOLDER ) + "/" + targetFileName );

                finalizeResponse(response, fileItem, uri);
            }

        } catch ( FileUploadException e ) {
            logError( e );
            writeResponse( response,
                           RESPONSE_FAIL );

        } catch ( URISyntaxException e ) {
            logError( e );
            writeResponse( response,
                           RESPONSE_FAIL );
        }
    }

    private void finalizeResponse(HttpServletResponse response, FileItem fileItem, URI uri) throws IOException {
        if ( !validateAccess( uri,
                              response ) ) {
            return;
        }

        final Path path = ioService.get( uri );

        writeFile( ioService,
                   path,
                   fileItem );

        writeResponse( response,
                       RESPONSE_OK );
    }

    private String getExtension( final String originalFileName ) {
        if ( originalFileName.contains( "." ) ) {
            return "." + originalFileName.substring( originalFileName.lastIndexOf( "." ) + 1 );
        }
        return "";
    }

}
