/*
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

package org.uberfire.client.authz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.AbstractWorkbenchPerspectiveActivity;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.PerspectiveActivity;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.impl.DefaultLoadOptions;
import org.uberfire.security.impl.authz.DefaultPermissionManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PerspectiveTreeProviderTest {

    @Mock
    ActivityBeansCache activityBeansCache;

    @Mock
    PermissionTreeI18n i18n;

    @Mock
    PermissionTree permissionTree;

    PermissionManager permissionManager;
    PerspectiveTreeProvider provider;
    PermissionNode root;

    @Before
    public void setUp() {
        List<SyncBeanDef<Activity>> beanDefs = new ArrayList<>();
        SyncBeanDef<Activity> bean1 = mock(SyncBeanDef.class);
        SyncBeanDef<Activity> bean2 = mock(SyncBeanDef.class);
        SyncBeanDef<Activity> bean3 = mock(SyncBeanDef.class);
        SyncBeanDef<Activity> bean4 = mock(SyncBeanDef.class);
        PerspectiveActivity perspective1 = mock(AbstractWorkbenchPerspectiveActivity.class);
        PerspectiveActivity perspective2 = mock(PerspectiveActivity.class);
        PerspectiveActivity perspective3 = mock(PerspectiveActivity.class);
        PerspectiveActivity perspective4 = mock(AbstractWorkbenchPerspectiveActivity.class);
        when(bean1.getInstance()).thenReturn(perspective1);
        when(bean2.getInstance()).thenReturn(perspective2);
        when(bean3.getInstance()).thenReturn(perspective3);
        when(bean4.getInstance()).thenReturn(perspective4);
        when(perspective1.getIdentifier()).thenReturn("Perspective1");
        when(perspective2.getIdentifier()).thenReturn("Perspective2");
        when(perspective3.getIdentifier()).thenReturn("org.Perspective3");
        when(perspective4.getIdentifier()).thenReturn("org.Perspective4");
        beanDefs.add(bean1);
        beanDefs.add(bean2);
        beanDefs.add(bean3);
        beanDefs.add(bean4);
        when(activityBeansCache.getActivity("Perspective1")).thenReturn(bean1);
        when(activityBeansCache.getActivity("Perspective2")).thenReturn(bean2);
        when(activityBeansCache.getActivity("org.Perspective3")).thenReturn(bean3);
        when(activityBeansCache.getActivity("org.Perspective4")).thenReturn(bean4);
        when(activityBeansCache.getPerspectiveActivities()).thenReturn(beanDefs);

        permissionManager = new DefaultPermissionManager();
        provider = new PerspectiveTreeProvider(activityBeansCache,
                                               permissionManager,
                                               i18n);
        provider.setRootNodeName("root");
        provider.setPerspectiveName("Perspective1",
                                    "A nice perspective");
        provider.setPerspectiveName("Perspective2",
                                    "Another nice perspective");
        root = provider.buildRootNode();
        root.setPermissionTree(permissionTree);
    }

    @Test
    public void testEmpty() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               0);
                              });
    }

    @Test
    public void testIncludedResourceIds() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setResourceIds(Arrays.asList("Perspective1"));
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                              });
    }

    @Test
    public void testExcludedResourceIds() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setResourceIds(Arrays.asList("Perspective1"));
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                              });
    }

    @Test
    public void testExcludedPerspectiveIds() {
        provider.excludePerspectiveId("Perspective1");
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               3);
                              });
    }

    @Test
    public void testNameSearch1() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("nice");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               2);
                              });
    }

    @Test
    public void testNameSearch2() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("another");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                              });
    }

    @Test
    public void testNameSearch3() {
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("another");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                              });
    }

    @Test
    public void testRootNode() {
        assertEquals(root.getPermissionList().size(),
                     4);
        checkDependencies(root, 3);
    }

    @Test
    public void testChildrenNodes() {
        root.expand(children -> {
            for (PermissionNode child : children) {
                assertEquals(child.getPermissionList().size(),
                             3);
                checkDependencies(child, 2);
            }
        });
    }

    @Test
    public void testPerspectiveName() {
        String name = provider.getPerspectiveName("Perspective1");
        assertEquals(name, "A nice perspective");
        name = provider.getPerspectiveName("Perspective2");
        assertEquals(name, "Another nice perspective");
        name = provider.getPerspectiveName("org.Perspective3");
        assertEquals(name, "org.Perspective3");
        name = provider.getPerspectiveName("org.Perspective4");
        assertEquals(name, "Perspective4");
    }

    protected void checkDependencies(PermissionNode permissionNode, int numberOfDependencies) {
        for (Permission permission : permissionNode.getPermissionList()) {
            Collection<Permission> dependencies = permissionNode.getDependencies(permission);

            if (permission.getName().startsWith("perspective.read")) {
                assertEquals(dependencies.size(),
                             numberOfDependencies);
            } else {
                assertNull(dependencies);
            }
        }
    }
}