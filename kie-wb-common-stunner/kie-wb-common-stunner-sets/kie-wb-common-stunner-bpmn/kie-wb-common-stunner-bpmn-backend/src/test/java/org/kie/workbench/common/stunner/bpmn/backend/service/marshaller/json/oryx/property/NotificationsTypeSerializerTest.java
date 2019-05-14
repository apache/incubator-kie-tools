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

package org.kie.workbench.common.stunner.bpmn.backend.service.marshaller.json.oryx.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.NotificationsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;

import static org.junit.Assert.assertEquals;

public class NotificationsTypeSerializerTest {

    private static final String SERIALIZED = "AAA|1h|me|me|Subj|NotStartedNotify|foo,bar,baz|foo,bar,baz";
    private static final String DELIMITER = "^";
    private NotificationsTypeSerializer serializer;

    @Before
    public void setUp() {
        serializer = new NotificationsTypeSerializer();
    }

    @Test
    public void testParseEmpty() {
        NotificationTypeListValue notificationTypeListValue = new NotificationTypeListValue();

        String result = serializer.serialize(new Object(), notificationTypeListValue);

        assertEquals(result,
                "");
    }

    @Test
    public void testParseContainsOne() {
        NotificationTypeListValue notificationTypeListValue = new NotificationTypeListValue(getNotificationValues());
        String result = serializer.serialize(new Object(), notificationTypeListValue);

        assertEquals(result, SERIALIZED);

        assertEquals(notificationTypeListValue, serializer.parse(result));
    }

    private List<NotificationValue> getNotificationValues() {
        List<NotificationValue> result = new ArrayList<>();
        result.add(getNotificationValue());
        return result;
    }

    private NotificationValue getNotificationValue() {
        return new NotificationValue("AAA",
                "1h",
                "Subj",
                "me",
                "NotStartedNotify",
                "me",
                Arrays.asList(new String[]{"foo", "bar", "baz"}),
                Arrays.asList(new String[]{"foo", "bar", "baz"}));
    }

    @Test
    public void testParseContainsFew() {
        NotificationTypeListValue notificationTypeListValue = new NotificationTypeListValue();
        notificationTypeListValue.addValue(getNotificationValue());
        notificationTypeListValue.addValue(getNotificationValue());
        notificationTypeListValue.addValue(getNotificationValue());
        String result = serializer.serialize(new Object(), notificationTypeListValue);

        assertEquals(result, SERIALIZED + DELIMITER + SERIALIZED + DELIMITER + SERIALIZED);
        assertEquals(notificationTypeListValue, serializer.parse(result));
    }
}
