package org.uberfire.rpc.impl;

import javax.enterprise.inject.Alternative;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.rpc.SessionInfo;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@Alternative
public class SessionInfoImpl implements SessionInfo {

    private String id;
    private User identity;

    public SessionInfoImpl() {
    }

    public SessionInfoImpl( final String id,
                            final User identity ) {
        this.id = checkNotEmpty( "id", id );
        this.identity = checkNotNull( "identity", identity );
    }

    public SessionInfoImpl( final User identity ) {
        this.identity = checkNotNull( "identity", identity );
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId( final String id ) {
        this.id = id;
    }

    @Override
    public User getIdentity() {
        return identity;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof SessionInfo ) ) {
            return false;
        }

        SessionInfo that = (SessionInfo) o;

        if ( !getId().equals( that.getId() ) ) {
            return false;
        }

        return getIdentity().getIdentifier().equals( that.getIdentity().getIdentifier() );

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + identity.getIdentifier().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SessionInfoImpl [id=" + id + ", identity=" + identity + "]";
    }

}
