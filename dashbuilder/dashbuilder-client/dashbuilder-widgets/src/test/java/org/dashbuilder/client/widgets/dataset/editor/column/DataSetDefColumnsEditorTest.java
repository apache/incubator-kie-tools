package org.dashbuilder.client.widgets.dataset.editor.column;

import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetDefColumnsEditorTest {

    @Mock
    ColumnListEditor columnListEditor;
    
    private DataSetDefColumnsEditor presenter;

    @Before
    public void setup() {
        presenter = new DataSetDefColumnsEditor(columnListEditor);
    }

    @Test
    public void testSetAcceptableValues() {
        final List acceptableValues = mock(List.class);
        presenter.setAcceptableValues(acceptableValues);
        assertEquals(acceptableValues, presenter.acceptableValues);
        verify(columnListEditor, times(1)).setAcceptableValues(acceptableValues);
        verify(columnListEditor, times(0)).onValueRestricted(anyString());
        verify(columnListEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnListEditor, times(0)).setProviderType(any(DataSetProviderType.class));
    }

    @Test
    public void testOnValueRestricted() {
        final String v = "value";
        presenter.onValueRestricted(v);
        verify(columnListEditor, times(1)).onValueRestricted(v);
        verify(columnListEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnListEditor, times(0)).setAcceptableValues(any(List.class));
        verify(columnListEditor, times(0)).setProviderType(any(DataSetProviderType.class));
    }

    @Test
    public void testOnValueUnRestricted() {
        final String v = "value";
        presenter.onValueUnRestricted(v);
        verify(columnListEditor, times(1)).onValueUnRestricted(v);
        verify(columnListEditor, times(0)).onValueRestricted(anyString());
        verify(columnListEditor, times(0)).setAcceptableValues(any(List.class));
        verify(columnListEditor, times(0)).setProviderType(any(DataSetProviderType.class));
    }

    @Test
    public void testSetValueBeanType() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        when(dataSetDef.isAllColumnsEnabled()).thenReturn(false);
        presenter.acceptableValues = buildAcceptableValues();
        presenter.setValue(dataSetDef);
        verify(dataSetDef, times(0)).setColumns(any(List.class));
        verify(dataSetDef, times(0)).setAllColumnsEnabled(anyBoolean());
        verify(columnListEditor, times(1)).setProviderType(DataSetProviderType.BEAN);
        verify(columnListEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnListEditor, times(0)).onValueRestricted(anyString());
        verify(columnListEditor, times(0)).setAcceptableValues(any(List.class));
    }

    @Test
    public void testSetValueWithNotAllColumns() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.isAllColumnsEnabled()).thenReturn(false);
        presenter.acceptableValues = buildAcceptableValues();
        presenter.setValue(dataSetDef);
        verify(dataSetDef, times(0)).setColumns(any(List.class));
        verify(dataSetDef, times(0)).setAllColumnsEnabled(anyBoolean());
        verify(columnListEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(columnListEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnListEditor, times(0)).onValueRestricted(anyString());
        verify(columnListEditor, times(0)).setAcceptableValues(any(List.class));
    }

    @Test
    public void testSetValueWithAllColumns() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.isAllColumnsEnabled()).thenReturn(true);
        presenter.acceptableValues = buildAcceptableValues();
        presenter.setValue(dataSetDef);
        verify(dataSetDef, times(1)).setColumns(any(List.class));
        verify(dataSetDef, times(1)).setAllColumnsEnabled(false);
        verify(columnListEditor, times(1)).setProviderType(any(DataSetProviderType.class));
        verify(columnListEditor, times(0)).onValueUnRestricted(anyString());
        verify(columnListEditor, times(0)).onValueRestricted(anyString());
        verify(columnListEditor, times(0)).setAcceptableValues(any(List.class));
    }
    
    private List<DataColumnDef> buildAcceptableValues() {
        final DataColumnDef col1 = mock(DataColumnDef.class);
        when(col1.getId()).thenReturn("col1");
        when(col1.getColumnType()).thenReturn(ColumnType.LABEL);
        final List<DataColumnDef> acceptableValues = new ArrayList<DataColumnDef>();
        acceptableValues.add(col1);
        return acceptableValues;
    }
    
}
