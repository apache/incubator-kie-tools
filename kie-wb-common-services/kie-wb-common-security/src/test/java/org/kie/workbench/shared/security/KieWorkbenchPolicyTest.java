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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
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
        Properties props = new Properties();
        props.load(is);

        policy = new KieWorkbenchPolicy();
        for (String key : props.stringPropertyNames()) {
            policy.put(key, props.getProperty(key));
        }
        registry = new KieWorkbenchFeatureRegistry();
        acl = new KieWorkbenchACLImpl();
        acl.setFeatureRegistry(registry);
        acl.activatePolicy(policy);
    }

    @Test
    public void testImply() {
        KieWorkbenchFeature endUsers = registry.getFeature("wb_end_users");
        KieWorkbenchFeature admin = registry.getFeature("wb_administration");
        KieWorkbenchFeature tasks = registry.getFeature("wb_tasks");
        assertThat(endUsers.implies(tasks));
        assertThat(!endUsers.implies(admin));
    }

    @Test
    public void testDeny() {
        Set<String> roles = acl.getGrantedRoles("wb_search");
        assertThat(roles).doesNotContain("admin");
        assertThat(roles).doesNotContain("analyst");

        roles = acl.getGrantedRoles("wb_jobs");
        assertThat(roles).doesNotContain("analyst");
    }
}
