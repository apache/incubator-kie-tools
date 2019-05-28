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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.ReassignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.ReassignmentsEditorWidget;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.event.ReassignmentEvent;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.bpmn.definition.property.reassignment.ReassignmentTypeListValue;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.tables.SimpleTable;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ReassignmentWidgetViewImplTest extends ReflectionUtilsTest {

    @GwtMock
    private ReassignmentWidgetViewImpl reassignmentWidgetViewImpl;

    @GwtMock
    private ReassignmentWidget presenter;

    @GwtMock
    private ReassignmentWidgetView view;

    @GwtMock
    private ReassignmentEditorWidget reassignmentEditorWidget;

    @GwtMock
    private ReassignmentEditorWidgetViewImpl reassignmentEditorWidgetImpl;

    @GwtMock
    private SimpleTable<ReassignmentRow> table;

    @GwtMock
    private BaseModal modal;

    private ListDataProvider<ReassignmentRow> dataProvider;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);
        ListDataProvider temp = new ListDataProvider();

        dataProvider = spy(temp);
        doNothing().when(dataProvider).addDataDisplay(any(HasData.class));
        doNothing().when(dataProvider).flush();
        doNothing().when(dataProvider).refresh();

        setFieldValue(reassignmentWidgetViewImpl, "modal", modal);
        setFieldValue(reassignmentWidgetViewImpl, "table", table);
        setFieldValue(reassignmentWidgetViewImpl, "dataProvider", dataProvider);

        doCallRealMethod().when(reassignmentWidgetViewImpl).init(any(ReassignmentWidgetView.Presenter.class), anyList());
        doCallRealMethod().when(reassignmentWidgetViewImpl).delete(any(ReassignmentRow.class));
        doCallRealMethod().when(reassignmentWidgetViewImpl).hide();
        doCallRealMethod().when(reassignmentWidgetViewImpl).save();
        doCallRealMethod().when(reassignmentWidgetViewImpl).onSubscription(any(ReassignmentEvent.class));
        doCallRealMethod().when(reassignmentWidgetViewImpl).addOrEdit(any(ReassignmentRow.class));

        doNothing().when(reassignmentWidgetViewImpl).initTable();
        when(reassignmentWidgetViewImpl.getParent()).thenReturn(reassignmentWidgetViewImpl);

        doCallRealMethod().when(dataProvider).addDataDisplay(table);
        doCallRealMethod().when(dataProvider).getList();
        doCallRealMethod().when(dataProvider).setList(anyList());
        doCallRealMethod().when(dataProvider).flush();
        doCallRealMethod().when(dataProvider).refresh();

        doCallRealMethod().when(reassignmentEditorWidget).createOrEdit(any(ReassignmentWidgetView.class),
                                                                       any(ReassignmentRow.class));

        doCallRealMethod().when(presenter).getValue();
        doCallRealMethod().when(presenter).save();
        doCallRealMethod().when(presenter).hide();
        doCallRealMethod().when(presenter).setValue(anyList());
        doCallRealMethod().when(presenter).setValue(anyList(), any(boolean.class));
        doCallRealMethod().when(presenter).setCallback(any(ReassignmentsEditorWidget.GetReassignmentsCallback.class));
    }

    @Test
    public void testRowCountZero() {
        reassignmentWidgetViewImpl.init(presenter, Collections.EMPTY_LIST);
        ListDataProvider<ReassignmentRow> dataProvider = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                                       reassignmentWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testRowCountOneAndTwo() {
        List<ReassignmentRow> rows = new ArrayList<>();
        rows.add(new ReassignmentRow());
        reassignmentWidgetViewImpl.init(presenter, rows);
        ListDataProvider<ReassignmentRow> dataProvider = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                                       reassignmentWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(1, dataProvider.getList().size());

        rows = new ArrayList<>();
        rows.add(new ReassignmentRow());
        reassignmentWidgetViewImpl.init(presenter, rows);
        Assert.assertEquals(1, dataProvider.getList().size());

        rows.add(new ReassignmentRow());
        reassignmentWidgetViewImpl.init(presenter, rows);
        Assert.assertEquals(2, dataProvider.getList().size());

        reassignmentWidgetViewImpl.init(presenter, Collections.EMPTY_LIST);
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testDeleteRow() {

        ReassignmentRow row4 = new ReassignmentRow();
        row4.setId(4);

        reassignmentWidgetViewImpl.init(presenter, generateRows());
        ListDataProvider<ReassignmentRow> dataProvider = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                                       reassignmentWidgetViewImpl,
                                                                       "dataProvider");
        Assert.assertEquals(4, dataProvider.getList().size());
        reassignmentWidgetViewImpl.delete(row4);
        Assert.assertEquals(3, dataProvider.getList().size());
    }

    @Test
    public void testHide() {
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        ListDataProvider<ReassignmentRow> dataProvider = getFieldValue(ReassignmentWidgetViewImpl.class,
                                                                       reassignmentWidgetViewImpl,
                                                                       "dataProvider");
        reassignmentWidgetViewImpl.hide();
        Assert.assertEquals(0, dataProvider.getList().size());
    }

    @Test
    public void testSave() {
        setFieldValue(presenter, "view", view);
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        Assert.assertEquals(4, dataProvider.getList().size());
        reassignmentWidgetViewImpl.save();
        Assert.assertEquals(4, dataProvider.getList().size());
        Assert.assertEquals(4, presenter.getValue().size());
    }

    @Test
    public void testSaveAndHide() {
        final ReassignmentTypeListValue[] value = new ReassignmentTypeListValue[1];
        ReassignmentsEditorWidget.GetReassignmentsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        reassignmentWidgetViewImpl.save();
        reassignmentWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    @Test
    public void testOnAddNewValueAndSave() {
        dataProvider.setList(new ArrayList<>());
        final ReassignmentTypeListValue[] value = new ReassignmentTypeListValue[1];
        ReassignmentsEditorWidget.GetReassignmentsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(reassignmentWidgetViewImpl, "editor", reassignmentEditorWidget);
        setFieldValue(reassignmentEditorWidget, "view", reassignmentEditorWidgetImpl);
        ReassignmentRow newRow = new ReassignmentRow();
        newRow.setId(111);
        reassignmentWidgetViewImpl.addOrEdit(newRow);
        reassignmentWidgetViewImpl.onSubscription(new ReassignmentEvent(newRow));

        reassignmentWidgetViewImpl.save();
        reassignmentWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(5, value[0].getValues().size());
    }

    @Test
    public void testOnAddNewValueAndClose() {
        dataProvider.setList(new ArrayList<>());
        final ReassignmentTypeListValue[] value = new ReassignmentTypeListValue[1];
        ReassignmentsEditorWidget.GetReassignmentsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(reassignmentWidgetViewImpl, "editor", reassignmentEditorWidget);
        setFieldValue(reassignmentEditorWidget, "view", reassignmentEditorWidgetImpl);
        ReassignmentRow newRow = new ReassignmentRow();
        newRow.setId(111);
        reassignmentWidgetViewImpl.addOrEdit(newRow);
        reassignmentWidgetViewImpl.onSubscription(new ReassignmentEvent(null));

        reassignmentWidgetViewImpl.save();
        reassignmentWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    @Test
    public void testOnEditValueAndSave() {
        dataProvider.setList(new ArrayList<>());
        final ReassignmentTypeListValue[] value = new ReassignmentTypeListValue[1];
        ReassignmentsEditorWidget.GetReassignmentsCallback callback = v -> value[0] = v;

        setFieldValue(presenter, "view", view);
        reassignmentWidgetViewImpl.init(presenter, generateRows());
        presenter.setCallback(callback);

        setFieldValue(reassignmentWidgetViewImpl, "editor", reassignmentEditorWidget);
        setFieldValue(reassignmentEditorWidget, "view", reassignmentEditorWidgetImpl);
        ReassignmentRow newRow = new ReassignmentRow();
        newRow.setId(1);
        reassignmentWidgetViewImpl.addOrEdit(newRow);
        reassignmentWidgetViewImpl.onSubscription(new ReassignmentEvent(newRow));

        reassignmentWidgetViewImpl.save();
        reassignmentWidgetViewImpl.hide();
        Assert.assertEquals(0, presenter.getValue().size());
        Assert.assertEquals(4, value[0].getValues().size());
    }

    private List<ReassignmentRow> generateRows() {
        List<ReassignmentRow> rows = new ArrayList<>();
        ReassignmentRow row1 = new ReassignmentRow();
        row1.setId(1);
        ReassignmentRow row2 = new ReassignmentRow();
        row2.setId(2);
        ReassignmentRow row3 = new ReassignmentRow();
        row3.setId(3);
        ReassignmentRow row4 = new ReassignmentRow();
        row4.setId(4);
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        rows.add(row4);
        return rows;
    }
}
