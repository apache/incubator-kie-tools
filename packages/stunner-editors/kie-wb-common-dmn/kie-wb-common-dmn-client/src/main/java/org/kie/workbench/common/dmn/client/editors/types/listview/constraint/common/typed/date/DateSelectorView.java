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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.date.JQueryDatepicker.$;

@Templated
@Dependent
public class DateSelectorView implements DateSelector.View {

    @DataField("date-input")
    private final HTMLInputElement dateInput;

    private final DateValueFormatter valueFormatter;

    private DateSelectorView presenter;

    private Consumer<BlurEvent> onValueInputBlur;

    @Inject
    public DateSelectorView(final HTMLInputElement dateInput,
                            final DateValueFormatter valueFormatter) {
        this.dateInput = dateInput;
        this.valueFormatter = valueFormatter;
    }

    @PostConstruct
    void init() {
        $(dateInput).datepicker(properties().getJavaScriptObject());
    }

    JSONObject properties() {
        final JSONObject jsonObject = makeJsonObject();
        jsonObject.put("format", new JSONString("d M yyyy"));
        return jsonObject;
    }

    JSONObject makeJsonObject() {
        return new JSONObject();
    }

    @Override
    public String getValue() {
        return StringUtils.isEmpty(dateInput.value) ? "" : valueFormatter.toRaw(dateInput.value);
    }

    @Override
    public void setValue(final String value) {
        if (StringUtils.isEmpty(value)) {
            dateInput.value = "";
        } else {
            $(dateInput).datepicker("setDate", valueFormatter.toDisplay(value));
        }
    }

    @Override
    public void setPlaceholder(final String placeholder) {
        dateInput.setAttribute("placeholder", placeholder);
    }

    @Override
    public void onValueChanged(final Consumer<Event> onValueChanged) {
        dateInput.onchange = (Event event) -> {
            onValueChanged.accept(event);
            return this;
        };
    }

    @Override
    public void select() {
        dateInput.select();
    }

    @EventHandler("date-input")
    public void onDateInputBlur(final BlurEvent blurEvent) {

        final Object target = getEventTarget(blurEvent);
        if (!Objects.isNull(onValueInputBlur) && !Objects.isNull(target)) {
            onValueInputBlur.accept(blurEvent);
        }
    }

    Object getEventTarget(final BlurEvent blurEvent) {
        return blurEvent.getNativeEvent().getRelatedEventTarget();
    }

    @Override
    public void onValueInputBlur(final Consumer<BlurEvent> blurEvent) {
        this.onValueInputBlur = blurEvent;
    }

    @Override
    public boolean isChildOfView(final Object element) {

        final Element viewElement = getElement();
        return viewElement.contains((Element) element);
    }

    @Override
    public void init(final DateSelectorView presenter) {
        this.presenter = presenter;
    }
}
