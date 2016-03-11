/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.ext.wires.client;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.annotations.WorkbenchPerspective;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.ext.wires.client.resources.UberfireWiresResources;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A Perspective to show UberFire Extension widgets
 */
@ApplicationScoped
@WorkbenchPerspective( identifier = WidgetsPerspective.UFWIDGETS )
public class WidgetsPerspective {

    public static final String UFWIDGETS = "UFWidgets";

    @Inject
    private UberfireDocks uberfireDocks;

    @Perspective
    public PerspectiveDefinition getPerspective() {
        final PerspectiveDefinition perspective = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        perspective.getRoot().addPart( new PartDefinitionImpl( new DefaultPlaceRequest( "PagedTableScreen" ) ) );
        return perspective;
    }

    @PostConstruct
    public void setup() {
        final DefaultPlaceRequest placeRequest = new DefaultPlaceRequest( "SimpleDockScreen" );
        uberfireDocks.add(
                new UberfireDock( UberfireDockPosition.WEST, "ADJUST", placeRequest, UFWIDGETS ).withSize( 400 ).withLabel( "Project Explorer" ),
                new UberfireDock( UberfireDockPosition.EAST, "COG", placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "Advanced" ),
                new UberfireDock( UberfireDockPosition.EAST, "RANDOM", placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "Drools" ),
                new UberfireDock( UberfireDockPosition.EAST, "BRIEFCASE", placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "JPA" ),
                new UberfireDock( UberfireDockPosition.EAST, UberfireWiresResources.INSTANCE.images().optaPlannerIconBlue(), UberfireWiresResources.INSTANCE.images().optaPlannerIconWhite(), placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "OptaPlanner" ),
                new UberfireDock( UberfireDockPosition.SOUTH, "BARS", placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "Setup" ),
                new UberfireDock( UberfireDockPosition.SOUTH, "COMMENT", placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "Comment" ),
                new UberfireDock( UberfireDockPosition.SOUTH, UberfireWiresResources.INSTANCE.images().optaPlannerIconBlue(), UberfireWiresResources.INSTANCE.images().optaPlannerIconWhite(), placeRequest, UFWIDGETS ).withSize( 450 ).withLabel( "OptaPlanner" )
        );
    }
}
