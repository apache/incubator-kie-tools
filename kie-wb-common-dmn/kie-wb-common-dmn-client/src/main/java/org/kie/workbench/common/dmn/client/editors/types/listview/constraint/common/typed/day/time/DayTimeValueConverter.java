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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.day.time;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.uberfire.client.views.pfly.widgets.MomentDurationObject;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.DurationHelper.addFunctionCall;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.typed.common.DurationHelper.getFunctionParameter;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Day;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Days;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Hour;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Hours;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Minute;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Minutes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Second;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DayTimeValueConverter_Seconds;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;
import static org.uberfire.client.views.pfly.widgets.MomentDuration.moment;

public class DayTimeValueConverter {

    private final TranslationService translationService;

    @Inject
    public DayTimeValueConverter(final TranslationService translationService) {
        this.translationService = translationService;
    }

    String toDMNString(final DayTimeValue value) {
        final JavaScriptObject properties = makeProperties(value.getDays(),
                                                           value.getHours(),
                                                           value.getMinutes(),
                                                           value.getSeconds());

        return addFunctionCall(moment.duration(properties).toISOString());
    }

    DayTimeValue fromDMNString(final String dmnString) {
        final MomentDurationObject duration = moment.duration(getFunctionParameter(dmnString));
        return new DayTimeValue(duration.days(),
                                duration.hours(),
                                duration.minutes(),
                                duration.seconds());
    }

    String toDisplayValue(final String dmnString) {

        final DayTimeValue value = fromDMNString(dmnString);
        final String daysLabel = pluralize(value.getDays(), DayTimeValueConverter_Day, DayTimeValueConverter_Days);
        final String hoursLabel = pluralize(value.getHours(), DayTimeValueConverter_Hour, DayTimeValueConverter_Hours);
        final String minutesLabel = pluralize(value.getMinutes(), DayTimeValueConverter_Minute, DayTimeValueConverter_Minutes);
        final String secondsLabel = pluralize(value.getSeconds(), DayTimeValueConverter_Second, DayTimeValueConverter_Seconds);

        return Stream
                   .of(daysLabel, hoursLabel, minutesLabel, secondsLabel)
                   .filter(e -> !isEmpty(e))
                   .collect(Collectors.joining(", "));
    }

    private JavaScriptObject makeProperties(final Integer days,
                                            final Integer hours,
                                            final Integer minutes,
                                            final Integer seconds) {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put("days", number(days));
        jsonObject.put("hours", number(hours));
        jsonObject.put("minutes", number(minutes));
        jsonObject.put("seconds", number(seconds));
        return jsonObject.getJavaScriptObject();
    }

    JSONValue number(final Integer value) {
        return Objects.isNull(value) ? JSONNull.getInstance() : new JSONNumber(value);
    }

    private String pluralize(final int value,
                             final String singular,
                             final String plural) {
        if (value == 1) {
            return value + " " + translationService.format(singular);
        }
        return value + " " + translationService.format(plural);
    }
}
