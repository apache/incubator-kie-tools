/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.kie.workbench.common.stunner.core.client.i18n;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.j2cl.tools.di.ui.translation.client.TranslationService;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.validation.DiagramElementNameProvider;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ClientTranslationServiceTest {

    public static final String UUID = "uuid";
    public static final String NAME = "name";

    private ClientTranslationService tested;

    @Mock
    private ManagedInstance<DiagramElementNameProvider> elementNameProviders;

    @Mock
    private DiagramElementNameProvider diagramElementNameProvider;

    @Mock
    private DefinitionUtils definitionUtils;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private TranslationService translationService;

    @Mock
    private ClientSession session;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Graph graph;

    @Mock
    private Node node;

    @Mock
    private Definition definition;

    @Mock
    private Object content;

    @Before
    public void setUp() throws Exception {

        final String definitionSetId = "definitionSetId";

        when(sessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(definitionSetId);
        when(graph.getNode(UUID)).thenReturn(node);
        when(node.getContent()).thenReturn(definition);
        when(definition.getDefinition()).thenReturn(content);
        when(elementNameProviders.spliterator()).thenReturn(singletonList(diagramElementNameProvider).spliterator());
        when(diagramElementNameProvider.getElementName(UUID)).thenReturn(Optional.of(NAME));
        when(diagramElementNameProvider.getElementName("unknown")).thenReturn(Optional.empty());
        when(diagramElementNameProvider.getDefinitionSetId()).thenReturn(definitionSetId);

        tested = new ClientTranslationService(translationService, elementNameProviders, sessionManager, definitionUtils);
    }

    @Test
    public void getElementName() {
        final Optional<String> elementName = tested.getElementName(UUID);
        assertTrue(elementName.isPresent());
        assertEquals(NAME, elementName.get());
    }

    @Test
    public void getElementNameNull() {
        final Optional<String> elementName = tested.getElementName("unknown");
        assertFalse(elementName.isPresent());
    }
}
