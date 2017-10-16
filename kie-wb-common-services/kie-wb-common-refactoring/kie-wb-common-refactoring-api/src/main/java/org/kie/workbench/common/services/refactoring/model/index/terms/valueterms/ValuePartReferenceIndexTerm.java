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
package org.kie.workbench.common.services.refactoring.model.index.terms.valueterms;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.model.index.terms.ReferencedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;

/**
 * This index term captures information about a part referenced in a resource, specifically:
 * <ul>
 * <li>the <em>type</em> of part</li>
 * <li>the signature or "fully-qualified" name of the part</li>
 * <li>the resource of the part: the original resource the part is defined, in other words</li>
 * </ul>
 */
@Portable
public class ValuePartReferenceIndexTerm extends ReferencedPartIndexTerm implements ValueIndexTerm {

    private String partName;
    private TermSearchType termType;

    public ValuePartReferenceIndexTerm() {
        //Errai marshalling
    }

    /**
     * Constructor for (resource) part references
     *
     * @param partSignatureName The part (field/function/ruleflow-group/etc.) name
     * @param partType          The part type
     * @param resourceFQN       The resource of which the part is a part
     */
    public ValuePartReferenceIndexTerm(final String resourceFQN,
                                       final String partSignatureName,
                                       final PartType partType) {
        this(resourceFQN,
             partSignatureName,
             partType,
             TermSearchType.NORMAL);
    }

    /**
     * Constructor for (resource) part references
     *
     * @param partSignatureName The part (field/function/ruleflow-group/etc.) name
     * @param partType          The part type
     * @param resourceFQN       The resource of which the part is a part
     * @param termType          The type of extended (term) functionality used in the term
     */
    public ValuePartReferenceIndexTerm(final String resourceFQN,
                                       final String partSignatureName,
                                       final PartType partType,
                                       final TermSearchType termType) {
        super(partType,
              resourceFQN);
        this.partName = PortablePreconditions.checkNotNull("partName",
                                                           partSignatureName);
        this.termType = PortablePreconditions.checkNotNull("termType",
                                                           termType);
    }

    @Override
    public String getValue() {
        return partName;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ExtendedValueIndexTerm#getTermType()
     */
    @Override
    public TermSearchType getSearchType() {
        return this.termType;
    }
}
