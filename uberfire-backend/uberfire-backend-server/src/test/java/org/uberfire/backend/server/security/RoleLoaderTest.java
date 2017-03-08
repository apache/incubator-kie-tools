/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.backend.server.security;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.jboss.errai.security.shared.api.Role;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.server.WebAppSettings;

import static org.junit.Assert.*;

public class RoleLoaderTest {

    @Before
    public void setUp() {
        URL fileURL = Thread.currentThread().getContextClassLoader().getResource("WEB-INF/classes/security-policy.properties");
        String homeDir = new File(fileURL.getPath()).getParentFile().getParentFile().getParent();
        WebAppSettings.get().setRootDir(homeDir);
        RoleRegistry.get().clear();
        RoleLoader roleLoader = new RoleLoader();
        roleLoader.registerRolesFromwWebXml();
    }

    @Test
    public void testLoad() {
        Set<Role> roles = RoleRegistry.get().getRegisteredRoles();
        assertEquals(roles.size(),
                     2);
        assertNotNull(RoleRegistry.get().getRegisteredRole("role1"));
        assertNotNull(RoleRegistry.get().getRegisteredRole("role2"));
        assertNull(RoleRegistry.get().getRegisteredRole("empty"));
    }
}
