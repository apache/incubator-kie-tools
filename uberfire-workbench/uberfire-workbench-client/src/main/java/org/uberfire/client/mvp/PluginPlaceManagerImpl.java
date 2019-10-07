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
package org.uberfire.client.mvp;

import java.util.Collection;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasWidgets;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.Shared;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBar;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.type.ResourceTypeDefinition;

/**
 * This {@link PlaceManager} implementation is active for plugins only, to
 * satisfy compile-time dependencies (of other components on the plugin's
 * classpath that require a place manager). It is not used at runtime as plugins
 * use the {@link SharedSingleton} {@link PlaceManager} provided by the main
 * application. Plugins get access to a fully functional {@link PlaceManager}
 * using @{@link Inject} @{@link Shared} {@link ShareablePlaceManager}.
 */
@ApplicationScoped
@EnabledByProperty(value = "uberfire.plugin.mode.active")
public class PluginPlaceManagerImpl implements PlaceManager {

    // Avoid pruning by aggressive reachability analysis in Errai
    @SuppressWarnings("unused")
    @Inject
    private WorkbenchLayout workbenchLayout;

    // Avoid pruning by aggressive reachability analysis in Errai
    @SuppressWarnings("unused")
    @Inject
    private WorkbenchMenuBar menubar;

    // Avoid pruning by aggressive reachability analysis in Errai
    @SuppressWarnings("unused")
    @Inject
    private ObservablePath path;

    // Avoid pruning by aggressive reachability analysis in Errai
    @SuppressWarnings("unused")
    @Inject
    private LockManager lockManager;

    private void fail() {
        throw new RuntimeException("Invalid use of standard PlaceManager in plugin. " +
                                           "Use @Inject @" + Shared.class.getName() + " " +
                                           PlaceManager.class.getName() + " instead!");
    }

    @Override
    public void goTo(final String identifier,
                     final PanelDefinition panel) {

        fail();
    }

    @Override
    public void goTo(final String identifier) {

        fail();
    }

    @Override
    public void goTo(PlaceRequest place) {

        fail();
    }

    @Override
    public void goTo(final Path path,
                     final PanelDefinition panel) {

        fail();
    }

    @Override
    public void goTo(final Path path) {
        fail();
    }

    @Override
    public void goTo(final Path path,
                     final PlaceRequest placeRequest,
                     final PanelDefinition panel) {
        fail();
    }

    @Override
    public void goTo(final Path path,
                     final PlaceRequest placeRequest) {
        fail();
    }

    @Override
    public void goTo(final PlaceRequest place,
                     final PanelDefinition panel) {
        fail();
    }

    @Override
    public void goTo(PlaceRequest place,
                     HasWidgets addTo) {
        fail();
    }

    @Override
    public void goTo(String id,
                     HTMLElement addTo) {
        fail();
    }

    @Override
    public void goTo(PlaceRequest place,
                     HTMLElement addTo) {
        fail();
    }

    @Override
    public void goTo(PlaceRequest place,
                     elemental2.dom.HTMLElement addTo) {
        fail();
    }

    @Override
    public void goTo(final PartDefinition part,
                     final PanelDefinition panel) {
        fail();
    }

    @Override
    public Activity getActivity(final PlaceRequest place) {
        fail();

        return null;
    }

    @Override
    public PlaceStatus getStatus(String id) {
        fail();

        return null;
    }

    @Override
    public PlaceStatus getStatus(final PlaceRequest place) {
        fail();

        return null;
    }

    @Override
    public void closePlace(final String id) {
        fail();
    }

    @Override
    public void closePlace(final PlaceRequest placeToClose) {
        fail();
    }

    @Override
    public void closePlace(final PlaceRequest placeToClose,
                           final Command doAfterClose) {
        fail();
    }

    @Override
    public void tryClosePlace(final PlaceRequest placeToClose,
                              final Command onAfterClose) {
        fail();
    }

    @Override
    public void forceClosePlace(final String id) {
        fail();
    }

    @Override
    public void forceClosePlace(final PlaceRequest placeToClose) {
        fail();
    }

    @Override
    public void closeAllPlaces() {
        fail();
    }

    @Override
    public void forceCloseAllPlaces() {
        fail();
    }

    @Override
    public boolean closeAllPlacesOrNothing() {
        fail();
        return false;
    }

    @Override
    public boolean canClosePlace(final PlaceRequest place) {
        fail();
        return false;
    }

    @Override
    public boolean canCloseAllPlaces() {
        fail();
        return false;
    }

    @Override
    public List<PlaceRequest> getUncloseablePlaces() {
        fail();
        return null;
    }

    @Override
    public void registerOnOpenCallback(final PlaceRequest place,
                                       final Command callback) {
        fail();
    }

    @Override
    public void unregisterOnOpenCallbacks(final PlaceRequest place) {
        fail();
    }

    @Override
    public void registerOnCloseCallback(final PlaceRequest place,
                                        final Command callback) {
        fail();
    }

    @Override
    public void unregisterOnCloseCallbacks(final PlaceRequest place) {
        fail();
    }

    @Override
    public void registerPerspectiveCloseChain(final String perspectiveIdentifier,
                                              final BiParameterizedCommand<Command, PlaceRequest> closeChain) {
        fail();
    }

    @Override
    public Collection<SplashScreenActivity> getActiveSplashScreens() {
        fail();
        return null;
    }

    @Override
    public List<Command> getOnOpenCallbacks(final PlaceRequest place) {
        fail();
        return null;
    }

    @Override
    public List<Command> getOnCloseCallbacks(final PlaceRequest place) {
        fail();
        return null;
    }

    @Override
    public Collection<PathPlaceRequest> getActivitiesForResourceType(ResourceTypeDefinition type) {
        fail();
        return null;
    }
}
