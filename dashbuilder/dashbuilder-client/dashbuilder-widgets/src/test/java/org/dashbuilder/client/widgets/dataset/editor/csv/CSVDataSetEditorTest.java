package org.dashbuilder.client.widgets.dataset.editor.csv;

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
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class CSVDataSetEditorTest {

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
    @Mock protected CSVDataSetDef dataSetDef;
    @Mock CSVDataSetDefAttributesEditor attributesEditor;
    CSVDataSetEditor presenter;
    
    @Before
    public void setup() throws Exception {
        this.presenter = new CSVDataSetEditor(basicAttributesEditor, attributesEditor, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.CSV);
    }

    @Test
    public void testFileUrl() {
        assertEquals(attributesEditor.fileURL, presenter.fileURL());
    }

    @Test
    public void testFilePath() {
        assertEquals(attributesEditor.filePath, presenter.filePath());
    }

    @Test
    public void testSepChar() {
        assertEquals(attributesEditor.separatorChar, presenter.separatorChar());
    }

    @Test
    public void testQuoteChar() {
        assertEquals(attributesEditor.quoteChar, presenter.quoteChar());
    }

    @Test
    public void testEscapeChar() {
        assertEquals(attributesEditor.escapeChar, presenter.escapeChar());
    }

    @Test
    public void testDatePattern() {
        assertEquals(attributesEditor.datePattern, presenter.datePattern());
    }

    @Test
    public void testNumberPattern() {
        assertEquals(attributesEditor.numberPattern, presenter.numberPattern());
    }

}
