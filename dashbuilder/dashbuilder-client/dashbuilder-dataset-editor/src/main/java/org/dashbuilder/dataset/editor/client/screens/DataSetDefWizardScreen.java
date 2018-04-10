/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.dashbuilder.dataset.editor.client.screens;

import com.google.gwt.user.client.ui.IsWidget;
import org.dashbuilder.client.widgets.dataset.editor.DataSetEditor;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.DataSetEditorWorkflowFactory;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetBasicAttributesWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.create.DataSetProviderTypeWorkflow;
import org.dashbuilder.client.widgets.dataset.editor.workflow.edit.DataSetEditWorkflow;
import org.dashbuilder.client.widgets.dataset.event.*;
import org.dashbuilder.common.client.error.ClientRuntimeError;
import org.dashbuilder.dataprovider.DataSetProviderType;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.client.DataSetClientServices;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.editor.client.resources.i18n.DataSetAuthoringConstants;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

@WorkbenchScreen(identifier = "DataSetDefWizard")
@Dependent
public class DataSetDefWizardScreen {

    SyncBeanManager beanManager;
    DataSetEditorWorkflowFactory workflowFactory;
    Caller<DataSetDefVfsServices> services;
    DataSetClientServices clientServices;
    Event<NotificationEvent> notification;
    PlaceManager placeManager;
    ErrorPopupPresenter errorPopupPresenter;
    public DataSetDefScreenView view;

    PlaceRequest placeRequest;
    Command nextCommand;
    Integer originalHash = 0;
    DataSetEditorWorkflow currentWorkflow;
    SavePopUpPresenter savePopUpPresenter;

    @Inject
    public DataSetDefWizardScreen(final SyncBeanManager beanManager,
                                  final DataSetEditorWorkflowFactory workflowFactory,
                                  final Caller<DataSetDefVfsServices> services,
                                  final DataSetClientServices clientServices,
                                  final Event<NotificationEvent> notification,
                                  final PlaceManager placeManager,
                                  final ErrorPopupPresenter errorPopupPresenter,
                                  final SavePopUpPresenter savePopUpPresenter,
                                  final DataSetDefScreenView view) {
        this.beanManager = beanManager;
        this.workflowFactory = workflowFactory;
        this.services = services;
        this.clientServices = clientServices;
        this.notification = notification;
        this.placeManager = placeManager;
        this.errorPopupPresenter = errorPopupPresenter;
        this.savePopUpPresenter = savePopUpPresenter;
        this.view = view;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return DataSetAuthoringConstants.INSTANCE.creationWizardTitle();
    }

    @WorkbenchPartView
    public IsWidget getView() {
        return view;
    }

    @OnStartup
    public void init(PlaceRequest placeRequest) {
        this.placeRequest = placeRequest;
        providerTypeEdition();
    }

    @OnMayClose
    public boolean onMayClose() {
        return mayClose();
    }

    @OnClose
    public void onClose() {
        disposeCurrentWorkflow();
    }

    public void setOriginalHash(Integer originalHash) {
        this.originalHash = originalHash;
    }

    public int getCurrentModelHash() {
        if (getDataSetDef() == null) {
            return 0;
        }
        return getDataSetDef().hashCode();
    }

    public DataSetDef getDataSetDef() {
        return currentWorkflow != null ? currentWorkflow.getDataSetDef() : null;
    }

    public boolean mayClose() {
        try {
            // Flush the current workflow state
            if (currentWorkflow != null) {
                currentWorkflow.flush();
            }
        } catch (Exception e) {
            // Ignore. The provider type selection workflow stage throws an error
            // due to the dataset def being in partial creation state.
        }
        Integer currentHash = getCurrentModelHash();
        if (isDirty(currentHash)) {
            return view.confirmClose();
        } else {
            return true;
        }
    }

    public boolean isDirty(Integer currentHash) {
        if (originalHash == null) {
            return currentHash != null;
        } else {
            return !originalHash.equals(currentHash);
        }
    }

    private void providerTypeEdition() {
        final DataSetDef dataSetDef = new DataSetDef();
        final DataSetProviderTypeWorkflow providerTypeWorkflow = workflowFactory.providerType();
        this.nextCommand = () -> onProviderTypeSelected(providerTypeWorkflow);

        // First step, provider type selection.
        providerTypeWorkflow.edit(dataSetDef).providerTypeEdition();
        setCurrentWorkflow(providerTypeWorkflow);
        setOriginalHash(getCurrentModelHash());
    }

