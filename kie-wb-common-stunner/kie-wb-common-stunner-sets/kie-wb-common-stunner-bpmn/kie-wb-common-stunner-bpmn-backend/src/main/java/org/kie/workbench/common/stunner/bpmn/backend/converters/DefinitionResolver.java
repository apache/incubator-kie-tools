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

package org.kie.workbench.common.stunner.bpmn.backend.converters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.Task;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tasks.Simulations;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;

public class DefinitionResolver {

    private final Map<String, Signal> signals = new HashMap<>();
    private final Map<String, ElementParameters> elementParameters = new HashMap<>();

    public DefinitionResolver(Definitions definitions) {
        for (RootElement el : definitions.getRootElements()) {
            if (el instanceof Signal) {
                signals.put(el.getId(), (Signal) el);
            }
        }
        FeatureMap value = definitions.getRelationships().get(0).getExtensionValues().get(0).getValue();
        List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>)
                value.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true);
        Scenario scenario = bpsimExtensions.get(0).getScenario().get(0);
        for (ElementParameters parameters : scenario.getElementParameters()) {
            elementParameters.put(parameters.getElementRef(), parameters);
        }
    }

    public Optional<Signal> resolveSignal(String id) {
        return Optional.ofNullable(signals.get(id));
    }

    public String resolveSignalName(String id) {
        return resolveSignal(id).map(Signal::getName).orElse("");
    }

    public Optional<ElementParameters> resolveSimulationParameters(String id) {
        return Optional.ofNullable(elementParameters.get(id));
    }

    public SimulationSet extractSimulationSet(String id) {
        return this.resolveSimulationParameters(id)
                .map(Simulations::simulationSet)
                .orElse(new SimulationSet());
    }

    public SimulationSet extractSimulationSet(Task task) {
        return extractSimulationSet(task.getId());
    }

    public SimulationAttributeSet extractSimulationAttributeSet(String id) {
        return this.resolveSimulationParameters(id)
                .map(Simulations::simulationAttributeSet)
                .orElse(new SimulationAttributeSet());
    }
}
