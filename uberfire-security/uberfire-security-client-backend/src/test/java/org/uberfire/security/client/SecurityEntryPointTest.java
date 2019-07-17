/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.security.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityEntryPointTest {

    @Mock
    private PermissionManager permissionManager;

    @InjectMocks
    private SecurityEntryPoint securityEntryPoint;

    @Test
    public void onPolicySavedTest() {
        final AuthorizationPolicy policy = mock(AuthorizationPolicy.class);
        final AuthorizationPolicySavedEvent event = new AuthorizationPolicySavedEvent(policy);

        securityEntryPoint.onPolicySaved(event);

        verify(permissionManager).setAuthorizationPolicy(policy);
    }
}
