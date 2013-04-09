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

package org.uberfire.security.server.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.uberfire.security.auth.AuthenticationException;
import org.uberfire.security.auth.AuthenticationProvider;
import org.uberfire.security.auth.AuthenticationResult;
import org.uberfire.security.auth.AuthenticationSource;
import org.uberfire.security.auth.AuthenticationStatus;
import org.uberfire.security.auth.Credential;
import org.uberfire.security.auth.Principal;
import org.uberfire.security.impl.auth.UserNameCredential;
import org.uberfire.security.impl.auth.UsernamePasswordCredential;

import static java.util.Collections.*;
import static org.kie.commons.validation.PortablePreconditions.*;

public class DefaultAuthenticationProvider implements AuthenticationProvider {

    private final AuthenticationSource authenticationSource;

    public DefaultAuthenticationProvider(final AuthenticationSource authSource) {
        this.authenticationSource = checkNotNull("authSource", authSource);
    }

    @Override
    public void initialize(final Map<String, ?> options) {
        authenticationSource.initialize(options);
    }

    @Override
    public boolean supportsCredential(final Credential credential) {
        return authenticationSource.supportsCredential(credential);
    }

    @Override
    public AuthenticationResult authenticate(final Credential credential)
            throws AuthenticationException {
        if (!supportsCredential(credential)) {
            return new AuthenticationResult() {
                @Override
                public List<String> getMessages() {
                    return new ArrayList<String>(1) {{
                        add("Credential not supported by " + DefaultAuthenticationProvider.class.getName());
                    }};
                }

                @Override
                public AuthenticationStatus getStatus() {
                    return AuthenticationStatus.NONE;
                }

                @Override
                public Principal getPrincipal() {
                    return null;
                }
            };
        }

        final UserNameCredential realCredential = UserNameCredential.class.cast(credential);

        if (!authenticationSource.authenticate(realCredential)) {
            return new AuthenticationResult() {
                @Override
                public List<String> getMessages() {
                    return new ArrayList<String>(1) {{
                        add("Invalid credentials.");
                    }};
                }

                @Override
                public AuthenticationStatus getStatus() {
                    return AuthenticationStatus.FAILED;
                }

                @Override
                public Principal getPrincipal() {
                    return null;
                }
            };
        }

        return new AuthenticationResult() {
            @Override
            public List<String> getMessages() {
                return emptyList();
            }

            @Override
            public AuthenticationStatus getStatus() {
                return AuthenticationStatus.SUCCESS;
            }

            @Override
            public Principal getPrincipal() {
                return new Principal() {

                    @Override
                    public String getName() {
                        return realCredential.getUserName();
                    }
                };
            }
        };
    }
}
