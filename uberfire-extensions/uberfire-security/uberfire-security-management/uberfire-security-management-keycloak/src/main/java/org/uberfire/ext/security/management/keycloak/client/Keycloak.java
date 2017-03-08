/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak.client;

import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.uberfire.ext.security.management.keycloak.client.auth.BearerAuthenticationInterceptor;
import org.uberfire.ext.security.management.keycloak.client.auth.TokenManager;
import org.uberfire.ext.security.management.keycloak.client.resource.RealmResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RealmsResource;

/**
 * The Keycloak client.
 * @since 0.9.0
 */
public class Keycloak {

    private final String serverUrl;
    private final String realm;
    private final ClientRequestFactory clientRequestFactory;

    Keycloak(String serverUrl,
             String realm,
             TokenManager tokenManager) {
        this.serverUrl = serverUrl;
        this.realm = realm;
        this.clientRequestFactory = new ClientRequestFactory(UriBuilder.fromUri(serverUrl).build());
        ResteasyProviderFactory.getInstance().getClientExecutionInterceptorRegistry().register(new BearerAuthenticationInterceptor(tokenManager));
    }

    public static Keycloak getInstance(String serverUrl,
                                       String realm,
                                       TokenManager tokenManager) {
        return new Keycloak(serverUrl,
                            realm,
                            tokenManager);
    }

    public RealmResource realm() {
        return clientRequestFactory.createProxy(RealmsResource.class).realm(getRealm());
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public String getRealm() {
        return realm;
    }

    public void close() {

    }
}
