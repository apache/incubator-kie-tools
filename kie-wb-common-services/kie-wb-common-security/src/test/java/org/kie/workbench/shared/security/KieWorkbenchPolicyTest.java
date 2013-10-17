/*
 * Copyright 2012 JBoss Inc
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
package org.kie.workbench.shared.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.security.KieWorkbenchACL;
import org.kie.workbench.common.services.security.KieWorkbenchFeature;
import org.kie.workbench.common.services.security.KieWorkbenchFeatureRegistry;
import org.kie.workbench.common.services.security.KieWorkbenchPolicy;
import org.kie.workbench.common.services.security.impl.KieWorkbenchACLImpl;

import static org.fest.assertions.api.Assertions.assertThat;

public class KieWorkbenchPolicyTest {

    private KieWorkbenchFeatureRegistry registry;
    private KieWorkbenchPolicy policy;
    private KieWorkbenchACLImpl acl;

    @Before
    public void setUp() throws Exception {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("workbench-policy.properties");
        String policyStr = fromStream(is);

        policy = new KieWorkbenchPolicy(policyStr);
        registry = new KieWorkbenchFeatureRegistry();

        acl = new KieWorkbenchACLImpl();
        acl.setFeatureRegistry(registry);
        acl.activatePolicy(policy);
    }


    public static String fromStream(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line).append("\n");
        }
        return out.toString();
    }

    @Test
    public void testImply() {
        KieWorkbenchFeature endUsers = registry.getFeature("wb_for_business_users");
        KieWorkbenchFeature admin = registry.getFeature("wb_administration");
        KieWorkbenchFeature tasks = registry.getFeature("wb_tasks");
        assertThat(endUsers).isNotNull();
        assertThat(admin).isNotNull();
        assertThat(tasks).isNotNull();
        assertThat(endUsers.implies(tasks));
        assertThat(!endUsers.implies(admin));
    }

    @Test
    public void testGranted() {
        Set<String> roles = acl.getGrantedRoles("wb_administration");
        assertThat(roles).contains("admin");

        roles = acl.getGrantedRoles("wb_asset_repository");
        assertThat(roles).contains("admin", "developer");

        roles = acl.getGrantedRoles("wb_jobs");
        assertThat(roles).contains("admin", "developer");

        roles = acl.getGrantedRoles("wb_tasks");
        assertThat(roles).contains("admin", "analyst", "developer", "user");

        roles = acl.getGrantedRoles("wb_dashboard_builder");
        assertThat(roles).contains("user", "manager");
    }

    @Test
    public void testDeny() {
        Set<String> roles = acl.getGrantedRoles("wb_administration");
        assertThat(roles).doesNotContain("developer", "analyst", "user", "manager");

        roles = acl.getGrantedRoles("wb_jobs");
        assertThat(roles).doesNotContain("analyst");

        roles = acl.getGrantedRoles("wb_deploy");
        assertThat(roles).doesNotContain("manager");
    }
}
