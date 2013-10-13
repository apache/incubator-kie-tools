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

package org.uberfire.security.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.json.client.JSONObject;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.security.authz.AuthorizationException;

import static org.jboss.errai.bus.client.api.base.DefaultErrorCallback.*;
import static org.uberfire.commons.validation.PortablePreconditions.*;
import static org.uberfire.security.Identity.*;

@EntryPoint
public class SecurityEntryPoint {

    private Identity currentIdentity = null;

    @Inject
    MessageBus bus;

    @Produces
    @ApplicationScoped
    public Identity currentUser() {
        if ( currentIdentity == null ) {
            setup();
        }
        return currentIdentity;
    }

    public void setup() {
        final JSONSubject clientSubject = loadCurrentSubject();
        final String name;
        final List<Role> roles = new ArrayList<Role>();
        final Map<String, String> properties = new HashMap<String, String>();

        if ( clientSubject == null ) {
            name = ANONYMOUS;
            roles.add( new Role() {
                @Override
                public String getName() {
                    return ANONYMOUS;
                }
            } );

        } else {
            name = clientSubject.getName();
            for ( int i = 0; i < clientSubject.getRoles().length(); i++ ) {
                final String roleName = clientSubject.getRoles().get( i );
                roles.add( new Role() {
                    @Override
                    public String getName() {
                        return roleName;
                    }
                } );
            }

            final JSONObject json = new JSONObject( clientSubject.getProperties() );
            for ( final String key : json.keySet() ) {
                properties.put( key, json.get( key ).isString().stringValue() );
            }
        }

        this.currentIdentity = new Identity() {
            @Override
            public List<Role> getRoles() {
                return roles;
            }

            @Override
            public boolean hasRole( final Role role ) {
                checkNotNull( "role", role );
                for ( final Role activeRole : roles ) {
                    if ( activeRole.getName().equals( role.getName() ) ) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public Map<String, String> getProperties() {
                return properties;
            }

            @Override
            public void aggregateProperty( final String name,
                                           final String value ) {
                throw new RuntimeException( "Identity.aggregateProperty method not allowed" );
            }

            @Override
            public void removeProperty( final String name ) {
                throw new RuntimeException( "Identity.removeProperty method not allowed" );
            }

            @Override
            public String getProperty( final String name,
                                       final String defaultValue ) {
                final String value = properties.get( name );
                if ( value == null ) {
                    return defaultValue;
                }
                return value;
            }

            @Override
            public String getName() {
                return name;
            }
        };

        bus.subscribe( CLIENT_ERROR_SUBJECT, new MessageCallback() {
            @Override
            public void callback( Message message ) {
                try {
                    final Throwable caught = message.get( Throwable.class, MessageParts.Throwable );
                    throw caught;
                } catch ( AuthorizationException ex ) {
                    redirect( "/login.jsp" );
                } catch ( Throwable ex ) {
                    //Let other ErrorCallbacks handle specific errors
                }
            }
        } );
    }

    public static native JSONSubject loadCurrentSubject() /*-{
        return $wnd.current_user;
    }-*/;

    public static native void redirect( final String url )/*-{
        $wnd.location = url;
    }-*/;

}
