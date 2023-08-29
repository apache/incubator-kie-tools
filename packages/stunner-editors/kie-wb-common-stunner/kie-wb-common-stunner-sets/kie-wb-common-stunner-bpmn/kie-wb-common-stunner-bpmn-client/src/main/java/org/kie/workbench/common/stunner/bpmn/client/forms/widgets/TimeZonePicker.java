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


package org.kie.workbench.common.stunner.bpmn.client.forms.widgets;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Dependent
public class TimeZonePicker implements IsWidget,
                                       TimeZonePickerView.Presenter {

    private TimeZonePickerView view;

    private String current;

    @Inject
    public TimeZonePicker(TimeZonePickerView view) {
        this.view = view;
        view.init(this, parseTimeZones());
    }

    protected List<TimeZoneDTO> parseTimeZones() {
        JSONObject json = JSONParser.parseStrict(TimeZone.INSTANCE.asJson().getText()).isObject();
        return json.keySet()
                .stream()
                .map(m -> new TimeZoneDTO(m, json.get(m).isObject()))
                .collect(Collectors.toList());
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public String getValue() {
        return view.getValue();
    }

    @Override
    public void setValue(String value) {
        setValue(value, false);
    }

    @Override
    public void setValue(String value, boolean fireEvents) {
        String oldValue = current;
        current = value;

        view.setValue(current);

        if (fireEvents) {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return view.asWidget().addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        view.asWidget().fireEvent(event);
    }

    interface TimeZone extends ClientBundle {

        TimeZone INSTANCE = GWT.create(TimeZone.class);

        @Source("timezones.json")
        TextResource asJson();
    }

    static class TimeZoneDTO {

        final String name;
        String code;
        final double offsetAsDouble;
        final String offsetAsString;

        TimeZoneDTO(String code, JSONObject json) {

            this(json.get("name").isString().stringValue(),
                 json.get("offset").isString().stringValue(),
                 Double.parseDouble(json.get("offset")
                                            .isString()
                                            .stringValue()
                                            .replace(":", ".")));
            this.code = code;
        }

        private TimeZoneDTO(String name, String offsetAsString, double offsetAsDouble) {
            this.name = name;
            this.offsetAsString = offsetAsString;
            this.offsetAsDouble = offsetAsDouble;
        }

        @Override
        public String toString() {
            return "TimeZoneDTO{" +
                    "code='" + code + '\'' +
                    ", name='" + name + '\'' +
                    ", offsetAsString=" + offsetAsString +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof TimeZoneDTO)) {
                return false;
            }
            TimeZoneDTO that = (TimeZoneDTO) o;
            return offsetAsString == that.offsetAsString &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(code, that.code);
        }

        @Override
        public int hashCode() {
            return HashUtil.combineHashCodes(Objects.hash(name),
                                             Objects.hash(code),
                                             Objects.hash(offsetAsString));
        }
    }
}
