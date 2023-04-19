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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.events.NewPerspectiveEvent;
import org.uberfire.client.workbench.events.NewWorkbenchScreenEvent;

/**
 *
 */
@ApplicationScoped
public class ActivityBeansCache {

    /**
     * All active activity beans mapped by their CDI bean name (names are mandatory for activity beans).
     */
    private final Map<String, SyncBeanDef<Activity>> activitiesById = new HashMap<String, SyncBeanDef<Activity>>();

    private SyncBeanManager iocManager;
    private Event<NewPerspectiveEvent> newPerspectiveEventEvent;
    private Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent;

    public ActivityBeansCache() {}

    @Inject
    public ActivityBeansCache(SyncBeanManager iocManager,
                              Event<NewPerspectiveEvent> newPerspectiveEventEvent,
                              Event<NewWorkbenchScreenEvent> newWorkbenchScreenEventEvent) {
        this.iocManager = iocManager;
        this.newPerspectiveEventEvent = newPerspectiveEventEvent;
        this.newWorkbenchScreenEventEvent = newWorkbenchScreenEventEvent;
    }

    @PostConstruct
    void init() {
        final var availableActivities = getAvailableActivities();

        for (final var activityBean : availableActivities) {

            final var id = activityBean.getName();
            validateUniqueness(id);
            activitiesById.put(id, activityBean);
        }

    }

    private void put(final SyncBeanDef<Activity> activityBean,
                     final String id) {

        activitiesById.put(id,
                activityBean);
    }

    Collection<SyncBeanDef<Activity>> getAvailableActivities() {
        var activeBeans = new ArrayList<SyncBeanDef<Activity>>();
        for (var bean : iocManager.lookupBeans(Activity.class)) {
            if (bean.isActivated()) {
                activeBeans.add(bean);
            }
        }
        return activeBeans;
    }

    public void removeActivity(String id) {
        activitiesById.remove(id);
    }

    /**
     * Used for runtime plugins.
     */
    public void addNewScreenActivity(final SyncBeanDef<Activity> activityBean) {
        final var id = activityBean.getName();
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

    public boolean hasActivity(String id) {
        return activitiesById.containsKey(id);
    }

    /**
     * Returns the activity with the given CDI bean name from this cache, or null if there is no such activity or the
     * activity with the given name is not an activated bean.
     * @param id the CDI name of the bean (see {@link Named}), or in the case of runtime plugins, the name the activity
     * was registered under.
     */
    public SyncBeanDef<Activity> getActivity(final String id) {
        if (id == null) {
            return null;
        }
        return activitiesById.get(id);
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

    public List<String> getActivitiesById() {
        return new ArrayList<String>(activitiesById.keySet());
    }

    public void noOp() {
        // intentionally left empty, can be called to activate this bean in a CDI context
    }

}
