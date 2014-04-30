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
package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;

/**
 * Simple query builder that supports AND between terms
 */
public class QueryBuilder {

    private boolean useWildcards = false;
    private final List<ValueIndexTerm> terms = new ArrayList<ValueIndexTerm>();

    public QueryBuilder addTerm( final ValueIndexTerm term ) {
        terms.add( term );
        return this;
    }

    public QueryBuilder useWildcards() {
        this.useWildcards = true;
        return this;
    }

    public Query build() {
        if ( useWildcards ) {
            return buildWildcardQuery();
        } else {
            return buildRegularQuery();
        }
    }

    private Query buildWildcardQuery() {
        final String field = buildField();
        final String text = buildText();
        final WildcardQuery query = new WildcardQuery( new Term( field,
                                                                 text ) );
        return query;
    }

    private Query buildRegularQuery() {
        final BooleanQuery query = new BooleanQuery();
        final String field = buildField();
        final String text = buildText();
        query.add( new TermQuery( new Term( field,
                                            text ) ),
                   BooleanClause.Occur.MUST );
        return query;
    }

    private String buildField() {
        final StringBuilder sb = new StringBuilder();
        for ( int i = 0; i < terms.size() - 1; i++ ) {
            final ValueIndexTerm term = terms.get( i );
            sb.append( term.getTerm() ).append( ":" ).append( term.getValue() ).append( ":" );
        }
        final IndexTerm term = terms.get( terms.size() - 1 );
        sb.append( term.getTerm() );
        return sb.toString();
    }

    private String buildText() {
        final ValueIndexTerm term = terms.get( terms.size() - 1 );
        return term.getValue().toLowerCase();
    }

}
