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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLOptionElement;
import elemental2.dom.HTMLSelectElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.editors.types.DMNSimpleTimeZone;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimePicker;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValue;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeValueFormatter;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.picker.TimeZoneProvider;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.uberfire.client.views.pfly.selectpicker.JQuerySelectPickerEvent;

import static org.uberfire.client.views.pfly.selectpicker.JQuerySelectPicker.$;

@Templated
@Dependent
public class TimeSelectorView implements TimeSelector.View {

    @DataField("toggle-timezone-button")
    private final HTMLButtonElement toggleTimeZoneButton;

    @DataField("toggle-timezone-icon")
    private final HTMLElement toggleTimeZoneIcon;

    @DataField("time-input")
    private HTMLInputElement timeInput;

    @DataField("time-zone-selector")
    private final HTMLSelectElement timeZoneSelector;

    @DataField("time-zone-select-option")
    private final HTMLOptionElement typeSelectOption;

    static final String NONE_TRANSLATION_KEY = "TimeSelectorView.None";
    static final String TIMEZONE_CLASS_ICON = "fa-globe";
    static final String OFFSET_CLASS_ICON = "fa-clock-o";
    static final String NONE_VALUE = "None";

    private final TimeZoneProvider timeZoneProvider;
    private final ClientTranslationService translationService;
    private final List<DMNSimpleTimeZone> timeZones;
    private final TimePicker picker;
    private final TimeValueFormatter formatter;
    private TimeSelectorView presenter;
    private Consumer<BlurEvent> onValueInputBlur;
    private boolean isOffsetMode;
    private String timeZoneSelectedValue;

    @Inject
    public TimeSelectorView(final HTMLInputElement timeInput,
                            final TimePicker picker,
                            final TimeZoneProvider timeZoneProvider,
                            final TimeValueFormatter formatter,
                            final @Named("i") HTMLElement toggleTimeZoneIcon,
                            final HTMLButtonElement toggleTimeZoneButton,
                            final ClientTranslationService translationService,
                            final HTMLSelectElement timeZoneSelector,
                            final HTMLOptionElement typeSelectOption) {
        this.timeInput = timeInput;
        this.picker = picker;
        this.timeZoneProvider = timeZoneProvider;
        this.formatter = formatter;
        this.toggleTimeZoneIcon = toggleTimeZoneIcon;
        this.toggleTimeZoneButton = toggleTimeZoneButton;
        this.timeZones = new ArrayList<>();
        this.translationService = translationService;
        this.typeSelectOption = typeSelectOption;

        this.isOffsetMode = false;
        this.timeZoneSelector = timeZoneSelector;
        this.timeZoneSelector.setAttribute("data-container", "body");
    }

    void timeZoneSelectorRefresh() {
        triggerPickerAction(getSelectPicker(), "refresh");
        showSelectPicker();
    }

    void showSelectPicker() {
        triggerPickerAction(getSelectPicker(), "show");
    }

    Element getSelectPicker() {
        return this.timeZoneSelector;
    }

    void triggerPickerAction(final Element element,
                             final String method) {
        $(element).selectpicker(method);
    }

    String getTimeZoneSelectedValue() {
        return timeZoneSelectedValue;
    }

    @PostConstruct
    void init() {
        picker.bind(timeInput);
        timeZoneProvider.getTimeZones(this::timeZoneProviderSuccessCallBack);
    }

    private void timeZoneProviderSuccessCallBack(final List<DMNSimpleTimeZone> timeZones) {

        this.timeZones.clear();
        this.timeZones.addAll(timeZones);
        setupOnChangeHandler(getSelectPicker());
        populateTimeZoneSelectorWithIds();
    }

    boolean getIsOffsetMode() {
        return isOffsetMode;
    }

    List<DMNSimpleTimeZone> getTimeZones() {
        return timeZones;
    }

    void populateTimeZoneSelectorWithIds() {

        RemoveHelper.removeChildren(timeZoneSelector);

        timeZoneSelector.appendChild(createNoneOption());

        for (int i = 0; i < getTimeZones().size(); i++) {
            final DMNSimpleTimeZone timeZone = getTimeZones().get(i);
            final HTMLOptionElement option = createOptionWithId(timeZone);
            timeZoneSelector.appendChild(option);
        }

        setPickerValue(getSelectPicker(), getTimeZoneSelectedValue());
        timeZoneSelectorRefresh();
    }

    HTMLOptionElement createOptionWithId(final DMNSimpleTimeZone timeZone) {

        final String timeZoneId = timeZone.getId();
        final HTMLOptionElement option = getNewOption();
        option.value = timeZoneId;
        option.text = timeZoneId;
        return option;
    }

    void populateTimeZoneSelectorWithOffSets() {

        final List<String> offSets = timeZoneProvider.getTimeZonesOffsets();
        RemoveHelper.removeChildren(timeZoneSelector);

        timeZoneSelector.appendChild(createNoneOption());

        for (int i = 0; i < offSets.size(); i++) {

            final HTMLOptionElement option = createOptionWithOffset(offSets.get(i));
            timeZoneSelector.appendChild(option);
        }

        setPickerValue(getSelectPicker(), getTimeZoneSelectedValue());
        timeZoneSelectorRefresh();
    }

