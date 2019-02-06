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
package org.drools.workbench.screens.scenariosimulation.client.editor.strategies;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;

/**
 * Abstract class to provide common methods to be used by actual implementations.
 */
public abstract class AbstractDataManagementStrategy implements DataManagementStrategy {

    protected ScenarioSimulationModel model;

    @Override
    public void setModel(ScenarioSimulationModel model) {
        this.model = model;
    }

    protected static FactModelTree getSimpleClassFactModelTree(Class clazz) {
        String key = clazz.getSimpleName();
        Map<String, String> simpleProperties = new HashMap<>();
        String fullName = clazz.getCanonicalName();
        simpleProperties.put("value", fullName);
        String packageName = fullName.substring(0, fullName.lastIndexOf("."));
        return new FactModelTree(key, packageName, simpleProperties, new HashMap<>());
    }


}
