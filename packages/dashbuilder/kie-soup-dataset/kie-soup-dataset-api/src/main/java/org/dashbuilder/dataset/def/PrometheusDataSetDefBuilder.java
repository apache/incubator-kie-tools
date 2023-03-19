/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dataset.def;

/**
 * A builder for defining Prometheus data sets.
 *
 * <pre>
 *    DataSetDef dataSetDef = DataSetDefFactory.newPrometheusDataSetDef()
 *     .serverUrl("http://localhost:9090")
 *     .query("up")
 *     .attributes("metricAttribute1,metricAttribute2")
 *     .buildDef();
 * </pre>
 */
public interface PrometheusDataSetDefBuilder<T extends DataSetDefBuilder> extends DataSetDefBuilder<T> {

    /**
     * Set the data set server url
     *
     * @param className The Prometheus server where this data set should send the queries to;
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T serverUrl(String serverUrl);

    /**
     * Set the Prometheus query that will be used to build this dataset
     *
     * @param query The Prometheus query
     * @param paramValue A string representation of the parameter value.
     * @return The DataSetDefBuilder instance that is being used to configure a DataSetDef.
     */
    T query(String query);

}