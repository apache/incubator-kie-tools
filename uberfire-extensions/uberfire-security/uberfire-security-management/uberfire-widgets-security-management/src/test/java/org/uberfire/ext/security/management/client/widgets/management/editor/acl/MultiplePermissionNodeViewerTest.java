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

package org.uberfire.ext.security.management.client.widgets.management.editor.acl;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.MultiplePermissionNodeViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionNodeViewer;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionWidgetFactory;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;
import org.uberfire.security.impl.authz.DotNamedPermission;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultiplePermissionNodeViewerTest {

    @Mock
    MultiplePermissionNodeViewer.View view;

    @Mock
    PermissionWidgetFactory widgetFactory;

    @Mock
    PermissionNode childNode1;

    @Mock
    PermissionNode childNode2;

    @Mock
    PermissionNodeViewer childViewer1;

    @Mock
    PermissionNodeViewer childViewer2;

    MultiplePermissionNodeViewer presenter;
    PermissionGroupNode permissionGroupNode;
    PermissionResourceNode permissionResourceNode;
    Permission permission1;
    Permission permission2;
    Permission permission3;

    @Before
    public void setUp() {
        presenter = new MultiplePermissionNodeViewer(view,
                                                     widgetFactory);

        when(widgetFactory.createViewer(childNode1)).thenReturn(childViewer1);
        when(widgetFactory.createViewer(childNode2)).thenReturn(childViewer2);

        permission1 = new DotNamedPermission("p1",
                                             false);
        permission2 = new DotNamedPermission("p2",
                                             true);
        permission3 = new DotNamedPermission("p2.a",
                                             false);

        permissionGroupNode = spy(new PermissionGroupNode(null));
        permissionGroupNode.setNodeName("r1");

        permissionResourceNode = spy(new PermissionResourceNode("r2",
                                                                null));
        permissionResourceNode.setNodeName("r2");
        permissionResourceNode.addPermission(permission1,
                                             "grant1",
                                             "deny1");
        permissionResourceNode.addPermission(permission2,
                                             "grant2",
                                             "deny2");

        when(childNode1.getNodeName()).thenReturn("p2.a");
        when(childNode1.getPermissionList()).thenReturn(Arrays.asList(permission3));

        doAnswer(invocationOnMock -> {
            LoadCallback callback = (LoadCallback) invocationOnMock.getArguments()[0];
            callback.afterLoad(Arrays.asList(childNode1,
                                             childNode2));
            return null;
        }).when(permissionGroupNode).expand(any(LoadCallback.class));

        doAnswer(invocationOnMock -> {
            LoadCallback callback = (LoadCallback) invocationOnMock.getArguments()[0];
            callback.afterLoad(Arrays.asList(childNode1));
            return null;
        }).when(permissionResourceNode).expand(any(LoadCallback.class));
    }

    @Test
    public void testInitGroupNode() {
        presenter.show(permissionGroupNode);

        assertEquals(presenter.getPermissionNode(),
                     permissionGroupNode);

        verify(view).setNodeName("r1");
        verify(view,
               never()).setNodeFullName(anyString());
        verify(view).setPermissionsVisible(false);
        verify(view).addChildViewer(childViewer1);
        verify(view).addChildViewer(childViewer2);
    }

    @Test
    public void testInitResourceNode() {
        presenter.show(permissionResourceNode);

        assertEquals(presenter.getPermissionNode(),
                     permissionResourceNode);

        verify(view,
               never()).addChildViewer(any());
        verify(view).setNodeName("r2");
        verify(view,
               never()).setNodeFullName(anyString());
        verify(view).setPermissionsVisible(true);
        verify(view).addItemsGrantedPermission("grant2",
                                               "r2");
        verify(view).addItemException("p2.a");
    }
}
