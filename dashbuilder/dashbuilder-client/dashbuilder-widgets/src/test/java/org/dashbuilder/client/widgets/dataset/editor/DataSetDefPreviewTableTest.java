package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetFactory;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.displayer.DisplayerSettings;
import org.dashbuilder.displayer.client.AbstractDisplayerTest;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefPreviewTableTest extends AbstractDisplayerTest {
    
    @Mock DataSetDefPreviewTable.View view;
    @Mock DataSetDef dataSetDef;
    
    private DataSetDefPreviewTable tested;
    final List<DataColumnDef> columnDefList = mock(List.class);
    final Displayer displayer = mock(Displayer.class);
    final DisplayerListener displayerListener = mock(DisplayerListener.class);
    final DisplayerSettings displayerSettings = mock(DisplayerSettings.class);
    
    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        
        when(columnDefList.isEmpty()).thenReturn(true);
        when(dataSetDef.clone()).thenReturn(dataSetDef);
        tested = new DataSetDefPreviewTable(displayerLocator, clientServices, view);
    }

    @Test
    public void testInit() throws Exception {
        tested.init();
        verify(view, times(1)).init(tested);
        verify(view, times(0)).setDisplayer(any(IsWidget.class));
        verify(view, times(0)).clear();
    }

    @Test
    public void testClear() throws Exception {
        tested.tableDisplayer = displayer;
        tested.clear();
        assertNull(tested.tableDisplayer);
        verify(view, times(0)).init(tested);
        verify(view, times(0)).setDisplayer(any(IsWidget.class));
        verify(view, times(1)).clear();
    }

    // TODO - David: @Test - Do the test after DisplayerLocator#get() removed.
    public void testShow() throws Exception {
        when(displayerSettings.getRenderer()).thenReturn("gwtcharts");
        tested.show(dataSetDef, columnDefList, displayerListener);
        assertNotNull(tested.tableDisplayer);
        verify(view, times(0)).init(tested);
        verify(view, times(1)).setDisplayer(any(IsWidget.class));
        verify(view, times(1)).clear();
    }

    @Test
    public void testDraw() throws Exception {
        tested.tableDisplayer = displayer;
        tested.draw(displayerListener);
        verify(displayer, times(1)).addListener(displayerListener);
        verify(displayer, times(1)).draw();
        verify(view, times(1)).setDisplayer(any(IsWidget.class));
        verify(view, times(0)).init(tested);
        verify(view, times(0)).clear();
    }

    @Test
    public void testCSVConfig() throws Exception {
        DataSetDef dataSetDef = DataSetDefFactory.newCSVDataSetDef()
                .datePattern("dd/MM/yyyy")
                .numberPattern("#,###")
                .allColumns(true)
                .buildDef();

        DataSet dataSet = DataSetFactory.newDataSetBuilder()
                .date("date")
                .number("number")
                .row(new Date(), 1d)
                .buildDataSet();

        when(dataSetLookupServices.lookupDataSet(any(), any())).thenReturn(dataSet);
        tested.show(dataSetDef, null, displayerListener);

        ArgumentCaptor<Displayer> argumentCaptor = ArgumentCaptor.forClass(Displayer.class);
        verify(displayerListener).onDataLoaded(argumentCaptor.capture());
        Displayer displayer = argumentCaptor.getValue();
        DisplayerSettings settings = displayer.getDisplayerSettings();
        assertEquals(settings.getColumnSettings("date").getValuePattern(), "dd/MM/yyyy");
        assertEquals(settings.getColumnSettings("number").getValuePattern(), "#,###");
        assertEquals(settings.isTableColumnPickerEnabled(), false);
    }

    @Test
    public void testSQLConfig() throws Exception {
        DataSetDef dataSetDef = DataSetDefFactory.newSQLDataSetDef()
                .column("date", ColumnType.DATE)
                .column("number", ColumnType.NUMBER)
                .buildDef();

        DataSet dataSet = DataSetFactory.newDataSetBuilder()
                .date("date")
                .number("number")
                .row(new Date(), 1d)
                .buildDataSet();

        when(dataSetLookupServices.lookupDataSet(any(), any())).thenReturn(dataSet);
        tested.show(dataSetDef, null, displayerListener);

        ArgumentCaptor<Displayer> argumentCaptor = ArgumentCaptor.forClass(Displayer.class);
        verify(displayerListener).onDataLoaded(argumentCaptor.capture());
        Displayer displayer = argumentCaptor.getValue();
        DisplayerSettings settings = displayer.getDisplayerSettings();
        assertFalse(settings.isTableColumnPickerEnabled());
        assertEquals(settings.getTablePageSize(), 10);
        assertTrue(settings.isTableSortEnabled());
    }
}
