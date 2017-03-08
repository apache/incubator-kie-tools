/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.security.management.keycloak;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.BaseTest;
import org.uberfire.ext.security.management.keycloak.client.Keycloak;
import org.uberfire.ext.security.management.keycloak.client.resource.RealmResource;
import org.uberfire.ext.security.management.keycloak.client.resource.RolesResource;
import org.uberfire.ext.security.management.keycloak.client.resource.UsersResource;

import static org.mockito.Mockito.*;

/**
 * <p>Base test class for KeyCloak based services.</p>
 * <p>It provides a mocked service for the remote KeyCloak Admin API for version <code>1.2.0.Final</code>.</p>
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseKeyCloakTest extends BaseTest {

    public static final String REALM = "testRealm";

    @Mock
    protected Keycloak keycloakMock;

    @Mock
    protected RealmResource realmResource;

    @Mock
    protected UsersResource usersResource;

    @Mock
    protected RolesResource rolesResource;

    @Before
    public void setup() throws Exception {
        when(realmResource.users()).thenReturn(usersResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(keycloakMock.realm()).thenReturn(realmResource);
    }
}
