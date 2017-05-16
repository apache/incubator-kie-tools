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
package org.kie.workbench.common.services.refactoring.backend.server.impact;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.kie.workbench.common.services.refactoring.backend.server.indexing.AbstractFileIndexer;
import org.kie.workbench.common.services.refactoring.ResourceReference;
import org.kie.workbench.common.services.refactoring.Resource;
import org.kie.workbench.common.services.refactoring.SharedPart;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class should be extended by all "Visitor" implementations in different editor backends. These Visitor implementations
 * are responsible for collecting reference part information about the assets that they modify.
 * </p>
 * Subsequently, the Visitor implementation instance (and extended {@link ResourceReferenceCollector} instance) is passed to the
 * protected {@link AbstractFileIndexer} addReferencedResourcesToIndexBuilder(..) method, so that the collected reference information
 * can be added to the index, to be used during change impact and refactoring.
 *
 * </p>
 */
public abstract class ResourceReferenceCollector {

    private static final Logger logger = LoggerFactory.getLogger(ResourceReferenceCollector.class);

    private Map<String, Resource> resourcesMap;
    private Map<String, ResourceReference> referencedResourcesMap;
    private Set<SharedPart> sharedPartReferences;

    /**
     * Used by the {@link AbstractFileIndexer} implementation to get the reference information.
     * @return A {@link Collection} of {@link ResourceReference} instances.
     */
    public Collection<ResourceReference> getResourceReferences() {
        if( referencedResourcesMap != null ) {
            return referencedResourcesMap.values();
        }
        return Collections.emptyList();
    }

    /**
     * Used by the {@link AbstractFileIndexer} implementation to get the shared reference information.
     * @return A {@link Collection} of {@link ResourceReference} instances.
     */
    public Collection<SharedPart> getSharedReferences() {
        if( sharedPartReferences != null ) {
            return sharedPartReferences;
        }
        return Collections.emptyList();
    }


    /**
     * Used by the {@link AbstractFileIndexer} implementation to get the shared reference information.
     * @return A {@link Collection} of {@link ResourcePartReference} instances.
     */
    public Collection<Resource> getResources() {
        if( resourcesMap != null ) {
            return resourcesMap.values();
        }
        return Collections.emptyList();
    }


    private Map<String, ResourceReference> internalGetResourceReferences() {
        if( referencedResourcesMap == null ) {
            referencedResourcesMap = new HashMap<>(4);
        }
        return referencedResourcesMap;
    }

    /**
     * Used by the Visitor implementation to add the FQN and type of a reference.
     * @param fullyQualifiedName The FQN of the reference
     * @param type The type of the reference part.
     * @return The {@link ResourceReference} being added, so that more part references can be added.
     */
    public ResourceReference addResourceReference(String fullyQualifiedName, ResourceType type) {
        ResourceReference resRef = internalGetResourceReferences().get(fullyQualifiedName);
        if( resRef == null ) {
            resRef = new ResourceReference(fullyQualifiedName, type);
            referencedResourcesMap.put(fullyQualifiedName, resRef);
        } else if( type != null && ! type.equals(resRef.getResourceType()) ) {
            String msg = "Existing resource reference found with type " + resRef.getResourceType().toString() + " (tried to add resrouce reference of type " + type.toString() + ")";
            logger.error(msg);
        }

        return resRef;
    }

    /**
     * Used by the Visitor implemenation to add multiple {@link ResourceReference} instances at once.
     * @param collector A {@link ResourceReferenceCollector} containing information about the asset being indexed.
     */
    public void addResourceReferences(ResourceReferenceCollector collector) {
        if( collector.referencedResourcesMap != null ) {
            Map<String, ResourceReference> refResMap = internalGetResourceReferences();
            for( Entry<String, ResourceReference> resRefEntry : collector.referencedResourcesMap.entrySet() ) {
                ResourceReference newResRef = resRefEntry.getValue();
                ResourceReference existingResRef = refResMap.put(resRefEntry.getKey(), newResRef);
                if( existingResRef != null ) {
                    newResRef.addPartReference(existingResRef.getPartReferences());
                }
            }
        }
    }

    /**
     * Used by the Visitor implementation to add the name and type of a shared reference.
     * @param partName The name of the shared reference
     * @param partType The type of the shared reference.
     */
    public void addSharedReference(String partName, PartType partType) {
        if( sharedPartReferences == null ) {
            sharedPartReferences = new HashSet<>();
        }
        sharedPartReferences.add(new SharedPart(partName, partType));
    }

    public Resource addResource(String resourceFQN, ResourceType type) {
        if( resourcesMap == null ) {
            resourcesMap = new HashMap<>(4);
        }
        Resource resRef = resourcesMap.get(resourceFQN);
        if( resRef == null ) {
            resRef = new Resource(resourceFQN, type);
            resourcesMap.put(resourceFQN, resRef);
        } else if( type != null && ! type.equals(resRef.getResourceType()) ) {
            String msg = "Existing resource found with type " + resRef.getResourceType().toString() + " (tried to add resource reference of type " + type.toString() + ")";
            logger.error(msg);
        }

        return resRef;
    }
}
