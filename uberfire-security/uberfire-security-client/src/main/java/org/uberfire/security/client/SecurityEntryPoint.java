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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.ClientMessageBus;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.bus.client.api.messaging.MessageCallback;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.security.client.local.api.SecurityContext;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.UnauthenticatedException;

@ApplicationScoped
public class SecurityEntryPoint {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private ClientMessageBus bus;

    @Produces
    @Dependent
    public User currentUser() {
        return securityContext.getCachedUser();
    }

    public void setup() {
        bus.subscribe( CLIENT_ERROR_SUBJECT, new MessageCallback() {
            @Override
            public void callback( Message message ) {
                final Throwable caught = message.get( Throwable.class, MessageParts.Throwable );
                if ( caught instanceof UnauthenticatedException ) {
                    redirect( "/login.jsp" );
                }
                // Let other ErrorCallbacks handle specific errors
            }
        } );
    }

    public static native void redirect( final String url )/*-{
        $wnd.location = url;
    }-*/;

}
