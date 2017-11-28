package org.dashbuilder.dataset.editor.client.screens;

import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberView;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataSetAuthoringHomePresenterTest {
    
    @Mock DataSetAuthoringHomePresenter.View view;
    @Mock PlaceManager placeManager;
    @Mock DataSetClientServices dataSetClientServices;
    
    private DataSetAuthoringHomePresenter presenter;
    
    @Before
    public void setup() throws Exception {
        final List<DataSetDef> dataSetDefList = mock(List.class);
        when(dataSetDefList.size()).thenReturn(5);
        
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                RemoteCallback<List<DataSetDef>> callback = (RemoteCallback<List<DataSetDef>>) invocationOnMock.getArguments()[0];
                callback.callback(dataSetDefList);
                return null;
            }
        }).when(dataSetClientServices).getPublicDataSetDefs(any(RemoteCallback.class));
        
        // The presenter instance to test.
        presenter = new DataSetAuthoringHomePresenter(view, placeManager, dataSetClientServices);

    }
    
    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).setDataSetCount(5);
    }

    @Test
    public void testGetView() {
        final UberView<DataSetAuthoringHomePresenter> view = presenter.getView();
        assertEquals(this.view, view);
    }

    @Test
    public void testNewDataSet() {
        presenter.newDataSet();
        verify(placeManager, times(1)).goTo("DataSetDefWizard");
    }

    @Test
    public void testDataSetRegistered() {
        int count = presenter.getDataSetCount();
        presenter.onDataSetDefRegisteredEvent(new DataSetDefRegisteredEvent(null));
        assertEquals(presenter.getDataSetCount(), count+1);
        verify(view).setDataSetCount(count+1);
    }

    @Test
    public void testDataSetRemoved() {
        int count = presenter.getDataSetCount();
        presenter.onDataSetDefRemovedEvent(new DataSetDefRemovedEvent(null));
        assertEquals(presenter.getDataSetCount(), count-1);
        verify(view).setDataSetCount(count-1);
    }
}