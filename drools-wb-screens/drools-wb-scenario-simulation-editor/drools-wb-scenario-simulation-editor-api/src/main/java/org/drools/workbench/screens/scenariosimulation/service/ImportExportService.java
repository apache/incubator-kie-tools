/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.scenariosimulation.service;

import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.jboss.errai.bus.server.annotations.Remote;

/**
 * This interface define the service to export and import data from a scenario
 */
@Remote
public interface ImportExportService {

    /**
     * This method export the given simulation to the requested type
     * @param type
     * @param simulation
     * @return
     */
    Object exportSimulation(ImportExportType type, Simulation simulation);

    /**
     * This method parse the raw value and return a new simulation. The originalSimulation can be used to retrieve
     * some metadata not available in the export (i.e. FactMapping)
     * @param type
     * @param raw
     * @param originalSimulation
     * @return
     */
    Simulation importSimulation(ImportExportType type, Object raw, Simulation originalSimulation);
}
