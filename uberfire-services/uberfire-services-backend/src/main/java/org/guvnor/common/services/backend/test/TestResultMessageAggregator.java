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
import java.util.List;

import org.guvnor.common.services.shared.test.Failure;
import org.guvnor.common.services.shared.test.TestResultMessage;

public class TestResultMessageAggregator {

    private int runCount = 0;
    private Long runTime = 0L;
    private List<Failure> failures = new ArrayList<Failure>();

    public void add(TestResultMessage testResultMessage) {
        runCount += testResultMessage.getRunCount();
        runTime += testResultMessage.getRunTime();
        failures.addAll(testResultMessage.getFailures());
    }

    public int getRunCountSum() {
        return runCount;
    }

    public long getRuntimeSum() {
        return runTime;
    }

    public List<Failure> getFailures() {
        return failures;
    }

    public TestResultMessage getSummary(final String identifier) {
        return new TestResultMessage(
                identifier,
                getRunCountSum(),
                getRuntimeSum(),
                getFailures());
    }
}
