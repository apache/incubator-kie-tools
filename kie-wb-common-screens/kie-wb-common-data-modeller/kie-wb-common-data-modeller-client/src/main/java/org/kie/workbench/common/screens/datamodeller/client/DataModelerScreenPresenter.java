/**
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client;


import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.kie.workbench.common.screens.messageconsole.events.UnpublishMessagesEvent;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.DataModelerUtils;
import org.kie.workbench.common.screens.datamodeller.client.widgets.NewDataObjectPopup;
import org.kie.workbench.common.screens.datamodeller.events.*;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.DataObjectTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.common.YesNoCancelPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.client.resources.i18n.CommonConstants;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.*;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.*;

import static org.kie.workbench.common.widgets.client.popups.project.ProjectConcurrentChangePopup.newConcurrentChange;
import static org.kie.workbench.common.widgets.client.popups.project.ProjectConcurrentChangePopup.newConcurrentUpdate;

//@Dependent
@WorkbenchScreen(identifier = "dataModelerScreen")
public class DataModelerScreenPresenter {

    public interface DataModelerScreenView
            extends
            UberView<DataModelerScreenPresenter> {

        void setContext( DataModelerContext context );

        boolean confirmClose();

    }

    @Inject
    private DataModelerScreenView view;

    @Inject
    private NewDataObjectPopup newDataObjectPopup;

    @Inject
    private Caller<DataModelerService> modelerService;

    @Inject
    private Caller<ProjectService> projectService;

    private Menus menus;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    @Inject
    private Event<UnpublishMessagesEvent> unpublishMessagesEvent;

    @Inject
    private ProjectContext workbenchContext;

    private Project currentProject;

    private DataModelTO dataModel;

    private DataModelerContext context;

    private boolean open = false;

    @Inject
    private SessionInfo sessionInfo;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.modelEditor_screen_name();
    }

    @WorkbenchPartView
    public UberView<DataModelerScreenPresenter> getView() {
        return view;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @OnStartup
    public void onStartup() {
        makeMenuBar();
        initContext();
        open = true;
        cleanSystemMessages();
        processProjectChange( workbenchContext.getActiveProject() );
    }

    @IsDirty
    public boolean isDirty() {
        return getContext() != null && getContext().isDirty();
    }

    @OnMayClose
    public boolean onMayClose() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @OnClose
    public void OnClose() {
        open = false;
        cleanSystemMessages();
        clearContext();
    }

    /*
     * The common use case, when the user clicks on the "Save" menu.
     */
    public void onSave() {

        if (getContext().isDMOInvalidated()) {

            //if the DMO was modified by another user we need to give the user the opportunity to force saving his
            //changes or reload the model.

            newConcurrentUpdate( getContext().getLastDMOUpdate().getProject().getRootPath(),
                    getContext().getLastDMOUpdate().getSessionInfo().getIdentity(),
                    new Command() {
                        @Override
                        public void execute() {
                            //force save.
                            saveAndChangeProject(true, null);
                        }
                    },
                    new Command() {
                        @Override
                        public void execute() {
                            //cancel
                            //the editor remains in current status.
                        }
                    },
                    new Command() {
                        @Override
                        public void execute() {
                            //re-open, local changes will be discarded.
                            reload();
                        }
                    }
            ).show();
        } else {
            //no concurrency problems, we can save the model directly.
            saveAndChangeProject(false, null);
        }
    }

    /*
     * Less normal use case, when the data modeler is open, and the user selects a distinct project in the project explorer.
     * The user will have the option to save/discard/or force save prior to navigate to the next project.
     *
     */
    public void onSaveAndChange( final Project changeProject ) {

        if (getContext().isDMOInvalidated()) {

            //The user selected to save current modifications prior to open next project, but the DMO was modified.
            //we need to ask the user if he wants to force writing his changes, or he prefer to discard them.

            final String newProjectURI = changeProject.getRootPath().toURI();
            final String currentProjectURI = ( currentProject != null ? currentProject.getRootPath().toURI() : "" );
            final String externalUser = getContext().getLastDMOUpdate() != null ? getSessionInfoIdentity(getContext().getLastDMOUpdate().getSessionInfo()) : null;

            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Error(),

                    Constants.INSTANCE.modelEditor_confirm_save_model_before_project_change_force(SafeHtmlUtils.htmlEscape(externalUser != null ? externalUser : ""), currentProjectURI ),
                    new Command() {
                        @Override
                        public void execute() {
                            //force my changes to be written and then change the project.
                            saveAndChangeProject(true, changeProject);
                        }
                    },
                    Constants.INSTANCE.modelEditor_action_yes_force_save(),
                    ButtonType.PRIMARY,
                    IconType.PLUS_SIGN,
                    new Command() {
                        @Override
                        public void execute() {
                            //discard my changes and load next project directly.
                            loadProjectDataModel(changeProject);
                        }
                    },
                    Constants.INSTANCE.modelEditor_action_no_discard_changes(),
                    ButtonType.DANGER,
                    null,
                    null,
                    null,
                    null,
                    null
            );
            yesNoCancelPopup.setCloseVisible(true);
            yesNoCancelPopup.show();

        } else {
            //no problem, save my changes and load next project.
            saveAndChangeProject(false, changeProject);
        }
    }

    /**
     * This method saves currentProject, and gives the opportunity to load another project after saving, the changeProject.
     * Also if the overwrite attribute is true, we will ensure that currentProject pojos will overwrite existing ones.
     * This attribute is used in cases where the project model was concurrently modified during edition and we want
     * to force a rewriting.
     *
     * @param overwrite. true, ensures that what you see in the UI will be the model after generation. This attribute
     *                   is set to true in when we want to force a model rewriting. false, indicates that we'll proceed
     *                   as in the normal sequence when the DMO wasn't concurrently modified.
     *
     * @param changeProject if this parameter is set, currentProject will be saved, and then the changeProject will be
     *                      loaded.
     */
    private void saveAndChangeProject(boolean overwrite, final Project changeProject) {

        BusyPopup.showMessage( Constants.INSTANCE.modelEditor_saving() );

        modelerService.call(
            new RemoteCallback<GenerationResult>() {
                @Override
                public void callback( GenerationResult result ) {
                    BusyPopup.close();
                    restoreModelStatus( result );
                    Boolean oldDirtyStatus = getContext().isDirty();
                    getContext().setDirty( false );
                    getContext().setLastDMOUpdate( null );
                    getContext().setLastJavaFileChangeEvent( null );
                    notification.fire( new NotificationEvent( Constants.INSTANCE.modelEditor_notification_dataModel_saved( result.getGenerationTimeSeconds() + "" ) ) );

                    if ( changeProject != null ) {
                        loadProjectDataModel( changeProject );
                    }

                    dataModelerEvent.fire(new DataModelStatusChangeEvent(DataModelerEvent.DATA_MODEL_BROWSER,
                            getDataModel(),
                            oldDirtyStatus,
                            getContext().isDirty()));

                    dataModelerEvent.fire(new DataModelSaved(null, getDataModel()));

                }
            }, new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_saving_error() ) ).saveModel(getDataModel(), currentProject, overwrite);
    }

    private void loadProjectDataModel( final Project project ) {

        BusyPopup.showMessage( Constants.INSTANCE.modelEditor_loading() );

        final Path projectRootPath = project.getRootPath();

        modelerService.call( new RemoteCallback<Map<String, AnnotationDefinitionTO>>() {
            @Override
            public void callback( final Map<String, AnnotationDefinitionTO> defs ) {

                context.setAnnotationDefinitions( defs );

                projectService.call( new RemoteCallback<Collection<Package>>() {

                    public void callback(Collection<Package> packages) {

                        context.cleanPackages();
                        context.appendPackages(packages);

                        modelerService.call(
                                new RemoteCallback<DataModelTO>() {

                                    @Override
                                    public void callback( DataModelTO dataModel ) {
                                        BusyPopup.close();

                                        getContext().setDirty( false );
                                        getContext().setLastDMOUpdate(null);
                                        getContext().setLastJavaFileChangeEvent(null);

                                        dataModel.setParentProjectName( projectRootPath.getFileName() );
                                        setDataModel( dataModel );
                                        notification.fire( new NotificationEvent( Constants.INSTANCE.modelEditor_notification_dataModel_loaded( projectRootPath.toURI() ) ) );
                                        showReadonlyStateInfo();
                                    }

                                },
                                new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() ) ).loadModel(project);

                    }
                }, new DataModelerErrorCallback(Constants.INSTANCE.modelEditor_loading_error())).resolvePackages(project);
            }
        },new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_annotationDef_loading_error() )
        ).getAnnotationDefinitions();

        currentProject = project;
    }

    public DataModelTO getDataModel() {
        return dataModel;
    }

    public DataModelerContext getContext() {
        return context;
    }

    private void setDataModel( DataModelTO dataModel ) {
        this.dataModel = dataModel;

        // Set data model helper before anything else
        if ( dataModel != null ) {
            context.setDataModel( dataModel );
            view.setContext( context );
            if ( dataModel.getDataObjects().size() > 0 ) {
                dataModelerEvent.fire( new DataObjectSelectedEvent( DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), dataModel.getDataObjects().get( 0 ) ) );
            } else {
                dataModelerEvent.fire( new DataObjectSelectedEvent( DataModelerEvent.DATA_MODEL_BROWSER, getDataModel(), null ) );
            }
        }
    }

    private void onNewDataObject() {

        if (getContext().isDMOInvalidated()) {
            newConcurrentChange( getContext().getLastDMOUpdate().getProject().getRootPath(),
                    getContext().getLastDMOUpdate().getSessionInfo().getIdentity(),
                    new Command() {
                        @Override
                        public void execute() {
                            //ignore external changes
                            newDataObjectPopup.setContext( getContext() );
                            newDataObjectPopup.show();
                        }
                    },
                    new Command() {
                        @Override
                        public void execute() {
                            //re-open
                            reload();
                        }
                    }
            ).show();
        } else {
            newDataObjectPopup.setContext( getContext() );
            newDataObjectPopup.show();
        }
    }

    private void onProjectContextChange( @Observes final ProjectContextChangeEvent event ) {
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        processProjectChange( project );
    }

    private void onDataModelReload( @Observes final DataModelReload event) {
        if (event.isFrom(getDataModel())) {
            //Some of the related widgets has sent this event in order to re-load the model.
            reload();
        }
    }

    private void onResourceAdded(@Observes  final ResourceAddedEvent resourceAddedEvent) {
        processExternalFileChange(resourceAddedEvent.getSessionInfo(), resourceAddedEvent);
    }

    private void onResourceRenamed(@Observes final ResourceRenamedEvent resourceRenamedEvent) {
        processExternalFileChange(resourceRenamedEvent.getSessionInfo(), resourceRenamedEvent);
    }

    private void onResourceDeleted(@Observes final ResourceDeletedEvent resourceDeletedEvent) {
        processExternalFileChange(resourceDeletedEvent.getSessionInfo(), resourceDeletedEvent);
    }

    private void onResourceUpdated(@Observes final ResourceUpdatedEvent resourceUpdatedEvent) {
        processExternalFileChange(resourceUpdatedEvent.getSessionInfo(), resourceUpdatedEvent);
    }

    private void onResourceCopied(@Observes final ResourceCopiedEvent resourceCopiedEvent) {
        processExternalFileChange(resourceCopiedEvent.getSessionInfo(), resourceCopiedEvent);
    }

    private void processExternalFileChange(SessionInfo evtSessionInfo, ResourceEvent resourceEvent) {
        if (sessionInfo.equals(evtSessionInfo) && resourceEvent.getPath().getFileName().endsWith(".java")
            && currentProject != null && resourceEvent.getPath().toURI().startsWith(currentProject.getRootPath().toURI())) {

            boolean notifyChange = false;
            Path path = null;

            if (resourceEvent instanceof ResourceRenamedEvent || resourceEvent instanceof ResourceCopiedEvent) {
                //datamodeller never generates these events
                path = resourceEvent instanceof ResourceRenamedEvent ? ((ResourceRenamedEvent)resourceEvent).getDestinationPath() : ((ResourceCopiedEvent)resourceEvent).getDestinationPath();
                notifyChange = true;
            } else if (resourceEvent instanceof ResourceAddedEvent) {
                path = resourceEvent.getPath();
                if (getDataModel() != null) {
                    String className = DataModelerUtils.calculateExpectedClassName(currentProject.getRootPath(), resourceEvent.getPath());
                    if (className != null) {
                        DataObjectTO dataObjectTO = getDataModel().getDataObjectByClassName(className);
                        if (dataObjectTO == null || dataObjectTO.isVolatile()) {
                            notifyChange = true;
                        }
                    }
                }
            } else if (resourceEvent instanceof ResourceDeletedEvent) {
                path = resourceEvent.getPath();
                if (getDataModel() != null) {
                    String className = DataModelerUtils.calculateExpectedClassName(currentProject.getRootPath(), resourceEvent.getPath());
                    if (className != null) {
                        DataObjectTO dataObjectTO = getDataModel().getDataObjectByClassName(className);
                        if (dataObjectTO != null) {
                            notifyChange = true;
                        } else {
                            for (DataObjectTO deletedObject : getDataModel().getDeletedDataObjects()) {
                                if (className.equals(deletedObject.getClassName())) {
                                    notifyChange = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (resourceEvent instanceof ResourceUpdatedEvent) {
                //TODO:
                //at the moment the only editor that can update a .java file in this same session and same user is the
                //datamodeller. So when we reach this point it means that the ResourceUpdateEvent for this .java file
                //was produced when this datamodeler screen updated the file. Also this case is produced only when
                //the datamodeller modifies one file. (if more than one file is deleted, modified, created, a
                // ResourceBatchChangesEvent is produced instead.
                // So no extra control is needed in this case.

                //modelerService.call( new FileVerificationRemoteCallback(evtSessionInfo, resourceEvent.getPath()),
                        //new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_loading_error() ) ).verifiesHash(resourceEvent.getPath());
            }

            if (notifyChange) {
                InvalidateDMOProjectCacheEvent event = new InvalidateDMOProjectCacheEvent(sessionInfo, currentProject, path);
                getContext().setLastJavaFileChangeEvent(event);
                if (getContext().isDirty()) {
                    notifyExternalDMOChange(event);
                }
            }
        }
    }

    private class FileVerificationRemoteCallback implements RemoteCallback<Boolean> {

        private SessionInfo sessionInfo;

        private Path path;

        private FileVerificationRemoteCallback() {
        }

        public FileVerificationRemoteCallback(SessionInfo sessionInfo, Path path) {
            this.sessionInfo = sessionInfo;
            this.path = path;
        }

        @Override
        public void callback(Boolean verifiesHash) {
            /**
            if (!verifiesHash) {
                InvalidateDMOProjectCacheEvent event = new InvalidateDMOProjectCacheEvent(sessionInfo, currentProject, path);
                getContext().setLastJavaFileChangeEvent(event);

                if (getContext().isDirty()) {
                    notifyExternalDMOChange(event);
                }
            }
            **/
        }
    }

    private boolean isOpen() {
        return open;
    }

    /**
     * This method is invoked when we detect that currentProject changed. Current project can change due to the following
     * two scenarios.
     * 1) The window is being opened at this moment, so we need to load the newProject for the first time.
     * 2) The window was already opened and the user navigated to another project with the project explorer.
     */
    private void processProjectChange( final Project newProject ) {

        if ( newProject != null && isOpen() && currentProjectChanged( newProject ) ) {
            //the project has changed and we have pending changes to save.
            final String newProjectURI = newProject.getRootPath().toURI();
            final String currentProjectURI = ( currentProject != null ? currentProject.getRootPath().toURI() : "" );
            if ( getContext() != null && getContext().isDirty() ) {
                //when the project changed, and the current project is dirty we give the opportunity to the user
                //to save current changes prior to open the destination project.
                YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Warning(),
                        Constants.INSTANCE.modelEditor_confirm_save_model_before_project_change( currentProjectURI,
                        newProjectURI ),
                        new Command() {
                            @Override
                            public void execute() {
                                //ok, the user wants to save current changes and then open next project.
                                //save current project and open the new project.
                                onSaveAndChange(newProject);
                            }
                        },
                        new Command() {
                            @Override
                            public void execute() {
                                //not save current changes, simply open the new project.
                                loadProjectDataModel(newProject);
                            }
                        },
                        null
                );
                yesNoCancelPopup.setCloseVisible(false);
                yesNoCancelPopup.show();
            } else if ( currentProject != null ) {
                //no pending changes, so simply notify the user that another project will be opened.
                YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Information(),
                        Constants.INSTANCE.modelEditor_notify_project_change( currentProjectURI, newProjectURI ),
                        new Command() {
                            @Override
                            public void execute() {
                                //simply open the new project.
                                loadProjectDataModel(newProject);
                            }
                        },
                        CommonConstants.INSTANCE.OK(),
                        null,
                        null,
                        null,
                        null
                );
                yesNoCancelPopup.setCloseVisible(false);
                yesNoCancelPopup.show();
            } else {
                //if currentProject is null, we are at the window opening type, simply load the data model
                //to start working.
                loadProjectDataModel(newProject);
            }
        } else {
            //TODO check if this is possible. By definition we will always have a path.
        }
    }

    private boolean currentProjectChanged( final Project newProject ) {
        if ( currentProject == null ) {
            return true;
        }
        return !newProject.getRootPath().equals( currentProject.getRootPath() );
    }

    private void processDMOChange(@Observes InvalidateDMOProjectCacheEvent evt) {
        /*
        Window.alert("InvalidateDMOProjectCacheEvent was received: \n" +
                " currentSessionInfo: \n" + printSessionInfo(sessionInfo) +
                " eventSessionInfo: " + printSessionInfo(evt.getSessionInfo()) + ", path: " + evt.getResourcePath() +
                " \n eventProject: " + evt.getProject() + " \n" +
                " eventID: " + evt.getEventId());
        */
        //filter if the event is related to current project
        if (currentProject != null && currentProject.getRootPath().equals( evt.getProject().getRootPath() ) && sessionInfo != null ) {


            if (!sessionInfo.equals(evt.getSessionInfo())
                    || isDMOChangeSensitivePath(evt.getResourcePath()) ) {
                //the project data model oracle was changed because of another user different than me OR
                //the modification was done by me, executing in the same session but saving the project
                //likely in the project editor.

                //current project DMO was concurrently modified.
                getContext().setLastDMOUpdate(evt);

                if (getContext().isDirty()) {
                    notifyExternalDMOChange(evt);
                }
            } else {
                //the event was generated when I saved the model
                getContext().setLastDMOUpdate(null);
            }
        }
    }

    boolean isDMOChangeSensitivePath(Path path) {
        return path != null &&
        (path.getFileName().equals("pom.xml") ||
         path.getFileName().endsWith(".drl") ||
         path.getFileName().equals("kmodule.xml") ||
         path.getFileName().equals("project.imports") ||
         path.getFileName().equals("import.suggestions") );
    }

    private void notifyExternalDMOChange(InvalidateDMOProjectCacheEvent evt) {

        newConcurrentChange( evt.getProject().getRootPath(),
                evt.getSessionInfo().getIdentity(),
                new Command() {
                    @Override
                    public void execute() {
                        //ignore, do nothing.
                    }
                },
                new Command() {
                    @Override
                    public void execute() {
                        reload();
                    }
                }
        ).show();

    }

    private void reload() {
        loadProjectDataModel(currentProject);
    }

    private void restoreModelStatus( GenerationResult result ) {
        //when the model is saved without errors
        //clean the deleted dataobjects status, mark all dataobjects as persisted (except readonly objects), etc.
        getDataModel().setPersistedStatus( false );
        getDataModel().updateFingerPrints( result.getObjectFingerPrints() );
    }

    private void cleanSystemMessages() {
        UnpublishMessagesEvent unpublishMessage = new UnpublishMessagesEvent();
        unpublishMessage.setShowSystemConsole( false );
        unpublishMessage.setMessageType( "DataModeler" );
        unpublishMessage.setUserId( (sessionInfo != null && sessionInfo.getIdentity() != null) ? sessionInfo.getIdentity().getName() : null );
        unpublishMessagesEvent.fire( unpublishMessage );
    }

    private void showReadonlyStateInfo() {
        final DataModelTO dataModelTO = getDataModel();
        final List<String> readonlyObjects = new ArrayList<String>();
        final StringBuilder message = new StringBuilder();
        boolean isFirst = true;

        message.append(Constants.INSTANCE.modelEditor_notify_externally_modified_objects_read());
        message.append("</BR>");
        message.append("</BR>");

        if (dataModelTO != null && dataModelTO.getDataObjects() != null) {
            for (DataObjectTO dataObjectTO : dataModelTO.getDataObjects()) {
                if (dataObjectTO.isExternallyModified()) {
                    readonlyObjects.add(dataObjectTO.getClassName());
                }
            }
        }

        Collections.sort(readonlyObjects);
        for (String readonlyObject : readonlyObjects) {
            if (!isFirst) {
                message.append("</BR>");
            }
            message.append(readonlyObject);
            isFirst = false;
        }

        if (readonlyObjects.size() > 0) {
            YesNoCancelPopup yesNoCancelPopup = YesNoCancelPopup.newYesNoCancelPopup(CommonConstants.INSTANCE.Information(),
                    message.toString(),
                    new Command() {
                        @Override
                        public void execute() {
                            //do nothing.
                        }
                    },
                    CommonConstants.INSTANCE.OK(),
                    null,
                    null,
                    null,
                    null
            );
            yesNoCancelPopup.setCloseVisible(false);
            yesNoCancelPopup.show();
        }
    }

    private void makeMenuBar() {

        org.uberfire.mvp.Command saveCommand = new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                onSave();
            }
        };

        menus = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.modelEditor_menu_save() )
                .respondsWith( saveCommand )
                .endMenu()
                .build();
    }

    private void initContext() {
        context = new DataModelerContext();

        modelerService.call(
                new RemoteCallback<List<PropertyTypeTO>>() {
                    @Override
                    public void callback( List<PropertyTypeTO> baseTypes ) {
                        context.init( baseTypes );
                    }
                },
                new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_propertyType_loading_error() )
        ).getBasePropertyTypes();
    }

    private void clearContext() {
        context.clear();
    }

    private static String printSessionInfo(SessionInfo sessionInfo) {
        if (sessionInfo != null) {
            return " [id: " + sessionInfo.getId() + ", identity: " + (sessionInfo.getIdentity() != null ? sessionInfo.getIdentity().getName() : null) + " ]";
        }
        return null;
    }

    private static String getSessionInfoIdentity(SessionInfo sessionInfo) {
        return sessionInfo != null && sessionInfo.getIdentity() != null ? sessionInfo.getIdentity().getName() : null;
    }
}