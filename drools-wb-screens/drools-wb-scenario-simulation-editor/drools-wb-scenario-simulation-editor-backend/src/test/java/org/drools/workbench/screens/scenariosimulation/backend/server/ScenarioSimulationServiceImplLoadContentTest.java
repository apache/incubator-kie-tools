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

import javax.enterprise.event.Event;

import org.drools.scenariosimulation.api.model.Background;
import org.drools.scenariosimulation.api.model.ScenarioSimulationModel;
import org.drools.scenariosimulation.api.model.Settings;
import org.drools.scenariosimulation.api.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModelContent;
import org.drools.workbench.screens.scenariosimulation.service.DMNTypeService;
import org.guvnor.common.services.backend.config.SafeSessionInfo;
import org.guvnor.common.services.shared.metadata.model.Overview;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.PackageDataModelOracle;
import org.kie.workbench.common.services.backend.service.KieServiceOverviewLoader;
import org.kie.workbench.common.services.datamodel.backend.server.service.DataModelService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.ext.editor.commons.backend.version.PathResolver;
import org.uberfire.io.IOService;
import org.uberfire.workbench.events.ResourceOpenedEvent;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScenarioSimulationServiceImplLoadContentTest {

    @Mock
    private IOService ioService;

    @Mock
    private PathResolver pathResolver;

    @Mock
    protected KieServiceOverviewLoader overviewLoader;

    @Mock
    private Event<ResourceOpenedEvent> resourceOpenedEvent;

    @Mock
    private DataModelService dataModelService;

    @Mock
    private DMNTypeService dmnTypeServiceMock;

    @InjectMocks
    private ScenarioSimulationServiceImpl serviceSpy = spy(new ScenarioSimulationServiceImpl(mock(SafeSessionInfo.class)) {
        @Override
        protected ScenarioSimulationModel unmarshalInternal(String content) {
            Simulation simulation = new Simulation();
            Background background = new Background();
            Settings settings = new Settings();
            settings.setType(ScenarioSimulationModel.Type.DMN);
            ScenarioSimulationModel toReturn = new ScenarioSimulationModel();
            toReturn.setSimulation(simulation);
            toReturn.setBackground(background);
            toReturn.setSettings(settings);
            return toReturn;
        }
    });

    private Path path = PathFactory.newPath("contextpath", "file:///contextpath");

    @Before
    public void setUp() {
        doReturn("").when(ioService).readAllString(any());
        doReturn(false).when(pathResolver).isDotFile(any());
        doReturn(mock(Overview.class)).when(overviewLoader).loadOverview(any());
        doReturn(mock(PackageDataModelOracle.class)).when(dataModelService).getDataModel(path);
    }

    @Test
    public void loadContent() {
        final ScenarioSimulationModelContent scenarioSimulationModelContent = serviceSpy.loadContent(path);

        assertNotNull(scenarioSimulationModelContent);
        assertNotNull(scenarioSimulationModelContent.getDataModel());
        assertNotNull(scenarioSimulationModelContent.getModel());
        assertNotNull(scenarioSimulationModelContent.getOverview());

        verify(serviceSpy, times(1)).constructContent(eq(path), isA(Overview.class));
        verify(dmnTypeServiceMock, times(1)).initializeNameAndNamespace(isA(Settings.class), eq(path), any());
        verify(resourceOpenedEvent).fire(any(ResourceOpenedEvent.class));
    }
}