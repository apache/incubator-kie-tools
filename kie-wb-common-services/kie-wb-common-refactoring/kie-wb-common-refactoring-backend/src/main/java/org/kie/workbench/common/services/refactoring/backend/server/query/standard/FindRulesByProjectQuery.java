/*
 * Copyright 2014 JBoss, by Red Hat, Inc
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

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.lucene.search.Query;
import org.drools.workbench.models.datamodel.util.PortablePreconditions;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SearchEmptyQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.NormalizedTerms;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.RuleNameResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

@ApplicationScoped
public class FindRulesByProjectQuery
        implements NamedQuery {

    public static String FIND_RULES_BY_PROJECT_QUERY = "FindRulesByProjectQuery";

    @Inject
    private RuleNameResponseBuilder responseBuilder;

    @Override
    public String getName() {
        return FIND_RULES_BY_PROJECT_QUERY;
    }

    @Override
    public Set<IndexTerm> getTerms() {
        return new HashSet<IndexTerm>() {{
            add( new PackageNameIndexTerm() );
            add( new ProjectRootPathIndexTerm() );
        }};
    }

    @Override
    public Query toQuery( final Set<ValueIndexTerm> terms,
                          final boolean useWildcards ) {
        PortablePreconditions.checkNotNull( "terms",
                                            terms );

        NormalizedTerms normalizedTerms = new NormalizedTerms( terms,
                                                               PackageNameIndexTerm.TERM,
                                                               ProjectRootPathIndexTerm.TERM );

        SearchEmptyQueryBuilder queryBuilder = new SearchEmptyQueryBuilder( useWildcards );
        queryBuilder
                .addTerm( normalizedTerms.get( PackageNameIndexTerm.TERM ) )
                .addTerm( normalizedTerms.get( ProjectRootPathIndexTerm.TERM ) )
                .addRuleNameWildCardTerm();

        return queryBuilder.build();
    }

    @Override
    public ResponseBuilder getResponseBuilder() {
        return responseBuilder;
    }

}
