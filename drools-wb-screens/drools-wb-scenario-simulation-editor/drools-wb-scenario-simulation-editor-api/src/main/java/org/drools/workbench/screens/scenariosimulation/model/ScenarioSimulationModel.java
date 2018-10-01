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

import java.util.stream.IntStream;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.soup.project.datamodel.imports.Imports;

@Portable
public class ScenarioSimulationModel
        implements HasImports {

    @XStreamAsAttribute()
    private String version = "1.0";

    private Simulation simulation;

    private Imports imports = new Imports();

    public ScenarioSimulationModel() {
        simulation = new Simulation();
        SimulationDescriptor simulationDescriptor = simulation.getSimulationDescriptor();

        simulationDescriptor.addFactMapping(FactIdentifier.INDEX.getName(), FactIdentifier.INDEX, ExpressionIdentifier.INDEX);
        simulationDescriptor.addFactMapping(FactIdentifier.DESCRIPTION.getName(), FactIdentifier.DESCRIPTION, ExpressionIdentifier.DESCRIPTION);

        Scenario scenario = simulation.addScenario();
        int row = simulation.getUnmodifiableScenarios().indexOf(scenario);
        scenario.setDescription(FactMappingValue.getPlaceHolder(0, 1));

        // Add GIVEN Facts
        IntStream.range(2, 4).forEach(id -> {
            ExpressionIdentifier givenExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.GIVEN);
            FactIdentifier givenFact = FactIdentifier.create(FactMappingType.GIVEN + "FACT-" + id, String.class.getCanonicalName());
            simulationDescriptor.addFactMapping(FactMapping.getPlaceHolder(FactMappingType.GIVEN, id), givenFact, givenExpression);
            scenario.addMappingValue(givenFact, givenExpression, FactMappingValue.getPlaceHolder(row, id));
        });

        // Add EXPECTED Facts
        IntStream.range(2, 4).forEach(id -> {
            id += 2; // This is to have consistent labels/names even when adding columns at runtime
            ExpressionIdentifier expectedExpression = ExpressionIdentifier.create(row + "|" + id, FactMappingType.EXPECTED);
            FactIdentifier expectFact = FactIdentifier.create(FactMappingType.EXPECTED + "FACT-" + id, String.class.getCanonicalName());
            simulationDescriptor.addFactMapping(FactMapping.getPlaceHolder(FactMappingType.EXPECTED, id), expectFact, expectedExpression);
            scenario.addMappingValue(expectFact, expectedExpression, FactMappingValue.getPlaceHolder(row, id));
        });
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
