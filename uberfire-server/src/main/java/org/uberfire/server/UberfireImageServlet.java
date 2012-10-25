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

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException,
                                                            IOException {
        final String imageUrl = request.getParameter( "url" );
        final String imageFormat = getImageSuffix( imageUrl );
        if ( imageUrl == null ) {
            response.setStatus( HttpServletResponse.SC_NOT_FOUND );
            return;
        }
        byte[] imageData = readImageData( imageUrl );
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

    private byte[] readImageData(final String imageUrl) {
        String url = imageUrl;
        if ( !url.startsWith( "/" ) ) {
            url = "/" + url;
        }
        final InputStream is = getServletContext().getResourceAsStream( url );
        if ( is == null ) {
            return null;
        }
        final BufferedInputStream bis = new BufferedInputStream( is );
        return getImageData( bis );
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

}
