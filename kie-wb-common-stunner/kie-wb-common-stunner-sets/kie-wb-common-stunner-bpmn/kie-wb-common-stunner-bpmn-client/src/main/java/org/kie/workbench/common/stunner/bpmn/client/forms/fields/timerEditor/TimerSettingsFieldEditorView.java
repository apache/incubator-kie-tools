/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.timerEditor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.i18n.client.DateTimeFormat;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerPosition;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.common.client.dom.Window;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.commons.data.Pair;

@Templated
public class TimerSettingsFieldEditorView
        implements IsElement,
                   TimerSettingsFieldEditorPresenter.View {

    public static final String EMPTY_VALUE = "";

    private static final String TimeDuration_Placeholder = "TimerSettingsFieldEditorView.TimeDuration_Placeholder";

    private static final String DurationTimer_Help_Header = "TimerSettingsFieldEditorView.DurationTimer_Help_Header";

    private static final String DurationTimer_Help_Line_1 = "TimerSettingsFieldEditorView.DurationTimer_Help_Line_1";

    private static final String TimeCycle_Placeholder = "TimerSettingsFieldEditorView.TimeCycle_Placeholder";

    private static final String TimeDate_Placeholder = "TimerSettingsFieldEditorView.TimeDate_Placeholder";

    private static final String TimeDateTimePicker_Placeholder = "TimerSettingsFieldEditorView.TimeDateTimePicker_Placeholder";

    private static final String MultipleTimer_Help_Header = "TimerSettingsFieldEditorView.MultipleTimer_Help_Header";

    private static final String MultipleTimer_Help_Line1 = "TimerSettingsFieldEditorView.MultipleTimer_Help_Line1";

    private static final String MultipleTimer_Help_Line2 = "TimerSettingsFieldEditorView.MultipleTimer_Help_Line2";

    private static final String DateTimer_Help_Header = "TimerSettingsFieldEditorView.DateTimer_Help_Header";

    public static final String DateTimer_Help_Line1 = "TimerSettingsFieldEditorView.DateTimer_Help_Line1";

    private static final String Expression_Help_Line = "TimerSettingsFieldEditorView.Expression_Help_Line";

    private static final String PLACEHOLDER_ATTR = "placeholder";

    private static final String DATA_CONTENT_ATTR = "data-content";

    private static final String DATE_TIME_PICKER_FORMAT = "yyyy-mm-dd hh:ii:ss";

    private DateTimeFormat isoDateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

    @Inject
    @DataField("duration-timer")
    private RadioInput durationTimer;

    @Inject
    @DataField("duration-timer-help")
    private Anchor durationTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> durationTimerHelpPopover;

    @Inject
    @DataField("duration-timer-params")
    private Div durationTimerParamsContainer;

    @Inject
    @DataField("time-duration")
    private TextInput timeDuration;

    @Inject
    @DataField("multiple-timer")
    private RadioInput multipleTimer;

    @Inject
    @DataField("multiple-timer-help")
    private Anchor multipleTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> multipleTimerHelpPopover;

    @Inject
    @DataField("multiple-timer-params")
    private Div multipleTimerParamsContainer;

    @Inject
    @DataField("time-cycle-language")
    private Select timeCycleLanguage;

    @Inject
    @DataField("time-cycle")
    private TextInput timeCycle;

    @Inject
    @DataField("date-timer")
    private RadioInput dateTimer;

    @Inject
    @DataField("date-timer-help")
    private Anchor dateTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> dateTimerHelpPopover;

    @Inject
    @DataField("date-timer-params")
    private Div dateTimerParamsContainer;

    @Inject
    @DataField("time-date")
    private TextInput timeDate;

    @DataField("time-date-time-picker")
    private DateTimePicker timeDateTimePicker = new DateTimePicker();

    @Inject
    @DataField("time-date-time-picker-button")
    private Button pickerButton;

    @Inject
    private ClientTranslationService translationService;

    private TimerSettingsFieldEditorPresenter presenter;

    @Override
    public void init(TimerSettingsFieldEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        timeDuration.setAttribute(PLACEHOLDER_ATTR,
                                  translationService.getValue(TimeDuration_Placeholder));
        timeCycle.setAttribute(PLACEHOLDER_ATTR,
                               translationService.getValue(TimeCycle_Placeholder));
        timeDate.setAttribute(PLACEHOLDER_ATTR,
                              translationService.getValue(TimeDate_Placeholder));

        durationTimerHelp.setAttribute(DATA_CONTENT_ATTR,
                                       getDurationTimerHtmlHelpText());
        durationTimerHelpPopover.wrap(durationTimerHelp).popover();

        multipleTimerHelp.setAttribute(DATA_CONTENT_ATTR,
                                       getMultipleTimerHtmlHelpText());
        multipleTimerHelpPopover.wrap(multipleTimerHelp).popover();

        dateTimerHelp.setAttribute(DATA_CONTENT_ATTR,
                                   getDateTimerHtmlHelpText());
        dateTimerHelpPopover.wrap(dateTimerHelp).popover();

        timeDateTimePicker.setPlaceholder(translationService.getValue(TimeDateTimePicker_Placeholder));
        timeDateTimePicker.setAutoClose(true);
        timeDateTimePicker.setHighlightToday(true);
        timeDateTimePicker.setShowTodayButton(true);
        timeDateTimePicker.setForceParse(false);
        timeDateTimePicker.setFormat(DATE_TIME_PICKER_FORMAT);
        timeDateTimePicker.addValueChangeHandler(event -> presenter.onTimeDateTimePickerChange());
        timeDateTimePicker.addHideHandler(hideEvent -> presenter.onTimeDateTimePickerHidden());
        timeDateTimePicker.setPosition(DateTimePickerPosition.BOTTOM_RIGHT);
        timeDateTimePicker.reload();
    }

    @Override
    public void setTimeDuration(String timeDuration) {
        this.timeDuration.setValue(timeDuration);
    }

    @Override
    public String getTimeDuration() {
        return timeDuration.getValue();
    }

    @Override
    public void setTimeDate(String timeDate) {
        this.timeDate.setValue(timeDate);
    }

    @Override
    public String getTimeDate() {
        return timeDate.getValue();
    }

    @Override
    public Date getTimeDateTimePickerValue() {
        return timeDateTimePicker.getValue();
    }

    @Override
    public void setTimeCycle(String timeCycle) {
        this.timeCycle.setValue(timeCycle);
    }

    @Override
    public String getTimeCycle() {
        return timeCycle.getValue();
    }

    @Override
    public void setTimeCycleLanguage(String timeCycleLanguage) {
        this.timeCycleLanguage.setValue(timeCycleLanguage);
    }

    @Override
    public String getTimeCycleLanguage() {
        return timeCycleLanguage.getValue();
    }

    @Override
    public void setTimeCycleLanguageOptions(List<Pair<String, String>> options,
                                            String selectedValue) {
        options.forEach(option ->
                                timeCycleLanguage.add(newOption(option.getK1(),
                                                                option.getK2())));
        timeCycleLanguage.setValue(selectedValue);
    }

    @Override
    public void setMultipleTimerChecked(boolean value) {
        multipleTimer.setChecked(value);
    }

    @Override
    public void setDurationTimerChecked(boolean value) {
        durationTimer.setChecked(value);
    }

    @Override
    public void setDateTimerChecked(boolean value) {
        dateTimer.setChecked(value);
    }

    @Override
    public void showMultipleTimerParams(boolean show) {
        showElement(multipleTimerParamsContainer,
                    show);
    }

    @Override
    public void showDurationTimerParams(boolean show) {
        showElement(durationTimerParamsContainer,
                    show);
    }

    @Override
    public void showDateTimerParams(boolean show) {
        showElement(dateTimerParamsContainer,
                    show);
    }

    @Override
    public void showTimeDate(boolean show) {
        showElement(timeDate,
                    show);
    }

    @Override
    public void showTimeDateTimePicker(boolean show) {
        timeDateTimePicker.setVisible(show);
        if (show) {
            timeDateTimePicker.show();
        }
    }

    @Override
    public void setTimeDateTimePickerValue(String value) {
        timeDateTimePicker.getTextBox().setValue(value);
    }

    @Override
    public void setTimeDateTimePickerValue(Date value) {
        timeDateTimePicker.setValue(value);
    }

    @Override
    public void clear() {
        setTimeDuration(EMPTY_VALUE);
        setTimeCycle(EMPTY_VALUE);
        setTimeDate(EMPTY_VALUE);
    }

    @Override
    public Date parseFromISO(final String value) throws IllegalArgumentException {
        return isoDateTimeFormat.parse(value);
    }

    @Override
    public String formatToISO(final Date value) {
        return isoDateTimeFormat.format(value);
    }

    private String getDurationTimerHtmlHelpText() {
        return buildHtmlHelpText(translationService.getValue(DurationTimer_Help_Header),
                                 translationService.getValue(DurationTimer_Help_Line_1),
                                 translationService.getValue(Expression_Help_Line));
    }

    private String getMultipleTimerHtmlHelpText() {
        return buildHtmlHelpText(translationService.getValue(MultipleTimer_Help_Header),
                                 translationService.getValue(MultipleTimer_Help_Line1),
                                 translationService.getValue(MultipleTimer_Help_Line2),
                                 translationService.getValue(Expression_Help_Line));
    }

    private String getDateTimerHtmlHelpText() {
        return buildHtmlHelpText(translationService.getValue(DateTimer_Help_Header),
                                 translationService.getValue(DateTimer_Help_Line1),
                                 translationService.getValue(Expression_Help_Line));
    }

    private void showElement(HTMLElement element,
                             boolean show) {
        if (show) {
            element.getStyle().removeProperty("display");
        } else {
            element.getStyle().setProperty("display",
                                           "none");
        }
    }

    private Option newOption(final String text,
                             final String value) {
        final Option option = (Option) Window.getDocument().createElement("option");
        option.setTextContent(text);
        option.setValue(value);
        return option;
    }

    private String buildHtmlHelpText(String header,
                                     String... lines) {
        StringBuilder html = new StringBuilder();
        html.append(header);
        html.append(":");
        if (lines.length > 0) {
            html.append("<UL>");
            Arrays.stream(lines).forEach(line -> {
                html.append("<LI>");
                html.append(line);
                html.append("</LI>");
            });
            html.append("</UL>");
        }
        return html.toString();
    }

    @EventHandler("multiple-timer")
    private void onMultipleTimerChange(@ForEvent("change") final Event event) {
        presenter.onMultipleTimerSelected();
    }

    @EventHandler("duration-timer")
    private void onDurationTimerChange(@ForEvent("change") final Event event) {
        presenter.onDurationTimerSelected();
    }

    @EventHandler("date-timer")
    private void onDateTimerSelected(@ForEvent("change") final Event event) {
        presenter.onDateTimerSelected();
    }

    @EventHandler("time-duration")
    private void onTimeDurationChange(@ForEvent("change") final Event event) {
        presenter.onTimerDurationChange();
    }

    @EventHandler("time-cycle")
    private void onTimeCycleChange(@ForEvent("change") final Event event) {
        presenter.onTimeCycleChange();
    }

    @EventHandler("time-cycle-language")
    private void onTimeCycleLanguageChange(@ForEvent("change") final Event event) {
        presenter.onTimeCycleLanguageChange();
    }

    @EventHandler("time-date")
    private void onTimeDateChange(@ForEvent("change") final Event event) {
        presenter.onTimeDateChange();
    }

    @EventHandler("time-date-time-picker-button")
    private void onShowDateTimePicker(@ForEvent("click") final Event event) {
        presenter.onShowTimeDateTimePicker();
    }
}
