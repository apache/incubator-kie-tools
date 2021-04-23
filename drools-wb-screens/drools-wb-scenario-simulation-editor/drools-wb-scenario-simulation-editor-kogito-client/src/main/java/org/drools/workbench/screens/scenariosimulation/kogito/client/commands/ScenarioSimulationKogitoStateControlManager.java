/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.kogito.client.commands;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.appformer.kogito.bridge.client.interop.WindowRef;
import org.appformer.kogito.bridge.client.stateControl.interop.StateControl;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioCommandRegistryManager;
import org.drools.workbench.screens.scenariosimulation.client.commands.ScenarioSimulationContext;

@Dependent
public class ScenarioSimulationKogitoStateControlManager {

    protected Supplier<Boolean> envelopeAvailableSupplier;
    protected Supplier<StateControl> stateControlSupplier;

    @Inject
    public ScenarioSimulationKogitoStateControlManager() {
         envelopeAvailableSupplier = WindowRef::isEnvelopeAvailable;
         stateControlSupplier = StateControl::get;
    }

    public void initialize(final ScenarioCommandRegistryManager scenarioCommandRegistryManager,
                           final ScenarioSimulationContext scenarioSimulationContext) {
        if (isEnvelopeAvailable()) {
            stateControlSupplier.get().setUndoCommand(() -> scenarioCommandRegistryManager.undo(scenarioSimulationContext));
            stateControlSupplier.get().setRedoCommand(() -> scenarioCommandRegistryManager.redo(scenarioSimulationContext));
        }
    }

    private boolean isEnvelopeAvailable() {
        return envelopeAvailableSupplier.get();
    }
}
