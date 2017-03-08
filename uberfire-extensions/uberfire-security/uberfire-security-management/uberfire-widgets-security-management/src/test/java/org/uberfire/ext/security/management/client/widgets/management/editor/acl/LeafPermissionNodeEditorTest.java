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

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.LeafPermissionNodeEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionSwitch;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionWidgetFactory;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LeafPermissionNodeEditorTest {

    @Mock
    LeafPermissionNodeEditor.View view;

    @Mock
    PermissionSwitch.View permissionSwitchView1;

    @Mock
    PermissionSwitch.View permissionSwitchView2;

    @Mock
    PermissionWidgetFactory widgetFactory;

    @Mock
    Event<PermissionChangedEvent> changedEvent;

    @Mock
    Permission permission1;

    @Mock
    Permission permission2;

    @Mock
    Command onChange;

    LeafPermissionNodeEditor presenter;
    PermissionSwitch permissionSwitch1;
    PermissionSwitch permissionSwitch2;
    PermissionLeafNode permissionNode;

    @Before
    public void setUp() {
        presenter = new LeafPermissionNodeEditor(view,
                                                 widgetFactory,
                                                 changedEvent);
        permissionSwitch1 = spy(new PermissionSwitch(permissionSwitchView1));
        permissionSwitch2 = spy(new PermissionSwitch(permissionSwitchView2));

        when(widgetFactory.createSwitch()).thenReturn(permissionSwitch1,
                                                      permissionSwitch2);
        when(permission1.getResult()).thenReturn(AuthorizationResult.ACCESS_DENIED);
        when(permission1.getName()).thenReturn("p1");
        when(permission2.getResult()).thenReturn(AuthorizationResult.ACCESS_GRANTED);
        when(permission2.getName()).thenReturn("p2");

        permissionNode = new PermissionLeafNode();
        permissionNode.setNodeName("r1");
        permissionNode.addPermission(permission1,
                                     "grant1",
                                     "deny1");
        permissionNode.addPermission(permission2,
                                     "grant2",
                                     "deny2");
    }

    @Test
    public void testInit() {
        presenter.edit(permissionNode);

        assertTrue(presenter.getChildEditors().isEmpty());
        assertEquals(presenter.getPermissionNode(),
                     permissionNode);

        verify(view).setNodeName("r1");
        verify(view,
               never()).setNodeFullName(anyString());
        verify(view).addPermission(permissionSwitch1);
        verify(view).addPermission(permissionSwitch2);
        verify(permissionSwitch1).init(eq("grant1"),
                                       eq("deny1"),
                                       eq(false),
                                       eq(0));
        verify(permissionSwitch2).init(eq("grant2"),
                                       eq("deny2"),
                                       eq(true),
                                       eq(0));
    }

    @Test
    public void testSwitchChange() {
        presenter.edit(permissionNode);

        permissionSwitch1.onChange();

        verify(permission1).setResult(any());
        verify(changedEvent).fire(any());
    }

    @Test
    public void testSwitchInitDependencies() {
        when(permissionSwitchView1.isOn()).thenReturn(false);
        when(permissionSwitchView2.isOn()).thenReturn(true);

        reset(permission2);
        permissionNode.addDependencies(permission1,
                                       permission2);
        presenter.edit(permissionNode);

        verify(permission2).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitch2).setEnabled(false);
        verify(permissionSwitch2).setOn(false);
    }

    @Test
    public void testSwitchChangeDependencies() {
        permissionNode.addDependencies(permission1,
                                       permission2);
        presenter.edit(permissionNode);

        reset(permission2);
        reset(permissionSwitch2);
        when(permissionSwitch1.isOn()).thenReturn(false);
        permissionSwitch1.onChange();

        verify(permission2).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitch2).setEnabled(false);
        verify(permissionSwitch2).setOn(false);

        reset(permissionSwitch2);
        when(permissionSwitch1.isOn()).thenReturn(true);
        permissionSwitch1.onChange();

        verify(permissionSwitch2).setEnabled(true);
        verify(permissionSwitch2,
               never()).setOn(anyBoolean());
    }
}
