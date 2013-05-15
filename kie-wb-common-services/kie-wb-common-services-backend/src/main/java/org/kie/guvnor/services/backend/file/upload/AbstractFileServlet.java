/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.services.backend.file.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.drools.core.util.DateUtils;
import org.kie.workbench.services.shared.file.upload.FileManagerFields;
import org.kie.workbench.services.shared.file.upload.FileOperation;
import org.uberfire.backend.vfs.Path;

/**
 * This is for dealing with assets that have an attachment (ie assets that are really an attachment).
 */
//TODO: Basic authentication
public abstract class AbstractFileServlet extends HttpServlet {

    private static final long serialVersionUID = 510l;

    /**
     * Load resource
     * @param path
     * @return
     */
    protected abstract InputStream doLoad( final Path path );

    /**
     * Create a new resource
     * @param path
     * @param data
     * @param comment
     */
    protected abstract void doCreate( final Path path,
                                      final InputStream data,
                                      final String comment );

    /**
     * Update a resource
     * @param path
     * @param data
     * @param comment
     */
    protected abstract void doUpdate( final Path path,
                                      final InputStream data,
                                      final String comment );

    /**
     * Convert fileName and contextPath into a Path
     * @param fileName
     * @param contextPath
     * @return
     */
    protected abstract Path convertPath( final String fileName,
                                         final String contextPath ) throws URISyntaxException;

    /**
     * Convert fullPath into a Path
     * @param fullPath
     * @return
     */
    protected abstract Path convertPath( final String fullPath ) throws URISyntaxException;

    /**
     * Posting accepts content of various types.
     */
    protected void doPost( final HttpServletRequest request,
                           final HttpServletResponse response ) throws ServletException, IOException {

        response.setContentType( "text/html" );
        final FormData item = getFormData( request );

        if ( item.getFile() != null ) {
            response.getWriter().write( processUpload( item ) );
            return;
        }

        response.getWriter().write( "NO-SCRIPT-DATA" );
    }

    /**
     * Get the form data from the inbound request.
     */
    @SuppressWarnings("rawtypes")
    private FormData getFormData( final HttpServletRequest request ) throws IOException {
        final FileItemFactory factory = new DiskFileItemFactory();
        final ServletFileUpload upload = new ServletFileUpload( factory );
        upload.setHeaderEncoding( "UTF-8" );

        final FormData data = new FormData();
        try {
            final List items = upload.parseRequest( request );
            final Iterator it = items.iterator();

            FileOperation operation = null;
            String fileName = null;
            String contextPath = null;
            String fullPath = null;

            while ( it.hasNext() ) {
                final FileItem item = (FileItem) it.next();
                if ( !item.isFormField() ) {
                    data.setFile( item );
                } else if ( item.getFieldName().equals( FileManagerFields.FORM_FIELD_PATH ) ) {
                    System.out.println( "path:" + item.getString() );
                    contextPath = item.getString();
                } else if ( item.getFieldName().equals( FileManagerFields.FORM_FIELD_NAME ) ) {
                    System.out.println( "name:" + item.getString() );
                    fileName = item.getString();
                } else if ( item.getFieldName().equals( FileManagerFields.FORM_FIELD_FULL_PATH ) ) {
                    System.out.println( "full path:" + item.getString() );
                    fullPath = item.getString();
                } else if ( item.getFieldName().equals( FileManagerFields.FORM_FIELD_OPERATION ) ) {
                    System.out.println( "operation:" + item.getString() );
                    operation = FileOperation.valueOf( item.getString() );
                }
            }

            if ( operation == null ) {
                throw new IllegalArgumentException( "FORM_FIELD_OPERATION is null. Cannot process upload." );
            }

            org.kie.commons.java.nio.file.Path path;
            switch ( operation ) {
                case CREATE:
                    if ( fileName == null ) {
                        throw new IllegalArgumentException( "FORM_FIELD_NAME is null. Cannot process upload." );
                    }
                    if ( contextPath == null ) {
                        throw new IllegalArgumentException( "FORM_FIELD_PATH is null. Cannot process upload." );
                    }
                    data.setOperation( operation );
                    data.setTargetPath( convertPath( fileName,
                                                     contextPath ) );
                    break;
                case UPDATE:
                    if ( fullPath == null ) {
                        throw new IllegalArgumentException( "FORM_FIELD_FULL_PATH is null. Cannot process upload." );
                    }
                    data.setOperation( operation );
                    data.setTargetPath( convertPath( fullPath ) );
            }

            return data;

        } catch ( Exception e ) {
            throw new org.kie.commons.java.nio.IOException( e.getMessage() );
        }
    }

    private String processUpload( final FormData item ) throws IOException {

        // If the file it doesn't exist.
        if ( "".equals( item.getFile().getName() ) ) {
            throw new IOException( "No file selected." );
        }

        final String processResult = uploadFile( item );

        return processResult;
    }

    private String uploadFile( final FormData item ) throws IOException {
        final InputStream fileData = item.getFile().getInputStream();
        final org.uberfire.backend.vfs.Path targetPath = item.getTargetPath();

        try {
            switch ( item.getOperation() ) {
                case CREATE:
                    doCreate( targetPath,
                              fileData,
                              "Uploaded " + getTimestamp() );
                    break;
                case UPDATE:
                    doUpdate( targetPath,
                              fileData,
                              "Uploaded " + getTimestamp() );
            }
        } finally {
            item.getFile().getInputStream().close();
        }

        return "OK";
    }

    private String getTimestamp() {
        final Calendar now = Calendar.getInstance();
        final StringBuilder sb = new StringBuilder();
        sb.append( DateUtils.format( now.getTime() ) );
        sb.append( " " );
        sb.append( now.get( Calendar.HOUR_OF_DAY ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.MINUTE ) );
        sb.append( ":" );
        sb.append( now.get( Calendar.SECOND ) );
        return sb.toString();
    }

    /**
     * doGet acting like a dispatcher.
     */
    protected void doGet( final HttpServletRequest req,
                          final HttpServletResponse res ) throws ServletException, IOException {

        final String path = req.getParameter( FileManagerFields.FORM_FIELD_PATH );

        if ( path != null ) {
            processAttachmentDownload( path,
                                       res );
        } else {
            res.sendError( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    protected void processAttachmentDownload( final String path,
                                              final HttpServletResponse response ) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            final Path sourcePath = convertPath( path );
            IOUtils.copy( doLoad( sourcePath ),
                          output );
            // String fileName = m2RepoService.getJarName(path);
            final String fileName = sourcePath.getFileName();

            response.setContentType( "application/x-download" );
            response.setHeader( "Content-Disposition", "attachment; filename=" + fileName + ";" );
            response.setContentLength( output.size() );
            response.getOutputStream().write( output.toByteArray() );
            response.getOutputStream().flush();

        } catch ( Exception e ) {
            throw new org.kie.commons.java.nio.IOException( e.getMessage() );
        }
    }

}
