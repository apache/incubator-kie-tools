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

import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;

public class ScenarioResult {

    private final FactIdentifier factIdentifier;
    private final FactMappingValue factMappingValue;
    private final Object resultValue;
    private final Boolean result;

    public ScenarioResult(FactIdentifier factIdentifier, FactMappingValue factMappingValue, Object resultValue, Boolean result) {
        this.factIdentifier = factIdentifier;
        this.factMappingValue = factMappingValue;
        this.resultValue = resultValue;
        this.result = result;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public FactMappingValue getFactMappingValue() {
        return factMappingValue;
    }

    public Object getResultValue() {
        return resultValue;
    }

    public Boolean getResult() {
        return result;
    }
}
