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

package org.uberfire.ext.security.management.keycloak.client.auth.credentials;

import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.client.core.ClientErrorInterceptor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.keycloak.OAuth2Constants;
import org.keycloak.common.util.Time;
import org.keycloak.util.BasicAuthHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.security.management.keycloak.client.auth.TokenManager;
import org.uberfire.ext.security.management.keycloak.client.auth.TokenService;

/**
 * Token manager that uses credentials based authentication settings to manage the access token.
 * Handles:
 * - Public / non public clients
 * - Token refreshments based on OAuth2 token's expiration time.
 * @since 0.9.0
 */
public class AuthTokenManager implements TokenManager {

    private static final Logger LOG = LoggerFactory.getLogger(AuthTokenManager.class);
    private static final long DEFAULT_MIN_VALIDITY = 30;
    private final AuthSettings config;
    long expirationTime;
    long minTokenValidity = DEFAULT_MIN_VALIDITY;
    AccessTokenResponse accessTokenResponse;
    private final ClientErrorInterceptor clientErrorInterceptor = new ClientErrorInterceptor() {
        @Override
        public void handle(ClientResponse<?> response) throws RuntimeException {

            // Whatever the error is, let's nullify the current access token response.
            AuthTokenManager.this.accessTokenResponse = null;

            // Handle some of the common errors.
            String error = null;
            Exception exception = null;

            try {

                BaseClientResponse r = (BaseClientResponse) response;
                InputStream stream = r.getStreamFactory().getInputStream();
                stream.reset();

                if (Response.Status.FORBIDDEN.equals(response.getResponseStatus())) {
                    error = "Error handling the Keycloak token, status is FORBIDDEN";
                } else if (Response.Status.UNAUTHORIZED.equals(response.getResponseStatus())) {
                    error = "Error handling the Keycloak token, status is UNAUTHORIZED";
                } else if (Response.Status.BAD_REQUEST.equals(response.getResponseStatus())) {
                    error = "Error handling the Keycloak token, status is BAD_REQUEST. Response data: " + getResponseData(r);
                } else if (Response.Status.NOT_FOUND.equals(response.getResponseStatus())) {
                    error = "Error handling the Keycloak token, status is NOT_FOUND.";
                } else if (!Response.Status.OK.equals(response.getResponseStatus())) {
                    error = "Error handling the Keycloak token. Response status is " + response.getResponseStatus() +
                            ". Response data: " + getResponseData(r);
                }
            } catch (IOException e) {

                error = "Error handling the Keycloak token.";
                exception = e;
            } finally {

                response.releaseConnection();
            }

            // If error is handled here, log it and throw the exception.
            // Otherwise, let's Resteasy do the generic work after a client error.
            if (null != error) {

                LOG.error(error);

                if (null != exception) {
                    throw new RuntimeException(error,
                                               exception);
                } else {
                    throw new RuntimeException(error);
                }
            }
        }

        private String getResponseData(BaseClientResponse response) {
            try {
                return (String) response.getEntity(String.class);
            } catch (Exception e) {
                LOG.error("Error trying to obtain response data as String.",
                          e);
            }
            return null;
        }
    };

    public AuthTokenManager(AuthSettings config) {
        this.config = config;
    }

    @Override
    public void grantToken() {
        MultivaluedMap<String, String> mvm = new CaseInsensitiveMap<String>();
        mvm.putSingle(OAuth2Constants.GRANT_TYPE,
                      "password");
        mvm.putSingle("username",
                      config.getUsername());
        mvm.putSingle("password",
                      config.getPassword());
        consumeGrantTokenService(mvm);
    }

    private void refreshToken() {
        MultivaluedMap<String, String> mvm = new CaseInsensitiveMap<String>();
        mvm.putSingle(OAuth2Constants.GRANT_TYPE,
                      "refresh_token");
        mvm.putSingle("refresh_token",
                      accessTokenResponse.getRefreshToken());
        consumeGrantTokenService(mvm);
    }

    protected void consumeGrantTokenService(final MultivaluedMap<String, String> mvm) {

        boolean isPublic = config.isPublicClient();

        String authorization = "";
        if (isPublic) { // if client is public access type
            mvm.putSingle(OAuth2Constants.CLIENT_ID,
                          config.getClientId());
        } else {
            authorization = BasicAuthHelper.createHeader(config.getClientId(),
                                                         config.getClientSecret());
        }

        TokenService client = createTokenService();
        AccessTokenResponse response = client.grantToken(config.getRealm(),
                                                         authorization,
                                                         mvm);

        int requestTime = Time.currentTime();
        expirationTime = requestTime + response.getExpiresIn();
        this.accessTokenResponse = response;
    }

    @Override
    public String getAccessTokenString() {
        if (null == this.accessTokenResponse) {
            grantToken();
        } else if (tokenExpired()) {
            refreshToken();
        }
        return accessTokenResponse != null ? accessTokenResponse.getToken() : null;
    }

    @Override
    public String getRealm() {
        return config.getRealm();
    }

    TokenService createTokenService() {
        ResteasyProviderFactory pf = ResteasyProviderFactory.getInstance();
        pf.addClientErrorInterceptor(clientErrorInterceptor);
        return ProxyFactory.create(TokenService.class,
                                   config.getServerUrl());
    }

    private boolean tokenExpired() {
        return accessTokenResponse != null && (Time.currentTime() + minTokenValidity) >= expirationTime;
    }
}
