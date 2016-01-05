/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.Iterator;
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

public abstract class BaseUploadServlet extends BaseFilteredServlet {

    private static final Logger logger = LoggerFactory.getLogger( BaseUploadServlet.class );

    protected FileItem getFileItem( HttpServletRequest request ) throws FileUploadException {
        final Iterator iterator = getServletFileUpload().parseRequest( request ).iterator();
        while ( iterator.hasNext() ) {
            FileItem item = (FileItem) iterator.next();
            if ( !item.isFormField() ) {
                return item;
            }
        }
        return null;
    }

    protected void writeResponse( HttpServletResponse response,
                                  String ok ) throws IOException {
        response.setContentType( "text/html" );
        response.getWriter().write( ok );
        response.getWriter().flush();
    }

    protected ServletFileUpload getServletFileUpload() {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );
        return upload;
    }

    protected void writeFile( final IOService ioService,
                              final Path path,
                              final FileItem uploadedItem ) throws IOException {
        if ( !ioService.exists( path ) ) {
            ioService.createFile( path );
        }

        ioService.write( path, IOUtils.toByteArray( uploadedItem.getInputStream() ) );

        uploadedItem.getInputStream().close();
    }

    protected void logError( Throwable e ) {
        logger.error( "Failed to upload a file.", e );
    }
}
