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


import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.Window;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.project.service.ProjectService;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.widgets.NewDataObjectPopup;
import org.kie.workbench.common.screens.datamodeller.events.DataModelStatusChangeEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataModelerEvent;
import org.kie.workbench.common.screens.datamodeller.events.DataObjectSelectedEvent;
import org.kie.workbench.common.screens.datamodeller.model.AnnotationDefinitionTO;
import org.kie.workbench.common.screens.datamodeller.model.DataModelTO;
import org.kie.workbench.common.screens.datamodeller.model.GenerationResult;
import org.kie.workbench.common.screens.datamodeller.model.PropertyTypeTO;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.annotations.WorkbenchToolBar;
import org.uberfire.client.common.BusyPopup;
import org.uberfire.client.mvp.UberView;
import org.uberfire.lifecycle.IsDirty;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;
import org.uberfire.workbench.model.toolbar.IconType;
import org.uberfire.workbench.model.toolbar.ToolBar;
import org.uberfire.workbench.model.toolbar.ToolBarItem;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBar;
import org.uberfire.workbench.model.toolbar.impl.DefaultToolBarItem;


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

    private ToolBar toolBar;

    @Inject
    Event<DataModelerEvent> dataModelerEvent;

    @Inject
    private Event<NotificationEvent> notification;

    @Inject
    private Event<ProjectContextChangeEvent> projectContextChangeEvent;

    @Inject
    private ProjectContext workbenchContext;

    private Project currentProject;

    private DataModelTO dataModel;

    private DataModelerContext context;

    private boolean open = false;

    @WorkbenchPartTitle
    public String getTitle() {
        return Constants.INSTANCE.modelEditor_screen_name();
    }

    @WorkbenchPartView
    public UberView<DataModelerScreenPresenter> getView() {
        return view;
    }

    @OnStartup
    public void onStartup() {
        makeMenuBar();
        makeToolBar();
        initContext();
        open = true;
        processProjectChange( workbenchContext.getActiveProject() );
    }

    @IsDirty
    public boolean isDirty() {
        return getContext() != null ? getContext().isDirty() : false;
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
        clearContext();
    }

    public void onSave( final Project project ) {

        BusyPopup.showMessage( Constants.INSTANCE.modelEditor_saving() );
        if ( project == null ) {
            projectContextChangeEvent.fire( new ProjectContextChangeEvent( workbenchContext.getActiveOrganizationalUnit(),
                                                                           workbenchContext.getActiveRepository(),
                                                                           currentProject ) );
        }

        modelerService.call( new RemoteCallback<GenerationResult>() {
            @Override
            public void callback( GenerationResult result ) {
                BusyPopup.close();
                restoreModelStatus( result );
                Boolean oldDirtyStatus = getContext().isDirty();
                getContext().setDirty( false );
                notification.fire( new NotificationEvent( Constants.INSTANCE.modelEditor_notification_dataModel_saved( result.getGenerationTimeSeconds() + "" ) ) );
                if ( project != null ) {
                    loadProjectDataModel( project );
                }
                dataModelerEvent.fire(new DataModelStatusChangeEvent(DataModelerEvent.DATA_MODEL_BROWSER,
                        getDataModel(),
                        oldDirtyStatus,
                        getContext().isDirty()));
            }
        },
        new DataModelerErrorCallback( Constants.INSTANCE.modelEditor_saving_error() ) ).saveModel(getDataModel(), currentProject);

    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @WorkbenchToolBar
    public ToolBar getToolBar() {
        return this.toolBar;
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
                                        dataModel.setParentProjectName( projectRootPath.getFileName() );
                                        setDataModel( dataModel );
                                        notification.fire( new NotificationEvent( Constants.INSTANCE.modelEditor_notification_dataModel_loaded( projectRootPath.toURI() ) ) );
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
        newDataObjectPopup.setContext( getContext() );
        newDataObjectPopup.show();
    }

    private void onProjectContextChange( @Observes final ProjectContextChangeEvent event ) {
        final Project project = event.getProject();
        if ( project == null ) {
            return;
        }
        processProjectChange( project );
    }

    /*
    private void onPackageAdded( @Observes final NewPackageEvent event ) {
        //Projects are not cached so no need to do anything if this presenter is not active
        if ( isOpen() ) {

            final Package pkg = event.getPackage();
            if ( pkg != null && isOnCurrentProject(pkg)) {
                if (context != null) {
                    List<Package> packages = new ArrayList<Package>();
                    packages.add(pkg);
                    context.appendPackages(packages);
                }
            }
        }
    }
    */

    private boolean isOpen() {
        return open;
    }

    private void processProjectChange( final Project newProject ) {

        final boolean[] needsSave = new boolean[]{ false };

        if ( newProject != null && isOpen() && currentProjectChanged( newProject ) ) {
            //the project has changed.
            final String newProjectURI = newProject.getRootPath().toURI();
            final String currentProjectURI = ( currentProject != null ? currentProject.getRootPath().toURI() : "" );
            if ( getContext() != null && getContext().isDirty() ) {
                needsSave[ 0 ] = Window.confirm( Constants.INSTANCE.modelEditor_confirm_save_model_before_project_change( currentProjectURI,
                                                                                                                          newProjectURI ) );
            } else if ( currentProject != null ) {
                Window.alert( Constants.INSTANCE.modelEditor_notify_project_change( currentProjectURI,
                                                                                    newProjectURI ) );
            }
            if ( needsSave[ 0 ] ) {
                onSave( newProject );
            } else {
                loadProjectDataModel( newProject );
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

    private void restoreModelStatus( GenerationResult result ) {
        //when the model is saved without errors
        //clean the deleted dataobjects status, mark all dataobjects as persisted, etc.
        getDataModel().setPersistedStatus();
        getDataModel().updateFingerPrints( result.getObjectFingerPrints() );
    }

    /*
    private boolean isOnCurrentProject(Package pkg) {
        if (currentProject != null) {
            return currentProject.getRootPath().equals(pkg.getProjectRootPath());
        }
        return false;
    }
    */

    private void makeToolBar() {
        toolBar = new DefaultToolBar( "dataModelerToolbar" );

        org.uberfire.mvp.Command saveCommand = new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                onSave( null );
            }
        };

        org.uberfire.mvp.Command newDataObjectCommand = new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                onNewDataObject();
            }
        };

        ToolBarItem item = new DefaultToolBarItem( IconType.SAVE, Constants.INSTANCE.modelEditor_menu_save(), saveCommand );
        toolBar.addItem( item );

        item = new DefaultToolBarItem( IconType.FILE, Constants.INSTANCE.modelEditor_menu_new_dataObject(), newDataObjectCommand );
        toolBar.addItem( item );

    }

    private void makeMenuBar() {

        org.uberfire.mvp.Command newDataObjectCommand = new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                onNewDataObject();
            }
        };

        org.uberfire.mvp.Command saveCommand = new org.uberfire.mvp.Command() {
            @Override
            public void execute() {
                onSave( null );
            }
        };

        menus = MenuFactory
                .newTopLevelMenu( Constants.INSTANCE.modelEditor_menu_new_dataObject() )
                .respondsWith( newDataObjectCommand )
                .endMenu()
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
}