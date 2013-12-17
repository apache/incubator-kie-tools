package org.uberfire.backend.repositories.impl;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.backend.repositories.PublicURI;

@Portable
public class DefaultPublicURI implements PublicURI {

    private String protocol;
    private String uri;

    public DefaultPublicURI() {
    }

    public DefaultPublicURI( final String uri ) {
        this( "", uri );
    }

    public DefaultPublicURI( final String protocol,
                             final String uri ) {
        this.protocol = protocol;
        this.uri = uri;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol( String protocol ) {
        this.protocol = protocol;
    }

    @Override
    public String getURI() {
        return uri;
    }

    public void setURI( final String uri ) {
        this.uri = uri;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof DefaultPublicURI ) ) {
            return false;
        }

        DefaultPublicURI publicURI = (DefaultPublicURI) o;

        if ( uri != null ? !uri.equals( publicURI.uri ) : publicURI.uri != null ) {
            return false;
        }
        if ( protocol != null ? !protocol.equals( publicURI.protocol ) : publicURI.protocol != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = protocol != null ? protocol.hashCode() : 0;
        result = 31 * result + ( uri != null ? uri.hashCode() : 0 );
        return result;
    }
}
