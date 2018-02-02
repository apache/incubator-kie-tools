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
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SearchEmptyQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.RuleNameResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueModuleRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

@ApplicationScoped
public class FindRulesByModuleQuery extends AbstractFindQuery implements NamedQuery {

    public static String NAME = "FindRulesByModuleQuery";

    @Inject
    private RuleNameResponseBuilder responseBuilder;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Query toQuery(final Set<ValueIndexTerm> terms) {

        checkNotNullAndNotEmpty(terms);

        ValuePackageNameIndexTerm packageTerm = null;
        ValueModuleRootPathIndexTerm projectTerm = null;
        ValueResourceIndexTerm ruleTerm = null;
        for (ValueIndexTerm term : terms) {
            if (term instanceof ValuePackageNameIndexTerm) {
                packageTerm = (ValuePackageNameIndexTerm) term;
            } else if (term instanceof ValueModuleRootPathIndexTerm) {
                projectTerm = (ValueModuleRootPathIndexTerm) term;
            } else if (term instanceof ValueResourceIndexTerm) {
                ruleTerm = (ValueResourceIndexTerm) term;
            }
        }

        SearchEmptyQueryBuilder queryBuilder = new SearchEmptyQueryBuilder()
                .addTerm(packageTerm)
                .addTerm(projectTerm);
        if (ruleTerm != null) {
            queryBuilder.addTerm(ruleTerm);
        } else {
            queryBuilder.addRuleNameWildCardTerm();
        }

        return queryBuilder.build();
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

    private static final ValueResourceIndexTerm ruleTerm = new ValueResourceIndexTerm("not-used", ResourceType.RULE);

    /* (non-Javadoc)
     * @see org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery#validateTerms(java.util.Set)
     */
    @Override
    public void validateTerms(Set<ValueIndexTerm> queryTerms) throws IllegalArgumentException {

        checkInvalidAndRequiredTerms(queryTerms, NAME,
                                     new String[]{
                                             ValuePackageNameIndexTerm.TERM,
                                             ValueModuleRootPathIndexTerm.TERM,
                                             null // not required
                                     },
                                     (t) -> (t instanceof ValuePackageNameIndexTerm),
                                     (t) -> (t instanceof ValueModuleRootPathIndexTerm),
                                     (t) -> (t.getTerm().equals(ruleTerm.getTerm()))
        );

        checkTermsSize(2, queryTerms);
    }
}
