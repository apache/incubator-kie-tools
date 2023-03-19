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
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchServicesProxy;
import org.uberfire.client.workbench.events.PerspectiveChange;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.plugin.PluginUtil.ensureIterable;

@ApplicationScoped
public class PerspectiveManagerImpl implements PerspectiveManager {

    @Inject
    private PanelManager panelManager;

    @Inject
    private WorkbenchServicesProxy wbServices;

    @Inject
    private Event<PerspectiveChange> perspectiveChangeEvent;

    @Inject
    private ActivityBeansCache activityBeansCache;

    @Inject
    private SyncBeanManager iocManager;

    private PerspectiveActivity currentPerspective;

    private PerspectiveDefinition livePerspectiveDef;

    private PlaceRequest currentPerspectivePlaceRequest;

    @Override
    public void switchToPerspective(final PlaceRequest placeRequest,
                                    final PerspectiveActivity activity,
                                    final ParameterizedCommand<PerspectiveDefinition> doWhenFinished) {

        // switching perspectives is a chain of async operations. they're declared here
        // in reverse order (last to first):

        NotifyOthersOfPerspectiveChangeCommand fourthOperation = new NotifyOthersOfPerspectiveChangeCommand(placeRequest,
                                                                                                            doWhenFinished);

        BuildPerspectiveFromDefinitionCommand thirdOperation = new BuildPerspectiveFromDefinitionCommand(activity,
                                                                                                         fourthOperation);

        FetchPerspectiveCommand secondOperation = new FetchPerspectiveCommand(placeRequest,
                                                                              activity,
                                                                              thirdOperation);

        secondOperation.execute();
    }

    @Override
    public PerspectiveActivity getCurrentPerspective() {
        return currentPerspective;
    }

    @Override
    public PerspectiveDefinition getLivePerspectiveDefinition() {
        return livePerspectiveDef;
    }

    @Override
    public void savePerspectiveState(Command doWhenFinished) {
        if (currentPerspective != null && !currentPerspective.isTransient()) {
            wbServices.save(currentPerspective.getIdentifier(),
                            livePerspectiveDef,
                            doWhenFinished);
        } else {
            doWhenFinished.execute();
        }
    }

    @Override
    public void loadPerspectiveStates(final ParameterizedCommand<Set<PerspectiveDefinition>> doWhenFinished) {
        wbServices.loadPerspectives(doWhenFinished);
    }

    @Override
    public void removePerspectiveState(final String perspectiveId,
                                       final Command doWhenFinished) {
        wbServices.removePerspectiveState(perspectiveId,
                                          doWhenFinished);
    }

    @Override
    public void removePerspectiveStates(final Command doWhenFinished) {
        wbServices.removePerspectiveStates(doWhenFinished);
    }

    @Override
    public String getDefaultPerspectiveIdentifier() {
        AbstractWorkbenchPerspectiveActivity defaultPerspective = null;
        final Iterator<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectivesIterator = getPerspectivesIterator();

        while (perspectivesIterator.hasNext()) {
            final SyncBeanDef<AbstractWorkbenchPerspectiveActivity> perspective = perspectivesIterator.next();
            final AbstractWorkbenchPerspectiveActivity instance = perspective.getInstance();
            if (instance.isDefault()) {
                defaultPerspective = instance;
                break;
            } else {
                iocManager.destroyBean(instance);
            }
        }

        if (defaultPerspective != null) {
            return defaultPerspective.getIdentifier();
        }

        return null;
    }

    @Override
    public PlaceRequest getCurrentPerspectivePlaceRequest() {
        return currentPerspectivePlaceRequest;
    }

    Iterator<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> getPerspectivesIterator() {
        final Collection<SyncBeanDef<AbstractWorkbenchPerspectiveActivity>> perspectives = iocManager.lookupBeans(AbstractWorkbenchPerspectiveActivity.class);
        return perspectives.iterator();
    }

    /**
     * Fetches the given perspective's definition either from the server (if non-transient) or from the activity itself
     * (if transient or if the fetch call fails).
     */
    class FetchPerspectiveCommand implements Command {

        private final PlaceRequest placeRequest;
        private final PerspectiveActivity perspective;
        private final ParameterizedCommand<PerspectiveDefinition> doAfterFetch;

