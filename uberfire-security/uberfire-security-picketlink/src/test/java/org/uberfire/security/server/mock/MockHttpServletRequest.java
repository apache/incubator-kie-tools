package org.uberfire.security.server.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


public class MockHttpServletRequest implements HttpServletRequest {

    private final Map<String, String[]> parameters = new HashMap<String, String[]>();
    private final MockHttpSession session;
    private final String method;
    private String servletPath;
    private String requestUri;

    public MockHttpServletRequest(MockHttpSession session, String method, String servletPath, String requestUri) {
        this.session = session;
        this.method = method;
        this.servletPath = servletPath;
        this.requestUri = requestUri;
    }

    @Override
    public Object getAttribute( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void setCharacterEncoding( String env ) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public int getContentLength() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getParameter( String name ) {
        if (parameters.get( name ) != null) {
            return parameters.get( name )[0];
        }
        return null;
    }

    public void setParameter( String name, String value ) {
        parameters.put( name, new String[] { value } );
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration( parameters.keySet() );
    }

    @Override
    public String[] getParameterValues( String name ) {
        return parameters.get( name );
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameters;
    }

    @Override
    public String getProtocol() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getScheme() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getServerName() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public int getServerPort() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRemoteAddr() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRemoteHost() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void setAttribute( String name, Object o ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void removeAttribute( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isSecure() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public RequestDispatcher getRequestDispatcher( String path ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRealPath( String path ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public int getRemotePort() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getLocalName() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getLocalAddr() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public int getLocalPort() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AsyncContext startAsync( ServletRequest servletRequest, ServletResponse servletResponse ) throws IllegalStateException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getAuthType() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Cookie[] getCookies() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public long getDateHeader( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getHeader( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Enumeration<String> getHeaders( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public int getIntHeader( String name ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getPathInfo() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getContextPath() {
        return "/mock-context";
    }

    @Override
    public String getQueryString() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRemoteUser() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isUserInRole( String role ) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getRequestURI() {
        return requestUri;
    }

    public void setRequestURI( String requestUri ) {
        this.requestUri = requestUri;
    }

    @Override
    public StringBuffer getRequestURL() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath( String servletPath ) {
        this.servletPath = servletPath;
    }

    @Override
    public HttpSession getSession( boolean create ) {
        return session;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean authenticate( HttpServletResponse response ) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void login( String username, String password ) throws ServletException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Part getPart( String name ) throws IOException, ServletException {
        throw new UnsupportedOperationException("Not implemented.");
    }

}
