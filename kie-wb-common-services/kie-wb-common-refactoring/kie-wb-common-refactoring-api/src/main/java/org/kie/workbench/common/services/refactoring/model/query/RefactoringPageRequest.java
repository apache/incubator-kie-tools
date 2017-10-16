/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kie.workbench.common.services.refactoring.model.query;

import java.util.Set;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.uberfire.paging.PageRequest;

/**
 * A Query request.
 */
@Portable
public class RefactoringPageRequest extends PageRequest {

    private String queryName;
    private Set<ValueIndexTerm> queryTerms;

    public RefactoringPageRequest(@MapsTo("queryName") final String queryName,
                                  @MapsTo("queryTerms") final Set<ValueIndexTerm> queryTerms,
                                  @MapsTo("startRowIndex") final int startRowIndex,
                                  @MapsTo("pageSize") final Integer pageSize) {
        super(startRowIndex,
              pageSize);
        this.queryName = PortablePreconditions.checkNotNull("queryName",
                                                            queryName);
        this.queryTerms = PortablePreconditions.checkNotNull("queryTerms",
                                                             queryTerms);
    }

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public Set<ValueIndexTerm> getQueryTerms() {
        return queryTerms;
    }

    public void setQueryTerms(Set<ValueIndexTerm> queryTerms) {
        this.queryTerms = queryTerms;
    }
}
