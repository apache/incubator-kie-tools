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
package org.kie.workbench.common.services.refactoring.model.index.terms;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.service.PartType;

/**
 * For fields that describe parts (of other resources) referred to in the resource being indexed.
 */
@Portable
public class ReferencedPartIndexTerm implements CompositeIndexTerm {

    private final static String TERM_PREFIX = "ref:";

    private PartType partType;
    private String resourceName;

    public ReferencedPartIndexTerm() {
        // default constructor (@Portable)
    }

    public ReferencedPartIndexTerm(PartType type,
                                   String resourceName) {
        this.partType = PortablePreconditions.checkNotNull("partType",
                                                           type);
        this.resourceName = PortablePreconditions.checkNotNull("resourceName",
                                                               resourceName);
    }

    @Override
    public String getTerm() {
        return TERM_PREFIX + partType.toString() + ":" + resourceName;
    }

    @Override
    public String getTermBase() {
        return TERM_PREFIX + partType.toString();
    }
}
