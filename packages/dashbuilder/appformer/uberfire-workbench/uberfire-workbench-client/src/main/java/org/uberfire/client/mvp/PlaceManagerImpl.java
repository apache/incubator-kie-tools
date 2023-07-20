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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;
import jsinterop.annotations.JsMethod;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.api.SharedSingleton;
import org.uberfire.client.mvp.ActivityLifecycleError.LifecyclePhase;
import org.uberfire.client.workbench.LayoutSelection;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.WorkbenchLayout;
import org.uberfire.client.workbench.events.BeforeClosePlaceEvent;
import org.uberfire.client.workbench.events.ClosePlaceEvent;
import org.uberfire.client.workbench.events.PlaceGainFocusEvent;
import org.uberfire.client.workbench.events.PlaceLostFocusEvent;
import org.uberfire.client.workbench.events.SelectPlaceEvent;
import org.uberfire.mvp.BiParameterizedCommand;
import org.uberfire.mvp.Command;
import org.uberfire.mvp.Commands;
import org.uberfire.mvp.ParameterizedCommand;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.ForcedPlaceRequest;
import org.uberfire.workbench.model.ActivityResourceType;
import org.uberfire.workbench.model.CustomPanelDefinition;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.PerspectiveDefinition;
import org.uberfire.workbench.model.Position;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static java.util.Collections.unmodifiableCollection;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.uberfire.plugin.PluginUtil.ensureIterable;
import static org.uberfire.plugin.PluginUtil.toInteger;

