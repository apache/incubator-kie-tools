/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.wbtest.client.perspective;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiTabWorkbenchPanelPresenter;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.wbtest.client.resize.ResizeTestScreenActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

/**
 * A perspective with a root panel of type MultiTabWorkbenchPanelPresenter.
 */
@Dependent
@Named( "org.uberfire.wbtest.client.perspective.TabbedPerspectiveActivity" )
public class TabbedPerspectiveActivity extends AbstractTestPerspectiveActivity {

    /**
     * The ID to pass to UberTabPanelWrapper to get the tab panel wrapper for this perspective.
     */
    public static final String TABBED_PANEL_ID = "tabbedPerspectiveDefault";

    @Inject
    public TabbedPerspectiveActivity( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( MultiTabWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "TabbedPerspectiveActivity" );

        DefaultPlaceRequest destintationPlace = new DefaultPlaceRequest( ResizeTestScreenActivity.class.getName() );
        destintationPlace.addParameter( "debugId", TABBED_PANEL_ID );
        pdef.getRoot().addPart( new PartDefinitionImpl( destintationPlace ) );
        return pdef;
    }
}
