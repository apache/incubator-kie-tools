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

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.google.common.collect.ImmutableMap;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.ui.Composite;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.ToggleSwitch;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.ColorType;
import org.gwtbootstrap3.extras.toggleswitch.client.ui.base.constants.SizeType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.selector.input.MultipleSelectorInput;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.AssigneeLiveSearchService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.event.NotificationEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.PeriodBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.TimeZonePicker;
import org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;
import org.uberfire.ext.widgets.common.client.dropdown.MultipleLiveSearchSelectionHandler;
import org.uberfire.ext.widgets.common.client.dropdown.SingleLiveSearchSelectionHandler;

import static jsinterop.annotations.JsPackage.GLOBAL;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.ISO_DATE_TIME;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.PERIOD;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.validation.ExpirationTypeOracle.REPEATABLE;

@Dependent
@Templated("NotificationEditorWidgetViewImpl.html#container")
public class NotificationEditorWidgetViewImpl extends Composite implements NotificationEditorWidgetView {

    @DataField
    @Inject
    protected HTMLInputElement repeatCount;

    @Inject
    @DataField
    protected HTMLDivElement timeperiodDiv;

    @Inject
    @DataField
    protected HTMLDivElement expressionDiv;

    @Inject
    @DataField
    protected HTMLDivElement datetimeDiv;

    @Inject
    @DataField
    protected HTMLDivElement notifyEveryPanelDiv;

    @Inject
    @DataField
    protected HTMLDivElement repeatNotificationsDiv;

    @Inject
    @DataField
    protected HTMLDivElement repeatNotificationPanelDiv;

    @Inject
    @DataField
    protected HTMLDivElement errorDivPanel;

    @Inject
    @DataField
    protected ToggleSwitch repeatNotification;

    @Inject
    @DataField
    protected DateTimePicker dateTimePicker;

    @Inject
    @DataField
    protected TimeZonePicker timeZonePicker;

    @Inject
    @DataField
    protected HTMLInputElement taskStateChanges;

    @Inject
    @DataField
    protected HTMLInputElement repeatCountReaches;

    @Inject
    @DataField
    protected HTMLInputElement notStartedInput;

    @Inject
    @DataField
    protected HTMLInputElement notCompletedInput;

    @Inject
    @DataField
    protected HTMLAnchorElement notificationPopover;

    @Inject
    @DataField
    protected HTMLAnchorElement replyPopover;

    protected DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm");

    protected Presenter presenter;

    protected BaseModal modal = new BaseModal();

    protected NotificationRow current;

    @Inject
    @AutoBound
    protected DataBinder<NotificationRow> customerBinder;

    @Inject
    protected Event<NotificationEvent> notificationEvent;

    protected AssigneeLiveSearchService assigneeLiveSearchServiceFrom;

    protected AssigneeLiveSearchService assigneeLiveSearchServiceReplyTo;

    protected AssigneeLiveSearchService assigneeLiveSearchServiceUsers;

    protected AssigneeLiveSearchService assigneeLiveSearchServiceGroups;

    @DataField
    @Bound(property = "users")
    protected MultipleSelectorInput<String> multipleSelectorInputUsers;

    @DataField
    @Bound(property = "groups")
    protected MultipleSelectorInput<String> multipleSelectorInputGroups;

    protected Select typeSelect = new Select();

    protected Option notStarted = new Option();

    protected Option notCompleted = new Option();

    @DataField
    protected Select taskExpiration = new Select();

    @DataField
    protected LiveSearchDropDown<String> liveSearchFromDropDown;

    @DataField
    protected LiveSearchDropDown<String> liveSearchReplyToDropDown;

    @Inject
    @DataField
    protected PeriodBox periodBox;

    @Inject
    @DataField
    protected PeriodBox repeatBox;

    @Inject
    @DataField
    @Bound(property = "subject")
    protected TextBox subject;

    @Inject
    @DataField
    @Bound(property = "body")
    protected TextArea body;

    @Inject
    @DataField
    protected TextArea expressionTextArea;

