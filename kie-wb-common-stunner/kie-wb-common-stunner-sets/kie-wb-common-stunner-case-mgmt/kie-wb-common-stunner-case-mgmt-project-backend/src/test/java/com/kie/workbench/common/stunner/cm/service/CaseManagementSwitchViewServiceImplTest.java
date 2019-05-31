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

package com.kie.workbench.common.stunner.cm.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.AbstractMetadata;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.diagram.DiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.registry.factory.FactoryRegistry;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CaseManagementSwitchViewServiceImplTest {

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Instance<DefinitionSetService> definitionSetServiceInstances;

    private CaseManagementSwitchViewServiceImpl tested;

    @Before
    public void setUp() {
        tested = new CaseManagementSwitchViewServiceImpl(factoryManager, definitionSetServiceInstances);
    }

    @Test
    public void testInit() {
        final List<DefinitionSetService> definitionSetServices = new LinkedList<>();

        final DefinitionSetService definitionSetService1 = mock(DefinitionSetService.class);
        final DefinitionSetService definitionSetService2 = mock(DefinitionSetService.class);
        definitionSetServices.add(definitionSetService1);
        definitionSetServices.add(definitionSetService2);

        when(definitionSetServiceInstances.iterator()).thenReturn(definitionSetServices.iterator());
        doAnswer(invocation -> {
            final Consumer consumer = invocation.getArgumentAt(0, Consumer.class);

            Iterator var2 = definitionSetServiceInstances.iterator();

            while (var2.hasNext()) {
                Object t = var2.next();
                consumer.accept(t);
            }
            return null;
        }).when(definitionSetServiceInstances).forEach(any(Consumer.class));

        tested.init();

        assertEquals(tested.definitionSetServices.size(), definitionSetServices.size());
        tested.definitionSetServices.containsAll(definitionSetServices);
    }

    @Test
    public void testSwitchView() throws IOException {
        final DefinitionSetService definitionSetService1 = mock(DefinitionSetService.class);
        String defSetId = "defSetId";
        when(definitionSetService1.accepts(eq(defSetId))).thenReturn(true);
        tested.definitionSetServices.add(definitionSetService1);

        final DiagramMarshaller diagramMarsaller1 = mock(DiagramMarshaller.class);
        String rawData = "rawData";
        when(diagramMarsaller1.marshall(any(Diagram.class))).thenReturn(rawData);
        when(definitionSetService1.getDiagramMarshaller()).thenReturn(diagramMarsaller1);

        final DefinitionSetService definitionSetService2 = mock(DefinitionSetService.class);
        String mappedDefSetId = "mappedDefSetId";
        when(definitionSetService2.accepts(eq(mappedDefSetId))).thenReturn(true);
        tested.definitionSetServices.add(definitionSetService2);

        final DiagramMarshaller diagramMarsaller2 = mock(DiagramMarshaller.class);
        final Graph graph = mock(Graph.class);
        final DefinitionSet content = mock(DefinitionSet.class);
        when(graph.getContent()).thenReturn(content);
        when(diagramMarsaller2.unmarshall(any(Metadata.class), any(InputStream.class))).thenReturn(graph);
        when(definitionSetService2.getDiagramMarshaller()).thenReturn(diagramMarsaller2);

        final FactoryRegistry factoryRegistry = mock(FactoryRegistry.class);
        final DiagramFactory diagramFactory = mock(DiagramFactory.class);
        when(factoryRegistry.getDiagramFactory(any(String.class), any(Class.class))).thenReturn(diagramFactory);
        when(factoryManager.registry()).thenReturn(factoryRegistry);

        final ProjectDiagram projectDiagram = mock(ProjectDiagram.class);
        final ProjectMetadata projectMetadata = mock(ProjectMetadata.class);
        when(projectDiagram.getMetadata()).thenReturn(projectMetadata);
        when(diagramFactory.build(any(String.class), any(Metadata.class), any(Graph.class))).thenReturn(projectDiagram);

        final Diagram diagram = mock(Diagram.class);
        final AbstractMetadata metadata = mock(AbstractMetadata.class);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getDefinitionSetId()).thenReturn(defSetId);

        String mappedShapeSetId = "mappedShapeSetId";

        Optional<ProjectDiagram> result = tested.switchView(diagram, mappedDefSetId, mappedShapeSetId);

        assertTrue(result.filter(d -> projectDiagram.equals(d)).isPresent());

        verify(metadata).setDefinitionSetId(eq(mappedDefSetId));
        verify(projectMetadata).setShapeSetId(eq(mappedShapeSetId));
    }
}