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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PrometheusClientTest {

    private static final String BASE_URL = "http://localhost:9090/";

    @Mock
    HttpClient httpClient;

    @Mock
    QueryResponseParser parser;

    @InjectMocks
    PrometheusClient prometheusClient;

    @Before
    public void setup() {
        prometheusClient.setBaseUrl(BASE_URL);
    }

    @Test
    public void testQueryResponse() {
        prometheusClient.query("up");
        verify(httpClient).doGet(eq("http://localhost:9090/api/v1/query?query=up"),
                                 eq(null),
                                 eq(null));
    }

    @Test
    public void testQueryRangeResponse() {
        prometheusClient.queryRange("up", "startDate", "endDate", "step");
        verify(httpClient).doGet(eq("http://localhost:9090/api/v1/query_range?query=up&start=startDate&end=endDate&step=step"), 
                                 eq(null), 
                                 eq(null));
    }
    
    @Test
    public void testQueryResponseWithCredentials() {
        System.setProperty(PrometheusCredentialProvider.PROMETHEUS_USER_PROP, "usr");
        System.setProperty(PrometheusCredentialProvider.PROMETHEUS_PASSWORD_PROP, "psw");
        prometheusClient.query("up");
        verify(httpClient).doGet(eq("http://localhost:9090/api/v1/query?query=up"),
                                 eq("usr"),
                                 eq("psw"));
        System.clearProperty(PrometheusCredentialProvider.PROMETHEUS_USER_PROP);
        System.clearProperty(PrometheusCredentialProvider.PROMETHEUS_PASSWORD_PROP);
    }

}
