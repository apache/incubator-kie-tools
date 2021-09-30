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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.uberfire.client.workbench.type.ClientResourceType;
import org.uberfire.workbench.category.Category;
import org.uberfire.workbench.type.ResourceTypeDefinition;

import static java.util.Collections.sort;

@ApplicationScoped
public class ResourceTypeManagerCache {

    private List<ActivityAndMetaInfo> resourceActivities = new ArrayList<>();
    private CategoriesManagerCache categoriesManagerCache;

    @Inject
    public ResourceTypeManagerCache(CategoriesManagerCache categoriesManagerCache) {
        this.categoriesManagerCache = categoriesManagerCache;
    }

    public void addAll(List<ClientResourceType> resourceTypeDefinitions) {
        this.categoriesManagerCache.addAllFromResourceTypes(resourceTypeDefinitions);
    }

    public Set<ResourceTypeDefinition> getResourceTypeDefinitions() {
        return this.resourceActivities.stream()
                .map(activityAndMetaInfo -> this.getResourceTypes(activityAndMetaInfo))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    public List<ActivityAndMetaInfo> getResourceActivities() {
        return resourceActivities;
    }

    public void addResourceActivity(ActivityAndMetaInfo activityAndMetaInfo) {
        getResourceActivities().add(activityAndMetaInfo);
        List<ClientResourceType> resourceTypes = getResourceTypes(activityAndMetaInfo);
        this.addAll(resourceTypes);
    }

    private List<ClientResourceType> getResourceTypes(ActivityAndMetaInfo activityAndMetaInfo) {
        return Arrays.stream(activityAndMetaInfo.getResourceTypes()).collect(Collectors.toList());
    }

    public List<ResourceTypeDefinition> getResourceTypeDefinitionsByCategory(Category category) {
        return this.getResourceTypeDefinitions()
                .stream()
                .filter(resourceTypeDefinition -> {
                    if (category != null) {
                        return category.equals(resourceTypeDefinition.getCategory());
                    } else {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public void sortResourceActivitiesByPriority() {
        sort(resourceActivities,
             (o1, o2) -> {
                 if (o1.getPriority() < o2.getPriority()) {
                     return 1;
                 } else if (o1.getPriority() > o2.getPriority()) {
                     return -1;
                 } else {
                     return 0;
                 }
             });
    }
}
