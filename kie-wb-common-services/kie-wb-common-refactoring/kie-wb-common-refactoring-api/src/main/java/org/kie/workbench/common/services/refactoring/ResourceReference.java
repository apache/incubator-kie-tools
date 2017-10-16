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
package org.kie.workbench.common.services.refactoring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.model.KProperty;

public class ResourceReference implements IndexElementsGenerator {

    private final String resourceFQN;
    private final ResourceType resourceType; // this might be (initially?) unknown
    private Map<String, ValuePartReferenceIndexTerm> fieldNamepartReferenceMap;

    public ResourceReference(final String resourceFQN,
                             ResourceType resoureType) {
        this.resourceFQN = PortablePreconditions.checkNotNull("resourceFQN",
                                                              resourceFQN);
        this.resourceType = PortablePreconditions.checkNotNull("resourceType",
                                                               resoureType);
    }

    public ResourceType getResourceType() {
        return this.resourceType;
    }

    public void addPartReference(String partName,
                                 PartType partType) {
        if (fieldNamepartReferenceMap == null) {
            this.fieldNamepartReferenceMap = new HashMap<String, ValuePartReferenceIndexTerm>(4);
        }
        fieldNamepartReferenceMap.put(partName,
                                      new ValuePartReferenceIndexTerm(resourceFQN,
                                                                      partName,
                                                                      partType));
    }

    public void addPartReference(Map<String, ValuePartReferenceIndexTerm> partReferences) {
        if (!(partReferences == null || partReferences.isEmpty())) {
            if (fieldNamepartReferenceMap == null) {
                this.fieldNamepartReferenceMap = new HashMap<>(4);
            }
            fieldNamepartReferenceMap.putAll(partReferences);
        }
    }

    public Map<String, ValuePartReferenceIndexTerm> getPartReferences() {
        if (fieldNamepartReferenceMap == null) {
            return Collections.emptyMap();
        }
        return fieldNamepartReferenceMap;
    }

    @Override
    public List<KProperty<?>> toIndexElements() {
        final List<KProperty<?>> indexElements = new ArrayList<>();

        // Impact Analysis references
        if (resourceFQN != null) {
            ValueReferenceIndexTerm refTerm = new ValueReferenceIndexTerm(this.resourceFQN,
                                                                          this.resourceType);
            indexElements.add(new KPropertyImpl<>(refTerm.getTerm(),
                                                  refTerm.getValue()));
        }

        if (this.fieldNamepartReferenceMap != null) {
            fieldNamepartReferenceMap.values().forEach((refPartTerm) -> indexElements.add(new KPropertyImpl<>(refPartTerm.getTerm(),
                                                                                                              refPartTerm.getValue())));
        }

        return indexElements;
    }

    @Override
    public String toString() {
        return "ref:" + resourceType.toString() + " => " + resourceFQN;
    }
}
