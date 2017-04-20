/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.workbench.client.docks;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datamodeller.client.DataModelerContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContext;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchContextChangeEvent;
import org.kie.workbench.common.screens.datamodeller.client.context.DataModelerWorkbenchFocusEvent;
import org.kie.workbench.common.screens.library.api.preferences.LibraryInternalPreferences;
import org.kie.workbench.common.workbench.client.authz.WorkbenchFeatures;
import org.kie.workbench.common.workbench.client.resources.i18n.DefaultWorkbenchConstants;
import org.kie.workbench.common.workbench.client.resources.images.WorkbenchImageResources;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDockPosition;
import org.uberfire.client.workbench.docks.UberfireDockReadyEvent;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.client.workbench.docks.UberfireDocksInteractionEvent;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

@Dependent
public class AuthoringWorkbenchDocks {

    protected DefaultWorkbenchConstants constants = DefaultWorkbenchConstants.INSTANCE;

    protected UberfireDocks uberfireDocks;

    protected DataModelerWorkbenchContext dataModelerWBContext;

    protected SessionInfo sessionInfo;

    protected LibraryInternalPreferences libraryInternalPreferences;

    protected AuthorizationManager authorizationManager;

    protected String authoringPerspectiveIdentifier;

    protected UberfireDock projectExplorerDock;

    protected boolean dataModelerIsHidden;

    protected DataModelerContext lastActiveContext;

    protected UberfireDock plannerDock = null;

    protected String currentPerspectiveIdentifier = null;

    protected boolean dataModelerDocksEnabled = true;

    protected boolean projectExplorerEnabled = true;

    @Inject
    public AuthoringWorkbenchDocks(final UberfireDocks uberfireDocks,
                                   final DataModelerWorkbenchContext dataModelerWBContext,
                                   final AuthorizationManager authorizationManager,
                                   final SessionInfo sessionInfo,
                                   final LibraryInternalPreferences libraryInternalPreferences) {
        this.uberfireDocks = uberfireDocks;
        this.dataModelerWBContext = dataModelerWBContext;
        this.authorizationManager = authorizationManager;
        this.sessionInfo = sessionInfo;
        this.libraryInternalPreferences = libraryInternalPreferences;
    }

    public void perspectiveChangeEvent(@Observes UberfireDockReadyEvent dockReadyEvent) {
        currentPerspectiveIdentifier = dockReadyEvent.getCurrentPerspective();
        if (authoringPerspectiveIdentifier != null && dockReadyEvent.getCurrentPerspective().equals(authoringPerspectiveIdentifier)) {
            updatePlannerDock(authoringPerspectiveIdentifier);
            if (projectExplorerEnabled) {
                expandProjectExplorer();
            }
        }
    }

    private void updatePlannerDock(String perspectiveIdentifier) {
        if (authorizationManager.authorize(WorkbenchFeatures.PLANNER_AVAILABLE,
                                           sessionInfo.getIdentity())) {
            if (plannerDock == null) {
                plannerDock = new UberfireDock(UberfireDockPosition.EAST,
                                               WorkbenchImageResources.INSTANCE.optaPlannerDisabledIcon(),
                                               WorkbenchImageResources.INSTANCE.optaPlannerEnabledIcon(),
                                               new DefaultPlaceRequest("PlannerDomainScreen"),
                                               perspectiveIdentifier)
                        .withSize(450).withLabel(constants.DocksOptaPlannerTitle());
            } else {
                //avoid duplications
                uberfireDocks.remove(plannerDock);
            }
            uberfireDocks.add(plannerDock);
        } else if (plannerDock != null) {
            uberfireDocks.remove(plannerDock);
        }
    }

    public void setup(String authoringPerspectiveIdentifier,
                      PlaceRequest projectExplorerPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
        projectExplorerDock = new UberfireDock(UberfireDockPosition.WEST,
                                               "ADJUST",
                                               projectExplorerPlaceRequest,
                                               authoringPerspectiveIdentifier).withSize(400).withLabel(constants.DocksProjectExplorerTitle());
        uberfireDocks.add(
                projectExplorerDock,
                new UberfireDock(UberfireDockPosition.EAST,
                                 "RANDOM",
                                 new DefaultPlaceRequest("DroolsDomainScreen"),
                                 authoringPerspectiveIdentifier).withSize(450).withLabel(constants.DocksDroolsJBPMTitle()),
                new UberfireDock(UberfireDockPosition.EAST,
                                 "BRIEFCASE",
                                 new DefaultPlaceRequest("JPADomainScreen"),
                                 authoringPerspectiveIdentifier).withSize(450).withLabel(constants.DocksPersistenceTitle()),
                new UberfireDock(UberfireDockPosition.EAST,
                                 "COG",
                                 new DefaultPlaceRequest("AdvancedDomainScreen"),
                                 authoringPerspectiveIdentifier).withSize(450).withLabel(constants.DocksAdvancedTitle()),
                new UberfireDock(UberfireDockPosition.EAST,
                                 "PENCIL_SQUARE_O",
                                 new DefaultPlaceRequest("ProjectDiagramPropertiesScreen"),
                                 authoringPerspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerPropertiesTitle()),
                new UberfireDock(UberfireDockPosition.EAST,
                                 "EYE",
                                 new DefaultPlaceRequest("ProjectDiagramExplorerScreen"),
                                 authoringPerspectiveIdentifier).withSize(450).withLabel(constants.DocksStunnerExplorerTitle())
        );
        updatePlannerDock(authoringPerspectiveIdentifier);
        uberfireDocks.disable(UberfireDockPosition.EAST,
                              authoringPerspectiveIdentifier);
        dataModelerDocksEnabled = false;
    }

