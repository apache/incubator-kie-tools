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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.api.identity.UserImpl;
import org.jboss.errai.security.shared.exception.FailedAuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.uberfire.backend.server.security.adapter.GroupAdapterAuthorizationSource;

/**
 * Implements stateful, thread-local authentication of a user via the JAAS API (
 * {@code javax.security.auth.login.LoginContext}).
 * <p>
 * <b>Do not use this module for authenticating web requests!</b> Upon login, it associates the current thread with the
 * authenticated user. This association is only undone upon a call to {@link #logout()}. This is appropriate for use
 * with the Git SSH daemon, but would cause serious security issues if used for authenticating HTTP requests.
 */
@ApplicationScoped
@Alternative
public class JAASAuthenticationService extends GroupAdapterAuthorizationSource implements AuthenticationService {

    public static final String DEFAULT_DOMAIN = "ApplicationRealm";

    static final String DEFAULT_ROLE_PRINCIPLE_NAME = "Roles";
    private final String rolePrincipleName = DEFAULT_ROLE_PRINCIPLE_NAME;

    private final ThreadLocal<User> userOnThisThread = new ThreadLocal<>();

    private final String domain;

    public JAASAuthenticationService(String domain) {
        this.domain = PortablePreconditions.checkNotNull("domain",
                                                         domain);
    }

    @Override
    public User login(final String username,
                      final String password) {
        final SecurityManager jsm = System.getSecurityManager();

        if (jsm != null) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final ClassLoader cl = this.getClass().getClassLoader();
            try {
                // RHBPMS-473 - TCCL used in javax.security.auth.login.LoginContext
                // is not the application CL if JSM is enabled.
                // Setting TCCL to application CL as workaround
                Thread.currentThread().setContextClassLoader(cl);

                return executeLogin(username,
                                    password);
            } catch (final LoginException ex) {
                throw new FailedAuthenticationException();
            } finally {
                // RHBPMS-473 - Restore original TCCL
                if (tccl != null) {
                    Thread.currentThread().setContextClassLoader(tccl);
                }
            }
        } else {
            try {
                return executeLogin(username,
                                    password);
            } catch (final LoginException ex) {
                throw new FailedAuthenticationException();
            }
        }
    }

    private User executeLogin(final String username,
                              final String password) throws LoginException {
        final LoginContext loginContext = createLoginContext(username,
                                                             password);
        loginContext.login();
        List<String> principals = loadEntitiesFromSubjectAndAdapters(username,
                                                                     loginContext.getSubject(),
                                                                     new String[]{rolePrincipleName});
        Collection<Role> roles = getRoles(principals);
        Collection<org.jboss.errai.security.shared.api.Group> groups = getGroups(principals, username);
        UserImpl user = new UserImpl(username,
                                     roles,
                                     groups);
        userOnThisThread.set(user);
        return user;
    }

    @Override
    public void logout() {
        userOnThisThread.remove();
    }

    @Override
    public User getUser() {
        User user = userOnThisThread.get();
        if (user == null) {
            return User.ANONYMOUS;
        }
        return user;
    }

    @Override
    public boolean isLoggedIn() {
        return userOnThisThread.get() != null;
    }

    LoginContext createLoginContext(String username,
                                    String password) throws LoginException {
        return new LoginContext(domain,
                                new UsernamePasswordCallbackHandler(username,
                                                                    password));
    }

    class UsernamePasswordCallbackHandler implements CallbackHandler {

        private final String username;
        private final String password;

        public UsernamePasswordCallbackHandler(final String username,
                                               final String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        public void handle(final Callback[] callbacks) throws IOException, UnsupportedCallbackException {
            for (final Callback callback : callbacks) {
                if (callback instanceof NameCallback) {
                    NameCallback nameCB = (NameCallback) callback;
                    nameCB.setName(username);
                } else if (callback instanceof PasswordCallback) {
                    PasswordCallback passwordCB = (PasswordCallback) callback;
                    passwordCB.setPassword(password.toCharArray());
                } else {
                    try {
                        final Method method = callback.getClass().getMethod("setObject",
                                                                            Object.class);
                        method.invoke(callback,
                                      password);
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
}