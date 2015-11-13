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
