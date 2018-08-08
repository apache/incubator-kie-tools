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

import java.util.HashMap;
import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.project.datamodel.imports.HasImports;
import org.kie.soup.project.datamodel.imports.Imports;

@Portable
public class ScenarioSimulationModel
        implements HasImports {

    private Simulation simulation;

    /**
     * Map of Header columns: key is the column number, value is the column text
     */
    private Map<Integer, String> headersMap;
    /**
     * Map of rows; Key is the row number, value is a Map itself where the key is the column number and the value is the cell text
     */
    private Map<Integer, Map<Integer, String>> rowsMap;

    private Imports imports = new Imports();

    public ScenarioSimulationModel() {
        headersMap = new HashMap<>();
        // DEFAULT HEADERS -TO CHANGE
        headersMap.put(0, "T");
        headersMap.put(1, "");
        headersMap.put(2, "Expression");
        rowsMap = new HashMap<>();
        simulation = new Simulation();
    }

    public ScenarioSimulationModel(final Map<Integer, String> headersMap, final Map<Integer, Map<Integer, String>> rowsMap) {
        this.headersMap = headersMap;
        this.rowsMap = rowsMap;
    }

    public Map<Integer, String> getHeadersMap() {
        return headersMap;
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
    public Map<Integer, Map<Integer, String>> getRowsMap() {
        return rowsMap;
    }
}
