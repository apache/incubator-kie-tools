/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.client.views.pfly.mock;

import java.util.Collection;
import java.util.List;
import javax.enterprise.inject.Alternative;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.Activity;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.PlaceStatus;
import org.uberfire.client.mvp.SplashScreenActivity;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Alternative
public class MockPlaceManager implements PlaceManager {

    @Override
    public void goTo(String identifier) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(Path path) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(Path path,
                     PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PartDefinition part,
                     PanelDefinition panel) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(String identifier,
                     PanelDefinition panel) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PlaceRequest place,
                     PanelDefinition panel) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(Path path,
                     PanelDefinition panel) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(Path path,
                     PlaceRequest place,
                     PanelDefinition panel) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PlaceRequest place,
                     HasWidgets addTo) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(String id,
                     HTMLElement addTo) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PlaceRequest place,
                     HTMLElement addTo) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void goTo(PlaceRequest place,
                     elemental2.dom.HTMLElement addTo) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Activity getActivity(PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public PlaceStatus getStatus(String id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public PlaceStatus getStatus(PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void closePlace(String id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void closePlace(PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void closePlace(PlaceRequest placeToClose,
                           Command doAfterClose) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void tryClosePlace(PlaceRequest placeToClose,
                              Command onAfterClose) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void forceClosePlace(String id) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void forceClosePlace(PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void closeAllPlaces() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void forceCloseAllPlaces() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean closeAllPlacesOrNothing() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean canClosePlace(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public boolean canCloseAllPlaces() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<PlaceRequest> getUncloseablePlaces() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void registerOnOpenCallback(final PlaceRequest place,
                                       final Command callback) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void unregisterOnOpenCallbacks(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void registerOnCloseCallback(final PlaceRequest place,
                                        final Command callback) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void unregisterOnCloseCallbacks(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void registerPerspectiveCloseChain(final String perspectiveIdentifier,
                                              final BiParameterizedCommand<Command, PlaceRequest> closeChain) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public void executeOnOpenCallbacks(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Collection<SplashScreenActivity> getActiveSplashScreens() {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public Collection<PathPlaceRequest> getActivitiesForResourceType(final ResourceTypeDefinition type) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<Command> getOnOpenCallbacks(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }

    @Override
    public List<Command> getOnCloseCallbacks(final PlaceRequest place) {
        throw new UnsupportedOperationException("Not implemented.");
    }
}
