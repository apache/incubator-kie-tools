/*
 * Copyright 2014 JBoss Inc
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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.paging.PageRequest;

/**
 * A Query request.
 */
@Portable
public class RefactoringPageRequest extends PageRequest {

    private String queryName;
    private Set<ValueIndexTerm> queryTerms;
    private boolean useWildcards = false;

    public RefactoringPageRequest() {
        //Errai marshalling
    }

    public RefactoringPageRequest( final String queryName,
                                   final Set<ValueIndexTerm> queryTerms,
                                   final int startRowIndex,
                                   final Integer pageSize ) {
        this( queryName,
              queryTerms,
              false,
              startRowIndex,
              pageSize );
    }

    public RefactoringPageRequest( final String queryName,
                                   final Set<ValueIndexTerm> queryTerms,
                                   final boolean useWildcards,
                                   final int startRowIndex,
                                   final Integer pageSize ) {
        super( startRowIndex,
               pageSize );
        this.queryName = PortablePreconditions.checkNotNull( "queryName",
                                                             queryName );
        this.queryTerms = PortablePreconditions.checkNotNull( "queryTerms",
                                                              queryTerms );
        this.useWildcards = useWildcards;
    }

    public String getQueryName() {
        return queryName;
    }

    public Set<ValueIndexTerm> getQueryTerms() {
        return queryTerms;
    }

    public boolean useWildcards() {
        return useWildcards;
    }

}
