/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.test;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;

@ApplicationScoped
@Priority(1) // needed in order to inject the @Alternatives outside of this bean bundle (aka maven module)
public class TestIdentityFactory {

    private User identity;

    @PostConstruct
    public void onStartup() {
        identity = new UserImpl("testUser");
    }

    @Produces
    @Alternative
    public User getIdentity() {
        return identity;
    }

    @Produces
    @Alternative
    public AuthenticationService authenticationService() {
        return new AuthenticationService() {
            boolean isLoggedIn = false;
            User user;

            @Override
            public User login(String username,
                              String password) {
                isLoggedIn = true;
                user = new UserImpl(username);
                return user;
            }

            @Override
            public boolean isLoggedIn() {
                return false;
            }

            @Override
            public void logout() {
                user = null;
                isLoggedIn = false;
            }

            @Override
            public User getUser() {
                return user;
            }
        };
    }
}