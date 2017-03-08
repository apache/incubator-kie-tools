/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import javax.enterprise.context.Dependent;

import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.keycloak.client.Keycloak;
import org.uberfire.ext.security.management.keycloak.client.auth.credentials.AuthSettings;
import org.uberfire.ext.security.management.keycloak.client.auth.credentials.AuthTokenManager;

/**
 * Factory that creates Keycloak clients based on using Credentials authentication settings connection settings.
 * @since 0.9.0
 */
@Dependent
public class CredentialsClientFactory extends BaseClientFactory {

    private static final String DEFAULT_REALM = "example";
    private static final String DEFAULT_USER = "examples-admin-client";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DEFAULT_CLIENT_ID = "examples-admin-client";
    private static final String DEFAULT_CLIENT_SECRET = "password";

    public void init(final ConfigProperties config) {
        final ConfigProperties.ConfigProperty authServer = config.get("org.uberfire.ext.security.management.keycloak.authServer",
                                                                      DEFAULT_AUTH_SERVER);
        final ConfigProperties.ConfigProperty realm = config.get("org.uberfire.ext.security.management.keycloak.realm",
                                                                 DEFAULT_REALM);
        final ConfigProperties.ConfigProperty user = config.get("org.uberfire.ext.security.management.keycloak.user",
                                                                DEFAULT_USER);
        final ConfigProperties.ConfigProperty password = config.get("org.uberfire.ext.security.management.keycloak.password",
                                                                    DEFAULT_PASSWORD);
        final ConfigProperties.ConfigProperty clientId = config.get("org.uberfire.ext.security.management.keycloak.clientId",
                                                                    DEFAULT_CLIENT_ID);
        final ConfigProperties.ConfigProperty clientSecret = config.get("org.uberfire.ext.security.management.keycloak.clientSecret",
                                                                        DEFAULT_CLIENT_SECRET);

        this.client = Keycloak.getInstance(authServer.getValue(),
                                           realm.getValue(),
                                           new AuthTokenManager(new AuthSettings(authServer.getValue(),
                                                                                 realm.getValue(),
                                                                                 user.getValue(),
                                                                                 password.getValue(),
                                                                                 clientId.getValue(),
                                                                                 clientSecret.getValue())));
    }
}
