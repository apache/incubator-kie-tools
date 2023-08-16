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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class NotificationRowTest {

    @Mock
    private NotificationValue mockNotification;

    private NotificationRow notificationRowUnderTest;

    @Before
    public void setUp() {
        initMocks(this);
        when(mockNotification.getType()).thenReturn(NotificationType.NOT_COMPLETED_NOTIFY.getType());

        notificationRowUnderTest = new NotificationRow(mockNotification);
    }

    @Test
    public void testClone() {
        assertEquals(notificationRowUnderTest, notificationRowUnderTest.clone());
    }

    @Test
    public void testToNotificationValue() {
        // Setup
        final NotificationValue expectedResult = new NotificationValue();
        expectedResult.setExpiresAt("");
        expectedResult.setFrom("");
        expectedResult.setBody("");
        expectedResult.setSubject("");
        expectedResult.setReplyTo("");
        expectedResult.setType("NotCompletedNotify");
        // Run the test
        final NotificationValue result = new NotificationRow().toNotificationValue();

        // Verify the results
        assertEquals(expectedResult, result);
    }

    @Test
    public void testEquals() {
        // Setup
        final Object obj = null;
        // Verify the results
        assertFalse(notificationRowUnderTest.equals(obj));
    }
}
