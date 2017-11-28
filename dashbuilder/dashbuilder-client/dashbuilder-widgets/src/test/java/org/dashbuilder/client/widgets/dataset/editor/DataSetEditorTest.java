package org.dashbuilder.client.widgets.dataset.editor;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.attributes.*;
import org.dashbuilder.client.widgets.dataset.event.ColumnsChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.FilterChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetMetadata;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.ColumnListEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.filter.DataSetFilter;
import org.dashbuilder.displayer.client.DataSetHandler;
import org.dashbuilder.displayer.client.Displayer;
import org.dashbuilder.displayer.client.DisplayerListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jgroups.util.Util.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class DataSetEditorTest {

    @Mock protected DataSetDefBasicAttributesEditor basicAttributesEditor;
    @Mock protected IsWidget providerAttributesEditorView;
    @Mock protected DataSetDefColumnsFilterEditor columnsAndFilterEditor;
    @Mock protected DataSetDefPreviewTable previewTable;
    @Mock protected DataSetDefBackendCacheAttributesEditor backendCacheAttributesEditor;
    @Mock protected DataSetDefClientCacheAttributesEditor clientCacheAttributesEditor;
    @Mock protected DataSetDefRefreshAttributesEditor refreshEditor;
    @Mock protected DataSetClientServices clientServices;
    @Mock protected LoadingBox loadingBox;
    @Mock protected EventSourceMock<ErrorEvent> errorEvent;
    @Mock protected EventSourceMock<TabChangedEvent> tabChangedEvent;
    @Mock protected DataSetEditor.View view;
    @Mock protected DataSetDefFilterEditor filterEditor;
    @Mock protected org.dashbuilder.dataset.client.editor.DataSetDefColumnsEditor columnsEditor;
    @Mock protected ColumnListEditor columnListEditor;
    @Mock protected DataSetDef dataSetDef;
    
    private DataSetEditor<DataSetDef> presenter;
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        IsWidget attributesEditor = mock(IsWidget.class);
        
        presenter = new DataSetEditor<DataSetDef>(basicAttributesEditor, attributesEditor, columnsAndFilterEditor,
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view) {
            @Override
            public void init() {
                super.init();
            }
        };
        
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        when(columnsAndFilterEditor.dataSetFilter()).thenReturn(filterEditor);
        when(columnsAndFilterEditor.columnListEditor()).thenReturn(columnsEditor);
        when(columnsEditor.columns()).thenReturn(columnListEditor);
    }
    
    @Test
    public void testInit() {
        presenter.init();
        verify(view, times(1)).init(presenter);
        verify(view, times(1)).initWidgets(
                any(DataSetDefBasicAttributesEditor.View.class),
                any(IsWidget.class),
                any(DataSetDefColumnsFilterEditor.View.class),
                any(DataSetDefPreviewTable.View.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefRefreshAttributesEditor.View.class)
        );
        verify(view, times(1)).addConfigurationTabItemClickHandler(any(Command.class));
        verify(view, times(1)).addPreviewTabItemClickHandler(any(Command.class));
        verify(view, times(1)).addAdvancedTabItemClickHandler(any(Command.class));
        verify(columnsAndFilterEditor, times(1)).setMaxHeight(anyString());
        verify(backendCacheAttributesEditor, times(1)).setRange(anyDouble(), anyDouble());
        verify(clientCacheAttributesEditor, times(1)).setRange(anyDouble(), anyDouble());
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
        verify(view, times(0)).openColumnsFilterPanel(anyString());
        verify(view, times(0)).closeColumnsFilterPanel(anyString());
        verify(view, times(0)).showErrorNotification(any(SafeHtml.class));
        verify(view, times(0)).clearErrorNotification();
    }

    @Test
    public void testShowConfigurationTab() {
        presenter.showConfigurationTab();
        presenter.afterPreviewCommand.execute();
        verify(view, times(1)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
    }

    @Test
    public void testShowPreviewTab() {
        presenter.showPreviewTab();
        presenter.afterPreviewCommand.execute();
        verify(view, times(1)).showPreviewTab();
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showAdvancedTab();
    }

    @Test
    public void testShowAdvTab() {
        presenter.showAdvancedTab();
        presenter.afterPreviewCommand.execute();
        verify(view, times(1)).showAdvancedTab();
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
    }

    @Test
    public void testSetAcceptableValues() {
        List<DataColumnDef> acceptableValues = mock(List.class);
        presenter.setAcceptableValues(acceptableValues);
        verify(columnsAndFilterEditor, times(1)).setAcceptableValues(acceptableValues);
        assertViewNotUsed();
    }

    @Test
    public void testBasicAttributesEditor() {
        assertEquals(basicAttributesEditor, presenter.basicAttributesEditor());
        assertViewNotUsed();
    }

    @Test
    public void testColumnsAndFilterEditor() {
        assertEquals(columnsAndFilterEditor, presenter.columnsAndFilterEditor());
        assertViewNotUsed();
    }

    @Test
    public void testBackendCacheEditor() {
        assertEquals(backendCacheAttributesEditor, presenter.backendCacheEditor());
        assertViewNotUsed();
    }

    @Test
    public void testClientCacheEditor() {
        assertEquals(clientCacheAttributesEditor, presenter.clientCacheEditor());
        assertViewNotUsed();
    }

    @Test
    public void testRefreshEditor() {
        assertEquals(refreshEditor, presenter.refreshEditor());
        assertViewNotUsed();
    }

    @Test
    public void testOnOpenColumnsFilterPanel() {
        presenter.onOpenColumnsFilterPanel();
        verify(view, times(1)).openColumnsFilterPanel(anyString());
    }

    @Test
    public void testOnCloseColumnsFilterPanel() {
        presenter.onCloseColumnsFilterPanel();
        verify(view, times(1)).closeColumnsFilterPanel(anyString());
    }

    @Test
    public void testConfigurationTabItemClickHandler() {
        presenter.configurationTabItemClickHandler.execute();
        final ArgumentCaptor<TabChangedEvent> tabChangedEventCaptor =  ArgumentCaptor.forClass(TabChangedEvent.class);
        verify(tabChangedEvent, times(1)).fire(tabChangedEventCaptor.capture());
        final TabChangedEvent tabChangedEvent = tabChangedEventCaptor.getValue();
        assertEquals(presenter, tabChangedEvent.getContext());
        assertEquals(DataSetEditor.TAB_CONFIGURATION, tabChangedEvent.getTabId());
        assertViewNotUsed();
    }

    @Test
    public void testPreviewTabItemClickHandler() {
        presenter.previewTabItemClickHandler.execute();
        final ArgumentCaptor<TabChangedEvent> tabChangedEventCaptor =  ArgumentCaptor.forClass(TabChangedEvent.class);
        verify(tabChangedEvent, times(1)).fire(tabChangedEventCaptor.capture());
        final TabChangedEvent tabChangedEvent = tabChangedEventCaptor.getValue();
        assertEquals(presenter, tabChangedEvent.getContext());
        assertEquals(DataSetEditor.TAB_PREVIEW, tabChangedEvent.getTabId());
        assertViewNotUsed();
    }

    @Test
    public void testAdvTabItemClickHandler() {
        presenter.advancedTabItemClickHandler.execute();
        final ArgumentCaptor<TabChangedEvent> tabChangedEventCaptor =  ArgumentCaptor.forClass(TabChangedEvent.class);
        verify(tabChangedEvent, times(1)).fire(tabChangedEventCaptor.capture());
        final TabChangedEvent tabChangedEvent = tabChangedEventCaptor.getValue();
        assertEquals(presenter, tabChangedEvent.getContext());
        assertEquals(DataSetEditor.TAB_ADVANCED, tabChangedEvent.getTabId());
        assertViewNotUsed();
    }

    @Test
    public void testShowError() {
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        when(error.getCause()).thenReturn("errorCause");
        presenter.afterPreviewCommand = mock(Command.class);
        presenter.showError(error);
        verify(loadingBox, times(1)).hide();
        verify(view, times(1)).showErrorNotification(any(SafeHtml.class));
        verify(errorEvent, times(1)).fire(any(ErrorEvent.class));
        verify(presenter.afterPreviewCommand, times(1)).execute();
        verify(view, times(0)).addConfigurationTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addPreviewTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addAdvancedTabItemClickHandler(any(Command.class));
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
        verify(view, times(0)).openColumnsFilterPanel(anyString());
        verify(view, times(0)).closeColumnsFilterPanel(anyString());
        verify(view, times(0)).clearErrorNotification();
    }

    @Test
    public void testOnColumnsChangedEvent() {
        final DataColumnDef col1 = mock(DataColumnDef.class);
        when(col1.getId()).thenReturn("col1");
        when(col1.getColumnType()).thenReturn(ColumnType.LABEL);
        final DataColumnDef col2 = mock(DataColumnDef.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.NUMBER);
        final List<DataColumnDef> cols = new ArrayList<DataColumnDef>();
        cols.add(col1);
        cols.add(col2);
        final ColumnsChangedEvent event = mock(ColumnsChangedEvent.class);
        when(event.getContext()).thenReturn(columnListEditor);
        when(event.getColumns()).thenReturn(cols);
        presenter.dataSetDef = this.dataSetDef;
        mockPreviewTableCall();
        final Command afterPreviewCommand = mock(Command.class);
        presenter.afterPreviewCommand = afterPreviewCommand;
        presenter.onColumnsChangedEvent(event);
        verify(dataSetDef, times(1)).setColumns(cols);
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();
        verify(view, times(1)).clearErrorNotification();
        verify(filterEditor, times(1)).init(any(DataSetMetadata.class));
        verify(afterPreviewCommand, times(1)).execute();
        verify(view, times(0)).init(any(DataSetEditor.class));
        verify(view, times(0)).initWidgets(
                any(DataSetDefBasicAttributesEditor.View.class),
                any(IsWidget.class),
                any(DataSetDefColumnsFilterEditor.View.class),
                any(DataSetDefPreviewTable.View.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefRefreshAttributesEditor.View.class)
        );
        verify(view, times(0)).addConfigurationTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addPreviewTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addAdvancedTabItemClickHandler(any(Command.class));
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
        verify(view, times(0)).openColumnsFilterPanel(anyString());
        verify(view, times(0)).closeColumnsFilterPanel(anyString());
        verify(view, times(0)).showErrorNotification(any(SafeHtml.class));
    }

    @Test
    public void testOnFilterChangedEvent() {
        final FilterChangedEvent event = mock(FilterChangedEvent.class);
        final DataSetFilter filter = mock(DataSetFilter.class);
        when(event.getContext()).thenReturn(filterEditor);
        when(event.getFilter()).thenReturn(filter);
        presenter.dataSetDef = this.dataSetDef;
        mockPreviewTableCall();
        final Command afterPreviewCommand = mock(Command.class);
        presenter.afterPreviewCommand = afterPreviewCommand;
        presenter.onFilterChangedEvent(event);
        verify(dataSetDef, times(1)).setDataSetFilter(filter);
        verify(loadingBox, times(1)).show();
        verify(loadingBox, times(1)).hide();
        verify(view, times(1)).clearErrorNotification();
        verify(filterEditor, times(0)).init(any(DataSetMetadata.class));
        verify(afterPreviewCommand, times(1)).execute();
        verify(view, times(0)).init(any(DataSetEditor.class));
        verify(view, times(0)).initWidgets(
                any(DataSetDefBasicAttributesEditor.View.class),
                any(IsWidget.class),
                any(DataSetDefColumnsFilterEditor.View.class),
                any(DataSetDefPreviewTable.View.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefRefreshAttributesEditor.View.class)
        );
        verify(view, times(0)).addConfigurationTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addPreviewTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addAdvancedTabItemClickHandler(any(Command.class));
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
        verify(view, times(0)).openColumnsFilterPanel(anyString());
        verify(view, times(0)).closeColumnsFilterPanel(anyString());
        verify(view, times(0)).showErrorNotification(any(SafeHtml.class));
    }

    protected void mockPreviewTableCall() {
        final Displayer displayer = mock(Displayer.class);
        final DataSetHandler dataSetHandler = mock(DataSetHandler.class);
        final DataSet dataSet = mock(DataSet.class);
        when(displayer.getDataSetHandler()).thenReturn(dataSetHandler);
        when(dataSetHandler.getLastDataSet()).thenReturn(dataSet);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                DisplayerListener listener = (DisplayerListener) invocationOnMock.getArguments()[2];
                listener.onDraw(displayer);
                return null;
            }
        }).when(previewTable).show(any(DataSetDef.class), any(Collection.class), any(DisplayerListener.class));
        
    }

    protected void assertViewNotUsed() {
        verify(view, times(0)).init(any(DataSetEditor.class));
        verify(view, times(0)).initWidgets(
                any(DataSetDefBasicAttributesEditor.View.class),
                any(IsWidget.class),
                any(DataSetDefColumnsFilterEditor.View.class),
                any(DataSetDefPreviewTable.View.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefCacheAttributesEditorView.class),
                any(DataSetDefRefreshAttributesEditor.View.class)
        );
        verify(view, times(0)).addConfigurationTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addPreviewTabItemClickHandler(any(Command.class));
        verify(view, times(0)).addAdvancedTabItemClickHandler(any(Command.class));
        verify(view, times(0)).showConfigurationTab();
        verify(view, times(0)).showPreviewTab();
        verify(view, times(0)).showAdvancedTab();
        verify(view, times(0)).openColumnsFilterPanel(anyString());
        verify(view, times(0)).closeColumnsFilterPanel(anyString());
        verify(view, times(0)).showErrorNotification(any(SafeHtml.class));
        verify(view, times(0)).clearErrorNotification();
    }
}
