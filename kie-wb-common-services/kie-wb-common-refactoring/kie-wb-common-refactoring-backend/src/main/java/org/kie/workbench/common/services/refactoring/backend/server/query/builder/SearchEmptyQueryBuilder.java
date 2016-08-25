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
package org.kie.workbench.common.services.refactoring.backend.server.query.builder;

import static org.apache.lucene.search.BooleanClause.Occur.MUST;
import static org.apache.lucene.search.BooleanClause.Occur.MUST_NOT;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;

public class SearchEmptyQueryBuilder extends AbstractQueryBuilder
        implements QueryBuilder<SearchEmptyQueryBuilder> {

    private final BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();

    public Query build() {
        Query query = queryBuilder.build();
        return query;
    }

    public SearchEmptyQueryBuilder addTerm( final ValueIndexTerm term ) {
        if ( term.getValue().trim().isEmpty() ) {
            queryBuilder.add( new WildcardQuery( new Term( term.getTerm(), "*" ) ), MUST_NOT );
        } else if ( term.getValue().trim().equals( "*" ) ) {
            queryBuilder.add( new MatchAllDocsQuery(), MUST );
        } else {
            Query query = getQuery(term);
            queryBuilder.add( query, MUST );
        }
        return this;
    }

    public SearchEmptyQueryBuilder addRuleNameWildCardTerm() {
        ValueIndexTerm valTerm = new ValueResourceIndexTerm( "*", ResourceType.RULE, TermSearchType.WILDCARD );
        queryBuilder.add( new WildcardQuery( new Term(valTerm.getTerm(), valTerm.getValue()) ), MUST );
        return this;
    }
}
