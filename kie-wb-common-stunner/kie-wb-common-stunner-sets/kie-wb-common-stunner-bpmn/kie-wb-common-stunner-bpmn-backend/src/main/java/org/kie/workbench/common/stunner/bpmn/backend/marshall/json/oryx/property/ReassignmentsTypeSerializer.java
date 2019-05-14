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

package org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.ReassignmentsType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@ApplicationScoped
public class ReassignmentsTypeSerializer implements Bpmn2OryxPropertySerializer<ReassignmentTypeListValue> {

    private static final String LIST_DELIMITER = "\\^";
    private static final String ELM_DELIMITER = "|";
    private static final String ARRAY_DELIMITER = ",";
    private static final String EMPTY_TOKEN = "";

    @Override
    public boolean accepts(final PropertyType type) {
        return ReassignmentsType.name.equals(type.getName());
    }

    @Override
    public ReassignmentTypeListValue parse(Object property,
                                           String value) {
        return parse(value);
    }

    public ReassignmentTypeListValue parse(String value) {
        ReassignmentTypeListValue reassignmentTypeListValue = new ReassignmentTypeListValue();
        Arrays.stream(value.split(LIST_DELIMITER)).forEach(elm
                -> reassignmentTypeListValue.addValue(parseReassignmentValue(elm)));
        return reassignmentTypeListValue;
    }

    private ReassignmentValue parseReassignmentValue(String value) {
        final List<String> tokens = parseReassignmentTokens(value);
        final String type = tokens.get(0);
        final String duration = tokens.get(1);
        final String groups = tokens.get(2);
        final String users = tokens.get(3);

        return new ReassignmentValue(type,
                duration,
                Arrays.stream(groups.split(ARRAY_DELIMITER)).collect(Collectors.toList()),
                Arrays.stream(users.split(ARRAY_DELIMITER)).collect(Collectors.toList()));
    }

    @Override
    public String serialize(Object property,
                            ReassignmentTypeListValue value) {

        return serializeList(value);
    }

    private String serializeList(ReassignmentTypeListValue value) {
        return value.getValues()
                .stream()
                .map(this::serializeReassignmentValue)
                .collect(Collectors.joining("^"));

    }

    private String serializeReassignmentValue(ReassignmentValue value) {
        final StringBuffer serializedValue = new StringBuffer();
        appendValue(serializedValue,
                value.getType());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                value.getDuration());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                value.getGroups().stream().collect(Collectors.joining(ARRAY_DELIMITER)));
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                value.getUsers().stream().collect(Collectors.joining(ARRAY_DELIMITER)));
        return serializedValue.toString();
    }

    private List<String> parseReassignmentTokens(final String value) {
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
