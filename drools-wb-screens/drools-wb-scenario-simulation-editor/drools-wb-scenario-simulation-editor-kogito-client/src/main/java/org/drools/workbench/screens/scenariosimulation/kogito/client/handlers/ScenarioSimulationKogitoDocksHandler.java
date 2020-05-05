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
package org.drools.workbench.screens.scenariosimulation.kogito.client.handlers;

import javax.enterprise.context.ApplicationScoped;

import org.drools.workbench.screens.scenariosimulation.client.handlers.AbstractScenarioSimulationDocksHandler;

/**
 * Docks handler for SCESIM Kogito version. No additional docks are added in Kogito, therefore it uses inherited docks
 * implemented in the Abstract class.
 */
@ApplicationScoped
public class ScenarioSimulationKogitoDocksHandler extends AbstractScenarioSimulationDocksHandler {

    /**
     * Test result dock is not implemented in Kogito version, then an <code>UnsupportedOperationException</code> is
     * thrown if method is called in this context.
     */
    @Override
    public void expandTestResultsDock() {
        throw new UnsupportedOperationException();
    }

}
