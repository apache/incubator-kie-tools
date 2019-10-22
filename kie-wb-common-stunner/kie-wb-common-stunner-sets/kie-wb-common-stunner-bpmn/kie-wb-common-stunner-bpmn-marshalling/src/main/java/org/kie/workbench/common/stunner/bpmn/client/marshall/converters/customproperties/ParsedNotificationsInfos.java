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

package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;

public class ParsedNotificationsInfos {

    NotificationValue notification = new NotificationValue();

    private ParsedNotificationsInfos(String type, String body) {
        notification.setType(type);

        if (body != null && !body.isEmpty()) {
            String temp;
            if (body.contains("@")) {
                String[] parts = body.split("@");
                parsePeriod(notification, parts[1]);
                temp = parts[0];
            } else {
                temp = body;
            }
            temp = replaceBracket(temp);

            getFrom(notification, temp);
            getUsers(notification, temp);
            getGroups(notification, temp);
            getReplyTo(notification, temp);
            getSubject(notification, temp);
            getBody(notification, temp);
        }
    }

    private static void getFrom(NotificationValue notification, String body) {
        notification.setFrom(parseElement(body, "from", 0));
    }

    private static String parseElement(String group, String type, int position) {
        if (group.contains(type)) {
            String result = group
                    .split("\\|")[position]
                    .replace(type + ":", "");
            if (!result.isEmpty()) {
                return result;
            }
        }
        return null;
    }

    private static void getUsers(NotificationValue notification, String body) {
        notification.setUsers(parseGroup(body, "tousers", 1));
    }

    private static List<String> parseGroup(String group, String type, int position) {
        if (group.contains(type)) {
            String result = group
                    .split("\\|")[position]
                    .replace(type + ":", "");
            if (!result.isEmpty()) {
                return Arrays.stream(result.split(",")).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private static void getGroups(NotificationValue notification, String body) {
        notification.setGroups(parseGroup(body, "togroups", 2));
    }

    private static void getReplyTo(NotificationValue notification, String body) {
        notification.setReplyTo(parseElement(body, "replyTo", 3));
    }

    private static void getSubject(NotificationValue notification, String body) {
        notification.setSubject(parseElement(body, "subject", 4));
    }

    private static void getBody(NotificationValue notification, String body) {
        notification.setBody(parseElement(body, "body", 5));
    }

    private static void parsePeriod(NotificationValue notification, String part) {
        notification.setExpiresAt(replaceBracket(part));
    }

    private static String replaceBracket(String original) {
        return original.replaceFirst("\\[", "").replace("]", "");
    }

    public static NotificationValue of(String type, String body) {
        return new ParsedNotificationsInfos(type, body).notification;
    }

    public static String ofCDATA(NotificationTypeListValue values, AssociationType type) {
        return new ParsedNotificationsInfos.CDATA(values, type).get();
    }

    private static class CDATA {

        private List<NotificationValue> notifications;

        private AssociationType type;

        CDATA(NotificationTypeListValue value, AssociationType type) {
            this.type = type;
            notifications = value.getValues();
        }

        String get() {
            return notifications.stream().filter(m -> m.getType().equals(type.getName()))
                    .map(m -> m.toCDATAFormat())
                    .collect(Collectors.joining("^"));
        }
    }
}
