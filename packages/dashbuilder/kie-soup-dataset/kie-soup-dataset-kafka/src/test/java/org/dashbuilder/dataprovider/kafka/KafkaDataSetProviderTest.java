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
package org.dashbuilder.dataprovider.kafka;

import java.util.List;

import org.dashbuilder.dataprovider.kafka.model.KafkaMetric;
import org.dashbuilder.dataprovider.kafka.model.KafkaMetricsRequest;
import org.dashbuilder.dataset.DataColumn;
import org.dashbuilder.dataset.DataSet;
import org.dashbuilder.dataset.def.KafkaDataSetDef.MetricsTarget;
import org.junit.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;

public class KafkaDataSetProviderTest {

    @Test
    public void testToDataSet() throws Exception {
        KafkaDataSetProvider provider = new KafkaDataSetProvider();
        String domain1 = "domain1";
        String type1 = "type1";
        String name1 = "name1";
        String attr1 = "attr";
        double value1 = 2.0;
        List<KafkaMetric> metrics = asList(KafkaMetric.from(domain1, type1, name1, attr1, value1));
        DataSet dataset = provider.toDataSet(metrics);
        assertEquals(1, dataset.getRowCount());

        DataColumn domainCl = dataset.getColumnById(KafkaDataSetProvider.DOMAIN_COLUMN);
        DataColumn typeCl = dataset.getColumnById(KafkaDataSetProvider.TYPE_COLUMN);
        DataColumn nameCl = dataset.getColumnById(KafkaDataSetProvider.NAME_COLUMN);
        DataColumn attributeCl = dataset.getColumnById(KafkaDataSetProvider.ATTRIBUTE_COLUMN);
        DataColumn valueCl = dataset.getColumnById(KafkaDataSetProvider.VALUE_COLUMN);

        assertEquals(domain1, domainCl.getValues().get(0));
        assertEquals(type1, typeCl.getValues().get(0));
        assertEquals(name1, nameCl.getValues().get(0));
        assertEquals(attr1, attributeCl.getValues().get(0));
        assertEquals(value1, valueCl.getValues().get(0));

    }

    @Test
    public void testToDataSetEmtpy() throws Exception {
        KafkaDataSetProvider provider = new KafkaDataSetProvider();
        DataSet dataset = provider.toDataSet(emptyList());
        assertEquals(0, dataset.getRowCount());
    }

    @Test
    public void testNoMetricsErrorMessage() {
        KafkaDataSetProvider provider = new KafkaDataSetProvider();

        KafkaMetricsRequest request = KafkaMetricsRequest.Builder.newBuilder("", "").build();
        String message = provider.noMetricsErrorMessage(request);
        assertEquals("No metrics were found. Check if the BROKER has available metrics and the filter matches any metrics", message);

        request = KafkaMetricsRequest.Builder.newBuilder("", "").target(MetricsTarget.PRODUCER).clientId("c").build();
        message = provider.noMetricsErrorMessage(request);
        assertEquals("No metrics were found. Check if the PRODUCER has available metrics, client id c is correct and the filter matches any metrics", message);

        request = KafkaMetricsRequest.Builder.newBuilder("", "").target(MetricsTarget.CONSUMER).clientId("c").build();
        message = provider.noMetricsErrorMessage(request);
        assertEquals("No metrics were found. Check if the CONSUMER has available metrics, client id c is correct and the filter matches any metrics", message);

        request = KafkaMetricsRequest.Builder.newBuilder("", "").target(MetricsTarget.CONSUMER).nodeId("n").build();
        message = provider.noMetricsErrorMessage(request);
        assertEquals("No metrics were found. Check if the CONSUMER has available metrics, node id n is correct and the filter matches any metrics", message);

        request = KafkaMetricsRequest.Builder.newBuilder("", "").target(MetricsTarget.CONSUMER).topic("t").build();
        message = provider.noMetricsErrorMessage(request);
        assertEquals("No metrics were found. Check if the CONSUMER has available metrics, topic t is correct and the filter matches any metrics", message);
    }

}