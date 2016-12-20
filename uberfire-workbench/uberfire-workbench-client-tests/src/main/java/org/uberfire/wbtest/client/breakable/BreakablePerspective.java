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

package org.uberfire.wbtest.client.breakable;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.panels.impl.MultiListWorkbenchPanelPresenter;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.wbtest.client.api.AbstractTestPerspectiveActivity;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PerspectiveDefinitionImpl;

@Dependent
@Named( "org.uberfire.wbtest.client.breakable.BreakablePerspective" )
public class BreakablePerspective extends AbstractTestPerspectiveActivity {

    private LifecyclePhase brokenLifecycle;

    @Inject
    public BreakablePerspective( PlaceManager placeManager ) {
        super( placeManager );
    }

    @Override
    public PerspectiveDefinition getDefaultPerspectiveLayout() {
        PerspectiveDefinition pdef = new PerspectiveDefinitionImpl( MultiListWorkbenchPanelPresenter.class.getName() );
        pdef.setName( "BreakablePerspective" );
        pdef.getRoot().addPart( BreakableMenuScreen.class.getName() );
        return pdef;
    }

    @Override
    public void onStartup( PlaceRequest place ) {
        super.onStartup( place );
        String brokenParam = place.getParameter( "broken", null );
        if ( brokenParam != null && brokenParam.length() > 0 ) {
            brokenLifecycle = LifecyclePhase.valueOf( brokenParam );
        }

        if ( brokenLifecycle == LifecyclePhase.STARTUP ) {
            throw new RuntimeException( "This perspective has a broken startup callback" );
        }
    }

    @Override
    public void onOpen() {
        super.onOpen();
        if ( brokenLifecycle == LifecyclePhase.OPEN ) {
            throw new RuntimeException( "This perspective has a broken open callback" );
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if ( brokenLifecycle == LifecyclePhase.CLOSE ) {
            throw new RuntimeException( "This perspective has a broken close callback" );
        }
    }

    @Override
    public void onShutdown() {
        super.onShutdown();
        if ( brokenLifecycle == LifecyclePhase.SHUTDOWN ) {
            throw new RuntimeException( "This perspective has a broken shutdown callback" );
        }
    }
}
