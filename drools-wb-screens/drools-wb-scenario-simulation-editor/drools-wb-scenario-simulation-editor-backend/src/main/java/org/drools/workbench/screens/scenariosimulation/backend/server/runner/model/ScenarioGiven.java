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

import java.util.List;

import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;

/**
 * This class wrap an entire given fact. It contains factIdentifier, instance of the
 * bean and list of values (columns) used to create it
 */
public class ScenarioGiven {

    private final FactIdentifier factIdentifier;
    private final Object value;
    private final List<FactMappingValue> factMappingValues;

    public ScenarioGiven(FactIdentifier factIdentifier,
                         Object value,
                         List<FactMappingValue> factMappingValues) {
        this.factIdentifier = factIdentifier;
        this.value = value;
        this.factMappingValues = factMappingValues;
    }

    public FactIdentifier getFactIdentifier() {
        return factIdentifier;
    }

    public Object getValue() {
        return value;
    }

    public List<FactMappingValue> getFactMappingValues() {
        return factMappingValues;
    }
}
