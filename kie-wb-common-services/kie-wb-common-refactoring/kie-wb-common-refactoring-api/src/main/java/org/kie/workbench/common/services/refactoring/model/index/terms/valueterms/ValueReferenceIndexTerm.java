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
import org.kie.workbench.common.services.refactoring.model.index.terms.ReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

@Portable
public class ValueReferenceIndexTerm extends ReferenceIndexTerm implements ValueIndexTerm {

    private String resourceName;
    private TermSearchType termType;

    public ValueReferenceIndexTerm() {
        //Errai marshalling
    }

    /**
     * @param resourceFQN  The fully-qualified resource name
     * @param resourceType The resource type
     */
    public ValueReferenceIndexTerm(final String resourceFQN,
                                   final ResourceType resourceType) {
        this(resourceFQN,
             resourceType,
             TermSearchType.NORMAL);
    }

    /**
     * @param resourceFQN  The fully-qualified resource name
     * @param resourceType The resource type
     * @param termType     The type of extended (term) functionality used in the term
     */
    public ValueReferenceIndexTerm(final String resourceFQN,
                                   final ResourceType resourceType,
                                   final TermSearchType termType) {
        super(resourceType);
        this.resourceName = PortablePreconditions.checkNotNull("resourceName",
                                                               resourceFQN);
        this.termType = PortablePreconditions.checkNotNull("termType",
                                                           termType);
    }

    @Override
    public String getValue() {
        return resourceName;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ExtendedValueIndexTerm#getTermType()
     */
    @Override
    public TermSearchType getSearchType() {
        return this.termType;
    }
}
