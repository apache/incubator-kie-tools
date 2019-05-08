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

import java.io.IOException;

import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.backend.server.importexport.ScenarioCsvImportExport;
import org.drools.workbench.screens.scenariosimulation.service.ImportExportType;
import org.guvnor.common.services.shared.exceptions.GenericPortableException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ImportExportServiceImplTest {

    @Mock
    ScenarioCsvImportExport scenarioCsvImportExportMock;

    @Mock
    Simulation simulationMock;

    ImportExportServiceImpl importExportService;

    @Before
    public void setup() {
        this.importExportService = new ImportExportServiceImpl() {
            {
                this.scenarioCsvImportExport = scenarioCsvImportExportMock;
            }
        };
    }

    @Test
    public void exportSimulation() throws IOException {
        importExportService.exportSimulation(ImportExportType.CSV, simulationMock);
        verify(scenarioCsvImportExportMock, times(1)).exportData(eq(simulationMock));

        when(scenarioCsvImportExportMock.exportData(any())).thenThrow(new IllegalStateException());
        assertThatThrownBy(() -> importExportService
                .exportSimulation(ImportExportType.CSV, simulationMock))
                .isInstanceOf(GenericPortableException.class);
    }

    @Test
    public void importSimulation() throws IOException {
        String raw = "";
        importExportService.importSimulation(ImportExportType.CSV, raw, simulationMock);
        verify(scenarioCsvImportExportMock, times(1)).importData(eq(raw), eq(simulationMock));

        when(scenarioCsvImportExportMock.importData(anyString(), any())).thenThrow(new IllegalStateException());
        assertThatThrownBy(() -> importExportService
                .importSimulation(ImportExportType.CSV, raw, simulationMock))
                .isInstanceOf(GenericPortableException.class);
    }
}