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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;
import org.uberfire.commons.data.Pair;
import org.uberfire.experimental.service.auth.ExperimentalActivitiesAuthorizationManager;

import static java.util.Collections.sort;

/**
 *
 */
@ApplicationScoped
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class ActivityBeansCache {

    /**
     * All active activity beans mapped by their CDI bean name (names are mandatory for activity beans).
     */
    private final Map<String, SyncBeanDef<Activity>> activitiesById = new HashMap<String, SyncBeanDef<Activity>>();
    /**
     * All active Activities that have an {@link AssociatedResources} annotation and are not splash screens.
     */

    /**
     * All active activities that are splash screens.
     */
    private final List<SplashScreenActivity> splashActivities = new ArrayList<SplashScreenActivity>();
    private SyncBeanManager iocManager;
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent;
    protected ResourceTypeManagerCache resourceTypeManagerCache;
    private ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager;

    public ActivityBeansCache() {
    }

    @Inject
    public ActivityBeansCache(SyncBeanManager iocManager,
                              Event<NewPerspectiveEvent> newPerspectiveEventEvent,
                              Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent,
                              ResourceTypeManagerCache resourceTypeManagerCache,
                              ExperimentalActivitiesAuthorizationManager experimentalActivitiesAuthorizationManager) {
        this.iocManager = iocManager;
        this.newPerspectiveEventEvent = newPerspectiveEventEvent;
        this.newWorkbenchScreenEventEvent = newWorkbenchScreenEventEvent;
        this.resourceTypeManagerCache = resourceTypeManagerCache;
        this.experimentalActivitiesAuthorizationManager = experimentalActivitiesAuthorizationManager;
    }

    @PostConstruct
    void init() {

        final Collection<SyncBeanDef<Activity>> availableActivities = getAvailableActivities();

        for (final SyncBeanDef<Activity> activityBean : availableActivities) {

            final String id = activityBean.getName();

            validateUniqueness(id);

            activitiesById.put(id,
                               activityBean);

            if (isSplashScreen(activityBean.getQualifiers())) {
                splashActivities.add((SplashScreenActivity) activityBean.getInstance());
            } else {
                final Pair<Integer, List<String>> metaInfo = generateActivityMetaInfo(activityBean);
                if (metaInfo != null) {
                    addResourceActivity(activityBean,
                                        metaInfo);
                }
            }
        }

        this.resourceTypeManagerCache.sortResourceActivitiesByPriority();
    }

    private void addResourceActivity(SyncBeanDef<Activity> activityBean,
                                     Pair<Integer, List<String>> metaInfo) {
        ActivityAndMetaInfo activityAndMetaInfo = new ActivityAndMetaInfo(iocManager,
                                                                          activityBean,
                                                                          metaInfo.getK1(),
                                                                          metaInfo.getK2());
        this.resourceTypeManagerCache.addResourceActivity(activityAndMetaInfo);
    }

    Collection<SyncBeanDef<Activity>> getAvailableActivities() {
        Collection<SyncBeanDef<Activity>> activeBeans = new ArrayList<SyncBeanDef<Activity>>();
        for (SyncBeanDef<Activity> bean : iocManager.lookupBeans(Activity.class)) {
            if (bean.isActivated()) {
                activeBeans.add(bean);
            }
        }
        return activeBeans;
    }

    private boolean isSplashScreen(final Set<Annotation> qualifiers) {
        for (final Annotation qualifier : qualifiers) {
            if (qualifier instanceof IsSplashScreen) {
                return true;
            }
        }
        return false;
    }

    public void removeActivity(String id) {
        activitiesById.remove(id);
    }

    /**
     * Used for runtime plugins.
     */
    public void addNewScreenActivity(final SyncBeanDef<Activity> activityBean) {
        final String id = activityBean.getName();

        validateUniqueness(id);

        activitiesById.put(id,
                           activityBean);
        newWorkbenchScreenEventEvent.fire(new NewWorkbenchScreenEvent(id));
    }

    private void validateUniqueness(final String id) {
        if (activitiesById.keySet().contains(id)) {
            throw new RuntimeException("Conflict detected: Activity already exists with id " + id);
        }
    }

    /**
     * Used for runtime plugins.
     */
    public void addNewPerspectiveActivity(final SyncBeanDef<Activity> activityBean) {
        final String id = activityBean.getName();

        validateUniqueness(id);

        activitiesById.put(id,
                           activityBean);
        newPerspectiveEventEvent.fire(new NewPerspectiveEvent(id));
    }

    /**
     * Used for runtime plugins.
     */
    public void addNewEditorActivity(final SyncBeanDef<Activity> activityBean,
                                     String priority,
                                     String resourceTypeName) {
        final String id = activityBean.getName();

        validateUniqueness(id);

        activitiesById.put(id,
                           activityBean);

        this.resourceTypeManagerCache.addResourceActivity(new ActivityAndMetaInfo(iocManager,
                                                                                  activityBean,
                                                                                  Integer.valueOf(priority),
                                                                                  Arrays.asList(resourceTypeName)));
        this.resourceTypeManagerCache.sortResourceActivitiesByPriority();
    }

    public void addNewSplashScreenActivity(final SyncBeanDef<Activity> activityBean) {
        final String id = activityBean.getName();

        validateUniqueness(id);

        activitiesById.put(id,
                           activityBean);
        splashActivities.add((SplashScreenActivity) activityBean.getInstance());
    }

    public boolean hasActivity(String id) {
        return activitiesById.containsKey(id);
    }

    /**
     * Returns all active splash screen activities in this cache.
     */
    public List<SplashScreenActivity> getSplashScreens() {
        return splashActivities;
    }

    /**
     * Returns the activity with the given CDI bean name from this cache, or null if there is no such activity or the
     * activity with the given name is not an activated bean.
     * @param id the CDI name of the bean (see {@link Named}), or in the case of runtime plugins, the name the activity
     * was registered under.
     */
    public SyncBeanDef<Activity> getActivity(final String id) {
        return activitiesById.get(id);
    }

    /**
     * Returns the activated activity with the highest priority that can handle the given file. Returns null if no
     * activated activity can handle the path.
     * @param path the file to find a path-based activity for (probably a {@link WorkbenchEditorActivity}, but this cache
     * makes no guarantees).
     */
    public SyncBeanDef<Activity> getActivity(final Path path) {

        Optional<ActivityAndMetaInfo> optional = resourceTypeManagerCache.getResourceActivities().stream()
                .filter(activityAndMetaInfo -> activitySupportsPath(activityAndMetaInfo, path))
                .findAny();

        if(optional.isPresent()) {
            return optional.get().getActivityBean();
        }

        throw new EditorResourceTypeNotFound();
    }

    private boolean activitySupportsPath(ActivityAndMetaInfo activity, Path path) {

        // Check if the editor activity is experimental && enabled
        if(experimentalActivitiesAuthorizationManager.authorizeActivityClass(activity.getActivityBean().getBeanClass())) {

            // Check if the editor resources types support the given path
            return Stream.of(activity.getResourceTypes())
                    .anyMatch(clientResourceType -> clientResourceType.accept(path));
        }

        return false;
    }

    public List<SyncBeanDef<Activity>> getPerspectiveActivities() {
        List<SyncBeanDef<Activity>> results = new ArrayList<>();
        for (SyncBeanDef<Activity> beanDef : activitiesById.values()) {
            if (beanDef.isAssignableTo(PerspectiveActivity.class)) {
                results.add(beanDef);
            }
        }
        return results;
    }

    Pair<Integer, List<String>> generateActivityMetaInfo(SyncBeanDef<Activity> activityBean) {
        return ActivityMetaInfo.generate(activityBean);
    }

    public List<String> getActivitiesById() {
        return new ArrayList<String>(activitiesById.keySet());
    }

    public class EditorResourceTypeNotFound extends RuntimeException {

    }
}
