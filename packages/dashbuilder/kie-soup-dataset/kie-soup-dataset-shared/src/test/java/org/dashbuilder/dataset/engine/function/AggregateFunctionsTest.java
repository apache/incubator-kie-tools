/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.engine.function;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AggregateFunctionsTest {

    DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    List<String> STRING_LIST = Arrays.asList("a", "b", "c");
    List<String> STRING_LIST_WITH_NULL = Arrays.asList("a", "b", "c", null);
    List<Date> DATE_LIST = new ArrayList<>();
    List<Date> DATE_LIST_WITH_NULL = new ArrayList<>();
    List<Double> NUMERIC_LIST = Arrays.asList(1d, 2d, 3d);
    List<Double> NUMERIC_LIST_WITH_NULL = Arrays.asList(1d, 2d, 3d, null);
    List<Double> EMPTY_LIST = Collections.emptyList();
    List<Double> NULL_LIST = new ArrayList<>();
    Date date1;
    Date date2;
    Date date3;

    @Before
    public void setUp() throws Exception {
        NULL_LIST.add(null);
        date1 = DATE_FORMAT.parse("01-01-2030 00:00:00");
        date2 = DATE_FORMAT.parse("01-02-2030 00:00:00");
        date3 = DATE_FORMAT.parse("01-03-2030 00:00:00");
        DATE_LIST.add(date1);
        DATE_LIST.add(date2);
        DATE_LIST.add(date3);
        DATE_LIST_WITH_NULL.addAll(DATE_LIST);
        DATE_LIST_WITH_NULL.add(null);
    }

    @Test
    public void testMinFunctionNumeric() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 1d);
    }

    @Test
    public void testMinFunctionNumericNull() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 1d);
    }

    @Test
    public void testMinFunctionDate() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(DATE_LIST);
        assertEquals(result, date1);
    }

    @Test
    public void testMinFunctionDateNull() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(DATE_LIST_WITH_NULL);
        assertEquals(result, date1);
    }

    @Test
    public void testMinFunctionString() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(STRING_LIST);
        assertEquals(result, "a");
    }

    @Test
    public void testMinFunctionStringNull() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(STRING_LIST_WITH_NULL);
        assertEquals(result, "a");
    }

    @Test
    public void testMinFunctionAllNull() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(NULL_LIST);
        assertNull(result);
    }

    @Test
    public void testMinFunctionEmpty() {
        MinFunction function = new MinFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertNull(result);
    }

    @Test
    public void testMaxFunctionNumeric() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testMaxFunctionNumericNull() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 3d);
    }

    @Test
    public void testMaxFunctionDate() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(DATE_LIST);
        assertEquals(result, date3);
    }

    @Test
    public void testMaxFunctionDateNull() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(DATE_LIST_WITH_NULL);
        assertEquals(result, date3);
    }

    @Test
    public void testMaxFunctionString() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(STRING_LIST);
        assertEquals(result, "c");
    }

    @Test
    public void testMaxFunctionStringNull() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(STRING_LIST_WITH_NULL);
        assertEquals(result, "c");
    }

    @Test
    public void testMaxFunctionAllNull() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(NULL_LIST);
        assertNull(result);
    }

    @Test
    public void testMaxFunctionEmpty() {
        MaxFunction function = new MaxFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertNull(result);
    }

    @Test
    public void testCountFunctionNumeric() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testCountFunctionNumericNull() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testCountFunctionDate() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(DATE_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testCountFunctionDateNull() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(DATE_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testCountFunctionString() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(STRING_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testCountFunctionStringNull() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(STRING_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testCountAllNull() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(NULL_LIST);
        assertEquals(result, 1d);
    }

    @Test
    public void testCountEmpty() {
        CountFunction function = new CountFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertEquals(result, 0d);
    }

    @Test
    public void testDistinctCountFunctionNumeric() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testDistinctFunctionNumericNull() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testDistinctFunctionDate() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(DATE_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testDistinctFunctionDateNull() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(DATE_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testDistinctFunctionString() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(STRING_LIST);
        assertEquals(result, 3d);
    }

    @Test
    public void testDistinctFunctionStringNull() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(STRING_LIST_WITH_NULL);
        assertEquals(result, 4d);
    }

    @Test
    public void testDistinctFunctionAllNull() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(NULL_LIST);
        assertEquals(result, 1d);
    }

    @Test
    public void testDistinctFunctionEmpty() {
        DistinctFunction function = new DistinctFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertEquals(result, 0d);
    }

    @Test
    public void testSumFunctionNumeric() {
        SumFunction function = new SumFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 6d);
    }

    @Test
    public void testSumFunctionNumericNull() {
        SumFunction function = new SumFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 6d);
    }

    @Test
    public void testSumFunctionAllNull() {
        SumFunction function = new SumFunction();
        Object result = function.aggregate(NULL_LIST);
        assertEquals(result, 0d);
    }

    @Test
    public void testSumFunctionEmpty() {
        SumFunction function = new SumFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertEquals(result, 0d);
    }

    @Test
    public void testAvgFunctionNumeric() {
        AverageFunction function = new AverageFunction();
        Object result = function.aggregate(NUMERIC_LIST);
        assertEquals(result, 2d);
    }

    @Test
    public void testAvgFunctionNumericNull() {
        AverageFunction function = new AverageFunction();
        Object result = function.aggregate(NUMERIC_LIST_WITH_NULL);
        assertEquals(result, 1.5d);
    }

    @Test
    public void testAvgFunctionAllNull() {
        AverageFunction function = new AverageFunction();
        Object result = function.aggregate(NULL_LIST);
        assertEquals(result, 0d);
    }

    @Test
    public void testAvgFunctionEmpty() {
        AverageFunction function = new AverageFunction();
        Object result = function.aggregate(EMPTY_LIST);
        assertEquals(result, 0d);
    }
}
