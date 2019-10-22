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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram.marshalling;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.Definitions;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.BPMNTestDefinitionFactory;
import org.kie.workbench.common.stunner.bpmn.WorkItemDefinitionMockRegistry;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.DefinitionsConverter;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.morph.BaseTaskMorphPropertyDefinition;
import org.kie.workbench.common.stunner.bpmn.workitem.service.WorkItemDefinitionLookupService;
import org.kie.workbench.common.stunner.core.backend.StunnerTestingGraphBackendAPI;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.bind.BackendBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.mockito.Mock;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BPMNDiagramMarshallerBaseTest {

    @Mock
    private CloneManager cloneManager;

    protected StunnerTestingGraphBackendAPI api;
    protected WorkItemDefinitionMockRegistry workItemDefinitionMockRegistry;
    protected BPMNDirectDiagramMarshaller marshaller;

    @SuppressWarnings("unchecked")
    protected void init() {
        initMocks(this);

        cloneManager = mock(CloneManager.class);

        try {
            api = StunnerTestingGraphBackendAPI.build(BPMNDefinitionSet.class,
                                                      new BPMNTestDefinitionFactory());
        } catch (Exception e) {
            fail("Cannot instantiate DefinitionSet [message=" + e.getMessage() + "]");
        }

        BackendBindableMorphAdapter<Object> morphAdapter =
                new BackendBindableMorphAdapter(api.getDefinitionUtils(),
                                                api.getFactoryManager(),
                                                cloneManager,
                                                singletonList(new BaseTaskMorphPropertyDefinition()));
        doReturn(morphAdapter).when(api.getAdapterRegistry()).getMorphAdapter(eq(UserTask.class));
        doReturn(morphAdapter).when(api.getAdapterRegistry()).getMorphAdapter(eq(NoneTask.class));
        doReturn(morphAdapter).when(api.getAdapterRegistry()).getMorphAdapter(eq(ScriptTask.class));
        doReturn(morphAdapter).when(api.getAdapterRegistry()).getMorphAdapter(eq(BusinessRuleTask.class));

        workItemDefinitionMockRegistry = new WorkItemDefinitionMockRegistry();
        WorkItemDefinitionLookupService widService = mock(WorkItemDefinitionLookupService.class);
        when(widService.execute(any(Metadata.class))).thenReturn(workItemDefinitionMockRegistry.items());

        marshaller = new BPMNDirectDiagramMarshaller(
                new XMLEncoderDiagramMetadataMarshaller(),
                api.getDefinitionManager(),
                api.getRuleManager(),
                widService,
                api.getFactoryManager(),
                api.commandFactory,
                api.commandManager);
    }

    protected void assertDiagram(Diagram<Graph, Metadata> diagram, int nodesSize) {
        assertEquals(nodesSize, getNodes(diagram).size());
    }

    @SuppressWarnings("unchecked")
    protected List<Node> getNodes(Diagram<Graph, Metadata> diagram) {
        Graph graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node> nodesIterable = graph.nodes().iterator();
        List<Node> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        return nodes;
    }

    protected Diagram<Graph, Metadata> unmarshall(DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller, String fileName) throws Exception {
        return Unmarshalling.unmarshall(marshaller, fileName);
    }

    protected Diagram<Graph, Metadata> unmarshall(DiagramMarshaller<Graph, Metadata, Diagram<Graph, Metadata>> marshaller, InputStream is) throws Exception {
        return Unmarshalling.unmarshall(marshaller, is);
    }

    protected Definitions convertToDefinitions(Diagram<Graph, Metadata> d) {
        return new DefinitionsConverter(d.getGraph())
                .toDefinitions();
    }

    protected InputStream getStream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }
}
