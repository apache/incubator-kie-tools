package org.dashbuilder.dataset.editor.client.screens;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.event.EditDataSetEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.explorer.DataSetExplorer;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefExplorerScreenTest {

    @Mock PlaceRequest placeRequest;
    @Mock PlaceManager placeManager;
    @Mock EventSourceMock<NotificationEvent> notification;
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock DataSetExplorer explorerWidget;
    @Mock DataSetDefVfsServices dataSetDefVfsServices;
    @Mock Caller<DataSetDefVfsServices> services;
    
    @InjectMocks
    DataSetDefExplorerScreen presenter;
    
    @Before
    public void setup() throws Exception {
        presenter.services = services;
        assertEquals(explorerWidget, presenter.getView());

        doAnswer(invocationOnMock -> {
            Command callback = (Command ) invocationOnMock.getArguments()[1];
            callback.execute();
            return null;
        }).when(placeManager).tryClosePlace(any(), any());

        doAnswer(invocationOnMock -> {
            RemoteCallback callback = (RemoteCallback) invocationOnMock.getArguments()[0];
            callback.callback(mock(Path.class));
            return null;
        }).when(services).call(any(RemoteCallback.class));
    }
    
    @Test
    public void testNewDataSet() {
        presenter.newDataSet();
        verify(placeManager).tryClosePlace(eq(new DefaultPlaceRequest("DataSetDefWizard")), any(Command.class));
        verify(placeManager).goTo("DataSetDefWizard");
    }
    
    // Cannot mock constructor for PathPlaceRequest, but having the classcast exception when mocking it implies that placeManager#goTo is called....
    @Test(expected = ClassCastException.class)
    public void testOnEditDataSetEvent() {
        final DataSetDef def = mock(DataSetDef.class);
        final EditDataSetEvent editDataSetEvent = mock(EditDataSetEvent.class);
        when(editDataSetEvent.getDef()).thenReturn(def);
        presenter.onEditDataSetEvent(editDataSetEvent);
        verify(placeManager, times(1)).goTo(any(PathPlaceRequest.class));
    }

    @Test
    public void testOnErrorEvent() {
        final ErrorEvent errorDataSetEvent = mock(ErrorEvent.class);
        when(errorDataSetEvent.getMessage()).thenReturn("errorMessage");
        presenter.onErrorEvent(errorDataSetEvent);
        verify(errorPopupPresenter, times(1)).showMessage("errorMessage");
    }
}