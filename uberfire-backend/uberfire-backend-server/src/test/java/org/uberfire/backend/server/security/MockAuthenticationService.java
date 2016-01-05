/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.security;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.service.AuthenticationService;


public class MockAuthenticationService implements AuthenticationService {

    public static final User FAKE_USER = new UserImpl( "fake" );
    
    @Override
    public User login( String username, String password ) {
        return FAKE_USER;
    }

    @Override
    public boolean isLoggedIn() {
        return true;
    }

    @Override
    public void logout() {
    }

    @Override
    public User getUser() {
        return FAKE_USER;
    }

}