    HTMLOptionElement createOptionWithOffset(final String timeZoneOffSet) {

        final HTMLOptionElement option = getNewOption();
        option.value = timeZoneOffSet;
        option.text = timeZoneOffSet;
        return option;
    }

    HTMLOptionElement getNewOption() {
        // This is a workaround for an issue on Errai (ERRAI-1114) related to 'ManagedInstance' + 'HTMLOptionElement'.
        return (HTMLOptionElement) typeSelectOption.cloneNode(false);
    }

    HTMLOptionElement createNoneOption() {

        final HTMLOptionElement none = getNewOption();
        none.text = translationService.getValue(NONE_TRANSLATION_KEY);
        none.value = NONE_VALUE;
        return none;
    }

    void setPickerValue(final Element element,
                        final String value) {
        $(element).selectpicker("val", value);
    }

    void setupOnChangeHandler(final Element element) {
        $(element).on("hidden.bs.select", this::onSelectChange);
    }

    public void onSelectChange(final JQuerySelectPickerEvent event) {

        final String newValue = event.target.value;

        if (!Objects.equals(newValue, getValue())) {
            setPickerValue(newValue);
        }
    }

    void setPickerValue(final String value) {
        setPickerValue(getSelectPicker(), value);
        this.timeZoneSelectedValue = value;
    }

    @Override
    public String getValue() {

        final String time = picker.getValue();
        final String timeZoneValue;
        if (!Objects.isNull(getTimeZoneSelectedValue())) {
            timeZoneValue = NONE_VALUE.equals(getTimeZoneSelectedValue()) ? "" : getTimeZoneSelectedValue();
        } else {
            timeZoneValue = "";
        }

        return formatter.buildRawValue(time, timeZoneValue);
    }

    @Override
    public void setValue(final String value) {

        final TimeValue timeValue = formatter.getTimeValue(value);
        picker.setValue(timeValue.getTime());
        switch (timeValue.getTimeZoneMode()) {

            case OFFSET:
                setIsOffsetMode(true);
                refreshTimeZoneOffsetMode(timeValue);
                break;

            case TIMEZONE:
                setIsOffsetMode(false);
                refreshTimeZoneOffsetMode(timeValue);
                break;

            case NONE:
                setPickerValue("");
                break;
        }
    }

    void setIsOffsetMode(final boolean isOffsetMode) {
        this.isOffsetMode = isOffsetMode;
    }

    void refreshTimeZoneOffsetMode(final TimeValue timeValue) {
        refreshToggleTimeZoneIcon();
        reloadTimeZoneSelector();
        setPickerValue(timeValue.getTimeZoneValue());
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        timeInput.setAttribute("placeholder", placeholder);
    }

    @Override
    public void setOnInputChangeCallback(final Consumer<Event> onValueChanged) {
        timeInput.onchange = (Event event) -> {
            onValueChanged.accept(event);
            return this;
        };

        picker.setOnDateChanged(v -> onValueChanged.accept(null));
    }

    @Override
    public void select() {
        timeInput.select();
    }

    @Override
    public void setOnInputBlurCallback(final Consumer<BlurEvent> onValueInputBlur) {
        this.onValueInputBlur = onValueInputBlur;
    }

    Consumer<BlurEvent> getOnValueInputBlur() {
        return this.onValueInputBlur;
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    @EventHandler("time-input")
    public void onTimeInputBlur(final BlurEvent blurEvent) {
        onBlur(blurEvent);
    }

    private void onBlur(final BlurEvent blurEvent) {
        final Object target = getEventTarget(blurEvent);
        if (!Objects.isNull(getOnValueInputBlur())
                && !Objects.isNull(target)
                && !isChildOfView(target)) {
            getOnValueInputBlur().accept(blurEvent);
        }
    }

    @EventHandler("toggle-timezone-button")
    public void onToggleTimeZoneButtonClick(final ClickEvent clickEvent) {

        setIsOffsetMode(!getIsOffsetMode());
        refreshToggleTimeZoneIcon();
        reloadTimeZoneSelector();
    }

    void reloadTimeZoneSelector() {

        if (getIsOffsetMode()) {
            populateTimeZoneSelectorWithOffSets();
        } else {
            populateTimeZoneSelectorWithIds();
        }
    }

    void refreshToggleTimeZoneIcon() {

        if (getIsOffsetMode()) {
            toggleTimeZoneIcon.classList.remove(TIMEZONE_CLASS_ICON);
            toggleTimeZoneIcon.classList.add(OFFSET_CLASS_ICON);
        } else {
            toggleTimeZoneIcon.classList.add(TIMEZONE_CLASS_ICON);
            toggleTimeZoneIcon.classList.remove(OFFSET_CLASS_ICON);
        }
    }

    @Override
    public boolean isChildOfView(final Object element) {

        final Element viewElement = getElement();
        return viewElement.contains((Element) element);
    }

    @Override
    public void init(final TimeSelectorView presenter) {
        this.presenter = presenter;
    }
}
