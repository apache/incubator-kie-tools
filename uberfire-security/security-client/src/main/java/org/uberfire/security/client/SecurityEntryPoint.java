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
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.bus.client.api.Message;
import org.jboss.errai.bus.client.api.MessageCallback;
import org.jboss.errai.bus.client.framework.MessageBus;
import org.jboss.errai.common.client.protocols.MessageParts;
import org.jboss.errai.ioc.client.api.EntryPoint;
import org.uberfire.security.Identity;
import org.uberfire.security.Role;
import org.uberfire.security.authz.AuthorizationException;

import static org.jboss.errai.bus.client.api.base.DefaultErrorCallback.*;
import static org.uberfire.commons.util.Preconditions.checkNotNull;

@EntryPoint
public class SecurityEntryPoint {

    private static final String ANONYMOUS = "Anonymous";
    private Identity currentIdentity = null;

    @Inject MessageBus bus;

    @Produces
    @ApplicationScoped
    public Identity currentUser() {
        if (currentIdentity == null) {
            setup();
        }
        return currentIdentity;
    }

    public void setup() {
        final JSONSubject clientSubject = loadCurrentSubject();
        final String name;
        final List<Role> roles = new ArrayList<Role>();

        if (clientSubject == null) {
            name = ANONYMOUS;
            roles.add(new Role() {
                @Override
                public String getName() {
                    return ANONYMOUS;
                }
            });

        } else {
            name = clientSubject.getName();
            for (int i = 0; i < clientSubject.getRoles().length(); i++) {
                final String roleName = clientSubject.getRoles().get(i);
                roles.add(new Role() {
                    @Override
                    public String getName() {
                        return roleName;
                    }
                });
            }
        }

        this.currentIdentity = new Identity() {
            @Override
            public List<Role> getRoles() {
                return roles;
            }

            @Override
            public boolean hasRole(final Role role) {
                checkNotNull("role", role);
                for (final Role activeRole : roles) {
                    if (activeRole.getName().equals(role.getName())){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getName() {
                return name;
            }
        };

        bus.subscribe(CLIENT_ERROR_SUBJECT, new MessageCallback() {
            @Override
            public void callback(Message message) {
                try {
                    final Throwable caught = message.get(Throwable.class, MessageParts.Throwable);
                    throw caught;
                } catch (AuthorizationException ex) {
                    redirect("/login.jsp");
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    public static final native JSONSubject loadCurrentSubject() /*-{
        return $wnd.current_user;
    }-*/;

    public static native void redirect(final String url)/*-{
        $wnd.location = url;
    }-*/;

}
