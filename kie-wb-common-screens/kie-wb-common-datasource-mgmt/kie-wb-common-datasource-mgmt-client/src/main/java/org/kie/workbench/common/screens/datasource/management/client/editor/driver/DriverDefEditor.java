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

package org.kie.workbench.common.screens.datasource.management.client.editor.driver;

import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datasource.management.client.resources.i18n.DataSourceManagementConstants;
import org.kie.workbench.common.screens.datasource.management.client.type.DriverDefType;
import org.kie.workbench.common.screens.datasource.management.client.util.PopupsUtil;
import org.kie.workbench.common.screens.datasource.management.model.DriverDefEditorContent;
import org.kie.workbench.common.screens.datasource.management.model.DriverDeploymentInfo;
import org.kie.workbench.common.screens.datasource.management.service.DataSourceRuntimeManagerClientService;
import org.kie.workbench.common.screens.datasource.management.service.DriverDefEditorService;
import org.uberfire.ext.editor.commons.client.file.popups.DeletePopUpPresenter;
import org.uberfire.ext.editor.commons.client.file.popups.SavePopUpPresenter;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartTitleDecoration;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.ext.editor.commons.client.BaseEditor;
import org.uberfire.ext.widgets.common.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.ext.widgets.common.client.resources.i18n.CommonConstants;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.ext.editor.commons.client.menu.MenuItems.*;

@Dependent
@WorkbenchEditor( identifier = "DriverDefEditor",
        supportedTypes = { DriverDefType.class } )
