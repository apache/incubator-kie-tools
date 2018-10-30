/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.uberfire.ext.metadata.backend.infinispan.ickl.converters;

import java.util.Iterator;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.uberfire.ext.metadata.backend.infinispan.ickl.IckleConverter;

public class BooleanQueryConverter implements QueryConverter {

    private BooleanQuery booleanQuery;
    private IckleConverter icklConverter;

    public BooleanQueryConverter(BooleanQuery booleanQuery,
                                 IckleConverter icklConverter) {
        this.booleanQuery = booleanQuery;
        this.icklConverter = icklConverter;
    }

    @Override
    public String convert() {
        Iterator<BooleanClause> clauses = this.booleanQuery.clauses().iterator();

        StringBuilder queryString = new StringBuilder();

        while (clauses.hasNext()) {
            BooleanClause clause = clauses.next();

            String queryResult = icklConverter.convert(clause.getQuery());

            if (BooleanQuery.class.isAssignableFrom(clause.getQuery().getClass())) {
                queryResult = "(" + queryResult + ")";
            }

            if (clause.isProhibited()) {
                queryResult = "NOT " + queryResult;
            }

            if (clauses.hasNext()) {
                String operator = this.getBooleanOperator(clause);
                queryResult = queryResult + " " + operator + " ";
            }

            queryString.append(queryResult);
        }

        return queryString.toString().trim();
    }

    private String getBooleanOperator(BooleanClause booleanClause) {
        return booleanClause.isRequired() ? "AND" : "OR";
    }
}