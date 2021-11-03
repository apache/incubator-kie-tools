package org.dashbuilder.client.widgets.dataset.editor.workflow.edit;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.driver.SQLDataSetDefDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.dataset.client.editor.SQLDataSetDefEditor;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class SQLDataSetEditWorkflowTest extends AbstractDataSetWorkflowTest {

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
    SimpleBeanEditorDriver<SQLDataSetDef, SQLDataSetDefEditor> driver;
    @Mock
    org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetEditor sqlEditor;
    @Mock
    SQLDataSetDef dataSetDef;
    @Mock
    DataSetDefRefreshAttributesEditor refreshEditor;

    private SQLDataSetEditWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();

        presenter = new SQLDataSetEditWorkflow( clientServices, validatorProvider, beanManager,
                                                saveRequestEvent, testDataSetEvent, cancelRequestEvent, view );
        when( dataSetDef.getProvider() ).thenReturn( DataSetProviderType.SQL );
        when( sqlEditor.refreshEditor() ).thenReturn( refreshEditor );
        when( refreshEditor.isRefreshEnabled() ).thenReturn( true );
    }

    @Test
    public void testGetDriverClass() {
        assertEquals( SQLDataSetDefDriver.class, presenter.getDriverClass() );
    }

    @Test
    public void testGetEditorClass() {
        assertEquals( org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetEditor.class,
                      presenter.getEditorClass() );
    }

    @Test
    public void testValidateUsingQuery() {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = sqlEditor;
        when( sqlEditor.isUsingQuery() ).thenReturn( true );
        presenter.validate( true, true, true );
        verify( sqlDataSetDefValidator, times( 1 ) ).validate( dataSetDef, true, true, true, true );
        verify( sqlDataSetDefValidator, times( 0 ) ).validateCustomAttributes( dataSetDef, true );
    }

    @Test
    public void testValidateUsingTable() {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = sqlEditor;
        when( sqlEditor.isUsingQuery() ).thenReturn( false );
        presenter.validate( true, true, true );
        verify( sqlDataSetDefValidator, times( 1 ) ).validate( dataSetDef, true, true, true, false );
        verify( sqlDataSetDefValidator, times( 0 ) ).validateCustomAttributes( dataSetDef, false );
    }

    @Test
    public void testFlushDriverUsingQuery() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = sqlEditor;
        when( sqlEditor.isUsingQuery() ).thenReturn( true );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setDbTable( null );
        verify( dataSetDef, times( 0 ) ).setDbSQL( null );
    }

    @Test
    public void testFlushDriverUsingTable() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.driver = driver;
        presenter.editor = sqlEditor;
        when( sqlEditor.isUsingQuery() ).thenReturn( false );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setDbSQL( null );
        verify( dataSetDef, times( 0 ) ).setDbTable( null );
    }

}
