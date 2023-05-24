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
package org.dashbuilder.client.external.metrics;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MetricsParserTest {

    private MetricsParser parser;

    private static String METRICS = "# HELP process_start_time_seconds Start time of the process since unix epoch.\n" +
            "# TYPE process_start_time_seconds gauge\n" +
            "process_start_time_seconds 1.650302155929E9\n" +
            "# HELP jvm_threads_states_threads The current number of threads having NEW state\n" +
            "# TYPE jvm_threads_states_threads gauge\n" +
            "jvm_threads_states_threads{state=\"runnable\",} 9.0";

    @Before
    public void init() {
        parser = new MetricsParser();
    }

    @Test
    public void testLabelMetricToArray() {
        var r = parser.metricToJsonArray("jvm_memory_max_bytes{area=\"nonheap\",id=\"Metaspace\",} -1.0");
        assertEquals("jvm_memory_max_bytes", r.getString(0));
        assertEquals("area=\"nonheap\",id=\"Metaspace\",", r.getString(1));
        assertEquals("-1.0", r.getString(2));
    }

    @Test
    public void testLabelWithSpaceMetricToArray() {
        var r = parser.metricToJsonArray("metric{l1=\"l1 val\",} -1.0");
        assertEquals("metric", r.getString(0));
        assertEquals("l1=\"l1 val\",", r.getString(1));
        assertEquals("-1.0", r.getString(2));
    }

    @Test
    public void testMetricWithNaN() {
        var r = parser.metricToJsonArray("metric{l1=\"l1 val\",} NaN");
        assertEquals("metric", r.getString(0));
        assertEquals("l1=\"l1 val\",", r.getString(1));
        assertEquals(MetricsParser.DEFAULT_NAN_VALUE, r.getString(2));
    }

    @Test
    public void testNoLabelMetricToArray() {
        var r = parser.metricToJsonArray("process_uptime_seconds 339164.251");
        assertEquals("process_uptime_seconds", r.getString(0));
        assertEquals("", r.getString(1));
        assertEquals("339164.251", r.getString(2));
    }

    @Test
    public void testEmptyMetricToArray() {
        var r = parser.metricToJsonArray("");
        assertTrue(r.isEmpty());
    }

    @Test
    public void testNoValueMetricToArray() {
        var r = parser.metricToJsonArray("test      ");
        assertTrue(r.isEmpty());
    }

    @Test
    public void testMetricsToArray() {
        var r = parser.metricsToJsonArray(METRICS);
        assertEquals(2, r.length());

        var processStartTimeSeconds = r.getArray(0);

        assertEquals("process_start_time_seconds", processStartTimeSeconds.getString(0));
        assertEquals("", processStartTimeSeconds.getString(1));
        assertEquals("1.650302155929E9", processStartTimeSeconds.getString(2));

        var jvmThreadsStatesThreads = r.getArray(1);

        assertEquals("jvm_threads_states_threads", jvmThreadsStatesThreads.getString(0));
        assertEquals("state=\"runnable\",", jvmThreadsStatesThreads.getString(1));
        assertEquals("9.0", jvmThreadsStatesThreads.getString(2));
    }

}
