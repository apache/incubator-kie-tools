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

package org.uberfire.ext.metadata.backend.infinispan.ickl;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class IcklConverterTest {

    private IckleConverter ickleConverter;

    @Before
    public void setUp() {
        this.ickleConverter = new IckleConverter();
    }

    @Test
    public void testTermQueryConversion() {

        Query query = new TermQuery(new Term("a.term",
                                             "theResult"));

        String queryString = this.ickleConverter.convert(query);
        assertThat(queryString).isEqualTo("a__term:'theResult'");
    }

    @Test
    public void testWildcardQueryConversion() {

        Query query = new WildcardQuery(new Term("a.term",
                                                 "123*"));

        String queryString = this.ickleConverter.convert(query);
        assertThat(queryString).isEqualTo("a__term:'123*'");
    }

    @Test
    public void testMatchAllDocsQueryQueryConversion() {

        MatchAllDocsQuery query = new MatchAllDocsQuery();

        String queryString = this.ickleConverter.convert(query);
        assertThat(queryString).isEqualTo("");
    }

    @Test
    public void testWhereClause() {

        {
            Query query = new WildcardQuery(new Term("a.term",
                                                     "123*"));

            String queryString = this.ickleConverter.where(query);
            assertThat(queryString).isEqualTo("where a__term:'123*'");
        }

        {
            MatchAllDocsQuery query = new MatchAllDocsQuery();

            String queryString = this.ickleConverter.where(query);
            assertThat(queryString).isEqualTo("");
        }
    }

    @Test
    public void testSortConvert() {
        {
            Sort sort = new Sort();
            String sortString = this.ickleConverter.convert(sort);
            assertThat(sortString).isEqualTo("");
        }

        {
            Sort sort = new Sort(new SortField("a.sort.field",
                                               SortField.Type.STRING));

            String sortString = this.ickleConverter.convert(sort);
            assertThat(sortString).isEqualTo("a__sort__field ASC");
        }

        {
            Sort sort = new Sort(new SortField("a.sort.field",
                                               SortField.Type.STRING,
                                               true));

            String sortString = this.ickleConverter.convert(sort);
            assertThat(sortString).isEqualTo("a__sort__field DESC");
        }

        {
            Sort sort = new Sort(new SortField("a.sort.field",
                                               SortField.Type.STRING,
                                               true),
                                 new SortField("another.sort.field",
                                               SortField.Type.STRING));

            String sortString = this.ickleConverter.convert(sort);
            assertThat(sortString).isEqualTo("a__sort__field DESC,another__sort__field ASC");
        }
    }

    @Test
    public void testSort() {
        {
            Sort sort = new Sort();
            String sortString = this.ickleConverter.sort(sort);
            assertThat(sortString).isEqualTo("");
        }

        {
            Sort sort = new Sort(new SortField("a.sort.field",
                                               SortField.Type.STRING,
                                               true),
                                 new SortField("another.sort.field",
                                               SortField.Type.STRING));

            String sortString = this.ickleConverter.sort(sort);
            assertThat(sortString).isEqualTo("order by a__sort__field DESC,another__sort__field ASC");
        }
    }

    @Test
    public void testRegexpQuery() {
        Query query = new RegexpQuery(new Term("libraryFileName", ".*(cmmn|bpmn|bpmn2|bpmn-cm)"));

        String queryString = this.ickleConverter.convert(query);

        assertThat(queryString).isEqualTo("libraryFileName:/.*(cmmn|bpmn|bpmn2|bpmn-cm)/");
    }
}
