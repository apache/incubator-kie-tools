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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;

@ApplicationScoped
public class ActivityBeansInfo {

    private static Comparator<String> ALPHABETICAL_ORDER = (str1, str2) -> {
        int res = String.CASE_INSENSITIVE_ORDER.compare(str1, str2);
        return res == 0 ? str1.compareTo(str2) : res;
    };

    private SyncBeanManager beanManager;
    private ActivityBeansCache activityBeansCache;

    @Inject
    public ActivityBeansInfo(SyncBeanManager beanManager, ActivityBeansCache activityBeansCache) {
        this.beanManager = beanManager;
        this.activityBeansCache = activityBeansCache;
    }

    public List<String> getAvailableWorkbenchScreensIds() {
        return lookupBeansId(WorkbenchScreenActivity.class);
    }

    public List<String> getAvailablePerspectivesIds() {
        return activityBeansCache.getPerspectiveActivities().stream()
                .map(this::getId)
                .sorted(ALPHABETICAL_ORDER)
                .collect(Collectors.toList());
    }

    public List<String> getAvailableSplashScreensIds() {
        return lookupBeansId(SplashScreenActivity.class);
    }

    public List<String> getAvailableWorkbenchEditorsIds() {
        return lookupBeansId(WorkbenchEditorActivity.class);
    }

    public void addActivityBean(List<String> activityBeans,
                                String newBean) {
        activityBeans.add(newBean);
        Collections.sort(activityBeans,
                         ALPHABETICAL_ORDER);
    }

    private List<String> lookupBeansId(Class<?> activityClass) {
        return lookupBeans(activityClass).stream()
                .map(this::getId)
                .sorted(ALPHABETICAL_ORDER)
                .collect(Collectors.toList());
    }

    public Collection<? extends IOCBeanDef<?>> lookupBeans(final Class<?> activityClass) {
        return getBeanManager().lookupBeans(activityClass);
    }

    public String getId(final IOCBeanDef<?> beanDef) {
        for (final Annotation annotation : beanDef.getQualifiers()) {
            if (isNamed(annotation)) {
                return ((Named) annotation).value();
            }
        }
        if (hasBeanName(beanDef)) {
            return beanDef.getName();
        }
        return "";
    }

    boolean isNamed(Annotation annotation) {
        return annotation instanceof Named;
    }

    public SyncBeanManager getBeanManager() {
        return beanManager;
    }

    private boolean hasBeanName(IOCBeanDef<?> beanDef) {
        return beanDef.getName() != null && !beanDef.getName().isEmpty();
    }
}
