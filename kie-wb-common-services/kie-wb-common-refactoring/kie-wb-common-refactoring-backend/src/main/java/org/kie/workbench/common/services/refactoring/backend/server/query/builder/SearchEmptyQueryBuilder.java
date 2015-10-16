/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

import static org.apache.lucene.search.BooleanClause.Occur.*;

public class SearchEmptyQueryBuilder
        implements QueryBuilder<SearchEmptyQueryBuilder> {

    private final BooleanQuery query = new BooleanQuery();
    private boolean useWildcards = false;

    public SearchEmptyQueryBuilder() {
        this(false);
    }

    public SearchEmptyQueryBuilder( final boolean useWildcards ) {
        this.useWildcards = useWildcards;
    }

    public SearchEmptyQueryBuilder useWildcards() {
        this.useWildcards = true;
        return this;
    }

    public Query build() {
        return query;
    }

    public SearchEmptyQueryBuilder addTerm( final ValueIndexTerm term ) {

        if ( term.getValue().trim().isEmpty() ) {

            query.add( new WildcardQuery( new Term( term.getTerm(),
                                                    "*" ) ),
                       MUST_NOT );

        } else if ( term.getValue().trim().equals( "*" ) ) {
            query.add( new MatchAllDocsQuery(),
                       MUST );
        } else {

            if ( !useWildcards ) {
                query.add( new TermQuery( new Term( term.getTerm(),
                                                    term.getValue().toLowerCase() ) ),
                           MUST );

            } else {
                query.add( new WildcardQuery( new Term( term.getTerm(),
                                                        term.getValue().toLowerCase() ) ),
                           MUST );

            }
        }
        return this;
    }

    public SearchEmptyQueryBuilder addRuleNameWildCardTerm() {
        query.add( new WildcardQuery( new Term( RuleIndexTerm.TERM,
                                                "*" ) ),
                   MUST );
        return this;
    }
}
