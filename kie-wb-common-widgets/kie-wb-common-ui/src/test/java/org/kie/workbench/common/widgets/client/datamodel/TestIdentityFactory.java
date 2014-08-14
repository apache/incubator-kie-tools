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

package org.kie.workbench.common.widgets.client.datamodel;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;

@Singleton
@Alternative
public class TestIdentityFactory {

    private User identity;

    @PostConstruct
    public void onStartup() {
        identity = new UserImpl( "testUser" );
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
            public User login( String username,
                               String password ) {
                isLoggedIn = true;
                user = new UserImpl( username );
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