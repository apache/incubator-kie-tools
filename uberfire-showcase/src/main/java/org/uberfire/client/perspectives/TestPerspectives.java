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

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Perspective;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.perspectives.PerspectiveDefinition;
import org.uberfire.client.workbench.perspectives.PerspectivePartDefinition;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * Test Perspectives. Multiple Perspectives can be defined in one class
 */
@ApplicationScoped
public class TestPerspectives {

    @Perspective(identifier = "TestPerspective")
    public PerspectiveDefinition getPerspective1() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-1" );
        p.addPart( new PerspectivePartDefinition( Position.ROOT,
                                                  new PlaceRequest( "Test" ) ) );
        return p;
    }

    @Perspective(identifier = "TestPerspective2")
    public PerspectiveDefinition getPerspective2() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-2" );
        p.addPart( new PerspectivePartDefinition( Position.ROOT,
                                                  new PlaceRequest( "Test2" ) ) );
        return p;
    }

    @Perspective(identifier = "TestPerspective3")
    public PerspectiveDefinition getPerspective3() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-3" );
        p.addPart( new PerspectivePartDefinition( Position.SOUTH,
                                                  new PlaceRequest( "Test" ) ) );
        return p;
    }

    @Perspective(identifier = "TestPerspective4")
    public PerspectiveDefinition getPerspective4() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-4" );
        final PerspectivePartDefinition south = new PerspectivePartDefinition( Position.SOUTH,
                                                                               new PlaceRequest( "Test" ) );
        south.addPart( new PerspectivePartDefinition( Position.EAST,
                                                      new PlaceRequest( "Test2" ) ) );
        p.addPart( south );
        return p;
    }

    @Perspective(identifier = "TestPerspective5")
    public PerspectiveDefinition getPerspective5() {
        final PerspectiveDefinition p = new PerspectiveDefinition();
        p.setName( "Show TestWidgets-5" );
        p.addPart( new PerspectivePartDefinition( Position.SOUTH,
                                                  new PlaceRequest( "Monitoring" ) ) );
        p.addPart( new PerspectivePartDefinition( Position.SELF,
                                                  new PlaceRequest( "MyAdminArea" ) ) );
        final PerspectivePartDefinition west = new PerspectivePartDefinition( Position.WEST,
                                                                              new PlaceRequest( "Test" ) );
        west.addPart( new PerspectivePartDefinition( Position.SOUTH,
                                                     new PlaceRequest( "Test2" ) ) );
        p.addPart( west );
        return p;
    }

}
