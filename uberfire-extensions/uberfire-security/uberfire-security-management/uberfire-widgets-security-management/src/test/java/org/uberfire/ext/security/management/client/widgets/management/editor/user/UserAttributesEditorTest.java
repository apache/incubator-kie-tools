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

package org.uberfire.ext.security.management.client.widgets.management.editor.user;

import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.security.management.api.Capability;
import org.uberfire.ext.security.management.api.UserManager;
import org.uberfire.ext.security.management.client.ClientUserSystemManager;
import org.uberfire.ext.security.management.client.widgets.management.events.CreateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.DeleteUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.OnErrorEvent;
import org.uberfire.ext.security.management.client.widgets.management.events.UpdateUserAttributeEvent;
import org.uberfire.ext.security.management.client.widgets.popup.ConfirmBox;
import org.uberfire.mocks.EventSourceMock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class UserAttributesEditorTest {

    @Mock ClientUserSystemManager userSystemManager;
    @Mock ConfirmBox confirmBox;
    @Mock EventSourceMock<CreateUserAttributeEvent> createUserAttributeEventEvent;
    @Mock EventSourceMock<UpdateUserAttributeEvent> updateUserAttributeEventEvent;
    @Mock EventSourceMock<DeleteUserAttributeEvent> deleteUserAttributeEventEvent;
    @Mock EventSourceMock<OnErrorEvent> errorEvent;
    @Mock NewUserAttributeEditor newUserAttributeEditor;
    @Mock UserAttributesEditor.View view;

    private UserAttributesEditor presenter;
    private UserManager.UserAttribute attribute;

    @Before
    public void setup() {
        when(newUserAttributeEditor.clear()).thenReturn(newUserAttributeEditor);
        when(userSystemManager.isUserCapabilityEnabled(any(Capability.class))).thenReturn(true);
        attribute = mock(UserManager.UserAttribute.class);
        when(attribute.getName()).thenReturn("attr1");
        when(attribute.getDefaultValue()).thenReturn("v1");
        when(attribute.isEditable()).thenReturn(true);
        when(attribute.isMandatory()).thenReturn(true);
        when(userSystemManager.getUserSupportedAttribute("attr1")).thenReturn(attribute);
        when(view.getColumnCount()).thenReturn(0);
        presenter = new UserAttributesEditor(userSystemManager, confirmBox, createUserAttributeEventEvent, updateUserAttributeEventEvent,
                deleteUserAttributeEventEvent, errorEvent, newUserAttributeEditor, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(2)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).redraw();
        verify(view, times(0)).removeColumn(anyInt());
        verify(view, times(0)).setCanCreate(anyBoolean());
        verify(view, times(0)).showEmpty();
    }

    @Test
    public void testClear() {
        presenter.attributes = new HashMap<UserManager.UserAttribute, String>();
        presenter.isEditMode = true;
        presenter.clear();
        assertNull(presenter.attributes);
        assertFalse(presenter.isEditMode);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).getColumnCount();
        verify(view, times(0)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(0)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).redraw();
        verify(view, times(0)).removeColumn(anyInt());
        verify(view, times(0)).setCanCreate(anyBoolean());
        verify(view, times(0)).showEmpty();
    }

    @Test
    public void testShow() {
        final User user = mock(User.class);
        Map<String, String> attrs = new HashMap<String, String>(1);
        attrs.put("attr1", "value1");
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getProperties()).thenReturn(attrs);
        presenter.isEditMode = true;
        presenter.show(user);
        assertFalse(presenter.isEditMode);
        assertTrue(presenter.attributes.size() == 1);
        verify(newUserAttributeEditor, times(1)).clear();
        verify(newUserAttributeEditor, times(1)).showAddButton();
        verify(view, times(1)).setCanCreate(false);
        verify(view, times(1)).redraw();
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(2)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).init(any(UserAttributesEditor.class));
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).removeColumn(anyInt());
    }

    @Test
    public void testEdit() {
        final User user = mock(User.class);
        Map<String, String> attrs = new HashMap<String, String>(1);
        attrs.put("attr1", "value1");
        when(user.getIdentifier()).thenReturn("user1");
        when(user.getProperties()).thenReturn(attrs);
        presenter.isEditMode = false;
        presenter.edit(user);
        assertTrue(presenter.isEditMode);
        assertTrue(presenter.attributes.size() == 1);
        verify(newUserAttributeEditor, times(1)).clear();
        verify(newUserAttributeEditor, times(1)).showAddButton();
        verify(view, times(1)).setCanCreate(true);
        verify(view, times(1)).redraw();
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(3)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).init(any(UserAttributesEditor.class));
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).removeColumn(anyInt());
    }

    @Test
    public void testGetValue() {
        UserManager.UserAttribute attribute1 = mock(UserManager.UserAttribute.class);
        when(attribute1.getName()).thenReturn("attr1");
        when(attribute1.getDefaultValue()).thenReturn("v1");
        when(attribute1.isEditable()).thenReturn(true);
        when(attribute1.isMandatory()).thenReturn(true);
        UserManager.UserAttribute attribute2 = mock(UserManager.UserAttribute.class);
        when(attribute2.getName()).thenReturn("attr2");
        when(attribute2.getDefaultValue()).thenReturn("v2");
        when(attribute2.isEditable()).thenReturn(true);
        when(attribute2.isMandatory()).thenReturn(true);
        Map<UserManager.UserAttribute, String> attrs = new HashMap<UserManager.UserAttribute, String>(1);
        attrs.put(attribute1, "value1");
        attrs.put(attribute2, "value2");
        presenter.attributes = attrs;
        Map<String, String> result = presenter.getValue();
        assertNotNull(result);
        assertTrue(result.size() == 2);
        assertEquals("value1", result.get("attr1"));
        assertEquals("value2", result.get("attr2"));
    }


    @Test
    public void testUpdateAttribute() {
        presenter.isEditMode = true;
        presenter.attributes = new HashMap<UserManager.UserAttribute, String>();
        presenter.attributes.put(attribute, "value1");
        presenter.updateUserAttribute(0, "attr1", "value2");
        verify(updateUserAttributeEventEvent, times(1)).fire(any(UpdateUserAttributeEvent.class));
        assertTrue(presenter.attributes.size() == 1);
        assertEquals("value2", presenter.attributes.get(attribute));
        verify(view, times(0)).setCanCreate(anyBoolean());
        verify(view, times(1)).redraw();
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(3)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).init(any(UserAttributesEditor.class));
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).removeColumn(anyInt());
    }

    @Test
    public void testRemoveAttribute() {
        presenter.isEditMode = true;
        presenter.attributes = new HashMap<UserManager.UserAttribute, String>();
        presenter.attributes.put(attribute, "value1");
        Map.Entry<String, String> entry = new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "attr1";
            }

            @Override
            public String getValue() {
                return "value1";
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
        presenter.removeUserAttribute(0, entry);
        verify(deleteUserAttributeEventEvent, times(1)).fire(any(DeleteUserAttributeEvent.class));
        assertTrue(presenter.attributes.isEmpty());
        verify(view, times(0)).setCanCreate(anyBoolean());
        verify(view, times(1)).redraw();
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(3)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).init(any(UserAttributesEditor.class));
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).removeColumn(anyInt());
    }


    @Test
    public void testOnAttributeCreated() {
        UserManager.UserAttribute attr2 = mock(UserManager.UserAttribute.class);
        when(attr2.getName()).thenReturn("attr2");
        when(attr2.getDefaultValue()).thenReturn("v2");
        when(attr2.isEditable()).thenReturn(true);
        when(attr2.isMandatory()).thenReturn(true);
        when(userSystemManager.getUserSupportedAttribute("attr2")).thenReturn(attr2);
        CreateUserAttributeEvent createUserAttributeEvent = mock(CreateUserAttributeEvent.class);
        Map.Entry<String, String> entry = new Map.Entry<String, String>() {
            @Override
            public String getKey() {
                return "attr2";
            }

            @Override
            public String getValue() {
                return "value2";
            }

            @Override
            public String setValue(String value) {
                return null;
            }
        };
        when(createUserAttributeEvent.getAttribute()).thenReturn(entry);
        presenter.attributes = new HashMap<UserManager.UserAttribute, String>();
        presenter.onAttributeCreated(createUserAttributeEvent);
        assertTrue(presenter.attributes.size() == 1);
        verify(view, times(0)).setCanCreate(anyBoolean());
        verify(view, times(1)).redraw();
        verify(view, times(1)).getColumnCount();
        verify(view, times(1)).setColumnSortHandler(any(ColumnSortEvent.ListHandler.class));
        verify(view, times(2)).addColumn(any(Column.class), anyString());
        verify(view, times(0)).init(any(UserAttributesEditor.class));
        verify(view, times(0)).initWidgets(any(NewUserAttributeEditor.View.class));
        verify(view, times(0)).removeColumn(anyInt());
    }

}
