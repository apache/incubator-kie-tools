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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import java.util.Date;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Expiration;

import static org.mockito.Mockito.doCallRealMethod;

@RunWith(GwtMockitoTestRunner.class)
public class NotificationEditorWidgetViewImplTest extends GWTTestCase {

    @GwtMock
    NotificationEditorWidgetViewImpl test;

    @Override
    public String getModuleName() {
        return "org.kie.workbench.common.stunner.bpmn.forms.validation.NotificationValueValidatorTest";
    }

    @Test
    public void testTzToOffset() {
        Assert.assertEquals(0, NotificationEditorWidgetViewImpl.ISO8601Builder.get().tzToOffset("0"));
        Assert.assertEquals(-600, NotificationEditorWidgetViewImpl.ISO8601Builder.get().tzToOffset("-10"));
        Assert.assertEquals(600, NotificationEditorWidgetViewImpl.ISO8601Builder.get().tzToOffset("10"));
        Assert.assertEquals(-150, NotificationEditorWidgetViewImpl.ISO8601Builder.get().tzToOffset("-02:30"));
        Assert.assertEquals(150, NotificationEditorWidgetViewImpl.ISO8601Builder.get().tzToOffset("02:30"));
    }

    @Before
    public void gwtSetUp() throws Exception {
        super.gwtSetUp();
        GwtMockito.initMocks(this);

        doCallRealMethod().when(test).getEscDomHandler();
    }

    @Test
    public void testISO8601BuilderExpression() {
        String result = NotificationEditorWidgetViewImpl.ISO8601Builder.get()
                .setRepeatable(false)
                .setType(Expiration.DATETIME.getName())
                .setRepeat("")
                .setUntil(false)
                .setDate(new Date())
                .setTz("+2")
                .setRepeatCount(0)
                .setPeriod("2M").build();
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetEscDomHandler() {
        Assert.assertNotNull(test.getEscDomHandler());
    }

    @Test
    public void testISO8601BuilderExpressionRepeatable() {
        String result = NotificationEditorWidgetViewImpl.ISO8601Builder.get()
                .setRepeatable(false)
                .setType(Expiration.DATETIME.getName())
                .setRepeat("true")
                .setUntil(false)
                .setDate(new Date())
                .setTz("+2")
                .setRepeatCount(10)
                .setPeriod("2M").build();

        Assert.assertNotNull(result);
    }
}
