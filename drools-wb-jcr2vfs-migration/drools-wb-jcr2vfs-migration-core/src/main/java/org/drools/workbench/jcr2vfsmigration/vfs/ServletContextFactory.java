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

package org.drools.workbench.jcr2vfsmigration.vfs;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * Producer for ServletContext required by org.uberfire.backend.server.plugin.RuntimePluginsServiceServerImpl
 * that is within the uberfire-server-backend dependency we need for JCR-to-VFS data migration.
 */
@Singleton
public class ServletContextFactory {

    private ServletContext servletContext;

    @PostConstruct
    public void onStartup() {
        servletContext = new ServletContext() {
            @Override
            public String getContextPath() {
                return null;
            }

            @Override
            public ServletContext getContext( String uripath ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getMajorVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public int getMinorVersion() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getMimeType( String file ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set getResourcePaths( String path ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public URL getResource( String path ) throws MalformedURLException {
                throw new UnsupportedOperationException();
            }

            @Override
            public InputStream getResourceAsStream( String path ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public RequestDispatcher getRequestDispatcher( String path ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public RequestDispatcher getNamedDispatcher( String name ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Servlet getServlet( String name ) throws ServletException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration getServlets() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration getServletNames() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void log( String msg ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void log( Exception exception,
                             String msg ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void log( String message,
                             Throwable throwable ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getRealPath( String path ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getServerInfo() {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getInitParameter( String name ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration getInitParameterNames() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Object getAttribute( String name ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Enumeration getAttributeNames() {
                throw new UnsupportedOperationException();
            }

            @Override
            public void setAttribute( String name,
                                      Object object ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void removeAttribute( String name ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public String getServletContextName() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Produces
    @Named("uf")
    public ServletContext servletContext() {
        return servletContext;
    }

}
