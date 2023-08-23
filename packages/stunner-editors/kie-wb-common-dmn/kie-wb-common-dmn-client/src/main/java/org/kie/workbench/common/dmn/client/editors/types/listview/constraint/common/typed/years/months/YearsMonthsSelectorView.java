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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.years.months;

import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
@Dependent
public class YearsMonthsSelectorView implements YearsMonthsSelector.View {

    private YearsMonthsSelectorView presenter;

    @DataField("years-input")
    private final HTMLInputElement yearInput;

    @DataField("months-input")
    private final HTMLInputElement monthInput;

    private Consumer<BlurEvent> onValueInputBlur;

    @Inject
    public YearsMonthsSelectorView(final HTMLInputElement yearInput,
                                   final HTMLInputElement monthInput) {
        this.yearInput = yearInput;
        this.monthInput = monthInput;
    }

    @Override
    public void init(final YearsMonthsSelectorView presenter) {
        this.presenter = presenter;
    }

    @Override
    public YearsMonthsValue getValue() {
        final YearsMonthsValue value = new YearsMonthsValue();
        value.setMonths(monthInput.value);
        value.setYears(yearInput.value);
        return value;
    }

    @Override
    public void setValue(final YearsMonthsValue value) {
        yearInput.value = value.getYears();
        monthInput.value = value.getMonths();
    }

    @Override
    public void setPlaceHolder(final String placeholder) {

        final String attribute = "placeholder";
        yearInput.setAttribute(attribute, placeholder);
        monthInput.setAttribute(attribute, placeholder);
    }

    @EventHandler("years-input")
    public void onYearsInputBlur(final BlurEvent blurEvent) {
        handle(blurEvent);
    }

    @EventHandler("months-input")
    public void onMonthsInputBlur(final BlurEvent blurEvent) {
        handle(blurEvent);
    }

    void handle(final BlurEvent blurEvent) {

        final Object target = getEventTarget(blurEvent);
        if (!Objects.isNull(onValueInputBlur) && !isYearsOrMonthsInput(target)) {
            onValueInputBlur.accept(blurEvent);
        }
    }

    boolean isYearsOrMonthsInput(final Object object) {
        return yearInput == object || monthInput == object;
    }

    @Override
    public void onValueChanged(final Consumer<Event> onValueChanged) {

        yearInput.onchange = event -> {
            onValueChanged.accept(event);
            return this;
        };

        monthInput.onchange = event -> {
            onValueChanged.accept(event);
            return this;
        };
    }

    @Override
    public void onValueInputBlur(final Consumer<BlurEvent> blurEvent) {
        this.onValueInputBlur = blurEvent;
    }

    @Override
    public void select() {
        yearInput.select();
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }
}
