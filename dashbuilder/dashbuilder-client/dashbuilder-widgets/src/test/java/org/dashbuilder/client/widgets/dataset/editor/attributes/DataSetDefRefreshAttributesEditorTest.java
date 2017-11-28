package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.ToggleSwitchEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefRefreshAttributesEditorTest {

    @Mock ToggleSwitchEditor refreshAlways;
    @Mock DataSetDefRefreshIntervalEditor refreshTime;
    @Mock DataSetDefRefreshAttributesEditor.View view;
    private DataSetDefRefreshAttributesEditor presenter;
    
    @Before
    public void setup() {
        presenter = new DataSetDefRefreshAttributesEditor(refreshAlways, refreshTime, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(refreshTime, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(any(IsWidget.class), any(DataSetDefRefreshIntervalEditor.View.class));
        verify(view, times(1)).addRefreshEnabledButtonHandler(any(Command.class));
        verify(view, times(0)).setEnabled(anyBoolean());
    }
    
    @Test
    public void testRefreshAlways() {
        assertEquals(refreshAlways, presenter.refreshAlways());
    }

    @Test
    public void testRefreshTime() {
        assertEquals(refreshTime, presenter.refreshTime());
    }

    @Test
    public void testRefreshEnabledButtonHandlerDisabled() {
        presenter.isRefreshEnabled = false;
        presenter.refreshEnabledButtonHandler.execute();
        assertEquals(true, presenter.isRefreshEnabled());
        verify(refreshTime, times(1)).setEnabled(true);
        verify(refreshAlways, times(1)).setEnabled(true);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefRefreshIntervalEditor.View.class));
        verify(view, times(0)).addRefreshEnabledButtonHandler(any(Command.class));
    }

    @Test
    public void testRefreshEnabledButtonHandlerEnabled() {
        presenter.isRefreshEnabled = true;
        presenter.refreshEnabledButtonHandler.execute();
        assertEquals(false, presenter.isRefreshEnabled());
        verify(refreshTime, times(1)).setEnabled(false);
        verify(refreshAlways, times(1)).setEnabled(false);
        verify(view, times(1)).setEnabled(false);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefRefreshIntervalEditor.View.class));
        verify(view, times(0)).addRefreshEnabledButtonHandler(any(Command.class));
    }
    
    @Test
    public void testSetValueEnabled() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.getRefreshTime()).thenReturn("1 second");
        presenter.setValue(dataSetDef);
        assertEquals(true, presenter.isRefreshEnabled());
        verify(refreshTime, times(1)).setEnabled(true);
        verify(refreshAlways, times(1)).setEnabled(true);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefRefreshIntervalEditor.View.class));
        verify(view, times(0)).addRefreshEnabledButtonHandler(any(Command.class));
    }

    @Test
    public void testSetValueDisabled() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.getRefreshTime()).thenReturn(null);
        presenter.setValue(dataSetDef);
        assertEquals(false, presenter.isRefreshEnabled());
        verify(refreshTime, times(1)).setEnabled(false);
        verify(refreshAlways, times(1)).setEnabled(false);
        verify(view, times(1)).setEnabled(false);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).initWidgets(any(IsWidget.class), any(DataSetDefRefreshIntervalEditor.View.class));
        verify(view, times(0)).addRefreshEnabledButtonHandler(any(Command.class));
    }

}
