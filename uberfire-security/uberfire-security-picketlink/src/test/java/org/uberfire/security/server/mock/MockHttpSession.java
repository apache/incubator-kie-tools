package org.uberfire.security.server.mock;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class MockHttpSession implements HttpSession {

    public Map<String, Object> attributes = new HashMap<String, Object>();
    
    @Override
    public long getCreationTime() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public String getId() {
        return "M0CK_535510N_1D";
    }

    @Override
    public long getLastAccessedTime() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void setMaxInactiveInterval( int interval ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public int getMaxInactiveInterval() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public HttpSessionContext getSessionContext() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public Object getAttribute( String name ) {
        return attributes.get( name );
    }

    @Override
    public Object getValue( String name ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration( attributes.keySet() );
    }

    @Override
    public String[] getValueNames() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void setAttribute( String name, Object value ) {
        attributes.put( name, value );
    }

    @Override
    public void putValue( String name, Object value ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void removeAttribute( String name ) {
        attributes.remove( name );
    }

    @Override
    public void removeValue( String name ) {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public void invalidate() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

    @Override
    public boolean isNew() {
        throw new UnsupportedOperationException( "Not implemented." );
    }

}
