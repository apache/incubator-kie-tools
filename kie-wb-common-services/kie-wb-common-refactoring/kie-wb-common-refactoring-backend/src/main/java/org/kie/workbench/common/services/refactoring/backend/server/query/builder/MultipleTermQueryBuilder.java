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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.drools.AbstractDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple query builder that supports AND between terms
 */
public class MultipleTermQueryBuilder extends AbstractQueryBuilder implements QueryBuilder<MultipleTermQueryBuilder> {

    private final List<ValueIndexTerm> terms = new ArrayList<ValueIndexTerm>();

    public MultipleTermQueryBuilder() {
    }

    public MultipleTermQueryBuilder addTerm( final ValueIndexTerm term ) {
        terms.add( term );
        return this;
    }

    public Query build() {
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        for( ValueIndexTerm valueTerm : terms ) {
            Query termQuery = getQuery(valueTerm);
            queryBuilder.add(termQuery, Occur.MUST);
        }

        BooleanQuery query = queryBuilder.build();
        return query;
    }



}
