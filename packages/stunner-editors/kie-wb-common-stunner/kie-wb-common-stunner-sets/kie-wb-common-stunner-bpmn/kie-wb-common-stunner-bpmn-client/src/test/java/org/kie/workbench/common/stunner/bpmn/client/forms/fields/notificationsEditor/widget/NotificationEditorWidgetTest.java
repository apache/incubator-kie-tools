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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;
import javax.validation.Validator;

import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.event.NotificationEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDownView;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType.NOT_COMPLETED_NOTIFY;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType.NOT_STARTED_NOTIFY;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.PERIOD;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(PowerMockRunner.class)
public class NotificationEditorWidgetTest extends ReflectionUtilsTest {

    private NotificationEditorWidget presenter;

    @GwtMock
    private NotificationWidgetViewImpl notificationWidgetViewImpl;

    @GwtMock
    private TextArea body;

    @GwtMock
    private TextBox subject;

    @GwtMock
    private Select typeSelect;

    private Option notCompleted;

    @GwtMock
    private NotificationEditorWidgetViewImpl view;

    private DataBinder<NotificationRow> customerBinder;

    @GwtMock
    private Event<NotificationEvent> notificationEvent;

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

    ParagraphElement incorrectEmail;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        BaseModal modal = mock(BaseModal.class);
        taskExpiration = mock(Select.class);
        setFieldValue(view, "taskExpiration", taskExpiration);

        Option notStarted = mock(Option.class);
        notCompleted = mock(Option.class);
        customerBinder = mock(DataBinder.class);
        searchSelectionReplyToHandler = mock(SingleLiveSearchSelectionHandler.class);
        searchSelectionFromHandler = mock(SingleLiveSearchSelectionHandler.class);
        multipleLiveSearchSelectionHandlerUsers = mock(MultipleLiveSearchSelectionHandler.class);
        multipleLiveSearchSelectionHandlerGroups = mock(MultipleLiveSearchSelectionHandler.class);
        notCompletedInput = mock(HTMLInputElement.class);
        notStartedInput = mock(HTMLInputElement.class);
        incorrectEmail = mock(ParagraphElement.class);

        doNothing().when(view).markEmailsAsCorrect();
        doNothing().when(modal).hide();
        doNothing().when(modal).show();

        doNothing().when(notificationEvent).fire(any(NotificationEvent.class));

        presenter = new NotificationEditorWidget(view, translationService);

        doCallRealMethod().when(typeSelect).setValue(any(String.class));
        doCallRealMethod().when(typeSelect).getValue();

        doCallRealMethod().when(view).setReadOnly(any(boolean.class));
        doNothing().when(view).createOrEdit(any(NotificationWidgetView.class), any(NotificationRow.class));
        doNothing().when(view).ok();

        setFieldValue(view, "modal", modal);
        setFieldValue(view, "body", body);
        setFieldValue(view, "customerBinder", customerBinder);
        setFieldValue(view, "notCompletedInput", notCompletedInput);
        setFieldValue(view, "notStartedInput", notStartedInput);
        setFieldValue(view, "subject", subject);
        setFieldValue(view, "emails", subject);
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
        setFieldValue(view, "incorrectEmail", incorrectEmail);

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
        presenter.setReadOnly(true);

        HTMLButtonElement closeButton = getFieldValue(NotificationEditorWidgetViewImpl.class,
                                                      view,
                                                      "closeButton");
        HTMLButtonElement okButton = getFieldValue(NotificationEditorWidgetViewImpl.class,
                                                   view,
                                                   "okButton");

