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
import org.kie.workbench.common.stunner.sw.definition.StateDataFilter;

public class HasDataFilterTest extends HasTranslationGeneralTest {

    private final HasDataFilter hasDataFilter = HasDataFilterTest.super::getTranslation;

    @Test
    public void stateDataFilterEmptyTest() {
        assertTranslations(TEST_STRING, hasDataFilter.getStateDataFilter(null), "Datafilter.null");
    }

    @Test
    public void stateDataFilterInputIsEmptyTest() {
        StateDataFilter filter = new StateDataFilter();
        String in = "in";
        filter.setInput(in);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": in\r\n" + TEST_STRING + ": undefined",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterOutputIsEmptyTest() {
        StateDataFilter filter = new StateDataFilter();
        String out = "out";
        filter.setOutput(out);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": undefined\r\n" + TEST_STRING + ": out",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterInputIsLongTest() {
        StateDataFilter filter = new StateDataFilter();
        String in = "012345678901234567890123456789012345";
        filter.setInput(in);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": 012345678901234567890123456789...\r\n" + TEST_STRING + ": undefined",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterOutputIsLongTest() {
        StateDataFilter filter = new StateDataFilter();
        String out = "012345678901234567890123456789012345";
        filter.setOutput(out);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": undefined\r\n" + TEST_STRING + ": 012345678901234567890123456789...",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterTest() {
        StateDataFilter filter = new StateDataFilter();
        String in = "in";
        filter.setInput(in);
        String out = "out";
        filter.setOutput(out);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": in\r\n" + TEST_STRING + ": out",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterInputIsTrimmedTest() {
        StateDataFilter filter = new StateDataFilter();
        String in = "                                                                          ";
        filter.setInput(in);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": \r\n" + TEST_STRING + ": undefined",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }

    @Test
    public void stateDataFilterOutputIsTrimmedTest() {
        StateDataFilter filter = new StateDataFilter();
        String out = " ";
        filter.setOutput(out);

        assertTranslations(TEST_STRING + ":\r\n" + TEST_STRING + ": undefined\r\n" + TEST_STRING + ": ",
                           hasDataFilter.getStateDataFilter(filter),
                           "Datafilter.parameter",
                           "Datafilter.input",
                           "Datafilter.output");
    }
}
