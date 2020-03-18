/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;
import javax.validation.Validator;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.event.NotificationEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class NotificationEditorWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private NotificationEditorWidget notificationEditorWidget;

    @GwtMock
    private NotificationWidgetViewImpl notificationWidgetViewImpl;

    private BaseModal modal;

    @GwtMock
    private TextArea body;

    @GwtMock
    private TextBox subject;

    @GwtMock
    private Select typeSelect;

    private Option notStarted;

    private Option notCompleted;

    @GwtMock
    private NotificationEditorWidgetViewImpl view;

    private DataBinder<NotificationRow> customerBinder;

    private MultipleSelectorInput<String> multipleSelectorInputUsers;

    @GwtMock
    private Event<NotificationEvent> notificationEvent;

    private MultipleSelectorInput<String> multipleSelectorInputGroups;

    private LiveSearchDropDown<String> liveSearchFromDropDown;

    @GwtMock
    private LiveSearchDropDown<String> liveSearchReplyToDropDown;

    @GwtMock
    private LiveSearchDropDownView liveSearchDropDownView;

    @GwtMock
    private ClientTranslationService translationService;

    private SingleLiveSearchSelectionHandler<String> searchSelectionReplyToHandler;

    private SingleLiveSearchSelectionHandler<String> searchSelectionFromHandler;

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerUsers;

    private MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerGroups;

    @GwtMock
    private Validator validator;

    private Select taskExpiration;

    HTMLInputElement notCompletedInput;

    HTMLInputElement notStartedInput;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        modal = mock(BaseModal.class);
        taskExpiration = mock(Select.class);
        setFieldValue(view, "taskExpiration", taskExpiration);

        notStarted = mock(Option.class);
        notCompleted = mock(Option.class);
        customerBinder = mock(DataBinder.class);
        searchSelectionReplyToHandler = mock(SingleLiveSearchSelectionHandler.class);
        searchSelectionFromHandler = mock(SingleLiveSearchSelectionHandler.class);
        multipleLiveSearchSelectionHandlerUsers = mock(MultipleLiveSearchSelectionHandler.class);
        multipleLiveSearchSelectionHandlerGroups = mock(MultipleLiveSearchSelectionHandler.class);
        notCompletedInput = mock(HTMLInputElement.class);
        notStartedInput = mock(HTMLInputElement.class);

        doNothing().when(modal).hide();
        doNothing().when(modal).show();

        doNothing().when(notificationEvent).fire(any(NotificationEvent.class));

        doCallRealMethod().when(notificationEditorWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(notificationEditorWidget).getNameHeader();
        setFieldValue(notificationEditorWidget, "view", view);
        setFieldValue(notificationEditorWidget, "translationService", translationService);

        doCallRealMethod().when(typeSelect).setValue(any(String.class));
        doCallRealMethod().when(typeSelect).getValue();

        doCallRealMethod().when(view).setReadOnly(any(boolean.class));
        doCallRealMethod().when(view).createOrEdit(any(NotificationWidgetView.class), any(NotificationRow.class));
        doCallRealMethod().when(view).ok();

        setFieldValue(view, "modal", modal);
        setFieldValue(view, "body", body);
        setFieldValue(view, "customerBinder", customerBinder);
        setFieldValue(view, "notCompletedInput", notCompletedInput);
        setFieldValue(view, "notStartedInput", notStartedInput);
        setFieldValue(view, "subject", subject);
        setFieldValue(view, "searchSelectionFromHandler", searchSelectionFromHandler);
        setFieldValue(view, "searchSelectionReplyToHandler", searchSelectionReplyToHandler);
        setFieldValue(view, "multipleLiveSearchSelectionHandlerUsers", multipleLiveSearchSelectionHandlerUsers);
        setFieldValue(view, "multipleLiveSearchSelectionHandlerGroups", multipleLiveSearchSelectionHandlerGroups);
        setFieldValue(view, "notificationEvent", notificationEvent);
        setFieldValue(view, "validator", validator);
        setFieldValue(view, "closeButton", new HTMLButtonElement());
        setFieldValue(view, "okButton", new HTMLButtonElement());
        setFieldValue(view, "customerBinder", customerBinder);
        setFieldValue(view, "typeSelect", typeSelect);
        setFieldValue(view, "notStarted", notStarted);
        setFieldValue(view, "notCompleted", notCompleted);

        doCallRealMethod().when(body).setValue(any(String.class));
        doCallRealMethod().when(body).getValue();

        doCallRealMethod().when(subject).setValue(any(String.class));
        doCallRealMethod().when(subject).getValue();

        doCallRealMethod().when(typeSelect).setValue(any(String.class));
        doCallRealMethod().when(typeSelect).getValue();

        setFieldValue(liveSearchReplyToDropDown, "view", liveSearchDropDownView);

        when(validator.validate(any(NotificationRow.class))).thenReturn(Collections.EMPTY_SET);

        doNothing().when(liveSearchReplyToDropDown).setSelectedItem(any(String.class));
        doCallRealMethod().when(view).init(any(NotificationEditorWidgetView.Presenter.class));

        when(translationService.getValue(any(String.class))).thenReturn("Notification");

    }

    @Test
    public void testReadOnly() {
        notificationEditorWidget.setReadOnly(true);

        HTMLButtonElement closeButton = getFieldValue(NotificationEditorWidgetViewImpl.class,
                                                      view,
                                                      "closeButton");
        HTMLButtonElement okButton = getFieldValue(NotificationEditorWidgetViewImpl.class,
                                                     view,
                                                     "okButton");

        Assert.assertFalse(closeButton.disabled);
        Assert.assertTrue(okButton.disabled);
    }

    @Test
    public void testGetNameHeader() {
        Assert.assertEquals(notificationEditorWidget.getNameHeader(), "Notification");
    }

    @Test
    public void testCreateAndSaveEmpty() {
        NotificationRow test = new NotificationRow();
        doNothing().when(view).hide();
        when(taskExpiration.getValue()).thenReturn(Expiration.EXPRESSION.getName());


        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(NotificationType.NotCompletedNotify.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(searchSelectionReplyToHandler.getSelectedValue()).thenReturn("");
        when(searchSelectionFromHandler.getSelectedValue()).thenReturn("");
        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(Collections.EMPTY_LIST);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(Collections.EMPTY_LIST);
        view.createOrEdit(notificationWidgetViewImpl, test);
        view.ok();

        NotificationRow result = getFieldValue(NotificationEditorWidgetViewImpl.class, view, "current");
        Assert.assertEquals(result, test);
    }

    @Test
    public void testCreateAndSave() {
        when(taskExpiration.getValue()).thenReturn(Expiration.EXPRESSION.getName());

        List<String> groups = Arrays.asList("AAA", "BBB", "CCC", "DDD");
        List<String> users = Arrays.asList("aaa", "bbb", "ccc");

        doNothing().when(view).hide();
        when(subject.getValue()).thenReturn("QWERTY!");
        when(body.getValue()).thenReturn("QWERTY!");

        NotificationRow test = new NotificationRow();
        doNothing().when(view).hide();

        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(NotificationType.NotCompletedNotify.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(searchSelectionReplyToHandler.getSelectedValue()).thenReturn("admin");
        when(searchSelectionFromHandler.getSelectedValue()).thenReturn("admin");

        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(groups);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(users);
        view.createOrEdit(notificationWidgetViewImpl, test);
        view.ok();

        Assert.assertEquals("QWERTY!", test.getSubject());
        Assert.assertEquals("QWERTY!", test.getBody());
        Assert.assertEquals("admin", test.getReplyTo());
        Assert.assertEquals("admin", test.getFrom());
        Assert.assertEquals(NotificationType.NotCompletedNotify, test.getType());
        Assert.assertEquals(groups, test.getGroups());
        Assert.assertEquals(users, test.getUsers());
    }

    @Test
    public void testCreateAndClose() {
        List<String> groups = Arrays.asList("AAA", "BBB", "CCC", "DDD");
        List<String> users = Arrays.asList("aaa", "bbb", "ccc");

        doNothing().when(view).hide();
        when(subject.getValue()).thenReturn("QWERTY!");
        when(body.getValue()).thenReturn("QWERTY!");

        NotificationRow test = new NotificationRow();
        doNothing().when(view).hide();

        when(customerBinder.getModel()).thenReturn(test);
        when(notCompleted.getValue()).thenReturn(NotificationType.NotStartedNotify.getAlias());
        when(typeSelect.getSelectedItem()).thenReturn(notCompleted);

        when(searchSelectionReplyToHandler.getSelectedValue()).thenReturn("admin");
        when(searchSelectionFromHandler.getSelectedValue()).thenReturn("admin");

        when(multipleLiveSearchSelectionHandlerGroups.getSelectedValues()).thenReturn(groups);
        when(multipleLiveSearchSelectionHandlerUsers.getSelectedValues()).thenReturn(users);
        view.createOrEdit(notificationWidgetViewImpl, test);
        view.close();

        Assert.assertNotEquals("QWERTY!", test.getSubject());
        Assert.assertNotEquals("QWERTY!", test.getBody());
        Assert.assertNotEquals("admin", test.getReplyTo());
        Assert.assertNotEquals("admin", test.getFrom());
        Assert.assertNotEquals(NotificationType.NotStartedNotify, test.getType());
        Assert.assertNotEquals(groups, test.getGroups());
        Assert.assertNotEquals(users, test.getUsers());
    }
}
