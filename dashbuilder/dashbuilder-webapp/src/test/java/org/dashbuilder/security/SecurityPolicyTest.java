/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.security;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import org.dashbuilder.perspectives.PerspectiveIds;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.server.authz.AuthorizationPolicyDeployer;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionCollection;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.uberfire.security.authz.AuthorizationResult.*;

@RunWith(MockitoJUnitRunner.class)
public class SecurityPolicyTest {

    static final String HOME_PERSPECTIVE = PerspectiveIds.HOME;

    static final List<String> DEFAULT_DENIED = Arrays.asList(
            "perspective.read",
            "perspective.create",
            "perspective.delete",
            "perspective.update");

    @Mock
    AuthorizationPolicyStorage storage;

    @Mock
    Event<AuthorizationPolicyDeployedEvent> deployedEvent;

    AuthorizationPolicyDeployer deployer;
    PermissionManager permissionManager;
    AuthorizationPolicy policy;

    @Before
    public void setUp() throws Exception {
        permissionManager = new DefaultPermissionManager();
        deployer = new AuthorizationPolicyDeployer(storage, permissionManager, deployedEvent);

        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("security-policy.properties");
        Path policyDir = Paths.get(fileURL.toURI()).getParent();
        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());
        policy = policyCaptor.getValue();
    }

    @Test
    public void testPolicyDeployment() {
        assertNotNull(policy);
        assertEquals(policy.getRoles().size(), 1);

        verify(storage).savePolicy(policy);
        verify(deployedEvent).fire(any());
    }

    @Test
    public void testDefaultPermissions() {
        assertEquals(policy.getHomePerspective(), HOME_PERSPECTIVE);
        PermissionCollection pc = policy.getPermissions();

        for (String permissionName : DEFAULT_DENIED) {
            Permission p = pc.get(permissionName);
            assertNotNull(p);
            assertEquals(p.getResult(), ACCESS_DENIED);
        }
    }

    @Test
    public void testAdminPermissions() {
        testPermissions(new RoleImpl("admin"), null, HOME_PERSPECTIVE, ACCESS_GRANTED, null);
    }

    public void testPermissions(Role role,
                                List<String> exceptionList,
                                String homeExpected,
                                AuthorizationResult defaultExpected,
                                AuthorizationResult exceptionExpected) {

        assertEquals(role != null ? policy.getHomePerspective(role) : policy.getHomePerspective(), homeExpected);
        PermissionCollection pc = policy.getPermissions(role);

        for (String permissionName : DEFAULT_DENIED) {
            if (exceptionList == null || !exceptionList.contains(permissionName)) {
                Permission p = pc.get(permissionName);
                assertNotNull(p);
                assertEquals(p.getResult(), defaultExpected);
            }
        }
        if (exceptionList != null) {
            for (String permissionName : exceptionList) {
                Permission p = pc.get(permissionName);
                assertNotNull(p);
                assertEquals(p.getResult(), exceptionExpected);
            }
        }
    }
}
