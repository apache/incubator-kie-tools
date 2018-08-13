/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.security.server;

import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.security.authz.AuthorizationManager;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.Collection;

import static org.jboss.errai.security.shared.api.identity.User.ANONYMOUS;

/**
 * Releases locks on session end and clear authz related caches.
 */
@WebListener
public class CleanupSecurityCacheSessionListener implements HttpSessionListener {

    private final Collection<AuthorizationManager> authorizationManagers = new ArrayList<>();

    public CleanupSecurityCacheSessionListener() {
        //empty needed for weld
    }

    @Inject
    public CleanupSecurityCacheSessionListener(final Instance<AuthorizationManager> authorizationManagers) {
        for (AuthorizationManager authorizationManager : authorizationManagers) {
            this.authorizationManagers.add(authorizationManager);
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        if (authorizationManagers.isEmpty()) {
            return;
        }
        final User currentUser = (User) se.getSession().getAttribute(ServletSecurityAuthenticationService.USER_SESSION_ATTR_NAME);
        if (!ANONYMOUS.equals(currentUser)) {
            for (AuthorizationManager authorizationManager : authorizationManagers) {
                authorizationManager.invalidate(currentUser);
            }
        }
    }
}