package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.editor.ToggleSwitchEditor;
import org.dashbuilder.common.client.editor.ValueBoxEditor;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefClientCacheAttributesEditorTest {

    @Mock ToggleSwitchEditor pushEnabled;
    @Mock ValueBoxEditor<Integer> pushMaxSize;
    @Mock DataSetDefCacheAttributesEditorView view;
    private DataSetDefClientCacheAttributesEditor presenter;
    
    @Before
    public void setup() {
        presenter = new DataSetDefClientCacheAttributesEditor(pushEnabled, pushMaxSize, view);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(pushMaxSize, times(1)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(1)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(1)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).setValue(anyDouble());
        verify(view, times(0)).setEnabled(anyBoolean());
    }

    @Test
    public void testSetRange() {
        presenter.setRange(1d, 2d);
        verify(view, times(1)).setRange(1d, 2d);
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(0)).setValue(anyDouble());
        verify(view, times(0)).setEnabled(anyBoolean());
    }
    
    @Test
    public void testPushEnabled() {
        assertEquals(pushEnabled, presenter.pushEnabled());
    }

    @Test
    public void testPushMaxSize() {
        assertEquals(pushMaxSize, presenter.pushMaxSize());
    }
    
    @Test
    public void testSetValueEnabled() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.isPushEnabled()).thenReturn(true);
        presenter.setValue(dataSetDef);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(1)).setValue(anyDouble());
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
    }

    @Test
    public void testSetValueDisabled() {
        final DataSetDef dataSetDef = mock(DataSetDef.class);
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.isPushEnabled()).thenReturn(false);
        presenter.setValue(dataSetDef);
        verify(view, times(1)).setEnabled(false);
        verify(view, times(1)).setValue(anyDouble());
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
    }

    @Test
    public void testViewCallback() {
        final Double value = 1d;
        presenter.viewCallback.onValueChange(value);
        verify(pushMaxSize, times(1)).setValue(anyInt());
        verify(pushEnabled, times(0)).setValue(anyBoolean());
        verify(view, times(0)).setEnabled(anyBoolean());
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(0)).setValue(anyDouble());
    }

    @Test
    public void testViewCallbackNullified() {
        final Double value = null;
        presenter.viewCallback.onValueChange(value);
        verify(pushMaxSize, times(1)).setValue(100);
        verify(pushEnabled, times(0)).setValue(anyBoolean());
        verify(view, times(0)).setEnabled(anyBoolean());
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(0)).setValue(anyDouble());
    }
    
    public void testOnEnabledChangedEventUsingTrue() {
        final ValueChangeEvent<Boolean> event = mock(ValueChangeEvent.class);
        when(event.getContext()).thenReturn(pushEnabled);
        when(event.getValue()).thenReturn(true);
        presenter.onEnabledChangedEvent(event);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(0)).setValue(anyDouble());
    }

    public void testOnEnabledChangedEventUsingFalse() {
        final ValueChangeEvent<Boolean> event = mock(ValueChangeEvent.class);
        when(event.getContext()).thenReturn(pushEnabled);
        when(event.getValue()).thenReturn(false);
        presenter.onEnabledChangedEvent(event);
        verify(view, times(1)).setEnabled(false);
        verify(view, times(0)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).init(anyString(), anyString(), any(IsWidget.class), any(ValueBoxEditor.View.class));
        verify(view, times(0)).init(any(DataSetDefCacheAttributesEditorView.ViewCallback.class));
        verify(view, times(0)).setValue(anyDouble());
    }
}
