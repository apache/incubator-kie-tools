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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.time;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.DateSelector;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.time.TimeSelector;

@Templated
@Dependent
public class DateTimeSelectorView implements DateTimeSelector.View {

    @DataField("date-selector-container")
    private final HTMLDivElement dateSelectorContainer;

    @DataField("time-selector-container")
    private final HTMLDivElement timeSelectorContainer;

    private final DateSelector dateSelector;

    private final TimeSelector timeSelector;

    private final ConstraintPlaceholderHelper placeholderHelper;

    private DateTimeSelector presenter;

    private Consumer<BlurEvent> onValueInputBlur;

    @Inject
    public DateTimeSelectorView(final HTMLDivElement dateSelectorContainer,
                                final HTMLDivElement timeSelectorContainer,
                                final DateSelector dateSelector,
                                final TimeSelector timeSelector,
                                final ConstraintPlaceholderHelper placeholderHelper) {

        this.dateSelectorContainer = dateSelectorContainer;
        this.timeSelectorContainer = timeSelectorContainer;
        this.dateSelector = dateSelector;
        this.timeSelector = timeSelector;
        this.placeholderHelper = placeholderHelper;
    }

    @PostConstruct
    void init() {

        dateSelectorContainer.appendChild(dateSelector.getElement());
        timeSelectorContainer.appendChild(timeSelector.getElement());

        dateSelector.setOnInputBlurCallback(this::onBlurCallback);
        timeSelector.setOnInputBlurCallback(this::onBlurCallback);

        dateSelector.setPlaceholder(placeholderHelper.getPlaceholderSample(BuiltInType.DATE.toString()));
        timeSelector.setPlaceholder(placeholderHelper.getPlaceholderSample(BuiltInType.TIME.toString()));
    }

    void onBlurCallback(final BlurEvent blurEvent) {

        if (!Objects.isNull(getOnValueInputBlur())) {

            final Object eventTarget = getEventTarget(blurEvent);
            if (!Objects.isNull(eventTarget)
                    && !dateSelector.isChild(eventTarget)
                    && !timeSelector.isChild(eventTarget)) {
                getOnValueInputBlur().accept(blurEvent);
            }
        }
    }

    Consumer<BlurEvent> getOnValueInputBlur() {
        return onValueInputBlur;
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    @Override
    public DateTimeValue getValue() {

        final String dateValue = dateSelector.getValue();
        final String timeValue = timeSelector.getValue();
        final DateTimeValue dateTimeValue = new DateTimeValue();
        dateTimeValue.setDate(dateValue);
        dateTimeValue.setTime(timeValue);

        return dateTimeValue;
    }

    @Override
    public void setValue(final DateTimeValue value) {

        dateSelector.setValue(value.getDate());
        timeSelector.setValue(value.getTime());
    }

    @Override
    public void setOnValueChanged(final Consumer<Event> onValueChanged) {

        dateSelector.setOnInputChangeCallback(onValueChanged);
        timeSelector.setOnInputChangeCallback(onValueChanged);
    }

    @Override
    public void setOnValueInputBlur(final Consumer<BlurEvent> blurEvent) {
        this.onValueInputBlur = blurEvent;
    }

    @Override
    public void select() {
        dateSelector.select();
    }

    @Override
    public void init(final DateTimeSelector presenter) {
        this.presenter = presenter;
    }
}