    public void onContextChange(@Observes DataModelerWorkbenchContextChangeEvent contextEvent) {
        if (isAuthoringActive()) {
            handleDocks();
        }
    }

    public void onDataModelerWorkbenchFocusEvent(@Observes DataModelerWorkbenchFocusEvent event) {
        if (isAuthoringActive()) {
            if (!event.isFocused()) {
                this.dataModelerIsHidden = true;
                enableDocks(false);
            } else {
                this.dataModelerIsHidden = false;
                handleDocks();
            }
        }
    }

    public boolean isAuthoringActive() {
        return authoringPerspectiveIdentifier != null &&
                authoringPerspectiveIdentifier.equals(currentPerspectiveIdentifier);
    }

    private void enableDocks(boolean enabled) {
        if (enabled != dataModelerDocksEnabled) {
            dataModelerDocksEnabled = enabled;
            if (enabled) {
                uberfireDocks.enable(UberfireDockPosition.EAST,
                                     authoringPerspectiveIdentifier);
            } else {
                uberfireDocks.disable(UberfireDockPosition.EAST,
                                      authoringPerspectiveIdentifier);
            }
        }
    }

    private void handleDocks() {
        DataModelerContext context = dataModelerWBContext.getActiveContext();
        if (!dataModelerIsHidden && shouldDisplayWestDocks(context) && lastActiveContext != context) {
            enableDocks(true);
            lastActiveContext = context;
        } else if (dataModelerIsHidden || !shouldDisplayWestDocks(context)) {
            enableDocks(false);
            lastActiveContext = null;
        }
    }

    private boolean shouldDisplayWestDocks(DataModelerContext context) {
        return context != null && context.getEditionMode() == DataModelerContext.EditionMode.GRAPHICAL_MODE;
    }

    public void hide() {
        uberfireDocks.disable(UberfireDockPosition.WEST,
                              authoringPerspectiveIdentifier);
        enableDocks(false);
        projectExplorerEnabled = false;
    }

    public void show() {
        uberfireDocks.enable(UberfireDockPosition.WEST,
                             authoringPerspectiveIdentifier);
        handleDocks();
        projectExplorerEnabled = true;

        libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                            if (loadedLibraryInternalPreferences.isProjectExplorerExpanded()) {
                                                expandProjectExplorer();
                                            }
                                        },
                                        parameter -> {
                                        });
    }

    public void expandProjectExplorer() {
        if (projectExplorerDock != null) {
            uberfireDocks.expand(projectExplorerDock);
        }
    }

    public void projectExplorerExpandedEvent(@Observes final UberfireDocksInteractionEvent uberfireDocksInteractionEvent) {
        final UberfireDock targetDock = uberfireDocksInteractionEvent.getTargetDock();
        if (targetDock == null) {
            return;
        }
        if (targetDock.equals(projectExplorerDock)) {
            final UberfireDocksInteractionEvent.InteractionType interactionType = uberfireDocksInteractionEvent.getType();
            if (interactionType.equals(UberfireDocksInteractionEvent.InteractionType.SELECTED)) {
                setProjectExplorerExpandedPreference(true);
            } else if (interactionType.equals(UberfireDocksInteractionEvent.InteractionType.DESELECTED)) {
                setProjectExplorerExpandedPreference(false);
            }
        }
    }

    void setProjectExplorerExpandedPreference(final boolean expand) {
        libraryInternalPreferences.load(loadedLibraryInternalPreferences -> {
                                            if (expand != loadedLibraryInternalPreferences.isProjectExplorerExpanded()) {
                                                loadedLibraryInternalPreferences.setProjectExplorerExpanded(expand);
                                                loadedLibraryInternalPreferences.save();
                                            }
                                        },
                                        error -> {
                                        });
    }
}
