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
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.kie.workbench.common.screens.projecteditor.client.resources.i18n.ProjectEditorConstants;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.services.project.service.ProjectService;
import org.kie.workbench.common.services.shared.builder.BuildService;
import org.kie.workbench.common.services.shared.context.KieWorkbenchContext;
import org.kie.workbench.common.services.shared.context.Project;
import org.kie.workbench.common.services.shared.context.ProjectChangeEvent;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.kie.workbench.common.widgets.client.popups.file.CommandWithCommitMessage;
import org.kie.workbench.common.widgets.client.popups.file.SaveOperationService;
import org.kie.workbench.common.widgets.client.resources.i18n.CommonConstants;
import org.kie.workbench.common.widgets.configresource.client.widget.unbound.ImportsWidgetPresenter;
import org.kie.workbench.common.widgets.metadata.client.widget.MetadataWidget;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.context.WorkbenchContext;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@WorkbenchScreen(identifier = "projectScreen")
public class ProjectScreenPresenter
        implements ProjectScreenView.Presenter {

    private ProjectScreenView view;

    private Caller<ProjectScreenService> projectScreenService;
    private Caller<ProjectService> projectService;
    private Caller<BuildService> buildServiceCaller;

    private Project project;
    private Path pathToPomXML;
    private SaveOperationService saveOperationService;

    private Menus menus;
    private ProjectScreenModel model;

    public ProjectScreenPresenter() {
    }

    @Inject
    public ProjectScreenPresenter(@New ProjectScreenView view,
                                  WorkbenchContext workbenchContext,
                                  Caller<ProjectScreenService> projectScreenService,
                                  Caller<ProjectService> projectService,
                                  Caller<BuildService> buildServiceCaller,
                                  SaveOperationService saveOperationService) {
        this.view = view;
        view.setPresenter(this);

        this.projectScreenService = projectScreenService;
        this.projectService = projectService;

        this.buildServiceCaller = buildServiceCaller;
        this.saveOperationService = saveOperationService;

        showCurrentProjectInfoIfAny(workbenchContext.getActivePath());

        makeMenuBar();
    }

    public void selectedPathChanged( @Observes final ProjectChangeEvent event ) {
        showCurrentProjectInfoIfAny( event.getProject() );
    }

    private void showCurrentProjectInfoIfAny(Path path) {
        projectService.call(new RemoteCallback<Path>() {
            @Override
            public void callback(Path pathToPomXML) {

                // TODO: Check save if there are changes -Rikkola-
                if (pathToPomXML != null && (ProjectScreenPresenter.this.pathToPomXML == null || !ProjectScreenPresenter.this.pathToPomXML.equals(pathToPomXML))) {

                    ProjectScreenPresenter.this.pathToPomXML = pathToPomXML;
                    init();
//                    multiPage.selectPage(0);
                }
            }
        }).resolvePathToPom(path);
    }

    private void init() {
        view.showBusyIndicator( CommonConstants.INSTANCE.Loading() );

        projectScreenService.call(
                new RemoteCallback<ProjectScreenModel>() {
                    @Override
                    public void callback( ProjectScreenModel model ) {
                        ProjectScreenPresenter.this.model = model;

                        view.setPOM(model.getPOM());
                        view.setPomMetadata(model.getPOMMetaData());

                        view.setKModule(model.getKModule());
                        view.setKModuleMetadata(model.getKModuleMetaData());

                        view.setImports(model.getProjectImports());
                        view.setImportsMetadata(model.getProjectImportsMetaData());
                    }
                }

                                 ).load( pathToPomXML );

        view.hideBusyIndicator();
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
        return new RemoteCallback<Void>() {
            @Override
            public void callback( final Void v ) {
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
}
