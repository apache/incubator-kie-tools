package org.dashbuilder.client.widgets.dataset.editor.workflow.create;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.driver.SQLDataSetDefAttributesDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class SQLDataSetBasicAttributesWorkflowTest extends AbstractDataSetWorkflowTest {

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
    SQLDataSetDef dataSetDef;
    @Mock
    DataSetDefBasicAttributesEditor basicAttributesEditor;
    @Mock
    org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetDefAttributesEditor sqlDataSetDefAttributesEditor;
    @Mock
    DataSetEditorWorkflow.View view;
    private SQLDataSetBasicAttributesWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();

        presenter = new SQLDataSetBasicAttributesWorkflow( clientServices,
                                                           validatorProvider,
                                                           beanManager,
                                                           basicAttributesEditor,
                                                           saveRequestEvent,
                                                           testDataSetEvent,
                                                           cancelRequestEvent,
                                                           view );
        when( dataSetDef.getProvider() ).thenReturn( DataSetProviderType.SQL );
    }

    @Test
    public void testGetDriverClass() {
        assertEquals( SQLDataSetDefAttributesDriver.class, presenter.getDriverClass() );
    }

    @Test
    public void testGetEditorClass() {
        assertEquals( org.dashbuilder.client.widgets.dataset.editor.sql.SQLDataSetDefAttributesEditor.class,
                      presenter.getEditorClass() );
    }

    @Test
    public void testValidateUsingQuery() {
        presenter._setDataSetDef( dataSetDef );
        presenter.editor = sqlDataSetDefAttributesEditor;
        when( sqlDataSetDefAttributesEditor.isUsingQuery() ).thenReturn( true );
        presenter.validate();
        verify( sqlDataSetDefValidator, times( 1 ) ).validateCustomAttributes( any( SQLDataSetDef.class ), eq( true ) );
        verify( sqlDataSetDefValidator, times( 0 ) ).validate( any( SQLDataSetDef.class ),
                                                               anyBoolean(),
                                                               anyBoolean(),
                                                               anyBoolean() );
    }

    @Test
    public void testValidateUsingTable() {
        presenter._setDataSetDef( dataSetDef );
        presenter.editor = sqlDataSetDefAttributesEditor;
        when( sqlDataSetDefAttributesEditor.isUsingQuery() ).thenReturn( false );
        presenter.validate();
        verify( sqlDataSetDefValidator, times( 1 ) ).validateCustomAttributes( any( SQLDataSetDef.class ),
                                                                               eq( false ) );
        verify( sqlDataSetDefValidator, times( 0 ) ).validate( any( SQLDataSetDef.class ),
                                                               anyBoolean(),
                                                               anyBoolean(),
                                                               anyBoolean() );
    }

    @Test
    public void testFlushDriverUsingQuery() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.editor = sqlDataSetDefAttributesEditor;
        when( sqlDataSetDefAttributesEditor.isUsingQuery() ).thenReturn( true );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setDbTable( null );
        verify( dataSetDef, times( 0 ) ).setDbSQL( null );
    }

    @Test
    public void testFlushDriverUsingTable() throws Exception {
        presenter._setDataSetDef( dataSetDef );
        presenter.editor = sqlDataSetDefAttributesEditor;
        when( sqlDataSetDefAttributesEditor.isUsingQuery() ).thenReturn( false );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setDbSQL( null );
        verify( dataSetDef, times( 0 ) ).setDbTable( null );
    }
}
