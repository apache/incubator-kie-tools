package org.dashbuilder.client.widgets.dataset.editor.workflow.create;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.DataSetDefProviderTypeEditor;
import org.dashbuilder.client.widgets.dataset.editor.driver.DataSetDefProviderTypeDriver;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.DataSetDefCreationRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.common.client.editor.list.HorizImageListEditor;
import org.dashbuilder.dataprovider.DataSetProviderType;
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

import static org.jgroups.util.Util.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSetProviderTypeWorkflowTest extends AbstractDataSetWorkflowTest {

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
    DataSetDefProviderTypeEditor providerTypeEditor;
    @Mock
    DataSetDefProviderTypeDriver dataSetDefProviderTypeDriver;
    @Mock
    SyncBeanDef<DataSetDefProviderTypeDriver> simpleBeanEditorDriverSyncBeanDef;
    @Mock
    HorizImageListEditor<DataSetProviderType> provider;
    @Mock
    DataSetEditorWorkflow.View view;

    private DataSetProviderTypeWorkflow presenter;

    @Before
    public void setup() throws Exception {

        // Bean instantiation mocks.
        when( beanManager.lookupBean( DataSetDefProviderTypeDriver.class ) ).thenReturn(
                simpleBeanEditorDriverSyncBeanDef );
        when( simpleBeanEditorDriverSyncBeanDef.newInstance() ).thenAnswer( new Answer<SimpleBeanEditorDriver>() {
            @Override
            public SimpleBeanEditorDriver answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return dataSetDefProviderTypeDriver;
            }
        } );

        presenter = new DataSetProviderTypeWorkflow( clientServices,
                                                     validatorProvider,
                                                     beanManager,
                                                     providerTypeEditor,
                                                     saveRequestEvent,
                                                     cancelRequestEvent,
                                                     testDataSetEvent,
                                                     view );
        when( providerTypeEditor.provider() ).thenReturn( provider );
    }


    @Test
    public void testProviderTypeValue() {
        when( provider.getValue() ).thenReturn( DataSetProviderType.SQL );
        assertEquals( DataSetProviderType.SQL, presenter.getProviderType() );
    }

    @Test
    public void testProviderTypeEdition() {
        DataSetDef def = mock( DataSetDef.class );
        presenter.edit( def ).providerTypeEdition();
        verify( beanManager, times( 1 ) ).lookupBean( DataSetDefProviderTypeDriver.class );
        verify( dataSetDefProviderTypeDriver, times( 1 ) ).initialize( providerTypeEditor );
        verify( dataSetDefProviderTypeDriver, times( 1 ) ).edit( any( DataSetDef.class ) );
        verify( view, times( 2 ) ).clearView();
        verify( view, times( 1 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

    @Test
    public void testProviderTypeSelected() {
        DataSetDefCreationRequestEvent createEvent = new DataSetDefCreationRequestEvent(providerTypeEditor, DataSetProviderType.BEAN);
        presenter.onProviderTypeSelected(createEvent);

        verify(saveRequestEvent).fire(any());
    }
}
