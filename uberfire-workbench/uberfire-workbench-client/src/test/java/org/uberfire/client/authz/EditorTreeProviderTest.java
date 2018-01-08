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
package org.uberfire.client.authz;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.ActivityBeansCache;
import org.uberfire.client.mvp.WorkbenchEditorActivity;
import org.uberfire.client.resources.i18n.PermissionTreeI18n;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.authz.PermissionManager;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTree;
import org.uberfire.security.client.authz.tree.impl.DefaultLoadOptions;
import org.uberfire.security.impl.authz.DefaultPermissionManager;
import org.uberfire.workbench.model.ActivityResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EditorTreeProviderTest {

    private static final String EDITOR1_ID = "Editor1";
    private static final String EDITOR2_ID = "Editor2";
    private static final String EDITOR1_NAME = "Editor 1 name";
    private static final String EDITOR2_NAME = "Editor 2 name";

    @Mock
    private ActivityBeansCache activityBeansCache;

    @Mock
    private PermissionTreeI18n i18n;

    @Mock
    private PermissionTree permissionTree;

    @Mock
    private SyncBeanManager iocManager;

    @Captor
    private ArgumentCaptor<WorkbenchEditorActivity> editorActivityCaptor;

    private PermissionManager permissionManager;
    private EditorTreeProvider provider;
    private PermissionNode root;

    @Before
    public void setUp() {
        final SyncBeanDef<Activity> editor1 = makeWorkbenchEditorActivity(EDITOR1_ID);
        final SyncBeanDef<Activity> editor2 = makeWorkbenchEditorActivity(EDITOR2_ID);
        when(activityBeansCache.getActivity(EDITOR1_ID)).thenReturn(editor1);
        when(activityBeansCache.getActivity(EDITOR2_ID)).thenReturn(editor2);

        when(i18n.editorResourceName()).thenReturn("Editor Resource name");
        when(i18n.editorsNodeName()).thenReturn("Editor Node name");

        permissionManager = new DefaultPermissionManager();
        provider = new EditorTreeProvider(activityBeansCache,
                                          iocManager,
                                          permissionManager,
                                          i18n);

        root = provider.buildRootNode();
        root.setPermissionTree(permissionTree);
    }

    @SuppressWarnings("unchecked")
    private SyncBeanDef<Activity> makeWorkbenchEditorActivity(final String editorId) {
        final SyncBeanDef<Activity> beanDef = mock(SyncBeanDef.class);
        final WorkbenchEditorActivity bean = mock(WorkbenchEditorActivity.class);
        when(beanDef.getInstance()).thenReturn(bean);
        when(bean.getIdentifier()).thenReturn(editorId);
        when(bean.getResourceType()).thenReturn(ActivityResourceType.EDITOR);
        return beanDef;
    }

    @Test
    public void testEmpty() {
        final DefaultLoadOptions options = new DefaultLoadOptions();
        provider.loadChildren(root,
                              options,
                              children -> assertEquals(children.size(),
                                                       0));
    }

    @Test
    public void testRegisterEditorDiscardsEditorInstance() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        verify(iocManager).destroyBean(editorActivityCaptor.capture());

        final WorkbenchEditorActivity editorActivity = editorActivityCaptor.getValue();
        assertEquals(EDITOR1_ID,
                     editorActivity.getIdentifier());
    }

    @Test
    public void testIncludedResourceIds() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        final DefaultLoadOptions options = new DefaultLoadOptions();
        options.setResourceIds(Collections.singletonList(EDITOR1_ID));
        provider.loadChildren(root,
                              options,
                              children -> assertEquals(children.size(),
                                                       1));
    }

    @Test
    public void testRegisterEditor() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        final DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("");
        provider.loadChildren(root,
                              options,
                              children -> assertEquals(children.size(),
                                                       1));
    }

    @Test
    public void testNameSearch1() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        provider.registerEditor(EDITOR2_ID,
                                EDITOR2_NAME);
        final DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("name");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               2);
                                  assertContains(EDITOR1_NAME,
                                                 children);
                                  assertContains(EDITOR2_NAME,
                                                 children);
                              });
    }

    private void assertContains(final String editorName,
                                final List<PermissionNode> children) {
        assertTrue(children.stream()
                           .filter(p -> p.getNodeName().equals(editorName))
                           .findFirst()
                           .isPresent());
    }

    @Test
    public void testNameSearch2() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        provider.registerEditor(EDITOR2_ID,
                                EDITOR2_NAME);
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("1");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                                  assertEquals(EDITOR1_NAME,
                                               children.get(0).getNodeName());
                              });
    }

    @Test
    public void testNameSearch3() {
        provider.registerEditor(EDITOR1_ID,
                                EDITOR1_NAME);
        provider.registerEditor(EDITOR2_ID,
                                EDITOR2_NAME);
        DefaultLoadOptions options = new DefaultLoadOptions();
        options.setNodeNamePattern("2");
        provider.loadChildren(root,
                              options,
                              children -> {
                                  assertEquals(children.size(),
                                               1);
                                  assertEquals(EDITOR2_NAME,
                                               children.get(0).getNodeName());
                              });
    }

    @Test
    public void testRootNode() {
        assertEquals(root.getPermissionList().size(),
                     1);
        checkDependencies(root);
    }

    @Test
    public void testChildrenNodes() {
        root.expand(children -> {
            for (PermissionNode child : children) {
                assertEquals(child.getPermissionList().size(),
                             3);
                checkDependencies(child);
            }
        });
    }

    protected void checkDependencies(PermissionNode permissionNode) {
        for (Permission permission : permissionNode.getPermissionList()) {
            Collection<Permission> dependencies = permissionNode.getDependencies(permission);

            if (permission.getName().startsWith("perspective.read")) {
                assertEquals(dependencies.size(),
                             2);
            } else {
                assertNull(dependencies);
            }
        }
    }
}