/*
 * Copyright 2012 JBoss Inc
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * Servlet to serve image data.
 */
public class UberfireImageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Collection<String> paths = new ArrayList<String>();

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        try {
            final String path = getConfig(config, "org.uberfire.images.paths");
            if (path != null) {
                final String[] paths = path.split(",");
                for (final String currentPath : paths) {
                    this.paths.add(currentPath);
                }
            }
        } catch (final Exception ex) {
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException,
                                                            IOException {
        final String imageUrl = request.getParameter( "url" );
        if ( imageUrl == null ) {
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        final String imageFormat = getImageSuffix( imageUrl );
        byte[] imageData = readImageData( request, imageUrl );
        if ( imageData == null ) {
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return;
        }

        if ( imageFormat != null ) {
            response.setContentType( "image/" + imageFormat );
        }
        response.getOutputStream().write( imageData,
                                          0,
                                          imageData.length );
    }

    private String getImageSuffix(final String imageUrl) {
        final int dotIndex = imageUrl.lastIndexOf( "." );
        if ( dotIndex == -1 ) {
            return null;
        }
        if ( dotIndex == imageUrl.length() ) {
            return null;
        }
        return imageUrl.substring( imageUrl.lastIndexOf( "." ) + 1 );
    }

    private byte[] readImageData(final HttpServletRequest request, final String imageUrl) {
        final String url;
        if (!imageUrl.startsWith("/")) {
            url = "/" + imageUrl;
        } else {
            url = imageUrl;
        }

        final InputStream is = resolveInputStream(request, url);
        if ( is == null ) {
            return null;
        }
        final BufferedInputStream bis = new BufferedInputStream( is );
        return getImageData( bis );
    }

    private InputStream resolveInputStream(final HttpServletRequest request, final String url) {
        final InputStream value = getServletContext().getResourceAsStream( url );
        if (value != null){
            return value;
        }
        for (final String path : paths) {
            final InputStream resource = getServletContext().getResourceAsStream( path + url );
            if (resource != null){
                return resource;
            }
        }
        return null;
    }

    private byte[] getImageData(final BufferedInputStream content) {
        try {
            final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            IOUtils.copy( content,
                          outContent );
            content.close();

            return outContent.toByteArray();
        } catch ( IOException ex ) {
            throw new IllegalStateException( "Can't copy content.",
                                             ex );
        }
    }

    private String getConfig(final ServletConfig config, final String key) {
        final String keyValue = config.getInitParameter(key);

        if (keyValue != null && keyValue.isEmpty()) {
            return null;
        }

        return keyValue;
    }

}
