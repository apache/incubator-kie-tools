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

package org.kie.workbench.common.stunner.bpmn.client.workitem;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionCacheRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionMetadataRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionRegistry;
import org.kie.workbench.common.stunner.bpmn.workitem.WorkItemDefinitionService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mocks.CallerMock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class WorkItemDefinitionClientRegistryTest {

    private static final String SESSION_ID = "session1";
    private static final String WID_ID = "id1";
    private static final String WID_ICON_DATA = "iconData1";
    private static final WorkItemDefinition DEF = new WorkItemDefinition()
            .setName(WID_ID)
            .setIconData(WID_ICON_DATA);

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path root;

    @Mock
    private WorkItemDefinitionService service;

    @Mock
    private WorkItemDefinitionCacheRegistry registry;

    @Mock
    private Consumer<WorkItemDefinitionCacheRegistry> registryInstanceDestroyer;

    private WorkItemDefinitionClientRegistry tested;

    @Before
    @SuppressWarnings("unchecked")
    public void init() {
        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getSessionUUID()).thenReturn(SESSION_ID);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getRoot()).thenReturn(root);
        when(service.search(eq(root))).thenReturn(Collections.singleton(DEF));
        when(registry.items()).thenReturn(Collections.singleton(DEF));
        when(registry.get(eq(WID_ID))).thenReturn(DEF);
        CallerMock<WorkItemDefinitionService> serviceCaller = new CallerMock<>(service);
        WorkItemDefinitionMetadataRegistry metadataRegistry = new WorkItemDefinitionMetadataRegistry();
        this.tested = new WorkItemDefinitionClientRegistry(sessionManager,
                                                           serviceCaller,
                                                           () -> registry,
                                                           registryInstanceDestroyer,
                                                           metadataRegistry);
        this.tested.init();
    }

    @Test
    public void testRegistryProducer() {
        WorkItemDefinitionRegistry registry = tested.getRegistry();
        assertEquals(this.registry, registry);
    }

    @Test
    public void testItems() {
        Collection<WorkItemDefinition> items = tested.items();
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals(DEF, items.iterator().next());
    }

    @Test
    public void testGetItem() {
        assertEquals(DEF, tested.get(WID_ID));
    }

    @Test
    public void testLoad() {
        Command callback = mock(Command.class);
        tested.load(session,
                    metadata,
                    callback);
        verify(registry, times(1)).register(eq(DEF));
        verify(callback, times(1)).execute();
    }

    @Test
    public void testOnSessionDestroyed() {
        tested.load(session,
                    metadata,
                    () -> {
                    });
        tested.onSessionDestroyed(new SessionDestroyedEvent(SESSION_ID,
                                                            "name1",
                                                            "graphUUID",
                                                            metadata));
        verify(registry, times(1)).clear();
        verify(registryInstanceDestroyer, times(1)).accept(eq(registry));
    }

    @Test
    public void testDestroy() {
        tested.load(session,
                    metadata,
                    () -> {
                    });
        tested.destroy();
        verify(registry, times(1)).clear();
        verify(registryInstanceDestroyer, times(1)).accept(eq(registry));
    }
}
