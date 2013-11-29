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

package org.uberfire.client.mvp;

import java.util.Collection;

import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

public interface PlaceManager {

    void goTo( final String identifier );

    void goTo( final String identifier,
               final Command callback );

    void goTo( final PlaceRequest place );

    void goTo( final PlaceRequest place,
               final Command callback );

    void goTo( final Path path );

    void goTo( final Path path,
               final PlaceRequest place );

    void goTo( final Path path,
               final Command callback );

    void goTo( final PartDefinition part,
               final PanelDefinition panel );

    void goTo( final String identifier,
               final PanelDefinition panel );

    void goTo( final String identifier,
               final Command callback,
               final PanelDefinition panel );

    void goTo( final PlaceRequest place,
               final PanelDefinition panel );

    void goTo( final PlaceRequest place,
               final Command callback,
               final PanelDefinition panel );

    void goTo( final Path path,
               final PanelDefinition panel );

    void goTo( final Path path,
               final PlaceRequest place,
               final PanelDefinition panel );

    void goTo( final Path path,
               final Command callback,
               final PanelDefinition panel );

    Activity getActivity( final PlaceRequest place );

    PlaceStatus getStatus( final String id );

    PlaceStatus getStatus( final PlaceRequest place );

    void closePlace( final String id );

    void closePlace( final PlaceRequest place );

    void forceClosePlace( final String id );

    void forceClosePlace( final PlaceRequest place );

    void closeAllPlaces();

    void registerOnOpenCallback( final PlaceRequest place,
                                 final Command command );

    void unregisterOnOpenCallback( final PlaceRequest place );

    void executeOnOpenCallback( final PlaceRequest place );

    Collection<SplashScreenActivity> getActiveSplashScreens();
}
