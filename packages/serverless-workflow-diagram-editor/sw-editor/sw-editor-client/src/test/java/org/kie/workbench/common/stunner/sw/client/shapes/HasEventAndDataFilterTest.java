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

package org.kie.workbench.common.stunner.sw.client.shapes;

import org.junit.Test;
import org.kie.workbench.common.stunner.sw.definition.EventDataFilter;

public class HasEventAndDataFilterTest extends HasTranslationGeneralTest {
    private final HasEventAndDataFilter hasEventAndDataFilter = HasEventAndDataFilterTest.super::getTranslation;
    private final String DATA = "No longer than 30 symbols data";
    private final String LONG_DATA = "Long data should be longer than 30 symbols";
    private final String LONG_DATA_CUT = "Long data should be longer tha...";
    private final String TO_STATE_DATA = "Not a long to state data";
    private final String LONG_TO_STATE_DATA = "Long to state data should be longer than 30 symbols";
    private final String LONG_TO_STATE_DATA_CUT = "Long to state data should be l...";

    @Test
    public void stateDataFilterEmptyTest() {
        assertTranslations(TEST_STRING, hasEventAndDataFilter.getEventFilter(null), "Eventfilter.null");
    }

    @Test
    public void stateEventAndDataFilterTest() {
        EventDataFilter filterData = new EventDataFilter();
        filterData.setData(LONG_DATA);
        filterData.setToStateData(LONG_TO_STATE_DATA);
        filterData.setUseData(false);

        assertTranslations(TEST_STRING + ":\r\n"
                        + TEST_STRING + ": " + LONG_DATA_CUT + "\r\n"
                        + TEST_STRING + ": " + LONG_TO_STATE_DATA_CUT + "\r\n"
                        + TEST_STRING + ": false",
                hasEventAndDataFilter.getEventFilter(filterData),
                "Eventfilter.parameter",
                "Eventfilter.data",
                "Eventfilter.tostatedata",
                "Eventfilter.usedata");
    }

    @Test
    public void stateEventAndDataFilterMissingDataTest() {
        EventDataFilter filterData = new EventDataFilter();
        filterData.setToStateData(TO_STATE_DATA);
        filterData.setUseData(false);

        assertTranslations(TEST_STRING + ":\r\n"
                        + TEST_STRING + ": undefined\r\n"
                        + TEST_STRING + ": " + TO_STATE_DATA + "\r\n"
                        + TEST_STRING + ": false",
                hasEventAndDataFilter.getEventFilter(filterData),
                "Eventfilter.parameter",
                "Eventfilter.data",
                "Eventfilter.tostatedata",
                "Eventfilter.usedata");
    }

    @Test
    public void stateEventAndDataFilterMissingToStateDataTest() {
        EventDataFilter filterData = new EventDataFilter();
        filterData.setData(DATA);
        filterData.setUseData(true);

        assertTranslations(TEST_STRING + ":\r\n"
                        + TEST_STRING + ": " + DATA + "\r\n"
                        + TEST_STRING + ": undefined\r\n"
                        + TEST_STRING + ": true",
                hasEventAndDataFilter.getEventFilter(filterData),
                "Eventfilter.parameter",
                "Eventfilter.data",
                "Eventfilter.tostatedata",
                "Eventfilter.usedata");
    }

    @Test
    public void stateEventAndDataFilterMissingUseDataTest() {
        EventDataFilter filterData = new EventDataFilter();
        filterData.setData(LONG_DATA);
        filterData.setToStateData(LONG_TO_STATE_DATA);

        assertTranslations(TEST_STRING + ":\r\n"
                        + TEST_STRING + ": " + LONG_DATA_CUT + "\r\n"
                        + TEST_STRING + ": " + LONG_TO_STATE_DATA_CUT + "\r\n"
                        + TEST_STRING + ": true",
                hasEventAndDataFilter.getEventFilter(filterData),
                "Eventfilter.parameter",
                "Eventfilter.data",
                "Eventfilter.tostatedata",
                "Eventfilter.usedata");
    }
}