        public FetchPerspectiveCommand(PlaceRequest placeRequest,
                                       PerspectiveActivity perspective,
                                       ParameterizedCommand<PerspectiveDefinition> doAfterFetch) {
            this.placeRequest = checkNotNull("placeRequest",
                                             placeRequest);
            this.perspective = checkNotNull("perspective",
                                            perspective);
            this.doAfterFetch = checkNotNull("doAfterFetch",
                                             doAfterFetch);
        }

        @Override
        public void execute() {
            currentPerspectivePlaceRequest = placeRequest;
            currentPerspective = perspective;
            if (perspective.isTransient()) {
                //Transient Perspectives are not saved and hence cannot be loaded
                doAfterFetch.execute(perspective.getDefaultPerspectiveLayout());
            } else {

                wbServices.loadPerspective(perspective.getIdentifier(),
                                           new ParameterizedCommand<PerspectiveDefinition>() {
                                               @Override
                                               public void execute(final PerspectiveDefinition response) {

                                                   if (isAValidDefinition(response)) {
                                                       doAfterFetch.execute(response);
                                                   } else {
                                                       doAfterFetch.execute(perspective.getDefaultPerspectiveLayout());
                                                   }
                                               }
                                           });
            }
        }

        boolean isAValidDefinition(PerspectiveDefinition response) {
            return response != null && allThePartsAreValid(response.getRoot());
        }

        private boolean allThePartsAreValid(PanelDefinition panel) {
            if (!checkIfAllPlacesAreValidActivities(panel)) {
                return false;
            } else {
                for (PanelDefinition child : ensureIterable(panel.getChildren())) {
                    if (!allThePartsAreValid(child)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean checkIfAllPlacesAreValidActivities(PanelDefinition child) {
            for (PartDefinition partDefinition : ensureIterable(child.getParts())) {
                PlaceRequest place = partDefinition.getPlace();
                if (!activityBeansCache.hasActivity(place.getIdentifier())) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Builds up the panels of a perspective based on the structure described in a given {@link PerspectiveDefinition}.
     */
    class BuildPerspectiveFromDefinitionCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final ParameterizedCommand<PerspectiveDefinition> doWhenFinished;
        private final PerspectiveActivity activity;

        public BuildPerspectiveFromDefinitionCommand(PerspectiveActivity activity,
                                                     ParameterizedCommand<PerspectiveDefinition> doWhenFinished) {
            this.activity = checkNotNull("activity",
                                         activity);
            this.doWhenFinished = checkNotNull("doWhenFinished",
                                               doWhenFinished);
        }

        @Override
        public void execute(PerspectiveDefinition perspectiveDef) {
            if (livePerspectiveDef != null) {
                tearDownChildPanelsRecursively(livePerspectiveDef.getRoot());
            }
            livePerspectiveDef = perspectiveDef;
            panelManager.setRoot(activity,
                                 perspectiveDef.getRoot());
            setupPanelRecursively(perspectiveDef.getRoot());
            doWhenFinished.execute(perspectiveDef);
        }

        private void tearDownChildPanelsRecursively(final PanelDefinition panel) {
            for (PanelDefinition child : ensureIterable(panel.getChildren())) {
                tearDownChildPanelsRecursively(child);
                panelManager.removeWorkbenchPanel(child);
            }
        }

        private void setupPanelRecursively(final PanelDefinition panel) {
            for (PanelDefinition child : ensureIterable(panel.getChildren())) {
                final PanelDefinition target = panelManager.addWorkbenchPanel(panel,
                                                                              child,
                                                                              child.getPosition());
                setupPanelRecursively(target);
            }
        }
    }

    class NotifyOthersOfPerspectiveChangeCommand implements ParameterizedCommand<PerspectiveDefinition> {

        private final PlaceRequest placeRequest;
        private final ParameterizedCommand<PerspectiveDefinition> doWhenFinished;

        public NotifyOthersOfPerspectiveChangeCommand(final PlaceRequest placeRequest,
                                                      final ParameterizedCommand<PerspectiveDefinition> doWhenFinished) {
            this.placeRequest = checkNotNull("placeRequest",
                                             placeRequest);
            this.doWhenFinished = checkNotNull("doWhenFinished",
                                               doWhenFinished);
        }

        @Override
        public void execute(PerspectiveDefinition perspectiveDef) {
            currentPerspective.getMenus(menus -> {
                perspectiveChangeEvent.fire(new PerspectiveChange(placeRequest,
                                                                  perspectiveDef,
                                                                  menus,
                                                                  currentPerspective.getIdentifier()));
                doWhenFinished.execute(perspectiveDef);
            });
        }
    }
}