    @Inject
    protected Validator validator;

    protected MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerUsers = new MultipleLiveSearchSelectionHandler();

    protected MultipleLiveSearchSelectionHandler<String> multipleLiveSearchSelectionHandlerGroups = new MultipleLiveSearchSelectionHandler();

    protected SingleLiveSearchSelectionHandler<String> searchSelectionFromHandler = new SingleLiveSearchSelectionHandler<>();

    protected SingleLiveSearchSelectionHandler<String> searchSelectionReplyToHandler = new SingleLiveSearchSelectionHandler<>();

    @DataField
    @Inject
    protected HTMLButtonElement closeButton, okButton;

    protected Map<String, HTMLDivElement> panels;

    @Inject
    public NotificationEditorWidgetViewImpl(final MultipleSelectorInput multipleSelectorInputUsers,
                                            final MultipleSelectorInput multipleSelectorInputGroups,
                                            final LiveSearchDropDown liveSearchFromDropDown,
                                            final LiveSearchDropDown liveSearchReplyToDropDown,

                                            final AssigneeLiveSearchService assigneeLiveSearchServiceFrom,
                                            final AssigneeLiveSearchService assigneeLiveSearchServiceReplyTo,
                                            final AssigneeLiveSearchService assigneeLiveSearchServiceUsers,
                                            final AssigneeLiveSearchService assigneeLiveSearchServiceGroups) {
        initUsersAndGroupsDropdowns(multipleSelectorInputUsers,
                                    multipleSelectorInputGroups,
                                    liveSearchFromDropDown,
                                    liveSearchReplyToDropDown,
                                    assigneeLiveSearchServiceFrom,
                                    assigneeLiveSearchServiceReplyTo,
                                    assigneeLiveSearchServiceUsers,
                                    assigneeLiveSearchServiceGroups);
        initTypeSelector();
    }

    private void initPopover() {
        PopOver.$(notificationPopover).popovers();
        PopOver.$(replyPopover).popovers();
    }

    protected void initTextBoxes() {
        subject.setMaxLength(100);
        periodBox.showLabel(false);
        body.setMaxLength(500);
        body.setHeight("70px");

        dateTimePicker.setValue(new Date());
        repeatBox.showLabel(false);

        taskStateChanges.addEventListener("change", event -> onTaskStateChanges());
        repeatCountReaches.addEventListener("change", event -> onTaskStateChanges());

        repeatNotification.setValue(false, true);
        repeatNotification.setSize(SizeType.MINI);
        repeatNotification.setOnColor(ColorType.INFO);
        repeatNotification.setOnColor(ColorType.PRIMARY);
        repeatNotification.setOnText("Yes");
        repeatNotification.setOffText("No");
        repeatNotification.addValueChangeHandler(event -> onRepeatNotification(event.getValue()));
    }

    protected void initUsersAndGroupsDropdowns(MultipleSelectorInput multipleSelectorInputUsers,
                                               MultipleSelectorInput multipleSelectorInputGroups,
                                               LiveSearchDropDown<String> liveSearchFromDropDown,
                                               LiveSearchDropDown<String> liveSearchReplyToDropDown,

                                               AssigneeLiveSearchService liveSearchServiceFrom,
                                               AssigneeLiveSearchService liveSearchServiceReplyTo,
                                               AssigneeLiveSearchService liveSearchServiceUsers,
                                               AssigneeLiveSearchService liveSearchServiceGroups) {
        this.assigneeLiveSearchServiceFrom = liveSearchServiceFrom;
        this.assigneeLiveSearchServiceReplyTo = liveSearchServiceReplyTo;
        this.assigneeLiveSearchServiceUsers = liveSearchServiceUsers;
        this.assigneeLiveSearchServiceGroups = liveSearchServiceGroups;

        this.multipleSelectorInputUsers = multipleSelectorInputUsers;
        this.multipleSelectorInputGroups = multipleSelectorInputGroups;
        this.liveSearchFromDropDown = liveSearchFromDropDown;
        this.liveSearchReplyToDropDown = liveSearchReplyToDropDown;

        this.assigneeLiveSearchServiceFrom.init(AssigneeType.USER);
        this.assigneeLiveSearchServiceReplyTo.init(AssigneeType.USER);
        this.assigneeLiveSearchServiceUsers.init(AssigneeType.USER);
        this.assigneeLiveSearchServiceGroups.init(AssigneeType.GROUP);

        this.multipleSelectorInputUsers.init(assigneeLiveSearchServiceUsers, multipleLiveSearchSelectionHandlerUsers);
        this.multipleSelectorInputGroups.init(assigneeLiveSearchServiceGroups, multipleLiveSearchSelectionHandlerGroups);
        this.liveSearchFromDropDown.init(assigneeLiveSearchServiceFrom, searchSelectionFromHandler);
        this.liveSearchReplyToDropDown.init(assigneeLiveSearchServiceReplyTo, searchSelectionReplyToHandler);
    }

