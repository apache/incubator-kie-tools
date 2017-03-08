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

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.client.exception.ResteasyClientException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.OAuth2Constants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.keycloak.client.auth.TokenService;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthTokenManagerTest {

    @Mock
    AuthSettings config;

    private AuthTokenManager tokenManager;
    private TokenService tokenService;
    private AccessTokenResponse response;

    @Before
    public void setup() throws Exception {
        when(config.getUsername()).thenReturn("user1");
        when(config.getRealm()).thenReturn("realm1");
        when(config.getClientId()).thenReturn("clientId1");
        when(config.getClientSecret()).thenReturn("clientSecret1");
        when(config.getPassword()).thenReturn("password1");
        when(config.getServerUrl()).thenReturn("serverUrl1");
        when(config.isPublicClient()).thenReturn(false);
        this.tokenManager = spy(new AuthTokenManager(config));
        this.tokenService = mock(TokenService.class);
        doReturn(tokenService).when(this.tokenManager).createTokenService();
        this.response = mock(AccessTokenResponse.class);
        doReturn(response).when(tokenService).grantToken(anyString(),
                                                         anyString(),
                                                         any(MultivaluedMap.class));
        when(response.getRefreshToken()).thenReturn("refreshToken");
    }

    @Test
    public void testGetRealm() throws Exception {
        String r = this.tokenManager.getRealm();
        Assert.assertEquals("realm1",
                            r);
    }

    @Test
    public void testGetAccessTokenString() throws Exception {
        AccessTokenResponse response = mock(AccessTokenResponse.class);
        when(response.getToken()).thenReturn("token2");
        this.tokenManager.accessTokenResponse = response;
        this.tokenManager.expirationTime = Long.MAX_VALUE;
        this.tokenManager.minTokenValidity = 0;
        String s = this.tokenManager.getAccessTokenString();
        Assert.assertEquals("token2",
                            s);
    }

    @Test
    public void testGrantToken() throws Exception {
        when(response.getExpiresIn()).thenReturn(1000l);
        this.tokenManager.grantToken();
        ArgumentCaptor<MultivaluedMap> mapCaptor = ArgumentCaptor.forClass(MultivaluedMap.class);
        verify(this.tokenService,
               times(1)).grantToken(eq("realm1"),
                                    anyString(),
                                    mapCaptor.capture());
        MultivaluedMap<String, String> mvm = mapCaptor.getValue();
        Assert.assertEquals("user1",
                            mvm.get("username").get(0));
        Assert.assertEquals("password1",
                            mvm.get("password").get(0));
    }

    @Test
    public void testGrantPublicToken() throws Exception {
        when(response.getExpiresIn()).thenReturn(1000l);
        when(config.isPublicClient()).thenReturn(true);
        this.tokenManager.grantToken();
        ArgumentCaptor<MultivaluedMap> mapCaptor = ArgumentCaptor.forClass(MultivaluedMap.class);
        verify(this.tokenService,
               times(1)).grantToken(eq("realm1"),
                                    anyString(),
                                    mapCaptor.capture());
        MultivaluedMap<String, String> mvm = mapCaptor.getValue();
        Assert.assertEquals("password",
                            mvm.get(OAuth2Constants.GRANT_TYPE).get(0));
        Assert.assertEquals("user1",
                            mvm.get("username").get(0));
        Assert.assertEquals("password1",
                            mvm.get("password").get(0));
        Assert.assertEquals("clientId1",
                            mvm.get(OAuth2Constants.CLIENT_ID).get(0));
    }

    @Test
    public void testRefreshToken() throws Exception {
        when(response.getExpiresIn()).thenReturn(1000l);
        this.tokenManager.accessTokenResponse = response;
        this.tokenManager.expirationTime = 0;
        this.tokenManager.minTokenValidity = 100;
        String s = this.tokenManager.getAccessTokenString();
        ArgumentCaptor<MultivaluedMap> mapCaptor = ArgumentCaptor.forClass(MultivaluedMap.class);
        verify(this.tokenService,
               times(1)).grantToken(eq("realm1"),
                                    anyString(),
                                    mapCaptor.capture());
        MultivaluedMap<String, String> mvm = mapCaptor.getValue();
        Assert.assertEquals("refresh_token",
                            mvm.get(OAuth2Constants.GRANT_TYPE).get(0));
        Assert.assertEquals("refreshToken",
                            mvm.get("refresh_token").get(0));
    }

    @Test
    public void testRefreshPublicToken() throws Exception {
        when(response.getExpiresIn()).thenReturn(1000l);
        when(config.isPublicClient()).thenReturn(true);
        this.tokenManager.accessTokenResponse = response;
        this.tokenManager.expirationTime = 0;
        this.tokenManager.minTokenValidity = 100;
        String s = this.tokenManager.getAccessTokenString();
        ArgumentCaptor<MultivaluedMap> mapCaptor = ArgumentCaptor.forClass(MultivaluedMap.class);
        verify(this.tokenService,
               times(1)).grantToken(eq("realm1"),
                                    anyString(),
                                    mapCaptor.capture());
        MultivaluedMap<String, String> mvm = mapCaptor.getValue();
        Assert.assertEquals("refresh_token",
                            mvm.get(OAuth2Constants.GRANT_TYPE).get(0));
        Assert.assertEquals("refreshToken",
                            mvm.get("refresh_token").get(0));
        Assert.assertEquals("clientId1",
                            mvm.get(OAuth2Constants.CLIENT_ID).get(0));
    }

    @Test(expected = RuntimeException.class)
    public void testClientError() throws Exception {
        ResteasyClientException exception = mock(ResteasyClientException.class);
        doThrow(exception).when(tokenService).grantToken(anyString(),
                                                         anyString(),
                                                         any(MultivaluedMap.class));
        when(response.getExpiresIn()).thenReturn(1000l);
        when(config.isPublicClient()).thenReturn(true);
        this.tokenManager.grantToken();
        Assert.assertNull(this.tokenManager.accessTokenResponse);
    }
}
