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

package org.drools.workbench.screens.guided.template.client.editor;

import java.util.Collections;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.guided.template.shared.TemplateModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.AbstractCellFactory;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.mockito.Mock;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({AbstractCellFactory.class, VerticalDecoratedTemplateDataGridWidget.class, DateTimeFormat.class})
public class TemplateDataTableWidgetTest {

    @Mock
    private EventBus eventBusMock;

    @Mock
    private AsyncPackageDataModelOracle oracleMock;

    @Mock
    private TemplateModel modelMock;

    @Mock
    private TemplateDataCellValueFactory cellValueFactoryMock;

    private TemplateDataTableWidget dataTableWidget;

    @Before
    public void setUp() throws Exception {
        dataTableWidget = new TemplateDataTableWidget(modelMock, oracleMock, false, eventBusMock);
        dataTableWidget.cellValueFactory = cellValueFactoryMock;
    }

    @Test
    public void testOnInsertRow() {
        final int index = 123;
        final List<String> rowData = Collections.singletonList("abc");
        final InsertRowEvent insertRowEvent = mock(InsertRowEvent.class);
        when(insertRowEvent.getIndex()).thenReturn(index);
        when(cellValueFactoryMock.makeRowData()).thenReturn(rowData);

        dataTableWidget.onInsertRow(insertRowEvent);

        verify(modelMock).addRow(eq(index),
                                 eq(rowData.toArray(new String[1])));
    }
}
