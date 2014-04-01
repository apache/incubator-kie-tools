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

import static org.jboss.errai.bus.client.api.base.DefaultErrorCallback.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageBus;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.exception.UnauthorizedException;

import com.google.gwt.json.client.JSONObject;

@EntryPoint
public class SecurityEntryPoint {

    private User currentSubject = null;

    @Inject
    private MessageBus bus;

    @Produces
    @ApplicationScoped
    public User currentUser() {
        if ( currentSubject == null ) {
            setup();
        }
        return currentSubject;
    }

    public void setup() {
        final JSONSubject clientSubject = loadCurrentSubject();
        final String name;
        final Set<Role> roles = new HashSet<Role>();
        final Map<String, String> properties = new HashMap<String, String>();

        if ( clientSubject == null ) {
            name = User.ANONYMOUS;
            roles.add( new RoleImpl(User.ANONYMOUS) );

        } else {
            name = clientSubject.getName();
            for ( int i = 0; i < clientSubject.getRoles().length(); i++ ) {
                final String roleName = clientSubject.getRoles().get( i );
                roles.add( new RoleImpl( roleName ) );
            }

            final JSONObject json = new JSONObject( clientSubject.getProperties() );
            for ( final String key : json.keySet() ) {
                properties.put( key, json.get( key ).isString().stringValue() );
            }
        }

        this.currentSubject = new UserImpl(name, roles, properties);

        bus.subscribe( CLIENT_ERROR_SUBJECT, new MessageCallback() {
            @Override
            public void callback( Message message ) {
                try {
                    final Throwable caught = message.get( Throwable.class, MessageParts.Throwable );
                    throw caught;
                } catch ( UnauthorizedException ex ) {
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
