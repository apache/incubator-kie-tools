/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.testscenario.backend.server;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.notification.Failure;
import org.uberfire.backend.vfs.Path;

public class ScenarioUtil {

    static List<org.guvnor.common.services.shared.test.Failure> failuresToFailures(final Path path,
                                                                                   final List<Failure> failures) {
        ArrayList<org.guvnor.common.services.shared.test.Failure> result = new ArrayList<org.guvnor.common.services.shared.test.Failure>();

        for (Failure failure : failures) {
            result.add(failureToFailure(path, failure));
        }

        return result;
    }

    static org.guvnor.common.services.shared.test.Failure failureToFailure(final Path path,
                                                                           final Failure failure) {
        return new org.guvnor.common.services.shared.test.Failure(
                getScenarioName(failure),
                failure.getMessage(),
                path);
    }

    static String getScenarioName(final Failure failure) {
        return failure.getDescription().getDisplayName().substring(0, failure.getDescription().getDisplayName().indexOf(".scenario"));
    }

    static String getKSessionName(List<String> kSessions) {
        if (kSessions == null || kSessions.isEmpty()) {
            return null;
        } else {
            return kSessions.iterator().next();
        }
    }
}
