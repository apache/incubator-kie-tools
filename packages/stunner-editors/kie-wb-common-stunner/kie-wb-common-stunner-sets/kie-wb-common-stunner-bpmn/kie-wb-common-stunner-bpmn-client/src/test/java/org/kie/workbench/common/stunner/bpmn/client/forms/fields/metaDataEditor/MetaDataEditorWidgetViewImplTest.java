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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MetaDataEditorWidgetViewImplTest {

    protected static final String ATTRIBUTES = "att1ßval1Øatt2ßval2Øatt3ßval3,val4";

    @Mock
    MetaDataEditorWidgetView.Presenter presenter;

    @GwtMock
    private Button button;

    @Mock
    private TableCellElement attributeth;

    @Mock
    private TableCellElement valueth;

    @GwtMock
    private ListWidget<MetaDataRow, MetaDataListItemWidgetViewImpl> metaDataRows;

    private MetaDataEditorWidgetViewImpl view;

    private List<MetaDataRow> rows;

    protected Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Before
    public void setUp() {
        GwtMockito.initMocks(this);
        view = GWT.create(MetaDataEditorWidgetViewImpl.class);
        view.metaDataRows = metaDataRows;
        view.addButton = button;
        view.attributeth = attributeth;
        view.valueth = valueth;
        view.notification = notification;
        doCallRealMethod().when(view).setMetaDataRows(any(List.class));
        doCallRealMethod().when(view).init(any(MetaDataEditorWidgetView.Presenter.class));
        doCallRealMethod().when(view).handleAddButton(any(ClickEvent.class));
        doCallRealMethod().when(view).getMetaDataRows();
        doCallRealMethod().when(view).getMetaDataWidget(anyInt());
        doCallRealMethod().when(view).getMetaDataRowsCount();
        doCallRealMethod().when(view).setValue(anyString(),
                                               anyBoolean());
        doCallRealMethod().when(view).setValue(anyString());
        doCallRealMethod().when(view).getValue();
        doCallRealMethod().when(view).doSetValue(anyString(),
                                                 anyBoolean(),
                                                 anyBoolean());
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        doCallRealMethod().when(view).initView();
        doCallRealMethod().when(view).addValueChangeHandler(any());
        doCallRealMethod().when(view).isDuplicateAttribute(anyString());
        rows = new ArrayList<>();
        rows.add(new MetaDataRow("attName", null));
        rows.add(new MetaDataRow("attName2", null));
    }

    @Test
    public void testInit() {
        view.init(presenter);
        verify(button,
               times(1)).setIcon(IconType.PLUS);
    }

    @Test
    public void testHandleAddVarButton() {
        view.init(presenter);
        view.handleAddButton(mock(ClickEvent.class));
        verify(presenter,
               times(1)).addAttribute();
    }

    @Test
    public void testGetMetaDataRows() {
        when(metaDataRows.getValue()).thenReturn(rows);
        assertEquals(rows,
                     view.getMetaDataRows());
    }

    @Test
    public void testGetMetaDataCountEmpty() {
        when(metaDataRows.getValue()).thenReturn(new ArrayList<>());
        assertEquals(0,
                     view.getMetaDataRowsCount());
    }

    @Test
    public void testGetMetaDataCount() {
        when(metaDataRows.getValue()).thenReturn(rows);
        assertEquals(2,
                     view.getMetaDataRowsCount());
    }

    @Test
    public void testGetMetaDataWidget() {
        view.getMetaDataWidget(0);
        verify(metaDataRows).getComponent(0);
    }

    @Test
    public void testGetMetaDataWidget2() {
        view.getMetaDataWidget(123);
        verify(metaDataRows).getComponent(123);
    }

    @Test
    public void testSetValue() {
        view.init(presenter);

        view.setValue(ATTRIBUTES,
                      true);
        verify(view,
               times(1)).doSetValue(ATTRIBUTES,
                                    true,
                                    false);
    }

    @Test
    public void testSetValueString() {
        view.init(presenter);
        view.setValue(ATTRIBUTES);
        verify(view,
               times(1)).doSetValue(ATTRIBUTES,
                                    false,
                                    false);
        assertEquals(ATTRIBUTES, view.getValue());
    }

    @Test
    public void testDoSetValueNotInitialize() {
        view.doSetValue(ATTRIBUTES,
                        true,
                        false);
        verify(view,
               times(0)).initView();
    }

    @Test
    public void testDoSetValueInitialize() {
        doNothing().when(view).initView();
        view.doSetValue(ATTRIBUTES,
                        true,
                        true);
        verify(view,
               times(1)).initView();
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(button,
               times(1)).setEnabled(false);
        for (int i = 0; i < metaDataRows.getValue().size(); i++) {
            verify(view.getMetaDataWidget(i),
                   times(1)).setReadOnly(true);
        }
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(button,
               times(1)).setEnabled(true);
        for (int i = 0; i < metaDataRows.getValue().size(); i++) {
            verify(view.getMetaDataWidget(i),
                   times(1)).setReadOnly(false);
        }
    }

    @Test
    public void testDoSave() {
        doCallRealMethod().when(view).doSave();
        view.init(presenter);
        view.setValue(ATTRIBUTES);
        view.doSave();
        verify(view,
               times(1)).setValue(ATTRIBUTES,
                                  false);
    }

    @Test
    public void testInitView() {
        view.init(presenter);
        view.initView();
        verify(view,
               times(1)).setMetaDataRows(new ArrayList<>());
    }

    @Test
    public void testAddValueChangeHandler() {
        view.init(presenter);
        ValueChangeHandler handler = valueChangeHandler -> valueChangeHandler.toString();

        view.addValueChangeHandler(handler);
        verify(view,
               times(1)).addHandler(handler, ValueChangeEvent.getType());
    }

    @Test
    public void testIsDuplicateAttribute() {
        view.init(presenter);

        when(presenter.isDuplicateAttribute("test")).thenReturn(false);
        assertFalse(view.isDuplicateAttribute("test"));

        verify(presenter,
               times(1)).isDuplicateAttribute("test");
    }
}
