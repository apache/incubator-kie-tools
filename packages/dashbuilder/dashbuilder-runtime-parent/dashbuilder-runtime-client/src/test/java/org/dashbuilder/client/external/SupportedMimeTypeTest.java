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

package org.dashbuilder.client.external;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class SupportedMimeTypeTest {

    @Test
    public void byCsvMimeTypeTest() {
        assertEquals(SupportedMimeType.CSV, SupportedMimeType.byMimeType("text/csv").get());
    }

    @Test
    public void byJsonMimeTypeTest() {
        assertEquals(SupportedMimeType.JSON, SupportedMimeType.byMimeType("application/json").get());
    }

    @Test
    public void byTextPlainMimeTypeTest() {
        assertFalse(SupportedMimeType.byMimeType("text/plain").isPresent());
    }

    @Test
    public void byCsvUrlTest() {
        assertEquals(SupportedMimeType.CSV, SupportedMimeType.byUrl("abluble.csv").get());
    }

    @Test
    public void byJsonUrlTest() {
        assertEquals(SupportedMimeType.JSON, SupportedMimeType.byUrl("abluble.json").get());
    }

    @Test
    public void byMetricUrlTest() {
        assertEquals(SupportedMimeType.METRIC, SupportedMimeType.byUrl("abluble/metrics").get());
    }

}
