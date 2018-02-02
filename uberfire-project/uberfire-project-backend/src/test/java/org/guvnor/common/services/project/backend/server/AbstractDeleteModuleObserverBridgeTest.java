/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Event;

import org.guvnor.common.services.project.events.DeleteModuleEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.project.ModuleFactory;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceDeleted;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceUpdated;

import static org.mockito.Mockito.*;

public class AbstractDeleteModuleObserverBridgeTest {

    private AbstractDeleteModuleObserverBridge bridge;
    private IOService ioService;
    private ModuleFactory<Module> moduleFactory;
    private Event<DeleteModuleEvent> deleteModuleEvent;
    private SessionInfo sessionInfo = mock(SessionInfo.class);

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        ioService = mock(IOService.class);
        moduleFactory = mock(ModuleFactory.class);
        deleteModuleEvent = mock(Event.class);

        bridge = new AbstractDeleteModuleObserverBridge<Module>(ioService,
                                                                deleteModuleEvent) {
            @Override
            protected Module getModule(final org.uberfire.java.nio.file.Path path) {
                return moduleFactory.simpleModuleInstance(path);
            }
        };
    }

    @Test
    public void testResourceDeletedEventPomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("file://module1/pom.xml");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        bridge.onBatchResourceChanges(new ResourceDeletedEvent(path,
                                                               "message",
                                                               sessionInfo));

        verify(deleteModuleEvent,
               times(1)).fire(any(DeleteModuleEvent.class));
    }

    @Test
    public void testResourceDeletedEventNonPomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("cheese.drl");
        when(path.toURI()).thenReturn("file://module1/cheese.drl");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        bridge.onBatchResourceChanges(new ResourceDeletedEvent(path,
                                                               "message",
                                                               sessionInfo));

        verify(deleteModuleEvent,
               times(0)).fire(any(DeleteModuleEvent.class));
    }

    @Test
    public void testResourceBatchChangesEventUpdatePomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("file://module1/pom.xml");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        final Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>() {{
            put(path,
                new ArrayList<ResourceChange>() {{
                    add(new ResourceUpdated(""));
                }});
        }};

        bridge.onBatchResourceChanges(new ResourceBatchChangesEvent(batch,
                                                                    "message",
                                                                    sessionInfo));

        verify(deleteModuleEvent,
               times(0)).fire(any(DeleteModuleEvent.class));
    }

    @Test
    public void testResourceBatchChangesEventUpdateNonPomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("cheese.drl");
        when(path.toURI()).thenReturn("file://module1/cheese.drl");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        final Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>() {{
            put(path,
                new ArrayList<ResourceChange>() {{
                    add(new ResourceUpdated(""));
                }});
        }};

        bridge.onBatchResourceChanges(new ResourceBatchChangesEvent(batch,
                                                                    "message",
                                                                    sessionInfo));

        verify(deleteModuleEvent,
               times(0)).fire(any(DeleteModuleEvent.class));
    }

    @Test
    public void testResourceBatchChangesEventDeletePomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("pom.xml");
        when(path.toURI()).thenReturn("file://module1/pom.xml");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        final Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>() {{
            put(path,
                new ArrayList<ResourceChange>() {{
                    add(new ResourceDeleted(""));
                }});
        }};

        bridge.onBatchResourceChanges(new ResourceBatchChangesEvent(batch,
                                                                    "message",
                                                                    sessionInfo));

        verify(deleteModuleEvent,
               times(1)).fire(any(DeleteModuleEvent.class));
    }

    @Test
    public void testResourceBatchChangesEventDeleteNonPomFile() {
        final Path path = mock(Path.class);
        final org.uberfire.java.nio.file.Path nioPath = mock(org.uberfire.java.nio.file.Path.class);
        when(path.getFileName()).thenReturn("cheese.drl");
        when(path.toURI()).thenReturn("file://module1/cheese.drl");
        when(ioService.get(any(URI.class))).thenReturn(nioPath);

        final Map<Path, Collection<ResourceChange>> batch = new HashMap<Path, Collection<ResourceChange>>() {{
            put(path,
                new ArrayList<ResourceChange>() {{
                    add(new ResourceDeleted(""));
                }});
        }};

        bridge.onBatchResourceChanges(new ResourceBatchChangesEvent(batch,
                                                                    "message",
                                                                    sessionInfo));

        verify(deleteModuleEvent,
               times(0)).fire(any(DeleteModuleEvent.class));
    }
}
