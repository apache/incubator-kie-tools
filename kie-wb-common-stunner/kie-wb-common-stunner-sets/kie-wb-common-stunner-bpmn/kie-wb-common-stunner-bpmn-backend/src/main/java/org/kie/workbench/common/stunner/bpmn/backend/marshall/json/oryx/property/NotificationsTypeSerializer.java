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

import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.type.NotificationsType;
import org.kie.workbench.common.stunner.core.definition.property.PropertyType;

@ApplicationScoped
public class NotificationsTypeSerializer implements Bpmn2OryxPropertySerializer<NotificationTypeListValue> {

    private static final String LIST_DELIMITER = "\\^";
    private static final String ELM_DELIMITER = "|";
    private static final String ARRAY_DELIMITER = ",";
    private static final String EMPTY_TOKEN = "";

    @Override
    public boolean accepts(final PropertyType type) {
        return NotificationsType.name.equals(type.getName());
    }

    @Override
    public NotificationTypeListValue parse(Object property,
                                           String value) {
        return parse(value);
    }

    public NotificationTypeListValue parse(String value) {
        NotificationTypeListValue notificationTypeListValue = new NotificationTypeListValue();
        Arrays.stream(value.split(LIST_DELIMITER)).forEach(elm
                                                                   -> notificationTypeListValue.addValue(parseNotificationValue(elm)));
        return notificationTypeListValue;
    }

    private NotificationValue parseNotificationValue(String value) {
        final List<String> tokens = parseNotificationTokens(value);
        final String body = tokens.get(0);
        final String expiresAt = tokens.get(1);
        final String from = tokens.get(2);
        final String replyTo = tokens.get(3);
        final String subject = tokens.get(4);
        final String type = tokens.get(5);
        final String users = tokens.get(6);
        final String groups = tokens.get(7);

        return new NotificationValue(body,
                                     expiresAt,
                                     subject,
                                     replyTo,
                                     type,
                                     from,
                                     Arrays.stream(users.split(ARRAY_DELIMITER)).collect(Collectors.toList()),
                                     Arrays.stream(groups.split(ARRAY_DELIMITER)).collect(Collectors.toList()));
    }

    @Override
    public String serialize(Object property,
                            NotificationTypeListValue value) {

        return serializeList(value);
    }

    private String serializeList(NotificationTypeListValue value) {
        return value.getValues()
                .stream()
                .map(this::serializeNotificationValue)
                .collect(Collectors.joining("^"));
    }

    private String serializeNotificationValue(NotificationValue value) {

        final StringBuffer serializedValue = new StringBuffer();
        appendValue(serializedValue,
                    value.getBody());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getExpiresAt());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getFrom());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getReplyTo());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getSubject());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getType());
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getUsers().stream().collect(Collectors.joining(ARRAY_DELIMITER)));
        serializedValue.append(ELM_DELIMITER);
        appendValue(serializedValue,
                    value.getGroups().stream().collect(Collectors.joining(ARRAY_DELIMITER)));
        return serializedValue.toString();
    }

    private List<String> parseNotificationTokens(final String value) {
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