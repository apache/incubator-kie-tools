package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.events.DataSetDefModifiedEvent;
import org.dashbuilder.dataset.events.DataSetDefRegisteredEvent;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.enterprise.inject.Instance;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetExplorerTest {

    @Mock DataSetExplorer.View view;
    @Mock DataSetPanel dataSetPanel;
    @Mock DataSetDef dataSetDef;
    @Mock DataSetDef dataSetDefCustom;
    @Mock DataSetClientServices dataSetClientServices;
    Instance<DataSetPanel> panelInstances;
    
    private DataSetExplorer presenter;
    final List<DataSetDef> dataSetDefList = new ArrayList<DataSetDef>();

    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.isPublic()).thenReturn(true);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        
        when(dataSetDefCustom.getUUID()).thenReturn("uuid2");
        when(dataSetDefCustom.getName()).thenReturn("name2");
        when(dataSetDefCustom.isPublic()).thenReturn(true);
        when(dataSetDefCustom.getProvider()).thenReturn(() -> "CUSTOM");

        dataSetDefList.add(dataSetDef);
        dataSetDefList.add(dataSetDefCustom);

        final Widget widget = mock(Widget.class);
        when(view.asWidget()).thenReturn(widget);
        panelInstances = new MockInstance();
        
        // Client services method mocks.
        when(dataSetPanel.getDataSetDef()).thenReturn(dataSetDef);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                RemoteCallback<List<DataSetDef>> callback = (RemoteCallback<List<DataSetDef>>) invocationOnMock.getArguments()[0];
                callback.callback(dataSetDefList);
                return null;
            }
        }).when(dataSetClientServices).getPublicDataSetDefs(any(RemoteCallback.class));
        
        // The presenter instance to test.
        presenter = new DataSetExplorer(panelInstances, dataSetClientServices, view);
    }

    @Test
    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
    }

    @Test
    public void testAsWidget() throws Exception {
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testShow() throws Exception {
        presenter.show();
        assertEquals(1, presenter.panels.size());
        assertEquals(dataSetPanel, presenter.panels.get(0));
        verify(view, times(1)).clear();
        verify(view, times(1)).addPanel(any(DataSetPanel.View.class));
        verify(dataSetPanel, times(1)).show(dataSetDef, "dataSetsExplorerPanelGroup");
    }

    @Test
    public void testShowTwice() throws Exception {
        // Add same data set def again, but only one panel must be visible.
        dataSetDefList.add(dataSetDef);
        presenter.show();
        assertEquals(1, presenter.panels.size());
        assertEquals(dataSetPanel, presenter.panels.get(0));
        verify(view, times(1)).clear();
        verify(view, times(1)).addPanel(any(DataSetPanel.View.class));
        verify(dataSetPanel, times(1)).show(dataSetDef, "dataSetsExplorerPanelGroup");
    }

    @Test
    public void testOnDataSetDefRegisteredEvent() {
        final DataSetDefRegisteredEvent event = mock(DataSetDefRegisteredEvent.class);
        when(event.getDataSetDef()).thenReturn(dataSetDef);
        presenter.onDataSetDefRegisteredEvent(event);
        assertEquals(1, presenter.panels.size());
        assertEquals(dataSetPanel, presenter.panels.get(0));
        verify(view).clear();
        verify(view).addPanel(any(DataSetPanel.View.class));
        verify(dataSetPanel).show(dataSetDef, "dataSetsExplorerPanelGroup");
    }

    @Test
    public void testOnDataSetDefModifiedEvent() {
        final DataSetDefModifiedEvent event = mock(DataSetDefModifiedEvent.class);
        when(event.getOldDataSetDef()).thenReturn(dataSetDef);
        when(event.getNewDataSetDef()).thenReturn(dataSetDef);
        presenter.panels.add(dataSetPanel);
        presenter.onDataSetDefModifiedEvent(event);
        assertEquals(1, presenter.panels.size());
        assertEquals(dataSetPanel, presenter.panels.get(0));
        verify(view, times(0)).clear();
        verify(view, times(0)).addPanel(any(DataSetPanel.View.class));
        verify(dataSetPanel, times(1)).show(dataSetDef, "dataSetsExplorerPanelGroup");
        verify(dataSetPanel, times(1)).close();
    }

    @Test
    public void testOnDataSetDefRemovedEvent() {
        final DataSetDef removedDataSetDef = mock(DataSetDef.class);
        when(removedDataSetDef.getUUID()).thenReturn("removed");
        when(removedDataSetDef.getName()).thenReturn("removedDef");
        when(removedDataSetDef.isPublic()).thenReturn(true);
        when(removedDataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        final DataSetDefRemovedEvent event = mock(DataSetDefRemovedEvent.class);
        when(event.getDataSetDef()).thenReturn(removedDataSetDef);
        presenter.onDataSetDefRemovedEvent(event);
        assertEquals(1, presenter.panels.size());
        assertEquals(dataSetPanel, presenter.panels.get(0));
        verify(view, times(1)).clear();
        verify(view, times(1)).addPanel(any(DataSetPanel.View.class));
        verify(dataSetPanel, times(1)).show(dataSetDef, "dataSetsExplorerPanelGroup");
    }

    // Mockito complains when mocking Instance<U>, so let's create an empty implementation for it. It returns a single mocked DataSetPanel instance.
    private class MockInstance implements Instance<DataSetPanel> {

        @Override
        public Instance<DataSetPanel> select(Annotation... annotations) {
            return null;
        }

        @Override
        public <U extends DataSetPanel> Instance<U> select(Class<U> aClass, Annotation... annotations) {
            return (Instance<U>) dataSetPanel;
        }

        @Override
        public boolean isUnsatisfied() {
            return false;
        }

        @Override
        public boolean isAmbiguous() {
            return false;
        }

        @Override
        public void destroy(DataSetPanel dataSetPanel) {

        }

        @Override
        public Iterator<DataSetPanel> iterator() {
            return null;
        }

        @Override
        public DataSetPanel get() {
            return dataSetPanel;
        }
    }
}
