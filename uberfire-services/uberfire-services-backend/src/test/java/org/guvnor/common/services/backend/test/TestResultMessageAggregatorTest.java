/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.common.services.backend.test;

import java.util.ArrayList;

import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestResultMessageAggregatorTest {

    @Test
    public void testEmpty() {
        final TestResultMessageAggregator testResultMessageAggregator = new TestResultMessageAggregator();

        assertEquals(0, testResultMessageAggregator.getRunCountSum());
        assertEquals(0L, testResultMessageAggregator.getRuntimeSum());
        assertEquals(0, testResultMessageAggregator.getFailures().size());
        assertEquals(0, testResultMessageAggregator.getSummary("test").getRunCount());
        assertEquals(0L, testResultMessageAggregator.getSummary("test").getRunTime());
        assertEquals(0, testResultMessageAggregator.getSummary("test").getFailures().size());
    }

    @Test
    public void testSum() {
        final TestResultMessageAggregator testResultMessageAggregator = new TestResultMessageAggregator();
        testResultMessageAggregator.add(getTestResultMessage(1, 1L, new Failure()));
        testResultMessageAggregator.add(getTestResultMessage(2, 2L, new Failure(), new Failure()));
        testResultMessageAggregator.add(getTestResultMessage(3, 3L, new Failure(), new Failure(), new Failure()));

        assertEquals(6, testResultMessageAggregator.getRunCountSum());
        assertEquals(6L, testResultMessageAggregator.getRuntimeSum());
        assertEquals(6, testResultMessageAggregator.getFailures().size());
        assertEquals(6, testResultMessageAggregator.getSummary("test").getRunCount());
        assertEquals(6L, testResultMessageAggregator.getSummary("test").getRunTime());
        assertEquals(6, testResultMessageAggregator.getSummary("test").getFailures().size());
    }

    private TestResultMessage getTestResultMessage(final int runCount,
                                                   final long runTime,
                                                   final Failure... failures) {
        final ArrayList<Failure> failuresList = new ArrayList<>();
        for (final Failure failure : failures) {
            failuresList.add(failure);
        }

        return new TestResultMessage("test",
                                     runCount,
                                     runTime,
                                     failuresList);
    }
}