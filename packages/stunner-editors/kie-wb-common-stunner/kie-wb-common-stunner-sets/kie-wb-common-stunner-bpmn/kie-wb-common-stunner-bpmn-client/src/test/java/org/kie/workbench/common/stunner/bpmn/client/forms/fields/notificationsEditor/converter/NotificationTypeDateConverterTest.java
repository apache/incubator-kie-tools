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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.converter;

import org.junit.Assert;
import org.junit.Test;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationType;

public class NotificationTypeDateConverterTest {

    private NotificationTypeDateConverter notificationTypeDateConverter = new NotificationTypeDateConverter();

    @Test
    public void toModelValueTest() {
        Assert.assertEquals(NotificationType.NOT_STARTED_NOTIFY, notificationTypeDateConverter.toModelValue("NotStartedNotify"));
        Assert.assertEquals(NotificationType.NOT_COMPLETED_NOTIFY, notificationTypeDateConverter.toModelValue("NotCompletedNotify"));
    }

    @Test
    public void toWidgetValueTest() {
        Assert.assertEquals(NotificationType.NOT_STARTED_NOTIFY.getType(), notificationTypeDateConverter.toWidgetValue(NotificationType.NOT_STARTED_NOTIFY));
        Assert.assertEquals(NotificationType.NOT_COMPLETED_NOTIFY.getType(), notificationTypeDateConverter.toWidgetValue(NotificationType.NOT_COMPLETED_NOTIFY));
    }
}
