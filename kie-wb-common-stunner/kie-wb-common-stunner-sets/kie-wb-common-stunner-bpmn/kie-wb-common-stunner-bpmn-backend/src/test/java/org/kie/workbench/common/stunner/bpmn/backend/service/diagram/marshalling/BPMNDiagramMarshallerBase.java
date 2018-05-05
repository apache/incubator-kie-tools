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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDirectDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.builder.BPMNGraphObjectBuilderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxIdMappings;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.Bpmn2OryxManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.AssignmentsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.BooleanTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertyManager;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.Bpmn2OryxPropertySerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ColorTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.DoubleTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.EnumTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.IntegerTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeListTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.ScriptTypeTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.TimerSettingsTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.MockApplicationFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.TaskTypeMorphDefinition;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.Unmarshalling;
import org.kie.workbench.common.stunner.bpmn.backend.service.diagram.WorkItemDefinitionMockRegistry;
import org.kie.workbench.common.stunner.bpmn.backend.workitem.service.WorkItemDefinitionBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.bind.BackendBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.reflect.BackendPropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.clone.CloneManager;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.diagram.MetadataImpl;
import org.kie.workbench.common.stunner.core.factory.graph.EdgeFactory;
import org.kie.workbench.common.stunner.core.factory.graph.ElementFactory;
import org.kie.workbench.common.stunner.core.factory.graph.GraphFactory;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.factory.impl.EdgeFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.GraphFactoryImpl;
import org.kie.workbench.common.stunner.core.factory.impl.NodeFactoryImpl;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public abstract class BPMNDiagramMarshallerBase {

    private static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    @Mock
    private DefinitionManager definitionManager;
    @Mock
    private AdapterManager adapterManager;
    @Mock
    private AdapterRegistry adapterRegistry;
    @Mock
    private RuleManager rulesManager;
    @Mock
    private CloneManager cloneManager;
    @Mock
    private FactoryManager applicationFactoryManager;

    private EdgeFactory<Object> connectionEdgeFactory;
    private NodeFactory<Object> viewNodeFactory;
    private GraphFactory bpmnGraphFactory;
    private TestScopeModelFactory testScopeModelFactory;
    private TaskTypeMorphDefinition taskMorphDefinition;

    protected BPMNDiagramMarshaller oldMarshaller;
    protected BPMNDirectDiagramMarshaller newMarshaller;
    protected WorkItemDefinitionMockRegistry workItemDefinitionMockRegistry;

    @SuppressWarnings("unchecked")
    protected void init() {
        initMocks(this);
        // Work Items.
        workItemDefinitionMockRegistry = new WorkItemDefinitionMockRegistry();
        definitionManager = mock(DefinitionManager.class);
        adapterManager = mock(AdapterManager.class);
        adapterRegistry = mock(AdapterRegistry.class);
        rulesManager = mock(RuleManager.class);
        cloneManager = mock(CloneManager.class);

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        DefinitionUtils definitionUtils1 = new DefinitionUtils(definitionManager,
                                                               applicationFactoryManager,
                                                               null); // TODO!
        testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final BackendDefinitionAdapter definitionAdapter = new BackendDefinitionAdapter(definitionUtils1);
        final BackendDefinitionSetAdapter definitionSetAdapter = new BackendDefinitionSetAdapter(definitionAdapter);
        final BackendPropertySetAdapter propertySetAdapter = new BackendPropertySetAdapter();
        final BackendPropertyAdapter propertyAdapter = new BackendPropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        GraphCommandManager commandManager1 = new GraphCommandManagerImpl(null,
                                                                          null,
                                                                          null);
        GraphCommandFactory commandFactory1 = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils1);
        bpmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (BPMNDefinitionSet.class.getName().equals(id)) {
                return bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    return viewNodeFactory.build(uuid, model);
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    return connectionEdgeFactory.build(uuid, model);
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (BPMNDefinitionSet.class.equals(type)) {
                return bpmnGraphFactory.build(uuid, BPMN_DEF_SET_ID);
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = BackendDefinitionAdapter.getGraphFactory(model.getClass());
                if (element.isAssignableFrom(NodeFactory.class)) {
                    return viewNodeFactory.build(uuid, model);
                } else if (element.isAssignableFrom(EdgeFactory.class)) {
                    return connectionEdgeFactory.build(uuid, model);
                }
            }
            return null;
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      any(Class.class));
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String defSetId = (String) invocationOnMock.getArguments()[1];
            final Graph graph = (Graph) applicationFactoryManager.newElement(uuid,
                                                                             defSetId);
            final DiagramImpl result = new DiagramImpl(uuid,
                                                       new MetadataImpl.MetadataImplBuilder(defSetId).build());
            result.setGraph(graph);
            return result;
        }).when(applicationFactoryManager).newDiagram(anyString(),
                                                      anyString(),
                                                      any(Metadata.class));
        // Bpmn 2 oryx stuff.
        Bpmn2OryxIdMappings oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager,
                                                                     () -> workItemDefinitionMockRegistry);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils1);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        TimerSettingsTypeSerializer timerSettingsTypeSerializer = new TimerSettingsTypeSerializer();
        ScriptTypeTypeSerializer scriptTypeTypeSerializer = new ScriptTypeTypeSerializer();
        ScriptTypeListTypeSerializer scriptTypeListTypeSerializer = new ScriptTypeListTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        propertySerializers.add(timerSettingsTypeSerializer);
        propertySerializers.add(scriptTypeTypeSerializer);
        propertySerializers.add(scriptTypeListTypeSerializer);
        Bpmn2OryxPropertyManager oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        Bpmn2OryxManager oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                                                            oryxPropertyManager);
        oryxManager.init();
        // Marshalling factories.
        BPMNGraphObjectBuilderFactory objectBuilderFactory = new BPMNGraphObjectBuilderFactory(definitionManager,
                                                                                               oryxManager,
                                                                                               () -> workItemDefinitionMockRegistry);
        taskMorphDefinition = new TaskTypeMorphDefinition();
        Collection<MorphDefinition> morphDefinitions = new ArrayList<MorphDefinition>() {{
            add(taskMorphDefinition);
        }};
        BackendBindableMorphAdapter<Object> morphAdapter =
                new BackendBindableMorphAdapter(definitionUtils1,
                                                applicationFactoryManager,
                                                cloneManager,
                                                morphDefinitions);
        when(adapterRegistry.getMorphAdapter(eq(UserTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(NoneTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(ScriptTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(BusinessRuleTask.class))).thenReturn(morphAdapter);
        GraphIndexBuilder<?> indexBuilder = new MapIndexBuilder();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());
        // The work item definition service.
        WorkItemDefinitionBackendService widService = mock(WorkItemDefinitionBackendService.class);
        when(widService.execute(any(Metadata.class))).thenReturn(workItemDefinitionMockRegistry.items());
        // The tested BPMN marshaller.
        oldMarshaller = new BPMNDiagramMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                                  objectBuilderFactory,
                                                  definitionManager,
                                                  indexBuilder,
                                                  oryxManager,
                                                  applicationFactoryManager,
                                                  null, // TODO!
                                                  rulesManager,
                                                  commandManager1,
                                                  commandFactory1,
                                                  widService);

        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        // initApplicationFactoryManagerAlt();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());

        DefinitionUtils definitionUtils = new DefinitionUtils(definitionManager,
                                                              applicationFactoryManager,
                                                              null); // TODO!
        TestScopeModelFactory testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        mockAdapterManager(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        mockAdapterRegistry(definitionAdapter, definitionSetAdapter, propertySetAdapter, propertyAdapter);
        applicationFactoryManager = new MockApplicationFactoryManager(
                definitionManager,
                new GraphFactoryImpl(definitionManager),
                testScopeModelFactory,
                new EdgeFactoryImpl(definitionManager),
                new NodeFactoryImpl(definitionUtils)
        );

        GraphCommandManagerImpl commandManager = new GraphCommandManagerImpl(null,
                                                                             null,
                                                                             null);
        GraphCommandFactory commandFactory = new GraphCommandFactory();

        // The tested BPMN marshaller.
        newMarshaller = new BPMNDirectDiagramMarshaller(
                new XMLEncoderDiagramMetadataMarshaller(),
                definitionManager,
                rulesManager,
                applicationFactoryManager,
                commandFactory,
                commandManager);
    }

    @SuppressWarnings("unchecked")
    private void mockAdapterRegistry(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
    }

    @SuppressWarnings("unchecked")
    private void mockAdapterManager(BackendDefinitionAdapter definitionAdapter, BackendDefinitionSetAdapter definitionSetAdapter, BackendPropertySetAdapter propertySetAdapter, BackendPropertyAdapter propertyAdapter) {
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
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

    protected InputStream getStream(String data) {
        return new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
    }

    private void assertNodeEquals(Diagram<Graph, Metadata> oldDiagram, Diagram<Graph, Metadata> newDiagram, String fileName) {
        Map<String, Node<View, ?>> oldNodes = asNodeMap(oldDiagram.getGraph().nodes());
        Map<String, Node<View, ?>> newNodes = asNodeMap(newDiagram.getGraph().nodes());

        assertEquals(fileName + ": Number of nodes should match", oldNodes.size(), newNodes.size());

        for (Node<View, ?> o : oldNodes.values()) {
            Node<View, ?> n = newNodes.get(o.getUUID());

            View oldContent = o.getContent();
            View newContent = n.getContent();

            Bounds oldBounds = oldContent.getBounds();
            Bounds newBounds = newContent.getBounds();

            assertEquals(
                    fileName + ": Bounds should match for " + o.getUUID(),
                    oldBounds,
                    newBounds
            );

            Object oldDefinition = oldContent.getDefinition();
            Object newDefinition = newContent.getDefinition();

            assertEquals(
                    fileName + ": Definitions should match for " + o.getUUID(),
                    oldDefinition,
                    newDefinition
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Node<View, ?>> asNodeMap(Iterable nodes) {
        Map<String, Node<View, ?>> oldNodes = new HashMap<>();
        nodes.forEach(n -> {
            Node n1 = (Node) n;
            oldNodes.put(n1.getUUID(), n1);
        });
        return oldNodes;
    }

    protected void assertDiagramEquals(Diagram<Graph, Metadata> oldDiagram, Diagram<Graph, Metadata> newDiagram, String fileName) {
        assertNodeEquals(oldDiagram, newDiagram, fileName);
        assertEdgeEquals(oldDiagram, newDiagram, fileName);
    }

    @SuppressWarnings("unchecked")
    private void assertEdgeEquals(Diagram<Graph, Metadata> oldDiagram, Diagram<Graph, Metadata> newDiagram, String fileName) {
        Set<Edge> oldEdges = asEdgeSet(oldDiagram.getGraph().nodes());
        Set<Edge> newEdges = asEdgeSet(newDiagram.getGraph().nodes());

        assertEquals(fileName + ": Number of edges should match", oldEdges.size(), newEdges.size());

        {
            Map<String, Edge> nonRelOldEdges = oldEdges.stream()
                    .filter(BPMNDiagramMarshallerBase::nonRelationshipConnector)
                    .collect(Collectors.toMap(Edge::getUUID, Function.identity()));

            Map<String, Edge> nonRelNewEdges = newEdges.stream()
                    .filter(BPMNDiagramMarshallerBase::nonRelationshipConnector)
                    .collect(Collectors.toMap(Edge::getUUID, Function.identity()));

            assertEquals(nonRelOldEdges, nonRelOldEdges);

            for (Edge<ViewConnector, ?> oldEdge : nonRelOldEdges.values()) {
                Edge<ViewConnector, ?> newEdge = nonRelNewEdges.get(oldEdge.getUUID());

                // (relationship) edges are equal iff <source, target> match respectively
                assertEquals(fileName + ": Source Connection should match for " + oldEdge.getUUID(),
                             oldEdge.getContent().getSourceConnection(), newEdge.getContent().getSourceConnection());
                assertEquals(fileName + ": Target Connection should match for " + oldEdge.getUUID(),
                             oldEdge.getContent().getTargetConnection(), newEdge.getContent().getTargetConnection());
            }
        }

        {

            List<Edge> relOldEdges = oldEdges.stream()
                    .filter(BPMNDiagramMarshallerBase::isRelationshipConnector)
                    .collect(Collectors.toList());
            List<Edge> relNewEdges = newEdges.stream()
                    .filter(BPMNDiagramMarshallerBase::isRelationshipConnector)
                    .collect(Collectors.toList());

            // sort lexicografically by source + target IDs
            relOldEdges.sort(Comparator.comparing(e -> e.getSourceNode().getUUID() + e.getTargetNode().getUUID()));
            relNewEdges.sort(Comparator.comparing(e -> e.getSourceNode().getUUID() + e.getTargetNode().getUUID()));

            Iterator<Edge> oldIt = relOldEdges.iterator();
            Iterator<Edge> newIt = relNewEdges.iterator();

            for (int i = 0; i < relOldEdges.size(); i++) {
                Edge<ViewConnector, ?> oldEdge = oldIt.next();
                Edge<ViewConnector, ?> newEdge = newIt.next();

                assertEquals(fileName + ": target node did not match", oldEdge.getTargetNode(), newEdge.getTargetNode());
                assertEquals(fileName + ": source node did not match", oldEdge.getSourceNode(), newEdge.getSourceNode());
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Set<Edge> asEdgeSet(Iterable nodes) {
        Set<Edge> oldEdges = new HashSet<>();
        nodes.forEach(n -> {
            oldEdges.addAll(((Node<?, Edge>) n).getOutEdges());
            oldEdges.addAll(((Node<?, Edge>) n).getInEdges());
        });
        return oldEdges;
    }

    private static boolean nonRelationshipConnector(Edge e) {
        return !isRelationshipConnector(e);
    }

    private static boolean isRelationshipConnector(Edge e) {
        return e.getContent() instanceof Parent
                || e.getContent() instanceof Child
                || e.getContent() instanceof Dock;
    }
}
