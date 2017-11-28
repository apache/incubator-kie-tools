package org.dashbuilder.client.widgets.dataset.editor.column;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataColumnDefEditorTest {

    @Mock ValueBoxEditor<String> id;
    @Mock ColumnTypeEditor columnType;
    @Mock DataColumnDefEditor.View view;
    @Mock Widget idWidget;
    private DataColumnDefEditor presenter;

    @Before
    public void setup() {
        presenter = new DataColumnDefEditor(id, columnType, view);
        presenter.providerType = DataSetProviderType.SQL;
        when(id.asWidget()).thenReturn(idWidget);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(columnType, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
    }

    @Test
    public void testRemoveFromParent() {
        presenter.removeFromParent();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(idWidget, times(1)).removeFromParent();
    }

    @Test
    public void testEnableEditMode() {
        presenter.isEditMode(true);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(columnType, times(1)).isEditMode(true);
    }

    @Test
    public void testEnableEditModeSpecificForBeanTypes() {
        presenter.providerType = DataSetProviderType.BEAN;
        presenter.isEditMode(true);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(columnType, times(1)).isEditMode(false);
    }

    @Test
    public void testDisableEditMode() {
        presenter.isEditMode(false);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(columnType, times(1)).isEditMode(false);
    }

    @Test
    public void testSetOriginalColumnType() {
        ColumnType type = mock(ColumnType.class);
        presenter.setOriginalColumnType(type);
        verify(columnType, times(1)).setOriginalColumnType(type);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(ValueBoxEditor.View.class), any(IsWidget.class));
        verify(columnType, times(0)).isEditMode(anyBoolean());
    }
    
    @Test
    public void testId() {
        assertEquals(id, presenter.id());
    }

    @Test
    public void testColumnType() {
        assertEquals(columnType, presenter.columnType());
    }

}
