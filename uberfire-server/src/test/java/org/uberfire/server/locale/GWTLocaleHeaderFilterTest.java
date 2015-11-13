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

package org.uberfire.server.locale;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GWTLocaleHeaderFilterTest {

    @Test
    public void testLocaleDefault() throws IOException, ServletException {
        final GWTLocaleHeaderFilter localeHeaderFilter = getFilter();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse resp = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getLocale() ).thenReturn( Locale.US );

        when( resp.getOutputStream() ).thenReturn( new ServletOutputStream() {
            @Override
            public void write( final int b ) throws IOException {
                baos.write( b );
            }
        } );

        localeHeaderFilter.doFilter( req, resp, chain );

        assertEquals( new Scanner( getClass().getResourceAsStream( "/expected-sample.html" ), "UTF-8" ).useDelimiter( "\\A" ).next(), baos.toString() );
    }

    @Test
    public void testLocaleParameter() throws IOException, ServletException {
        final GWTLocaleHeaderFilter localeHeaderFilter = getFilter();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse resp = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getParameter( "locale" ) ).thenReturn( "jp" );

        when( resp.getOutputStream() ).thenReturn( new ServletOutputStream() {
            @Override
            public void write( final int b ) throws IOException {
                baos.write( b );
            }
        } );

        localeHeaderFilter.doFilter( req, resp, chain );

        assertEquals( new Scanner( getClass().getResourceAsStream( "/expected-2-sample.html" ), "UTF-8" ).useDelimiter( "\\A" ).next(), baos.toString() );
    }

    @Test
    public void testNonExistentLocaleParameter() throws IOException, ServletException {
        final GWTLocaleHeaderFilter localeHeaderFilter = getFilter();

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse resp = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getParameter( "locale" ) ).thenReturn( "xxx" );
        when( req.getLocale() ).thenReturn( Locale.US );

        when( resp.getOutputStream() ).thenReturn( new ServletOutputStream() {
            @Override
            public void write( final int b ) throws IOException {
                baos.write( b );
            }
        } );

        localeHeaderFilter.doFilter( req, resp, chain );

        assertEquals( new Scanner( getClass().getResourceAsStream( "/expected-3-sample.html" ), "UTF-8" ).useDelimiter( "\\A" ).next(), baos.toString() );
    }

    private GWTLocaleHeaderFilter getFilter() {
        return new GWTLocaleHeaderFilter() {
            protected CharResponseWrapper getWrapper( final HttpServletResponse response ) {
                final CharResponseWrapper wrapper = new CharResponseWrapper( response );
                final String text = new Scanner( getClass().getResourceAsStream( "/sample.html" ), "UTF-8" ).useDelimiter( "\\A" ).next();
                try {
                    wrapper.getOutputStream().write( text.getBytes() );
                } catch ( final IOException ignored ) {
                }
                return wrapper;
            }
        };
    }
}
