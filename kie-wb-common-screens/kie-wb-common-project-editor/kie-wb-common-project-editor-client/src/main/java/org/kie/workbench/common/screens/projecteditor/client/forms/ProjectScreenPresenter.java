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

package org.kie.workbench.common.screens.projecteditor.client.forms;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.New;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContext;
import org.guvnor.common.services.project.events.ProjectChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.builder.model.DeployResult;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;


@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;

    private Caller<ProjectScreenService> projectScreenService;
    private Caller<BuildService> buildServiceCaller;

    private Project project;
    private Path pathToPomXML;
    private SaveOperationService saveOperationService;

    private Menus menus;
    private ProjectScreenModel model;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter( @New ProjectScreenView view,
                                   ProjectContext workbenchContext,
                                   Caller<ProjectScreenService> projectScreenService,
                                   Caller<BuildService> buildServiceCaller,
                                   SaveOperationService saveOperationService ) {
        this.view = view;
        view.setPresenter( this );

        this.projectScreenService = projectScreenService;

        this.buildServiceCaller = buildServiceCaller;
        this.saveOperationService = saveOperationService;

        showCurrentProjectInfoIfAny( workbenchContext.getActiveProject() );

        makeMenuBar();
    }

    public void selectedPathChanged( @Observes final ProjectChangeEvent event ) {
        showCurrentProjectInfoIfAny( event.getProject() );
    }

    private void showCurrentProjectInfoIfAny( final Project project ) {
        if ( project != null && !project.equals( this.project ) ) {
            this.project = project;
            this.pathToPomXML = project.getPomXMLPath();
            init();
        }
    }

    private void init() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        projectScreenService.call(
                new RemoteCallback<ProjectScreenModel>() {
                    @Override
                    public void callback( ProjectScreenModel model ) {
                        ProjectScreenPresenter.this.model = model;

                        view.setPOM( model.getPOM() );
                        view.setDependencies( model.getPOM().getDependencies() );
                        view.setPomMetadata( model.getPOMMetaData() );

                        view.setKModule( model.getKModule() );
                        view.setKModuleMetadata( model.getKModuleMetaData() );

                        view.setImports( model.getProjectImports() );
                        view.setImportsMetadata( model.getProjectImportsMetaData() );

                        view.hideBusyIndicator();
                    }
                }

                                 ).load( pathToPomXML );

        view.showGAVPanel();
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( CommonConstants.INSTANCE.File() )
                .menus()
                .menu( CommonConstants.INSTANCE.Save() )
                .respondsWith( getSaveCommand() )
                .endMenu()
                .endMenus()
                .endMenu()
                .newTopLevelMenu( ProjectEditorConstants.INSTANCE.Build() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        view.showBusyIndicator( ProjectEditorConstants.INSTANCE.Building() );
                        buildServiceCaller.call( getBuildSuccessCallback(),
                                                 new HasBusyIndicatorDefaultErrorCallback( view ) ).buildAndDeploy( project );
                    }
                } )
                .endMenu().build();

    }

    private Command getSaveCommand() {
        return new Command() {
            @Override
            public void execute() {
                saveOperationService.save( pathToPomXML,
                                           new CommandWithCommitMessage() {
                                               @Override
                                               public void execute( final String comment ) {
                                                   view.showBusyIndicator( CommonConstants.INSTANCE.Saving() );

                                                   projectScreenService.call( new RemoteCallback<Void>() {
                                                       @Override
                                                       public void callback( Void v ) {
                                                           view.hideBusyIndicator();
                                                       }
                                                   } ).save( pathToPomXML, model, comment );

                                               }
                                           } );
            }
        };
    }

    private RemoteCallback getBuildSuccessCallback() {
        return new RemoteCallback<DeployResult>() {
            @Override
            public void callback( final DeployResult r ) {
                view.hideBusyIndicator();
            }
        };
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorConstants.INSTANCE.ProjectScreen();
    }

    @WorkbenchPartView
    public IsWidget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

    @Override
    public void onGAVPanelSelected() {
        view.showGAVPanel();
    }

    @Override
    public void onGAVMetadataPanelSelected() {
        view.showGAVMetadataPanel();
    }

    @Override
    public void onKBasePanelSelected() {
        view.showKBasePanel();
    }

    @Override
    public void onKBaseMetadataPanelSelected() {
        view.showKBaseMetadataPanel();
    }

    @Override
    public void onImportsPanelSelected() {
        view.showImportsPanel();
    }

    @Override
    public void onImportsMetadataPanelSelected() {
        view.showImportsMetadataPanel();
    }

    @Override
    public void onDependenciesSelected() {
        view.showDependenciesPanel();
    }
}
