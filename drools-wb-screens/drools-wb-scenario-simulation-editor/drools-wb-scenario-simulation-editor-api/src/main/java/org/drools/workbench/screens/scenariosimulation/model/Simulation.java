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
package org.drools.workbench.screens.scenariosimulation.model;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;

/**
 * Envelop class that wrap the definition of the simulation and the values of the scenarios
 */
@Portable
public class Simulation {

    /**
     * Describes structure of the simulation
     */
    private final SimulationDescriptor simulationDescriptor = new SimulationDescriptor();
    /**
     * Contains list of scenarios to test
     */
    private final List<Scenario> scenarios = new LinkedList<>();

    public List<Scenario> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    public SimulationDescriptor getSimulationDescriptor() {
        return simulationDescriptor;
    }

    public Scenario getScenarioByIndex(int index) {
        return scenarios.get(index);
    }

    public Scenario addScenario() {
        Scenario scenario = new Scenario(simulationDescriptor);
        scenarios.add(scenario);
        return scenario;
    }

    public void clear() {
        simulationDescriptor.clear();
        scenarios.clear();
    }

    public void sort() {
        scenarios.forEach(Scenario::sort);
    }
}