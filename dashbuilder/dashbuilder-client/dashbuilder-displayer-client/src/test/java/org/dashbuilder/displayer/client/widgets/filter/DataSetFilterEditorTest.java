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
package org.dashbuilder.displayer.client.widgets.filter;

import javax.enterprise.event.Event;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.CoreFunctionType;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.dataset.filter.FilterFactory;
import org.dashbuilder.displayer.client.events.ColumnFilterDeletedEvent;
import org.dashbuilder.displayer.client.events.DataSetFilterChangedEvent;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetFilterEditorTest {

    @Mock
    ColumnFilterEditor columnFilterEditor;

    @Mock
    DataSetFilterEditor.View filterView;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    SyncBeanDef<ColumnFilterEditor> columnFilterEditorBeanDef;

    @Mock
    DataSetMetadata metadata;

    @Mock
    Event<DataSetFilterChangedEvent> changedEvent;

    @Before
    public void setup() {
        when(beanManager.lookupBean(ColumnFilterEditor.class)).thenReturn(columnFilterEditorBeanDef);
        when(columnFilterEditorBeanDef.newInstance()).thenReturn(columnFilterEditor);
        when(metadata.getNumberOfColumns()).thenReturn(3);
        when(metadata.getColumnId(0)).thenReturn("column1");
        when(metadata.getColumnId(1)).thenReturn("column2");
        when(metadata.getColumnId(2)).thenReturn("column3");
        when(metadata.getColumnType(0)).thenReturn(ColumnType.LABEL);
        when(metadata.getColumnType(1)).thenReturn(ColumnType.NUMBER);
        when(metadata.getColumnType(2)).thenReturn(ColumnType.DATE);
    }

    @Test
    public void testViewInitialization() {
        DataSetFilter filter = new DataSetFilter();
        ColumnFilter filter1 = FilterFactory.equalsTo("column1", "Test");
        filter.addFilterColumn(filter1);

        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, beanManager, changedEvent);
        filterEditor.init(filter, metadata);

        assertEquals(filterView, filterEditor.view);
        verify(filterView).showNewFilterHome();
        verify(filterView).addColumn("column1");
        verify(filterView).addColumn("column2");
        verify(filterView).addColumn("column3");
        verify(filterView, times(filter.getColumnFilterList().size())).addColumnFilterEditor(any(ColumnFilterEditor.class));
    }

    @Test
    public void testWorkflow() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, beanManager, changedEvent);
        filterEditor.init(null, metadata);
        reset(filterView);

        filterEditor.onNewFilterStart();
        verify(filterView).showColumnSelector();

        filterEditor.onNewFilterCancel();
        verify(filterView).showNewFilterHome();
    }

    @Test
    public void testCreateLabelFilter() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, beanManager, changedEvent);
        filterEditor.init(null, metadata);
        reset(filterView);
        when(filterView.getSelectedColumnIndex()).thenReturn(0);

        filterEditor.onCreateFilter();
        verify(changedEvent).fire(any(DataSetFilterChangedEvent.class));

        DataSetFilter filter = filterEditor.getFilter();
        assertNotNull(filter);
        assertEquals(filter.getColumnFilterList().size(), 1);

        ColumnFilter expected = FilterFactory.createCoreFunctionFilter("column1", ColumnType.LABEL, CoreFunctionType.NOT_EQUALS_TO);
        assertEquals(filter.getColumnFilterList().get(0), expected);
    }

    @Test
    public void testCreateDateFilter() {
        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, beanManager, changedEvent);
        filterEditor.init(null, metadata);
        reset(filterView);
        when(filterView.getSelectedColumnIndex()).thenReturn(2);

        filterEditor.onCreateFilter();
        verify(changedEvent).fire(any(DataSetFilterChangedEvent.class));

        DataSetFilter filter = filterEditor.getFilter();
        assertNotNull(filter);
        assertEquals(filter.getColumnFilterList().size(), 1);

        ColumnFilter expected = FilterFactory.createCoreFunctionFilter("column3", ColumnType.DATE, CoreFunctionType.TIME_FRAME);
        assertEquals(filter.getColumnFilterList().get(0), expected);
    }

    @Test
    public void testDeleteDuplicatedFilters() {
        ColumnFilter columnFilter1 = FilterFactory.notEqualsTo("column", "val1");
        ColumnFilter columnFilter2 = FilterFactory.notEqualsTo("column", "val1");
        ColumnFilterEditor columnFilterEditor1 = mock(ColumnFilterEditor.class);
        ColumnFilterEditor columnFilterEditor2 = mock(ColumnFilterEditor.class);
        when(columnFilterEditor1.getFilter()).thenReturn(columnFilter1);
        when(columnFilterEditor2.getFilter()).thenReturn(columnFilter2);
        when(columnFilterEditorBeanDef.newInstance()).thenReturn(columnFilterEditor1, columnFilterEditor2);

        DataSetFilter filter = new DataSetFilter();
        filter.addFilterColumn(columnFilter1, columnFilter2);

        DataSetFilterEditor filterEditor = new DataSetFilterEditor(filterView, beanManager, changedEvent);
        filterEditor.init(filter, metadata);
        filterEditor.onColumnFilterDeleted(new ColumnFilterDeletedEvent(columnFilterEditor2));
        filterEditor.onColumnFilterDeleted(new ColumnFilterDeletedEvent(columnFilterEditor1));

        assertEquals(filter.getColumnFilterList().size(), 0);
        verify(filterView).removeColumnFilterEditor(columnFilterEditor1);
        verify(filterView).removeColumnFilterEditor(columnFilterEditor2);
        verify(beanManager).destroyBean(columnFilterEditor1);
        verify(beanManager).destroyBean(columnFilterEditor2);
        verify(changedEvent, times(2)).fire(any(DataSetFilterChangedEvent.class));
    }
}