@SharedSingleton
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class PlaceManagerImpl implements PlaceManager {

    /**
     * Activities that have been created by us but not destroyed 
     */
    private final Map<PlaceRequest, Activity> existingWorkbenchActivities = new HashMap<PlaceRequest, Activity>();

    /**
     * Places that are currently open in the current perspective.
     */
    private final Map<PlaceRequest, PartDefinition> visibleWorkbenchParts = new HashMap<PlaceRequest, PartDefinition>();

    /**
     * Custom panels we have opened but not yet closed.
     */
    private final Map<PlaceRequest, CustomPanelDefinition> customPanels = new HashMap<>();

    private final Map<PlaceRequest, List<Command>> onOpenCallbacks = new HashMap<>();
    private final Map<PlaceRequest, List<Command>> onCloseCallbacks = new HashMap<>();
    private final Map<String, BiParameterizedCommand<Command, PlaceRequest>> perspectiveCloseChain = new HashMap<>();
    private final Map<String, PopupActivity> activePopups = new HashMap<String, PopupActivity>();
    private final Map<PlaceRequest, Activity> onMayCloseList = new HashMap<PlaceRequest, Activity>();
    private EventBus tempBus = null;
    @Inject
    private Event<BeforeClosePlaceEvent> workbenchPartBeforeCloseEvent;
    @Inject
    private Event<ClosePlaceEvent> workbenchPartCloseEvent;
    @Inject
    private ActivityManager activityManager;
    @Inject
    private Event<SelectPlaceEvent> selectWorkbenchPartEvent;
    @Inject
    private PanelManager panelManager;
    @Inject
    private PerspectiveManager perspectiveManager;
    @Inject
    private ActivityLifecycleErrorHandler lifecycleErrorHandler;
    private WorkbenchLayout workbenchLayout;
    @Inject
    private LayoutSelection layoutSelection;

    @PostConstruct
    public void init() {
        workbenchLayout = layoutSelection.get();
    }

    @Override
    public void goTo(final String identifier) {
        final var place = new DefaultPlaceRequest(identifier);
        goTo(place, (PanelDefinition) null);
    }

    @Override
    public void goTo(PlaceRequest place) {
        goTo(place,
                (PanelDefinition) null);
    }

    @Override
    public void goTo(final PlaceRequest place,
                     final PanelDefinition panel) {
        goTo(place,
                panel,
                Commands.DO_NOTHING);
    }

    private void closeOpenPlacesAt(Predicate<CustomPanelDefinition> filterPanels) {
        new HashSet<>(customPanels.values()).stream()
                .filter(filterPanels)
                .flatMap(p -> p.getParts().stream())
                .forEach(part -> closePlace(part.getPlace()));
    }

    private Predicate<CustomPanelDefinition> panelsOfThisHTMLElement(HTMLElement addTo) {
        return p -> p.getHtmlElementContainer().isPresent() && p.getHtmlElementContainer().get().equals(addTo);
    }

    private Predicate<CustomPanelDefinition> panelsOfThisHasWidgets(HasWidgets addTo) {
        return p -> p.getHasWidgetsContainer().isPresent() && p.getHasWidgetsContainer().get().equals(addTo);
    }

    private Predicate<CustomPanelDefinition> panelsOfThisHTMLElement(elemental2.dom.HTMLElement addTo) {
        return p -> p.getElemental2HtmlElementContainer().isPresent() && p.getElemental2HtmlElementContainer().get()
                .equals(addTo);
    }

    private void goToTargetPanel(final PlaceRequest place,
                                 final CustomPanelDefinition adoptedPanel) {
        if (existingWorkbenchActivities.containsKey(place)) {
            // if already open, behaviour is to select the place where it already lives
            goTo(place,
                    null,
                    Commands.DO_NOTHING);
        } else {
            customPanels.put(place,
                    adoptedPanel);
            goTo(place,
                    adoptedPanel,
                    Commands.DO_NOTHING);
        }
    }

    private void goTo(final PlaceRequest place,
                      final PanelDefinition panel,
                      final Command doWhenFinished) {
        if (place == null || place.equals(DefaultPlaceRequest.NOWHERE)) {
            return;
        }

        final ResolvedRequest resolved = resolveActivity(place);

        if (resolved.getActivity() != null) {
            final Activity activity = resolved.getActivity();
            if (activity.isType(ActivityResourceType.SCREEN.name())) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;

                // check if we need to open the owning perspective before launching this screen/editor
                if (workbenchActivity.getOwningPlace() != null && getStatus(workbenchActivity
                        .getOwningPlace()) == PlaceStatus.CLOSE) {
                    goTo(workbenchActivity.getOwningPlace(),
                            null,
                            () -> goTo(place, panel, doWhenFinished));
                    return;
                }
                launchWorkbenchActivityAtPosition(resolved.getPlaceRequest(),
                        workbenchActivity,
                        workbenchActivity.getDefaultPosition(),
                        panel);
                doWhenFinished.execute();
            } else if (activity.isType(ActivityResourceType.POPUP.name())) {
                launchPopupActivity(resolved.getPlaceRequest(),
                        (PopupActivity) activity);
                doWhenFinished.execute();
            } else if (activity.isType(ActivityResourceType.PERSPECTIVE.name())) {
                launchPerspectiveActivity(place,
                        doWhenFinished,
                        (PerspectiveActivity) activity);
            }
        } else {
            goTo(resolved.getPlaceRequest(),
                    panel,
                    doWhenFinished);
        }
    }

    private void launchPerspectiveActivity(final PlaceRequest place,
                                           final Command doWhenFinished,
                                           final PerspectiveActivity activity) {
        final Command launchPerspectiveCommand = () -> {
            launchPerspectiveActivity(place,
                    activity,
                    doWhenFinished);
        };

        final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
        final boolean thereIsAnOpenedPerspective = currentPerspective != null;
        final boolean isDifferentPerspective = thereIsAnOpenedPerspective && !place.equals(currentPerspective
                .getPlace());
        final boolean isForcedPlaceRequest = place instanceof ForcedPlaceRequest;

        // Before launching the perspective, checks if there is some close chain to be executed for the current perspective
        if (thereIsAnOpenedPerspective && (isDifferentPerspective || isForcedPlaceRequest)) {
            final BiParameterizedCommand<Command, PlaceRequest> closeChain = this.perspectiveCloseChain.get(
                    currentPerspective.getIdentifier());
            if (closeChain != null) {
                closeChain.execute(launchPerspectiveCommand,
                        currentPerspective.getPlace());
            } else {
                launchPerspectiveCommand.execute();
            }
        } else {
            launchPerspectiveCommand.execute();
        }
    }

    private boolean closePlaces(final Collection<PlaceRequest> placeRequests) {
        boolean result = true;
        for (final PlaceRequest placeRequest : placeRequests) {
            final Activity activity = existingWorkbenchActivities.get(placeRequest);
            if (activity != null && (activity.isType(ActivityResourceType.SCREEN.name()))) {
                if (((WorkbenchActivity) activity).onMayClose()) {
                    onMayCloseList.put(placeRequest,
                            activity);
                } else {
                    result = false;
                    break;
                }
            }
        }

        if (!result) {
            onMayCloseList.clear();
        } else {
            for (final PlaceRequest placeRequest : placeRequests) {
                closePlace(placeRequest);
            }
        }

        return result;
    }

    /**
     * Resolves the given place request into an Activity instance, if one can be found. If not, this method substitutes
     * special "not found" or "too many" place requests when the resolution doesn't work.
     * <p/>
     * The behaviour of this method is affected by the boolean-valued
     * {@code org.uberfire.client.mvp.PlaceManagerImpl.ignoreUnkownPlaces} property in {@link UberfirePreferences}.
     * @param place A non-null place request that could have originated from within application code, from within the
     * framework, or by parsing a hash fragment from a browser history event.
     * @param lazyLoadingSuccessCallback
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

        if (activities == null || activities.size() == 0) {
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
            final PlaceRequest multiplePlaces = new DefaultPlaceRequest("workbench.activities.multiple").addParameter(
                    "requestedPlaceIdentifier",
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

    private void closeLazyLoadingScreenAndGoToPlace(final PlaceRequest place) {
        this.closePlace(new DefaultPlaceRequest("LazyLoadingScreen"), () -> this.goTo(place));
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

        return null;
    }

    @Override
    public void goTo(final PartDefinition part,
                     final PanelDefinition panel) {
        final PlaceRequest place = part.getPlace();
        if (place == null) {
            return;
        }

        final ResolvedRequest resolved = resolveActivity(place);

        if (resolved.getActivity() != null) {
            final Activity activity = resolved.getActivity();

            if (activity.isType(ActivityResourceType.SCREEN.name())) {
                final WorkbenchActivity workbenchActivity = (WorkbenchActivity) activity;
                launchWorkbenchActivityInPanel(place,
                        workbenchActivity,
                        part,
                        panel);
            } else {
                throw new IllegalArgumentException(
                        "placeRequest does not represent a WorkbenchActivity. Only WorkbenchActivities can be launched in a specific targetPanel.");
            }
        } else {
            goTo(resolved.getPlaceRequest());
        }
    }

    @Override
    public Activity getActivity(final PlaceRequest place) {
        if (place == null) {
            return null;
        }
        final Activity activity = existingWorkbenchActivities.get(place);
        return activity;
    }

    @Override
    public PlaceStatus getStatus(final PlaceRequest place) {
        PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
        if (currentPerspective != null && currentPerspective.getPlace().equals(place)) {
            return PlaceStatus.OPEN;
        }
        return resolveExistingParts(place) != null ? PlaceStatus.OPEN : PlaceStatus.CLOSE;
    }

    @JsMethod
    @Override
    public void closePlace(final String id) {
        closePlace(new DefaultPlaceRequest(id));
    }

    @JsMethod
    @Override
    public void closePlace(final PlaceRequest placeToClose) {
        if (placeToClose == null) {
            return;
        }
        closePlace(placeToClose,
                false);
    }

    @Override
    public void closePlace(final PlaceRequest placeToClose,
                           final Command doAfterClose) {
        if (placeToClose == null) {
            return;
        }
        closePlace(placeToClose,
                false,
                doAfterClose);
    }

    @Override
    public void tryClosePlace(final PlaceRequest placeToClose,
                              final Command onAfterClose) {
        boolean execute = false;
        if (placeToClose == null) {
            execute = true;
        } else {
            execute = closePlaces(Arrays.asList(placeToClose));
        }

        if (execute) {
            onAfterClose.execute();
        }
    }

    @Override
    public void forceClosePlace(final String id) {
        forceClosePlace(new DefaultPlaceRequest(id));
    }

    @Override
    public void forceClosePlace(final PlaceRequest placeToClose) {
        if (placeToClose == null) {
            return;
        }
        closePlace(placeToClose,
                true);
    }

    @Override
    public void closeAllPlaces() {
        closeAllPlaces(false);
    }

    @Override
    public void forceCloseAllPlaces() {
        closeAllPlaces(true);
    }

    private void closeAllPlaces(final boolean force) {
        final List<PlaceRequest> placesToClose = new ArrayList<PlaceRequest>(visibleWorkbenchParts.keySet());
        for (PlaceRequest placeToClose : placesToClose) {
            closePlace(placeToClose,
                    force);
        }
    }

    @Override
    public boolean closeAllPlacesOrNothing() {
        final List<PlaceRequest> placesToClose = new ArrayList<>(visibleWorkbenchParts.keySet());
        for (PlaceRequest placeToClose : placesToClose) {
            if (!canClosePlace(placeToClose)) {
                return false;
            }
        }

        forceCloseAllPlaces();
        return true;
    }

    @Override
    public List<PlaceRequest> getUncloseablePlaces() {
        final Set<PlaceRequest> placesToClose = visibleWorkbenchParts.keySet();
        final List<PlaceRequest> uncloseablePlaces = new ArrayList<>();

        for (PlaceRequest place : placesToClose) {
            if (!canClosePlace(place)) {
                uncloseablePlaces.add(place);
            }
        }

        return uncloseablePlaces;
    }

    private boolean closeAllCurrentPanels() {
        return closePlaces(new ArrayList<PlaceRequest>(visibleWorkbenchParts.keySet()));
    }

    @Override
    public void registerOnOpenCallback(final PlaceRequest place,
                                       final Command callback) {
        checkNotNull("place",
                place);
        checkNotNull("callback",
                callback);

        List<Command> callbacks = getOnOpenCallbacks(place);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            this.onOpenCallbacks.put(place,
                    callbacks);
        }

        callbacks.add(callback);
    }

    @Override
    public void unregisterOnOpenCallbacks(final PlaceRequest place) {
        checkNotNull("place",
                place);
        this.onOpenCallbacks.remove(place);
    }

    @Override
    public void registerOnCloseCallback(final PlaceRequest place,
                                        final Command callback) {
        checkNotNull("place",
                place);
        checkNotNull("callback",
                callback);

        List<Command> callbacks = getOnCloseCallbacks(place);
        if (callbacks == null) {
            callbacks = new ArrayList<>();
            this.onCloseCallbacks.put(place,
                    callbacks);
        }

        callbacks.add(callback);
    }

    @Override
    public void unregisterOnCloseCallbacks(final PlaceRequest place) {
        checkNotNull("place",
                place);
        this.onCloseCallbacks.remove(place);
    }

    @Override
    public void registerPerspectiveCloseChain(final String perspectiveIdentifier,
                                              final BiParameterizedCommand<Command, PlaceRequest> closeChain) {
        checkNotNull("perspectiveIdentifier",
                perspectiveIdentifier);
        checkNotNull("closeChain",
                closeChain);

        perspectiveCloseChain.put(perspectiveIdentifier,
                closeChain);
    }

    /**
     * Returns all the PlaceRequests that map to activies that are currently in the open state and accessible
     * somewhere in the current perspective.
     * @return an unmodifiable view of the current active place requests. This view may or may not update after
     * further calls into PlaceManager that modify the workbench state. It's best not to hold on to the returned
     * set; instead, call this method again for current information.
     */
    public Collection<PlaceRequest> getActivePlaceRequests() {
        return unmodifiableCollection(existingWorkbenchActivities.keySet());
    }

    private void launchWorkbenchActivityAtPosition(final PlaceRequest place,
                                                   final WorkbenchActivity activity,
                                                   final Position position,
                                                   final PanelDefinition _panel) {

        if (visibleWorkbenchParts.containsKey(place)) {
            selectWorkbenchPartEvent.fire(new SelectPlaceEvent(place));
            return;
        }

        final PartDefinition part = new PartDefinitionImpl(place);
        final PanelDefinition panel;
        if (_panel != null) {
            panel = _panel;
        } else {
            panel = panelManager.addWorkbenchPanel(panelManager.getRoot(),
                    position,
                    activity.preferredHeight(),
                    activity.preferredWidth(),
                    null,
                    null);
        }

        launchWorkbenchActivityInPanel(place,
                activity,
                part,
                panel);
    }

    private void launchWorkbenchActivityInPanel(final PlaceRequest place,
                                                final WorkbenchActivity activity,
                                                final PartDefinition part,
                                                final PanelDefinition panel) {
        if (visibleWorkbenchParts.containsKey(place)) {
            selectWorkbenchPartEvent.fire(new SelectPlaceEvent(place));
            return;
        }

        visibleWorkbenchParts.put(place,
                part);

        final IsWidget titleDecoration = maybeWrapExternalWidget(activity,
                () -> activity.getTitleDecorationElement(),
                () -> activity.getTitleDecoration());

        final IsWidget widget = maybeWrapExternalWidget(activity,
                () -> activity.getWidgetElement(),
                () -> activity.getWidget());

        final UIPart uiPart = new UIPart(activity.getTitle(),
                titleDecoration,
                widget);

        panelManager.addWorkbenchPart(place,
                part,
                panel,
                uiPart,
                activity.contextId(),
                toInteger(panel.getWidthAsInt()),
                toInteger(panel.getHeightAsInt()));

        try {
            activity.onOpen();
        } catch (Exception ex) {
            lifecycleErrorHandler.handle(activity,
                    LifecyclePhase.OPEN,
                    ex);
            closePlace(place);
        }
    }

    private IsWidget maybeWrapExternalWidget(WorkbenchActivity activity,
                                             Supplier<Element> element,
                                             Supplier<IsWidget> widget) {

        if (activity.isDynamic()) {
            final Element e = element.get();
            return (e == null) ? null : ElementWrapperWidget.getWidget(e);
        }

        return widget.get();
    }

    private void launchPopupActivity(final PlaceRequest place,
                                     final PopupActivity activity) {

        if (activePopups.get(place.getIdentifier()) != null) {
            return;
        }

        activePopups.put(place.getIdentifier(),
                activity);

        try {
            activity.onOpen();
        } catch (Exception ex) {
            activePopups.remove(place.getIdentifier());
            lifecycleErrorHandler.handle(activity,
                    LifecyclePhase.OPEN,
                    ex);
        }
    }

    /**
     * Before launching the perspective we check that it isn't already open by asking the
     * placeHistory service to extract the perspective encoded in the URL
     * @param place
     * @param activity
     * @param doWhenFinished
     */
    private void launchPerspectiveActivity(final PlaceRequest place,
                                           final PerspectiveActivity activity,
                                           final Command doWhenFinished) {

        checkNotNull("doWhenFinished",
                doWhenFinished);

        if (place instanceof ForcedPlaceRequest) {
            switchToPerspective(place,
                    activity,
                    new ParameterizedCommand<PerspectiveDefinition>() {

                        @Override
                        public void execute(PerspectiveDefinition perspectiveDef) {
                            openPartsRecursively(perspectiveDef.getRoot());
                            doWhenFinished.execute();
                            workbenchLayout.onResize();
                        }
                    });
        } else {
            final PerspectiveActivity oldPerspectiveActivity = perspectiveManager.getCurrentPerspective();
            if (oldPerspectiveActivity != null && place.equals(oldPerspectiveActivity.getPlace())) {
                return;
            }

            perspectiveManager.savePerspectiveState(new Command() {

                @Override
                public void execute() {
                    // first try to open the new perspective, so we can avoid leaving the user on a blank screen if the onOpen() method fails
                    try {
                        activity.onOpen();
                    } catch (Exception ex) {
                        lifecycleErrorHandler.handle(activity,
                                LifecyclePhase.OPEN,
                                ex);
                        try {
                            activity.onClose();
                        } catch (Exception ex2) {
                            // not unexpected; probably happened because onOpen failed to complete
                        }
                        existingWorkbenchActivities.remove(place);
                        activityManager.destroyActivity(activity);
                        return;
                    }

                    switchToPerspective(place,
                            activity,
                            new ParameterizedCommand<PerspectiveDefinition>() {

                                @Override
                                public void execute(PerspectiveDefinition perspectiveDef) {
                                    if (oldPerspectiveActivity != null) {
                                        try {
                                            oldPerspectiveActivity.onClose();
                                        } catch (Exception ex) {
                                            lifecycleErrorHandler.handle(oldPerspectiveActivity,
                                                    LifecyclePhase.CLOSE,
                                                    ex);
                                        }
                                        existingWorkbenchActivities.remove(oldPerspectiveActivity.getPlace());
                                        activityManager.destroyActivity(oldPerspectiveActivity);
                                    }
                                    openPartsRecursively(perspectiveDef.getRoot());
                                    doWhenFinished.execute();
                                    workbenchLayout.onResize();
                                }
                            });
                }
            });
        }
    }

    private void switchToPerspective(final PlaceRequest place,
                                     final PerspectiveActivity newPerspectiveActivity,
                                     final ParameterizedCommand<PerspectiveDefinition> closeOldPerspectiveOpenPartsAndExecuteChainedCallback) {
        if (closeAllCurrentPanels()) {
            perspectiveManager.switchToPerspective(place,
                    newPerspectiveActivity,
                    closeOldPerspectiveOpenPartsAndExecuteChainedCallback);
        } else {

            // some panels didn't want to close, so not going to launch new perspective. clean up its activity.
            try {
                newPerspectiveActivity.onClose();
            } catch (Exception ex) {
                lifecycleErrorHandler.handle(newPerspectiveActivity,
                        LifecyclePhase.OPEN,
                        ex);
            }
            existingWorkbenchActivities.remove(newPerspectiveActivity.getPlace());
            activityManager.destroyActivity(newPerspectiveActivity);
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

    private void closePlace(final PlaceRequest place,
                            final boolean force) {
        closePlace(place,
                force,
                null);
    }

    private void closePlace(final PlaceRequest place,
                            final boolean force,
                            final Command onAfterClose) {

        final Activity existingActivity = existingWorkbenchActivities.get(place);
        if (existingActivity == null) {
            return;
        }

        final Command closeCommand = getCloseCommand(place,
                force,
                onAfterClose);

        if (force) {
            closeCommand.execute();
        } else {
            final PerspectiveActivity currentPerspective = perspectiveManager.getCurrentPerspective();
            final BiParameterizedCommand<Command, PlaceRequest> closeChain = this.perspectiveCloseChain.getOrDefault(
                    currentPerspective.getIdentifier(),
                    (chain, placeRequest) -> chain.execute());
            closeChain.execute(closeCommand,
                    place);
        }
    }

    private Command getCloseCommand(final PlaceRequest place,
                                    final boolean force,
                                    final Command onAfterClose) {
        return () -> {

            final Activity activity = existingWorkbenchActivities.get(place);
            if (activity == null) {
                return;
            }

            workbenchPartBeforeCloseEvent.fire(new BeforeClosePlaceEvent(place,
                    force,
                    true));

            activePopups.remove(place.getIdentifier());

            if (activity.isType(ActivityResourceType.SCREEN.name())) {
                WorkbenchActivity activity1 = (WorkbenchActivity) activity;
                if (force || onMayCloseList.containsKey(place) || activity1.onMayClose()) {
                    onMayCloseList.remove(place);
                    try {
                        activity1.onClose();
                    } catch (Exception ex) {
                        lifecycleErrorHandler.handle(activity1,
                                LifecyclePhase.CLOSE,
                                ex);
                    }
                } else {
                    return;
                }
            } else if (activity.isType(ActivityResourceType.POPUP.name())) {
                PopupActivity activity1 = (PopupActivity) activity;
                if (force || activity1.onMayClose()) {
                    try {
                        activity1.onClose();
                    } catch (Exception ex) {
                        lifecycleErrorHandler.handle(activity1,
                                LifecyclePhase.CLOSE,
                                ex);
                    }
                } else {
                    return;
                }
            } else {
                activity.onClose();
            }

            workbenchPartCloseEvent.fire(new ClosePlaceEvent(place));

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

            if (onAfterClose != null) {
                onAfterClose.execute();
            }
        };
    }

    @Override
    public boolean canClosePlace(final PlaceRequest place) {

        final Activity activity = existingWorkbenchActivities.get(place);
        if (activity == null) {
            return false;
        }

        if (activity.isType(ActivityResourceType.SCREEN.name())) {
            WorkbenchActivity activity1 = (WorkbenchActivity) activity;
            if (onMayCloseList.containsKey(place) || activity1.onMayClose()) {
                return true;
            }
        } else if (activity.isType(ActivityResourceType.POPUP.name())) {
            PopupActivity activity1 = (PopupActivity) activity;
            if (activity1.onMayClose()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canCloseAllPlaces() {
        return getUncloseablePlaces().isEmpty();
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartOnFocus(@Observes PlaceGainFocusEvent event) {
        final PlaceRequest place = event.getPlace();
        final Activity activity = getActivity(place);
        if (activity == null) {
            return;
        }
        if (activity instanceof WorkbenchActivity) {
            ((WorkbenchActivity) activity).onFocus();
        }
    }

    @SuppressWarnings("unused")
    private void onWorkbenchPartLostFocus(@Observes PlaceLostFocusEvent event) {
        final Activity activity = getActivity(event.getPlace());
        if (activity == null) {
            return;
        }
        if (activity instanceof WorkbenchActivity) {
            ((WorkbenchActivity) activity).onLostFocus();
        }
    }

    @Produces
    @ApplicationScoped
    private EventBus produceEventBus() {
        if (tempBus == null) {
            tempBus = new SimpleEventBus();
        }
        return tempBus;
    }

    @Override
    public List<Command> getOnOpenCallbacks(final PlaceRequest place) {
        return this.onOpenCallbacks.get(place);
    }

    @Override
    public List<Command> getOnCloseCallbacks(final PlaceRequest place) {
        return this.onCloseCallbacks.get(place);
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

            if (activity != null ? !activity.equals(resolvedRequest.activity) : resolvedRequest.activity != null) {
                return false;
            }
            if (placeRequest != null ? !placeRequest.equals(resolvedRequest.placeRequest)
                    : resolvedRequest.placeRequest != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = 0;
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
