package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.ClientDataSetManager;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetMetadataCallback;
import org.dashbuilder.dataset.def.DataSetDef;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetSummaryTest {
    
    @Mock DataSetClientServices dataSetClientServices;
    @Mock ClientDataSetManager clientDataSetManager;
    @Mock EventSourceMock<ErrorEvent> errorEvent;
    @Mock DataSetDef dataSetDef;
    @Mock DataSetSummary.View view;
    
    private DataSetSummary presenter;
    final DataSetMetadata dataSetMetadata = mock(DataSetMetadata.class);
    
    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(dataSetDef.isCacheEnabled()).thenReturn(true);
        when(dataSetDef.isPushEnabled()).thenReturn(true);
        when(dataSetDef.isRefreshAlways()).thenReturn(true);
        when(dataSetDef.getRefreshTime()).thenReturn("1second");
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                DataSetMetadataCallback callback = (DataSetMetadataCallback) invocationOnMock.getArguments()[1];
                callback.callback(dataSetMetadata);
                return null;
            }
        }).when(dataSetClientServices).fetchMetadata(anyString(), any(DataSetMetadataCallback.class));
        
        // The presenter instance to test.
        final Widget widget = mock(Widget.class);
        when(view.asWidget()).thenReturn(widget);
        presenter = new DataSetSummary(dataSetClientServices, errorEvent, view);
    }

    @Test
    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(0)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }
    
    @Test
    public void testAsWidget() throws Exception {
        assertEquals(view.asWidget(), presenter.asWidget());
    }
    
    @Test
    public void testShow() throws Exception {
        final int estimatedSize = 100;
        final int rowCount = 10;
        when(dataSetMetadata.getEstimatedSize()).thenReturn(estimatedSize);
        when(dataSetMetadata.getNumberOfRows()).thenReturn(rowCount);
        presenter.show(dataSetDef);
        verify(view, times(0)).init(presenter);
        verify(view, times(1)).showStatusPanel(true, true, true);
        verify(view, times(1)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(1)).showSizePanel(anyString(), anyString());
    }

    // TODO: @Test - Mock NumberFormat
    public void testHumanReadableByteCount() throws Exception {
        long estimatedSize = (long) 2024.2;
        String s = presenter.humanReadableByteCount(estimatedSize);
        // assertEquals("", s);
    }

    // TODO: @Test - Mock NumberFormat
    public void testHumanReadableRowCount() throws Exception {
        long rows = (long) 1000;
        String s = presenter.humanReadableRowCount(rows);
        // assertEquals("", s);
    }

    @Test
    public void testShowLoadingIcon() {
        presenter.showLoadingIcon();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(1)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }

    @Test
    public void testShowErrorIcon() {
        presenter.showErrorIcon();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(1)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }

    @Test
    public void testShowClientRuntimeError() {
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        presenter.showError("uuid1", error);
        verify(errorEvent, times(1)).fire(any(ErrorEvent.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(0)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }

    @Test
    public void testShowThrowable() {
        final Throwable error = mock(Throwable.class);
        presenter.showError("uuid1", error);
        verify(errorEvent, times(1)).fire(any(ErrorEvent.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(0)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }

    @Test
    public void testShowErrorMessage() {
        presenter.showError("uuid1", "errorMessage");
        verify(errorEvent, times(1)).fire(any(ErrorEvent.class));
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).showStatusPanel(anyBoolean(), anyBoolean(), anyBoolean());
        verify(view, times(0)).showSizePanelIcon(any(IconType.class), anyString(), anyString(), anyBoolean());
        verify(view, times(0)).showSizePanel(anyString(), anyString());
    }

}
