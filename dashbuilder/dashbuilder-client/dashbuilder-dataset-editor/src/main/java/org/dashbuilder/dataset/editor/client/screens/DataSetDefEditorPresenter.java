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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
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
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.backend.EditDataSetDef;
import org.dashbuilder.dataset.def.DataColumnDef;
import org.dashbuilder.dataset.def.DataSetDef;
import org.dashbuilder.dataset.editor.client.resources.i18n.DataSetAuthoringConstants;
import org.dashbuilder.dataset.events.DataSetDefRemovedEvent;
import org.dashbuilder.dataset.service.DataSetDefVfsServices;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.events.ChangeTitleWidgetEvent;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.editor.commons.file.DefaultMetadata;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.COPY;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.DELETE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.SAVE;
import static org.uberfire.ext.editor.commons.client.menu.MenuItems.VALIDATE;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.ERROR;
import static org.uberfire.workbench.events.NotificationEvent.NotificationType.SUCCESS;

@Dependent
@WorkbenchEditor(identifier = "DataSetDefEditor", supportedTypes = {DataSetDefType.class}, priority = Integer.MAX_VALUE)
public class DataSetDefEditorPresenter extends BaseEditor<DataSetDef, DefaultMetadata> {

    @Inject
    SyncBeanManager beanManager;
    @Inject
    DataSetEditorWorkflowFactory workflowFactory;
    @Inject
    Caller<DataSetDefVfsServices> services;
    @Inject
    PlaceManager placeManager;
    @Inject
    DataSetDefType resourceType;
    @Inject
    ErrorPopupPresenter errorPopupPresenter;
    @Inject
    SavePopUpPresenter savePopUpPresenter;

