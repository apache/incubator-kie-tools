package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.event.FilterChangedEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.client.events.DataSetFilterChangedEvent;
import org.dashbuilder.displayer.client.widgets.filter.DataSetFilterEditor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefFilterEditorTest {
    
    @Mock EventSourceMock<FilterChangedEvent> filterChangedEvent;
    @Mock DataSetDefFilterEditor.View view;
    @Mock DataSetDef dataSetDef;
    @Mock DataSetFilterEditor dataSetFilterEditor;
    @Mock DataSetMetadata dataSetMetadata = mock(DataSetMetadata.class);
    @Mock DataSetFilter filter1 = mock(DataSetFilter.class);
    @Mock DataSetFilter filter2 = mock(DataSetFilter.class);

    private DataSetDefFilterEditor tested;
    
    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        
        when(filter1.cloneInstance()).thenReturn(filter2);
        tested = new DataSetDefFilterEditor(view, dataSetFilterEditor, filterChangedEvent);
    }

    @Test
    public void testInit() throws Exception {
        verify(view).init(tested);
    }

    @Test
    public void testInitFilterEditor() throws Exception {
        tested.value = filter1;
        tested.init(dataSetMetadata);
        verify(view).setWidget(any(IsWidget.class));
        verify(dataSetFilterEditor).init(eq(filter2), eq(dataSetMetadata));
    }

    @Test
    public void testSetValue() throws Exception {
        tested.setValue(filter1);
        assertEquals(filter2, tested.value);
        verify(view, never()).setWidget(any(IsWidget.class));
        verify(dataSetFilterEditor, never()).init(any(DataSetFilter.class), any(DataSetMetadata.class));
    }

    @Test
    public void testOnValueChanged() throws Exception {
        DataSetFilter filter3 = mock(DataSetFilter.class);
        when(filter2.cloneInstance()).thenReturn(filter3);
        tested.value = filter1;
        tested.onValueChanged(new DataSetFilterChangedEvent(filter2));
        assertEquals(filter3, tested.value);
        verify(view, never()).setWidget(any(IsWidget.class));
        verify(dataSetFilterEditor, never()).init(any(DataSetFilter.class), any(DataSetMetadata.class));
        verify(filterChangedEvent).fire(any(FilterChangedEvent.class));
    }
}
