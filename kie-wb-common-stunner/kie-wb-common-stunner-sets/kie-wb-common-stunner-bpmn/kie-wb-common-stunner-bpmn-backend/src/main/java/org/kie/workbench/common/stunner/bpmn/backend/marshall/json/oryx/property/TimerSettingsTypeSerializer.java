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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.property.event.timer.TimerSettingsValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.TimerSettingsType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@ApplicationScoped
public class TimerSettingsTypeSerializer implements Bpmn2OryxPropertySerializer<TimerSettingsValue> {

    private static final char DELIMITER = '|';
    private static final String EMPTY_TOKEN = "";

    @Override
    public boolean accepts(final PropertyType type) {
        return TimerSettingsType.name.equals(type.getName());
    }

    @Override
    public TimerSettingsValue parse(Object property,
                                    String value) {
        return parse(value);
    }

    public TimerSettingsValue parse(String value) {
        final List<String> tokens = parseTimerTokens(value);
        final String timeDate = tokens.get(0);
        final String timeDuration = tokens.get(1);
        final String timeCycle = tokens.get(2);
        final String timeCycleLanguage = tokens.get(3);
        return new TimerSettingsValue(!timeDate.isEmpty() ? timeDate : null,
                                      !timeDuration.isEmpty() ? timeDuration : null,
                                      !timeCycle.isEmpty() ? timeCycle : null,
                                      !timeCycleLanguage.isEmpty() ? timeCycleLanguage : null);
    }

    @Override
    public String serialize(Object property,
                            TimerSettingsValue value) {

        return serialize(value);
    }

    public String serialize(TimerSettingsValue value) {
        final StringBuffer serializedValue = new StringBuffer();
        appendValue(serializedValue,
                    value.getTimeDate());
        serializedValue.append(DELIMITER);
        appendValue(serializedValue,
                    value.getTimeDuration());
        serializedValue.append(DELIMITER);
        appendValue(serializedValue,
                    value.getTimeCycle());
        serializedValue.append(DELIMITER);
        appendValue(serializedValue,
                    value.getTimeCycleLanguage());
        return serializedValue.toString();
    }

    private List<String> parseTimerTokens(final String value) {
        final List<String> tokens = new ArrayList<>();
        if (value != null) {
            String remainder = value;
            String token;
            int index;
            while ((index = remainder.indexOf('|')) >= 0) {
                token = remainder.substring(0,
                                            index);
                tokens.add(token);
                remainder = remainder.substring(index + 1,
                                                remainder.length());
            }
            tokens.add(remainder);
        }
        return tokens;
    }

    private void appendValue(final StringBuffer stringBuffer,
                             final String value) {
        if (value != null) {
            stringBuffer.append(value);
        } else {
            stringBuffer.append(EMPTY_TOKEN);
        }
    }
}
