package org.dashbuilder.client.widgets.dataset.editor.attributes;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.common.client.event.ValueChangeEvent;
import org.gwtbootstrap3.client.ui.constants.Placement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefRefreshIntervalEditorTest {
    
    @Mock EventSourceMock<ValueChangeEvent<String>> valueChangeEvent;
    @Mock DataSetDefRefreshIntervalEditor.View view;
    
    private DataSetDefRefreshIntervalEditor presenter;

    @Before
    public void setup() {
        presenter = new DataSetDefRefreshIntervalEditor(view, valueChangeEvent);
    }

    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(6)).addIntervalTypeItem(anyString());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).setQuantity(anyDouble());
        verify(view, times(0)).getQuantity();
        verify(view, times(0)).setEnabled(anyBoolean());
    }

    @Test
    public void testAddHelpContent() {
        final Placement p = mock(Placement.class);
        presenter.addHelpContent("t1", "c1", p);
        verify(view, times(1)).addHelpContent("t1", "c1", p);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
        verify(view, times(0)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).setQuantity(anyDouble());
        verify(view, times(0)).getQuantity();
        verify(view, times(0)).setEnabled(anyBoolean());
    }

    @Test
    public void testSetEnabled() {
        presenter.setEnabled(true);
        verify(view, times(1)).setEnabled(true);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
        verify(view, times(0)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).setQuantity(anyDouble());
        verify(view, times(0)).getQuantity();
    }

    @Test
    public void testSetDisabled() {
        presenter.setEnabled(false);
        verify(view, times(1)).setEnabled(false);
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
        verify(view, times(0)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).setQuantity(anyDouble());
        verify(view, times(0)).getQuantity();
    }

    @Test
    public void testSetValue() {
        final String value = "1second";
        presenter.setValue(value);
        verify(view, times(1)).setQuantity(1d);
        verify(view, times(1)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).setEnabled(anyBoolean());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).getQuantity();
    }

    @Test
    public void testSetAnotherValue() {
        final String value = "10minute";
        presenter.setValue(value);
        verify(view, times(1)).setQuantity(10d);
        verify(view, times(1)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).setEnabled(anyBoolean());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
        verify(view, times(0)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).getQuantity();
    }

    @Test
    public void testGetValue() {
        when(view.getQuantity()).thenReturn(1d);
        when(view.getSelectedIntervalTypeIndex()).thenReturn(0);
        final String value = presenter.getValue();
        assertEquals("1 second", value);
        verify(view, times(1)).getQuantity();
        verify(view, times(1)).getSelectedIntervalTypeIndex();
        verify(view, times(0)).setQuantity(10d);
        verify(view, times(0)).setSelectedIntervalType(anyInt());
        verify(view, times(0)).setEnabled(anyBoolean());
        verify(view, times(0)).addHelpContent(anyString(), anyString(), any(Placement.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).addIntervalTypeItem(anyString());
    }
}