public class DriverDefEditor
        extends BaseEditor
        implements DriverDefEditorView.Presenter {

    private DriverDefEditorView view;

    private DriverDefMainPanel mainPanel;

    private DriverDefEditorHelper editorHelper;

    private PopupsUtil popupsUtil;

    private DriverDefType type;

    private Caller<DriverDefEditorService> editorService;

    private Caller<DataSourceRuntimeManagerClientService> dataSourceManagerClient;

    private DriverDefEditorContent editorContent;

    private SavePopUpPresenter savePopUpPresenter;

    private DeletePopUpPresenter deletePopUpPresenter;

    @Inject
    public DriverDefEditor( final DriverDefEditorView view,
            final DriverDefMainPanel mainPanel,
            final DriverDefEditorHelper editorHelper,
            final PopupsUtil popupsUtil,
            final DriverDefType type,
            final SavePopUpPresenter savePopUpPresenter,
            final DeletePopUpPresenter deletePopUpPresenter,
            final Caller<DriverDefEditorService> editorService,
            final Caller<DataSourceRuntimeManagerClientService> dataSourceManagerClient ) {
        super( view );
        this.view = view;
        this.mainPanel = mainPanel;
        this.editorHelper = editorHelper;
        this.popupsUtil = popupsUtil;
        this.type = type;
        this.savePopUpPresenter = savePopUpPresenter;
        this.deletePopUpPresenter = deletePopUpPresenter;
        this.editorService = editorService;
        this.dataSourceManagerClient = dataSourceManagerClient;
        view.init( this );
        view.setMainPanel( mainPanel );
        editorHelper.init( mainPanel );
    }

    @OnStartup
    public void onStartup( final ObservablePath path, final PlaceRequest place ) {
        init( path,
                place,
                type,
                true,
                false,
                SAVE );
    }

    @WorkbenchPartTitleDecoration
    public IsWidget getTitle() {
        return super.getTitle();
    }

    @WorkbenchPartTitle
    public String getTitleText() {
        return super.getTitleText();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchPartView
    public IsWidget getWidget() {
        return view.asWidget();
    }

    @OnMayClose
    public boolean onMayClose() {
        return super.mayClose( getContent().hashCode() );
    }

    @Override
    protected void loadContent() {
        editorService.call( getLoadContentSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback( view ) ).loadContent(
                versionRecordManager.getCurrentPath() );
    }

    @Override
    public boolean mayClose( Integer currentHash ) {
        return super.mayClose( currentHash );
    }

    @Override
    protected void makeMenuBar() {
        super.makeMenuBar();
        menuBuilder.addDelete( onDelete( versionRecordManager.getCurrentPath() ) );
        menuBuilder.addValidate( onValidate() );
        addTestStatusMenu();
    }

    /**
     * Save method initially executed by the save menu entry.
     */
    @Override
    protected void save() {
        safeSave();
    }

    /**
     * Executes a safe saving of the driver by checking if there are dependant data sources that may be affected by
     * the change.
     */
    protected void safeSave() {
        executeSafeUpdateCommand( DataSourceManagementConstants.DriverDefEditor_DriverHasDependantsForSaveMessage,
                new Command() {
                    @Override public void execute() {
                        _save( );
                    }
                },
                new Command() {
                    @Override public void execute() {
                        _save( );
                    }
                },
                new Command() {
                    @Override public void execute() {
                        //do nothing;
                    }
                } );
    }

    /**
     * Performs the formal save of the driver.
     */
    protected void _save( ) {
        savePopUpPresenter.show( versionRecordManager.getCurrentPath(),
                new ParameterizedCommand<String>() {
                    @Override
                    public void execute( final String commitMessage ) {
                        editorService.call( getSaveSuccessCallback( getContent().hashCode() ),
                                new HasBusyIndicatorDefaultErrorCallback( view ) ).save( versionRecordManager.getCurrentPath(),
                                getContent(),
                                commitMessage );
                    }
                }
        );
        concurrentUpdateSessionInfo = null;
    }

    /**
     * Checks if current driver has dependant data sources prior to execute an update operation.
     */
    protected void executeSafeUpdateCommand( String onDependantsMessageKey,
            Command defaultCommand, Command yesCommand, Command noCommand ) {
        dataSourceManagerClient.call( new RemoteCallback<DriverDeploymentInfo>() {
            @Override
            public void callback( DriverDeploymentInfo deploymentInfo ) {

                if ( deploymentInfo != null && deploymentInfo.hasDependants() ) {
                    popupsUtil.showYesNoPopup( CommonConstants.INSTANCE.Warning(),
                            editorHelper.getMessage( onDependantsMessageKey ),
                            yesCommand,
                            CommonConstants.INSTANCE.YES(),
                            ButtonType.WARNING,
                            noCommand,
                            CommonConstants.INSTANCE.NO(),
                            ButtonType.DEFAULT );
                } else {
                    defaultCommand.execute();
                }
            }
        } ).getDriverDeploymentInfo( getContent().getDriverDef().getUuid() );
    }

    /**
     * Creates the deletion command to be invoked by the delete menu entry.
     */
    protected Command onDelete( ObservablePath currentPath ) {
        return new Command() {
            @Override
            public void execute() {
                safeDelete( currentPath );
            }
        };
    }

    /**
     * Executes a safe deletion of the driver by checking if there are dependant data sources that may be affected by
     * the change.
     */
    protected void safeDelete( ObservablePath currentPath ) {
        executeSafeUpdateCommand( DataSourceManagementConstants.DriverDefEditor_DriverHasDependantsForDeleteMessage,
                new Command() {
                    @Override public void execute() {
                        delete( currentPath );
                    }
                },
                new Command() {
                    @Override public void execute() {
                        delete( currentPath );
                    }
                },
                new Command() {
                    @Override public void execute() {
                        //do nothing.
                    }
                }
        );
    }

    /**
     * Performs the formal delete of the driver.
     */
    protected void delete( ObservablePath currentPath ) {

        deletePopUpPresenter.show( new ParameterizedCommand<String>() {
            @Override
            public void execute( final String comment ) {
                view.showBusyIndicator( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.Deleting() );
                editorService.call( new RemoteCallback<Void>() {
                    @Override public void callback( Void aVoid ) {
                        view.hideBusyIndicator();
                        notification.fire( new NotificationEvent( org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemDeletedSuccessfully(),
                                NotificationEvent.NotificationType.SUCCESS ) );
                    }
                } , new HasBusyIndicatorDefaultErrorCallback( view ) ).delete( currentPath, comment );
            }
        } );
    }

    /**
     * Validate command executed by the validate menu entry.
     */
    @Override
    protected Command onValidate() {
        return new Command() {
            @Override
            public void execute() {
                validate();
            }
        };
    }

    protected void validate() {
        editorService.call(
                getValidationSuccessCallback() ).validate( getContent().getDriverDef() );
    }

    private RemoteCallback<DriverDefEditorContent> getLoadContentSuccessCallback() {
        return new RemoteCallback<DriverDefEditorContent>() {
            @Override
            public void callback( DriverDefEditorContent editorContent ) {
                view.hideBusyIndicator();
                onContentLoaded( editorContent );
            }
        };
    }

    protected void onContentLoaded( final DriverDefEditorContent editorContent ) {
        //Path is set to null when the Editor is closed (which can happen before async calls complete).
        if ( versionRecordManager.getCurrentPath() == null ) {
            return;
        }
        setContent( editorContent );
        setOriginalHash( editorContent.hashCode() );
    }

    protected DriverDefEditorContent getContent() {
        return editorContent;
    }

    protected void setContent( final DriverDefEditorContent editorContent ) {
        this.editorContent = editorContent;
        this.editorHelper.setDriverDef( editorContent.getDriverDef() );
        editorHelper.setValid( true );
    }

    private RemoteCallback<List<ValidationMessage>> getValidationSuccessCallback() {
        return new RemoteCallback<List<ValidationMessage>>() {
            @Override
            public void callback( List<ValidationMessage> messages ) {

                if ( messages == null || messages.isEmpty() ) {
                    notification.fire( new NotificationEvent(
                            org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants.INSTANCE.ItemValidatedSuccessfully(),
                            NotificationEvent.NotificationType.SUCCESS ) );
                } else {
                    popupsUtil.showValidationMessages( messages );
                }
            }
        };
    }

    //Check if we want to keep this check status option in the future.
    private void addTestStatusMenu() {
        menuBuilder.addNewTopLevelMenu( MenuFactory.newTopLevelMenu(
                editorHelper.getMessage( DataSourceManagementConstants.DriverDefEditor_CheckStatusMenu ) )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        onCheckStatus();
                    }
                } )
                .endMenu()
                .build().getItems().get( 0 ) );
    }

    private void onCheckStatus() {
        dataSourceManagerClient.call( new RemoteCallback<DriverDeploymentInfo>() {
            @Override
            public void callback( DriverDeploymentInfo deploymentInfo ) {
                StringBuilder builder = new StringBuilder();
                if ( deploymentInfo == null ) {
                    builder.append( editorHelper.getMessage(
                            DataSourceManagementConstants.DriverDefEditor_DriverNotRegisteredMessage,
                            getContent().getDriverDef().getUuid() ) );
                } else if ( !deploymentInfo.hasDependants() ) {
                    builder.append( editorHelper.getMessage(
                            DataSourceManagementConstants.DriverDefEditor_DriverHasNoDependantsMessage ) );
                } else {
                    builder.append( editorHelper.getMessage(
                            DataSourceManagementConstants.DriverDefEditor_DriverHasDependantsMessage,
                            deploymentInfo.getDependants().size() ) );
                }
                popupsUtil.showInformationPopup( builder.toString() );
            }
        } ).getDriverDeploymentInfo( getContent().getDriverDef().getUuid() );
    }
}