    void initTaskExpirationSelector() {
        for (Expiration value : Expiration.values()) {
            Option option = new Option();
            option.setText(presenter.getExpirationLabel(value.toString()));
            option.setValue(value.getName());
            taskExpiration.add(option);
        }
    }

    void initTypeSelector() {
        notStarted.setValue(NotificationType.NotStartedNotify.getAlias());
        notStarted.setText(NotificationType.NotStartedNotify.getType());
        notCompleted.setText(NotificationType.NotCompletedNotify.getType());
        notCompleted.setValue(NotificationType.NotCompletedNotify.getAlias());

        typeSelect.add(notStarted);
        typeSelect.add(notCompleted);
    }

    protected void onTaskExpressionChange(ValueChangeEvent<String> event) {
        panels.values().forEach(div -> div.style.display = Style.Display.NONE.getCssName());
        panels.get(event.getValue()).style.display = Style.Display.BLOCK.getCssName();
        if (event.getValue().equals(Expiration.EXPRESSION.getName())) {
            repeatNotificationsDiv.style.display = Style.Display.NONE.getCssName();
        } else {
            repeatNotificationsDiv.style.display = Style.Display.BLOCK.getCssName();
        }
        checkNotifyEveryPanelDivVisible();
    }

    protected void onTaskStateChanges() {
        repeatCount.disabled = taskStateChanges.checked;
    }

    protected void onRepeatNotification(Boolean show) {
        if (show) {
            repeatNotificationPanelDiv.style.display = Style.Display.BLOCK.getCssName();
        } else {
            repeatNotificationPanelDiv.style.display = Style.Display.NONE.getCssName();
            repeatCount.value = "1";
        }
    }

    protected void checkNotifyEveryPanelDivVisible() {
        notifyEveryPanelDiv.style.display = taskExpiration.getValue().equals(Expiration.DATETIME.getName())
                ? Style.Display.BLOCK.getCssName()
                : Style.Display.NONE.getCssName();
    }

    @PostConstruct
    public void init() {
        closeButton.addEventListener("click", event -> close(), false);
        okButton.addEventListener("click", event -> ok(), false);
        taskExpiration.addValueChangeHandler(event -> onTaskExpressionChange(event));
        repeatCount.value = "1";

        panels = ImmutableMap.of(Expiration.TIMEPERIOD.getName(), timeperiodDiv,
                                 Expiration.EXPRESSION.getName(), expressionDiv,
                                 Expiration.DATETIME.getName(), datetimeDiv);
        initPopover();
    }

    @Override
    public void init(final Presenter presenter) {
        this.presenter = presenter;

        initModel();
        initTextBoxes();
        initTaskExpirationSelector();
    }

    protected void initModel() {
        modal.setTitle(presenter.getNameHeader());
        modal.setWidth("535px");
        modal.setBody(this);
        modal.setClosable(false);
        modal.addDomHandler(getEscDomHandler(), KeyDownEvent.getType());

        this.liveSearchFromDropDown.setOnChange(() -> customerBinder.getWorkingModel().setFrom(searchSelectionFromHandler.getSelectedValue()));
        this.liveSearchReplyToDropDown.setOnChange(() -> customerBinder.getWorkingModel().setReplyTo(searchSelectionReplyToHandler.getSelectedValue()));
    }

