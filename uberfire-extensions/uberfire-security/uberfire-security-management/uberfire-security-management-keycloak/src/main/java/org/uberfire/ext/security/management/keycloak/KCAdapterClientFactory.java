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
import javax.servlet.http.HttpServletRequest;

import org.uberfire.commons.config.ConfigProperties;
import org.uberfire.ext.security.management.keycloak.client.Keycloak;
import org.uberfire.ext.security.management.keycloak.client.auth.adapter.KCAdapterContextTokenManager;

/**
 * Factory that build Keycloak clients using the current session access token provided by KC client adapter.
 * @since 0.9.0
 */
@Dependent
public class KCAdapterClientFactory extends BaseClientFactory {

    private Keycloak client;

    @Override
    public Keycloak get() {
        assert client != null;
        return client;
    }

    public void init(final ConfigProperties config,
                     HttpServletRequest request) {

        // Check mandatory properties.
        final ConfigProperties.ConfigProperty authServer = config.get("org.uberfire.ext.security.management.keycloak.authServer",
                                                                      DEFAULT_AUTH_SERVER);

        final KCAdapterContextTokenManager tokenManager = new KCAdapterContextTokenManager(request);
        this.client = Keycloak.getInstance(authServer.getValue(),
                                           tokenManager.getRealm(),
                                           tokenManager);
    }
}
