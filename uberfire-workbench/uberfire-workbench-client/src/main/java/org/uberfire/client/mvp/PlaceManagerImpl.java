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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.panels.impl.StaticWorkbenchPanelPresenter;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static org.uberfire.plugin.PluginUtil.ensureIterable;
import static org.uberfire.plugin.PluginUtil.toInteger;

@SharedSingleton
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class PlaceManagerImpl implements PlaceManager {

    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<>();
    private final Map<PlaceRequest, PartDefinition> visibleWorkbenchParts = new HashMap<>();
    private final Map<PlaceRequest, CustomPanelDefinition> customPanels = new HashMap<>();

    private EventBus tempBus = null;
    @Inject
    private ActivityManager activityManager;
    @Inject
    private PanelManager panelManager;
    @Inject
    private PerspectiveActivity defaultPerspective;
    @Inject
    private WorkbenchLayout workbenchLayout;

    @Override
    public void bootstrapPerspective() {
        final ParameterizedCommand<PerspectiveDefinition> command = perspectiveDef -> {
            panelManager.setRoot(defaultPerspective,
                                 perspectiveDef.getRoot());
            openPartsRecursively(perspectiveDef.getRoot());
            workbenchLayout.onResize();
        };

        command.execute(defaultPerspective.getDefaultPerspectiveLayout());
    }

    private void goTo(final PlaceRequest place,
                      final PanelDefinition panel) {
        if (place == null || place.equals(PlaceRequest.NOWHERE)) {
            return;
        }

        final ResolvedRequest resolved = resolveActivity(place);

        if (resolved.getActivity() != null) {
            final Activity activity = resolved.getActivity();
            if (activity.isType(ActivityResourceType.SCREEN.name())) {
                launchWorkbenchActivity(resolved.getPlaceRequest(),
                                        activity,
                                        panel);
            }
        } else {
            goTo(resolved.getPlaceRequest(),
                 panel);
        }
    }

    @Override
    public void goTo(PlaceRequest place,
                     HasWidgets addTo) {
        final Predicate<CustomPanelDefinition> filter = p -> p.getHasWidgetsContainer().isPresent()
                && p.getHasWidgetsContainer().get().equals(addTo);

        new HashSet<>(customPanels.values()).stream()
                .filter(filter)
                .flatMap(p -> p.getParts().stream())
                .forEach(part -> closePlace(part.getPlace()));

        goToTargetPanel(place,
                        panelManager.addCustomPanel(addTo,
                                                    StaticWorkbenchPanelPresenter.class.getName()));
    }

    private void goToTargetPanel(final PlaceRequest place,
                                 final CustomPanelDefinition adoptedPanel) {
        if (existingWorkbenchActivities.containsKey(place)) {
            // if already open, behaviour is to select the place where it already lives
            goTo(place,
                 (PanelDefinition)null);
        } else {
            customPanels.put(place,
                             adoptedPanel);
            goTo(place,
                 adoptedPanel);
        }
    }

    /**
     * Resolves the given place request into an Activity instance, if one can be found. If not, this method substitutes
     * special "not found" or "too many" place requests when the resolution doesn't work.
     * <p/>
     * @param place A non-null place request that could have originated from within application code, from within the
     * framework, or by parsing a hash fragment from a browser history event.
     * @return a non-null ResolvedRequest, where:
     * <ul>
     * <li>the Activity value is either the unambiguous resolved Activity instance, or null if the activity was
     * not resolvable; in this case, the Activity has been added to the {@link #existingWorkbenchActivities} map.
     * <li>if there is an Activity value, the PlaceRequest represents that Activity; otherwise
     * it is a substitute PlaceRequest that should be navigated to recursively (ultimately by another call to
     * this method). The PlaceRequest is never null.
     * </ul>
     * TODO (UF-94) : make this simpler. with enough tests in place, we should experiment with doing the recursive
     * lookup automatically.
     */
    private ResolvedRequest resolveActivity(final PlaceRequest place) {

        final PlaceRequest resolvedPlaceRequest = resolvePlaceRequest(place);

        final ResolvedRequest existingDestination = resolveExistingParts(resolvedPlaceRequest);

        if (existingDestination != null) {
            return existingDestination;
        }

        final Set<Activity> activities = activityManager.getActivities(resolvedPlaceRequest);

        if (activities == null || activities.isEmpty()) {
            final PlaceRequest notFoundPopup = new DefaultPlaceRequest("workbench.activity.notfound");
            notFoundPopup.addParameter("requestedPlaceIdentifier",
                                       resolvedPlaceRequest.getIdentifier());

            if (activityManager.containsActivity(notFoundPopup)) {
                return new ResolvedRequest(null,
                                           notFoundPopup);
            } else {
                final PlaceRequest ufNotFoundPopup = new DefaultPlaceRequest("uf.workbench.activity.notfound");
                ufNotFoundPopup.addParameter("requestedPlaceIdentifier",
                                             place.getIdentifier());
                return new ResolvedRequest(null,
                                           ufNotFoundPopup);
            }
        } else if (activities.size() > 1) {
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest("workbench.activities.multiple").addParameter("requestedPlaceIdentifier",
                                                                                                                      null);

            return new ResolvedRequest(null,
                                       multiplePlaces);
        }

        Activity unambigousActivity = activities.iterator().next();
        existingWorkbenchActivities.put(resolvedPlaceRequest,
                                        unambigousActivity);
        return new ResolvedRequest(unambigousActivity,
                                   resolvedPlaceRequest);
    }

    private PlaceRequest resolvePlaceRequest(PlaceRequest place) {
        if (isaConditionalPlaceRequest(place)) {
            return resolveConditionalPlaceRequest((ConditionalPlaceRequest) place);
        }
        return place;
    }

    private PlaceRequest resolveConditionalPlaceRequest(ConditionalPlaceRequest conditionalPlaceRequest) {
        return conditionalPlaceRequest.resolveConditionalPlaceRequest();
    }

    private boolean isaConditionalPlaceRequest(PlaceRequest place) {
        return place instanceof ConditionalPlaceRequest;
    }

    private ResolvedRequest resolveExistingParts(final PlaceRequest place) {
        final Activity activity = getActivity(place);

        if (activity != null) {
            return new ResolvedRequest(activity,
                                       place);
        }

        if (place instanceof PathPlaceRequest) {
            final ObservablePath path = ((PathPlaceRequest) place).getPath();

            for (final Map.Entry<PlaceRequest, PartDefinition> entry : visibleWorkbenchParts.entrySet()) {
                final PlaceRequest pr = entry.getKey();
                if (pr instanceof PathPlaceRequest) {
                    final Path visiblePath = ((PathPlaceRequest) pr).getPath();
                    final String visiblePathURI = visiblePath.toURI();
                    if ((visiblePathURI != null && visiblePathURI.compareTo(path.toURI()) == 0) || visiblePath.compareTo(path) == 0) {
                        return new ResolvedRequest(getActivity(pr),
                                                   pr);
                    }
                }
            }
        }

        return null;
    }

    private void goTo(final PartDefinition part,
                     final PanelDefinition panel) {
        final PlaceRequest place = part.getPlace();
        if (place == null) {
            return;
        }

        final ResolvedRequest resolved = resolveActivity(place);

        if (resolved.getActivity() != null) {
            final Activity activity = resolved.getActivity();

            if (activity.isType(ActivityResourceType.EDITOR.name()) ||
                    activity.isType(ActivityResourceType.SCREEN.name())) {
                launchWorkbenchActivityInPanel(place,
                                               activity,
                                               part,
                                               panel);
            } else {
                throw new IllegalArgumentException("placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel.");
            }
        } else {
            goTo(resolved.getPlaceRequest(),
                 (PanelDefinition) null);
        }
    }

    private Activity getActivity(final PlaceRequest place) {
        if (place == null) {
            return null;
        }
        return existingWorkbenchActivities.get(place);
    }

    @Override
    public void closePlace(final PlaceRequest placeToClose) {
        if (placeToClose == null) {
            return;
        }
        closePlace(placeToClose,
                   null);
    }

    private void launchWorkbenchActivity(final PlaceRequest place,
                                         final Activity activity,
                                         final PanelDefinition _panel) {

        if (visibleWorkbenchParts.containsKey(place)) {
            return;
        }

        final PartDefinition part = new PartDefinitionImpl(place);
        final PanelDefinition panel;
        if (_panel != null) {
            panel = _panel;
        } else {
            throw new RuntimeException("CAPONETTO");
        }

        launchWorkbenchActivityInPanel(place,
                                       activity,
                                       part,
                                       panel);
    }

    private void launchWorkbenchActivityInPanel(final PlaceRequest place,
                                                final Activity activity,
                                                final PartDefinition part,
                                                final PanelDefinition panel) {
        if (visibleWorkbenchParts.containsKey(place)) {
            return;
        }

        visibleWorkbenchParts.put(place,
                                  part);

        panelManager.addWorkbenchPart(place,
                                      part,
                                      panel,
                                      activity.getWidget(),
                                      toInteger(panel.getWidthAsInt()),
                                      toInteger(panel.getHeightAsInt()));

        try {
            activity.onOpen();
        } catch (Exception ex) {
            closePlace(place);
        }
    }

    /**
     * Opens all the parts of the given panel and its subpanels. This is a subroutine of the perspective switching
     * process.
     */
    private void openPartsRecursively(PanelDefinition panel) {

        for (PartDefinition part : ensureIterable(panel.getParts())) {
            final PlaceRequest place = part.getPlace().clone();
            part.setPlace(place);
            goTo(part, panel);
        }
        for (PanelDefinition child : ensureIterable(panel.getChildren())) {
            openPartsRecursively(child);
        }
    }

    @Override
    public void closePlace(final PlaceRequest place,
                           final Command onAfterClose) {

        final Activity existingActivity = existingWorkbenchActivities.get(place);
        if (existingActivity == null) {
            return;
        }

        final Command closeCommand = getCloseCommand(place,
                                                     onAfterClose);

        final BiParameterizedCommand<Command, PlaceRequest> closeChain = (chain, placeRequest) -> chain.execute();
        closeChain.execute(closeCommand,
                           place);
    }

    private Command getCloseCommand(final PlaceRequest place,
                                    final Command onAfterClose) {
        return () -> {

            final Activity activity = existingWorkbenchActivities.get(place);
            if (activity == null) {
                return;
            }

            if (activity.isType(ActivityResourceType.SCREEN.name())) {
                try {
                    activity.onClose();
                } catch (Exception ex) {
                }
            } else {
                activity.onClose();
            }

            panelManager.removePartForPlace(place);
            existingWorkbenchActivities.remove(place);
            visibleWorkbenchParts.remove(place);
            activityManager.destroyActivity(activity);

            // currently, we force all custom panels as Static panels, so they can only ever contain the one part we put in them.
            // we are responsible for cleaning them up when their place closes.
            PanelDefinition customPanelDef = customPanels.remove(place);
            if (customPanelDef != null) {
                panelManager.removeWorkbenchPanel(customPanelDef);
            }

            if (place instanceof PathPlaceRequest) {
                ((PathPlaceRequest) place).getPath().dispose();
            }

            if (onAfterClose != null) {
                onAfterClose.execute();
            }
        };
    }

    @Produces
    @ApplicationScoped
    private EventBus produceEventBus() {
        if (tempBus == null) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

    /**
     * The result of an attempt to resolve a PlaceRequest to an Activity.
     */
    private static class ResolvedRequest {

        private final Activity activity;
        private final PlaceRequest placeRequest;

        public ResolvedRequest(final Activity resolvedActivity,
                               final PlaceRequest substitutePlace) {
            this.activity = resolvedActivity;
            this.placeRequest = substitutePlace;
        }

        public Activity getActivity() {
            return activity;
        }

        public PlaceRequest getPlaceRequest() {
            return placeRequest;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final ResolvedRequest resolvedRequest = (ResolvedRequest) o;

            if (!Objects.equals(activity, resolvedRequest.activity)) {
                return false;
            }

            return Objects.equals(placeRequest, resolvedRequest.placeRequest);
        }

        @Override
        public int hashCode() {
            int result;
            result = activity != null ? activity.hashCode() : 0;
            result = 31 * result + (placeRequest != null ? placeRequest.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "{activity=" + activity + ", placeRequest=" + placeRequest + "}";
        }
    }
}
