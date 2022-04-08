/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.client.external;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CSVParserTest {

    String test = "h1,h2,h3\n" +
            "\"a\"\"\",\"b,\",c\n" +
            "d,e,f";
    
    @Test
    public void testParse() {
        var parser = new CSVParser();
        var array = parser.toJsonArray(test);
        System.out.println(array.toJson());
        var row1 = array.getArray(0);
        var row2 = array.getArray(1);
        assertEquals("a\"", row1.getString(0));
        assertEquals("b,", row1.getString(1));
        assertEquals("c", row1.getString(2));
        assertEquals("d", row2.getString(0));
        assertEquals("e", row2.getString(1));
        assertEquals("f", row2.getString(2));
    }
}
