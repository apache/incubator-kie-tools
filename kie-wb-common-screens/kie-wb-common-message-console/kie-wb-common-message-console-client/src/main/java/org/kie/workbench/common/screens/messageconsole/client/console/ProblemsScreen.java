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

package org.kie.workbench.common.screens.projecteditor.client.messages;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.Widget;
import org.guvnor.common.services.project.builder.model.BuildResults;
import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.common.services.project.context.ProjectContextChangeEvent;
import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.widgets.client.callbacks.HasBusyIndicatorDefaultErrorCallback;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchMenu;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.mvp.Command;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.menu.MenuFactory;
import org.uberfire.workbench.model.menu.Menus;

@ApplicationScoped
@WorkbenchScreen(identifier = "org.kie.guvnor.Problems")
public class ProblemsScreen
        implements ProblemsScreenView.Presenter {

    private final PlaceManager placeManager;
    private final ProblemsScreenView view;
    private final ProblemsService problemsService;

    private final Caller<BuildService> buildService;
    private final Event<BuildResults> buildResultsEvent;

    private Project project;

    private Menus menus;

    @Inject
    public ProblemsScreen( final ProblemsScreenView view,
                           final PlaceManager placeManager,
                           final ProblemsService problemsService,
                           final Caller<BuildService> buildService,
                           final Event<BuildResults> buildResultsEvent ) {
        this.view = view;
        this.placeManager = placeManager;
        this.problemsService = problemsService;
        this.buildService = buildService;
        this.buildResultsEvent = buildResultsEvent;

        makeMenuBar();

        view.setPresenter( this );
    }

    private void makeMenuBar() {
        menus = MenuFactory
                .newTopLevelMenu( ProjectEditorResources.CONSTANTS.RefreshProblemsPanel() )
                .respondsWith( new Command() {
                    @Override
                    public void execute() {
                        view.showBusyIndicator( ProjectEditorResources.CONSTANTS.Refreshing() );
                        buildService.call( new RemoteCallback<BuildResults>() {
                            @Override
                            public void callback( final BuildResults results ) {
                                buildResultsEvent.fire( results );
                                view.hideBusyIndicator();
                            }
                        }, new HasBusyIndicatorDefaultErrorCallback( view ) ).build( project );
                    }
                } )
                .endMenu()
                .build();
    }

    public void selectedProjectChanged( @Observes final ProjectContextChangeEvent event ) {
        this.project = event.getProject();
        this.menus.getItems().get( 0 ).setEnabled( project != null );
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return Position.SOUTH;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ProjectEditorResources.CONSTANTS.Problems();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @WorkbenchMenu
    public Menus getMenus() {
        return menus;
    }

}
