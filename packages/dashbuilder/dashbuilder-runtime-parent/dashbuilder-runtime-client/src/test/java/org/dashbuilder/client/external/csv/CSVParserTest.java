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

package org.dashbuilder.client.external.csv;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CSVParserTest {

    private static final String TEST =
            "1942,1943,15,SPECIAL AWARD,\" Metro-Goldwyn-Mayer for its achievement in representing the American Way of Life in the production of the \"\"Andy Hardy\"\" series of films.\",,True\n" +
                    "1943,1944,16,ART DIRECTION (Black-and-White),\"Art Direction:  Hans Dreier, Ernst Fegte;  Interior Decoration:  Bertram Granger\",Five Graves to Cairo,False\n" +
                    "1953,1954,26,HONORARY AWARD,\" Pete Smith for his witty and pungent observations on the American scene in his series of \"\"Pete Smith Specialties.\"\"\",,True";

    private static final String EXP =
            "[[\"1943\",\"1944\",\"16\",\"ART DIRECTION (Black-and-White)\",\"Art Direction:  Hans Dreier, Ernst Fegte;  Interior Decoration:  Bertram Granger\",\"Five Graves to Cairo\",\"false\"],[\"1953\",\"1954\",\"26\",\"HONORARY AWARD\",\" Pete Smith for his witty and pungent observations on the American scene in his series of \\\"Pete Smith Specialties.\\\"\",\"\",\"true\"]]";

    private static final String SAMPLE_TEST =
            "Year,Make,Model,Description,Price\n" +
                    "1997,Ford,E350,\"ac, abs, moon\",3000.00\n" +
                    "1999,Chevy,\"Venture \"\"Extended Edition\"\"\",\"\",4900.00\n" +
                    "1999,Chevy,\"Venture \"\"Extended Edition, Very Large\"\"\",\"\",5000.00\n" +
                    "1996,Jeep,Grand Cherokee,\"MUST SELL! air, moon roof, loaded\",4799.00";

    private static final String SAMPLE_EXPECTED =
            "[[\"1997\",\"Ford\",\"E350\",\"ac, abs, moon\",\"3000.00\"],[\"1999\",\"Chevy\",\"Venture \\\"Extended Edition\\\"\",\"\",\"4900.00\"],[\"1999\",\"Chevy\",\"Venture \\\"Extended Edition, Very Large\\\"\",\"\",\"5000.00\"],[\"1996\",\"Jeep\",\"Grand Cherokee\",\"MUST SELL! air, moon roof, loaded\",\"4799.00\"]]";

    private static final String HEADER = "a,b,c\n";

    private static final String MISSING_VALUE_ON_MIDDLE = HEADER +
            "x,,z\n";
    private static final String MISSING_VALUE_ON_END = HEADER +
            "x,y,\n";

    private static final String MISSING_VALUE_ON_BEGGINING = HEADER +
            ",y,z\n";

    private static final String ONLY_MIDDLE_QUOTED_EMPTY = HEADER +
            ",\"\",\n";

    private static final String ONLY_MIDDLE_QUOTED = HEADER +
            "x,\"y\",z\n";

    private static final String QUOTED_VALUES = HEADER +
            "\"x\",\"y\",\"z\"\n";

    private static final String NO_QUOTE_EMPTY_VALUES = HEADER +
            ",,\n";

    private CSVParser parser;

    @Before
    public void prepare() {
        parser = new CSVParser();
    }

    @Test
    public void testParse() {
        var array = parser.toJsonArray(TEST);
        assertEquals(EXP, array);
    }

    @Test
    public void testParseMissingValueOnMiddle() {
        var array = parser.toJsonArray(MISSING_VALUE_ON_MIDDLE);
        assertEquals("[[\"x\",\"\",\"z\"]]", array);
    }

    @Test
    public void testParseMissingValueOnEnd() {
        var array = parser.toJsonArray(MISSING_VALUE_ON_END);
        assertEquals("[[\"x\",\"y\",\"\"]]", array);
    }

    @Test
    public void testParseMissingValueOnBeginning() {
        var array = parser.toJsonArray(MISSING_VALUE_ON_BEGGINING);
        assertEquals("[[\"\",\"y\",\"z\"]]", array);
    }

    @Test
    public void testParseOnlyMiddleQuotedEmpty() {
        var array = parser.toJsonArray(ONLY_MIDDLE_QUOTED_EMPTY);
        assertEquals("[[\"\",\"\",\"\"]]", array);
    }

    @Test
    public void testParseOnlyMiddleQuoted() {
        var array = parser.toJsonArray(ONLY_MIDDLE_QUOTED);
        assertEquals("[[\"x\",\"y\",\"z\"]]", array);
    }

    @Test
    public void testParseAllQuotedValues() {
        var array = parser.toJsonArray(QUOTED_VALUES);
        assertEquals("[[\"x\",\"y\",\"z\"]]", array);
    }

    @Test
    public void testParseNoQuoteValues() {
        var array = parser.toJsonArray(NO_QUOTE_EMPTY_VALUES);
        assertEquals("[[\"\",\"\",\"\"]]", array);
    }

    @Test
    public void testParseSample() {
        var array = parser.toJsonArray(SAMPLE_TEST);
        assertEquals(SAMPLE_EXPECTED, array);
    }

    @Test
    public void testIsEscapedQuote() {
        assertTrue(parser.isEscapedQuote("\"\"", 0));
        assertFalse(parser.isEscapedQuote("\"a\",\"\"", 2));
        assertTrue(parser.isEscapedQuote("\\\"", 0));
    }

}
