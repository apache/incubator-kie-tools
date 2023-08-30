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

public class CSVColumnsFunctionTest {

    private static final String VALID_HEADERS = "a,b,c\n" +
            "1,2,3";

    private static final String EMPTY_HEADERS = "\n1,2,3";

    private static final String EMPTY_CONTENT = " ";

    private CSVColumnsFunction columnsFunction;

    @Before
    public void prepare() {
        columnsFunction = new CSVColumnsFunction();

    }

    @Test
    public void testColumns() {
        var columns = columnsFunction.apply(VALID_HEADERS);
        assertEquals(3, columns.size());
        assertEquals("a", columns.get(0).getId());
        assertEquals("b", columns.get(1).getId());
        assertEquals("c", columns.get(2).getId());
    }

    @Test
    public void testEmptyHeaders() {
        var columns = columnsFunction.apply(EMPTY_HEADERS);
        assertEquals(0, columns.size());        
    }

    @Test
    public void testEmptyContent() {
        var columns = columnsFunction.apply(EMPTY_CONTENT);
        assertEquals(0, columns.size());
    }

}
