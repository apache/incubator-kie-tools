package org.dashbuilder.dataset.editor.client.screens;

import java.util.List;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflowFactory;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.DataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.event.CancelRequestEvent;
import org.dashbuilder.client.widgets.dataset.event.ErrorEvent;
import org.dashbuilder.client.widgets.dataset.event.TabChangedEvent;
import org.dashbuilder.client.widgets.dataset.event.TestDataSetRequestEvent;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.backend.EditDataSetDef;
import org.dashbuilder.dataset.client.editor.DataSetDefEditor;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.history.VersionRecordManager;
import org.uberfire.ext.editor.commons.client.menu.BasicFileMenuBuilder;
import org.uberfire.ext.editor.commons.client.validation.DefaultFileNameValidator;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @since 0.4.0
 */
@RunWith(GwtMockitoTestRunner.class)
public class DataSetDefEditorPresenterTest {

    @Mock
    EventSourceMock<ChangeTitleWidgetEvent> changeTitleNotification;
    @Mock
    EventSourceMock<NotificationEvent> notification;
    @Mock
    VersionRecordManager versionRecordManager;
    @Mock
    BasicFileMenuBuilder menuBuilder;
    @Mock
    DefaultFileNameValidator fileNameValidator;
    @Mock
    PlaceManager placeManager;
    @Mock
    PlaceRequest placeRequest;
    @Mock
    ObservablePath observablePath;
    @Mock
    SyncBeanManager beanManager;
    @Mock
    DataSetEditorWorkflowFactory workflowFactory;
    @Mock
    DataSetDefType resourceType;
    @Mock
    ErrorPopupPresenter errorPopupPresenter;
    @Mock
    DataSetDefVfsServices dataSetDefVfsServices;
    @Mock
    DataSetDef dataSetDef;
    @Mock
    DataSetDefEditor dataSetDefEditor;
    @Mock
    DataSetDefScreenView view;
    Caller<DataSetDefVfsServices> services;

    @InjectMocks
    private DataSetDefEditorPresenter presenter;

    final List<DataColumnDef> columns = mock(List.class);
    final DataSetEditWorkflow editWorkflow = mock(DataSetEditWorkflow.class);

    @Before
    public void setup() throws Exception {
        when(dataSetDef.getUUID()).thenReturn("uuid1");
        when(dataSetDef.getName()).thenReturn("name1");
        when(dataSetDef.getProvider()).thenReturn(DataSetProviderType.BEAN);
        when(dataSetDef.getColumns()).thenReturn(columns);
        services = new CallerMock<>(dataSetDefVfsServices);
        presenter.services = services;
        presenter.workflow = editWorkflow;
        final EditDataSetDef editDataSetDef = mock(EditDataSetDef.class);
        when(editDataSetDef.getDefinition()).thenReturn(dataSetDef);
        when(editDataSetDef.getColumns()).thenReturn(columns);
        when(dataSetDefVfsServices.load(any(Path.class))).thenReturn(editDataSetDef);
        when(dataSetDefVfsServices.get(any(Path.class))).thenReturn(dataSetDef);
        when(workflowFactory.edit(any(DataSetProviderType.class))).thenReturn(editWorkflow);
        when(editWorkflow.getDataSetDef()).thenReturn(dataSetDef);
        when(editWorkflow.getEditor()).thenReturn(dataSetDefEditor);
        when(editWorkflow.edit(any(DataSetDef.class), any(List.class))).thenReturn(editWorkflow);
        assertEquals(view.asWidget(), presenter.getWidget());
    }

    @Test
    public void testOnMayClose() {
        presenter.loadContent();
        when(editWorkflow.getDataSetDef()).thenReturn(mock(DataSetDef.class));
        presenter.onMayClose();
        verify(view).confirmClose();
        assertTrue(presenter.isDirty(presenter.getCurrentModelHash()));
    }

    @Test
    public void testOnClose() throws Exception {
        presenter.onClose();

        verify(workflowFactory).dispose(editWorkflow);
    }

    @Test
    public void testLoadContent() throws Exception {
        presenter.loadContent();
        assertFalse(presenter.isDirty(presenter.getCurrentModelHash()));
        verify(dataSetDefVfsServices, times(1)).load(any(Path.class));
        verify(changeTitleNotification, times(1)).fire(any(ChangeTitleWidgetEvent.class));
        verify(view, times(1)).hideBusyIndicator();
        verify(view, times(1)).setWidget(editWorkflow);
        verify(editWorkflow, times(1)).edit(dataSetDef, columns);
        verify(editWorkflow, times(1)).showPreviewTab();
    }

