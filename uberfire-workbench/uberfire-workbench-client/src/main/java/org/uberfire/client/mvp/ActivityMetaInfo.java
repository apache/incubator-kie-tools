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
import java.util.List;
import java.util.Set;

import org.jboss.errai.ioc.client.container.DynamicAnnotation;
import org.jboss.errai.ioc.client.container.IOCBeanDef;
import org.uberfire.client.workbench.annotations.AssociatedResources;
import org.uberfire.client.workbench.annotations.Priority;
import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.commons.data.Pair;

public class ActivityMetaInfo {

    static Pair<Integer, List<String>> generate(final IOCBeanDef<?> beanDefinition) {

        AssociatedResources associatedResources = null;
        DynamicAnnotation dynAssociatedResources = null;

        Priority priority = null;
        DynamicAnnotation dynPriority = null;

        final Set<Annotation> annotations = beanDefinition.getQualifiers();
        final boolean dynamic = beanDefinition.isDynamic();

        for (Annotation a : annotations) {
            final DynamicAnnotation da = (dynamic) ? (DynamicAnnotation) a : null;
            if (a instanceof AssociatedResources) {
                associatedResources = (AssociatedResources) a;
            } else if (da != null && AssociatedResources.class.getName().equals(da.getName())) {
                dynAssociatedResources = da;
            } else if (a instanceof Priority) {
                priority = (Priority) a;
            } else if (da != null && Priority.class.getName().equals(da.getName())) {
                dynPriority = da;
            }
        }

        if (associatedResources == null && dynAssociatedResources == null) {
            return null;
        }

        final int priorityValue;
        if (priority == null && dynPriority == null) {
            priorityValue = 0;
        } else {
            if (dynamic) {
                priorityValue = Integer.valueOf(dynPriority.getMember("value"));
            } else {
                priorityValue = priority.value();
            }
        }

        final List<String> types = new ArrayList<String>();
        if (dynamic) {
            String resourceTypes = dynAssociatedResources.getMember("value");
            resourceTypes = resourceTypes.substring(1,
                                                    resourceTypes.length() - 1);
            types.addAll(Arrays.asList(resourceTypes.split(",")));
        } else {
            for (Class<? extends ClientResourceType> type : associatedResources.value()) {
                types.add(type.getName());
            }
        }

        return Pair.newPair(priorityValue,
                            types);
    }
}
