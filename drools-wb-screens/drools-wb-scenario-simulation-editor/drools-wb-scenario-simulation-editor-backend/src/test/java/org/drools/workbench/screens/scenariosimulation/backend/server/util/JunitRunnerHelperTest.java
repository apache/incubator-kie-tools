/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.scenariosimulation.backend.server.util;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.shared.test.Failure;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.uberfire.backend.vfs.Path;

import static org.drools.workbench.screens.scenariosimulation.backend.server.util.JunitRunnerHelper.runWithJunit;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class JunitRunnerHelperTest {

    static Runner fakeRunner = new Runner() {
        private final Description desc = Description.createSuiteDescription("Fake runner");

        @Override
        public Description getDescription() {
            return desc;
        }

        @Override
        public void run(RunNotifier notifier) {
            Description childDescription = Description.createTestDescription(getClass(),
                                                                             "Test");
            desc.addChild(childDescription);
            EachTestNotifier singleNotifier = new EachTestNotifier(notifier, childDescription);
            singleNotifier.addFailedAssumption(new AssumptionViolatedException("Test 2"));
            singleNotifier.addFailedAssumption(new AssumptionViolatedException("Test 1"));
            singleNotifier.addFailure(new IllegalArgumentException("Test"));
        }
    };

    @Test
    public void runWithJunitTest() {
        List<Failure> failures = new ArrayList<>();
        List<Failure> failureDetails = new ArrayList<>();
        Path path = mock(Path.class);
        Result result = runWithJunit(path, fakeRunner, failures, failureDetails);
        assertFalse(result.wasSuccessful());
        assertEquals(1, failures.size());
        assertEquals(2, failureDetails.size());
        assertEquals("Test", failures.get(0).getMessage());
        assertEquals(path, failures.get(0).getPath());
    }
}