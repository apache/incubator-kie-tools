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
package org.dashbuilder.displayer.client.widgets;

import java.util.Arrays;
import java.util.List;
import javax.enterprise.event.Event;

import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.DataSetLookupConstraints;
import org.dashbuilder.dataset.DataSetLookupFactory;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.dashbuilder.dataset.group.ColumnGroup;
import org.dashbuilder.dataset.group.GroupFunction;
import org.dashbuilder.dataset.impl.DataSetMetadataImpl;
import org.dashbuilder.displayer.client.events.DataSetLookupChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionChangedEvent;
import org.dashbuilder.displayer.client.events.GroupFunctionDeletedEvent;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.dashbuilder.displayer.client.widgets.group.ColumnFunctionEditor;
import org.dashbuilder.displayer.client.widgets.group.DataSetGroupDateEditor;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.dashbuilder.dataset.filter.FilterFactory.*;
import static org.dashbuilder.dataset.ColumnType.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetLookupEditorTest {

    public static final String POPULATION_UUID = "population";

    public static final String REVENUE_UUID = "revenue";

    public static final DataSetDef POPULATION_DSET = DataSetDefFactory.newStaticDataSetDef()
            .uuid(POPULATION_UUID)
            .name("Population")
            .label("continent")
            .label("country")
            .number("population")
            .date("year")
            .buildDef();

    public static final DataSetDef REVENUE_DSET = DataSetDefFactory.newStaticDataSetDef()
            .uuid(REVENUE_UUID)
            .name("Revenue")
            .label("company")
            .label("sector")
            .number("revenue")
            .date("year")
            .buildDef();

    public static final List<DataSetDef> DATA_SET_LIST = Arrays.asList(POPULATION_DSET, REVENUE_DSET);

    public static final DataSetMetadata POPULATION_META = new DataSetMetadataImpl(
            POPULATION_DSET, POPULATION_UUID, 0, 4,
            Arrays.asList("continent", "country", "population", "year"),
            Arrays.asList(LABEL, LABEL, NUMBER, DATE), 0);

    public static final DataSetMetadata REVENUE_META = new DataSetMetadataImpl(
            POPULATION_DSET, REVENUE_UUID, 0, 4,
            Arrays.asList("company", "sector", "revenue", "year"),
            Arrays.asList(LABEL, LABEL, NUMBER, DATE), 0);

    public static final DataSetLookupConstraints DATA_2D_MULTIPLE = new DataSetLookupConstraints()
            .setGroupRequired(true)
            .setGroupColumn(true)
            .setMaxColumns(10)
            .setMinColumns(2)
            .setExtraColumnsAllowed(true)
            .setExtraColumnsType(ColumnType.NUMBER)
            .setGroupsTitle("Categories")
            .setColumnsTitle("Series")
            .setColumnTypes(new ColumnType[]{
                    LABEL,
                    NUMBER});

    public static final DataSetLookupConstraints DATA_2D_FIXED = new DataSetLookupConstraints()
            .setGroupRequired(true)
            .setGroupColumn(true)
            .setMaxColumns(2)
            .setMinColumns(2)
            .setExtraColumnsAllowed(false)
            .setGroupsTitle("Categories")
            .setColumnsTitle("Series")
            .setColumnTypes(new ColumnType[]{
                    LABEL,
                    NUMBER});

    public static final DataSetLookupConstraints DATA_MULTIPLE = new DataSetLookupConstraints()
            .setGroupAllowed(true)
            .setGroupRequired(false)
            .setMaxColumns(-1)
            .setMinColumns(1)
            .setExtraColumnsAllowed(true)
            .setGroupsTitle("Rows")
            .setColumnsTitle("Columns")
            .setColumnTypes(new ColumnType[]{
                    LABEL,
                    NUMBER});

    public static final DataSetLookupConstraints DATA_MULTIPLE_NO_GROUP = new DataSetLookupConstraints()
            .setGroupAllowed(false)
            .setGroupRequired(false)
            .setMaxColumns(-1)
            .setMinColumns(1)
            .setExtraColumnsAllowed(true)
            .setGroupsTitle("Rows")
            .setColumnsTitle("Columns")
            .setColumnTypes(new ColumnType[]{
                    LABEL,
                    NUMBER});

    @Mock
    DataSetLookupEditor.View view;

    @Mock
    Event<DataSetLookupChangedEvent> event;

    @Mock
    SyncBeanManager beanManager;

    @Mock
    SyncBeanDef<ColumnFunctionEditor> columnFunctionEditorBeanDef;

    @Mock
    ColumnFunctionEditor columnFunctionEditor;

    @Mock
    DataSetFilterEditor filterEditor;

    @Mock
    DataSetGroupDateEditor groupDateEditor;

    @Mock
    DataSetClientServices clientServices;

    DataSetLookupEditor presenter = null;

    @Before
    public void init() throws Exception {
        presenter = new DataSetLookupEditor(view, beanManager, filterEditor, groupDateEditor, clientServices, event);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                RemoteCallback<List<DataSetDef>> callback = (RemoteCallback<List<DataSetDef>>) invocationOnMock.getArguments()[0];
                callback.callback(DATA_SET_LIST);
                return null;
            }
        }).when(clientServices).getPublicDataSetDefs(any(RemoteCallback.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DataSetMetadataCallback callback = (DataSetMetadataCallback) invocationOnMock.getArguments()[1];
                callback.callback(POPULATION_META);
                return null;
            }
        }).when(clientServices).fetchMetadata(eq(POPULATION_UUID), any(DataSetMetadataCallback.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                DataSetMetadataCallback callback = (DataSetMetadataCallback) invocationOnMock.getArguments()[1];
                callback.callback(REVENUE_META);
                return null;
            }
        }).when(clientServices).fetchMetadata(eq(REVENUE_UUID), any(DataSetMetadataCallback.class));

        when(beanManager.lookupBean(ColumnFunctionEditor.class)).thenReturn(columnFunctionEditorBeanDef);
        when(columnFunctionEditorBeanDef.newInstance()).thenReturn(columnFunctionEditor);
    }

    @Test
    public void testNonExistingLookup() {
        presenter.init(DATA_2D_FIXED, null);

        verify(view).clearDataSetSelector();
        verify(view).enableDataSetSelectorHint();
        verify(view).addDataSetItem("Population", POPULATION_UUID);
        verify(view).addDataSetItem("Revenue", REVENUE_UUID);
        verify(view, never()).setSelectedDataSetIndex(anyInt());

        verify(view, never()).setFilterEnabled(true);
        verify(view, never()).setGroupEnabled(true);
        verify(view, never()).setColumnsSectionEnabled(true);
        verify(view, never()).setAddColumnOptionEnabled(true);
    }

    @Test
    public void testExistingLookup() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .filter(greaterThan(100))
                .group("country")
                .column("country", "Country")
                .column("population", AggregateFunctionType.SUM, "Total")
                .buildLookup());

        verify(view).clearDataSetSelector();
        verify(view, never()).enableDataSetSelectorHint();
        verify(view).addDataSetItem("Population", POPULATION_UUID);
        verify(view).addDataSetItem("Revenue", REVENUE_UUID);
        verify(view, times(1)).setSelectedDataSetIndex(anyInt());

        verify(view).setFilterEnabled(true);
        verify(filterEditor).init(presenter.getDataSetLookup().getFirstFilterOp(), POPULATION_META);

        verify(view).setGroupEnabled(true);
        verify(view, never()).setGroupByDateEnabled(true);
        verify(view).setGroupColumnSelectorTitle("Categories");
        verify(view, never()).enableGroupColumnSelectorHint();
        verify(groupDateEditor, never()).init(any(ColumnGroup.class));

        verify(view).clearGroupColumnSelector();
        verify(view).addGroupColumnItem("continent");
        verify(view).addGroupColumnItem("country");
        verify(view).addGroupColumnItem("year");
        verify(view).addGroupColumnItem("population");

        verify(view).setColumnsSectionEnabled(true);
        verify(view, never()).setGroupByDateEnabled(true);
        verify(view).setColumnSectionTitle("Series");
        verify(view).setAddColumnOptionEnabled(false);
        verify(view, never()).setAddColumnOptionEnabled(true);

        GroupFunction gf = presenter.getFirstGroupFunctions().get(1);
        verify(columnFunctionEditor).init(POPULATION_META, gf, NUMBER, null, true, false);
        verify(view).addColumnEditor(any(ColumnFunctionEditor.class));
    }

    @Test
    public void testFromNonExistingLookup() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .filter(greaterThan(100))
                .group("country")
                .column("country", "Country")
                .column("population", AggregateFunctionType.SUM, "Total")
                .buildLookup());

        verify(view, never()).enableDataSetSelectorHint();

        reset(view);
        presenter.init(DATA_2D_FIXED, null);
        verify(view).enableDataSetSelectorHint();
    }

    @Test
    public void testDataSetDefFilter() {

        presenter.setDataSetDefFilter(new DataSetLookupEditor.DataSetDefFilter() {
            public boolean accept(DataSetDef def) {
                return def.getUUID().equals(REVENUE_UUID);
            }
        });

        presenter.init(DATA_2D_FIXED, null);

        verify(view).clearDataSetSelector();
        verify(view, never()).addDataSetItem("Population", POPULATION_UUID);
        verify(view).addDataSetItem("Revenue", REVENUE_UUID);
    }

    @Test
    public void testDateGroup() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .filter(greaterThan(100))
                .group("year")
                .column("year", "Year")
                .column("population", AggregateFunctionType.SUM, "Total")
                .buildLookup());

        verify(view).setGroupEnabled(true);
        verify(view).setGroupByDateEnabled(true);
        verify(view, never()).enableGroupColumnSelectorHint();
        verify(groupDateEditor).init(any(ColumnGroup.class));
    }

    @Test
    public void testGroupAllowed() {
        presenter.init(DATA_MULTIPLE, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .column("year")
                .column("population")
                .buildLookup());

        verify(view).setGroupEnabled(true);
        verify(view).enableGroupColumnSelectorHint();
        verify(view).setAddColumnOptionEnabled(true);
        verify(event, never()).fire(any(DataSetLookupChangedEvent.class));
    }

    @Test
    public void testDeleteColumns() {
        DataSetLookup lookup = DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .column("year")
                .column("population")
                .buildLookup();

        GroupFunction year = lookup.getLastGroupOp().getGroupFunction("year");
        presenter.init(DATA_MULTIPLE, lookup);

        reset(view);
        presenter.onColumnFunctionDeleted(new GroupFunctionDeletedEvent(year));

        verify(view).clearColumnList();
        verify(view, times(1)).addColumnEditor(any(ColumnFunctionEditor.class));
        verify(event).fire(any(DataSetLookupChangedEvent.class));
    }

    @Test
    public void testGroupNotAllowed() {
        presenter.init(DATA_MULTIPLE_NO_GROUP, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .column("year")
                .column("population")
                .buildLookup());

        verify(view, never()).setGroupEnabled(true);
        verify(view, never()).enableGroupColumnSelectorHint();
        verify(event, never()).fire(any(DataSetLookupChangedEvent.class));
    }

    @Test
    public void testAutoCreateRequiredGroup() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .filter(greaterThan(100))
                .column("country")
                .column("population", AggregateFunctionType.SUM)
                .buildLookup());

        assertEquals(presenter.getFirstGroupColumnId(), "continent");
        verify(event).fire(any(DataSetLookupChangedEvent.class));
    }

    @Test
    public void testDataSetSelected() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .group("country")
                .column("country")
                .column("population", AggregateFunctionType.SUM)
                .buildLookup());

        when(view.getSelectedDataSetId()).thenReturn(REVENUE_UUID);
        presenter.onDataSetSelected();
        verify(event).fire(any(DataSetLookupChangedEvent.class));

        DataSetLookup newLookup = presenter.getDataSetLookup();
        assertEquals(newLookup.getDataSetUUID(), REVENUE_UUID);
        assertEquals(presenter.getFirstGroupColumnId(), "company");
        assertEquals(presenter.getFirstGroupFunctions().size(), 2);
        assertEquals(presenter.getFirstGroupFunctions().get(0).getSourceId(), "company");
        assertEquals(presenter.getFirstGroupFunctions().get(1).getSourceId(), "revenue");
        assertEquals(presenter.getFirstGroupFunctions().get(0).getFunction(), null);
        assertEquals(presenter.getFirstGroupFunctions().get(1).getFunction(), AggregateFunctionType.SUM);
    }

    @Test
    public void testGroupColumnSelected() {
        presenter.init(DATA_2D_FIXED, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .group("country")
                .column("country")
                .column("population", AggregateFunctionType.SUM)
                .buildLookup());

        when(view.getSelectedGroupColumnId()).thenReturn("continent");
        presenter.onGroupColumnSelected();
        verify(event).fire(any(DataSetLookupChangedEvent.class));

        assertEquals(presenter.getFirstGroupColumnId(), "continent");
        assertEquals(presenter.getFirstGroupFunctions().size(), 2);
        assertEquals(presenter.getFirstGroupFunctions().get(0).getSourceId(), "continent");
        assertEquals(presenter.getFirstGroupFunctions().get(1).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(0).getFunction(), null);
        assertEquals(presenter.getFirstGroupFunctions().get(1).getFunction(), AggregateFunctionType.SUM);
    }

    @Test
    public void testGroupColumnDeselected() {
        presenter.init(DATA_MULTIPLE, DataSetLookupFactory.newDataSetLookupBuilder()
                .dataset(POPULATION_UUID)
                .group("country")
                .column("country")
                .column("population", AggregateFunctionType.SUM)
                .buildLookup());

        when(view.getSelectedGroupColumnId()).thenReturn(null);
        presenter.onGroupColumnSelected();
        verify(event).fire(any(DataSetLookupChangedEvent.class));

        assertEquals(presenter.getFirstGroupColumnId(), null);
        assertEquals(presenter.getFirstGroupFunctions().size(), 2);
        assertEquals(presenter.getFirstGroupFunctions().get(0).getSourceId(), "country");
        assertEquals(presenter.getFirstGroupFunctions().get(1).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(0).getFunction(), null);
        assertEquals(presenter.getFirstGroupFunctions().get(1).getFunction(), null);
    }

    @Test
    public void testAddColumns() {

        DataSetLookup lookup = DATA_2D_MULTIPLE.newDataSetLookup(POPULATION_META);
        presenter.init(DATA_2D_MULTIPLE, lookup);

        verify(view).setAddColumnOptionEnabled(true);
        presenter.onAddColumn();
        verify(event).fire(any(DataSetLookupChangedEvent.class));

        assertEquals(presenter.getFirstGroupFunctions().size(), 3);
        assertEquals(presenter.getFirstGroupFunctions().get(2).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(2).getColumnId(), "population_2");
        assertEquals(presenter.getFirstGroupFunctions().get(2).getFunction(), AggregateFunctionType.SUM);

        presenter.onAddColumn();
        assertEquals(presenter.getFirstGroupFunctions().size(), 4);
        assertEquals(presenter.getFirstGroupFunctions().get(3).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(3).getColumnId(), "population_3");
        assertEquals(presenter.getFirstGroupFunctions().get(3).getFunction(), AggregateFunctionType.SUM);

        reset(event);
        GroupFunction gf = new GroupFunction("population", "population_2", AggregateFunctionType.SUM);
        presenter.onColumnFunctionDeleted(new GroupFunctionDeletedEvent(gf));
        verify(event).fire(any(DataSetLookupChangedEvent.class));
        assertEquals(presenter.getFirstGroupFunctions().size(), 3);
        assertEquals(presenter.getFirstGroupFunctions().get(2).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(2).getColumnId(), "population_3");
        assertEquals(presenter.getFirstGroupFunctions().get(2).getFunction(), AggregateFunctionType.SUM);

        presenter.onAddColumn();
        assertEquals(presenter.getFirstGroupFunctions().size(), 4);
        assertEquals(presenter.getFirstGroupFunctions().get(3).getSourceId(), "population");
        assertEquals(presenter.getFirstGroupFunctions().get(3).getColumnId(), "population_2");
        assertEquals(presenter.getFirstGroupFunctions().get(3).getFunction(), AggregateFunctionType.SUM);
    }

    @Test
    public void testColumnChanged() {
        DataSetLookup lookup = DATA_2D_MULTIPLE.newDataSetLookup(POPULATION_META);
        presenter.init(DATA_2D_MULTIPLE, lookup);

        GroupFunction gf = new GroupFunction("population", "population", AggregateFunctionType.SUM);
        presenter.onColumnFunctionChanged(new GroupFunctionChangedEvent(gf));
        assertEquals(gf.getColumnId(), "population_2");
    }
}
