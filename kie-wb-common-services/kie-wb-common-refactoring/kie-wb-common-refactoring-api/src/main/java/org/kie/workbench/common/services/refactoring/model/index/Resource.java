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
package org.kie.workbench.common.services.refactoring.model.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.commons.data.Pair;

public class Resource implements IndexElementsGenerator {

    private final String resourceFQN;
    private final ResourceType resourceType;

    private final Map<String, PartType> parts;

    public Resource(String resourceFQN, ResourceType resourceType ) {
        this.resourceFQN = resourceFQN;
        this.resourceType = resourceType;
        this.parts = new HashMap<String, PartType>();
    }

    public void addPart( String uniquePartName, PartType partType ) {
        PartType previousPartType = parts.put(uniquePartName, partType);
        if( previousPartType != null ) {
            throw new IllegalArgumentException(partType.toString() + " " + uniquePartName + " has already been added!");
        }
    }

    public String getResourceFQN() {
        return this.resourceFQN;
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    @Override
    public List<Pair<String, String>> toIndexElements() {
        final List<Pair<String, String>> indexElements = new ArrayList<Pair<String, String>>();

        ValueResourceIndexTerm resTerm = new ValueResourceIndexTerm(resourceFQN, resourceType);
        indexElements.add(new Pair<String, String>(resTerm.getTerm(), resTerm.getValue()));

        for( Entry<String, PartType> part : parts.entrySet() ) {
            ValuePartIndexTerm partTerm = new ValuePartIndexTerm(part.getKey(), part.getValue());
            indexElements.add(new Pair<String, String>(partTerm.getTerm(), partTerm.getValue()));
        }

        return indexElements;
    }

    @Override
    public String toString() {
        return resourceType.toString() + " => " + resourceFQN;
    }

}
