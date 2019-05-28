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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NotificationWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private NotificationWidgetViewImpl notificationWidgetView;

    @GwtMock
    private ClientTranslationService translationService;

    private NotificationWidget notificationWidget;

    private HTMLButtonElement saveButton, addButton;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        notificationWidget = spy(new NotificationWidget(notificationWidgetView, translationService));

        saveButton = spy(new HTMLButtonElement());
        addButton = spy(new HTMLButtonElement());

        doCallRealMethod().when(notificationWidget).getNameHeader();
        doCallRealMethod().when(notificationWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(notificationWidgetView).setReadOnly(any(boolean.class));

        setFieldValue(notificationWidgetView, "saveButton", saveButton);
        setFieldValue(notificationWidgetView, "addButton", addButton);

        when(translationService.getValue(any(String.class))).thenReturn("Notification");
    }

    @Test
    public void getNameHeaderTest() {
        Assert.assertEquals("Notification", notificationWidget.getNameHeader());
    }

    @Test
    public void setReadOnlyTest() {
        notificationWidget.setReadOnly(false);
        boolean readOnly = getFieldValue(NotificationWidgetViewImpl.class, notificationWidgetView, "readOnly");
        Assert.assertEquals(false, readOnly);
        notificationWidget.save();
    }
}
