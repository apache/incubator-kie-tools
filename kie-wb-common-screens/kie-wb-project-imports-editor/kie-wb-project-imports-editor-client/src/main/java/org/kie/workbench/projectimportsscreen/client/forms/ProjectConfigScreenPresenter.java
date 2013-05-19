/*
 * Copyright 2012 JBoss Inc
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

package org.kie.workbench.projectimportsscreen.client.forms;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;

import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.services.shared.metadata.MetadataService;
import org.kie.workbench.common.services.shared.metadata.model.Metadata;
import org.kie.workbench.common.services.shared.version.events.RestoreEvent;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.guvnor.project.model.ProjectImports;
import org.kie.guvnor.project.service.ProjectService;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.projectimportsscreen.client.resources.i18n.ProjectConfigScreenConstants;
import org.kie.workbench.projectimportsscreen.client.type.ProjectImportsResourceType;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.IsDirty;
import org.uberfire.client.annotations.OnClose;
import org.uberfire.client.annotations.OnMayClose;
import org.uberfire.client.annotations.OnSave;
import org.uberfire.client.annotations.OnStart;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.mvp.Command;
import org.uberfire.client.workbench.widgets.events.NotificationEvent;
import org.uberfire.client.workbench.widgets.menu.Menus;

@WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = { ProjectImportsResourceType.class })
public class ProjectConfigScreenPresenter
        implements ProjectConfigScreenView.Presenter {

    private ProjectConfigScreenView view;

    private Caller<ProjectService> projectService;
    private Caller<MetadataService> metadataService;
    private ProjectImports packageConfiguration;

    private Event<NotificationEvent> notification;

    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private Path path;
    private boolean isReadOnly;

    public ProjectConfigScreenPresenter() {
    }

    @Inject
    public ProjectConfigScreenPresenter( @New ProjectConfigScreenView view,
                                         @New FileMenuBuilder menuBuilder,
                                         Caller<ProjectService> projectService,
                                         Caller<MetadataService> metadataService,
                                         Event<NotificationEvent> notification ) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.projectService = projectService;
        this.metadataService = metadataService;
        this.notification = notification;
        view.setPresenter( this );
    }

    @OnStart
    public void init( final Path path ) {
        this.path = path;

        makeMenuBar();

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        loadContent();
    }

    private void loadContent() {
        projectService.call( getModelSuccessCallback(),
                             new HasBusyIndicatorDefaultErrorCallback( view ) ).load( path );
    }

    private void makeMenuBar() {
        menus = menuBuilder.addSave( new Command() {
            @Override
            public void execute() {
                onSave();
            }
        } ).build();
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectConfigScreenConstants.INSTANCE.ExternalImports();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onShowMetadata() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );
        metadataService.call( getMetadataSuccessCallback(),
                              new HasBusyIndicatorDefaultErrorCallback( view ) ).getMetadata( path );
    }

    private RemoteCallback<ProjectImports> getModelSuccessCallback() {
        return new RemoteCallback<ProjectImports>() {

            @Override
            public void callback( final ProjectImports response ) {
                ProjectConfigScreenPresenter.this.packageConfiguration = response;
                view.setImports( path,
                                 packageConfiguration.getImports() );
                view.hideBusyIndicator();
            }
        };
    }

    private RemoteCallback<Metadata> getMetadataSuccessCallback() {
        return new RemoteCallback<Metadata>() {

            @Override
            public void callback( final Metadata metadata ) {
                view.setMetadata( metadata );
                view.hideBusyIndicator();
            }
        };
    }

    @OnSave
    public void onSave() {
        if ( isReadOnly ) {
            view.alertReadOnly();
            return;
        }

        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String comment ) {
                                                 view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 projectService.call( getSaveSuccessCallback(),
                                                                      new HasBusyIndicatorDefaultErrorCallback( view ) ).save( path,
                                                                                                                               packageConfiguration,
                                                                                                                               view.getMetadata(),
                                                                                                                               comment );
                                             }
                                         } );
    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                view.setNotDirty();
                view.hideBusyIndicator();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        };
    }

    @IsDirty
    public boolean isDirty() {
        return view.isDirty();
    }

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @OnMayClose
    public boolean checkIfDirty() {
        if ( isDirty() ) {
            return view.confirmClose();
        }
        return true;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    public void onRestore( @Observes RestoreEvent restore ) {
        if ( path == null || restore == null || restore.getPath() == null ) {
            return;
        }
        if ( path.equals( restore.getPath() ) ) {
            loadContent();
            notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemRestored() ) );
        }
    }

}
