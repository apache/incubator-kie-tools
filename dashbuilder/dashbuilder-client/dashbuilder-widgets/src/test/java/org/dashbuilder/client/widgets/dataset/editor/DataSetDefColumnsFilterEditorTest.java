package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.column.DataSetDefColumnsEditor;
import org.dashbuilder.client.widgets.dataset.event.FilterChangedEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.ColumnFilter;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefColumnsFilterEditorTest {

    @Mock DataSetDefColumnsEditor columnsEditor;
    @Mock DataSetDefFilterEditor dataSetFilterEditor;
    @Mock DataSetDefColumnsFilterEditor.View view;
    @Mock DataSetDef dataSetDef;
    
    private DataSetDefColumnsFilterEditor tested;

    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        
        tested = new DataSetDefColumnsFilterEditor(columnsEditor, dataSetFilterEditor, view);
    }

    @Test
    public void testInit() throws Exception {
        tested.init();
        verify(view, times(1)).init(tested);
        verify(view, times(1)).initWidgets(any(IsWidget.class), any(DataSetDefFilterEditor.View.class));
        verify(view, times(0)).setMaxHeight(anyString());
    }

    @Test
    public void testSetMaxHeight() throws Exception {
        final String maxH = "100px";
        tested.setMaxHeight(maxH);
        verify(view, times(0)).init(tested);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefFilterEditor.View.class));
        verify(view, times(1)).setMaxHeight(maxH);
    }

    @Test
    public void testColumnListEditor() throws Exception {
        assertEquals(columnsEditor, tested.columnListEditor());
    }

    @Test
    public void testDataSetFilterEditor() throws Exception {
        assertEquals(dataSetFilterEditor, tested.dataSetFilter());
    }

    @Test
    public void testSetAcceptableValues() throws Exception {
        final List l = mock(List.class);
        tested.setAcceptableValues(l);
        verify(columnsEditor, times(1)).setAcceptableValues(l);
        verify(view, times(0)).init(tested);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefFilterEditor.View.class));
        verify(view, times(0)).setMaxHeight(anyString());
    }

    @Test
    public void testSetValue() throws Exception {
        DataSetFilter newFilter = mock(DataSetFilter.class);
        List<ColumnFilter> createColumnFilters = createColumnFilters("col1", "col2");
        when(newFilter.getColumnFilterList()).thenReturn(createColumnFilters);
        when(dataSetDef.getDataSetFilter()).thenReturn(newFilter);
        tested.setValue(dataSetDef);
        verify(columnsEditor, times(1)).onValueRestricted("col1");
        verify(columnsEditor, times(1)).onValueRestricted("col2");
        verify(columnsEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnsEditor, times(0)).setAcceptableValues(any(List.class));
        verify(view, times(0)).init(tested);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefFilterEditor.View.class));
        verify(view, times(0)).setMaxHeight(anyString());
    }

    @Test
    public void testOnFilterChangedEvent() throws Exception {
        DataSetFilter oldFilter = mock(DataSetFilter.class);
        List<ColumnFilter> oldCreateColumnFilters = createColumnFilters("col1", "col2", "col3");
        when(oldFilter.getColumnFilterList()).thenReturn(oldCreateColumnFilters);
        DataSetFilter newFilter = mock(DataSetFilter.class);
        List<ColumnFilter> createColumnFilters = createColumnFilters("col2");
        when(newFilter.getColumnFilterList()).thenReturn(createColumnFilters);
        when(dataSetDef.getDataSetFilter()).thenReturn(newFilter);
        FilterChangedEvent filterChangedEvent = mock(FilterChangedEvent.class);
        when(filterChangedEvent.getContext()).thenReturn(dataSetFilterEditor);
        when(filterChangedEvent.getOldFilter()).thenReturn(oldFilter);
        when(filterChangedEvent.getFilter()).thenReturn(newFilter);
        tested.onFilterChangedEvent(filterChangedEvent);
        verify(columnsEditor, times(1)).onValueRestricted("col2");
        verify(columnsEditor, times(1)).onValueUnRestricted("col1");
        verify(columnsEditor, times(1)).onValueUnRestricted("col3");
        verify(columnsEditor, times(0)).setAcceptableValues(any(List.class));
        verify(view, times(0)).init(tested);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefFilterEditor.View.class));
        verify(view, times(0)).setMaxHeight(anyString());
    }

    private List<ColumnFilter> createColumnFilters(String... columns) {
        List<ColumnFilter> result = new ArrayList<ColumnFilter>();
        for (String column : columns) {
            ColumnFilter cf = mock(ColumnFilter.class);
            when(cf.getColumnId()).thenReturn(column);
            result.add(cf);
        }
        return result;
    }
    
}
