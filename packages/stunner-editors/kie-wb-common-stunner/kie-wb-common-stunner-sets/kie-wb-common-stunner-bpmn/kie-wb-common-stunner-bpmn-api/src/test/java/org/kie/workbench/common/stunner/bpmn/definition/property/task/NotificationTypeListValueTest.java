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

package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

public class NotificationTypeListValueTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new NotificationTypeListValue(),
                             new NotificationTypeListValue())
                .addTrueCase(new NotificationTypeListValue(new ArrayList<>()),
                             new NotificationTypeListValue(new ArrayList<>()))
                .addTrueCase(new NotificationTypeListValue(),
                             new NotificationTypeListValue(new ArrayList<>()))
                .addFalseCase(new NotificationTypeListValue(getNotificationValues()),
                              new NotificationTypeListValue(new ArrayList<>()))
                .test();
    }

    private List<NotificationValue> getNotificationValues() {
        List<NotificationValue> result = new ArrayList<>();
        result.add(new NotificationValue("AAA",
                                         "1h",
                                         "Subj",
                                         "me",
                                         "NotStartedNotify",
                                         "me",
                                         Arrays.asList("foo", "bar", "baz"),
                                         Arrays.asList("foo", "bar", "baz"),
                                         ""));
        return result;
    }
}
