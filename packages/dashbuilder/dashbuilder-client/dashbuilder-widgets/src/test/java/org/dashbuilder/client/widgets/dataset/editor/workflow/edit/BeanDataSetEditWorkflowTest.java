package org.dashbuilder.client.widgets.dataset.editor.workflow.edit;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.driver.BeanDataSetDefDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.dataset.def.BeanDataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class BeanDataSetEditWorkflowTest extends AbstractDataSetWorkflowTest {

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
    org.dashbuilder.client.widgets.dataset.editor.bean.BeanDataSetEditor beanEditor;
    @Mock
    BeanDataSetDef dataSetDef;
    @Mock
    DataSetDefRefreshAttributesEditor refreshEditor;

    private BeanDataSetEditWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();

        presenter = new BeanDataSetEditWorkflow( clientServices, validatorProvider, beanManager,
                                                 saveRequestEvent, testDataSetEvent, cancelRequestEvent, view );
        when( dataSetDef.getProvider() ).thenReturn( DataSetProviderType.BEAN );
        when( beanEditor.refreshEditor() ).thenReturn( refreshEditor );
        when( refreshEditor.isRefreshEnabled() ).thenReturn( true );
    }

    @Test
    public void testGetDriverClass() {
        assertEquals( BeanDataSetDefDriver.class, presenter.getDriverClass() );
    }

    @Test
    public void testGetEditorClass() {
        assertEquals( org.dashbuilder.client.widgets.dataset.editor.bean.BeanDataSetEditor.class,
                      presenter.getEditorClass() );
    }

    @Test
    public void testValidate() {
        presenter._setDataSetDef( dataSetDef );
        presenter.validate( true, true, true );
        verify( beanDataSetDefValidator, times( 1 ) ).validate( dataSetDef, true, true, true );
        verify( beanDataSetDefValidator, times( 0 ) ).validateCustomAttributes( dataSetDef, true );
    }

}
