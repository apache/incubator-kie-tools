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
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.api.EnabledByProperty;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.util.GWTEditorNativeRegister;

/**
 *
 */
@ApplicationScoped
@EnabledByProperty(value = "uberfire.plugin.mode.active", negated = true)
public class ActivityBeansCache {

    /**
     * All active activity beans mapped by their CDI bean name (names are mandatory for activity beans).
     */
    private final Map<String, SyncBeanDef<Activity>> activitiesById = new HashMap<>();
    private SyncBeanManager iocManager;
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    public ActivityBeansCache() {
    }

    @Inject
    public ActivityBeansCache(SyncBeanManager iocManager,
                              GWTEditorNativeRegister gwtEditorNativeRegister) {
        this.iocManager = iocManager;
        this.gwtEditorNativeRegister = gwtEditorNativeRegister;
    }

    @PostConstruct
    void init() {
        registerGwtEditorProvider();

        final Collection<SyncBeanDef<Activity>> availableActivities = getAvailableActivities();

        for (final SyncBeanDef<Activity> activityBean : availableActivities) {

            final String id = activityBean.getName();

            validateUniqueness(id);

            activitiesById.put(id, activityBean);

            if (activityBean.getInstance() instanceof AbstractWorkbenchClientEditorActivity) {
                registerGwtClientBean(id, activityBean);
            }
        }
    }

    void registerGwtEditorProvider() {
        gwtEditorNativeRegister.nativeRegisterGwtEditorProvider();
    }

    void registerGwtClientBean(final String id, final SyncBeanDef<Activity> activityBean) {
        gwtEditorNativeRegister.nativeRegisterGwtClientBean(id, activityBean);
    }

    Collection<SyncBeanDef<Activity>> getAvailableActivities() {
        Collection<SyncBeanDef<Activity>> activeBeans = new ArrayList<>();
        for (SyncBeanDef<Activity> bean : iocManager.lookupBeans(Activity.class)) {
            if (bean.isActivated()) {
                activeBeans.add(bean);
            }
        }
        return activeBeans;
    }

    private void validateUniqueness(final String id) {
        if (activitiesById.containsKey(id)) {
            throw new RuntimeException("Conflict detected: Activity already exists with id " + id);
        }
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
}