        assertFalse(closeButton.disabled);
        assertTrue(okButton.disabled);
    }

    @Test
    public void testGetNameHeader() {
        assertEquals("Notification", presenter.getNameHeader());
    }

    @Test
    public void testOkWithIncorrectString() {
        String stringWithInvalidEmail = "invalid";
        presenter.ok(stringWithInvalidEmail);
        verify(view).setValidationFailed(stringWithInvalidEmail);
        verify(view, never()).ok();
    }

    @Test
    public void testOkWithCorrectString() {
        String stringWithValidEmail = "valid@email.com";
        presenter.ok(stringWithValidEmail);
        verify(view, never()).setValidationFailed(anyString());
        verify(view).ok();
    }

    @Test
    public void testOkWithEmptyValue() {
        presenter.ok("");
        verify(view, never()).setValidationFailed(anyString());
        verify(view).ok();
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
        when(notCompleted.getValue()).thenReturn(NOT_STARTED_NOTIFY.getAlias());
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
        Assert.assertNotEquals(NOT_STARTED_NOTIFY, test.getType());
        Assert.assertNotEquals(groups, test.getGroups());
        Assert.assertNotEquals(users, test.getUsers());
    }

    @Test
    public void testClearEmailsWithEmptyValue() {
        assertEquals("", presenter.clearEmails(""));
    }

    @Test
    public void testClearEmailsFromSpaces() {
        assertEquals("abcd", presenter.clearEmails(" a b  c   d  "));
    }

    @Test
    public void testClearEmailsFromCommas() {
        assertEquals("abc", presenter.clearEmails(",abc,"));
    }

    @Test
    public void testGetExpirationLabelDateTime() {
        presenter.getExpirationLabel(Expiration.DATETIME);
        verify(translationService).getValue("notification.expiration.datetime.label");
    }

    @Test
    public void testGetExpirationLabelExpression() {
        presenter.getExpirationLabel(Expiration.EXPRESSION);
        verify(translationService).getValue("notification.expiration.expression.label");
    }

    @Test
    public void testGetExpirationLabelTimePeriod() {
        presenter.getExpirationLabel(Expiration.TIME_PERIOD);
        verify(translationService).getValue("notification.expiration.time.period.label");
    }

    @Test(expected = NullPointerException.class)
    public void testGetExpirationLabelNull() {
        presenter.getExpirationLabel(null);
    }

    @Test
    public void testShowRepeatNotification() {
        presenter.setRepeatNotificationVisibility(true);
        verify(view).showRepeatNotificationDiv();
        verify(view, never()).hideRepeatNotificationDiv();
    }

    @Test
    public void testHideRepeatNotification() {
        presenter.setRepeatNotificationVisibility(false);
        verify(view).hideRepeatNotificationDiv();
        verify(view, never()).showRepeatNotificationDiv();
    }

    @Test
    public void testShowInvisibleRepeatNotification() {
        presenter.setRepeatNotificationInvisibility(false);
        verify(view).showRepeatNotificationDiv();
        verify(view, never()).hideRepeatNotificationDiv();
    }

    @Test
    public void testHideInvisibleRepeatNotification() {
        presenter.setRepeatNotificationInvisibility(true);
        verify(view).hideRepeatNotificationDiv();
        verify(view, never()).showRepeatNotificationDiv();
    }

    @Test
    public void testShowRepeatNotificationPanel() {
        presenter.setNotificationPanelDivVisibility(true);
        verify(view).showRepeatNotificationPanel();
        verify(view, never()).hideRepeatNotificationPanel();
    }

    @Test
    public void testHideRepeatNotificationPanel() {
        presenter.setNotificationPanelDivVisibility(false);
        verify(view).hideRepeatNotificationPanel();
        verify(view, never()).showRepeatNotificationPanel();
    }

    @Test
    public void testAddEmptyUsers() {
        presenter.addUsers(null);
        presenter.addUsers(new ArrayList<>());
        verify(view, never()).addUserToLiveSearch(anyString());
        verify(view, never()).addUsersToSelect(any());
    }

    @Test
    public void testAddUsers() {
        String user1 = "user1";
        String user2 = "user2";
        List<String> users = new ArrayList<>();
        users.add(user1);
        users.add(user2);

        presenter.addUsers(users);
        verify(view).addUsersToSelect(users);
        verify(view).addUserToLiveSearch(user1);
        verify(view).addUserToLiveSearch(user2);
    }

    @Test
    public void testAddEmptyGroups() {
        presenter.addGroups(null);
        presenter.addGroups(new ArrayList<>());
        verify(view, never()).addGroupToLiveSearch(anyString());
        verify(view, never()).addGroupsToSelect(any());
    }

    @Test
    public void testAddGroups() {
        String group1 = "group1";
        String group2 = "group2";
        List<String> users = new ArrayList<>();
        users.add(group1);
        users.add(group2);

        presenter.addGroups(users);
        verify(view).addGroupsToSelect(users);
        verify(view).addGroupToLiveSearch(group1);
        verify(view).addGroupToLiveSearch(group2);
    }

    @Test
    public void testAddFrom() {
        String from = "from";
        presenter.addFrom(from);
        verify(view).addFrom(from);
    }

    @Test
    public void testAddEmptyFrom() {
        String from = "";
        presenter.addFrom(null);
        presenter.addFrom(from);
        verify(view, never()).addFrom(from);
    }

    @Test
    public void testAddReplyTo() {
        String replyTo = "replyTo";
        presenter.addReplyTo(replyTo);
        verify(view).addReplyTo(replyTo);
    }

    @Test
    public void testAddEmptyReplyTo() {
        String replyTo = "";
        presenter.addReplyTo(null);
        presenter.addReplyTo(replyTo);
        verify(view, never()).addReplyTo(replyTo);
    }

    @Test
    public void testParseExpirationAtIsEmpty() {
        assertEquals(Expiration.TIME_PERIOD, presenter.parseExpiration(null, Expiration.DATETIME));
        assertEquals(Expiration.TIME_PERIOD, presenter.parseExpiration("", Expiration.DATETIME));
    }

    @Test
    public void testParseExpirationIsEmpty() {
        assertEquals(Expiration.DATETIME, presenter.parseExpiration("nonEmpty", Expiration.DATETIME));
    }

    @Test
    public void testParseExpiration() {
        assertEquals(Expiration.EXPRESSION, presenter.parseExpiration("R5", null));
    }

    @Test
    public void testSetExpirationExpression() {
        String expiresAt = "some value";
        NotificationRow row = new NotificationRow();
        row.setExpiresAt(expiresAt);

        presenter.setExpiration(Expiration.EXPRESSION, row);
        verify(view).setExpressionTextValue(expiresAt);
        verify(view, never()).setExpirationDateTime(any());
        verify(view, never()).setExpirationTimePeriod(any());
    }

    @Test
    public void testSetExpirationDateTime() {
        String expiresAt = "some value";
        NotificationRow row = new NotificationRow();
        row.setExpiresAt(expiresAt);

        presenter.setExpiration(Expiration.DATETIME, row);
        verify(view, never()).setExpressionTextValue(any());
        verify(view).setExpirationDateTime(row);
        verify(view, never()).setExpirationTimePeriod(any());
    }

    @Test
    public void testSetExpirationTimePeriod() {
        String expiresAt = "some value";
        NotificationRow row = new NotificationRow();
        row.setExpiresAt(expiresAt);

        presenter.setExpiration(Expiration.TIME_PERIOD, row);
        verify(view, never()).setExpressionTextValue(any());
        verify(view, never()).setExpirationDateTime(any());
        verify(view).setExpirationTimePeriod(expiresAt);
    }

    @Test
    public void testClearZeroTimeZone() {
        assertEquals("0", presenter.clearTimeZone("00Z"));
    }

    @Test
    public void testClearTimeZone() {
        assertEquals("-02Z", presenter.clearTimeZone("-02Z"));
    }

    @Test
    public void testSetEmptyExpirationDateTime() {
        presenter.setExpirationDateTime("");
        verify(view, never()).enableRepeatNotification(any(), any(), any(), any());
        verify(view, never()).disableRepeatNotification(any(), any());
    }

    @Test
    public void testDisableRepeatNotification() {
        String dateTime = "2020-07-23T16:36";
        String timeZone = "+02:00";
        presenter.setExpirationDateTime(dateTime + timeZone);
        verify(view, never()).enableRepeatNotification(any(), any(), any(), any());
        verify(view).disableRepeatNotification(presenter.parseDate(dateTime), timeZone);
    }

    @Test
    public void testEnableRepeatNotification() {
        String dateTime = "2020-07-23T16:36";
        String timeZone = "+02:00";
        String period = "P2D";
        String repeatCount = "R2";
        presenter.setExpirationDateTime(repeatCount + "/" + dateTime + timeZone + "/" + period);
        verify(view).enableRepeatNotification(presenter.parseDate(dateTime),
                                              timeZone,
                                              period,
                                              repeatCount);
        verify(view, never()).disableRepeatNotification(any(), any());
    }

    @Test
    public void testGetNotificationType() {
        assertEquals(NOT_STARTED_NOTIFY, presenter.getNotificationType(true));
        assertEquals(NOT_COMPLETED_NOTIFY, presenter.getNotificationType(false));
    }

    @Test
    public void testIsRepeatable() {
        assertTrue(presenter.isRepeatable("R/something"));
        assertFalse(presenter.isRepeatable("something"));
    }

    @Test
    public void testMinutesOrMonth() {
        String minutes = "PT4M";
        String month = "P11M";
        String repeatMonth = "R/P7M";
        String repeatMinutes = "R/PT9M";

        assertEquals("m", presenter.minuteOrMonth(getMatchFor(minutes)));
        assertEquals("m", presenter.minuteOrMonth(getMatchFor(repeatMinutes)));
        assertEquals("M", presenter.minuteOrMonth(getMatchFor(month)));
        assertEquals("M", presenter.minuteOrMonth(getMatchFor(repeatMonth)));
    }

    private MatchResult getMatchFor(String pattern) {
        return RegExp.compile(PERIOD).exec(pattern);
    }
}
