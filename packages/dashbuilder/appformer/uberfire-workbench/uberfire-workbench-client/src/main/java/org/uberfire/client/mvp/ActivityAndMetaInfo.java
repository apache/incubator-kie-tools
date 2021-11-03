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
 *
 */

package org.uberfire.client.mvp;

import java.util.Collection;
import java.util.List;

import org.jboss.errai.ioc.client.container.SyncBeanDef;
import org.jboss.errai.ioc.client.container.SyncBeanManager;
import org.uberfire.client.workbench.type.ClientResourceType;

public class ActivityAndMetaInfo {

    private SyncBeanManager iocManager;
    private final SyncBeanDef<Activity> activityBean;

    private final int priority;
    final List<String> resourceTypesNames;
    ClientResourceType[] resourceTypes;

    @SuppressWarnings("rawtypes")
    ActivityAndMetaInfo(final SyncBeanManager iocManager,
                        final SyncBeanDef<Activity> activityBean,
                        final int priority,
                        final List<String> resourceTypesNames) {
        this.iocManager = iocManager;
        this.activityBean = activityBean;
        this.priority = priority;
        this.resourceTypesNames = resourceTypesNames;
    }

    public SyncBeanDef<Activity> getActivityBean() {
        return activityBean;
    }

    public int getPriority() {
        return priority;
    }

    public ClientResourceType[] getResourceTypes() {
        if (resourceTypes == null) {
            dynamicLookupResourceTypes();
        }
        return resourceTypes;
    }

    private void dynamicLookupResourceTypes() {
        this.resourceTypes = new ClientResourceType[resourceTypesNames.size()];
        for (int i = 0; i < resourceTypesNames.size(); i++) {
            final String resourceTypeIdentifier = resourceTypesNames.get(i);
            final Collection<SyncBeanDef> resourceTypeBeans = iocManager.lookupBeans(resourceTypeIdentifier);
            if (resourceTypeBeans.isEmpty()) {
                throw new RuntimeException("ClientResourceType " + resourceTypeIdentifier + " not found");
            }

            this.resourceTypes[i] = (ClientResourceType) resourceTypeBeans.iterator().next().getInstance();
        }
    }
}
