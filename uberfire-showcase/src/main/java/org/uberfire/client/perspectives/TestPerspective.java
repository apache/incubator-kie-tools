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
import javax.inject.Inject;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.perspectives.PerspectiveProvider;
import org.uberfire.client.workbench.widgets.panels.PanelManager;
import org.uberfire.shared.mvp.PlaceRequest;

/**
 * A Perspective to show all Test widgets
 */
@ApplicationScoped
public class TestPerspective
    implements
    PerspectiveProvider {

    @Inject
    PlaceManager                placeManager;

    private static final String NAME = "Tests";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void buildWorkbench(final PanelManager panelManager) {
        //TODO {manstis} We should ideally be able to construct a perspective by adding panels to the Workbench root panel
        //This approach, however, does not currently register Activities within the MVP framework and hence unpredictable
        //results can occur.
        placeManager.goTo( new PlaceRequest( "Test" ) );
    }

}
