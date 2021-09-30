package org.dashbuilder.dataset.editor.client.screens;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflowFactory;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetProviderTypeWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.SQLDataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.ColumnType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.client.editor.SQLDataSetDefEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.def.DataSetDefFactory;
import org.dashbuilder.dataset.def.SQLDataSetDef;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import java.util.ArrayList;
import java.util.List;

import static org.jgroups.util.Util.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;


@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefWizardScreenTest {

    @Mock EventSourceMock<NotificationEvent> notification;
    @Mock PlaceManager placeManager;
    @Mock SyncBeanManager beanManager;
    @Mock DataSetEditorWorkflowFactory workflowFactory;
    @Mock DataSetClientServices clientServices;
    @Mock ErrorPopupPresenter errorPopupPresenter;
    @Mock DataSetDefScreenView view;
    @Mock DataSetProviderTypeWorkflow dataSetProviderTypeWorkflow;
    @Mock SQLDataSetDef dataSetDef;
    @Mock SQLDataSetDefEditor dataSetDefEditor;
    @Mock DataSetBasicAttributesWorkflow dataSetBasicAttributesWorkflow;
    @Mock SQLDataSetEditWorkflow editWorkflow;
    @Mock DataSetDefVfsServices dataSetDefVfsServices;
    @Mock SavePopUpPresenter savePopUpPresenter;
    Caller<DataSetDefVfsServices> services;
    private DataSetDefWizardScreen presenter;

    @Before
    public void setup() throws Exception {
        services = new CallerMock<>( dataSetDefVfsServices );

        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.SQL);
        when(workflowFactory.providerType()).thenReturn(dataSetProviderTypeWorkflow);
        when(workflowFactory.edit(any(DataSetProviderType.class))).thenReturn(editWorkflow);
        when(dataSetProviderTypeWorkflow.edit(any(DataSetDef.class))).thenReturn(dataSetProviderTypeWorkflow);
        when(dataSetProviderTypeWorkflow.providerTypeEdition()).thenReturn(dataSetProviderTypeWorkflow);

        doAnswer(invocationOnMock -> {
            RemoteCallback callback = (RemoteCallback) invocationOnMock.getArguments()[1];
            callback.callback(dataSetDef);
            return null;
        }).when(clientServices).newDataSet(any(DataSetProviderType.class), any(RemoteCallback.class));

        doAnswer(invocationOnMock -> {
            presenter.onClose();
            return null;
        }).when(placeManager).closePlace(any(PlaceRequest.class));

        when(dataSetBasicAttributesWorkflow.edit(any(DataSetDef.class))).thenReturn(dataSetBasicAttributesWorkflow);
        when(dataSetBasicAttributesWorkflow.basicAttributesEdition()).thenReturn(editWorkflow);
        when(editWorkflow.getDataSetDef()).thenReturn(dataSetDef);
        when(editWorkflow.edit(any(SQLDataSetDef.class), any(List.class))).thenReturn(editWorkflow);
        when(editWorkflow.showNextButton()).thenReturn(editWorkflow);
        when(editWorkflow.showBackButton()).thenReturn(editWorkflow);
        when(editWorkflow.showTestButton()).thenReturn(editWorkflow);
        when(editWorkflow.showPreviewTab()).thenReturn(editWorkflow);
        when(editWorkflow.showConfigurationTab()).thenReturn(editWorkflow);
        when(editWorkflow.showAdvancedTab()).thenReturn(editWorkflow);
        when(workflowFactory.basicAttributes(any(DataSetProviderType.class))).thenReturn(dataSetBasicAttributesWorkflow);
        presenter = new DataSetDefWizardScreen( beanManager, workflowFactory, services, clientServices,
                                                notification, placeManager, errorPopupPresenter, savePopUpPresenter, view );
        presenter.services = services;
    }

    @Test
    public void testOnMayClose() {
        presenter.init(null);
        when(dataSetProviderTypeWorkflow.getDataSetDef()).thenReturn(mock(SQLDataSetDef.class));
        presenter.onMayClose();
        verify(view).confirmClose();
        assertTrue(presenter.isDirty(presenter.getCurrentModelHash()));
    }

    @Test
    public void testOnClose() {
        presenter.init(PlaceRequest.NOWHERE);
        DataSetEditorWorkflow currentWorkflow = presenter.currentWorkflow;

        presenter.onClose();
        verify(workflowFactory).dispose(currentWorkflow);
        assertNull("current workflow null", presenter.currentWorkflow);
    }

    @Test
    public void testShowError() {
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        when(error.getCause()).thenReturn("errorCause");
        presenter.showError(error);
        verify(errorPopupPresenter, times(1)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testInitProviderTypeEdition() {
        PlaceRequest placeRequest = mock(PlaceRequest.class);
        presenter.init(placeRequest);
        assertEquals(dataSetProviderTypeWorkflow, presenter.currentWorkflow);
        assertFalse(presenter.isDirty(presenter.getCurrentModelHash()));
        verify(workflowFactory, times(1)).providerType();
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(dataSetProviderTypeWorkflow, times(1)).edit(any(DataSetDef.class));
        verify(dataSetProviderTypeWorkflow, times(0)).showNextButton();
        verify(dataSetProviderTypeWorkflow, times(0)).showTestButton();
        verify(dataSetProviderTypeWorkflow, times(0)).showBackButton();
    }

    @Test
    public void testOnProviderTypeSelected() throws Exception {
        when(dataSetProviderTypeWorkflow.getProviderType()).thenReturn(DataSetProviderType.SQL);
        presenter.onProviderTypeSelected(dataSetProviderTypeWorkflow);
        assertEquals(dataSetBasicAttributesWorkflow, presenter.currentWorkflow);
        assertFalse(presenter.isDirty(presenter.getCurrentModelHash()));
        verify(view, times(1)).setWidget(any(IsWidget.class));
        verify(dataSetBasicAttributesWorkflow, times(1)).edit(any(DataSetDef.class));
        verify(editWorkflow, times(1)).showTestButton();
        verify(editWorkflow, times(1)).showBackButton();
        verify(editWorkflow, times(0)).showNextButton();
    }

    @Test
    public void testOnTestEventWithErrors() {
        TestDataSetRequestEvent event = mock(TestDataSetRequestEvent.class);
        when(event.getContext()).thenReturn(dataSetBasicAttributesWorkflow);
        when(dataSetBasicAttributesWorkflow.hasErrors()).thenReturn(true);
        presenter.currentWorkflow = dataSetBasicAttributesWorkflow;
        presenter.onTestEvent(event);
        verify(dataSetBasicAttributesWorkflow, times(0)).testDataSet(any(DataSetEditorWorkflow.TestDataSetCallback.class));
    }

    @Test
    public void testOnTestEventAndCompleteEdition() {
        final DataSet dataSet = mock(DataSet.class);
        DataColumn col1 = mock(DataColumn.class);
        when(col1.getId()).thenReturn("col1");
        when(col1.getColumnType()).thenReturn(ColumnType.LABEL);
        DataColumn col2 = mock(DataColumn.class);
        when(col2.getId()).thenReturn("col2");
        when(col2.getColumnType()).thenReturn(ColumnType.LABEL);
        List<DataColumn> columns = new ArrayList<DataColumn>();
        columns.add(col1);
        columns.add(col2);
        when(dataSet.getColumns()).thenReturn(columns);
        TestDataSetRequestEvent event = mock(TestDataSetRequestEvent.class);
        when(event.getContext()).thenReturn(dataSetBasicAttributesWorkflow);
        when(dataSetBasicAttributesWorkflow.hasErrors()).thenReturn(false);
        when(dataSetBasicAttributesWorkflow.getDataSetDef()).thenReturn(dataSetDef);
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                DataSetEditorWorkflow.TestDataSetCallback callback = (DataSetEditorWorkflow.TestDataSetCallback) invocationOnMock.getArguments()[0];
                callback.onSuccess(dataSet);
                return null;
            }
        }).when(dataSetBasicAttributesWorkflow).testDataSet(any(DataSetEditorWorkflow.TestDataSetCallback.class));
        presenter.currentWorkflow = dataSetBasicAttributesWorkflow;

        presenter.onTestEvent(event);

        verify(dataSetBasicAttributesWorkflow, times(1)).testDataSet(any(DataSetEditorWorkflow.TestDataSetCallback.class));
        assertEquals(editWorkflow, presenter.currentWorkflow);
        assertTrue(presenter.isDirty(presenter.getCurrentModelHash()));
        verify(workflowFactory, times(1)).edit(any(DataSetProviderType.class));
        verify(view, times(1)).setWidget(any(IsWidget.class));
        final ArgumentCaptor<List> dataCaptor =  ArgumentCaptor.forClass(List.class);
        verify(editWorkflow, times(1)).edit(any(SQLDataSetDef.class), dataCaptor.capture());
        List<DataColumnDef> columnsCaptured = dataCaptor.getValue();
        assertNotNull(columnsCaptured);
        assertEquals(2, columnsCaptured.size());
        assertEquals("col1", columnsCaptured.get(0).getId());
        assertEquals("col2", columnsCaptured.get(1).getId());
        verify(editWorkflow, times(1)).showPreviewTab();
        verify(editWorkflow, times(1)).showNextButton();
        verify(editWorkflow, times(1)).showBackButton();
        verify(editWorkflow, times(0)).showTestButton();
    }

    @Test
    public void testOnSave() {
        final Path path = mock(Path.class);
        DataSetDef dataSetDef = DataSetDefFactory.newBeanDataSetDef().buildDef();
        PlaceRequest placeRequest = mock(PlaceRequest.class);
        when(dataSetDefVfsServices.save(any(DataSetDef.class), anyString())).thenReturn(path);
        when(dataSetProviderTypeWorkflow.getDataSetDef()).thenReturn(dataSetDef);

        presenter.init(placeRequest);
        presenter.onSave(dataSetDef, "saveMessage");
        verify(placeManager, times(1)).goTo("DataSetAuthoringHome");
        verify(notification, times(1)).fire(any(NotificationEvent.class));
        verify(placeManager, times(1)).closePlace(any(PlaceRequest.class));
        verify(workflowFactory).dispose(dataSetProviderTypeWorkflow);
        verify(workflowFactory, times(0)).edit(any(DataSetProviderType.class));

        assertNull("current workflow null", presenter.currentWorkflow);
        assertFalse(presenter.isDirty(presenter.getCurrentModelHash()));
        assertTrue(presenter.mayClose());
    }

    @Test
    public void testOnCancelEvent() {
        presenter.currentWorkflow = dataSetBasicAttributesWorkflow;
        CancelRequestEvent event = mock(CancelRequestEvent.class);
        when(event.getContext()).thenReturn(dataSetBasicAttributesWorkflow);
        presenter.onCancelEvent(event);
        verify(workflowFactory, times(1)).providerType();
        verify(workflowFactory, times(0)).edit(any(DataSetProviderType.class));
        verify(workflowFactory, times(0)).basicAttributes(any(DataSetProviderType.class));
    }

    @Test
    public void testOnErrorEvent() {
        ErrorEvent event = mock(ErrorEvent.class);
        when(event.getClientRuntimeError()).thenReturn(null);
        when(event.getMessage()).thenReturn("errorMessage");
        presenter.currentWorkflow = dataSetBasicAttributesWorkflow;
        presenter.onErrorEvent(event);
        verify(dataSetBasicAttributesWorkflow, times(0)).clear();
        verify(dataSetBasicAttributesWorkflow, times(0)).clearButtons();
        verify(dataSetBasicAttributesWorkflow, times(0)).showTestButton();
        verify(dataSetBasicAttributesWorkflow, times(0)).showNextButton();
        verify(dataSetBasicAttributesWorkflow, times(0)).showBackButton();
        verify(dataSetBasicAttributesWorkflow, times(0)).edit(any(DataSetDef.class));
        verify(errorPopupPresenter, times(1)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_ConfigurationTab() {
        when(editWorkflow.getEditor()).thenReturn(dataSetDefEditor);
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_CONFIGURATION);
        presenter.currentWorkflow = editWorkflow;
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(1)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(SQLDataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_PreviewTab() {
        when(editWorkflow.getEditor()).thenReturn(dataSetDefEditor);
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_PREVIEW);
        presenter.currentWorkflow = editWorkflow;
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(1)).showNextButton();
        verify(editWorkflow, times(1)).showBackButton();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).edit(any(SQLDataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_AdvancedTab() {
        when(editWorkflow.getEditor()).thenReturn(dataSetDefEditor);
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_ADVANCED);
        presenter.currentWorkflow = editWorkflow;
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(1)).showNextButton();
        verify(editWorkflow, times(1)).showBackButton();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).edit(any(SQLDataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

}
