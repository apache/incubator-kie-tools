/*
 * Copyright 2015 JBoss Inc
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

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Serializes and adds the GWT locale meta tag in the
 * application's host page. This is useful in case the
 * host page is a simple html file.
 */
public class GWTLocaleHeaderFilter implements Filter {

    @Override
    public void init( final FilterConfig filterConfig ) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter( final ServletRequest request,
                          final ServletResponse response,
                          final FilterChain chain ) throws IOException, ServletException {

        final CharResponseWrapper wrappedResponse = getWrapper( (HttpServletResponse) response );
        chain.doFilter( request,
                        wrappedResponse );

        final String output;

        final Locale locale = getLocale( request );
        final String injectedScript = "<meta name=\"gwt:property\" content=\"locale=" + locale.toString() + "\">";

        final Document document = Jsoup.parse( wrappedResponse.toString() );
        document.head().append( injectedScript );
        output = document.html();

        final byte[] outputBytes = output.getBytes( "UTF-8" );
        response.setContentLength( outputBytes.length );
        response.getOutputStream().write( outputBytes );
    }

    protected CharResponseWrapper getWrapper( final HttpServletResponse response ) {
        return new CharResponseWrapper( response );
    }

    private Locale getLocale( final ServletRequest request ) {
        Locale locale = null;
        try {
            locale = new Locale( request.getParameter( "locale" ) );
        } catch ( Exception e ) {
            locale = request.getLocale();
        }
        return locale;
    }

    static class CharResponseWrapper extends HttpServletResponseWrapper {

        protected CharArrayWriter charWriter = new CharArrayWriter();

        protected ServletOutputStream outputStream = new ServletOutputStream() {
            @Override
            public void write( int b ) throws IOException {
                charWriter.write( b );
            }
        };

        protected PrintWriter writer = new PrintWriter( charWriter );

        public CharResponseWrapper( final HttpServletResponse response ) {
            super( response );
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            // Don't remove this override!
            // When intercepting static content, WAS 8.5.5.5 prematurely calls this
            // method to flush the output stream before we can calculate the content
            // length (see above).
        }

        @Override
        public String toString() {
            return charWriter.toString();
        }
    }

}