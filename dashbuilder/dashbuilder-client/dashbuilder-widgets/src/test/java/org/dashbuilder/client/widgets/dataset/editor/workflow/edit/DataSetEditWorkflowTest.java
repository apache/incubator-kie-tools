package org.dashbuilder.client.widgets.dataset.editor.workflow.edit;

import java.util.List;
import javax.validation.ConstraintViolation;

import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.workflow.AbstractDataSetWorkflowTest;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.SaveRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.DataSetLookup;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.DataSetReadyCallback;
import org.dashbuilder.dataset.client.editor.DataSetDefRefreshAttributesEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class DataSetEditWorkflowTest extends AbstractDataSetWorkflowTest {

    public static final String UUID = "uuid1";
    public static final String NAME = "name1";

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
    DataSetDef dataSetDef;
    @Mock
    DataSet dataSet;
    @Mock
    DataSetEditorWorkflow.View view;
    @Mock
    DataSetDefRefreshAttributesEditor refreshEditor;
    @Mock
    SyncBeanDef<SimpleBeanEditorDriver> simpleBeanEditorDriverSyncBeanDef;
    @Mock
    SyncBeanDef<DataSetEditor> dataSetEditorSyncBeanDef;
    @Mock
    SimpleBeanEditorDriver driver;
    @Mock
    DataSetEditor editor;

    DataSetEditWorkflow presenter;

    @Before
    public void setup() throws Exception {
        super.setup();
        when( dataSetDef.getUUID() ).thenReturn( UUID );
        when( dataSetDef.getName() ).thenReturn( NAME );
        when( dataSet.getUUID() ).thenReturn( UUID );
        when( dataSet.getRowCount() ).thenReturn( 0 );
        when( dataSetDef.clone() ).thenReturn( dataSetDef );
        when( editor.refreshEditor() ).thenReturn( refreshEditor );

        // Bean instantiation mocks.
        when( beanManager.lookupBean( SimpleBeanEditorDriver.class ) ).thenReturn( simpleBeanEditorDriverSyncBeanDef );
        when( simpleBeanEditorDriverSyncBeanDef.newInstance() ).thenAnswer( new Answer<SimpleBeanEditorDriver>() {
            @Override
            public SimpleBeanEditorDriver answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return driver;
            }
        } );
        when( beanManager.lookupBean( DataSetEditor.class ) ).thenReturn( dataSetEditorSyncBeanDef );
        when( dataSetEditorSyncBeanDef.newInstance() ).thenAnswer( new Answer<DataSetEditor>() {
            @Override
            public DataSetEditor answer( InvocationOnMock invocationOnMock ) throws Throwable {
                return editor;
            }
        } );


        doAnswer( new Answer<Void>() {
            @Override
            public Void answer( final InvocationOnMock invocationOnMock ) throws Throwable {
                DataSetReadyCallback callback = (DataSetReadyCallback) invocationOnMock.getArguments()[2];
                callback.callback( dataSet );
                return null;
            }
        } ).when( clientServices ).lookupDataSet( any( dataSetDef.getClass() ),
                                                  any( DataSetLookup.class ),
                                                  any( DataSetReadyCallback.class ) );

        presenter = new DataSetEditWorkflow( clientServices, validatorProvider, beanManager, saveRequestEvent,
                                             testDataSetEvent, cancelRequestEvent, view ) {

            @Override
            protected Class<? extends SimpleBeanEditorDriver> getDriverClass() {
                return SimpleBeanEditorDriver.class;
            }

            @Override
            protected Class getEditorClass() {
                return DataSetEditor.class;
            }

            @Override
            protected Iterable<ConstraintViolation<?>> validate( boolean isCacheEnabled,
                                                                 boolean isPushEnabled,
                                                                 boolean isRefreshEnabled ) {
                return null;
            }
        };
    }

    @Test
    public void testEdit() {
        List<DataColumnDef> columnDefs = mock( List.class );
        presenter.edit( dataSetDef, columnDefs );
        assertEquals( editor, presenter.getEditor() );
        verify( driver, times( 1 ) ).initialize( editor );
        verify( editor, times( 1 ) ).setAcceptableValues( columnDefs );
        verify( driver, times( 1 ) ).edit( dataSetDef );
        verify( view, times( 2 ) ).clearView();
        verify( view, times( 1 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

    @Test
    public void testShowConfigurationTab() {
        presenter.editor = editor;
        presenter.showConfigurationTab();
        verify( editor, times( 1 ) ).showConfigurationTab();
        verify( editor, times( 0 ) ).showPreviewTab();
        verify( editor, times( 0 ) ).showAdvancedTab();
        verify( view, times( 0 ) ).clearView();
        verify( view, times( 0 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

    @Test
    public void testShowPreviewTab() {
        presenter.editor = editor;
        presenter.showPreviewTab();
        verify( editor, times( 1 ) ).showPreviewTab();
        verify( editor, times( 0 ) ).showConfigurationTab();
        verify( editor, times( 0 ) ).showAdvancedTab();
        verify( view, times( 0 ) ).clearView();
        verify( view, times( 0 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

    @Test
    public void testShowAdvancedTab() {
        presenter.editor = editor;
        presenter.showAdvancedTab();
        verify( editor, times( 1 ) ).showAdvancedTab();
        verify( editor, times( 0 ) ).showPreviewTab();
        verify( editor, times( 0 ) ).showConfigurationTab();
        verify( view, times( 0 ) ).clearView();
        verify( view, times( 0 ) ).add( any( IsWidget.class ) );
        verify( view, times( 0 ) ).init( presenter );
        verify( view, times( 0 ) ).addButton( anyString(), anyString(), anyBoolean(), any( Command.class ) );
        verify( view, times( 0 ) ).clearButtons();
    }

    @Test
    public void testFlushDriverRefreshEnabled() throws Exception {
        presenter.editor = editor;
        when( refreshEditor.isRefreshEnabled() ).thenReturn( true );
        presenter.afterFlush();
        verify( dataSetDef, times( 0 ) ).setRefreshTime( null );
    }

    @Test
    public void testFlushDriverRefreshDisabled() throws Exception {
        presenter.editor = editor;
        presenter._setDataSetDef( dataSetDef );
        when( refreshEditor.isRefreshEnabled() ).thenReturn( false );
        presenter.afterFlush();
        verify( dataSetDef, times( 1 ) ).setRefreshTime( null );
    }

}
