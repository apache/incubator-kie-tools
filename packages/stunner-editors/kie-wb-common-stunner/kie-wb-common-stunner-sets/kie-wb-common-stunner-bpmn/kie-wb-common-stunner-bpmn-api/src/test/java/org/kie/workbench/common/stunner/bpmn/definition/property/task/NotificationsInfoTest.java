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


package org.kie.workbench.common.stunner.bpmn.definition.property.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationsInfo;
import org.kie.workbench.common.stunner.bpmn.util.EqualsAndHashCodeTestUtils;

public class NotificationsInfoTest {

    @Test
    public void testEqualsAndHashCode() {
        EqualsAndHashCodeTestUtils.TestCaseBuilder.newTestCase()
                .addTrueCase(new NotificationsInfo(),
                             new NotificationsInfo())
                .addTrueCase(new NotificationsInfo(new NotificationTypeListValue()),
                             new NotificationsInfo(new NotificationTypeListValue()))
                .addTrueCase(new NotificationsInfo(),
                             new NotificationsInfo(new NotificationTypeListValue()))
                .addFalseCase(new NotificationsInfo(),
                              new NotificationsInfo(new NotificationTypeListValue(getNotificationValues())))
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
