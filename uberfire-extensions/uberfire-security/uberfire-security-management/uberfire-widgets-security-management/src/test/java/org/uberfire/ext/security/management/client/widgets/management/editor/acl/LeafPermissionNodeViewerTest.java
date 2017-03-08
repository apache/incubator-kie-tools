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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.LeafPermissionNodeViewer;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeafPermissionNodeViewerTest {

    @Mock
    LeafPermissionNodeViewer.View view;

    @Mock
    Permission permission1;

    @Mock
    Permission permission2;

    LeafPermissionNodeViewer presenter;
    PermissionLeafNode permissionNode;

    @Before
    public void setUp() {
        presenter = new LeafPermissionNodeViewer(view);

        when(permission1.getResult()).thenReturn(AuthorizationResult.ACCESS_DENIED);
        when(permission1.getName()).thenReturn("p1");
        when(permission2.getResult()).thenReturn(AuthorizationResult.ACCESS_GRANTED);
        when(permission2.getName()).thenReturn("p2");

        permissionNode = new PermissionLeafNode();
        permissionNode.setNodeName("r1");
        permissionNode.setNodeFullName("r1 full");
        permissionNode.addPermission(permission1,
                                     "grant1",
                                     "deny1");
        permissionNode.addPermission(permission2,
                                     "grant2",
                                     "deny2");
        presenter.show(permissionNode);
    }

    @Test
    public void testShow() {
        assertNull(presenter.getChildren());
        assertEquals(presenter.getPermissionNode(),
                     permissionNode);

        verify(view).setNodeName("r1");
        verify(view).setNodeFullName("r1 full");
        verify(view).permissionDenied("deny1");
        verify(view).permissionGranted("grant2");
        verify(view,
               never()).permissionGranted("grant1");
        verify(view,
               never()).permissionDenied("deny2");
    }
}
