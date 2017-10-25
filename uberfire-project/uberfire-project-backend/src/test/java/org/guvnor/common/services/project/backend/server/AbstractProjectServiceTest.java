/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.backend.server;

import javax.enterprise.event.Event;

import org.guvnor.common.services.backend.util.CommentedOptionFactory;
import org.guvnor.common.services.project.builder.events.InvalidateDMOProjectCacheEvent;
import org.guvnor.common.services.project.events.NewPackageEvent;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.events.RenameProjectEvent;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AbstractProjectServiceTest {

    @Mock
    private IOService ioService;

    @Mock
    private POMService pomService;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private ConfigurationFactory configurationFactory;

    @Mock
    private Event<NewProjectEvent> newProjectEvent;

    @Mock
    private Event<NewPackageEvent> newPackageEvent;

    @Mock
    private Event<RenameProjectEvent> renameProjectEvent;

    @Mock
    private Event<InvalidateDMOProjectCacheEvent> invalidateDMOCache;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private BackwardCompatibleUtil backward;

    @Mock
    private CommentedOptionFactory commentedOptionFactory;

    @Mock
    private ResourceResolver resourceResolver;

    @Mock
    private Path path;

    @Mock
    private Project project;

    private AbstractProjectService abstractProjectService;

    @Before
    public void setup() {
        abstractProjectService = spy(new AbstractProjectService(ioService,
                                                                pomService,
                                                                configurationService,
                                                                configurationFactory,
                                                                newProjectEvent,
                                                                newPackageEvent,
                                                                renameProjectEvent,
                                                                invalidateDMOCache,
                                                                sessionInfo,
                                                                authorizationManager,
                                                                backward,
                                                                commentedOptionFactory,
                                                                resourceResolver) {
            @Override
            public Object newProject(final org.uberfire.backend.vfs.Path repositoryRoot,
                                     final POM pom,
                                     final String baseURL) {
                return null;
            }

            @Override
            public Object newProject(final org.uberfire.backend.vfs.Path repositoryRoot,
                                     final POM pom,
                                     final String baseURL,
                                     final DeploymentMode mode) {
                return null;
            }

            @Override
            public Project simpleProjectInstance(final org.uberfire.java.nio.file.Path parent) {
                return null;
            }
        });
    }

    @Test
    public void testReImport() throws Exception {
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("file://project1/pom.xml");
        when(resourceResolver.resolveProject(any(Path.class))).thenReturn(project);

        abstractProjectService.reImport(path);

        verify(invalidateDMOCache).fire(any(InvalidateDMOProjectCacheEvent.class));
    }

    @Test
    public void testRenameEventFiredBeforeDeleteEvent() {
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("file://project1/pom.xml");
        when(resourceResolver.resolveProject(any(Path.class))).thenReturn(project);
        when(pomService.load(any())).thenReturn(mock(POM.class));

        final InOrder inOrder = inOrder(renameProjectEvent,
                                        ioService);

        abstractProjectService.rename(path,
                                      "newName",
                                      "comment");

        inOrder.verify(renameProjectEvent).fire(any());
        inOrder.verify(ioService).endBatch();
    }
}