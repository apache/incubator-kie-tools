package org.dashbuilder.client.widgets.dataset.explorer;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;

import org.dashbuilder.client.widgets.common.CustomDataSetProviderType;
import org.dashbuilder.client.widgets.common.DataSetEditorPlugin;
import org.dashbuilder.client.widgets.dataset.event.EditDataSetEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mocks.EventSourceMock;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetPanelTest {

    @Mock DataSetSummary dataSetSummary;
    @Mock EventSourceMock<EditDataSetEvent> editDataSetEvent;
    @Mock DataSetPanel.View view;
    @Mock DataSetDef dataSetDef;
    @Mock ManagedInstance<DataSetEditorPlugin> dataSetEditorPlugin;
    @Mock DataSetEditorPlugin pluginEditor;
    
    private DataSetPanel presenter;

    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);               
        
        // The presenter instance to test.
        final Widget widget = mock(Widget.class);
        when(view.asWidget()).thenReturn(widget);
        presenter = spy(new DataSetPanel(dataSetSummary, editDataSetEvent, view, dataSetEditorPlugin));
        
    }
    
    @Test
    public void testInit() throws Exception {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).configure(any(DataSetSummary.View.class));
        verify(view, times(0)).showHeader(anyString(), anyString(), any(SafeUri.class), anyString(), anyString());
        verify(view, times(0)).showSummary();
        verify(view, times(0)).hideSummary();
        verify(view, times(0)).enableActionButton(anyString(), any(ClickHandler.class));
        verify(view, times(0)).disableActionButton();
    }

    @Test
    public void testAsWidget() throws Exception {
        assertEquals(view.asWidget(), presenter.asWidget());
    }

    @Test
    public void testClose() throws Exception {
        presenter.close();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).configure(any(DataSetSummary.View.class));
        verify(view, times(0)).showHeader(anyString(), anyString(), any(SafeUri.class), anyString(), anyString());
        verify(view, times(0)).showSummary();
        verify(view, times(1)).hideSummary();
        verify(view, times(0)).enableActionButton(anyString(), any(ClickHandler.class));
        verify(view, times(0)).disableActionButton();
    }

    @Test
    public void testDisable() throws Exception {
        presenter.disable();
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).configure(any(DataSetSummary.View.class));
        verify(view, times(0)).showHeader(anyString(), anyString(), any(SafeUri.class), anyString(), anyString());
        verify(view, times(0)).showSummary();
        verify(view, times(0)).hideSummary();
        verify(view, times(0)).enableActionButton(anyString(), any(ClickHandler.class));
        verify(view, times(1)).disableActionButton();
    }

    @Test
    public void testShow() throws Exception {
        final String iconTitle = "iconTitle";
        doReturn(iconTitle).when(presenter).getTypeIconTitle(dataSetDef);
        final SafeUri iconUri = mock(SafeUri.class);
        doReturn(iconUri).when(presenter).getTypeIconUri(dataSetDef);
        final String parentPanelId = "parentPanel";
        presenter.show(dataSetDef, parentPanelId);
        assertEquals(dataSetDef, presenter.getDataSetDef());
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).configure(any(DataSetSummary.View.class));
        verify(view, times(1)).showHeader("uuid1", parentPanelId, iconUri, iconTitle, "name1");
        verify(view, times(0)).showSummary();
        verify(view, times(0)).hideSummary();
        verify(view, times(0)).enableActionButton(anyString(), any(ClickHandler.class));
        verify(view, times(0)).disableActionButton();
    }

    @Test
    public void testOpen() throws Exception {
        presenter.def = dataSetDef;
        presenter.open();
        verify(dataSetSummary, times(1)).show(dataSetDef);
        verify(view, times(0)).init(presenter);
        verify(view, times(0)).configure(any(DataSetSummary.View.class));
        verify(view, times(0)).showHeader(anyString(), anyString(), any(SafeUri.class), anyString(), anyString());
        verify(view, times(1)).showSummary();
        verify(view, times(0)).hideSummary();
        verify(view, times(1)).enableActionButton(anyString(), any(ClickHandler.class));
        verify(view, times(0)).disableActionButton();
    }
    
    @Test
    public void testEditorPlugin() throws Exception {

        when(pluginEditor.getProviderType()).thenReturn(new CustomDataSetProviderType());
        when(pluginEditor.getTypeSelectorTitle()).thenReturn("Custom");
        when(pluginEditor.getTypeSelectorImageUri()).thenReturn(Mockito.mock(SafeUri.class));
        
        when(dataSetEditorPlugin.isUnsatisfied()).thenReturn(false);
        when(dataSetEditorPlugin.iterator()).thenReturn(Arrays.asList(pluginEditor).iterator(), Arrays.asList(pluginEditor).iterator());
        
        when(dataSetDef.getProvider()).thenReturn(new CustomDataSetProviderType());
        presenter.def = dataSetDef;
        final String parentPanelId = "parentPanel";
        presenter.show(dataSetDef, parentPanelId);
        verify(dataSetEditorPlugin, times(2)).isUnsatisfied();
        verify(dataSetEditorPlugin, times(2)).iterator();
        
        verify(pluginEditor, times(1)).getTypeSelectorImageUri();
        verify(pluginEditor, times(1)).getTypeSelectorTitle();
        
    }

}
