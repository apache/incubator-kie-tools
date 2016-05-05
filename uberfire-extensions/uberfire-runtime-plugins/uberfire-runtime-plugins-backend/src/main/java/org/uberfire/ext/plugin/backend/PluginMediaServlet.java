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

package org.uberfire.ext.plugin.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.ext.plugin.event.MediaAdded;
import org.uberfire.ext.plugin.model.Media;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.EncodingUtil;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.server.BaseUploadServlet;
import org.uberfire.server.MimeType;

public class PluginMediaServlet
        extends BaseUploadServlet {

    @Inject
    @Named("ioStrategy")
    private IOService ioService;

    @Inject
    private Event<MediaAdded> newMediaEvent;

    private String pattern = "/plugins/";

    private static MediaServletURI mediaServletURI = new MediaServletURI( "plugins/" );

    private FileSystem fileSystem;
    private Path root;

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        final String pattern = config.getInitParameter( "url-pattern" );
        if ( pattern != null && !pattern.trim().isEmpty() ) {
            if ( pattern.endsWith( "/" ) ) {
                this.pattern = pattern;
            } else {
                this.pattern = pattern + "/";
            }
            if ( this.pattern.startsWith( "/" ) ) {
                mediaServletURI.setURI( this.pattern.substring( 1 ) );
            } else {
                mediaServletURI.setURI( this.pattern );
            }
        }
        try {
            fileSystem = ioService.newFileSystem( URI.create( "default://plugins" ),
                                                  new HashMap<String, Object>() {{
                                                      put( "init", Boolean.TRUE );
                                                      put( "internal", Boolean.TRUE );
                                                  }} );
        } catch ( final FileSystemAlreadyExistsException e ) {
            fileSystem = ioService.getFileSystem( URI.create( "default://plugins" ) );
        }
        this.root = fileSystem.getRootDirectories().iterator().next();
    }

    @Produces
    @Named("MediaServletURI")
    public MediaServletURI produceMediaServletURI() {
        return mediaServletURI;
    }

    @Override
    public void doGet( final HttpServletRequest req,
                       final HttpServletResponse resp ) throws IOException {
        String mime = null;
        InputStream in;

        boolean isPreview = req.getParameterMap().containsKey( "preview" );

        final String _filename = EncodingUtil.decode( req.getRequestURI().substring( req.getContextPath().length() ) );
        final String filename;
        if ( _filename.toLowerCase().endsWith( "?preview" ) ) {
            filename = _filename.substring( 0, _filename.toLowerCase().indexOf( "?preview" ) );
            isPreview = true;
        } else {
            filename = _filename;
        }

        final Path mediaPath = root.resolve( filename.replace( pattern, "/" ) );
        if ( !ioService.exists( mediaPath ) ) {
            mime = "image/png";
            in = getClass().getResourceAsStream( "/nofound.png" );
        } else {
            mime = MimeType.fromExtension( "." + FilenameUtils.getExtension( mediaPath.getFileName().toString() ) ).getType();
            if ( isPreview ) {
                if ( mime != null && !mime.startsWith( "image/" ) ) {
                    mime = "image/png";
                    in = getClass().getResourceAsStream( "/placeholder.png" );
                } else {
                    in = ioService.newInputStream( mediaPath );
                }
            } else {
                in = ioService.newInputStream( mediaPath );
            }
        }

        if ( mime == null ) {
            resp.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );
            return;
        }

        resp.setContentType( mime );

        final OutputStream out = resp.getOutputStream();

        byte[] buf = new byte[ 1024 ];
        int count = 0;
        while ( ( count = in.read( buf ) ) >= 0 ) {
            out.write( buf, 0, count );
        }
        out.close();
        in.close();
    }

    @Override
    protected void doPost( HttpServletRequest req,
                           HttpServletResponse response ) throws ServletException, IOException {

        try {
            final String filename = req.getRequestURI().substring( req.getContextPath().length() );
            final String pluginName = filename.replace( pattern, "/" );
            if ( pluginName != null ) {
                final FileItem fileItem = getFileItem( req );
                final Path path = root.resolve( pluginName + "/media/" + fileItem.getName() );

                if ( ioService.exists( path ) ) {
                    writeResponse( response, "FAIL - ALREADY EXISTS" );
                    return;
                }

                try {
                    ioService.startBatch();
                    writeFile( ioService, path, fileItem );
                } finally {
                    ioService.endBatch();
                }

                newMediaEvent.fire( new MediaAdded( pluginName.substring( 1 ), new Media( pattern.substring( 1 ) + pluginName.substring( 1 ) + "/media/" + path.getFileName(), Paths.convert( path ) ) ) );

                writeResponse( response, "OK" );
            }
        } catch ( final Exception e ) {
            logError( e );
            writeResponse( response, "FAIL" );
        }
    }
}
