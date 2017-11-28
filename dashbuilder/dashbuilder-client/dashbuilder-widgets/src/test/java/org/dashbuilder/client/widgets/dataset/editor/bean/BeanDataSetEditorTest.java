package org.dashbuilder.client.widgets.dataset.editor.bean;

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
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class BeanDataSetEditorTest {

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
    @Mock protected BeanDataSetDef dataSetDef;
    @Mock BeanDataSetDefAttributesEditor attributesEditor;
    BeanDataSetEditor presenter;
    
    @Before
    public void setup() throws Exception {
        this.presenter = new BeanDataSetEditor(basicAttributesEditor, attributesEditor, columnsAndFilterEditor, 
                previewTable, backendCacheAttributesEditor, clientCacheAttributesEditor, refreshEditor, clientServices,
                loadingBox, errorEvent, tabChangedEvent, view);
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
    }

    @Test
    public void testGeneratorClass() {
        assertEquals(attributesEditor.generatorClass, presenter.generatorClass());
    }

    @Test
    public void testParameterMap() {
        assertEquals(attributesEditor.paramaterMap, presenter.paramaterMap());
    }

    /* Bean types does not support backend cache attributes edition. */
    @Test
    public void testSetValue() {
        presenter.setValue(dataSetDef);
        assertNull(presenter.backendCacheEditor());
    }

}
