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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.NotificationRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.NotificationsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor.event.NotificationEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.notification.NotificationTypeListValue;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class NotificationWidgetViewImplTest extends ReflectionUtilsTest {

    @GwtMock
    private NotificationWidgetViewImpl notificationWidgetViewImpl;

    @GwtMock
    private NotificationWidget presenter;

    @GwtMock
    private NotificationWidgetView view;

    @GwtMock
    private NotificationEditorWidget notificationEditorWidget;

    @GwtMock
    private NotificationEditorWidgetViewImpl notificationEditorWidgetImpl;

    @GwtMock
    private SimpleTable<NotificationRow> table;

    @GwtMock
    private BaseModal modal;

    private ListDataProvider<NotificationRow> dataProvider;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);
        ListDataProvider temp = new ListDataProvider();

        dataProvider = spy(temp);
        doNothing().when(dataProvider).addDataDisplay(any(HasData.class));
        doNothing().when(dataProvider).flush();
        doNothing().when(dataProvider).refresh();

        setFieldValue(notificationWidgetViewImpl, "modal", modal);
        setFieldValue(notificationWidgetViewImpl, "table", table);
        setFieldValue(notificationWidgetViewImpl, "dataProvider", dataProvider);

        doCallRealMethod().when(notificationWidgetViewImpl).init(any(NotificationWidgetView.Presenter.class), anyList());
        doCallRealMethod().when(notificationWidgetViewImpl).delete(any(NotificationRow.class));
        doCallRealMethod().when(notificationWidgetViewImpl).hide();
        doCallRealMethod().when(notificationWidgetViewImpl).save();
        doCallRealMethod().when(notificationWidgetViewImpl).onSubscription(any(NotificationEvent.class));
        doCallRealMethod().when(notificationWidgetViewImpl).addOrEdit(any(NotificationRow.class));

        doNothing().when(notificationWidgetViewImpl).initTable();
        when(notificationWidgetViewImpl.getParent()).thenReturn(notificationWidgetViewImpl);

        doCallRealMethod().when(dataProvider).addDataDisplay(table);
        doCallRealMethod().when(dataProvider).getList();
        doCallRealMethod().when(dataProvider).setList(anyList());
        doCallRealMethod().when(dataProvider).flush();
        doCallRealMethod().when(dataProvider).refresh();

        doCallRealMethod().when(notificationEditorWidget).createOrEdit(any(NotificationWidgetView.class),
                                                                       any(NotificationRow.class));

        doCallRealMethod().when(presenter).getValue();
        doCallRealMethod().when(presenter).save();
        doCallRealMethod().when(presenter).hide();
        doCallRealMethod().when(presenter).setValue(anyList());
        doCallRealMethod().when(presenter).setValue(anyList(), any(boolean.class));
        doCallRealMethod().when(presenter).setCallback(any(NotificationsEditorWidget.GetNotificationsCallback.class));
    }

    @Test
    public void testRowCountZero() {
        notificationWidgetViewImpl.init(presenter, Collections.EMPTY_LIST);
        ListDataProvider<NotificationRow> dataProvider = getFieldValue(NotificationWidgetViewImpl.class,
                                                                       notificationWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testRowCountOneAndTwo() {
        List<NotificationRow> rows = new ArrayList<>();
        rows.add(new NotificationRow());
        notificationWidgetViewImpl.init(presenter, rows);
        ListDataProvider<NotificationRow> dataProvider = getFieldValue(NotificationWidgetViewImpl.class,
                                                                       notificationWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(1, dataProvider.getList().size());

        rows = new ArrayList<>();
        rows.add(new NotificationRow());
        notificationWidgetViewImpl.init(presenter, rows);
        Assert.assertEquals(1, dataProvider.getList().size());

        rows.add(new NotificationRow());
        notificationWidgetViewImpl.init(presenter, rows);
        Assert.assertEquals(2, dataProvider.getList().size());

        notificationWidgetViewImpl.init(presenter, Collections.EMPTY_LIST);
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testDeleteRow() {

        NotificationRow row4 = new NotificationRow();
        row4.setId(4);

        notificationWidgetViewImpl.init(presenter, generateRows());
        ListDataProvider<NotificationRow> dataProvider = getFieldValue(NotificationWidgetViewImpl.class,
                                                                       notificationWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(4, dataProvider.getList().size());
        notificationWidgetViewImpl.delete(row4);
        Assert.assertEquals(3, dataProvider.getList().size());
    }

    @Test
    public void testHide() {
        notificationWidgetViewImpl.init(presenter, generateRows());
        ListDataProvider<NotificationRow> dataProvider = getFieldValue(NotificationWidgetViewImpl.class,
                                                                       notificationWidgetViewImpl,
                                                                       "dataProvider");
        notificationWidgetViewImpl.hide();
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testSave() {
        setFieldValue(presenter, "view", view);
        notificationWidgetViewImpl.init(presenter, generateRows());
        Assert.assertEquals(4, dataProvider.getList().size());
        notificationWidgetViewImpl.save();
        Assert.assertEquals(4, dataProvider.getList().size());
        Assert.assertEquals(4, presenter.getValue().size());
    }

    @Test
    public void testSaveAndHide() {
        final NotificationTypeListValue[] value = new NotificationTypeListValue[1];
        NotificationsEditorWidget.GetNotificationsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        notificationWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        notificationWidgetViewImpl.save();
        notificationWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    @Test
    public void testOnAddNewValueAndSave() {
        dataProvider.setList(new ArrayList<>());
        final NotificationTypeListValue[] value = new NotificationTypeListValue[1];
        NotificationsEditorWidget.GetNotificationsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        notificationWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(notificationWidgetViewImpl, "editor", notificationEditorWidget);
        setFieldValue(notificationEditorWidget, "view", notificationEditorWidgetImpl);
        NotificationRow newRow = new NotificationRow();
        newRow.setId(111);
        notificationWidgetViewImpl.addOrEdit(newRow);
        notificationWidgetViewImpl.onSubscription(new NotificationEvent(newRow));

        notificationWidgetViewImpl.save();
        notificationWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(5, value[0].getValues().size());
    }

    @Test
    public void testOnAddNewValueAndClose() {
        dataProvider.setList(new ArrayList<>());
        final NotificationTypeListValue[] value = new NotificationTypeListValue[1];
        NotificationsEditorWidget.GetNotificationsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        notificationWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(notificationWidgetViewImpl, "editor", notificationEditorWidget);
        setFieldValue(notificationEditorWidget, "view", notificationEditorWidgetImpl);
        NotificationRow newRow = new NotificationRow();
        newRow.setId(111);
        notificationWidgetViewImpl.addOrEdit(newRow);
        notificationWidgetViewImpl.onSubscription(new NotificationEvent(null));

        notificationWidgetViewImpl.save();
        notificationWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    @Test
    public void testOnEditValueAndSave() {
        dataProvider.setList(new ArrayList<>());
        final NotificationTypeListValue[] value = new NotificationTypeListValue[1];
        NotificationsEditorWidget.GetNotificationsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        notificationWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(notificationWidgetViewImpl, "editor", notificationEditorWidget);
        setFieldValue(notificationEditorWidget, "view", notificationEditorWidgetImpl);
        NotificationRow newRow = new NotificationRow();
        newRow.setId(1);
        notificationWidgetViewImpl.addOrEdit(newRow);
        notificationWidgetViewImpl.onSubscription(new NotificationEvent(newRow));

        notificationWidgetViewImpl.save();
        notificationWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    private List<NotificationRow> generateRows() {
        List<NotificationRow> rows = new ArrayList<>();
        NotificationRow row1 = new NotificationRow();
        row1.setId(1);
        NotificationRow row2 = new NotificationRow();
        row2.setId(2);
        NotificationRow row3 = new NotificationRow();
        row3.setId(3);
        NotificationRow row4 = new NotificationRow();
        row4.setId(4);
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        return rows;
    }
}
