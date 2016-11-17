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
package org.kie.workbench.common.screens.library.client.util;


import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class LibraryDocks {

    @Inject
    private UberfireDocks uberfireDocks;

    @Inject
    private Event<ProjectDetailEvent> projectDetailEvent;

    @Inject
    private TranslationService ts;


    private UberfireDock projectDock;

    private Project currentProject;

    @PostConstruct
    public void setup() {
        projectDock = new UberfireDock( UberfireDockPosition.EAST, "INFO_CIRCLE",
                                        new DefaultPlaceRequest( "ProjectsDetailScreen" ),
                                        "LibraryPerspective" )
                .withSize( 450 )
                .withLabel( ts.getTranslation(
                        LibraryConstants.ProjectsDetailsScreen_Title ) );
    }

    public UberfireDock getProjectDock() {
        return projectDock;
    }

    public void handle( Project selectedProject ) {

        if ( currentProject == null ) {
            uberfireDocks.enable( projectDock.getDockPosition(), projectDock.getAssociatedPerspective() );
            uberfireDocks.expand( projectDock );
        }
        this.currentProject = selectedProject;
        projectDetailEvent.fire( new ProjectDetailEvent( selectedProject ) );
    }

    public void start() {
        uberfireDocks.add( projectDock );
        uberfireDocks.disable( projectDock.getDockPosition(), projectDock.getAssociatedPerspective() );
    }

    public void hide() {
        uberfireDocks.disable( projectDock.getDockPosition(), projectDock.getAssociatedPerspective() );
    }

    public void refresh() {
        currentProject = null;
    }

    public void reloadProjectDetail( @Observes UberfireDocksInteractionEvent event ) {

        if ( shouldUpdate( event ) ) {
            projectDetailEvent.fire( new ProjectDetailEvent( currentProject ) );
        }
    }

    private boolean shouldUpdate( @Observes UberfireDocksInteractionEvent event ) {
        return currentProject != null && event.getTargetDock() == projectDock && event
                .getType() == UberfireDocksInteractionEvent.InteractionType.SELECTED;
    }
}