    protected KeyDownHandler getEscDomHandler() {
        return event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                close();
            }
        };
    }

    @Override
    public void createOrEdit(NotificationWidgetView parent, NotificationRow row) {
        current = row;
        customerBinder.setModel(row.clone());
        if (row.getUsers() != null && row.getUsers().size() > 0) {
            row.getUsers().forEach(u -> assigneeLiveSearchServiceUsers.addCustomEntry(u));
            multipleSelectorInputUsers.setValue(row.getUsers());
        }
        if (row.getGroups() != null && row.getGroups().size() > 0) {
            row.getGroups().forEach(u -> assigneeLiveSearchServiceGroups.addCustomEntry(u));
            multipleSelectorInputGroups.setValue(row.getGroups());
        }

        if (row.getFrom() != null && !row.getFrom().isEmpty()) {
            assigneeLiveSearchServiceFrom.addCustomEntry(row.getFrom());
            liveSearchFromDropDown.setSelectedItem(row.getFrom());
        }

        if (row.getReplyTo() != null && !row.getReplyTo().isEmpty()) {
            assigneeLiveSearchServiceReplyTo.addCustomEntry(row.getReplyTo());
            liveSearchReplyToDropDown.setSelectedItem(row.getReplyTo());
        }

        if (row.getType() != null) {
            if (row.getType().equals(NotificationType.NotCompletedNotify)) {
                notCompletedInput.checked = true;
                notStartedInput.checked = false;
            } else if (row.getType().equals(NotificationType.NotStartedNotify)) {
                notCompletedInput.checked = false;
                notStartedInput.checked = true;
            }
        }
        setExpiration(row);
        modal.show();
    }

    protected void setExpiration(NotificationRow row) {
        Expiration expiration;
        if (row.getExpiresAt().isEmpty()) {
            expiration = Expiration.TIMEPERIOD;
        } else {
            expiration = row.getExpiration() != null ? row.getExpiration()
                    : new ExpirationTypeOracle().guess(row.getExpiresAt());
        }
        taskExpiration.setValue(expiration.getName(), true);
        if (expiration.equals(Expiration.EXPRESSION)) {
            expressionTextArea.setValue(row.getExpiresAt());
        } else if (expiration.equals(Expiration.DATETIME)) {
            setExpirationDATETIME(row);
        } else if (expiration.equals(Expiration.TIMEPERIOD)) {
            setExpirationTimeperiod(row.getExpiresAt());
        }
    }

    protected void setExpirationDATETIME(NotificationRow row) {
        MatchResult result = RegExp.compile(REPEATABLE + "/" + ISO_DATE_TIME + "/" + PERIOD).exec(row.getExpiresAt());
        if (result != null) {
            repeatNotification.setValue(true, true);
            Date DATETIME = dateTimeFormat.parse(result.getGroup(2));
            String tz = result.getGroup(3);
            setPeriod(row.getExpiresAt().split("/")[2], repeatBox);
            dateTimePicker.setValue(DATETIME);
            setTimeZonePickerValue(tz.equals("00Z") ? "0" : tz);
            setTaskStateOrRepeatCountValue(getRepeatCount(row.getExpiresAt().split("/")[0]));
        } else {
            result = RegExp.compile(ISO_DATE_TIME).exec(row.getExpiresAt());
            if (result != null) {
                repeatNotification.setValue(false);
                Date DATETIME = dateTimeFormat.parse(result.getGroup(1));
                String tz = result.getGroup(2);
                dateTimePicker.setValue(DATETIME);
                setTimeZonePickerValue(tz);
            }
        }
    }

    protected void setTimeZonePickerValue(String value) {
        timeZonePicker.setValue(value.equals("00Z") ? "0" : value);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        okButton.disabled = readOnly;
    }

    protected String combineISO8601String() {
        if (taskExpiration.getValue().equals(Expiration.EXPRESSION.getName())) {
            return expressionTextArea.getValue();
        } else {
            ISO8601Builder builder = ISO8601Builder.get();
            builder.setRepeatable(repeatNotification.getValue())
                    .setType(taskExpiration.getValue())
                    .setRepeat(repeatBox.getValue())
                    .setUntil(repeatCountReaches.checked)
                    .setDate(dateTimePicker.getValue())
                    .setTz(timeZonePicker.getValue())
                    .setRepeatCount(Integer.parseInt(repeatCount.value))
                    .setPeriod(periodBox.getValue());
            return builder.build();
        }
    }

    protected void setExpirationTimeperiod(String iso) {
        MatchResult result = RegExp.compile(REPEATABLE + "/" + PERIOD).exec(iso);
        if (result != null) {
            if (isRepeatable(iso.split("/")[0])) {
                repeatNotification.setValue(true, true);
                setPeriod(iso.split("/")[1], periodBox);
                setTaskStateOrRepeatCountValue(getRepeatCount(iso.split("/")[0]));
            }
        } else {
            result = RegExp.compile(PERIOD).exec(iso);
            if (result != null) {
                setPeriod(result.getGroup(0), periodBox);
                repeatNotification.setValue(false, true);
            }
        }
    }

    protected void setTaskStateOrRepeatCountValue(String count) {
        if (count == null || count.isEmpty() || count.equals("0")) {
            taskStateOrRepeatCount(true);
        } else {
            taskStateOrRepeatCount(false);
            repeatCount.value = count;
        }
    }

    protected void setPeriod(String period, PeriodBox box) {
        MatchResult match = RegExp.compile(PERIOD).exec(period);
        String duration = match.getGroup(2);
        StringBuffer result = new StringBuffer();
        result.append(duration);
        result.append(minuteOrMonth(match));
        box.setValue(result.toString());
    }

    protected String minuteOrMonth(MatchResult match) {
        String _T = match.getGroup(1);
        return ((match.getGroup(3).equals("M") && !_T.isEmpty()) ? "m" : match.getGroup(3));
    }

    protected boolean isRepeatable(String repeatable) {
        MatchResult matcher = RegExp.compile(REPEATABLE).exec(repeatable);
        if (matcher == null) {
            return false;
        }
        return true;
    }

    protected String getRepeatCount(String repeatable) {
        MatchResult matcher = RegExp.compile(REPEATABLE).exec(repeatable);
        return matcher.getGroup(1);
    }

    void ok() {
        // TODO looks like errai data binder doesn't support liststore widgets.
        current.setUsers(multipleLiveSearchSelectionHandlerUsers.getSelectedValues());
        current.setGroups(multipleLiveSearchSelectionHandlerGroups.getSelectedValues());
        current.setBody(body.getValue());
        current.setSubject(subject.getValue());
        current.setFrom(searchSelectionFromHandler.getSelectedValue() != null ? searchSelectionFromHandler.getSelectedValue() : "");
        current.setReplyTo(searchSelectionReplyToHandler.getSelectedValue() != null ? searchSelectionReplyToHandler.getSelectedValue() : "");
        current.setExpiresAt(combineISO8601String());
        current.setExpiration(Expiration.get(taskExpiration.getValue()));
        current.setType(notStartedInput.checked ? NotificationType.NotStartedNotify : NotificationType.NotCompletedNotify);
        notificationEvent.fire(new NotificationEvent(current));
        hide();
    }

    void close() {
        notificationEvent.fire(new NotificationEvent(null));
        hide();
    }

    protected void onViolationError(Set<ConstraintViolation<NotificationRow>> violations) {
        expressionTextArea.getElement().getStyle().setBorderColor("red");
        errorDivPanel.innerHTML = violations.stream().map(v -> "* " + v.getMessage()).collect(Collectors.joining("\n"));
    }

    void hide() {
        //clear widgets and set default values
        multipleSelectorInputUsers.setValue(Collections.EMPTY_LIST);
        multipleSelectorInputGroups.setValue(Collections.EMPTY_LIST);
        periodBox.clear();
        subject.clear();
        body.clear();
        liveSearchFromDropDown.clearSelection();
        liveSearchReplyToDropDown.clearSelection();
        notStartedInput.checked = true;
        repeatBox.clear();
        repeatNotification.setValue(false, true);
        taskExpiration.setValue(Expiration.TIMEPERIOD.getName(), true);
        repeatBox.setValue("1");
        errorDivPanel.innerHTML = "";
        expressionTextArea.getElement().getStyle().setBorderColor("");

        dateTimePicker.setValue(new Date());
        timeZonePicker.setValue("0");

        onRepeatNotification(false);

        taskStateOrRepeatCount(true);
        modal.hide();
    }

    protected void taskStateOrRepeatCount(boolean state) {
        if (state) {
            taskStateChanges.checked = true;
            repeatCountReaches.checked = false;
            repeatCount.disabled = true;
        } else {
            taskStateChanges.checked = false;
            repeatCountReaches.checked = true;
            repeatCount.disabled = false;
            repeatCount.value = "1";
        }
    }

    static class ISO8601Builder {

        protected boolean repeatable, until;

        protected String type;

        protected String period, repeat, tz;

        protected int repeatCount;

        protected Date date;

        protected ISO8601Builder() {

        }

        static ISO8601Builder get() {
            return new ISO8601Builder();
        }

        public ISO8601Builder setRepeatable(boolean repeatable) {
            this.repeatable = repeatable;
            return this;
        }

        public ISO8601Builder setUntil(boolean until) {
            this.until = until;
            return this;
        }

        public ISO8601Builder setType(String type) {
            this.type = type;
            return this;
        }

        public ISO8601Builder setPeriod(String period) {
            this.period = period;
            return this;
        }

        public ISO8601Builder setRepeat(String repeat) {
            this.repeat = repeat;
            return this;
        }

        public ISO8601Builder setRepeatCount(int repeatCount) {
            this.repeatCount = repeatCount;
            return this;
        }

        public ISO8601Builder setDate(Date date) {
            this.date = date;
            return this;
        }

        public ISO8601Builder setTz(String tz) {
            this.tz = tz;
            return this;
        }

        public String build() {
            StringBuffer sb = new StringBuffer();

            if (repeatable) {
                sb.append("R");
                if (until) {
                    sb.append(repeatCount);
                }
                sb.append("/");
            }

            if (type.equals(Expiration.TIMEPERIOD.getName())) {
                // Note: "P1M" is a one-month duration and "PT1M" is a one-minute duration;
                sb.append((period.contains("M") || period.contains("Y") || period.contains("D")) ? "P" : "PT");
                sb.append(period.toUpperCase());
            } else {
                DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm");

                String strFormat = dateTimeFormat.format(date);
                sb.append(strFormat);
                sb.append(tz.equals("0") ? ":00Z" : tz);
                if (repeatable) {
                    if (type.equals(Expiration.DATETIME.getName())) {
                        sb.append("/");
                    }
                    // Note: "P1M" is a one-month duration and "PT1M" is a one-minute duration;
                    sb.append((repeat.contains("M") || repeat.contains("Y") || repeat.contains("D")) ? "P" : "PT");
                    sb.append(repeat.toUpperCase());
                }
            }
            return sb.toString();
        }

        protected int tzToOffset(String tz) {
            if (tz.contains(":")) {
                int hours = Integer.parseInt(tz.split(":")[0]);
                int minutes = Integer.parseInt(tz.split(":")[1]);
                return hours * 60 + ((hours < 0) ? -minutes : minutes);
            }
            return Integer.parseInt(tz) * 60;
        }
    }

    @JsType(isNative = true)
    private static abstract class PopOver {

        @JsMethod(namespace = GLOBAL, name = "jQuery")
        public native static PopOver $(final elemental2.dom.Node selector);

        public native void popovers();
    }
}