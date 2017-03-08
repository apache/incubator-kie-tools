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

package org.uberfire.ext.preferences.client.utils;

import java.lang.annotation.Annotation;
import java.util.Collection;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.mvp.ActivityBeansInfo;
import org.uberfire.client.mvp.WorkbenchScreenActivity;
import org.uberfire.ext.preferences.client.annotations.PreferenceForm;

/**
 * Service that provides preferences beans information.
 */
@ApplicationScoped
public class PreferenceFormBeansInfo {

    private ActivityBeansInfo activityBeansInfo;

    @Inject
    public PreferenceFormBeansInfo(final ActivityBeansInfo activityBeansInfo) {
        this.activityBeansInfo = activityBeansInfo;
    }

    /**
     * Searches for a {@link WorkbenchScreenActivity} that is qualified with {@link PreferenceForm} with the passed
     * preference bean identifier as its value.
     * @param preferenceIdentifier The {@link PreferenceForm} value to be searched.
     * @return The screen bean identifier.
     */
    public String getPreferenceFormFor(final String preferenceIdentifier) {
        final Collection<? extends IOCBeanDef<?>> screenBeans = activityBeansInfo.lookupBeans(WorkbenchScreenActivity.class);

        for (final IOCBeanDef<?> beanDef : screenBeans) {
            for (final Annotation annotation : beanDef.getQualifiers()) {
                if (annotation instanceof PreferenceForm) {
                    PreferenceForm preferenceFormQualifier = (PreferenceForm) annotation;
                    if (preferenceIdentifier.equals(preferenceFormQualifier.value())) {
                        return activityBeansInfo.getId(beanDef);
                    }
                }
            }
        }

        return null;
    }
}
