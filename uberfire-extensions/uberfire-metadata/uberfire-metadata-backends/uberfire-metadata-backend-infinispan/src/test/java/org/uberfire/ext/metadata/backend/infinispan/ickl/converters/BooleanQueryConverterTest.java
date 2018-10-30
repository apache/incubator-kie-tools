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

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.infinispan.ickl.IckleConverter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class BooleanQueryConverterTest {

    @Test
    public void testSingleBooleanTermQuery() {

        TermQuery query = new TermQuery(new Term("a.term",
                                                 "theResult"));

        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(query,
                     BooleanClause.Occur.MUST)
                .build();

        BooleanQueryConverter converter = new BooleanQueryConverter(booleanQuery,
                                                                    new IckleConverter());

        String queryString = converter.convert();

        assertThat(queryString).isEqualTo("a__term:'theResult'");
    }

    @Test
    public void testSingleClauseBooleanTermQuery() {

        TermQuery query = new TermQuery(new Term("a.term",
                                                 "theResult"));

        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(query,
                     BooleanClause.Occur.MUST)
                .build();

        BooleanQueryConverter converter = new BooleanQueryConverter(booleanQuery,
                                                                    new IckleConverter());

        String queryString = converter.convert();

        assertThat(queryString).isEqualTo("a__term:'theResult'");
    }

    @Test
    public void testNotBooleanQuery() {

        TermQuery query = new TermQuery(new Term("a.term",
                                                 "theResult"));

        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(query,
                     BooleanClause.Occur.MUST_NOT)
                .build();

        BooleanQueryConverter converter = new BooleanQueryConverter(booleanQuery,
                                                                    new IckleConverter());

        String queryString = converter.convert();

        assertThat(queryString).isEqualTo("NOT a__term:'theResult'");
    }

    @Test
    public void testMultipleBooleanTermQuery() {

        TermQuery query = new TermQuery(new Term("a.term",
                                                 "theResult"));

        TermQuery query2 = new TermQuery(new Term("a.term.other",
                                                  "123"));

        BooleanQuery orBooleanQuery = new BooleanQuery.Builder()
                .add(query,
                     BooleanClause.Occur.SHOULD)
                .add(query2,
                     BooleanClause.Occur.MUST_NOT).build();

        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(query,
                     BooleanClause.Occur.MUST)
                .add(query2,
                     BooleanClause.Occur.MUST)
                .add(orBooleanQuery,
                     BooleanClause.Occur.MUST)
                .build();

        BooleanQueryConverter converter = new BooleanQueryConverter(booleanQuery,
                                                                    new IckleConverter());

        String queryString = converter.convert();

        assertThat(queryString).isEqualTo("a__term:'theResult' AND a__term__other:123 AND (a__term:'theResult' OR NOT a__term__other:123)");
    }
}