    @Inject
    public DataSetDefScreenView view;
    DataSetEditWorkflow workflow;

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        super.baseView = view;
        init(path,
             place,
             resourceType,
             true,
             false,
             VALIDATE,
             SAVE,
             COPY,
             DELETE);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return buildTitle();
    }

    protected String buildTitle() {
        if (getDataSetDef() == null) {
            return DataSetAuthoringConstants.INSTANCE.editorTitleGeneric();
        } else {
            String type = getDataSetDef().getProvider().getName();
            return DataSetAuthoringConstants.INSTANCE.editorTitle(getDataSetDef().getName(),
                                                                  type);
        }
    }

    @WorkbenchMenu
    public void getMenus(final Consumer<Menus> menusConsumer) {
        super.getMenus(menusConsumer);
    }

    @WorkbenchPartView
    public Widget getWidget() {
        return view.asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        workflow.flush();
        return super.mayClose(getCurrentModelHash());
    }

    @OnClose
    public void onClose() {
        disposeWorkflow();
    }

    @Override
    protected void loadContent() {
        try {
            services.call(loadCallback,
                          errorCallback).load(versionRecordManager.getCurrentPath());
        } catch (final Exception e) {
            // Edit only the definition, so user can fix the wrong attributes, if any.
            loadDefinition();
        }
    }

    private void loadDefinition() {
        services.call(getDefinitionCallback,
                      getDefinitionErrorCallback).get(versionRecordManager.getCurrentPath());
    }

    public DataSetDef getDataSetDef() {
        return workflow != null ? workflow.getDataSetDef() : null;
    }

    private void testDataSet() {
        assert workflow != null;
        workflow.testDataSet(new DataSetEditorWorkflow.TestDataSetCallback() {
            @Override
            public void onSuccess(final DataSet dataSet) {
                edit(dataSet);
            }

            @Override
            public void onError(final ClientRuntimeError error) {
                showError(error);
            }
        });
    }

    private void edit(final DataSetDef dataSetDef,
                      final List<DataColumnDef> columnDefs) {
        final DataSetProviderType type = dataSetDef.getProvider() != null ? dataSetDef.getProvider() : null;
        workflow = workflowFactory.edit(type);
        view.setWidget(workflow);
        workflow.edit(dataSetDef, columnDefs).showPreviewTab();
    }

    private void edit(final DataSet dataset) {
        if (dataset != null) {
            final DataSetDef dataSetDef = workflow.getDataSetDef();
            List<DataColumn> columns = dataset.getColumns();
            if (columns != null && !columns.isEmpty()) {

                // Obtain all data columns available from the resulting data set.
                List<DataColumnDef> columnDefs = new ArrayList<>(columns.size());
                for (final DataColumn column : columns) {
                    columnDefs.add(new DataColumnDef(column.getId(),
                                                     column.getColumnType()));
                }

                edit(dataSetDef,
                     columnDefs);
            } else {
                showError("Data set has no columns");
            }
        } else {
            showError("Data set is empty.");
        }
    }

    @Override
    protected void onValidate(final Command callFinished) {
        workflow.flush();
        if (!workflow.hasErrors()) {
            notification.fire(new NotificationEvent(DataSetAuthoringConstants.INSTANCE.validationOk(),
                                                    SUCCESS));
        } else {
            notification.fire(new NotificationEvent(DataSetAuthoringConstants.INSTANCE.validationFailed(),
                                                    ERROR));
        }
        callFinished.execute();
    }

    @Override
    protected void save() {
        workflow.flush();
        if (!workflow.hasErrors()) {
            savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                    commitMessage -> {
                                        final DataSetDef def = getDataSetDef();
                                        services.call(new RemoteCallback<Path>() {
                                                          @Override
                                                          public void callback(final Path path) {
                                                              DataSetDefEditorPresenter.this.getSaveSuccessCallback(getCurrentModelHash()).callback(path);
                                                              placeManager.closePlace(DataSetDefEditorPresenter.this.place);
                                                          }
                                                      },
                                                      errorCallback)
                                                .save(def,
                                                      commitMessage);
                                    }
            );
            concurrentUpdateSessionInfo = null;
        }
    }

    public int getCurrentModelHash() {
        if (getDataSetDef() == null) {
            return 0;
        }
        return getDataSetDef().hashCode();
    }

    public void disposeWorkflow() {
        if (workflow != null) {
            workflowFactory.dispose(workflow);
        }
    }

    RemoteCallback<DataSetDef> getDefinitionCallback = result -> {
        load(result,
             result != null ? result.getColumns() : null);
    };

    RemoteCallback<EditDataSetDef> loadCallback = result -> {
        load(result != null ? result.getDefinition() : null,
             result != null ? result.getColumns() : null);
    };

    ErrorCallback<Message> errorCallback = (message, throwable) -> {
        // Edit only the definition, so user can fix the wrong attributes, if any.
        loadDefinition();
        return false;
    };

    ErrorCallback<Message> getDefinitionErrorCallback = new ErrorCallback<Message>() {
        @Override
        public boolean error(Message message,
                             Throwable throwable) {
            view.hideBusyIndicator();
            showError(new ClientRuntimeError(throwable));
            return false;
        }
    };

    protected void load(final DataSetDef dataSetDef,
                        List<DataColumnDef> columns) {
        if (dataSetDef == null) {
            view.hideBusyIndicator();
            showError(DataSetAuthoringConstants.INSTANCE.dataSetNotFound());
        } else {
            changeTitleNotification.fire(new ChangeTitleWidgetEvent(place,
                                                                    buildTitle()));
            view.hideBusyIndicator();

            edit(dataSetDef, columns);
            setOriginalHash(getCurrentModelHash());
        }
    }

    protected Caller<? extends SupportsDelete> getDeleteServiceCaller() {
        return services;
    }

    protected Caller<? extends SupportsCopy> getCopyServiceCaller() {
        return services;
    }

    private void onDataSetDefRemovedEvent(@Observes DataSetDefRemovedEvent event) {
        placeManager.closePlace(place);
    }

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
        if (testDataSetRequestEvent.getContext().equals(workflow)) {
            if (!workflow.hasErrors()) {
                testDataSet();
            }
        }
    }

    void onCancelEvent(@Observes CancelRequestEvent cancelEvent) {
        checkNotNull("cancelEvent",
                     cancelEvent);
        if (cancelEvent.getContext().equals(workflow)) {
            workflow.clear();
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
        if (tabChangedEvent.getContext().equals(workflow.getEditor())) {
            workflow.clearButtons();
            String tabId = tabChangedEvent.getTabId();
            if (tabId != null && DataSetEditor.TAB_CONFIGURATION.equals(tabId)) {
                workflow.showTestButton();
            }
        }
    }
}
