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
package org.uberfire.ext.security.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.uberfire.backend.server.security.RoleRegistry;
import org.uberfire.backend.server.security.adapter.GroupAdapterAuthorizationSource;

@Service
@ApplicationScoped
public class ServletSecurityAuthenticationService extends GroupAdapterAuthorizationSource implements AuthenticationService {

    static final String USER_SESSION_ATTR_NAME = "uf.security.user";
    static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";

    private String[] rolePrincipleNames = new String[]{DEFAULT_ROLE_PRINCIPLE_NAME};

    public ServletSecurityAuthenticationService() {
        final String value = System.getProperty("org.uberfire.security.principal.names",
                                                "");
        if (value != null && !value.trim().isEmpty()) {
            rolePrincipleNames = value.split(",");
        }
    }

    protected static HttpServletRequest getRequestForThread() {
        HttpServletRequest request = SecurityIntegrationFilter.getRequest();
        if (request == null) {
            throw new IllegalStateException("This service only works from threads that are handling HTTP servlet requests");
        }
        return request;
    }

    @Override
    public User login(String username,
                      String password) {
        final HttpServletRequest request = getRequestForThread();

        try {
            request.login(username,
                          password);
            return getUser();
        } catch (final ServletException e) {
            throw new FailedAuthenticationException("Failed to authenticate user " + username,
                                                    e);
        }
    }

    @Override
    public boolean isLoggedIn() {
        HttpServletRequest request = getRequestForThread();
        return request.getUserPrincipal() != null;
    }

    @Override
    public void logout() {
        HttpServletRequest request = getRequestForThread();
        try {
            request.logout();
        } catch (Exception e) {

        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            // The try/catch is an ugly hack for EAP 7.0.x with Keycloak (or RH-SSO) adapter.
            // Undertow (1.4.6-) will return what it appears to be a valid session (session != null), but in fact it
            // was already invalidated by the Keycloak adapter during the request.logout() call.
            // See https://issues.jboss.org/browse/RHBPMS-4574
            try {
                session.invalidate();
            } catch (IllegalStateException ise) {
                // Make sure we catch only the intended exception thrown by Undertow. Re-throw any other exception
                String exceptionMessage = ise.getMessage();
                if (exceptionMessage == null || !exceptionMessage.contains("UT000021")) {
                    throw ise;
                }
            }
        }

    }

    @Override
    public User getUser() {
        HttpServletRequest request = getRequestForThread();

        if (request.getUserPrincipal() == null) {
            return null;
        }
        User user = null;
        final HttpSession session = request.getSession();
        if (session != null) {
            user = (User) session.getAttribute(USER_SESSION_ATTR_NAME);
            if (user == null) {
                // Use roles present in the registry.
                final Collection<Role> userRoles = new HashSet<Role>();
                for (final Role checkRole : RoleRegistry.get().getRegisteredRoles()) {
                    if (request.isUserInRole(checkRole.getName())) {
                        userRoles.add(checkRole);
                    }
                }
                // Obtain roles and groups from entities present in the javax security Principal instance.
                final String name = request.getUserPrincipal().getName();
                Subject subject = getSubjectFromPolicyContext();
                List<String> principals = loadEntitiesFromSubjectAndAdapters(name,
                                                                             subject,
                                                                             rolePrincipleNames);
                Collection<Role> roles = getRoles(principals);
                if (null != roles && !roles.isEmpty()) {
                    userRoles.addAll(roles);
                }
                Collection<org.jboss.errai.security.shared.api.Group> userGroups = getGroups(principals, name);
                // Create the user instance.
                user = new UserImpl(name,
                                    userRoles,
                                    userGroups);
                session.setAttribute(USER_SESSION_ATTR_NAME,
                                     user);
            }
        }

        return user;
    }

    Subject getSubjectFromPolicyContext() {
        Subject subject;
        try {
            subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        } catch (final Exception e) {
            subject = null;
        }
        return subject;
    }
}