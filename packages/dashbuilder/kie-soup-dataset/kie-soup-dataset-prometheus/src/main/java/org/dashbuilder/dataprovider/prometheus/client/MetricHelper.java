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

import java.util.Map;

public class MetricHelper {

    private static final String METRIC_NAME = "__name__";
    private static final String METRIC_INSTANCE = "instance";
    private static final String METRIC_JOB = "job";

    private MetricHelper() {
        // do nothing
    }

    public static String getName(Map<String, String> metric) {
        return metric.get(METRIC_NAME);
    }

    public static String getInstance(Map<String, String> metric) {
        return metric.get(METRIC_INSTANCE);
    }

    public static String getJob(Map<String, String> metric) {
        return metric.get(METRIC_JOB);
    }

}