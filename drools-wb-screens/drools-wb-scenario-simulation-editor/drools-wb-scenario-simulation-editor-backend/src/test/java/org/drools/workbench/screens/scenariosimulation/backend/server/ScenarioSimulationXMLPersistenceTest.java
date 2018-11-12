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

import org.assertj.core.api.Assertions;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.junit.Test;
import org.kie.soup.project.datamodel.imports.Import;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScenarioSimulationXMLPersistenceTest {

    ScenarioSimulationXMLPersistence instance = ScenarioSimulationXMLPersistence.getInstance();
    
    @Test
    public void noFQCNUsed() throws Exception {
        final ScenarioSimulationModel simulationModel = new ScenarioSimulationModel();
        simulationModel.getImports().addImport(new Import("org.test.Test"));

        final String xml = instance.marshal(simulationModel);

        assertFalse(xml.contains("org.drools.workbench.screens.scenariosimulation.model"));
        assertFalse(xml.contains("org.kie.soup.project.datamodel.imports"));
    }

    @Test
    public void versionAttributeExists() throws Exception {
        final String xml = instance.marshal(new ScenarioSimulationModel());
        assertTrue(xml.startsWith("<ScenarioSimulationModel version=\""+ ScenarioSimulationXMLPersistence.getCurrentVersion() + "\">"));
    }

    @Test
    public void migrateIfNecessary_1_0_to_1_1() {
        String migrated = instance.migrateIfNecessary("<ScenarioSimulationModel version=\"1.0\">\n" +
                                                       "  <simulation>\n" +
                                                       "    <simulationDescriptor>\n" +
                                                       "      <factMappings>\n" +
                                                       "        <FactMapping>\n" +
                                                       "          <expressionElements>\n" +
                                                       "            <ExpressionElement>\n" +
                                                       "              <step>lengthYears</step>\n" +
                                                       "            </ExpressionElement>\n" +
                                                       "          </expressionElements>\n" +
                                                       "          <expressionIdentifier>\n" +
                                                       "            <name>1541680933813</name>\n" +
                                                       "            <type>EXPECTED</type>\n" +
                                                       "          </expressionIdentifier>\n" +
                                                       "          <factIdentifier>\n" +
                                                       "            <name>1541680933813</name>\n" +
                                                       "            <className>mortgages.mortgages.LoanApplication</className>\n" +
                                                       "          </factIdentifier>\n" +
                                                       "          <className>java.lang.Integer</className>\n" +
                                                       "          <factAlias>LoanApplication1</factAlias>\n" +
                                                       "          <expressionAlias>lengthYears</expressionAlias>\n" +
                                                       "        </FactMapping>\n" +
                                                       "      </factMappings>\n" +
                                                       "    </simulationDescriptor>\n" +
                                                       "    <scenarios>\n" +
                                                       "      <Scenario>\n" +
                                                       "        <factMappingValues>\n" +
                                                       "          <FactMappingValue/>\n" +
                                                       "        </factMappingValues>\n" +
                                                       "        <simulationDescriptor reference=\"../../../simulationDescriptor\"/>\n" +
                                                       "      </Scenario>\n" +
                                                       "    </scenarios>\n" +
                                                       "  </simulation>\n" +
                                                       "  <imports>\n" +
                                                       "    <imports/>\n" +
                                                       "  </imports>\n" +
                                                       "</ScenarioSimulationModel>");
        assertTrue(migrated.contains("EXPECT"));
        assertFalse(migrated.contains("EXPECTED"));
    }

    @Test
    public void migrateIfNecessary() {
        Assertions.assertThatThrownBy(() -> instance.migrateIfNecessary("<ScenarioSimulationModel version=\"9999999999.99999999999\">"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Version 9999999999.99999999999 of the file is not supported. Current version is " + ScenarioSimulationXMLPersistence.getCurrentVersion());

        String noMigrationNeeded = "<ScenarioSimulationModel version=\"" + ScenarioSimulationXMLPersistence.getCurrentVersion() + "\">";
        String afterMigration = instance.migrateIfNecessary(noMigrationNeeded);
        assertEquals(noMigrationNeeded, afterMigration);
    }

    @Test
    public void extractVersion() {
        String version = instance.extractVersion("<ScenarioSimulationModel version=\"1.0\" version=\"1.1\">");
        assertEquals("1.0", version);
    }
}