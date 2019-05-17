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

package org.kie.workbench.common.stunner.project.client.service;

import java.util.Optional;

import org.guvnor.common.services.project.model.Package;
import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.service.AbstractClientDiagramServiceTest;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.service.DiagramLookupService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.mocks.CallerMock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClientProjectDiagramServiceTest extends AbstractClientDiagramServiceTest<ProjectMetadata, ProjectDiagram, ProjectDiagramService, ClientProjectDiagramService> {

    @Override
    protected ProjectMetadata makeTestMetadata() {
        return mock(ProjectMetadata.class);
    }

    @Override
    protected ProjectDiagram makeTestDiagram() {
        return mock(ProjectDiagram.class);
    }

    @Override
    protected ProjectDiagramService makeTestDiagramService() {
        return mock(ProjectDiagramService.class);
    }

    @Override
    protected ClientProjectDiagramService makeTestClientDiagramService() {
        final Caller<ProjectDiagramService> diagramServiceCaller = new CallerMock<>(diagramService);
        final Caller<DiagramLookupService> diagramLookupServiceCaller = new CallerMock<>(diagramLookupService);
        return new ClientProjectDiagramService(shapeManager,
                                               diagramServiceCaller,
                                               diagramLookupServiceCaller,
                                               saveDiagramSessionCommandExecutedEventEvent);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateWithProjectDetails() {
        final String name = "d1";
        final String defSetId = "id1";
        final String projName = "project-name";
        final Package projPackage = mock(Package.class);
        final ServiceCallback<Path> callback = mock(ServiceCallback.class);
        final Optional<String> projectType = Optional.of("type");

        tested.create(path,
                      name,
                      defSetId,
                      projName,
                      projPackage,
                      projectType,
                      callback);

        verify(diagramService,
               times(1)).create(eq(path),
                                eq(name),
                                eq(defSetId),
                                eq(projName),
                                eq(projPackage),
                                eq(projectType));
        verify(callback,
               times(1)).onSuccess(any(Path.class));
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSaveAsXml() {
        final String xml = "xml";
        final String comment = "comment";
        final Metadata metadata = mock(Metadata.class);
        final ServiceCallback<String> callback = mock(ServiceCallback.class);

        tested.saveAsXml(path,
                         xml,
                         metadata,
                         comment,
                         callback);

        verify(diagramService,
               times(1)).saveAsXml(eq(path),
                                   eq(xml),
                                   eq(metadata),
                                   eq(comment));
        verify(callback,
               times(1)).onSuccess(anyString());
        verify(callback,
               times(0)).onError(any(ClientRuntimeError.class));
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testDuplicatedBusinessProcess() {
        final String name = "d1";
        final String defSetId = "id1";
        final String projName = "project-name";
        final Package projPackage = new Package();
        final Optional<String> projectType = Optional.of("type");

        ProjectDiagramService projectDiagramService = mock(ProjectDiagramService.class);

        doThrow(new FileAlreadyExistsException(path.toString())).when(projectDiagramService).create(path,
                                                                                                    name,
                                                                                                    defSetId,
                                                                                                    projName,
                                                                                                    projPackage,
                                                                                                    projectType);
        exception.expect(FileAlreadyExistsException.class);
        projectDiagramService.create(path,
                                     name,
                                     defSetId,
                                     projName,
                                     projPackage,
                                     projectType);
    }
}
