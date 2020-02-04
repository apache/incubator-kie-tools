/**
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.backend.server.authz;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.event.Event;

import org.jboss.errai.security.shared.api.Group;
import org.jboss.errai.security.shared.api.GroupImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.authz.AuthorizationPolicyStorage;
import org.uberfire.backend.events.AuthorizationPolicyDeployedEvent;
import org.uberfire.backend.events.AuthorizationPolicySavedEvent;
import org.uberfire.security.authz.AuthorizationPolicy;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.authz.PermissionTypeRegistry;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.security.impl.authz.DefaultPermissionTypeRegistry;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServiceTest {

    AuthorizationServiceImpl authorizationService;

    AuthorizationPolicyDeployer deployer;

    @Mock
    AuthorizationPolicyStorage storage;

    @Mock
    Event<AuthorizationPolicyDeployedEvent> event;

    @Mock
    Event<AuthorizationPolicySavedEvent> policySavedEvent;

    PermissionManager permissionManager;

    private static final String path = "WEB-INF/classes/security-policy.properties";

    @Before
    public void setUp(){
        PermissionTypeRegistry permissionTypeRegistry = new DefaultPermissionTypeRegistry();
        permissionManager = spy(new DefaultPermissionManager(permissionTypeRegistry));
        deployer = new AuthorizationPolicyDeployer(storage, permissionManager, event);
        authorizationService = new AuthorizationServiceImpl(storage, permissionManager, policySavedEvent);
    }

    @Test
    public void testPolicyLoad() throws Exception {
        getPolicyFromPath(path);
    }

    @Test
    public void testPolicySave() throws Exception {
        Path policyDir = getPolicyFromPath(path);
        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());
        AuthorizationPolicy ap = policyCaptor.getValue();

        authorizationService.savePolicy(ap);
        verify(permissionManager, times(2)).setAuthorizationPolicy(ap);
        verify(policySavedEvent).fire(any());
    }

    @Test
    public void testPolicyDelete() throws Exception {
        Path policyDir = getPolicyFromPath(path);
        deployer.deployPolicy(policyDir);

        ArgumentCaptor<AuthorizationPolicy> policyCaptor = ArgumentCaptor.forClass(AuthorizationPolicy.class);
        verify(storage).loadPolicy();
        verify(storage).savePolicy(policyCaptor.capture());

        AuthorizationPolicy ap = policyCaptor.getValue();
        Group group = new GroupImpl("group1");
        authorizationService.deletePolicyByGroup(group, ap);
        verify(storage).deletePolicyByGroup(group, ap);
        verify(permissionManager, times(2)).setAuthorizationPolicy(any());
        verify(policySavedEvent, times(1)).fire(any());
    }

    private Path getPolicyFromPath(String path) throws Exception {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource(path);
        Path policyDir = Paths.get(fileURL.toURI()).getParent();
        assertNotNull(policyDir);
        return policyDir;
    }
}
