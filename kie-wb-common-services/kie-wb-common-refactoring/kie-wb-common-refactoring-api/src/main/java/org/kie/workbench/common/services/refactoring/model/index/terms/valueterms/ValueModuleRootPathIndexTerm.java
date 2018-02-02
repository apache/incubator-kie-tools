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
import org.kie.workbench.common.services.refactoring.model.index.terms.ModuleRootPathIndexTerm;

@Portable
public class ValueModuleRootPathIndexTerm
        extends ModuleRootPathIndexTerm
        implements ValueIndexTerm {

    private String modulePath;
    private TermSearchType searchType;

    public ValueModuleRootPathIndexTerm() {
        //Errai marshalling
    }

    public ValueModuleRootPathIndexTerm(final String modulePath) {
        this(modulePath,
             TermSearchType.NORMAL);
    }

    public ValueModuleRootPathIndexTerm(final String modulePath,
                                        final TermSearchType searchType) {
        this.modulePath = PortablePreconditions.checkNotNull("modulePath",
                                                             modulePath);
        this.searchType = PortablePreconditions.checkNotNull("searchType",
                                                             searchType);
    }

    @Override
    public String getValue() {
        return modulePath;
    }

    @Override
    public TermSearchType getSearchType() {
        return searchType;
    }
}
