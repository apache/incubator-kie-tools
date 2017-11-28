package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.list.DropDownImageListEditor;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class ColumnTypeEditorTest {

    @Mock DropDownImageListEditor<ColumnType> columnType;
    @Mock Widget columnTypeWidget;
    private ColumnTypeEditor presenter;

    @Before
    public void setup() {
        presenter = spy(new ColumnTypeEditor(columnType));
        final SafeUri uri = mock(SafeUri.class);
        doReturn(uri).when(presenter).getImageUri(any(ColumnType.class));
        when(columnType.asWidget()).thenReturn(columnTypeWidget);
    }

    @Test
    public void testAsWidget() {
        assertEquals(columnTypeWidget, presenter.asWidget());
    }
    
    @Test
    public void testInit() {
        presenter.init();
        verify(columnType, times(1)).setImageSize(anyString(), anyString());
        verify(columnType, times(1)).setEntries(any(Collection.class));
        verify(columnType, times(0)).setHelpContent(anyString(), anyString(), any(Placement.class));
        verify(columnType, times(0)).isEditMode(anyBoolean());
    }


    @Test
    public void testAddHelpContent() {
        final Placement p = mock(Placement.class);
        presenter.addHelpContent("t1", "c1", p);
        verify(columnType, times(1)).setHelpContent("t1", "c1", p);
        verify(columnType, times(0)).setImageSize(anyString(), anyString());
        verify(columnType, times(0)).setEntries(any(Collection.class));
        verify(columnType, times(0)).isEditMode(anyBoolean());
    }
    
    @Test
    public void testColumnType() {
        assertEquals(columnType, presenter.columnType());
    }

    @Test
    public void testEnableEditMode() {
        presenter.isEditMode(true);
        verify(columnType, times(1)).isEditMode(true);
        verify(columnType, times(0)).setHelpContent(anyString(), anyString(), any(Placement.class));
        verify(columnType, times(0)).setImageSize(anyString(), anyString());
        verify(columnType, times(0)).setEntries(any(Collection.class));
    }

    @Test
    public void testDisableEditMode() {
        presenter.isEditMode(false);
        verify(columnType, times(1)).isEditMode(false);
        verify(columnType, times(0)).setHelpContent(anyString(), anyString(), any(Placement.class));
        verify(columnType, times(0)).setImageSize(anyString(), anyString());
        verify(columnType, times(0)).setEntries(any(Collection.class));
    }

    @Test
    public void testSetValue() {
        final DataColumnDef col1 = mock(DataColumnDef.class);
        when(col1.getId()).thenReturn("col1");
        when(col1.getColumnType()).thenReturn(ColumnType.LABEL);
        presenter.setValue(col1);
        verify(columnType, times(1)).setEntries(any(Collection.class));
        verify(columnType, times(0)).isEditMode(anyBoolean());
        verify(columnType, times(1)).setHelpContent(anyString(), anyString(), any(Placement.class));
        verify(columnType, times(0)).setImageSize(anyString(), anyString());
    }

    @Test
    public void testSetOriginalColumnType() {
        ColumnType type = mock(ColumnType.class);
        presenter.setOriginalColumnType(type);
        verify(columnType, times(1)).setEntries(any(Collection.class));
        verify(columnType, times(0)).isEditMode(anyBoolean());
        verify(columnType, times(0)).setHelpContent(anyString(), anyString(), any(Placement.class));
        verify(columnType, times(0)).setImageSize(anyString(), anyString());
    }

}
