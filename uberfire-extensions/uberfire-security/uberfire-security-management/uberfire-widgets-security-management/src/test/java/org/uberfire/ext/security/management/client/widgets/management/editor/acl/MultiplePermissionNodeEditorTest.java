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

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.LeafPermissionNodeEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.MultiplePermissionNodeEditor;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionExceptionSwitch;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionSwitch;
import org.uberfire.ext.security.management.client.widgets.management.editor.acl.node.PermissionWidgetFactory;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionChangedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeAddedEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.PermissionNodeRemovedEvent;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchService;
import org.uberfire.mvp.Command;
import org.uberfire.security.authz.AuthorizationResult;
import org.uberfire.security.authz.Permission;
import org.uberfire.security.client.authz.tree.LoadCallback;
import org.uberfire.security.client.authz.tree.LoadOptions;
import org.uberfire.security.client.authz.tree.PermissionNode;
import org.uberfire.security.client.authz.tree.PermissionTreeProvider;
import org.uberfire.security.client.authz.tree.impl.PermissionGroupNode;
import org.uberfire.security.client.authz.tree.impl.PermissionLeafNode;
import org.uberfire.security.client.authz.tree.impl.PermissionResourceNode;
import org.uberfire.security.impl.authz.DotNamedPermission;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MultiplePermissionNodeEditorTest {

    @Mock
    MultiplePermissionNodeEditor.View view;

    @Mock
    LeafPermissionNodeEditor.View childView1;

    @Mock
    LeafPermissionNodeEditor.View childView2;

    @Mock
    PermissionSwitch.View permissionSwitchReadView;

    @Mock
    PermissionSwitch.View permissionSwitchUpdateView;

    @Mock
    PermissionExceptionSwitch.View permissionSwitchReadView1;

    @Mock
    PermissionExceptionSwitch.View permissionSwitchUpdateView1;

    @Mock
    PermissionExceptionSwitch.View permissionSwitchReadView2;

    @Mock
    PermissionExceptionSwitch.View permissionSwitchUpdateView2;

    @Mock
    LiveSearchDropDown liveSearchDropDown;

    @Mock
    PermissionWidgetFactory widgetFactory;

    @Mock
    PermissionWidgetFactory widgetFactory1;

    @Mock
    PermissionWidgetFactory widgetFactory2;

    @Mock
    Event<PermissionChangedEvent> changedEvent;

    @Mock
    Event<PermissionNodeAddedEvent> nodeAddedEvent;

    @Mock
    Event<PermissionNodeRemovedEvent> nodeRemovedEvent;

    @Mock
    Command onChange;

    PermissionTreeProvider permissionTreeProvider;
    PermissionGroupNode permissionGroupNode;
    PermissionResourceNode permissionResourceNode;
    MultiplePermissionNodeEditor presenter;
    PermissionSwitch permissionSwitchRead;
    PermissionSwitch permissionSwitchUpdate;
    PermissionExceptionSwitch permissionSwitchRead1;
    PermissionExceptionSwitch permissionSwitchUpdate1;
    PermissionExceptionSwitch permissionSwitchRead2;
    PermissionExceptionSwitch permissionSwitchUpdate2;
    PermissionLeafNode permissionChildNode1;
    PermissionLeafNode permissionChildNode2;
    LeafPermissionNodeEditor childEditor1;
    LeafPermissionNodeEditor childEditor2;
    Permission permissionRead;
    Permission permissionUpdate;
    Permission permissionRead1;
    Permission permissionUpdate1;
    Permission permissionRead2;
    Permission permissionUpdate2;
    List<PermissionNode> permissionResourceChildrenAdded = new ArrayList<>();
    List<PermissionNode> permissionResourceChildrenAvailable = new ArrayList<>();

    class TestPermissionProvider implements PermissionTreeProvider {

        @Override
        public PermissionNode buildRootNode() {
            return null;
        }

        @Override
        public void loadChildren(PermissionNode parent, LoadOptions options, LoadCallback consumer) {
            consumer.afterLoad(permissionResourceChildrenAvailable);
        }
    }

    @Before
    public void setUp() {
        permissionTreeProvider = new TestPermissionProvider();

        permissionRead = spy(new DotNamedPermission("read",
                                                    true));
        permissionRead1 = spy(new DotNamedPermission("read.p1",
                                                     false));
        permissionRead2 = spy(new DotNamedPermission("read.p2",
                                                     false));
        permissionUpdate = spy(new DotNamedPermission("update",
                                                      true));
        permissionUpdate1 = spy(new DotNamedPermission("update.p1",
                                                       false));
        permissionUpdate2 = spy(new DotNamedPermission("update.p2",
                                                       false));

        permissionSwitchRead = spy(new PermissionSwitch(permissionSwitchReadView));
        permissionSwitchRead1 = spy(new PermissionExceptionSwitch(permissionSwitchReadView1));
        permissionSwitchRead2 = spy(new PermissionExceptionSwitch(permissionSwitchReadView2));
        permissionSwitchUpdate = spy(new PermissionSwitch(permissionSwitchUpdateView));
        permissionSwitchUpdate1 = spy(new PermissionExceptionSwitch(permissionSwitchUpdateView1));
        permissionSwitchUpdate2 = spy(new PermissionExceptionSwitch(permissionSwitchUpdateView2));

        permissionChildNode1 = spy(new PermissionLeafNode());
        permissionChildNode1.setNodeName("p1");
        permissionChildNode1.addPermission(permissionRead1,
                                           "read",
                                           "read");
        permissionChildNode1.addPermission(permissionUpdate1,
                                           "update",
                                           "update");
        permissionChildNode1.addDependencies(permissionRead1,
                                             permissionUpdate1);

        permissionChildNode2 = spy(new PermissionLeafNode());
        permissionChildNode2.setNodeName("p2");
        permissionChildNode2.addPermission(permissionRead2,
                                           "read",
                                           "read");
        permissionChildNode2.addPermission(permissionUpdate2,
                                           "update",
                                           "update");
        permissionChildNode2.addDependencies(permissionRead2,
                                             permissionUpdate2);

        permissionGroupNode = spy(new PermissionGroupNode(permissionTreeProvider));
        permissionGroupNode.setNodeName("group");
        permissionGroupNode.addPermission(permissionRead,
                                          "read",
                                          "read");
        permissionGroupNode.addPermission(permissionUpdate,
                                          "update",
                                          "update");
        permissionGroupNode.addDependencies(permissionRead,
                                            permissionUpdate);

        permissionResourceNode = spy(new PermissionResourceNode("resource",
                                                                permissionTreeProvider));
        permissionResourceNode.setNodeName("resource");
        permissionResourceNode.addPermission(permissionRead,
                                             "read",
                                             "read");
        permissionResourceNode.addPermission(permissionUpdate,
                                             "update",
                                             "update");
        permissionResourceNode.addDependencies(permissionRead,
                                               permissionUpdate);

        when(widgetFactory1.createExceptionSwitch()).thenReturn(permissionSwitchRead1,
                                                                permissionSwitchUpdate1);
        when(widgetFactory2.createExceptionSwitch()).thenReturn(permissionSwitchRead2,
                                                                permissionSwitchUpdate2);

        childEditor1 = spy(new LeafPermissionNodeEditor(childView1,
                                                        widgetFactory1,
                                                        changedEvent));
        childEditor2 = spy(new LeafPermissionNodeEditor(childView2,
                                                        widgetFactory2,
                                                        changedEvent));

        when(widgetFactory.createSwitch()).thenReturn(permissionSwitchRead,
                                                      permissionSwitchUpdate);
        when(widgetFactory.createEditor(permissionChildNode1)).thenReturn(childEditor1);
        when(widgetFactory.createEditor(permissionChildNode2)).thenReturn(childEditor2);

        presenter = new MultiplePermissionNodeEditor(view,
                                                     liveSearchDropDown,
                                                     widgetFactory,
                                                     changedEvent,
                                                     nodeAddedEvent,
                                                     nodeRemovedEvent);

        permissionResourceChildrenAvailable.add(permissionChildNode1);
        permissionResourceChildrenAvailable.add(permissionChildNode2);

        permissionResourceChildrenAdded.add(permissionChildNode1);
        permissionResourceChildrenAdded.add(permissionChildNode2);

        doAnswer(invocationOnMock -> {
            LoadCallback callback = (LoadCallback) invocationOnMock.getArguments()[0];
            callback.afterLoad(permissionResourceChildrenAdded);
            return null;
        }).when(permissionGroupNode).expand(any(LoadCallback.class));

        doAnswer(invocationOnMock -> {
            LoadCallback callback = (LoadCallback) invocationOnMock.getArguments()[0];
            callback.afterLoad(permissionResourceChildrenAdded);
            return null;
        }).when(permissionResourceNode).expand(any(LoadCallback.class));
    }

    @Test
    public void testInitGroupNode() {
        presenter.edit(permissionGroupNode);

        assertEquals(presenter.getChildEditors().size(),
                     2);
        assertEquals(presenter.getPermissionNode(),
                     permissionGroupNode);

        verify(view).setNodeName("group");
        verify(view,
               never()).setNodeFullName(anyString());
        verify(view).addPermission(permissionSwitchRead);
        verify(view).addPermission(permissionSwitchUpdate);
        verify(permissionSwitchRead).init(eq("read"),
                                          eq("read"),
                                          eq(true),
                                          eq(0));
        verify(permissionSwitchUpdate).init(eq("update"),
                                            eq("update"),
                                            eq(true),
                                            eq(0));

        verify(view).setClearChildrenEnabled(false);
        verify(view,
               never()).setAddChildEnabled(true);
        verify(view,
               never()).setChildSelector(any());
        verifyZeroInteractions(liveSearchDropDown);
    }

    @Test
    public void testInitResourceNode() {
        presenter.edit(permissionResourceNode);

        assertEquals(presenter.getChildEditors().size(),
                     2);
        assertEquals(presenter.getPermissionNode(),
                     permissionResourceNode);

        verify(view).setAddChildEnabled(true);
        verify(view).setChildSelector(liveSearchDropDown);
    }

    @Test
    public void testExpandGroupNode() {
        presenter.edit(permissionGroupNode);
        presenter.onNodeClick();

        verify(view).addChildEditor(childEditor1,
                                    false);
        verify(view).addChildEditor(childEditor2,
                                    false);
        verify(view).setAddChildEnabled(false);
        verify(view,
               never()).setAddChildEnabled(true);
        verify(view,
               never()).setChildSelector(any());
        verify(view,
               never()).setClearChildrenEnabled(true);
        verify(childEditor1).edit(permissionChildNode1);
        verify(childEditor2).edit(permissionChildNode2);
    }

    @Test
    public void testExpandResourceNode() {
        presenter.edit(permissionResourceNode);
        presenter.onNodeClick();

        verify(view).addChildEditor(childEditor1,
                                    true);
        verify(view).addChildEditor(childEditor2,
                                    true);
        verify(view).setAddChildEnabled(true);
        verify(view).setChildSelector(any());
        verify(view,
               atLeastOnce()).setClearChildrenEnabled(true);
        verify(childEditor1).edit(permissionChildNode1);
        verify(childEditor2).edit(permissionChildNode2);
    }

    @Test
    public void testSwitchChange() {
        presenter.edit(permissionGroupNode);

        permissionSwitchRead.onChange();

        verify(permissionRead).setResult(any());
        verify(changedEvent).fire(any());
    }

    @Test
    public void testSwitchInitDependencies() {
        when(permissionSwitchReadView.isOn()).thenReturn(false);
        when(permissionSwitchUpdateView.isOn()).thenReturn(true);

        presenter.edit(permissionGroupNode);

        verify(permissionUpdate).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);
    }

    @Test
    public void testSwitchChangeDependencies() {
        presenter.edit(permissionGroupNode);

        // Deny "read" permission
        reset(permissionUpdate);
        reset(permissionSwitchUpdate);
        when(permissionSwitchRead.isOn()).thenReturn(false);
        permissionSwitchRead.onChange();

        // "update" permission switched to denied as it depends on "read"
        verify(permissionUpdate).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);

        // Grant "read" permission
        reset(permissionSwitchUpdate);
        when(permissionSwitchRead.isOn()).thenReturn(true);
        permissionSwitchRead.onChange();

        // "update" permission enabled but not switched on
        verify(permissionSwitchUpdate).setEnabled(true);
        verify(permissionSwitchUpdate,
               never()).setOn(anyBoolean());
    }

    @Test
    public void testSwitchChildDependencies() {
        presenter.edit(permissionResourceNode);

        // Permissions are denied by default on children
        permissionSwitchReadView1.setExceptionEnabled(false);
        permissionSwitchUpdateView1.setExceptionEnabled(false);

        // Deny parent's read permission
        reset(permissionUpdate);
        reset(permissionSwitchRead);
        reset(permissionSwitchUpdate);
        when(permissionSwitchRead.isOn()).thenReturn(false);
        permissionSwitchRead.onChange();

        // Children exception flag hidden as parent has been denied
        permissionSwitchReadView1.setExceptionEnabled(false);
        permissionSwitchUpdateView1.setExceptionEnabled(false);

        // Parent's "update" permission switched to denied as it depends on "read"
        verify(permissionUpdate).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);
        verify(permissionSwitchUpdate).setNumberOfExceptions(0);
    }

    @Test
    public void testChildrenSwitchExceptions() {
        presenter.edit(permissionResourceNode);

        // Deny child read permission
        when(permissionSwitchReadView1.isOn()).thenReturn(true);
        permissionSwitchRead1.onChange();
        verify(permissionRead1).setResult(AuthorizationResult.ACCESS_GRANTED);
        verify(permissionSwitchReadView1).setExceptionEnabled(false);

        // Deny parent's update permission
        reset(permissionUpdate);
        reset(permissionSwitchRead);
        reset(permissionSwitchUpdate);
        reset(permissionSwitchUpdateView1);
        when(permissionSwitchUpdateView.isOn()).thenReturn(false);
        permissionSwitchRead.onChange();

        // Children update exception flag hidden as parent has been denied
        verify(permissionUpdate).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);
        verify(permissionSwitchUpdateView1,
               atLeastOnce()).setExceptionEnabled(false);

        // Deny parent's read permission
        reset(permissionUpdate);
        reset(permissionSwitchRead);
        reset(permissionSwitchUpdate);
        reset(permissionSwitchUpdateView1);
        when(permissionSwitchReadView.isOn()).thenReturn(false);
        permissionSwitchRead.onChange();

        // Parent's "update" permission switched to denied as it depends on "read"
        verify(permissionUpdate).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);
        verify(permissionSwitchUpdate).setNumberOfExceptions(0);

        // Children's "update" permission switched to denied as well
        verify(permissionUpdate1).setResult(AuthorizationResult.ACCESS_DENIED);
        verify(permissionSwitchUpdate).setEnabled(false);
        verify(permissionSwitchUpdate).setOn(false);
        verify(permissionSwitchUpdateView1,
               atLeastOnce()).setExceptionEnabled(false);
    }

    @Test
    public void testAddChildDropDownEmpty() {
        permissionResourceChildrenAdded.clear();
        permissionResourceChildrenAdded.add(permissionChildNode1);
        permissionResourceChildrenAdded.add(permissionChildNode2);
        presenter.edit(permissionResourceNode);

        LiveSearchService searchService = presenter.getChildrenSearchService();
        searchService.search("", -1, results -> {
            assertEquals(results.size(), 0);
        });
    }

    @Test
    public void testAddChildDropDownFull() {
        permissionResourceChildrenAdded.clear();
        presenter.edit(permissionResourceNode);

        LiveSearchService searchService = presenter.getChildrenSearchService();
        searchService.search("", -1, results -> {
            assertEquals(results.size(), 2);
        });
    }

    @Test
    public void testAddChildDropDownDuplicateNames() {
        permissionResourceChildrenAdded.clear();
        permissionResourceChildrenAdded.add(permissionChildNode2);
        PermissionLeafNode permissionChildNode3 = new PermissionLeafNode();
        permissionChildNode3.addPermission(new DotNamedPermission("read.p1b", true), "", "");
        permissionChildNode3.setNodeName("p1");
        permissionResourceChildrenAvailable.add(permissionChildNode3);
        presenter.edit(permissionResourceNode);

        LiveSearchService<String> searchService = presenter.getChildrenSearchService();
        searchService.search("", -1, results -> {
            assertEquals(results.size(), 2);
            assertEquals(results.get(0).getKey(), "read.p1");
            assertEquals(results.get(0).getValue(), "p1");
            assertEquals(results.get(1).getKey(), "read.p1b");
            assertEquals(results.get(1).getValue(), "p1");
        });
    }
}
