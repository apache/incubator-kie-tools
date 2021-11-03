/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataprovider.prometheus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dashbuilder.dataprovider.prometheus.client.QueryResponse;
import org.dashbuilder.dataprovider.prometheus.client.Result;
import org.dashbuilder.dataprovider.prometheus.client.Value;
import org.dashbuilder.dataset.DataSet;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrometheusDataSetProviderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void toDataSetTest() {
        PrometheusDataSetProvider provider = new PrometheusDataSetProvider();

        QueryResponse response = new QueryResponse();

        Map<String, String> metric1 = new HashMap<>();
        final String M1_CL = "M1";
        final String M2_CL = "M2";

        metric1.put(M1_CL, "abc1");
        metric1.put(M2_CL, "abc2");
        List<Value> values1 = Arrays.asList(Value.of(1, "2"),
                                            Value.of(3, "4"));

        Map<String, String> metric2 = new HashMap<>();
        metric2.put(M1_CL, "abc3");
        metric2.put(M2_CL, "abc4");

        List<Value> values2 = Arrays.asList(Value.of(123, "42"),
                                            Value.of(321, "24"));

        Result result1 = new Result(metric1, values1);
        Result result2 = new Result(metric2, values2);

        List<Result> results = Arrays.asList(result1, result2);

        response.setResults(results);
        DataSet dataSet = provider.toDataSet(response);

        assertEquals(4, dataSet.getColumns().size());
        assertEquals(4, dataSet.getRowCount());

        List<Object> m1Cl = dataSet.getColumnById(M1_CL).getValues();
        assertTrue(m1Cl.contains("abc1"));
        assertTrue(m1Cl.contains("abc3"));

        List<Object> m2Cl = dataSet.getColumnById(M2_CL).getValues();
        assertTrue(m2Cl.contains("abc2"));
        assertTrue(m2Cl.contains("abc4"));

        List<Object> timeCl = dataSet.getColumnById(PrometheusDataSetProvider.TIME_COLUMN).getValues();
        Object[] expectedTimes = {1.0, 3.0, 123.0, 321.0};
        assertArrayEquals(timeCl.toArray(), expectedTimes);

        List<Object> valueCl = dataSet.getColumnById(PrometheusDataSetProvider.VALUE_COLUMN).getValues();
        Object[] expectedValues = {2.0, 4.0, 42.0, 24.0};
        assertArrayEquals(valueCl.toArray(), expectedValues);

    }

    @Test
    public void emptyQueryResponseToDataSetTest() {
        PrometheusDataSetProvider provider = new PrometheusDataSetProvider();

        QueryResponse response = new QueryResponse();

        DataSet dataSet = provider.toDataSet(response);

        assertEquals(0, dataSet.getRowCount());
        assertEquals(2, dataSet.getColumns().size());

    }

}