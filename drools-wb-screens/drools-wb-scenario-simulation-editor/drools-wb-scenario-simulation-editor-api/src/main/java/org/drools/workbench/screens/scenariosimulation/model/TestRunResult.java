/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.model;

import java.util.Map;

import org.guvnor.common.services.shared.test.TestResultMessage;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public class TestRunResult {

    private final Map<Integer, Scenario> map;

    private final TestResultMessage testResultMessage;

    public TestRunResult(@MapsTo("map") Map<Integer, Scenario> map,
                         @MapsTo("testResultMessage") TestResultMessage testResultMessage) {
        this.map = map;
        this.testResultMessage = testResultMessage;
    }

    public Map<Integer, Scenario> getMap() {
        return map;
    }

    public TestResultMessage getTestResultMessage() {
        return testResultMessage;
    }
}
