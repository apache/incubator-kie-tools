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

import java.util.Arrays;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.uberfire.ext.metadata.backend.infinispan.ickl.FieldConverterImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(Parameterized.class)
public class TermQueryConverterTest {

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"aString", "a__term:'aString'"},
                {"123", "a__term:123"}, // Int
                {"0.3", "a__term:0.3"}, // Float
                {"true", "a__term:true"} // Boolean
        });
    }

    @Parameterized.Parameter()
    public String termText;
    @Parameterized.Parameter(1)
    public String expectedConversionResult;

    @Test
    public void test() {
        TermQuery query = new TermQuery(new Term("a.term",
                                                 termText));
        QueryConverter converter = new TermQueryConverter(query,
                                                          new FieldConverterImpl());
        String queryString = converter.convert();
        assertThat(queryString).isEqualTo(expectedConversionResult);
    }
}