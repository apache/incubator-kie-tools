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
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunListener;
import org.uberfire.backend.vfs.Path;

public class JunitRunnerHelper {

    public static Result runWithJunit(Path path,
                                      Runner runner,
                                      List<Failure> failures,
                                      List<Failure> failureDetails) {
        JUnitCore jUnitCore = new JUnitCore();

        jUnitCore.addListener(new RunListener() {
            @Override
            public void testAssumptionFailure(org.junit.runner.notification.Failure failure) {
                failureDetails.add(failureToFailure(path,failure));
            }
        });

        Result result = jUnitCore.run(runner);
        failures.addAll(failuresToFailures(path, result.getFailures()));

        return result;
    }

    static List<org.guvnor.common.services.shared.test.Failure> failuresToFailures(final Path path,
                                                                                   final List<org.junit.runner.notification.Failure> failures) {
        List<org.guvnor.common.services.shared.test.Failure> result = new ArrayList<>();

        for (org.junit.runner.notification.Failure failure : failures) {


            result.add(failureToFailure(path, failure));
        }

        return result;
    }

    static org.guvnor.common.services.shared.test.Failure failureToFailure(final Path path,
                                                                           final org.junit.runner.notification.Failure failure) {
        return new org.guvnor.common.services.shared.test.Failure(
                getScenarioName(failure),
                failure.getMessage(),
                path);
    }

    static String getScenarioName(final org.junit.runner.notification.Failure failure) {
        return failure.getDescription().getDisplayName();
    }
}
