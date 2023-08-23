/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.webapp.kogito.common.client.services;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FEELListParserTest {

    @Parameterized.Parameters(name = "{index}: input={0}, expected={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, new String[]{}},
                {"", new String[]{}},
                {"one", new String[]{"one"}},
                {"one,two", new String[]{"one", "two"}},
                {"\"one,two\"", new String[]{"\"one,two\""}},
                {"one ,two", new String[]{"one", "two"}},
                {"one, two", new String[]{"one", "two"}},
                {"\"one\",two", new String[]{"\"one\"", "two"}},
                {"\"one\",\"two\"", new String[]{"\"one\"", "\"two\""}},
                {"\"\"one\"\",\"\"two\"\"", new String[]{"\"\"one\"\"", "\"\"two\"\""}},
                {"\"one,two\",\"three\"", new String[]{"\"one,two\"", "\"three\""}},
                {"\"one,two\",three", new String[]{"\"one,two\"", "three"}},
                {"\"one\", \"two\", \"three\"", new String[]{"\"one\"", "\"two\"", "\"three\""}},
                {"one, \"two\", three", new String[]{"one", "\"two\"", "three"}},
                {"\"one\", two, \"three\"", new String[]{"\"one\"", "two", "\"three\""}},
                {"\"one, two\", \"three\"", new String[]{"\"one, two\"", "\"three\""}},
                {" one , two , three ", new String[]{"one", "two", "three"}},
                {"\"NY, Long Street, 123\"", new String[]{"\"NY, Long Street, 123\""}}
        });
    }

    protected String input;
    protected String[] expected;

    public FEELListParserTest(final String input,
                              final String[] expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testParsing() {
        final List<String> actual = FEELListParser.parse(input);
        assertThat(actual.size()).isEqualTo(expected.length);
        assertThat(actual).containsExactly(expected);
    }
}
