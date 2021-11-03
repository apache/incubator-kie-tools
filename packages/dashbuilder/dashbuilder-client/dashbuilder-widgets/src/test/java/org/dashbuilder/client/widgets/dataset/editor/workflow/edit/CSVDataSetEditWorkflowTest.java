package org.dashbuilder.client.widgets.dataset.editor.workflow.edit;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.driver.CSVDataSetDefDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.CSVDataSetDefEditor;
import org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.dataset.def.CSVDataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class CSVDataSetEditWorkflowTest extends AbstractDataSetWorkflowTest {

    @Mock
    SyncBeanManager beanManager;
    @Mock
    EventSourceMock<SaveRequestEvent> saveRequestEvent;
    @Mock
    EventSourceMock<TestDataSetRequestEvent> testDataSetEvent;
    @Mock
    EventSourceMock<CancelRequestEvent> cancelRequestEvent;
    @Mock
    DataSetClientServices clientServices;
    @Mock
    DataSet dataSet;
    @Mock
    DataSetEditorWorkflow.View view;
    @Mock
    SimpleBeanEditorDriver<CSVDataSetDef, CSVDataSetDefEditor> driver;
    @Mock
    org.dashbuilder.client.widgets.dataset.editor.csv.CSVDataSetEditor csvEditor;
    @Mock
    CSVDataSetDef dataSetDef;
    @Mock
    DataSetDefRefreshAttributesEditor refreshEditor;

    private CSVDataSetEditWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();
        presenter = new CSVDataSetEditWorkflow( clientServices, validatorProvider, beanManager,
                                                saveRequestEvent, testDataSetEvent, cancelRequestEvent, view );
        when( dataSetDef.getProvider() ).thenReturn( DataSetProviderType.CSV );
        when( csvEditor.refreshEditor() ).thenReturn( refreshEditor );
        when( refreshEditor.isRefreshEnabled() ).thenReturn( true );
    }

    @Test
    public void testGetDriverClass() {
        assertEquals( CSVDataSetDefDriver.class, presenter.getDriverClass() );
    }

    @Test
    public void testGetEditorClass() {
        assertEquals( org.dashbuilder.client.widgets.dataset.editor.csv.CSVDataSetEditor.class,
                      presenter.getEditorClass() );
    }

    @Test
    public void testValidateUsingFilePath() {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = csvEditor;
        when( csvEditor.isUsingFilePath() ).thenReturn( true );
        presenter.validate( true, true, true );
        verify( csvDataSetDefValidator, times( 1 ) ).validate( dataSetDef, true, true, true, true );
        verify( csvDataSetDefValidator, times( 0 ) ).validateCustomAttributes( dataSetDef, true );
    }

    @Test
    public void testValidateUsingFileUrl() {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = csvEditor;

        when( csvEditor.isUsingFilePath() ).thenReturn( false );
        presenter.validate( true, true, true );
        verify( csvDataSetDefValidator, times( 1 ) ).validate( dataSetDef, true, true, true, false );
        verify( csvDataSetDefValidator, times( 0 ) ).validateCustomAttributes( dataSetDef, false );
    }

    @Test
    public void testFlushDriverUsingFileUrl() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = csvEditor;
        when( csvEditor.isUsingFilePath() ).thenReturn( false );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setFilePath( null );
        verify( dataSetDef, times( 0 ) ).setFileURL( null );
    }

    @Test
    public void testFlushUsingFilePath() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = csvEditor;
        when( csvEditor.isUsingFilePath() ).thenReturn( true );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setFileURL( null );
        verify( dataSetDef, times( 0 ) ).setFilePath( null );
    }

}
