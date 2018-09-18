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
package org.drools.workbench.screens.scenariosimulation.backend.server;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScenarioSimulationXMLPersistenceTest {

    @Test
    public void noFQCNUsed() throws Exception {
        final ScenarioSimulationModel simulationModel = new ScenarioSimulationModel();
        simulationModel.getImports().addImport(new Import("org.test.Test"));

        final String xml = ScenarioSimulationXMLPersistence.getInstance().marshal(simulationModel);

        assertFalse(xml.contains("org.drools.workbench.screens.scenariosimulation.model"));
        assertFalse(xml.contains("org.kie.soup.project.datamodel.imports"));
    }

    @Test
    public void versionAttributeExists() throws Exception {
        final String xml = ScenarioSimulationXMLPersistence.getInstance().marshal(new ScenarioSimulationModel());
        assertTrue(xml.startsWith("<ScenarioSimulationModel version=\"1.0\">"));
    }
}