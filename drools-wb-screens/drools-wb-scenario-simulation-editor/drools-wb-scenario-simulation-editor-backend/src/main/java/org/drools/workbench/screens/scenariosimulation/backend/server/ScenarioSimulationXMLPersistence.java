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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingType;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValueOperator;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.soup.commons.xstream.XStreamUtils;
import org.kie.soup.project.datamodel.imports.Import;

public class ScenarioSimulationXMLPersistence {

    private XStream xt;
    private static final ScenarioSimulationXMLPersistence INSTANCE = new ScenarioSimulationXMLPersistence();

    private ScenarioSimulationXMLPersistence() {
        xt = XStreamUtils.createTrustingXStream(new DomDriver());

        xt.autodetectAnnotations(true);

        xt.alias("ExpressionElement", ExpressionElement.class);
        xt.alias("ExpressionIdentifier", ExpressionIdentifier.class);
        xt.alias("FactIdentifier", FactIdentifier.class);
        xt.alias("FactMapping", FactMapping.class);
        xt.alias("FactMappingType", FactMappingType.class);
        xt.alias("FactMappingValue", FactMappingValue.class);
        xt.alias("FactMappingValueOperator", FactMappingValueOperator.class);
        xt.alias("Scenario", Scenario.class);
        xt.alias("ScenarioSimulationModel", ScenarioSimulationModel.class);
        xt.alias("ScenarioSimulationModelContent", ScenarioSimulationModelContent.class);
        xt.alias("Simulation", Simulation.class);
        xt.alias("SimulationDescriptor", SimulationDescriptor.class);

        xt.alias("Import", Import.class);
    }

    public static ScenarioSimulationXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(final ScenarioSimulationModel sc) {
        return xt.toXML(sc);
    }

    public ScenarioSimulationModel unmarshal(final String xml) {
        if (xml == null) {
            return new ScenarioSimulationModel();
        }
        if (xml.trim().equals("")) {
            return new ScenarioSimulationModel();
        }
        Object o = xt.fromXML(xml);

        return (ScenarioSimulationModel) o;
    }
}
