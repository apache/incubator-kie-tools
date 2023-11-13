/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;
import javax.validation.Validator;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.event.ReassignmentEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReassignmentEditorWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private ReassignmentEditorWidget reassignmentEditorWidget;

    @GwtMock
    private ReassignmentWidgetViewImpl reassignmentWidgetViewImpl;

    private BaseModal modal;

    @GwtMock
    private ClientTranslationService translationService;

    @GwtMock
    private Select typeSelect;

    private Option notStarted;

    private Option notCompleted;

    @GwtMock
    private ReassignmentEditorWidgetViewImpl view;

    private DataBinder<ReassignmentRow> customerBinder;

    private MultipleSelectorInput<String> multipleSelectorInputUsers;

    private MultipleSelectorInput<String> multipleSelectorInputGroups;

    @GwtMock
    private Event<ReassignmentEvent> reassignmentEvent;

    @GwtMock
    private LiveSearchDropDownView liveSearchDropDownView;

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerUsers;

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerGroups;

    @GwtMock
    private Validator validator;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        modal = mock(BaseModal.class);
        notStarted = mock(Option.class);
        notCompleted = mock(Option.class);
        customerBinder = mock(DataBinder.class);

        multipleLiveSearchSelectionHandlerUsers = mock(MultipleLiveSearchSelectionHandler.class);
        multipleLiveSearchSelectionHandlerGroups = mock(MultipleLiveSearchSelectionHandler.class);

        doNothing().when(modal).hide();
        doNothing().when(modal).show();

        doNothing().when(reassignmentEvent).fire(any(ReassignmentEvent.class));

        doCallRealMethod().when(reassignmentEditorWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(reassignmentEditorWidget).getNameHeader();
        setFieldValue(reassignmentEditorWidget, "view", view);
        setFieldValue(reassignmentEditorWidget, "translationService", translationService);

        doCallRealMethod().when(typeSelect).setValue(any(String.class));
        doCallRealMethod().when(typeSelect).getValue();

        doCallRealMethod().when(view).setReadOnly(any(boolean.class));
        doCallRealMethod().when(view).initTypeSelector();
        doCallRealMethod().when(view).createOrEdit(any(ReassignmentWidgetView.class), any(ReassignmentRow.class));
        doCallRealMethod().when(view).ok();

        setFieldValue(view, "modal", modal);
        setFieldValue(view, "customerBinder", customerBinder);
        setFieldValue(view, "multipleLiveSearchSelectionHandlerUsers", multipleLiveSearchSelectionHandlerUsers);
        setFieldValue(view, "multipleLiveSearchSelectionHandlerGroups", multipleLiveSearchSelectionHandlerGroups);
        setFieldValue(view, "reassignmentEvent", reassignmentEvent);
        setFieldValue(view, "validator", validator);
        setFieldValue(view, "closeButton", new HTMLButtonElement());
        setFieldValue(view, "okButton", new HTMLButtonElement());
        setFieldValue(view, "customerBinder", customerBinder);
        setFieldValue(view, "typeSelect", typeSelect);
        setFieldValue(view, "notStarted", notStarted);
        setFieldValue(view, "notCompleted", notCompleted);

        doCallRealMethod().when(typeSelect).setValue(any(String.class));
        doCallRealMethod().when(typeSelect).getValue();

        when(validator.validate(any(ReassignmentRow.class))).thenReturn(Collections.EMPTY_SET);

        doCallRealMethod().when(view).init(any(ReassignmentEditorWidgetView.Presenter.class));

        when(translationService.getValue(any(String.class))).thenReturn("Reassignment");

        view.initTypeSelector();
    }

    @Test
    public void testReadOnly() {
        reassignmentEditorWidget.setReadOnly(true);

        HTMLButtonElement closeButton = getFieldValue(ReassignmentEditorWidgetViewImpl.class,
                                                      view,
                                                      "closeButton");
        HTMLButtonElement okButton = getFieldValue(ReassignmentEditorWidgetViewImpl.class,
                                                     view,
                                                     "okButton");

        Assert.assertFalse(closeButton.disabled);
        Assert.assertTrue(okButton.disabled);
    }

    @Test
    public void testGetNameHeader() {
        Assert.assertEquals(reassignmentEditorWidget.getNameHeader(), "Reassignment");
    }

    @Test
    public void testCreateAndSaveEmpty() {
        ReassignmentRow test = new ReassignmentRow();
        doNothing().when(view).hide();

        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(ReassignmentType.NotCompletedReassign.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(Collections.EMPTY_LIST);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(Collections.EMPTY_LIST);
        view.createOrEdit(reassignmentWidgetViewImpl, test);
        view.ok();

        ReassignmentRow result = getFieldValue(ReassignmentEditorWidgetViewImpl.class, view, "current");
        Assert.assertEquals(result, test);
    }

    @Test
    public void testCreateAndSave() {
        List<String> groups = Arrays.asList("AAA", "BBB", "CCC", "DDD");
        List<String> users = Arrays.asList("aaa", "bbb", "ccc");

        doNothing().when(view).hide();

        ReassignmentRow test = new ReassignmentRow();
        doNothing().when(view).hide();

        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(ReassignmentType.NotCompletedReassign.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(groups);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(users);
        view.createOrEdit(reassignmentWidgetViewImpl, test);
        view.ok();

        Assert.assertEquals(ReassignmentType.NotCompletedReassign, test.getType());
        Assert.assertEquals(groups, test.getGroups());
        Assert.assertEquals(users, test.getUsers());
    }

    @Test
    public void testCreateAndClose() {
        List<String> groups = Arrays.asList("AAA", "BBB", "CCC", "DDD");
        List<String> users = Arrays.asList("aaa", "bbb", "ccc");

        doNothing().when(view).hide();

        ReassignmentRow test = new ReassignmentRow();
        doNothing().when(view).hide();

        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(ReassignmentType.NotStartedReassign.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(groups);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(users);
        view.createOrEdit(reassignmentWidgetViewImpl, test);
        view.close();

        Assert.assertNotEquals(ReassignmentType.NotStartedReassign, test.getType());
        Assert.assertNotEquals(groups, test.getGroups());
        Assert.assertNotEquals(users, test.getUsers());
    }
}
