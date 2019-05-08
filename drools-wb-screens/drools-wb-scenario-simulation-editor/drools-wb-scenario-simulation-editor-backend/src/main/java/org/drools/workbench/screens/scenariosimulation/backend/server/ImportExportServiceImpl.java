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

package org.drools.workbench.screens.scenariosimulation.backend.server;

import javax.enterprise.context.ApplicationScoped;

import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.backend.server.importexport.ScenarioCsvImportExport;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportService;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.guvnor.common.services.backend.exceptions.ExceptionUtilities;
import org.jboss.errai.bus.server.annotations.Service;

@Service
@ApplicationScoped
public class ImportExportServiceImpl implements ImportExportService {

    protected ScenarioCsvImportExport scenarioCsvImportExport = new ScenarioCsvImportExport();

    @Override
    public Object exportSimulation(ImportExportType type, Simulation simulation) {
        try {
            switch (type) {
                case CSV:
                    return scenarioCsvImportExport.exportData(simulation);
                default:
                    throw new IllegalArgumentException("Impossible to parse " + type);
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }

    @Override
    public Simulation importSimulation(ImportExportType type, Object raw, Simulation originalSimulation) {
        try {
            switch (type) {
                case CSV:
                    return scenarioCsvImportExport.importData((String) raw, originalSimulation);
                default:
                    throw new IllegalArgumentException("Impossible to parse " + type);
            }
        } catch (Exception e) {
            throw ExceptionUtilities.handleException(e);
        }
    }
}