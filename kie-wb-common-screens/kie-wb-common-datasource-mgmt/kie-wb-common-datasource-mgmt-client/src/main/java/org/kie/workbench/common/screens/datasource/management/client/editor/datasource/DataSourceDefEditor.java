/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datasource.management.client.editor.datasource;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorer;
import org.kie.workbench.common.screens.datasource.management.client.dbexplorer.DatabaseStructureExplorerView;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.type.DataSourceDefType;
import org.kie.workbench.common.screens.datasource.management.client.util.InitializeCallback;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DataSourceDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceDefEditorService;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceRuntimeManagerClientService;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;

@Dependent
@WorkbenchEditor(identifier = "DataSourceDefEditor",
        supportedTypes = {DataSourceDefType.class})
public class DataSourceDefEditor
        extends BaseEditor<DataSourceDefEditorContent, Metadata>
        implements DataSourceDefEditorView.Presenter {

    private DataSourceDefEditorView view;

    private DataSourceDefMainPanel mainPanel;

    private DataSourceDefEditorHelper editorHelper;

    private DatabaseStructureExplorer dbStructureExplorer;

    private PopupsUtil popupsUtil;

    private PlaceManager placeManager;

    private DataSourceDefType type;

    private Caller<DataSourceDefEditorService> editorService;

    private Caller<DataSourceRuntimeManagerClientService> dataSourceManagerClient;

    private DataSourceDefEditorContent editorContent;

    private SavePopUpPresenter savePopUpPresenter;

    private DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    public DataSourceDefEditor(final DataSourceDefEditorView view,
                               final DataSourceDefMainPanel mainPanel,
                               final DataSourceDefEditorHelper editorHelper,
                               final DatabaseStructureExplorer dbStructureExplorer,
                               final PopupsUtil popupsUtil,
                               final PlaceManager placeManager,
                               final DataSourceDefType type,
                               final SavePopUpPresenter savePopUpPresenter,
                               final DeletePopUpPresenter deletePopUpPresenter,
                               final Caller<DataSourceDefEditorService> editorService,
                               final Caller<DataSourceRuntimeManagerClientService> dataSourceManagerClient) {
        super(view);
        this.view = view;
        this.mainPanel = mainPanel;
        this.editorHelper = editorHelper;
        this.dbStructureExplorer = dbStructureExplorer;
        this.popupsUtil = popupsUtil;
        this.placeManager = placeManager;
        this.type = type;
        this.savePopUpPresenter = savePopUpPresenter;
        this.deletePopUpPresenter = deletePopUpPresenter;
        this.editorService = editorService;
        this.dataSourceManagerClient = dataSourceManagerClient;
        view.init(this);
        editorHelper.init(mainPanel);
    }

    @PostConstruct
    protected void init() {
        showEditor();
        dbStructureExplorer.addHandler(new DatabaseStructureExplorerView.Handler() {
            @Override
            public void onDataSourceSelected() {
                showEditor();
            }
        });
    }

    @OnStartup
    public void onStartup(final ObservablePath path,
                          final PlaceRequest place) {
        init(path,
             place,
             type,
             true,
             false);
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose(getContent().hashCode());
    }

    @Override
    protected void loadContent() {
        editorService.call(getLoadContentSuccessCallback(),
                           new HasBusyIndicatorDefaultErrorCallback(view)).loadContent(
                versionRecordManager.getCurrentPath());
    }

    @Override
    public boolean mayClose(Integer currentHash) {
        return super.mayClose(currentHash);
    }

    @Override
    public void onSave() {
        save();
    }

    @Override
    public void onCancel() {
        placeManager.closePlace(place);
    }

    @Override
    public void onDelete() {
        safeDelete(versionRecordManager.getCurrentPath());
    }

    protected void save() {
        if (!editorHelper.isNameValid() ||
                !editorHelper.isConnectionURLValid() ||
                !editorHelper.isUserValid() ||
                !editorHelper.isPasswordValid() ||
                !editorHelper.isDriverValid()) {
            popupsUtil.showInformationPopup(editorHelper.getMessage(
                    DataSourceManagementConstants.DataSourceDefEditor_AllFieldsRequiresValidation));
        } else {
            safeSave();
        }
    }

    /**
     * Executes a safe saving of the data source by checking it's status and asking user confirmation if needed.
     */
    protected void safeSave() {
        executeSafeUpdateCommand(DataSourceManagementConstants.DataSourceDefEditor_DataSourceHasBeenReferencedForSaveMessage,
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         _save();
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         _save();
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         //do nothing;
                                     }
                                 });
    }

    /**
     * Performs the formal save of the data source.
     */
    protected void _save() {
        savePopUpPresenter.show(versionRecordManager.getCurrentPath(),
                                new ParameterizedCommand<String>() {
                                    @Override
                                    public void execute(final String commitMessage) {
                                        editorService.call(getSaveSuccessCallback(getContent().hashCode()),
                                                           new HasBusyIndicatorDefaultErrorCallback(view)
                                        ).save(versionRecordManager.getCurrentPath(),
                                               getContent(),
                                               commitMessage);
                                    }
                                }
        );
        concurrentUpdateSessionInfo = null;
    }

    /**
     * Executes a safe deletion of the data source by checking it's status and asking user confirmation if needed.
     */
    protected void safeDelete(ObservablePath currentPath) {
        executeSafeUpdateCommand(DataSourceManagementConstants.DataSourceDefEditor_DataSourceHasBeenReferencedForDeleteMessage,
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         delete(currentPath);
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         delete(currentPath);
                                     }
                                 },
                                 new Command() {
                                     @Override
                                     public void execute() {
                                         //do nothing.
                                     }
                                 }
        );
    }

    /**
     * Performs the formal deletion of the data source.
     */
    protected void delete(ObservablePath currentPath) {

        deletePopUpPresenter.show(new ParameterizedCommand<String>() {
            @Override
            public void execute(final String comment) {
                view.showBusyIndicator(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Deleting());
                editorService.call(new RemoteCallback<Void>() {
                                       @Override
                                       public void callback(Void aVoid) {
                                           view.hideBusyIndicator();
                                           notification.fire(new NotificationEvent(org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully(),
                                                                                   NotificationEvent.NotificationType.SUCCESS));
                                       }
                                   },
                                   new HasBusyIndicatorDefaultErrorCallback(view)).delete(currentPath,
                                                                                          comment);
            }
        });
    }

    /**
     * Checks current data source status prior to execute an update operation.
     */
    protected void executeSafeUpdateCommand(String onDependantsMessageKey,
                                            Command defaultCommand,
                                            Command yesCommand,
                                            Command noCommand) {
        dataSourceManagerClient.call(new RemoteCallback<DataSourceDeploymentInfo>() {
            @Override
            public void callback(DataSourceDeploymentInfo deploymentInfo) {

                if (deploymentInfo != null && deploymentInfo.wasReferenced()) {
                    popupsUtil.showYesNoPopup(CommonConstants.INSTANCE.Warning(),
                                              editorHelper.getMessage(onDependantsMessageKey),
                                              yesCommand,
                                              CommonConstants.INSTANCE.YES(),
                                              ButtonType.WARNING,
                                              noCommand,
                                              CommonConstants.INSTANCE.NO(),
                                              ButtonType.DEFAULT);
                } else {
                    defaultCommand.execute();
                }
            }
        }).getDataSourceDeploymentInfo(getContent().getDef().getUuid());
    }

    private RemoteCallback<DataSourceDefEditorContent> getLoadContentSuccessCallback() {
        return new RemoteCallback<DataSourceDefEditorContent>() {
            @Override
            public void callback(DataSourceDefEditorContent editorContent) {
                view.hideBusyIndicator();
                onContentLoaded(editorContent);
            }
        };
    }

    protected void onContentLoaded(final DataSourceDefEditorContent editorContent) {
        //Path is set to null when the Editor is closed (which can happen before async calls complete).
        if (versionRecordManager.getCurrentPath() == null) {
            return;
        }
        setContent(editorContent);
        setOriginalHash(editorContent.hashCode());
        editorHelper.loadDrivers(getLoadDriversSuccessCommand(),
                                 getLoadDriversErrorCommand());
    }

    protected DataSourceDefEditorContent getContent() {
        return editorContent;
    }

    protected void setContent(final DataSourceDefEditorContent editorContent) {
        this.editorContent = editorContent;
        view.setDataSourceName(editorContent.getDef().getName());
        editorHelper.setDataSourceDef(editorContent.getDef());
        editorHelper.setModule(editorContent.getModule());
        editorHelper.setValid(true);
    }

    public Command getLoadDriversSuccessCommand() {
        return new Command() {
            @Override
            public void execute() {
                mainPanel.setDriver(getContent().getDef().getDriverUuid());
            }
        };
    }

    public ParameterizedCommand<Throwable> getLoadDriversErrorCommand() {
        return new ParameterizedCommand<Throwable>() {
            @Override
            public void execute(Throwable parameter) {
                popupsUtil.showErrorPopup(editorHelper.getMessage(
                        DataSourceManagementConstants.DataSourceDefEditor_LoadDriversErrorMessage,
                        parameter.getMessage()));
            }
        };
    }

    @Override
    public void onShowContent() {
        DatabaseStructureExplorer.Settings settings = new DatabaseStructureExplorer.Settings()
                .dataSourceUuid(getContent().getDef().getUuid())
                .dataSourceName(getContent().getDef().getName());

        dbStructureExplorer.initialize(settings,
                                       new InitializeCallback() {
                                           @Override
                                           public void onInitializeError(Throwable throwable) {
                                               // do nothing, just stay showing the parameters edition.
                                           }

                                           @Override
                                           public void onInitializeSuccess() {
                                               showContent();
                                           }
                                       });
    }

    private void showEditor() {
        view.clearContent();
        view.setContentWidth(DataSourceDefEditorView.COL_MD_8);
        view.setContent(mainPanel);
        view.showHeaderPanel(true);
        view.showActionsPanel(true);
    }

    private void showContent() {
        view.showHeaderPanel(false);
        view.showActionsPanel(false);
        view.clearContent();
        view.setContentWidth(DataSourceDefEditorView.COL_MD_12);
        view.setContent(dbStructureExplorer);
    }
}