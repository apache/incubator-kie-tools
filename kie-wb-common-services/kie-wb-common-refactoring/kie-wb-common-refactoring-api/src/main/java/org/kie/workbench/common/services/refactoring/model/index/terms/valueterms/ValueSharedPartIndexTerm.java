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
import org.kie.workbench.common.services.refactoring.model.index.terms.SharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;

/**
 * This index term captures information about a part referenced in a resource, specifically:
 * <ul>
 * <li>the <em>type</em> of part</li>
 * <li>the "fully-qualified" name of the part (most names are already fully-qualified for shared parts)</li>
 * </ul>
 */
@Portable
public class ValueSharedPartIndexTerm extends SharedPartIndexTerm implements ValueIndexTerm {

    private String partName;
    private TermSearchType termType;

    public ValueSharedPartIndexTerm() {
        //Errai marshalling
    }

    /**
     * @param partName The part (ruleflow-group/agenda-group/etc.) name
     * @param partType The part type
     */
    public ValueSharedPartIndexTerm(final String partName,
                                    final PartType partType) {
        super(partType);
        this.partName = PortablePreconditions.checkNotNull("partName",
                                                           partName);
        this.termType = TermSearchType.NORMAL;
    }

    /**
     * @param partName The part (ruleflow-group/agenda-group/etc.) name
     * @param partType The part type
     * @param termType The type of extended (term) functionality used in the term
     */
    public ValueSharedPartIndexTerm(final String partName,
                                    final PartType partType,
                                    final TermSearchType termType) {
        super(partType);
        this.partName = PortablePreconditions.checkNotNull("partName",
                                                           partName);
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
