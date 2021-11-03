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
package org.dashbuilder.dataprovider.prometheus.client;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QueryResponseParserTest {

    private final String VECTOR_JSON = "{\"status\":\"success\",\"data\":{\"resultType\":\"vector\",\"result\":" +
                                       "[{\"metric\":{\"__name__\":\"up\",\"instance\":\"localhost:9090\",\"job\":\"prometheus\"}," +
                                       "\"value\":[1608760240.193,\"1\"]}]}}";

    private final String MATRIX_JSON = "{\"status\":\"success\",\"data\":{\"resultType\":\"matrix\",\"result\":" +
                                       "[{\"metric\":{\"__name__\":\"up\",\"job\":\"prometheus\",\"instance\":\"localhost:9090\"}," +
                                       "\"values\":[[1435781430.781,\"1\"],[1435781445.781,\"1\"]]}," +
                                       "{\"metric\":{\"__name__\":\"up\",\"job\":\"node\",\"instance\":\"localhost:9091\"}," +
                                       "\"values\":[[1435781430.781,\"0\"],[1435781445.781,\"1\"]]}]}}";

    private final String SCALAR_JSON = "{\"status\":\"success\",\"data\":{\"resultType\":\"scalar\",\"result\":" +
                                       "[1608819321.842,\"10\"]}}";

    private final String METRIC_PARAM_JSON = "{\"status\":\"success\",\"data\":{\"resultType\":\"vector\",\"result\":" +
                                             "[{\"metric\":{\"attr\":\"attr_val\"}," +
                                             "\"value\":[1609169361.502,\"12\"]}]}}";

    private final String ERROR_RESPONSE_JSON = "{\"status\":\"error\",\"errorType\":\"error type\",\"error\":\"error message\"}";

    @Test
    public void testParseVectorResponse() {
        QueryResponse response = QueryResponseParser.get().parse(VECTOR_JSON);

        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(ResultType.VECTOR, response.getResultType());

        Result result = response.getResults().get(0);
        Map<String, String> metric = result.getMetric();
        assertEquals("up", MetricHelper.getName(metric));
        assertEquals("localhost:9090", MetricHelper.getInstance(metric));
        assertEquals("prometheus", MetricHelper.getJob(metric));

        List<Value> values = result.getValues();
        assertEquals(1, values.size());
        assertEquals(1608760240, values.get(0).getTimestamp());
        assertEquals("1", values.get(0).getValue());
    }

    @Test
    public void testParseMatrixResponse() {
        QueryResponse response = QueryResponseParser.get().parse(MATRIX_JSON);

        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(ResultType.MATRIX, response.getResultType());

        Result result1 = response.getResults().get(0);
        Map<String, String> metric1 = result1.getMetric();
        assertEquals("up", MetricHelper.getName(metric1));
        assertEquals("localhost:9090", MetricHelper.getInstance(metric1));
        assertEquals("prometheus", MetricHelper.getJob(metric1));

        List<Value> values = result1.getValues();
        assertEquals(2, values.size());
        assertEquals(1435781430, values.get(0).getTimestamp());
        assertEquals("1", values.get(0).getValue());
        assertEquals(1435781445, values.get(1).getTimestamp());
        assertEquals("1", values.get(1).getValue());

        Result result2 = response.getResults().get(1);
        Map<String, String> metric2 = result2.getMetric();
        assertEquals("up", MetricHelper.getName(metric2));
        assertEquals("localhost:9091", MetricHelper.getInstance(metric2));
        assertEquals("node", MetricHelper.getJob(metric2));

        List<Value> values2 = result2.getValues();
        assertEquals(2, values2.size());
        assertEquals(1435781430, values2.get(0).getTimestamp());
        assertEquals("0", values2.get(0).getValue());
        assertEquals(1435781445, values2.get(1).getTimestamp());
        assertEquals("1", values2.get(1).getValue());
    }

    @Test
    public void testParseScalarResponse() {
        QueryResponse response = QueryResponseParser.get().parse(SCALAR_JSON);

        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(ResultType.SCALAR, response.getResultType());

        Value value = response.getResults().get(0).getValues().get(0);
        assertEquals(1608819321, value.getTimestamp());
        assertEquals("10", value.getValue());
    }

    @Test
    public void testMetricAttributeResponse() {
        QueryResponse response = QueryResponseParser.get().parse(METRIC_PARAM_JSON);

        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(ResultType.VECTOR, response.getResultType());

        assertEquals("attr_val", response.getResults().get(0).getMetric().get("attr"));

    }
    
    @Test
    public void testErrorResponse() {
        QueryResponse response = QueryResponseParser.get().parse(ERROR_RESPONSE_JSON);

        assertEquals(Status.ERROR, response.getStatus());

        assertEquals("error message", response.getError());
        assertEquals("error type", response.getErrorType());

    }
}