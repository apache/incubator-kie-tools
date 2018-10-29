/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.experimental.client.service.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.widgets.menu.events.PerspectiveVisibiltiyChangeEvent;
import org.uberfire.experimental.client.disabled.screen.DisabledFeatureActivity;
import org.uberfire.experimental.client.service.ClientExperimentalFeaturesRegistryService;
import org.uberfire.experimental.client.util.ExperimentalUtils;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;
import org.uberfire.experimental.service.events.NonPortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.events.PortableExperimentalFeatureModifiedEvent;
import org.uberfire.experimental.service.registry.ExperimentalFeature;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.ConditionalPlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

@Singleton
public class ExperimentalActivitiesAuthorizationManagerImpl implements ExperimentalActivitiesAuthorizationManager {

    private ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService;
    private SyncBeanManager iocManager;
    private Supplier<String> uniqueIdSupplier;
    private Event<PerspectiveVisibiltiyChangeEvent> perspectiveVisibleEvent;

    private Map<String, String> activityIdToExperimentalFeatureId = new HashMap<>();
    private Map<String, String> activityClassToExperimentalFeatureId = new HashMap<>();
    private List<String> perspectiveIds = new ArrayList<>();
    private List<String> screenIds = new ArrayList<>();
    private List<String> editorIds = new ArrayList<>();

    @Inject
    public ExperimentalActivitiesAuthorizationManagerImpl(SyncBeanManager iocManager, ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService, Event<PerspectiveVisibiltiyChangeEvent> perspectiveVisibleEvent) {
        this(iocManager, experimentalFeaturesRegistryService, perspectiveVisibleEvent, () -> createUniqueId());
    }

    ExperimentalActivitiesAuthorizationManagerImpl(SyncBeanManager iocManager, ClientExperimentalFeaturesRegistryService experimentalFeaturesRegistryService, Event<PerspectiveVisibiltiyChangeEvent> perspectiveVisibleEvent, Supplier<String> uniqueIdSupplier) {
        this.iocManager = iocManager;
        this.uniqueIdSupplier = uniqueIdSupplier;
        this.perspectiveVisibleEvent = perspectiveVisibleEvent;
        this.experimentalFeaturesRegistryService = experimentalFeaturesRegistryService;
    }

    public void init() {
        Collection<SyncBeanDef<ExperimentalActivityReference>> activities = iocManager.lookupBeans(ExperimentalActivityReference.class);

        activities.stream()
                .map(SyncBeanDef::getInstance)
                .forEach(activity -> {
                    activityIdToExperimentalFeatureId.put(activity.getActivityId(), activity.getExperimentalFeatureId());
                    activityClassToExperimentalFeatureId.put(activity.getActivityTypeName(), activity.getExperimentalFeatureId());
                    switch (activity.getActivityType()) {
                        case PERSPECTIVE:
                            perspectiveIds.add(activity.getActivityId());
                            break;
                        case SCREEN:
                            screenIds.add(activity.getActivityId());
                            break;
                        case EDITOR:
                            editorIds.add(activity.getActivityId());
                            break;
                    }
                });
    }

    @Override
    public boolean authorizeActivity(Object activity) {
        return authorizeActivityClass(activity.getClass());
    }

    @Override
    public boolean authorizeActivityClass(Class activityClass) {
        return authorizeByClassName(activityClass.getName());
    }

    @Override
    public void securePart(PartDefinition part, PanelDefinition panel) {

        final PlaceRequest request = part.getPlace();
        final String identifier = request.getIdentifier();

        if (request instanceof PathPlaceRequest) {
            return;
        }

        Optional<String> optional = Optional.ofNullable(activityIdToExperimentalFeatureId.get(identifier));

        if (optional.isPresent()) {
            panel.removePart(part);

            DefaultPlaceRequest disabledRequest = new DefaultPlaceRequest(DisabledFeatureActivity.ID);

            disabledRequest.addParameter(DisabledFeatureActivity.ID_PARAM, uniqueIdSupplier.get());
            disabledRequest.addParameter(DisabledFeatureActivity.FEATURE_ID_PARAM, optional.get());

            part.setPlace(new ConditionalPlaceRequest(identifier, request.getParameters()).when(placeRequest -> authorizeActivityId(identifier)).orElse(disabledRequest));
        }
    }

    protected boolean authorizeByClassName(final String activityClassName) {
        return doAuthorize(() -> activityClassToExperimentalFeatureId.get(activityClassName));
    }

    @Override
    public boolean authorizeActivityId(final String activityId) {
        return doAuthorize(() -> activityIdToExperimentalFeatureId.get(activityId));
    }

    protected boolean doAuthorize(final Supplier<String> keySupplier) {
        Optional<String> optional = Optional.ofNullable(keySupplier.get());

        return optional.map(this::authorize).orElse(true);
    }

    protected boolean authorize(final String experimentalFeatureId) {
        return experimentalFeaturesRegistryService.isFeatureEnabled(experimentalFeatureId);
    }

    private static String createUniqueId() {
        return ExperimentalUtils.createUniqueId();
    }

    public void onFeatureModified(@Observes PortableExperimentalFeatureModifiedEvent event) {
        onFeatureModified(event.getFeature());
    }

    public void onFeatureModified(@Observes NonPortableExperimentalFeatureModifiedEvent event) {
        onFeatureModified(event.getFeature());
    }

    private void onFeatureModified(ExperimentalFeature feature) {

        activityIdToExperimentalFeatureId.entrySet().stream()
                .filter(entry -> perspectiveIds.contains(entry.getKey()) && entry.getValue().equals(feature.getFeatureId()))
                .findAny()
                .map(Map.Entry::getKey)
                .ifPresent(activityId -> perspectiveVisibleEvent.fire(new PerspectiveVisibiltiyChangeEvent(activityId, feature.isEnabled())));
    }
}
