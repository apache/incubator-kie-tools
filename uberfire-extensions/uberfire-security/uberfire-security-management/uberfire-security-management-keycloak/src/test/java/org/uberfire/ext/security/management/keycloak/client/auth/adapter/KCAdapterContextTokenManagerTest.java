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
package org.uberfire.ext.security.management.keycloak.client.auth.adapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakSecurityContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class KCAdapterContextTokenManagerTest {

    @Mock
    HttpServletRequest request;
    
    @Mock
    HttpSession session;
    
    @Mock
    KeycloakSecurityContext context;

    private KCAdapterContextTokenManager tested;

    @Before
    public void setup() throws Exception {
        when(request.getAttribute(KeycloakSecurityContext.class.getName())).thenReturn(context);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(KeycloakSecurityContext.class.getName())).thenReturn(context);
        when(context.getTokenString()).thenReturn("token1");
        when(context.getRealm()).thenReturn("realm1");
        this.tested = new KCAdapterContextTokenManager(request);
    }

    @Test
    public void testGetAccessTokenString() throws Exception {
        String s = this.tested.getAccessTokenString();
        Assert.assertEquals("token1",
                            s);
    }

    @Test
    public void testGetRealm() throws Exception {
        String s = this.tested.getRealm();
        Assert.assertEquals("realm1",
                            s);
    }
}
