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
package org.kie.workbench.common.services.refactoring.backend.server.query.standard;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartIndexTerm;

@ApplicationScoped
public class FindResourcePartsQuery extends AbstractFindQuery implements NamedQuery {

    public static String NAME = FindResourcePartsQuery.class.getSimpleName();

    @Inject
    private DefaultResponseBuilder responseBuilder;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms ) {

        checkNotNullAndNotEmpty(terms);

        return buildFromSingleTerm(terms.iterator().next());
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery#validateTerms(java.util.Set)
     */
    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {

        checkInvalidAndRequiredTerms(queryTerms, NAME,
                new String [] { ValuePartIndexTerm.class.getSimpleName() },
                (t) -> (t instanceof ValuePartIndexTerm));

        checkTermsSize(1, queryTerms);
    }

}
