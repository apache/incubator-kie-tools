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

import javax.annotation.Generated;
import javax.enterprise.context.ApplicationScoped;

import org.uberfire.client.annotations.Identifier;
import org.uberfire.client.mvp.AbstractPerspectiveActivity;
import org.uberfire.client.workbench.Position;
import org.uberfire.client.workbench.perspectives.Perspective;
import org.uberfire.client.workbench.perspectives.PerspectivePart;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * A Perspective to show all Test widgets
 */
@ApplicationScoped
@Generated("org.uberfire.annotations.processors.PerspectiveProcessor")
@Identifier("TestPerspective")
////////////////////////////////////////
// *** THIS WILL BECOME GENERATED *** //
////////////////////////////////////////
public class TestPerspective extends AbstractPerspectiveActivity {

    @Override
    public String getIdentifier() {
        return "TestPerspective";
    }

    @Override
    public Perspective getPerspective() {
        final Perspective p = new Perspective();
        p.setName( "Show TestWidgets" );
        p.addPart( new PerspectivePart( Position.ROOT,
                                        new PlaceRequest( "Test" ) ) );
        return p;
    }

}
