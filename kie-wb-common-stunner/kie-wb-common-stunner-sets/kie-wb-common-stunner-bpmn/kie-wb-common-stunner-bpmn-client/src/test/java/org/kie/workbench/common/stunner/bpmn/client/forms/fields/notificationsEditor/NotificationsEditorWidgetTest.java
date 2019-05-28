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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor;

import java.util.ArrayList;
import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLInputElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget.NotificationWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.widget.NotificationWidgetViewImpl;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationValue;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class NotificationsEditorWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private NotificationsEditorWidget notificationsEditorWidget;

    @GwtMock
    private NotificationWidget notificationWidget;

    @GwtMock
    private NotificationWidgetViewImpl notificationWidgetViewImpl;

    @GwtMock
    private HTMLButtonElement notificationButton;

    @Mock
    private NotificationTypeListValue values;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        doCallRealMethod().when(notificationsEditorWidget).setValue(any(NotificationTypeListValue.class));
        doCallRealMethod().when(notificationsEditorWidget).setValue(any(NotificationTypeListValue.class), any(boolean.class));
        doCallRealMethod().when(notificationsEditorWidget).getValue();
        doCallRealMethod().when(notificationsEditorWidget).init();
        doCallRealMethod().when(notificationsEditorWidget).addValueChangeHandler(any(ValueChangeHandler.class));
        doCallRealMethod().when(notificationsEditorWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(notificationsEditorWidget).showNotificationsDialog();

        doCallRealMethod().when(notificationWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(notificationWidget).show();
        doCallRealMethod().when(notificationWidgetViewImpl).setReadOnly(any(boolean.class));

        doCallRealMethod().when(values).setValues(any(List.class));
        doCallRealMethod().when(values).getValues();
        doCallRealMethod().when(values).addValue(any(NotificationValue.class));
        doCallRealMethod().when(values).isEmpty();

        setFieldValue(notificationsEditorWidget, "notificationTextBox", new HTMLInputElement());
    }

    @Test
    public void testZeroNotifications() {
        values.setValues(new ArrayList<>());
        notificationsEditorWidget.setValue(values);

        Assert.assertEquals(0, values.getValues().size());
        Assert.assertTrue(values.isEmpty());
        Assert.assertEquals(0, notificationsEditorWidget.getValue().getValues().size());

        HTMLInputElement input = getFieldValue(NotificationsEditorWidget.class,
                                               notificationsEditorWidget,
                                               "notificationTextBox");
        Assert.assertEquals("0 notifications", input.value);
    }

    @Test
    public void testOneNotification() {
        values.setValues(new ArrayList<>());
        values.addValue(new NotificationValue());

        Assert.assertEquals(1, values.getValues().size());
        Assert.assertFalse(values.isEmpty());

        notificationsEditorWidget.setValue(values);

        Assert.assertEquals(1, values.getValues().size());
        Assert.assertEquals(1, notificationsEditorWidget.getValue().getValues().size());

        HTMLInputElement input = getFieldValue(NotificationsEditorWidget.class,
                                               notificationsEditorWidget,
                                               "notificationTextBox");
        Assert.assertEquals("1 notifications", input.value);
    }

    @Test
    public void testShowNotificationsDialog() {
        values.setValues(new ArrayList<>());
        notificationsEditorWidget.setValue(values);

        setFieldValue(notificationWidget, "view", notificationWidgetViewImpl);
        setFieldValue(notificationsEditorWidget, "notificationWidget", notificationWidget);

        notificationsEditorWidget.showNotificationsDialog();
        verify(notificationWidget, times(1)).show();
    }

    @Test
    public void testReadOnly() {

        setFieldValue(notificationWidgetViewImpl, "addButton", new HTMLButtonElement());
        setFieldValue(notificationWidgetViewImpl, "saveButton", new HTMLButtonElement());

        setFieldValue(notificationWidget, "view", notificationWidgetViewImpl);
        setFieldValue(notificationsEditorWidget, "notificationWidget", notificationWidget);

        notificationsEditorWidget.setReadOnly(true);

        boolean readOnly = getFieldValue(NotificationWidgetViewImpl.class,
                                         notificationWidgetViewImpl,
                                         "readOnly");

        HTMLButtonElement addButton = getFieldValue(NotificationWidgetViewImpl.class,
                                                    notificationWidgetViewImpl,
                                                    "addButton");
        HTMLButtonElement saveButton = getFieldValue(NotificationWidgetViewImpl.class,
                                                     notificationWidgetViewImpl,
                                                     "saveButton");

        Assert.assertTrue(readOnly);
        Assert.assertTrue(addButton.disabled);
        Assert.assertTrue(saveButton.disabled);
    }
}
