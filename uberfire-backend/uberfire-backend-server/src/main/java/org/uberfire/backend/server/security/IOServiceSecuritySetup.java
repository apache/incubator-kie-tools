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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.jboss.errai.security.shared.api.identity.User;
import org.jboss.errai.security.shared.exception.AuthenticationException;
import org.jboss.errai.security.shared.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.java.nio.file.FileSystemMetadata;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.security.FileSystemUser;
import org.uberfire.java.nio.security.SecuredFileSystemProvider;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.ssh.service.backend.auth.SSHKeyAuthenticator;

@ApplicationScoped
@Startup
public class IOServiceSecuritySetup {

    /**
     * The system property that specifies which authentication domain the default security service should be configured
     * for. Not used if the application provides its own {@code @IOSecurityAuth AuthenticationService}.
     */
    public static final String AUTH_DOMAIN_KEY = "org.uberfire.domain";
    private static final Logger LOG = LoggerFactory.getLogger(IOServiceSecuritySetup.class);
    @Inject
    @IOSecurityAuth
    Instance<AuthenticationService> authenticationManagers;

    @Inject
    AuthorizationManager authorizationManager;

    @Inject
    SSHKeyAuthenticator sshKeyAuthenticator;

    @PostConstruct
    public void setup() {
        final AuthenticationService authenticationManager;

        if (authenticationManagers.isUnsatisfied()) {
            final String authType = System.getProperty("org.uberfire.io.auth",
                                                       null);
            final String domain = System.getProperty(AUTH_DOMAIN_KEY,
                                                     JAASAuthenticationService.DEFAULT_DOMAIN);

            if (authType == null || authType.toLowerCase().equals("jaas") || authType.toLowerCase().equals("container")) {
                authenticationManager = new JAASAuthenticationService(domain);
            } else {
                authenticationManager = loadClazz(authType,
                                                  AuthenticationService.class);
            }
        } else {
            authenticationManager = authenticationManagers.get();
        }

        for (final FileSystemProvider fp : FileSystemProviders.installedProviders()) {
            if (fp instanceof SecuredFileSystemProvider) {
                SecuredFileSystemProvider sfp = (SecuredFileSystemProvider) fp;
                sfp.setAuthenticator((username, password) -> {
                                         try {
                                             final User result = authenticationManager.login(username,
                                                                                             password);
                                             if (result != null) {
                                                 return new UserAdapter(result);
                                             }
                                             return null;
                                         } catch (final AuthenticationException loginFailed) {
                                             LOG.warn("Login failed",
                                                      loginFailed);
                                             return null;
                                         }
                                     }
                );
                sfp.setAuthorizer((fs, fileSystemUser) ->
                                          authorizationManager.authorize(
                                                  new FileSystemResourceAdaptor(new FileSystemMetadata(fs)),
                                                  ((UserAdapter) fileSystemUser).getWrappedUser())

                );
                sfp.setSSHAuthenticator((userName, key) -> {
                    User user = sshKeyAuthenticator.authenticate(userName, key);

                    if (user != null) {
                        return new UserAdapter(user);
                    }

                    return null;
                });
            }
        }
    }

    private <T> T loadClazz(final String clazzName,
                            final Class<T> typeOf) {

        if (clazzName == null || clazzName.isEmpty()) {
            return null;
        }

        try {
            final Class<?> clazz = Class.forName(clazzName);

            if (!typeOf.isAssignableFrom(clazz)) {
                // FIXME this could only be due to a deployment configuration error. why do we continue in this case?
                LOG.error("Class '" + clazzName + "' is not assignable to expected type " + typeOf + ". Continuing as if no class was specified.");
                return null;
            }

            return typeOf.cast(clazz.newInstance());
        } catch (final Exception e) {
            // FIXME this could only be due to a deployment error. why do we continue in this case?
            LOG.error("Failed to load class '" + clazzName + "' as type " + typeOf + ". Continuing as if none was specified.",
                      e);
        }

        return null;
    }

    static class UserAdapter implements FileSystemUser {

        private final User user;

        UserAdapter(User user) {
            this.user = user;
        }

        @Override
        public String getName() {
            return user.getIdentifier();
        }

        public User getWrappedUser() {
            return user;
        }
    }
}
