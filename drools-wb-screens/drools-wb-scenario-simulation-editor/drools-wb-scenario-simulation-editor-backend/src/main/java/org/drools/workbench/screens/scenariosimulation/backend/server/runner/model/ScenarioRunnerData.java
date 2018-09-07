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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScenarioRunnerData {

    private final List<ScenarioInput> inputData = new ArrayList<>();
    private final List<ScenarioOutput> outputData = new ArrayList<>();
    private final List<ScenarioResult> resultData = new ArrayList<>();

    public void addInput(ScenarioInput input) {
        inputData.add(input);
    }

    public void addOutput(ScenarioOutput output) {
        outputData.add(output);
    }

    public void addResult(ScenarioResult result) {
        resultData.add(result);
    }

    public List<ScenarioInput> getInputData() {
        return Collections.unmodifiableList(inputData);
    }

    public List<ScenarioOutput> getOutputData() {
        return Collections.unmodifiableList(outputData);
    }

    public List<ScenarioResult> getResultData() {
        return Collections.unmodifiableList(resultData);
    }
}
