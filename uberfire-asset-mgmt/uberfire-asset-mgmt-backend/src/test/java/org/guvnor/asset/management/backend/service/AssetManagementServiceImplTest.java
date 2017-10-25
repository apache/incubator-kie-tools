/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.asset.management.backend.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;

import org.guvnor.asset.management.model.ConfigureRepositoryEvent;
import org.guvnor.asset.management.service.AssetManagementService;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AssetManagementServiceImplTest {

    @Mock
    private Instance<ProjectService<?>> projectService;

    @Mock
    private IOService ioService;

    @Mock
    private POMService pomService;

    @Mock
    private RepositoryService repositoryService;

    private AssetManagementService assetManagementService;

    private final List<Object> receivedEvents = new ArrayList<Object>();
    private final List<Object> receivedBranchEvents = new ArrayList<Object>();

    private Event<NewBranchEvent> newBranchEvent = new EventSourceMock<NewBranchEvent>() {

        @Override
        public void fire(NewBranchEvent event) {
            receivedBranchEvents.add(event);
        }
    };

    private Event<ConfigureRepositoryEvent> configureRepositoryEvent = new EventSourceMock<ConfigureRepositoryEvent>() {

        @Override
        public void fire(ConfigureRepositoryEvent event) {
            receivedEvents.add(event);
        }
    };

    @Before
    public void setup() {
        receivedEvents.clear();

        Path path = Mockito.mock(Path.class);
        when(path.getFileName()).thenReturn(Mockito.mock(Path.class));
        when(path.toUri()).thenReturn(URI.create("dummy://test"));
        when(path.getFileSystem()).thenReturn(Mockito.mock(FileSystem.class));

        when(ioService.get(any(URI.class))).thenReturn(path);

        assetManagementService = new AssetManagementServiceImpl(newBranchEvent,
                                                                configureRepositoryEvent,
                                                                pomService,
                                                                ioService,
                                                                repositoryService,
                                                                projectService);
    }

    @Test
    public void testConfigureRepository() {

        assetManagementService.configureRepository("test-repo",
                                                   "master",
                                                   "dev",
                                                   "release",
                                                   "1.0.0");

        assertEquals(1,
                     receivedEvents.size());

        Object event = receivedEvents.get(0);

        assertTrue(event instanceof ConfigureRepositoryEvent);
        ConfigureRepositoryEvent eventReceived = (ConfigureRepositoryEvent) event;

        Map<String, Object> parameters = eventReceived.getParams();
        assertNotNull(parameters);
        assertEquals(5,
                     parameters.size());

        assertTrue(parameters.containsKey("RepositoryName"));
        assertTrue(parameters.containsKey("SourceBranchName"));
        assertTrue(parameters.containsKey("DevBranchName"));
        assertTrue(parameters.containsKey("RelBranchName"));
        assertTrue(parameters.containsKey("Version"));

        assertEquals("test-repo",
                     parameters.get("RepositoryName"));
        assertEquals("master",
                     parameters.get("SourceBranchName"));
        assertEquals("dev",
                     parameters.get("DevBranchName"));
        assertEquals("release",
                     parameters.get("RelBranchName"));
        assertEquals("1.0.0",
                     parameters.get("Version"));

        assertEquals(2,
                     receivedBranchEvents.size());

        event = receivedBranchEvents.get(0);
        assertTrue(event instanceof NewBranchEvent);

        assertEquals("test-repo",
                     ((NewBranchEvent) event).getRepositoryAlias());
        assertEquals("dev-1.0.0",
                     ((NewBranchEvent) event).getBranchName());

        event = receivedBranchEvents.get(1);
        assertTrue(event instanceof NewBranchEvent);

        assertEquals("test-repo",
                     ((NewBranchEvent) event).getRepositoryAlias());
        assertEquals("release-1.0.0",
                     ((NewBranchEvent) event).getBranchName());
    }
}
