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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.MinMaxValueHelper.setupMinMaxHandlers;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.MinMaxValueHelper.toInteger;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time.DayTimeValue.NONE;

@Templated
public class DayTimeSelectorView implements DayTimeSelector.View {

    @DataField("days-input")
    private final HTMLInputElement daysInput;

    @DataField("hours-input")
    private final HTMLInputElement hoursInput;

    @DataField("minutes-input")
    private final HTMLInputElement minutesInput;

    @DataField("seconds-input")
    private final HTMLInputElement secondsInput;

    private Consumer<Event> onValueChangedConsumer;

    private Consumer<BlurEvent> onValueInputBlurConsumer;

    private DayTimeSelector presenter;

    @Inject
    public DayTimeSelectorView(final HTMLInputElement daysInput,
                               final HTMLInputElement hoursInput,
                               final HTMLInputElement minutesInput,
                               final HTMLInputElement secondsInput) {
        this.daysInput = daysInput;
        this.hoursInput = hoursInput;
        this.minutesInput = minutesInput;
        this.secondsInput = secondsInput;
    }

    @Override
    public void init(final DayTimeSelector presenter) {
        this.presenter = presenter;
    }

    @PostConstruct
    public void setupEventHandlers() {

        setupMinMaxHandlers(daysInput);
        setupMinMaxHandlers(hoursInput);
        setupMinMaxHandlers(minutesInput);
        setupMinMaxHandlers(secondsInput);

        daysInput.onchange = getOnChangeHandler();
        hoursInput.onchange = getOnChangeHandler();
        minutesInput.onchange = getOnChangeHandler();
        secondsInput.onchange = getOnChangeHandler();
    }

    @Override
    public DayTimeValue getValue() {
        final DayTimeValue value = new DayTimeValue();
        value.setDays(toInteger(daysInput.value, NONE));
        value.setHours(toInteger(hoursInput.value, NONE));
        value.setMinutes(toInteger(minutesInput.value, NONE));
        value.setSeconds(toInteger(secondsInput.value, NONE));
        return value;
    }

    @Override
    public void setValue(final DayTimeValue value) {
        daysInput.value = toDisplay(value.getDays());
        hoursInput.value = toDisplay(value.getHours());
        minutesInput.value = toDisplay(value.getMinutes());
        secondsInput.value = toDisplay(value.getSeconds());
    }

    @Override
    public void setOnValueChanged(final Consumer<Event> onValueChangedConsumer) {
        this.onValueChangedConsumer = onValueChangedConsumer;
    }

    @Override
    public void setOnValueInputBlur(final Consumer<BlurEvent> onValueInputBlurConsumer) {
        this.onValueInputBlurConsumer = onValueInputBlurConsumer;
    }

    @EventHandler("days-input")
    public void onDaysInputBlurEvent(final BlurEvent blurEvent) {
        onBlurHandler(blurEvent);
    }

    @EventHandler("hours-input")
    public void onHoursInputBlurEvent(final BlurEvent blurEvent) {
        onBlurHandler(blurEvent);
    }

    @EventHandler("minutes-input")
    public void onMinutesInputBlurEvent(final BlurEvent blurEvent) {
        onBlurHandler(blurEvent);
    }

    @EventHandler("seconds-input")
    public void onSecondsInputBlurEvent(final BlurEvent blurEvent) {
        onBlurHandler(blurEvent);
    }

    @Override
    public void select() {
        daysInput.select();
    }

    Element.OnchangeFn getOnChangeHandler() {
        return e -> {
            getOnValueChangedConsumer().ifPresent(consumer -> consumer.accept(e));
            return true;
        };
    }

    void onBlurHandler(final BlurEvent blurEvent) {
        if (!isDayTimeInput(getEventTarget(blurEvent))) {
            getOnValueInputBlurConsumer().ifPresent(consumer -> consumer.accept(blurEvent));
        }
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    Optional<Consumer<BlurEvent>> getOnValueInputBlurConsumer() {
        return Optional.ofNullable(onValueInputBlurConsumer);
    }

    Optional<Consumer<Event>> getOnValueChangedConsumer() {
        return Optional.ofNullable(onValueChangedConsumer);
    }

    private boolean isDayTimeInput(final Object element) {
        return Arrays.<Object>asList(daysInput, hoursInput, minutesInput, secondsInput).contains(element);
    }

    private String toDisplay(final Integer value) {
        return Objects.isNull(value) || Objects.equals(value, 0) ? "" : value.toString();
    }
}
