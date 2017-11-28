package org.dashbuilder.client.widgets.dataset.editor.elasticsearch;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.common.LoadingBox;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefColumnsFilterEditor;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefPreviewTable;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBackendCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefClientCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.ElasticSearchDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ElasticSearchDataSetEditorTest {

    @Mock protected DataSetDefBasicAttributesEditor basicAttributesEditor;
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
    @Mock protected ElasticSearchDataSetDef dataSetDef;
    @Mock ElasticSearchDataSetDefAttributesEditor attributesEditor;
    ElasticSearchDataSetEditor presenter;
    
    @Before
    public void setup() throws Exception {
        this.presenter = new ElasticSearchDataSetEditor(basicAttributesEditor, attributesEditor, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.ELASTICSEARCH);
    }

    @Test
    public void testServerUrl() {
        assertEquals(attributesEditor.serverURL, presenter.serverURL());
    }

    @Test
    public void testClusterName() {
        assertEquals(attributesEditor.clusterName, presenter.clusterName());
    }

    @Test
    public void testIndex() {
        assertEquals(attributesEditor.index, presenter.index());
    }

    @Test
    public void testType() {
        assertEquals(attributesEditor.type, presenter.type());
    }

}
