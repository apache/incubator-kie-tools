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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PrometheusClient {

    private static final String DEFAULT_BASE_URL = "http://localhost:9090";

    private static final String API_BASE_URI = "api/v1";
    private static final String QUERY_URL = API_BASE_URI + "/query";

    private static final String QUERY_RANGE_URI = API_BASE_URI + "/query_range";

    private static final String QUERY_PARAM = "query";
    private static final String START_PARAM = "start";
    private static final String END_PARAM = "end";
    private static final String STEP_PARAM = "step";

    private String baseUrl = DEFAULT_BASE_URL;
    private HttpClient client;
    private QueryResponseParser parser;

    public PrometheusClient() {
        this(DEFAULT_BASE_URL);
    }

    public PrometheusClient(String baseUrl) {
        this(baseUrl, HttpClient.get(), QueryResponseParser.get());
    }

    PrometheusClient(String baseUrl, HttpClient client, QueryResponseParser parser) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.parser = parser;
    }

    public QueryResponse query(String query) {
        String url = buildUrl(QUERY_URL, query, null, null, null);
        return getQueryResponse(url);
    }

    public QueryResponse queryRange(String query, String start, String end, String step) {
        String url = buildUrl(QUERY_RANGE_URI, query, start, end, step);
        return getQueryResponse(url);
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    String buildUrl(String base, String query, String start, String end, String step) {
        String url = removeLastSlash();
        String apiUrl = String.join("/", url, base) + "?";

        Map<String, String> params = new HashMap<>();
        addParam(params, QUERY_PARAM, query);
        addParam(params, START_PARAM, start);
        addParam(params, END_PARAM, end);
        addParam(params, STEP_PARAM, step);

        return params.entrySet().stream()
                     .map(e -> e.getKey() + "=" + encodeValue(e.getValue()))
                     .collect(Collectors.joining("&", apiUrl, ""));
    }

    private String removeLastSlash() {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    private void addParam(Map<String, String> params, String paramName, String paramValue) {
        if (paramValue != null) {
            params.put(paramName, paramValue);
        }
    }

    private String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error encoding value " + value, e);
        }
    }
    
    private QueryResponse getQueryResponse(String url) {
        PrometheusCredentialProvider credentialProvider = PrometheusCredentialProvider.get();
        String username = credentialProvider.getUser();
        String password = credentialProvider.getPassword();
        return parser.parse(client.doGet(url, username, password));
    }

}
