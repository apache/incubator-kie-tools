package org.dashbuilder.client.widgets.dataset.editor.sql;

import static org.mockito.Mockito.when;

import org.dashbuilder.client.widgets.dataset.editor.DataSetDefColumnsFilterEditor;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefPreviewTable;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBackendCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefClientCacheAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.common.client.widgets.LoadingBox;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import com.google.gwtmockito.GwtMockitoTestRunner;

@RunWith(GwtMockitoTestRunner.class)
public class SQLDataSetEditorTest  {

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
    @Mock protected SQLDataSetDef dataSetDef;
    @Mock SQLDataSetDefAttributesEditor attributesEditor;
    SQLDataSetEditor presenter;
    
    @Before
    public void setup() throws Exception {
        this.presenter = new SQLDataSetEditor(basicAttributesEditor, attributesEditor, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
    }

    @Test
    public void testDataSource() {
        Assert.assertEquals(attributesEditor.dataSource, presenter.dataSource());
    }

    @Test
    public void testDbSchema() {
        Assert.assertEquals(attributesEditor.dbSchema, presenter.dbSchema());
    }

    @Test
    public void testDbTable() {
        Assert.assertEquals(attributesEditor.dbTable, presenter.dbTable());
    }

    @Test
    public void testDbSQL() {
        Assert.assertEquals(attributesEditor.dbSQL, presenter.dbSQL());
    }
    
}
