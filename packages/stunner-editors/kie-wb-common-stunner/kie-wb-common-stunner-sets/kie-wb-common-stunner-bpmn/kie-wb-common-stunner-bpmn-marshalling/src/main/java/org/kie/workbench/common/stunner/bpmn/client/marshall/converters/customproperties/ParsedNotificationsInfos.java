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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.associations.AssociationType;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;

import static java.lang.String.join;
import static org.kie.workbench.common.stunner.core.util.StringUtils.nonEmpty;

public class ParsedNotificationsInfos {

    private ParsedNotificationsInfos() {
    }

    public static NotificationValue of(String type, String body) {
        NotificationValue notification = new NotificationValue();
        notification.setType(type);

        if (nonEmpty(body)) {
            String tBody;
            if (body.contains("]@[")) {
                String[] parts = body.split("\\]@\\[");
                notification.setExpiresAt(parts[1].substring(0, parts[1].length() - 1));
                tBody = parts[0].replaceFirst("\\[", "");
            } else {
                tBody = body;
                tBody = removeBracket(tBody);
            }

            notification.setFrom(parseElement(tBody, "from"));
            notification.setUsers(parseGroup(tBody, "tousers"));
            notification.setGroups(parseGroup(tBody, "togroups"));
            notification.setEmails(join(",", parseGroup(tBody, "toemails")));
            notification.setReplyTo(parseElement(tBody, "replyTo"));
            notification.setSubject(replaceAsciiSymbols(parseElement(tBody, "subject")));
            notification.setBody(replaceAsciiSymbols(parseElement(tBody, "body")));
        }

        return notification;
    }

    private static String parseElement(String group, String type) {
        return Arrays.stream(group.split("\\|"))
                .filter(x -> x.startsWith(type))
                .findFirst().orElse("")
                .replace(type + ":", "");
    }

    private static List<String> parseGroup(String group, String type) {
        if (group.contains(type)) {
            String result = Arrays.stream(group.split("\\|"))
                    .filter(x -> x.startsWith(type))
                    .findFirst().orElse("")
                    .replace(type + ":", "");
            if (!result.isEmpty()) {
                return Arrays.stream(result.split(",")).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    private static String removeBracket(String original) {
        return original.replaceFirst("\\[", "").replaceFirst("\\]([^\\]]*)$", "$1");
    }

    public static String ofCDATA(NotificationTypeListValue values, AssociationType type) {
        return new ParsedNotificationsInfos.CDATA(values, type).get();
    }

    private static String replaceAsciiSymbols(String value) {
        if (value == null) {
            return null;
        }

        return value
                .replaceAll("&#124;", "|")
                .replaceAll("&#94;", "^");
    }

    private static class CDATA {

        private final List<NotificationValue> notifications;

        private final AssociationType type;

        CDATA(NotificationTypeListValue value, AssociationType type) {
            this.type = type;
            notifications = value.getValues();
        }

        String get() {
            return notifications.stream().filter(m -> m.getType().equals(type.getName()))
                    .map(NotificationValue::toCDATAFormat)
                    .collect(Collectors.joining("^"));
        }
    }

}
