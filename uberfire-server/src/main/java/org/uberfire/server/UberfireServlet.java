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

import static org.mvel2.templates.TemplateCompiler.compileTemplate;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;
import org.uberfire.security.Role;
import org.uberfire.security.Subject;
import org.uberfire.security.server.cdi.SecurityFactory;
import org.uberfire.server.cdi.UberFireGeneralFactory;

public class UberfireServlet extends HttpServlet {

    CompiledTemplate appTemplate = null;
    CompiledTemplate headerTemplate = null;
    CompiledTemplate footerTemplate = null;
    CompiledTemplate userDataTemplate = null;

    @Override
    public void init( final ServletConfig config ) throws ServletException {
        UberFireGeneralFactory.setServletContext( config.getServletContext() );
        try {
            final String appTemplateRef = getConfig( config, "org.uberfire.template.app" );
            if ( appTemplateRef != null ) {
                appTemplate = compileTemplate( getFileContent( appTemplateRef ) );
            } else {
                appTemplate = compileTemplate( getResourceContent( "app.html.template" ) );
            }
        } catch ( Exception ex ) {
            final String headerRef = getConfig( config, "org.uberfire.template.header" );
            if ( headerRef != null ) {
                headerTemplate = compileTemplate( getFileContent( headerRef ) );
            } else {
                headerTemplate = compileTemplate( getResourceContent( "header.html.template" ) );
            }

            final String footerRef = getConfig( config, "org.uberfire.template.footer" );
            if ( footerRef != null ) {
                footerTemplate = compileTemplate( getFileContent( footerRef ) );
            } else {
                footerTemplate = compileTemplate( getResourceContent( "footer.html.template" ) );
            }
            userDataTemplate = compileTemplate( getResourceContent( "user_data_on_html.template" ) );
        }
    }

    private String getFileContent( final String fileName ) {
        try {
            return getTemplateContent( new BufferedInputStream( new FileInputStream( fileName ) ) );
        } catch ( IOException e ) {
            throw new IllegalStateException( "Couldn't read template file: \"" + fileName + "\"", e );
        }
    }

    private String getResourceContent( final String resourceName ) {
        try {
            return getTemplateContent( new BufferedInputStream( getClass().getClassLoader().getResourceAsStream( resourceName ) ) );
        } catch (IOException e) {
            throw new IllegalStateException( "Couldn't read template resource: \"" + resourceName + "\"", e );
        }
    }

    private String getTemplateContent( final BufferedInputStream content ) throws IOException {
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        IOUtils.copy( content, outContent );
        content.close();
        return outContent.toString();
    }

    private String getConfig( final ServletConfig config,
                              final String key ) {
        final String keyValue = config.getInitParameter( key );

        if ( keyValue != null && keyValue.isEmpty() ) {
            return null;
        }

        return keyValue;
    }

    @Override
    protected void doGet( final HttpServletRequest request,
                          final HttpServletResponse response )
            throws ServletException, IOException {

        response.setContentType( "text/html" );
        response.setCharacterEncoding( "UTF-8" );

        final PrintWriter writer = response.getWriter();
        final Locale locale = request.getLocale();

        if ( appTemplate != null ) {
            loadApp( writer,
                     locale );
        } else {
            loadHeader( writer );
            loadUserInfo( writer );
            loadFooter( writer );
        }

    }

    @Override
    protected void doPost( HttpServletRequest req,
                           HttpServletResponse resp ) throws ServletException, IOException {
        doGet( req, resp );
    }

    private void loadApp( PrintWriter writer,
                          Locale locale ) {
        final Subject subject = SecurityFactory.getIdentity();
        final String localeTag = locale.getLanguage() + "_" + locale.getCountry();

        final Map<String, String> map = new HashMap<String, String>() {{
            put( "name", subject.getName() );
            put( "roles", collectionAsString( subject.getRoles() ) );
            put( "properties", mapAsString( subject.getProperties() ) );
            put( "locale", localeTag );
        }};

        final String content = TemplateRuntime.execute( appTemplate, map ).toString();

        writer.append( content );
    }

    private void loadHeader( PrintWriter writer ) {
        final String content = (String) TemplateRuntime.execute( headerTemplate );
        writer.append( content );
    }

    private void loadFooter( PrintWriter writer ) {
        final String content = (String) TemplateRuntime.execute( footerTemplate );
        writer.append( content );
    }

    private void loadUserInfo( PrintWriter writer ) {
        final Subject subject = SecurityFactory.getIdentity();

        final Map<String, String> map = new HashMap<String, String>() {{
            put( "name", subject.getName() );
            put( "roles", collectionAsString( subject.getRoles() ) );
            put( "properties", mapAsString( subject.getProperties() ) );
        }};

        final String content = TemplateRuntime.execute( userDataTemplate, map ).toString();

        writer.append( content );
    }

    private String collectionAsString( final Collection<Role> collection ) {
        final StringBuilder sb = new StringBuilder();

        Iterator<Role> iterator = collection.iterator();
        int i = 0;
        while ( iterator.hasNext() ) {
            final Role next = iterator.next();
            sb.append( '"' ).append( next.getName() ).append( '"' );
            if ( i + 1 < collection.size() ) {
                sb.append( ", " );
            }
            i++;
        }

        return sb.toString();
    }

    private String mapAsString( final Map<String, String> map ) {
        final StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        int i = 0;
        while ( iterator.hasNext() ) {
            final Map.Entry<String, String> next = iterator.next();
            sb.append( '"' ).append( next.getKey() ).append( '"' ).append( ":" ).append( '"' ).append( next.getValue() ).append( '"' );
            if ( i + 1 < map.size() ) {
                sb.append( ", " );
            }
            i++;
        }

        return sb.toString();
    }

}
