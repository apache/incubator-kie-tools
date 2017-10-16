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
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;

@Portable
public class ValuePackageNameIndexTerm extends PackageNameIndexTerm implements ValueIndexTerm {

    private String packageName;
    private TermSearchType searchType = TermSearchType.NORMAL;

    public ValuePackageNameIndexTerm() {
        //Errai marshalling
    }

    public ValuePackageNameIndexTerm(final String packageName) {
        this(packageName,
             TermSearchType.NORMAL);
    }

    public ValuePackageNameIndexTerm(final String packageName,
                                     final TermSearchType searchType) {
        this.packageName = PortablePreconditions.checkNotNull("packageName",
                                                              packageName);
        this.searchType = PortablePreconditions.checkNotNull("searchType",
                                                             searchType);
    }

    @Override
    public String getValue() {
        return packageName;
    }

    @Override
    public TermSearchType getSearchType() {
        return this.searchType;
    }
}
