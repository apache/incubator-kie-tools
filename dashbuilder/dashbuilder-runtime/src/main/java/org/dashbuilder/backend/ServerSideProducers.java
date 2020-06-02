/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.backend;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.annotations.Customizable;
import org.uberfire.annotations.FallbackImplementation;
import org.uberfire.preferences.client.store.PreferenceBeanStoreClientImpl;
import org.uberfire.preferences.shared.PreferenceScopeResolutionStrategy;
import org.uberfire.preferences.shared.UsernameProvider;
import org.uberfire.preferences.shared.bean.PreferenceBeanStore;
import org.uberfire.preferences.shared.impl.DefaultPreferenceScopeResolutionStrategy;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.rpc.impl.SessionInfoImpl;

@ApplicationScoped
public class ServerSideProducers {

    @Inject
    @FallbackImplementation
    DefaultPreferenceScopeResolutionStrategy defaultPreferenceScopeResolutionStrategy;

    @Produces
    @ApplicationScoped
    public User produceUser() {
        return User.ANONYMOUS;
    }

    @Produces
    @ApplicationScoped
    public SessionInfo produceSessionInfo() {
        return new SessionInfoImpl();
    }

    @Produces
    @ApplicationScoped
    public PreferenceBeanStore producePreferenceBeanStore() {
        return new PreferenceBeanStoreClientImpl();
    }

    @Produces
    @Customizable
    @ApplicationScoped
    public PreferenceScopeResolutionStrategy producePreferenceScopeResolutionStrategy() {
        return defaultPreferenceScopeResolutionStrategy;
    }

    @Produces
    @ApplicationScoped
    public UsernameProvider produceUsernameProvider() {
        return () -> User.ANONYMOUS.getIdentifier();
    }

    @Produces
    @ApplicationScoped
    public AuthenticationService produceAuthenticationService() {
        return new AuthenticationService() {

            @Override
            public void logout() {
                // not used
            }

            @Override
            public User login(String username, String password) {
                return null;
            }

            @Override
            public boolean isLoggedIn() {
                return false;
            }

            @Override
            public User getUser() {
                return User.ANONYMOUS;
            }
        };
    }

}
