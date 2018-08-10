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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.soup.project.datamodel.imports.Imports;

@Portable
public class ScenarioSimulationModel
        implements HasImports {

    private Simulation simulation;

    private Imports imports = new Imports();

    public ScenarioSimulationModel() {
        simulation = new Simulation();
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();

        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);

        FactIdentifier givenFact = FactIdentifier.create("GIVEN", String.class.getCanonicalName());
        FactIdentifier expectFact = FactIdentifier.create("EXPECT", String.class.getCanonicalName());

        ExpressionIdentifier givenExpression = ExpressionIdentifier.create("GIVEN", FactMappingType.GIVEN);
        ExpressionIdentifier expectedExpression = ExpressionIdentifier.create("EXPECTED", FactMappingType.EXPECTED);

        simulationDescriptor.addFactMapping(givenFact, givenExpression);
        simulationDescriptor.addFactMapping(expectFact, expectedExpression);

        Scenario scenario = simulation.addScenario();
        scenario.setDescription("Scenario example");
        scenario.addMappingValue(givenFact, givenExpression, "sample");
        scenario.addMappingValue(expectFact, expectedExpression, "sample");
    }

    public ScenarioSimulationModel(Simulation simulation) {
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Imports getImports() {
        return imports;
    }

    @Override
    public void setImports(Imports imports) {
        this.imports = imports;
    }
}