    @Test
    public void testLoadContentNullified() throws Exception {
        when(dataSetDefVfsServices.load(any(Path.class))).thenReturn(null);
        presenter.loadContent();
        verify(dataSetDefVfsServices, times(1)).load(any(Path.class));
        verify(errorPopupPresenter, times(1)).showMessage(anyString());
        verify(view, times(1)).hideBusyIndicator();
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testLoadDefinition() throws Exception {
        final Exception loadContentException = mock(Exception.class);
        doThrow(loadContentException).when(dataSetDefVfsServices).load(any(Path.class));
        presenter.loadContent();
        assertFalse(presenter.isDirty(presenter.getCurrentModelHash()));
        verify(dataSetDefVfsServices, times(1)).get(any(Path.class));
        verify(view, times(1)).hideBusyIndicator();
        verify(view, times(1)).setWidget(editWorkflow);
        verify(editWorkflow, times(1)).edit(dataSetDef, columns);
        verify(editWorkflow, times(1)).showPreviewTab();
    }

    @Test
    public void testGetDataSetDef() {
        assertEquals(dataSetDef, presenter.getDataSetDef());
    }

    @Test
    public void testGetDataSetDefNullified() {
        when(editWorkflow.getDataSetDef()).thenReturn(null);
        DataSetDef d = presenter.getDataSetDef();
        assertNull(d);
    }

    @Test
    public void testOnTestEvent() {
        TestDataSetRequestEvent event = mock(TestDataSetRequestEvent.class);
        when(event.getContext()).thenReturn(editWorkflow);
        when(editWorkflow.hasErrors()).thenReturn(false);
        presenter.onTestEvent(event);
        verify(editWorkflow, times(1)).testDataSet(any(DataSetEditorWorkflow.TestDataSetCallback.class));
    }

    @Test
    public void testOnTestEventWithErrors() {
        TestDataSetRequestEvent event = mock(TestDataSetRequestEvent.class);
        when(event.getContext()).thenReturn(editWorkflow);
        when(editWorkflow.hasErrors()).thenReturn(true);
        presenter.onTestEvent(event);
        verify(editWorkflow, times(0)).testDataSet(any(DataSetEditorWorkflow.TestDataSetCallback.class));
    }

    @Test
    public void testOnValidateSuccess() {
        when(editWorkflow.hasErrors()).thenReturn(false);
        presenter.getValidateCommand().execute();
        verify(editWorkflow, times(1)).flush();
        final ArgumentCaptor<NotificationEvent> dataCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification, times(1)).fire(dataCaptor.capture());
        NotificationEvent ne = dataCaptor.getValue();
        assertNotNull(ne);
        assertEquals(NotificationEvent.NotificationType.SUCCESS, ne.getType());
    }

    @Test
    public void validateCallbackIsCalled() throws Exception {
        final Command command = mock(Command.class);
        presenter.onValidate(command);
        verify(command).execute();
    }

    @Test
    public void testOnValidateFailed() {
        when(editWorkflow.hasErrors()).thenReturn(true);
        presenter.getValidateCommand().execute();
        verify(editWorkflow, times(1)).flush();
        final ArgumentCaptor<NotificationEvent> dataCaptor = ArgumentCaptor.forClass(NotificationEvent.class);
        verify(notification, times(1)).fire(dataCaptor.capture());
        NotificationEvent ne = dataCaptor.getValue();
        assertNotNull(ne);
        assertEquals(NotificationEvent.NotificationType.ERROR, ne.getType());
    }

    // TODO: @Test - Do it when SaveOperationService refactored.
    public void testOnSave() {
        presenter.save();
    }

    @Test
    public void testShowError() {
        final ClientRuntimeError error = mock(ClientRuntimeError.class);
        when(error.getCause()).thenReturn("errorCause");
        presenter.showError(error);
        verify(errorPopupPresenter, times(1)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
        verify(editWorkflow, times(0)).clear();
        verify(editWorkflow, times(0)).clearButtons();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
    }

    @Test
    public void testOnCancelEvent() {
        CancelRequestEvent event = mock(CancelRequestEvent.class);
        when(event.getContext()).thenReturn(editWorkflow);
        presenter.onCancelEvent(event);
        verify(editWorkflow, times(1)).clear();
        verify(editWorkflow, times(0)).clearButtons();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnErrorEvent() {
        ErrorEvent event = mock(ErrorEvent.class);
        when(event.getClientRuntimeError()).thenReturn(null);
        when(event.getMessage()).thenReturn("errorMessage");
        presenter.onErrorEvent(event);
        verify(editWorkflow, times(0)).clear();
        verify(editWorkflow, times(0)).clearButtons();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(1)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_ConfigurationTab() {
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_CONFIGURATION);
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(1)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_PreviewTab() {
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_PREVIEW);
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }

    @Test
    public void testOnTabChangedEvent_AdvancedTab() {
        TabChangedEvent event = mock(TabChangedEvent.class);
        when(event.getContext()).thenReturn(dataSetDefEditor);
        when(event.getTabId()).thenReturn(DataSetEditor.TAB_ADVANCED);
        presenter.onTabChangedEvent(event);
        verify(editWorkflow, times(1)).clearButtons();
        verify(editWorkflow, times(0)).showTestButton();
        verify(editWorkflow, times(0)).showNextButton();
        verify(editWorkflow, times(0)).showBackButton();
        verify(editWorkflow, times(0)).edit(any(DataSetDef.class), any(List.class));
        verify(errorPopupPresenter, times(0)).showMessage(anyString());
        verify(view, times(0)).setWidget(any(IsWidget.class));
    }
}