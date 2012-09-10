/*
 * Copyright 2012 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.uberfire.client.perspectives;

import static org.uberfire.shared.security.AppRoles.DIRECTOR;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.model.PanelDefinition;
import org.uberfire.client.workbench.model.PartDefinition;
import org.uberfire.client.workbench.model.PerspectiveDefinition;
import org.uberfire.security.annotations.Roles;
import org.uberfire.shared.mvp.PlaceRequest;
import org.uberfire.shared.security.ShowcaseRoles;

/**
 * Test Perspectives. Multiple Perspectives can be defined in one class
 */
@ApplicationScoped
public class TestPerspectives {

    @Perspective(identifier = "TestPerspective")
    public PerspectiveDefinition getPerspective1() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-1" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "Test" ) ) );

        return p;
    }

    @Perspective(identifier = "TestPerspective2")
    public PerspectiveDefinition getPerspective2() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-2" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "Test2" ) ) );

        return p;
    }


    @Perspective(identifier = "GadgetPerspective")
    public PerspectiveDefinition getGadgetPerspective() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show Gadget" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "Gadget" ) ) );

        return p;
    }
    
    @Perspective(identifier = "TestPerspective3")
    public PerspectiveDefinition getPerspective3() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-3" );

        final PanelDefinition south = new PanelDefinition();
        south.addPart( new PartDefinition( new PlaceRequest( "Test" ) ) );
        p.getRoot().setChild( Position.SOUTH,
                              south );

        return p;
    }

    @Perspective(identifier = "TestPerspective4")
    public PerspectiveDefinition getPerspective4() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-4" );

        final PanelDefinition south = new PanelDefinition();
        south.addPart( new PartDefinition( new PlaceRequest( "Test" ) ) );
        p.getRoot().setChild( Position.SOUTH,
                              south );

        final PanelDefinition east = new PanelDefinition();
        east.addPart( new PartDefinition( new PlaceRequest( "Test2" ) ) );
        south.setChild( Position.EAST,
                        east );

        return p;
    }

    @Perspective(identifier = "TestPerspective5")
    public PerspectiveDefinition getPerspective5() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-5" );

        final PanelDefinition south = new PanelDefinition();
        south.addPart( new PartDefinition( new PlaceRequest( "Test" ) ) );
        p.getRoot().setChild( Position.SOUTH,
                              south );

        final PanelDefinition west = new PanelDefinition();
        west.addPart( new PartDefinition( new PlaceRequest( "Test2" ) ) );
        p.getRoot().setChild( Position.WEST,
                              west );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "MyAdminArea" ) ) );

        return p;
    }

    @Perspective(identifier = "TestPerspective6")
    @ShowcaseRoles({DIRECTOR})
    //This Perspective should not be shown as the default user does not poses this role
    public PerspectiveDefinition getPerspective6() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-6" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "MyAdminArea" ) ) );

        return p;
    }

    @Perspective(identifier = "TestPerspective7")
    @Roles({"ADMIN"})
    //This Perspective should not be shown as the default user does not poses this role
    public PerspectiveDefinition getPerspective7() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-7" );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "MyAdminArea" ) ) );

        return p;
    }

    @Perspective(identifier = "TestSizedPerspective1")
    public PerspectiveDefinition getSizedPerspective1() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestSized-1" );

        final PanelDefinition west = new PanelDefinition();
        west.setHeight( 200 );
        west.setWidth( 200 );
        west.setMinHeight( 100 );
        west.setMinWidth( 100 );
        west.addPart( new PartDefinition( new PlaceRequest( "MyAdminArea" ) ) );
        p.getRoot().setChild( Position.WEST,
                              west );

        p.getRoot().addPart( new PartDefinition( new PlaceRequest( "Test2" ) ) );

        return p;
    }
}
