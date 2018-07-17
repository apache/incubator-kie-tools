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
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.kie.soup.commons.xstream.XStreamUtils;

public class ScenarioSimulationXMLPersistence {

    private XStream xt;
    private static final ScenarioSimulationXMLPersistence INSTANCE = new ScenarioSimulationXMLPersistence();

    private ScenarioSimulationXMLPersistence() {
        xt = XStreamUtils.createTrustingXStream(new DomDriver());
    }

    public static ScenarioSimulationXMLPersistence getInstance() {
        return INSTANCE;
    }

    public String marshal(ScenarioSimulationModel sc) {
        return xt.toXML(sc);
    }

    public ScenarioSimulationModel unmarshal(String xml) {
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
