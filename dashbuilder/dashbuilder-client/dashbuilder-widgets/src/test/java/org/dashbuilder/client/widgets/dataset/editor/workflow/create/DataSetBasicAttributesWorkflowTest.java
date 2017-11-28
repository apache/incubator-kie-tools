package org.dashbuilder.client.widgets.dataset.editor.workflow.create;

import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.attributes.DataSetDefBasicAttributesEditor;
import org.dashbuilder.client.widgets.dataset.editor.driver.DataSetDefBasicAttributesDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.DataSetDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;

import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSetBasicAttributesWorkflowTest extends AbstractDataSetWorkflowTest {

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
    DataSetDefBasicAttributesEditor basicAttributesEditor;
    @Mock
    DataSetDefBasicAttributesDriver dataSetDefBasicAttributesDriver;
    @Mock
    SyncBeanDef<DataSetDefBasicAttributesDriver> simpleBeanEditorDriverSyncBeanDef;
    @Mock
    SyncBeanDef<DataSetDefBasicAttributesEditor> simpleEditorSyncBeanDef;
    @Mock
    DataSetEditorWorkflow.View view;
    private DataSetBasicAttributesWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();

        // Bean instantiation mocks.
        when( beanManager.lookupBean( DataSetDefBasicAttributesDriver.class ) ).thenReturn(
                simpleBeanEditorDriverSyncBeanDef );
        when( simpleBeanEditorDriverSyncBeanDef.newInstance() ).thenAnswer( new Answer<DataSetDefBasicAttributesDriver>() {
            @Override
            public DataSetDefBasicAttributesDriver answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return dataSetDefBasicAttributesDriver;
            }
        } );
        when( beanManager.lookupBean( DataSetDefBasicAttributesEditor.class ) ).thenReturn( simpleEditorSyncBeanDef );
        when( simpleEditorSyncBeanDef.newInstance() ).thenAnswer( new Answer<DataSetDefBasicAttributesEditor>() {
            @Override
            public DataSetDefBasicAttributesEditor answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return basicAttributesEditor;
            }
        } );

        presenter = new DataSetBasicAttributesWorkflow( clientServices,
                                                        validatorProvider,
                                                        beanManager,
                                                        basicAttributesEditor,
                                                        saveRequestEvent,
                                                        testDataSetEvent,
                                                        cancelRequestEvent,
                                                        view ) {

            @Override
            protected Class<? extends SimpleBeanEditorDriver> getDriverClass() {
                return DataSetDefBasicAttributesDriver.class;
            }

            @Override
            protected Class getEditorClass() {
                return DataSetDefBasicAttributesEditor.class;
            }

            @Override
            protected Iterable<ConstraintViolation<?>> validate() {
                return null;
            }
        };

    }

    @Test
    public void testBasicAttributesEdition() {
        DataSetDef def = mock( DataSetDef.class );
        presenter.edit( def ).basicAttributesEdition();
        verify( beanManager, times( 2 ) ).lookupBean( DataSetDefBasicAttributesDriver.class );
        verify( dataSetDefBasicAttributesDriver, times( 2 ) ).initialize( basicAttributesEditor );
        verify( dataSetDefBasicAttributesDriver, times( 2 ) ).edit( def );
        verify( view, times( 2 ) ).clearView();
        verify( view, times( 2 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

}
