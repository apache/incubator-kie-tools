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
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.util.GWTEditorNativeRegister;
import org.uberfire.mvp.PlaceRequest;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@ApplicationScoped
public class ActivityManagerImpl implements ActivityManager {

    private final Map<Activity, PlaceRequest> startedActivities = new IdentityHashMap<>();
    private final Map<String, SyncBeanDef<Activity>> activitiesById = new HashMap<>();

    @Inject
    private SyncBeanManager iocManager;
    @Inject
    private GWTEditorNativeRegister gwtEditorNativeRegister;

    @PostConstruct
    void init() {
        gwtEditorNativeRegister.nativeRegisterGwtEditorProvider();

        iocManager.lookupBeans(Activity.class)
                .stream()
                .filter(IOCBeanDef::isActivated)
                .forEach(bean -> {
                    final String id = bean.getName();
                    if (activitiesById.containsKey(id)) {
                        throw new RuntimeException("Conflict detected: Activity already exists with id " + id);
                    }
                    activitiesById.put(id, bean);
                    if (bean.getInstance() instanceof EditorActivity) {
                        gwtEditorNativeRegister.nativeRegisterGwtClientBean(id, bean);
                    }
                });
    }

    @Override
    public Set<Activity> getActivities(final PlaceRequest placeRequest) {
        return getActivitiesFromBeans(resolveById(placeRequest.getIdentifier()))
                .stream()
                .map(activity -> startIfNecessary(activity, placeRequest))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @Override
    public void destroyActivity(final Activity activity) {
        if (startedActivities.remove(activity) == null) {
            throw new IllegalStateException("Activity " + activity + " is not currently in the started state");
        }
        if (getBeanScope(activity) == Dependent.class) {
            iocManager.destroyBean(activity);
        }
    }

    private Class<?> getBeanScope(Activity startedActivity) {
        final IOCBeanDef<?> beanDef = activitiesById.get(startedActivity.getPlace().getIdentifier());
        if (beanDef == null) {
            return Dependent.class;
        }
        return beanDef.getScope();
    }

    private Set<Activity> getActivitiesFromBeans(final Collection<SyncBeanDef<Activity>> activityBeans) {
        return activityBeans
                .stream()
                .filter(IOCBeanDef::isActivated)
                .map(SyncBeanDef::getInstance)
                .collect(Collectors.toSet());
    }

    private Activity startIfNecessary(Activity activity,
                                      PlaceRequest place) {
        if (activity == null) {
            return null;
        }
        try {
            startedActivities.computeIfAbsent(activity, a -> {
                a.onStartup(place);
                return place;
            });
            return activity;
        } catch (Exception ex) {
            destroyActivity(activity);
            return null;
        }
    }

    private Collection<SyncBeanDef<Activity>> resolveById(final String identifier) {
        if (identifier == null) {
            return emptyList();
        }

        SyncBeanDef<Activity> beanDefActivity = activitiesById.get(identifier);
        if (beanDefActivity == null) {
            return emptyList();
        }
        return singletonList(beanDefActivity);
    }
}
