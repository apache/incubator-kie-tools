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

package org.kie.workbench.common.stunner.project.backend.service;

import java.io.IOException;

import org.guvnor.common.services.project.model.Package;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramServiceTest;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.DeleteOption;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectDiagramServiceControllerTest
        extends AbstractVFSDiagramServiceTest<ProjectMetadata, ProjectDiagram> {

    @Mock
    private KieModuleService moduleService;

    @Mock
    private KieModule kieModule;

    @Mock
    private Package kiePackage;

    @Mock
    private Path rootModulePath;

    @Before
    public void setUp() throws IOException {
        super.setUp();
        when(moduleService.resolveModule(any(Path.class))).thenReturn(kieModule);
        when(moduleService.resolvePackage(any(Path.class))).thenReturn(kiePackage);
        when(kieModule.getModuleName()).thenReturn("kieModule1");
        when(kiePackage.getModuleRootPath()).thenReturn(rootModulePath);
    }

    @Override
    public AbstractVFSDiagramService<ProjectMetadata, ProjectDiagram> createVFSDiagramService() {
        ProjectDiagramServiceController service = new ProjectDiagramServiceController(definitionManager,
                                                                                      factoryManager,
                                                                                      definitionSetServiceInstances,
                                                                                      ioService,
                                                                                      registryFactory,
                                                                                      moduleService);
        service.initialize();
        return service;
    }

    @Override
    public Class<? extends Metadata> getMetadataType() {
        return ((ProjectDiagramServiceController) diagramService).getMetadataType();
    }

    @Override
    public ProjectDiagram mockDiagram() {
        return mock(ProjectDiagram.class);
    }

    public ProjectMetadata mockMetadata() {
        return mock(ProjectMetadata.class);
    }

    @Test
    public void testSaveOrUpdate() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(FILE_URI);
        when(metadata.getPath()).thenReturn(path);
        org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        diagramService.saveOrUpdate(diagram);

        verify(ioService,
               times(1)).write(expectedNioPath,
                               DIAGRAM_MARSHALLED);
    }

    @Test
    public void testDelete() {
        Path path = mock(Path.class);
        when(path.toURI()).thenReturn(FILE_URI);
        when(metadata.getPath()).thenReturn(path);
        org.uberfire.java.nio.file.Path expectedNioPath = Paths.convert(path);

        diagramService.delete(diagram);

        verify(ioService,
               times(1)).deleteIfExists(eq(expectedNioPath),
                                        any(DeleteOption.class));
    }
}
