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
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import io.crysknife.client.IsElement;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.DateTimePicker;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.constants.DateTimePickerPosition;
import org.gwtproject.i18n.client.DateTimeFormat;
import org.gwtproject.user.client.Event;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;
import org.uberfire.commons.Pair;

@Templated
@Dependent
public class TimerSettingsFieldEditorView
        implements IsElement,
                   TimerSettingsFieldEditorPresenter.View {

    static final String EMPTY_VALUE = "";

    static final String TimeDuration_Placeholder = "TimerSettingsFieldEditorView.TimeDuration_Placeholder";

    static final String DurationTimer_Help_Header = "TimerSettingsFieldEditorView.DurationTimer_Help_Header";

    static final String DurationTimer_Help_Line_1 = "TimerSettingsFieldEditorView.DurationTimer_Help_Line_1";

    static final String TimeCycle_Placeholder = "TimerSettingsFieldEditorView.TimeCycle_Placeholder";

    static final String TimeDate_Placeholder = "TimerSettingsFieldEditorView.TimeDate_Placeholder";

    static final String TimeDateTimePicker_Placeholder = "TimerSettingsFieldEditorView.TimeDateTimePicker_Placeholder";

    static final String MultipleTimer_Help_Header = "TimerSettingsFieldEditorView.MultipleTimer_Help_Header";

    static final String MultipleTimer_Help_Line1 = "TimerSettingsFieldEditorView.MultipleTimer_Help_Line1";

    static final String MultipleTimer_Help_Line2 = "TimerSettingsFieldEditorView.MultipleTimer_Help_Line2";

    static final String MultipleTimer_Help_Line3 = "TimerSettingsFieldEditorView.MultipleTimer_Help_Line3";

    static final String DateTimer_Help_Header = "TimerSettingsFieldEditorView.DateTimer_Help_Header";

    static final String DateTimer_Help_Line1 = "TimerSettingsFieldEditorView.DateTimer_Help_Line1";

    static final String Expression_Help_Line = "TimerSettingsFieldEditorView.Expression_Help_Line";

    static final String PLACEHOLDER_ATTR = "placeholder";

    static final String DATA_CONTENT_ATTR = "data-content";

    static final DateTimeFormat isoDateTimeFormat = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ssZZZ");

    @Inject
    @DataField("duration-timer")
    private HTMLInputElement durationTimer;

    @Inject
    @DataField("duration-timer-help")
    private HTMLAnchorElement durationTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> durationTimerHelpPopover;

    @Inject
    @DataField("duration-timer-params")
    private HTMLDivElement durationTimerParamsContainer;

    @Inject
    @DataField("time-duration")
    private HTMLInputElement timeDuration;

    @Inject
    @DataField("multiple-timer")
    private HTMLInputElement multipleTimer;

    @Inject
    @DataField("multiple-timer-help")
    private HTMLAnchorElement multipleTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> multipleTimerHelpPopover;

    @Inject
    @DataField("multiple-timer-params")
    private HTMLDivElement multipleTimerParamsContainer;

    @Inject
    @DataField("time-cycle-language")
    private HTMLSelectElement timeCycleLanguage;

    @Inject
    @DataField("time-cycle")
    private HTMLInputElement timeCycle;

    @Inject
    @DataField("date-timer")
    private HTMLInputElement dateTimer;

    @Inject
    @DataField("date-timer-help")
    private HTMLAnchorElement dateTimerHelp;

    @Inject
    private JQueryProducer.JQuery<Popover> dateTimerHelpPopover;

    @Inject
    @DataField("date-timer-params")
    private HTMLDivElement dateTimerParamsContainer;

    @Inject
    @DataField("time-date")
    private HTMLInputElement timeDate;

    @DataField("time-date-time-picker")
    private DateTimePicker timeDateTimePicker = new DateTimePicker();

    @Inject
    @DataField("time-date-time-picker-button")
    private HTMLButtonElement pickerButton;

    @Inject
    private ClientTranslationService translationService;

    private Supplier<HTMLOptionElement> optionSupplier = () -> (HTMLOptionElement) DomGlobal.document.createElement("option");

    private TimerSettingsFieldEditorPresenter presenter;

    @Override
    public void init(TimerSettingsFieldEditorPresenter presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void init() {
        timeCycle.type = "text";
        timeDuration.type = "text";
        timeDate.type = "text";
        dateTimer.type = "radio";
        multipleTimer.type = "radio";
        durationTimer.type = "radio";



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
        timeDateTimePicker.addValueChangeHandler(event -> presenter.onTimeDateTimePickerChange());
        timeDateTimePicker.addHideHandler(hideEvent -> presenter.onTimeDateTimePickerHidden());
        timeDateTimePicker.setPosition(DateTimePickerPosition.BOTTOM_RIGHT);
        timeDateTimePicker.reload();
    }

    @Override
    public void setTimeDuration(String timeDuration) {
        this.timeDuration.value = (timeDuration);
    }

    @Override
    public String getTimeDuration() {
        return timeDuration.value;
    }

    @Override
    public void setTimeDate(String timeDate) {
        this.timeDate.value = (timeDate);
    }

    @Override
    public String getTimeDate() {
        return timeDate.value;
    }

    @Override
    public Date getTimeDateTimePickerValue() {
        return timeDateTimePicker.getValue();
    }

    @Override
    public void setTimeCycle(String timeCycle) {
        this.timeCycle.value = (timeCycle);
    }

    @Override
    public String getTimeCycle() {
        return timeCycle.value;
    }

    @Override
    public void setTimeCycleLanguage(String timeCycleLanguage) {
        this.timeCycleLanguage.value = (timeCycleLanguage);
    }

    @Override
    public String getTimeCycleLanguage() {
        return timeCycleLanguage.value;
    }

    @Override
    public void setTimeCycleLanguageOptions(List<Pair<String, String>> options,
                                            String selectedValue) {
        options.forEach(option ->
                                timeCycleLanguage.add(newOption(option.getK1(),
                                                                option.getK2())));
        timeCycleLanguage.value = (selectedValue);
    }

    @Override
    public void setMultipleTimerChecked(boolean value) {
        multipleTimer.checked = (value);
    }

    @Override
    public void setDurationTimerChecked(boolean value) {
        durationTimer.checked = (value);
    }

    @Override
    public void setDateTimerChecked(boolean value) {
        dateTimer.checked = (value);
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

    @Override
    public void setReadOnly(final boolean readOnly) {
        durationTimer.disabled = (readOnly);
        timeDuration.disabled = (readOnly);
        multipleTimer.disabled = (readOnly);
        timeCycleLanguage.disabled = (readOnly);
        timeCycle.disabled = (readOnly);
        dateTimer.disabled = (readOnly);
        timeDate.disabled = (readOnly);
        pickerButton.disabled = (readOnly);
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
                                 translationService.getValue(MultipleTimer_Help_Line3),
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
            element.style.removeProperty("display");
        } else {
            element.style.setProperty("display",
                                           "none");
        }
    }

    private HTMLOptionElement newOption(final String text,
                                        final String value) {
        final HTMLOptionElement option = newOption();
        option.textContent = (text);
        option.value = (value);
        return option;
    }

    private HTMLOptionElement newOption() {
        return optionSupplier.get();
    }

    /**
     * For testing purposes
     */
    void setOptionSupplier(Supplier<HTMLOptionElement> optionSupplier) {
        this.optionSupplier = optionSupplier;
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
    void onMultipleTimerChange(@ForEvent("change") final Event event) {
        presenter.onMultipleTimerSelected();
    }

    @EventHandler("duration-timer")
    void onDurationTimerChange(@ForEvent("change") final Event event) {
        presenter.onDurationTimerSelected();
    }

    @EventHandler("date-timer")
    void onDateTimerChange(@ForEvent("change") final Event event) {
        presenter.onDateTimerSelected();
    }

    @EventHandler("time-duration")
    void onTimeDurationChange(@ForEvent("change") final Event event) {
        presenter.onTimerDurationChange();
    }

    @EventHandler("time-cycle")
    void onTimeCycleChange(@ForEvent("change") final Event event) {
        presenter.onTimeCycleChange();
    }

    @EventHandler("time-cycle-language")
    void onTimeCycleLanguageChange(@ForEvent("change") final Event event) {
        presenter.onTimeCycleLanguageChange();
    }

    @EventHandler("time-date")
    void onTimeDateChange(@ForEvent("change") final Event event) {
        presenter.onTimeDateChange();
    }

    @EventHandler("time-date-time-picker-button")
    void onShowDateTimePicker(@ForEvent("click") final Event event) {
        presenter.onShowTimeDateTimePicker();
    }
}
