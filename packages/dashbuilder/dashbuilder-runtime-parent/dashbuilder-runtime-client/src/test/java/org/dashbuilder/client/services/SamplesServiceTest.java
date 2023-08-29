/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.dashbuilder.client.services;

import java.util.HashMap;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SamplesServiceTest {

    private static final String SAMPLES_JSON = "{\n" +
            "        \"Basic\": [\n" +
            "                {\"id\": \"products-dashboard\"},\n" +
            "                {\"id\": \"dashbuilder-kitchensink\"}\n" +
            "        ],\n" +
            "        \"Other\": [\n" +
            "                {\"id\": \"other\"}\n" +
            "        ],\n" +
            "        \"Other2\": [\n" +
            "                {\"id\": \"other2\", \"name\": \"Other 2\"}\n" +
            "        ]\n" +
            "}";

    @Test
    public void testJsonParse() {
        var service = new SamplesService();
        service.samplesByCategory = new HashMap<>();

        service.extractSamplesFromResponse("http://test.org", SAMPLES_JSON);

        var samplesByCategory = service.samplesByCategory();
        var basicSamples = samplesByCategory.get("Basic");
        var otherSamples = samplesByCategory.get("Other");
        var other2Samples = samplesByCategory.get("Other2");

        assertEquals(3, samplesByCategory.size());
        assertEquals(2, basicSamples.size());
        assertEquals(1, otherSamples.size());
        assertEquals(1, other2Samples.size());

        var other = otherSamples.get(0);
        assertEquals("other", other.getId());
        assertEquals("other", other.getName());

        var other2 = other2Samples.get(0);
        assertEquals("other2", other2.getId());
        assertEquals("Other 2", other2.getName());

    }

    @Test
    public void testIsSample() {
        var service = new SamplesService();
        service.samplesByCategory = new HashMap<>();

        service.extractSamplesFromResponse("samples", SAMPLES_JSON);

        assertTrue(service.isSample("samples/other2/other2.dash.yaml"));
        assertTrue(service.isSample("samples/products-dashboard/products-dashboard.dash.yaml"));
    }

    @Test
    public void testBuildSampleUrl() {
        var service = new SamplesService();

        service.samplesEditUrl = "edit";
        assertEquals("edit?sampleId=abc", service.buildSampleUrl("abc"));

        service.samplesEditUrl = "edit?otherParam=test";
        assertEquals("edit?otherParam=test&sampleId=abc", service.buildSampleUrl("abc"));
    }

}
