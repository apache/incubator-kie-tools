/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.services.verifier.plugin.backend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VerifierWebWorkerServlet
        extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger( VerifierWebWorkerServlet.class );

    public static String loadResource( final String name ) throws
                                                           Exception {
        final InputStream in = VerifierWebWorkerServlet.class.getResourceAsStream( name );
        final Reader reader = new InputStreamReader( in,
                                                     StandardCharsets.UTF_8 );
        final StringBuilder text = new StringBuilder();
        final char[] buf = new char[1024];
        int len = 0;
        while ( ( len = reader.read( buf ) ) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text.toString();
    }

    @Override
    protected void doGet( final HttpServletRequest request,
                          final HttpServletResponse response ) throws
                                                               ServletException,
                                                               IOException {
        LOGGER.debug( "Loading verifier web worker" );

        try {
            final String requestURI = request.getRequestURI();
            final int indexOf = requestURI.indexOf( "/verifier" );

            if ( indexOf >= 0 ) {

                final String fileName = requestURI.substring( indexOf );

                if ( fileName.endsWith( "cache.js" ) ) {

                    final byte[] bytes = loadResource( fileName ).getBytes();

                    response.setContentType( "application/javascript" );

                    response.getOutputStream()
                            .write(
                                    bytes,
                                    0,
                                    bytes.length );
                }
            }
        } catch ( final Exception e ) {
            LOGGER.error( "Failed to load verifier web worker." );
        }

    }
}