    void onProviderTypeSelected(final DataSetProviderTypeWorkflow providerTypeWorkflow) {
        final DataSetProviderType selectedProviderType = providerTypeWorkflow.getProviderType();
        try {
            clientServices.newDataSet(selectedProviderType,
                                      this::basicAttributesEdition);
        } catch (final Exception e) {
            showError(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
        }
    }

    private void setCurrentWorkflow(final DataSetEditorWorkflow w) {
        this.currentWorkflow = w;
        view.setWidget(w);
    }

    private void disposeCurrentWorkflow() {
        if (this.currentWorkflow != null) {
            workflowFactory.dispose(this.currentWorkflow);
            this.currentWorkflow = null;
        }
    }

    private void basicAttributesEdition(final DataSetDef typedDataSetDef) {
        final DataSetProviderType type = typedDataSetDef.getProvider() != null ? typedDataSetDef.getProvider() : null;
        final DataSetBasicAttributesWorkflow basicAttributesWorkflow = workflowFactory.basicAttributes(type);
        basicAttributesWorkflow.edit(typedDataSetDef).basicAttributesEdition().showBackButton().showTestButton();
        setCurrentWorkflow(basicAttributesWorkflow);
        setOriginalHash(getCurrentModelHash());
    }

    private void testDataSet() {
        assert currentWorkflow != null;
        currentWorkflow.testDataSet(new DataSetEditorWorkflow.TestDataSetCallback() {
            @Override
            public void onSuccess(final DataSet dataSet) {
                completeEdition(currentWorkflow.getDataSetDef(),
                                dataSet);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                showError(error);
            }
        });
    }

    public void completeEdition(final DataSetDef dataSetDef,
                                final DataSet dataset) {
        if (dataset != null) {
            this.nextCommand = this::save;
            List<DataColumn> columns = dataset.getColumns();
            if (columns != null && !columns.isEmpty()) {

                // Obtain all data columns available from the resulting data set.
                List<DataColumnDef> columnDefs = new ArrayList<>(columns.size());
                for (final DataColumn column : columns) {
                    columnDefs.add(new DataColumnDef(column.getId(),
                                                     column.getColumnType()));
                }

                // Delegate edition to the dataSetEditWorkflow.
                final DataSetProviderType type = dataSetDef.getProvider() != null ? dataSetDef.getProvider() : null;
                final DataSetEditWorkflow editWorkflow = workflowFactory.edit(type);
                editWorkflow.edit(dataSetDef,
                                  columnDefs)
                        .showPreviewTab()
                        .showBackButton()
                        .showNextButton();

                setCurrentWorkflow(editWorkflow);
            } else {
                showError("Data set has no columns");
            }
        } else {
            showError("Data set is empty.");
        }
    }

    protected void save() {
        final DataSetDef dataSetDef = currentWorkflow.getDataSetDef();
        savePopUpPresenter.show(message -> onSave(dataSetDef,
                                                  message));
    }

    void onSave(final DataSetDef dataSetDef,
                final String message) {
        BusyPopup.showMessage(DataSetAuthoringConstants.INSTANCE.saving());
        services.call(saveSuccessCallback,
                      errorCallback).save(dataSetDef,
                                          message);
        placeManager.goTo("DataSetAuthoringHome");
    }

    RemoteCallback<Path> saveSuccessCallback = new RemoteCallback<Path>() {
        @Override
        public void callback(Path path) {
            disposeCurrentWorkflow();
            BusyPopup.close();
            setOriginalHash(0);
            notification.fire(new NotificationEvent(DataSetAuthoringConstants.INSTANCE.savedOk()));
            placeManager.closePlace(placeRequest);
        }
    };

    ErrorCallback<Message> errorCallback = (message, throwable) -> {
        BusyPopup.close();
        showError(new ClientRuntimeError(throwable));
        return false;
    };

    void showError(final ClientRuntimeError error) {
        final String message = error.getCause() != null ? error.getCause() : error.getMessage();
        showError(message);
    }

    void showError(final String message) {
        errorPopupPresenter.showMessage(message);
    }

    /*************************************************************
     ** CDI EVENT HANDLING METHODS **
     *************************************************************/

    void onTestEvent(@Observes TestDataSetRequestEvent testDataSetRequestEvent) {
        checkNotNull("testDataSetRequestEvent",
                     testDataSetRequestEvent);
        if (testDataSetRequestEvent.getContext().equals(currentWorkflow)) {
            if (!currentWorkflow.hasErrors()) {
                testDataSet();
            }
        }
    }

    void onSaveEvent(@Observes SaveRequestEvent saveEvent) {
        checkNotNull("saveEvent",
                     saveEvent);
        if (saveEvent.getContext().equals(currentWorkflow)) {
            if (this.nextCommand != null && !currentWorkflow.hasErrors()) {
                this.nextCommand.execute();
            }
        }
    }

    void onCancelEvent(@Observes CancelRequestEvent cancelEvent) {
        checkNotNull("cancelEvent",
                     cancelEvent);
        if (cancelEvent.getContext().equals(currentWorkflow) && mayClose()) {
            providerTypeEdition();
        }
    }

    void onErrorEvent(@Observes ErrorEvent errorEvent) {
        checkNotNull("errorEvent",
                     errorEvent);
        if (errorEvent.getClientRuntimeError() != null) {
            showError(errorEvent.getClientRuntimeError());
        } else if (errorEvent.getMessage() != null) {
            showError(errorEvent.getMessage());
        }
    }

    void onTabChangedEvent(@Observes TabChangedEvent tabChangedEvent) {
        checkNotNull("tabChangedEvent",
                     tabChangedEvent);
        try {
            // This event should only be fired when worklow is an instance of the DataSetEditWorkflow, as it uses the main tabbed editor. 
            if (tabChangedEvent.getContext().equals(((DataSetEditWorkflow) currentWorkflow).getEditor())) {
                currentWorkflow.clearButtons();
                String tabId = tabChangedEvent.getTabId();
                if (tabId != null && DataSetEditor.TAB_CONFIGURATION.equals(tabId)) {
                    currentWorkflow.showTestButton();
                } else {
                    currentWorkflow.showBackButton().showNextButton();
                }
            }
        } catch (final ClassCastException e) {
            // Skip event.
        }
    }
}
