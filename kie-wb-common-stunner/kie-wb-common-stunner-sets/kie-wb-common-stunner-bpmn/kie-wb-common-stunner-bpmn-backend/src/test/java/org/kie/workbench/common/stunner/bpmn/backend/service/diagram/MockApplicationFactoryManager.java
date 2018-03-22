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

package org.kie.workbench.common.stunner.bpmn.backend.service.diagram;

import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.BackendFactoryManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;

import static org.kie.workbench.common.stunner.bpmn.backend.service.diagram.BPMNDirectDiagramMarshallerTest.BPMN_DEF_SET_ID;

public class MockApplicationFactoryManager extends BackendFactoryManager {

    private final GraphFactory bpmnGraphFactory;
    private final TestScopeModelFactory testScopeModelFactory;
    private final EdgeFactory<Object> connectionEdgeFactory;
    private final NodeFactory<Object> viewNodeFactory;

    public MockApplicationFactoryManager(final DefinitionManager definitionManager,
                                         final GraphFactory bpmnGraphFactory,
                                         final TestScopeModelFactory testScopeModelFactory,
                                         final EdgeFactory<Object> connectionEdgeFactory,
                                         final NodeFactory<Object> viewNodeFactory) {
        super(definitionManager);
        this.bpmnGraphFactory = bpmnGraphFactory;
        this.testScopeModelFactory = testScopeModelFactory;
        this.connectionEdgeFactory = connectionEdgeFactory;
        this.viewNodeFactory = viewNodeFactory;
    }

    @Override
    public <T> T newDefinition(String id) {
        return (T) testScopeModelFactory.build(id);
    }

    @Override
    public Element<?> newElement(String uuid, String id) {
        if (BPMNDefinitionSet.class.getName().equals(id)) {
            Graph graph = bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            return graph;
        }
        Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
        if (null != model) {
            Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
            if (element.isAssignableFrom(NodeFactory.class)) {
                Node node = viewNodeFactory.build(uuid,
                                                  model);
                return node;
            } else if (element.isAssignableFrom(EdgeFactory.class)) {
                Edge edge = connectionEdgeFactory.build(uuid,
                                                        model);
                return edge;
            }
        }
        return null;
    }

    @Override
    public Element<?> newElement(String uuid, Class<?> type) {
        String id = BindableAdapterUtils.getGenericClassName(type);
        if (BPMNDefinitionSet.class.equals(type)) {
            Graph graph = bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            return graph;
        }
        Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
        if (null != model) {
            Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
            if (element.isAssignableFrom(NodeFactory.class)) {
                Node node = viewNodeFactory.build(uuid,
                                                  model);
                return node;
            } else if (element.isAssignableFrom(EdgeFactory.class)) {
                Edge edge = connectionEdgeFactory.build(uuid,
                                                        model);
                return edge;
            }
        }
        return null;
    }

    @Override
    public <M extends Metadata, D extends Diagram> D newDiagram(String uuid, String defSetId, M metadata) {
        final Graph graph = (Graph) this.newElement(uuid,
                                                    defSetId);
        final DiagramImpl result = new DiagramImpl(uuid,
                                                   new MetadataImpl.MetadataImplBuilder(defSetId).build());
        result.setGraph(graph);
        return (D) result;
    }
}
