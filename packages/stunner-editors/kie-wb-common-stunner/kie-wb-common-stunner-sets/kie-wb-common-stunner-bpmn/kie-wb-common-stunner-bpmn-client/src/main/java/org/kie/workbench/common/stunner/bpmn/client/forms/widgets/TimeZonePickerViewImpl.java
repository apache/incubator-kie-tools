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

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.extras.select.client.ui.Option;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Dependent
@Templated(value = "TimeZonePicker.html")
public class TimeZonePickerViewImpl extends Composite implements TimeZonePickerView {

    private Presenter presenter;

    private final double defaultOffset = new Date().getTimezoneOffset() * -1;

    private static final String NEW_NOTIFICATION = "0";

    String userTimeZone;

    @DataField
    Select tzSelect = new Select();

    private List<TimeZonePicker.TimeZoneDTO> zones;

    @DataField
    private Button tzSwitch = new Button();

    private TZSelectType tzSelectType = TZSelectType.TZ;

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public void init(Presenter presenter, List<TimeZonePicker.TimeZoneDTO> zones) {
        this.presenter = presenter;
        this.zones = zones;

        tzSwitch.setIcon(IconType.GLOBE);
        tzSwitch.setColor("blue");
        tzSwitch.addClickHandler(event -> changeSwitchType());
        populateTzSelector();
    }

    private void changeSwitchType() {
        tzSelectType = tzSelectType.equals(TZSelectType.COUNTRY) ? TZSelectType.TZ : TZSelectType.COUNTRY;
        populateTzSelector();
    }

    void populateTzSelector() {
        tzSelect.clear();
        if (tzSelectType.equals(TZSelectType.COUNTRY)) {
            for (int i = 0; i < zones.size(); i++) {
                Option option = new Option();
                option.setText(zones.get(i).name);
                option.setValue(zones.get(i).offsetAsString + "");
                if (new Double(zones.get(i).offsetAsDouble).equals(new Double(defaultOffset / 60))) {
                    option.setSelected(true);
                    userTimeZone = option.getValue();
                }
                tzSelect.add(option);
            }
        } else {
            zones.stream().filter(distinctByKey(p -> p.offsetAsDouble))
                    .sorted((z1, z2) -> Double.valueOf(z1.offsetAsDouble)
                            .compareTo(new Double(z2.offsetAsDouble)))
                    .collect(Collectors.toCollection(LinkedHashSet::new))
                    .forEach(zone -> {
                        Option option = new Option();
                        option.setText(zone.offsetAsString + "");
                        option.setValue(zone.offsetAsString + "");
                        if (new Double(zone.offsetAsDouble).equals(new Double(defaultOffset / 60))) {
                            option.setSelected(true);
                            userTimeZone = option.getValue();
                        }
                        tzSelect.add(option);
                    });
        }
        tzSelect.refresh();
    }

    @Override
    public String getValue() {
        return tzSelect.getValue();
    }

    @Override
    public void setValue(String value) {
        if (value.equals(NEW_NOTIFICATION)) {
            value = userTimeZone;
        }
        tzSelect.setValue(value);
    }

    private enum TZSelectType {
        TZ,
        COUNTRY
    }
}
