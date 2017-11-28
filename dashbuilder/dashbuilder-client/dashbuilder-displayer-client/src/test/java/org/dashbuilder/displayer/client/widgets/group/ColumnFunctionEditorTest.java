/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.displayer.client.widgets.group;

import java.util.List;
import javax.enterprise.event.Event;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.displayer.client.events.GroupFunctionChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionDeletedEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ColumnFunctionEditorTest {

    @Mock
    ColumnFunctionEditor.View view;

    @Mock
    ColumnDetailsEditor columnDetailsEditor;

    @Mock
    Event<GroupFunctionChangedEvent> changeEvent;

    @Mock
    Event<GroupFunctionDeletedEvent> deleteEvent;

    @Mock
    DataSetMetadata metadata;

    ColumnFunctionEditor presenter;

    @Before
    public void init() {
        presenter = new ColumnFunctionEditor(view, columnDetailsEditor, changeEvent, deleteEvent);

        when(metadata.getNumberOfColumns()).thenReturn(3);
        when(metadata.getColumnId(0)).thenReturn("column1");
        when(metadata.getColumnId(1)).thenReturn("column2");
        when(metadata.getColumnId(2)).thenReturn("column3");
        when(metadata.getColumnType(0)).thenReturn(ColumnType.LABEL);
        when(metadata.getColumnType(1)).thenReturn(ColumnType.NUMBER);
        when(metadata.getColumnType(2)).thenReturn(ColumnType.DATE);
        when(metadata.getColumnType("column1")).thenReturn(ColumnType.LABEL);
        when(metadata.getColumnType("column2")).thenReturn(ColumnType.NUMBER);
        when(metadata.getColumnType("column3")).thenReturn(ColumnType.DATE);
    }

    @Test
    public void testViewInitialization() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", null);
        presenter.init(metadata, groupFunction, null, "Title", false, false);

        verify(view).setDeleteOptionEnabled(false);
        verify(view, never()).setDeleteOptionEnabled(true);

        verify(view).setColumnSelectorTitle("Title");
        verify(view).clearColumnSelector();
        verify(view, times(3)).addColumnItem(anyString());
        verify(view).setSelectedColumnIndex(0);

        verify(view).setFunctionSelectorEnabled(false);
        verify(view, never()).setFunctionSelectorEnabled(true);
    }

    @Test
    public void testLabelTarget() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", null);
        presenter.init(metadata, groupFunction, ColumnType.LABEL, "Title", true, true);

        verify(view).clearColumnSelector();
        verify(view, times(1)).addColumnItem(anyString());
        verify(view).setSelectedColumnIndex(0);

        verify(view).setFunctionSelectorEnabled(false);
        verify(view, never()).setFunctionSelectorEnabled(true);
    }

    @Test
    public void testNumericTarget() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", null);
        presenter.init(metadata, groupFunction, ColumnType.NUMBER, "Title", true, true);

        verify(view).clearColumnSelector();
        verify(view, times(3)).addColumnItem(anyString());
        verify(view).setSelectedColumnIndex(0);

        verify(view).setFunctionSelectorEnabled(true);
        verify(view, never()).setVoidFunctionEnabled(true);
        verify(view, times(presenter.getSupportedFunctionTypes().size())).addFunctionItem(any(AggregateFunctionType.class));
        verify(view, never()).setSelectedFunctionIndex(anyInt());
    }

    @Test
    public void testUnspecifiedTarget() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", AggregateFunctionType.COUNT);
        presenter.init(metadata, groupFunction, null, "Title", true, true);

        verify(view).clearColumnSelector();
        verify(view, times(3)).addColumnItem(anyString());
        verify(view).setSelectedColumnIndex(0);

        List<AggregateFunctionType> supportedFunctions = presenter.getSupportedFunctionTypes();
        verify(view).setFunctionSelectorEnabled(true);
        verify(view).setVoidFunctionEnabled(true);
        verify(view, times(supportedFunctions.size())).addFunctionItem(any(AggregateFunctionType.class));
        verify(view).setSelectedFunctionIndex(anyInt());
    }

    @Test
    public void testSelectColumn() {
        when(view.getSelectedColumnId()).thenReturn("column2");

        GroupFunction groupFunction = new GroupFunction("column1", "column1", AggregateFunctionType.COUNT);
        presenter.init(metadata, groupFunction, null, "Title", true, true);
        presenter.onColumnSelected();

        assertEquals(presenter.getGroupFunction().getSourceId(), "column2");
        verify(changeEvent).fire(any(GroupFunctionChangedEvent.class));
    }

    @Test
    public void testUpdateFunctionsAvailable() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", AggregateFunctionType.COUNT);
        presenter.init(metadata, groupFunction, null, "Title", true, true);

        List<AggregateFunctionType> typeListColumn1 = presenter.getSupportedFunctionTypes();
        List<AggregateFunctionType> typeListLabel = presenter.getSupportedFunctionTypes(ColumnType.LABEL);
        assertEquals(typeListColumn1.size(), typeListLabel.size());
        verify(view).clearFunctionSelector();
        verify(view, times(typeListLabel.size())).addFunctionItem(any(AggregateFunctionType.class));

        reset(view);
        when(view.getSelectedColumnId()).thenReturn("column2");
        presenter.onColumnSelected();

        List<AggregateFunctionType> typeListColumn2 = presenter.getSupportedFunctionTypes();
        List<AggregateFunctionType> typeListNumber = presenter.getSupportedFunctionTypes(ColumnType.NUMBER);
        assertEquals(typeListColumn2.size(), typeListNumber.size());
        assertEquals(presenter.getGroupFunction().getSourceId(), "column2");
        verify(view).clearFunctionSelector();
        verify(view, times(typeListNumber.size())).addFunctionItem(any(AggregateFunctionType.class));
        verify(changeEvent).fire(any(GroupFunctionChangedEvent.class));
    }

    @Test
    public void testSelectFunction() {
        when(view.getSelectedFunctionIndex()).thenReturn(1);

        GroupFunction groupFunction = new GroupFunction("column1", "column1", AggregateFunctionType.COUNT);
        presenter.init(metadata, groupFunction, null, "Title", true, true);
        presenter.onFunctionSelected();

        List<AggregateFunctionType> supportedFunctions = presenter.getSupportedFunctionTypes();
        assertEquals(presenter.getGroupFunction().getFunction(), supportedFunctions.get(1));
        verify(changeEvent).fire(any(GroupFunctionChangedEvent.class));
    }

    @Test
    public void testDelete() {
        GroupFunction groupFunction = new GroupFunction("column1", "column1", AggregateFunctionType.COUNT);
        presenter.init(metadata, groupFunction, null, "Title", true, true);
        presenter.delete();

        verify(deleteEvent).fire(any(GroupFunctionDeletedEvent.class));
    }
}