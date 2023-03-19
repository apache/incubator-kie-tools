/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.DataSetCore;
import org.dashbuilder.dataset.group.AggregateFunction;
import org.dashbuilder.dataset.group.AggregateFunctionManager;
import org.dashbuilder.dataset.group.AggregateFunctionType;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AggregateFunctionTest {

    AggregateFunctionManager aggregateFunctionManager;
    List listOfNumbers = Arrays.asList(1, 2, 3, 4, 5);
    List listOfStrings = Arrays.asList("A", "B", "C", "A", "B");

    @Before
    public void setUp() throws Exception {
        aggregateFunctionManager = DataSetCore.get().getAggregateFunctionManager();
    }

    @Test
    public void testSumFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.SUM);
        Object result = sf.aggregate(listOfNumbers);
        assertThat(result).isEqualTo(15d);
    }

    @Test
    public void testAvgFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.AVERAGE);
        Object result = sf.aggregate(listOfNumbers);
        assertThat(result).isEqualTo(3d);
    }

    @Test
    public void testMaxFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.MAX);
        Object result = sf.aggregate(listOfNumbers);
        assertThat(result).isEqualTo(5d);
    }

    @Test
    public void testMinFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.MIN);
        Object result = sf.aggregate(listOfNumbers);
        assertThat(result).isEqualTo(1d);
    }

    @Test
    public void testCountFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.COUNT);
        Object result = sf.aggregate(listOfStrings);
        assertThat(result).isEqualTo(5d);
    }

    @Test
    public void testDistinctFunction() throws Exception {
        AggregateFunction sf = aggregateFunctionManager.getFunctionByType(AggregateFunctionType.DISTINCT);
        Object result = sf.aggregate(listOfStrings);
        assertThat(result).isEqualTo(3d);
    }
}
