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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dashbuilder.json.Json;
import org.dashbuilder.json.JsonArray;
import org.dashbuilder.json.JsonObject;
import org.dashbuilder.json.JsonType;

public class QueryResponseParser {

    private static QueryResponseParser INSTANCE;

    private static final String STATUS_PROP = "status";
    private static final String DATA_PROP = "data";
    private static final String RESULT_TYPE_PROP = "resultType";
    private static final String RESULT_PROP = "result";
    private static final String METRIC_PROP = "metric";
    private static final String VALUES_PROP = "values";
    private static final String VALUE_PROP = "value";
    private static final String ERROR_TYPE_PROP = "errorType";
    private static final String ERROR_PROP = "error";

    public QueryResponse parse(String responseStr) {
        JsonObject json = Json.parse(responseStr);
        QueryResponseBuilder response =  QueryResponseBuilder.newQueryResponseBuilder();
        
        Status status = Status.of(json.getString(STATUS_PROP));
        response.status(status);
        
        if (status == Status.ERROR) {
            response.errorType(json.getString(ERROR_TYPE_PROP));
            response.error(json.getString(ERROR_PROP));
            return response.build();
        }

        return fillSuccessResponse(json, response);
    }

    private QueryResponse fillSuccessResponse(JsonObject json, QueryResponseBuilder response) {
        JsonObject data = json.getObject(DATA_PROP);
        ResultType resultType = ResultType.of(data.getString(RESULT_TYPE_PROP));
        JsonArray result = data.getArray(RESULT_PROP);

        switch (resultType) {
            case STRING:
            case SCALAR:
                response.results(parseScalarResult(result));
                break;
            case MATRIX:
                response.results(parseResults(result, this::parseMatrixResult));
                break;
            case VECTOR:
                response.results(parseResults(result, this::parseVectorResult));
                break;
            default:
                break;

        }
        response.resultType(resultType);

        return response.build();
    }

    private List<Result> parseResults(JsonArray resultArray, Function<JsonObject, Result> resultParser) {
        return IntStream.range(0, resultArray.length())
                        .mapToObj(i -> (JsonObject) resultArray.get(i))
                        .map(resultParser)
                        .collect(Collectors.toList());
    }

    private List<Result> parseScalarResult(JsonArray resultArray) {
        List<Value> valueList = Collections.singletonList(parseValue(resultArray));
        Result result = new Result(Collections.emptyMap(), valueList);
        return Collections.singletonList(result);
    }

    private Result parseMatrixResult(JsonObject resultObj) {
        return buildResult(resultObj.getObject(METRIC_PROP), resultObj.getArray(VALUES_PROP));
    }

    private Result parseVectorResult(JsonObject resultObj) {
        return buildResult(resultObj.getObject(METRIC_PROP), resultObj.getArray(VALUE_PROP));
    }

    private Result buildResult(JsonObject metricObj, JsonArray valuesArray) {
        Map<String, String> metric = parseMetric(metricObj);
        List<Value> values = parseValues(valuesArray);
        return new Result(metric, values);
    }

    private Map<String, String> parseMetric(JsonObject object) {
        return Arrays.asList(object.keys())
                     .stream()
                     .collect(Collectors.toMap(k -> k, object::getString));
    }

    private List<Value> parseValues(JsonArray values) {
        if (values.length() == 0) {
            return Collections.emptyList();
        }
        JsonType type = values.get(0).getType();
        if (type == JsonType.ARRAY) {
            return IntStream.range(0, values.length())
                            .mapToObj(i -> values.getArray(i))
                            .map(this::parseValue)
                            .collect(Collectors.toList());
        } else {
            return Collections.singletonList(parseValue(values));
        }
    }

    private Value parseValue(JsonArray array) {
        Double asNumber = array.get(0).asNumber();
        return Value.of(asNumber.longValue(),
                        array.get(1).asString());
    }

    public static QueryResponseParser get() {
        if (INSTANCE == null) {
            INSTANCE = new QueryResponseParser();
        }
        return INSTANCE;
    }

}