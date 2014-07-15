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

package org.kie.workbench.common.screens.projectimportsscreen.client.forms;

import javax.enterprise.event.Event;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.model.ProjectImports;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.uberfire.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.uberfire.client.common.BusyIndicatorView;
import org.kie.workbench.common.screens.projectimportsscreen.client.resources.i18n.ProjectConfigScreenConstants;
import org.kie.workbench.common.screens.projectimportsscreen.client.type.ProjectImportsResourceType;
import org.kie.workbench.common.services.shared.project.ProjectImportsService;
import org.kie.workbench.common.widgets.client.menu.FileMenuBuilder;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.lifecycle.OnClose;
import org.uberfire.lifecycle.OnStartup;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.model.menu.Menus;

import static org.uberfire.commons.validation.PortablePreconditions.*;

@WorkbenchEditor(identifier = "projectConfigScreen", supportedTypes = { ProjectImportsResourceType.class })
public class ProjectImportsScreenPresenter
        implements ProjectImportsScreenView.Presenter {

    private BusyIndicatorView busyIndicatorView;
    private Event<NotificationEvent> notification;

    private ProjectImportsScreenView view;

    private Caller<ProjectImportsService> importsService;
    private Caller<MetadataService> metadataService;

    private FileMenuBuilder menuBuilder;
    private Menus menus;

    private Path path;
    private ProjectImports content;
    private ImportsWidgetPresenter importsWidget;

    private boolean isReadOnly;

    public ProjectImportsScreenPresenter() {
    }

    @Inject
    public ProjectImportsScreenPresenter( @New final ProjectImportsScreenView view,
                                          @New final FileMenuBuilder menuBuilder,
                                          @New final ImportsWidgetPresenter importsWidget,
                                          final Event<NotificationEvent> notification,
                                          final BusyIndicatorView busyIndicatorView,
                                          final Caller<ProjectImportsService> importsService,
                                          final Caller<MetadataService> metadataService ) {
        this.view = view;
        this.menuBuilder = menuBuilder;
        this.importsWidget = importsWidget;
        this.importsService = importsService;
        this.metadataService = metadataService;
        this.busyIndicatorView = busyIndicatorView;
        this.notification = notification;

        view.setImports( importsWidget );

        view.setPresenter( this );
    }

    @OnStartup
    public void init( final Path path,
                      final PlaceRequest place ) {
        this.path = checkNotNull( "path",
                                  path );
        this.isReadOnly = place.getParameter( "readOnly", null ) != null;

        makeMenuBar();

        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        importsService.call(getModelSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).load( path );
    }

    private RemoteCallback<ProjectImports> getModelSuccessCallback() {
        return new RemoteCallback<ProjectImports>() {

            @Override
            public void callback( final ProjectImports projectImports ) {
                content = projectImports;
                importsWidget.setContent( content, isReadOnly );
            }
        };
    }

    private void makeMenuBar() {
        if ( isReadOnly ) {
            menus = menuBuilder.addRestoreVersion( path ).build();
        } else {
            menus = menuBuilder
                    .addSave( new Command() {
                        @Override
                        public void execute() {
                            onSave();
                        }
                    } )
                    .addCopy( path )
                    .addRename( path )
                    .addDelete( path )
                    .build();
        }
    }

    private void onSave() {
        new SaveOperationService().save( path,
                                         new CommandWithCommitMessage() {
                                             @Override
                                             public void execute( final String commitMessage ) {
                                                 busyIndicatorView.showBusyIndicator( CommonConstants.INSTANCE.Saving() );
                                                 importsService.call(getSaveSuccessCallback(),
                                                         new HasBusyIndicatorDefaultErrorCallback(busyIndicatorView)).save( path,
                                                                                                                                            content,
                                                                                                                                            view.getMetadata(),
                                                                                                                                            commitMessage );
                                             }
                                         }
                                       );

    }

    private RemoteCallback<Path> getSaveSuccessCallback() {
        return new RemoteCallback<Path>() {

            @Override
            public void callback( final Path path ) {
                busyIndicatorView.hideBusyIndicator();
                importsWidget.setNotDirty();
                notification.fire( new NotificationEvent( CommonConstants.INSTANCE.ItemSavedSuccessfully() ) );
            }
        };
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
        view.showBusyIndicator(CommonConstants.INSTANCE.Loading());
        metadataService.call(getMetadataSuccessCallback(),
                new HasBusyIndicatorDefaultErrorCallback(view)).getMetadata(path);
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

    @OnClose
    public void onClose() {
        this.path = null;
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
