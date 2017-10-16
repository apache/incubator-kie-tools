/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.ext.metadata.model.KProperty;

/**
 * This object is a DTO to represent a "shared" part reference in a resource (a DRL rule definition or a BPMN2 process definition).
 * </p>
 * Unlike Java, there are parts of a DRL (or BPMN2, I think?) that are not intrinsically "part" of the DRL definition, but still defined
 * the DRL. For example, a "ruleflow-group" attribute can be defined in any number of rule definitions, but unlike a Java Field, the
 * "ruleflow-group" attribute is not an intrinsic part of the DRL definition.
 * </p>
 * However, users will want to be able to refactor these types of parts (other examples are agenda-groups, activation-groups and entry-points)
 * so that these "references" must be stored. I'm writing "references"  with quotes, because the use of such a part is not a reference
 * to a particular resource (rule) definition that defines this part, but a shared "reference" to a particular part.
 */
public class SharedPart implements IndexElementsGenerator {

    private final String partName;
    private PartType partType;

    public SharedPart(final String partName,
                      PartType partType) {
        this.partName = PortablePreconditions.checkNotNull("partName",
                                                           partName);
        this.partType = PortablePreconditions.checkNotNull("partType",
                                                           partType);
    }

    @Override
    public List<KProperty<?>> toIndexElements() {
        final List<KProperty<?>> indexElements = new ArrayList<>();

        // Impact Analysis reference
        ValueSharedPartIndexTerm sharedPartTerm = new ValueSharedPartIndexTerm(this.partName,
                                                                               this.partType);
        indexElements.add(new KPropertyImpl<>(sharedPartTerm.getTerm(),
                                              sharedPartTerm.getValue()));

        return indexElements;
    }

    @Override
    public String toString() {
        return "shared:" + partType.toString() + " => " + partName;
    }
}
