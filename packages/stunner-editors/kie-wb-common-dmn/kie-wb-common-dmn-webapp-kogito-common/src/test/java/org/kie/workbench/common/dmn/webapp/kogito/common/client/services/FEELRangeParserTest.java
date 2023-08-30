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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.workbench.common.dmn.api.editors.types.RangeValue;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FEELRangeParserTest {

    @Parameterized.Parameters(name = "{index}: input={0}, expected={1}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, new RangeValue()},
                {"", new RangeValue()},
                {".", new RangeValue()},
                {"..", new RangeValue()},
                {"1..2", new RangeValue()},
                {"[1..2", new RangeValue()},
                {"1..2]", new RangeValue()},
                {"(1..2", new RangeValue()},
                {"1..2)", new RangeValue()},
                {"[1..2]", new RangeValueBuilder().includeStartValue().includeEndValue().startValue("1").endValue("2").build()},
                {"(1..2)", new RangeValueBuilder().startValue("1").endValue("2").build()},
                {"[1..2)", new RangeValueBuilder().includeStartValue().startValue("1").endValue("2").build()},
                {"(1..2]", new RangeValueBuilder().includeEndValue().startValue("1").endValue("2").build()},
                {"( 1..2 )", new RangeValueBuilder().startValue("1").endValue("2").build()},
                {"( 1 .. 2 )", new RangeValueBuilder().startValue("1").endValue("2").build()},
                {"( 1 . . 2 )", new RangeValue()},
                {"( 1 . .. 2 )", new RangeValue()},
                {"[ 1 .. . 2 ]", new RangeValue()},
                {"( \"1 .\" .. \"2\" )", new RangeValueBuilder().startValue("\"1 .\"").endValue("\"2\"").build()},
                {"[ \"1\" .. \". 2\" ]", new RangeValueBuilder().includeStartValue().startValue("\"1\"").includeEndValue().endValue("\". 2\"").build()},
                {"[ 1 .. 2 ]", new RangeValueBuilder().includeStartValue().startValue("1").includeEndValue().endValue("2").build()},
                {"[ 1.1 .. 2.2 ]", new RangeValueBuilder().includeStartValue().startValue("1.1").includeEndValue().endValue("2.2").build()},
                {"[ \"a\" .. \"c\" ]", new RangeValueBuilder().includeStartValue().startValue("\"a\"").includeEndValue().endValue("\"c\"").build()},
                {"(..2)", new RangeValue()},
                {"(1..)", new RangeValue()},
                {"(\"a\"..\"z\")", new RangeValueBuilder().startValue("\"a\"").endValue("\"z\"").build()},
                {"(\"a..c\"..\"x..z\")", new RangeValueBuilder().startValue("\"a..c\"").endValue("\"x..z\"").build()},
                {"( date ( \"2018-01-01\" ) .. date( \"2018-02-02\" ) )", new RangeValueBuilder().startValue("date ( \"2018-01-01\" )").endValue("date( \"2018-02-02\" )").build()}
        });
    }

    private String input;
    private RangeValue expected;

    public FEELRangeParserTest(final String input,
                               final RangeValue expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testParsing() {
        final RangeValue actual = FEELRangeParser.parse(input);

        assertThat(actual).isEqualTo(expected);
    }

    private static class RangeValueBuilder {

        private boolean includeStartValue = false;
        private boolean includeEndValue = false;
        private String startValue = "";
        private String endValue = "";

        RangeValueBuilder includeStartValue() {
            this.includeStartValue = true;
            return this;
        }

        RangeValueBuilder includeEndValue() {
            this.includeEndValue = true;
            return this;
        }

        RangeValueBuilder startValue(final String value) {
            this.startValue = value;
            return this;
        }

        RangeValueBuilder endValue(final String value) {
            this.endValue = value;
            return this;
        }

        RangeValue build() {
            final RangeValue rangeValue = new RangeValue();
            rangeValue.setIncludeStartValue(includeStartValue);
            rangeValue.setIncludeEndValue(includeEndValue);
            rangeValue.setStartValue(startValue);
            rangeValue.setEndValue(endValue);
            return rangeValue;
        }
    }
}