package org.uberfire.rpc.impl;

import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.Identity;

import static org.uberfire.commons.validation.PortablePreconditions.*;

public class SessionInfoImpl implements SessionInfo {

    private String id;
    private Identity identity;

    public SessionInfoImpl() {
    }

    public SessionInfoImpl( final String id,
                            final Identity identity ) {
        this.id = checkNotEmpty( "id", id );
        this.identity = checkNotNull( "identity", identity );
    }

    public SessionInfoImpl( final Identity identity ) {
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
    public Identity getIdentity() {
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

        return getIdentity().getName().equals( that.getIdentity().getName() );

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + identity.getName().hashCode();
        return result;
    }
}
