/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import javax.enterprise.inject.spi.BeanManager;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.ItemAwareElement;
import org.eclipse.bpmn2.ItemDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Property;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.jboss.drools.MetaDataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.backend.ApplicationFactoryManager;
import org.kie.workbench.common.stunner.backend.definition.factory.TestScopeModelFactory;
import org.kie.workbench.common.stunner.backend.service.XMLEncoderDiagramMetadataMarshaller;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNDiagramMarshaller;
import org.kie.workbench.common.stunner.bpmn.backend.legacy.resource.JBPMBpmn2ResourceImpl;
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
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.StringTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.backend.marshall.json.oryx.property.VariablesTypeSerializer;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EndNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EndTerminateEvent;
import org.kie.workbench.common.stunner.bpmn.definition.ExclusiveDatabasedGateway;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.bpmn.definition.StartNoneEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartSignalEvent;
import org.kie.workbench.common.stunner.bpmn.definition.StartTimerEvent;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.SignalRef;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimeDefinitionSetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertyAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.annotation.RuntimePropertySetAdapter;
import org.kie.workbench.common.stunner.core.backend.definition.adapter.binding.RuntimeBindableMorphAdapter;
import org.kie.workbench.common.stunner.core.definition.adapter.AdapterManager;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.morph.MorphDefinition;
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
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManager;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.impl.GraphCommandFactory;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.DiscreteConnection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnectorImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.processing.index.GraphIndexBuilder;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndexBuilder;
import org.kie.workbench.common.stunner.core.registry.definition.AdapterRegistry;
import org.kie.workbench.common.stunner.core.rule.RuleEvaluationContext;
import org.kie.workbench.common.stunner.core.rule.RuleManager;
import org.kie.workbench.common.stunner.core.rule.RuleSet;
import org.kie.workbench.common.stunner.core.rule.violations.DefaultRuleViolations;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNDiagramMarshallerTest {

    private static final String BPMN_DEF_SET_ID = BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class);

    private static final String BPMN_BASIC = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/basic.bpmn";
    private static final String BPMN_EVALUATION = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/evaluation.bpmn";
    private static final String BPMN_LANES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/lanes.bpmn";
    private static final String BPMN_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/boundaryIntmEvent.bpmn";
    private static final String BPMN_NOT_BOUNDARY_EVENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/notBoundaryIntmEvent.bpmn";
    private static final String BPMN_PROCESSVARIABLES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processVariables.bpmn";
    private static final String BPMN_USERTASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignments.bpmn";
    private static final String BPMN_BUSINESSRULETASKASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTaskAssignments.bpmn";
    private static final String BPMN_STARTEVENTASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startEventAssignments.bpmn";
    private static final String BPMN_STARTTIMEREVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startTimerEvent.bpmn";
    private static final String BPMN_STARTSIGNALEVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/startSignalEvent.bpmn";
    private static final String BPMN_INTERMEDIATE_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/intermediateTimerEvent.bpmn";
    private static final String BPMN_ENDEVENTASSIGNMENTS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/endEventAssignments.bpmn";
    private static final String BPMN_PROCESSPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/processProperties.bpmn";
    private static final String BPMN_BUSINESSRULETASKRULEFLOWGROUP = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/businessRuleTask.bpmn";
    private static final String BPMN_REUSABLE_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/reusableSubprocessCalledElement.bpmn";
    private static final String BPMN_EMBEDDED_SUBPROCESS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/embeddedSubprocess.bpmn";
    private static final String BPMN_SCRIPTTASK = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/scriptTask.bpmn";
    private static final String BPMN_USERTASKASSIGNEES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskAssignees.bpmn";
    private static final String BPMN_USERTASKPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/userTaskProperties.bpmn";
    private static final String BPMN_SEQUENCEFLOW = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/sequenceFlow.bpmn";
    private static final String BPMN_XORGATEWAY = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/xorGateway.bpmn";
    private static final String BPMN_TIMER_EVENT = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/timerEvent.bpmn";
    private static final String BPMN_SIMULATIONPROPERTIES = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/simulationProperties.bpmn";
    private static final String BPMN_MAGNETDOCKERS = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetDockers.bpmn";
    private static final String BPMN_MAGNETSINLANE = "org/kie/workbench/common/stunner/bpmn/backend/service/diagram/magnetsInLane.bpmn";

    @Mock
    DefinitionManager definitionManager;

    @Mock
    AdapterManager adapterManager;

    @Mock
    AdapterRegistry adapterRegistry;

    @Mock
    BeanManager beanManager;

    @Mock
    RuleManager rulesManager;

    @Mock
    ApplicationFactoryManager applicationFactoryManager;

    EdgeFactory<Object> connectionEdgeFactory;
    NodeFactory<Object> viewNodeFactory;
    DefinitionUtils definitionUtils;

    GraphCommandManager commandManager;
    GraphCommandFactory commandFactory;

    GraphFactory bpmnGraphFactory;

    Bpmn2OryxIdMappings oryxIdMappings;
    Bpmn2OryxPropertyManager oryxPropertyManager;
    Bpmn2OryxManager oryxManager;

    TestScopeModelFactory testScopeModelFactory;
    BPMNGraphObjectBuilderFactory objectBuilderFactory;

    TaskTypeMorphDefinition taskMorphDefinition;

    private BPMNDiagramMarshaller tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        // Graph utils.
        when(definitionManager.adapters()).thenReturn(adapterManager);
        when(adapterManager.registry()).thenReturn(adapterRegistry);
        definitionUtils = new DefinitionUtils(definitionManager,
                                              applicationFactoryManager);
        testScopeModelFactory = new TestScopeModelFactory(new BPMNDefinitionSet.BPMNDefinitionSetBuilder().build());
        // Definition manager.
        final RuntimeDefinitionAdapter definitionAdapter = new RuntimeDefinitionAdapter(definitionUtils);
        final RuntimeDefinitionSetAdapter definitionSetAdapter = new RuntimeDefinitionSetAdapter(definitionAdapter);
        final RuntimePropertySetAdapter propertySetAdapter = new RuntimePropertySetAdapter();
        final RuntimePropertyAdapter propertyAdapter = new RuntimePropertyAdapter();
        when(adapterManager.forDefinitionSet()).thenReturn(definitionSetAdapter);
        when(adapterManager.forDefinition()).thenReturn(definitionAdapter);
        when(adapterManager.forPropertySet()).thenReturn(propertySetAdapter);
        when(adapterManager.forProperty()).thenReturn(propertyAdapter);
        when(adapterRegistry.getDefinitionSetAdapter(any(Class.class))).thenReturn(definitionSetAdapter);
        when(adapterRegistry.getDefinitionAdapter(any(Class.class))).thenReturn(definitionAdapter);
        when(adapterRegistry.getPropertySetAdapter(any(Class.class))).thenReturn(propertySetAdapter);
        when(adapterRegistry.getPropertyAdapter(any(Class.class))).thenReturn(propertyAdapter);
        commandManager = new GraphCommandManagerImpl(null,
                                                     null,
                                                     null);
        commandFactory = new GraphCommandFactory();
        connectionEdgeFactory = new EdgeFactoryImpl(definitionManager);
        viewNodeFactory = new NodeFactoryImpl(definitionUtils);
        bpmnGraphFactory = new GraphFactoryImpl(definitionManager);
        doAnswer(invocationOnMock -> {
            String id = (String) invocationOnMock.getArguments()[0];
            return testScopeModelFactory.build(id);
        }).when(applicationFactoryManager).newDefinition(anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            String id = (String) invocationOnMock.getArguments()[1];
            if (BPMNDefinitionSet.class.getName().equals(id)) {
                Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                                                             BPMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
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
        }).when(applicationFactoryManager).newElement(anyString(),
                                                      anyString());
        doAnswer(invocationOnMock -> {
            String uuid = (String) invocationOnMock.getArguments()[0];
            Class type = (Class) invocationOnMock.getArguments()[1];
            String id = BindableAdapterUtils.getGenericClassName(type);
            if (BPMNDefinitionSet.class.equals(type)) {
                Graph graph = (Graph) bpmnGraphFactory.build(uuid,
                                                             BPMN_DEF_SET_ID);
                return graph;
            }
            Object model = testScopeModelFactory.accepts(id) ? testScopeModelFactory.build(id) : null;
            if (null != model) {
                Class<? extends ElementFactory> element = RuntimeDefinitionAdapter.getGraphFactory(model.getClass());
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
        oryxIdMappings = new Bpmn2OryxIdMappings(definitionManager);
        StringTypeSerializer stringTypeSerializer = new StringTypeSerializer();
        BooleanTypeSerializer booleanTypeSerializer = new BooleanTypeSerializer();
        ColorTypeSerializer colorTypeSerializer = new ColorTypeSerializer();
        DoubleTypeSerializer doubleTypeSerializer = new DoubleTypeSerializer();
        IntegerTypeSerializer integerTypeSerializer = new IntegerTypeSerializer();
        EnumTypeSerializer enumTypeSerializer = new EnumTypeSerializer(definitionUtils);
        AssignmentsTypeSerializer assignmentsTypeSerializer = new AssignmentsTypeSerializer();
        VariablesTypeSerializer variablesTypeSerializer = new VariablesTypeSerializer();
        List<Bpmn2OryxPropertySerializer<?>> propertySerializers = new LinkedList<>();
        propertySerializers.add(stringTypeSerializer);
        propertySerializers.add(booleanTypeSerializer);
        propertySerializers.add(colorTypeSerializer);
        propertySerializers.add(doubleTypeSerializer);
        propertySerializers.add(integerTypeSerializer);
        propertySerializers.add(enumTypeSerializer);
        propertySerializers.add(assignmentsTypeSerializer);
        propertySerializers.add(variablesTypeSerializer);
        oryxPropertyManager = new Bpmn2OryxPropertyManager(propertySerializers);
        oryxManager = new Bpmn2OryxManager(oryxIdMappings,
                                           oryxPropertyManager);
        oryxManager.init();
        // Marshalling factories.
        objectBuilderFactory = new BPMNGraphObjectBuilderFactory(definitionManager,
                                                                 oryxManager);
        taskMorphDefinition = new TaskTypeMorphDefinition();
        Collection<MorphDefinition> morphDefinitions = new ArrayList<MorphDefinition>() {{
            add(taskMorphDefinition);
        }};
        RuntimeBindableMorphAdapter<Object> morphAdapter =
                new RuntimeBindableMorphAdapter(definitionUtils,
                                                applicationFactoryManager,
                                                morphDefinitions);
        when(adapterRegistry.getMorphAdapter(eq(UserTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(NoneTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(ScriptTask.class))).thenReturn(morphAdapter);
        when(adapterRegistry.getMorphAdapter(eq(BusinessRuleTask.class))).thenReturn(morphAdapter);
        GraphIndexBuilder<?> indexBuilder = new MapIndexBuilder();
        when(rulesManager.evaluate(any(RuleSet.class),
                                   any(RuleEvaluationContext.class))).thenReturn(new DefaultRuleViolations());
        // The tested BPMN marshaller.
        tested = new BPMNDiagramMarshaller(new XMLEncoderDiagramMetadataMarshaller(),
                                           objectBuilderFactory,
                                           definitionManager,
                                           indexBuilder,
                                           oryxManager,
                                           applicationFactoryManager,
                                           rulesManager,
                                           commandManager,
                                           commandFactory);
    }

    // 4 nodes expected: BPMNDiagram, StartNode, Task and EndNode
    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        assertDiagram(diagram,
                      4);
        assertEquals("Basic process",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> task1 = diagram.getGraph().getNode("810797AB-7D09-4E1F-8A5B-96C424E4B031");
        assertTrue(task1.getContent().getDefinition() instanceof NoneTask);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram,
                      8);
        assertEquals("Evaluation",
                     diagram.getMetadata().getTitle());
        Node<? extends View, ?> task1 = diagram.getGraph().getNode("_88233779-B395-4B8C-A086-9EF43698426C");
        Node<? extends View, ?> task2 = diagram.getGraph().getNode("_AE5BF0DC-B720-4FDE-9499-5ED89D41FB1A");
        Node<? extends View, ?> task3 = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        assertTrue(task1.getContent().getDefinition() instanceof UserTask);
        assertTrue(task2.getContent().getDefinition() instanceof UserTask);
        assertTrue(task3.getContent().getDefinition() instanceof UserTask);
        // Assert bounds.
        Bounds task1Bounds = task1.getContent().getBounds();
        Bounds.Bound task1ULBound = task1Bounds.getUpperLeft();
        Bounds.Bound task1LRBound = task1Bounds.getLowerRight();
        assertEquals(648d,
                     task1ULBound.getX(),
                     0);
        assertEquals(149d,
                     task1ULBound.getY(),
                     0);
        assertEquals(784d,
                     task1LRBound.getX(),
                     0);
        assertEquals(197d,
                     task1LRBound.getY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        assertDiagram(diagram,
                      8);
        assertEquals("ProcessVariables",
                     diagram.getMetadata().getTitle());
        ProcessVariables variables = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) oDefinition;
                    variables = bpmnDiagram.getProcessData().getProcessVariables();
                    break;
                }
            }
        }
        assertEquals(variables.getValue(),
                     "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
        Node<? extends Definition, ?> diagramNode = diagram.getGraph().getNode("_luRBMdEjEeWXpsZ1tNStKQ");
        assertTrue(diagramNode.getContent().getDefinition() instanceof BPMNDiagram);
        BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) diagramNode.getContent().getDefinition();
        assertTrue(bpmnDiagram.getProcessData() != null);
        assertTrue(bpmnDiagram.getProcessData().getProcessVariables() != null);
        variables = bpmnDiagram.getProcessData().getProcessVariables();
        assertEquals(variables.getValue(),
                     "employee:java.lang.String,reason:java.lang.String,performance:java.lang.String");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("BPSimple",
                     diagram.getMetadata().getTitle());
        DiagramSet diagramProperties = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BPMNDiagram) {
                    BPMNDiagramImpl bpmnDiagram = (BPMNDiagramImpl) oDefinition;
                    diagramProperties = bpmnDiagram.getDiagramSet();
                    break;
                }
            }
        }
        assertEquals("BPSimple",
                     diagramProperties.getName().getValue());
        assertEquals("This is a\n" +
                             "simple\n" +
                             "process",
                     diagramProperties.getDocumentation().getValue());
        assertEquals("JDLProj.BPSimple",
                     diagramProperties.getId().getValue());
        assertEquals("org.jbpm",
                     diagramProperties.getPackageProperty().getValue());
        assertEquals(Boolean.valueOf(true),
                     diagramProperties.getExecutable().getValue());
        assertEquals(Boolean.valueOf(true),
                     diagramProperties.getAdHoc().getValue());
        assertEquals("This is the\n" +
                             "Process\n" +
                             "Instance\n" +
                             "Description",
                     diagramProperties.getProcessInstanceDescription().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        assertDiagram(diagram,
                      8);
        assertEquals("UserTaskAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> selfEvaluationNode = diagram.getGraph().getNode("_6063D302-9D81-4C86-920B-E808A45377C2");
        UserTask selfEvaluationTask = (UserTask) selfEvaluationNode.getContent().getDefinition();
        assertEquals(selfEvaluationTask.getTaskType().getValue(),
                     TaskTypes.USER);
        UserTaskExecutionSet executionSet = selfEvaluationTask.getExecutionSet();
        AssignmentsInfo assignmentsinfo = executionSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                     "|reason:com.test.Reason,Comment:Object,Skippable:Object||performance:Object|[din]reason->reason,[dout]performance->performance");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        assertDiagram(diagram,
                      4);
        assertEquals("BusinessRuleTaskAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> businessRuleNode = diagram.getGraph().getNode("_45C2C340-D1D0-4D63-8419-EF38F9E73507");
        BusinessRuleTask businessRuleTask = (BusinessRuleTask) businessRuleNode.getContent().getDefinition();
        assertEquals(businessRuleTask.getTaskType().getValue(),
                     TaskTypes.BUSINESS_RULE);
        DataIOSet dataIOSet = businessRuleTask.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals(assignmentsinfo.getValue(),
                     "|input1:String,input2:String||output1:String,output2:String|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv2,[dout]output2->pv2");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTTIMEREVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartTimerEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startTimerEventNode = diagram.getGraph().getNode("_49ADC988-B63D-4AEB-B811-67969F305FD0");
        StartTimerEvent startTimerEvent = (StartTimerEvent) startTimerEventNode.getContent().getDefinition();
        IsInterrupting isInterrupting = startTimerEvent.getIsInterrupting();
        assertEquals(false,
                     isInterrupting.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTSIGNALEVENT);
        assertDiagram(diagram,
                      4);
        assertEquals("StartSignalEvent",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startSignalEventNode = diagram.getGraph().getNode("_1876844A-4DAC-4214-8BCD-2ABA3FCC8EB5");
        StartSignalEvent startSignalEvent = (StartSignalEvent) startSignalEventNode.getContent().getDefinition();
        assertNotNull(startSignalEvent.getExecutionSet());
        SignalRef signalRef = startSignalEvent.getExecutionSet().getSignalRef();
        assertEquals("sig1",
                     signalRef.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_TIMER_EVENT);
        assertDiagram(diagram,
                      2);
        assertEquals("intermediateTimer",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> intermediateEventNode = diagram.getGraph().getNode("_8D881072-284F-4F0D-8CF2-AD1F4540FC4E");
        IntermediateTimerEvent intermediateTimerEvent = (IntermediateTimerEvent) intermediateEventNode.getContent().getDefinition();
        assertNotNull(intermediateTimerEvent.getGeneral());
        assertEquals("MyTimer",
                     intermediateTimerEvent.getGeneral().getName().getValue());
        assertNotNull(intermediateTimerEvent.getExecutionSet());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimeCycle().getValue());
        assertEquals("none",
                     intermediateTimerEvent.getExecutionSet().getTimeCycleLanguage().getValue());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimeDate().getValue());
        assertEquals("abc",
                     intermediateTimerEvent.getExecutionSet().getTimeDuration().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallStartEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTEVENTASSIGNMENTS);
        assertDiagram(diagram,
                      8);
        assertEquals("StartEventAssignments",
                     diagram.getMetadata().getTitle());
        Node<? extends Definition, ?> startEventNode = diagram.getGraph().getNode("_106AADA7-E381-4736-8BD6-7EB47A1D096B");
        StartNoneEvent startNoneEvent = (StartNoneEvent) startEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = startNoneEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("||StartEventOutput1:String||[dout]StartEventOutput1->employee",
                     assignmentsinfo.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallEndEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDEVENTASSIGNMENTS);
        assertDiagram(diagram,
                      8);
        assertEquals("EndEventAssignments",
                     diagram.getMetadata().getTitle());

        Node<? extends Definition, ?> endTerminateEventNode = diagram.getGraph().getNode("_0493ECBF-3C0F-4086-9979-2391CE740AA8");
        EndTerminateEvent endTerminateEvent = (EndTerminateEvent) endTerminateEventNode.getContent().getDefinition();
        DataIOSet dataIOSet = endTerminateEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo = dataIOSet.getAssignmentsinfo();
        assertEquals("EndTermEventInput1:String||||[din]employee->EndTermEventInput1",
                     assignmentsinfo.getValue());

        Node<? extends Definition, ?> endNoneEventNode = diagram.getGraph().getNode("_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5");
        EndNoneEvent endNoneEvent = (EndNoneEvent) endNoneEventNode.getContent().getDefinition();
        DataIOSet dataIOSet2 = endNoneEvent.getDataIOSet();
        AssignmentsInfo assignmentsinfo2 = dataIOSet2.getAssignmentsinfo();
        assertEquals("EndNoneEventInput1:String||||[din]reason->EndNoneEventInput1",
                     assignmentsinfo2.getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        assertDiagram(diagram,
                      6);
        assertEquals("UserGroups",
                     diagram.getMetadata().getTitle());
        UserTaskExecutionSet executionSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    executionSet = userTask.getExecutionSet();
                    break;
                }
            }
        }
        assertEquals("user,user1",
                     executionSet.getActors().getValue());
        assertEquals("admin,kiemgmt",
                     executionSet.getGroupid().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("MyBP",
                     diagram.getMetadata().getTitle());
        UserTaskExecutionSet userTaskExecutionSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    userTaskExecutionSet = userTask.getExecutionSet();
                    break;
                }
            }
        }
        assertEquals("MyUserTask",
                     userTaskExecutionSet.getTaskName().getValue());
        assertEquals("true",
                     userTaskExecutionSet.getIsAsync().getValue().toString());

        assertEquals("false",
                     userTaskExecutionSet.getSkippable().getValue().toString());

        assertEquals("my subject",
                     userTaskExecutionSet.getSubject().getValue());

        assertEquals("admin",
                     userTaskExecutionSet.getCreatedBy().getValue());

        assertEquals("my description",
                     userTaskExecutionSet.getDescription().getValue());

        assertEquals("3",
                     userTaskExecutionSet.getPriority().getValue());

        assertEquals("true",
                     userTaskExecutionSet.getAdHocAutostart().getValue().toString());

        assertEquals("System.out.println(\"Hello\");",
                     userTaskExecutionSet.getOnEntryAction().getValue());

        assertEquals("System.out.println(\"Bye\");",
                     userTaskExecutionSet.getOnExitAction().getValue());

        assertEquals("java",
                     userTaskExecutionSet.getScriptLanguage().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        assertDiagram(diagram,
                      4);
        assertEquals("SimulationProperties",
                     diagram.getMetadata().getTitle());

        SimulationSet simulationSet = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof UserTask) {
                    UserTask userTask = (UserTask) oDefinition;
                    simulationSet = userTask.getSimulationSet();
                    break;
                }
            }
        }

        assertEquals(Double.valueOf(111),
                     simulationSet.getQuantity().getValue());
        assertEquals("poisson",
                     simulationSet.getDistributionType().getValue());
        assertEquals(Double.valueOf(123),
                     simulationSet.getUnitCost().getValue());
        assertEquals(Double.valueOf(999),
                     simulationSet.getWorkingHours().getValue());
        assertEquals(Double.valueOf(321),
                     simulationSet.getMean().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        assertEquals("Not Boundary Event",
                     diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                      6);
        // Assert than the intermediate event is connected using a view connector,
        // so not boundary to the task ( not docked ).
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundViewConnector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof ViewConnector) {
                foundViewConnector = true;
            }
        }
        assertTrue(foundViewConnector);
        // Assert absolute position as the node is not docked.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(305,
                     ul.getX(),
                     0);
        assertEquals(300,
                     ul.getY(),
                     0);
        assertEquals(335,
                     lr.getX(),
                     0);
        assertEquals(330,
                     lr.getY(),
                     0);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        // Basic assertions.
        assertEquals("Boundary Event",
                     diagram.getMetadata().getTitle());
        assertDiagram(diagram,
                      6);
        // Assert than the intermediate event is connected using a dock connector,
        // so boundary to the task.
        Node event = diagram.getGraph().getNode("_CB178D55-8DC2-4CAA-8C42-4F5028D4A1F6");
        List<Edge> inEdges = event.getInEdges();
        boolean foundDockConector = false;
        for (Edge e : inEdges) {
            if (e.getContent() instanceof Dock) {
                foundDockConector = true;
            }
        }
        assertTrue(foundDockConector);
        // Assert relative position for the docked node.
        Bounds bounds = ((View) event.getContent()).getBounds();
        Bounds.Bound ul = bounds.getUpperLeft();
        Bounds.Bound lr = bounds.getLowerRight();
        assertEquals(57,
                     ul.getX(),
                     0);
        assertEquals(70,
                     ul.getY(),
                     0);
        assertEquals(87,
                     lr.getX(),
                     0);
        assertEquals(100,
                     lr.getY(),
                     0);
    }

    @Test
    public void testUnmarshallScriptTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SCRIPTTASK);
        ScriptTask javascriptScriptTask = null;
        ScriptTask javaScriptTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ScriptTask) {
                    ScriptTask task = (ScriptTask) oDefinition;
                    if ("Javascript Script Task".equals(task.getGeneral().getName().getValue())) {
                        javascriptScriptTask = task;
                    } else if ("Java Script Task".equals(task.getGeneral().getName().getValue())) {
                        javaScriptTask = task;
                    }
                }
            }
        }
        assertNotNull(javascriptScriptTask);
        assertNotNull(javascriptScriptTask.getExecutionSet());
        assertNotNull(javascriptScriptTask.getExecutionSet().getScript());
        assertNotNull(javascriptScriptTask.getExecutionSet().getScriptLanguage());
        assertEquals(javascriptScriptTask.getTaskType().getValue(),
                     TaskTypes.SCRIPT);
        assertEquals("Javascript Script Task",
                     javascriptScriptTask.getGeneral().getName().getValue());
        assertEquals("var str = FirstName + LastName;",
                     javascriptScriptTask.getExecutionSet().getScript().getValue());
        assertEquals("javascript",
                     javascriptScriptTask.getExecutionSet().getScriptLanguage().getValue());
        assertEquals("true",
                     javascriptScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     javascriptScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertNotNull(javaScriptTask);
        assertNotNull(javaScriptTask.getExecutionSet());
        assertNotNull(javaScriptTask.getExecutionSet().getScript());
        assertNotNull(javaScriptTask.getExecutionSet().getScriptLanguage());
        assertEquals(javaScriptTask.getTaskType().getValue(),
                     TaskTypes.SCRIPT);
        assertEquals("Java Script Task",
                     javaScriptTask.getGeneral().getName().getValue());
        assertEquals("if (name.toString().equals(\"Jay\")) {\n" +
                             "\n" +
                             "      System.out.println(\"Hello\\n\" + name.toString() + \"\\n\");\n" +
                             "\n" +
                             "} else {\n" +
                             "\n" +
                             "\n" +
                             "  System.out.println(\"Hi\\n\" + name.toString() + \"\\n\");\n" +
                             "\n" +
                             "\n" +
                             "}\n",
                     javaScriptTask.getExecutionSet().getScript().getValue());
        assertEquals("java",
                     javaScriptTask.getExecutionSet().getScriptLanguage().getValue());
        assertEquals("true",
                     javaScriptTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     javaScriptTask.getExecutionSet().getIsAsync().getValue().toString());
    }

    @Test
    public void testUnmarshallSequenceFlow() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEQUENCEFLOW);
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ExclusiveDatabasedGateway) {
                    List<Edge> outEdges = ((NodeImpl) element).getOutEdges();
                    for (Edge edge : outEdges) {
                        SequenceFlow flow = (SequenceFlow) ((ViewConnectorImpl) ((EdgeImpl) edge).getContent()).getDefinition();
                        if ("route1".equals(flow.getGeneral().getName().getValue())) {
                            sequenceFlow1 = flow;
                        }
                        if ("route2".equals(flow.getGeneral().getName().getValue())) {
                            sequenceFlow2 = flow;
                        }
                    }
                }
            }
        }
        assertNotNull(sequenceFlow1);
        assertNotNull(sequenceFlow1.getExecutionSet());
        assertNotNull(sequenceFlow1.getExecutionSet().getConditionExpression());
        assertNotNull(sequenceFlow1.getExecutionSet().getConditionExpressionLanguage());
        assertNotNull(sequenceFlow1.getExecutionSet().getPriority());
        assertNotNull(sequenceFlow1.getGeneral());
        assertNotNull(sequenceFlow1.getGeneral().getName());
        assertEquals("route1",
                     sequenceFlow1.getGeneral().getName().getValue());
        assertEquals("age >= 10;",
                     sequenceFlow1.getExecutionSet().getConditionExpression().getValue());
        assertEquals("javascript",
                     sequenceFlow1.getExecutionSet().getConditionExpressionLanguage().getValue());
        assertEquals("2",
                     sequenceFlow1.getExecutionSet().getPriority().getValue());

        assertNotNull(sequenceFlow2);
        assertNotNull(sequenceFlow2.getExecutionSet());
        assertNotNull(sequenceFlow2.getExecutionSet().getConditionExpression());
        assertNotNull(sequenceFlow2.getExecutionSet().getConditionExpressionLanguage());
        assertNotNull(sequenceFlow2.getExecutionSet().getPriority());
        assertNotNull(sequenceFlow2.getGeneral());
        assertNotNull(sequenceFlow2.getGeneral().getName());
        assertEquals("route2",
                     sequenceFlow2.getGeneral().getName().getValue());
        assertEquals("age\n" +
                             "<\n" +
                             "10;",
                     sequenceFlow2.getExecutionSet().getConditionExpression().getValue());
        assertEquals("java",
                     sequenceFlow2.getExecutionSet().getConditionExpressionLanguage().getValue());
        assertEquals("1",
                     sequenceFlow2.getExecutionSet().getPriority().getValue());
    }

    @Test
    public void testUnmarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        BusinessRuleTask businessRuleTask = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof BusinessRuleTask) {
                    businessRuleTask = (BusinessRuleTask) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(businessRuleTask);
        assertNotNull(businessRuleTask.getExecutionSet());
        assertNotNull(businessRuleTask.getExecutionSet().getRuleFlowGroup());
        assertNotNull(businessRuleTask.getGeneral());
        assertNotNull(businessRuleTask.getGeneral().getName());
        assertEquals(businessRuleTask.getTaskType().getValue(),
                     TaskTypes.BUSINESS_RULE);
        assertEquals("my business rule task",
                     businessRuleTask.getGeneral().getName().getValue());
        assertEquals("my-ruleflow-group",
                     businessRuleTask.getExecutionSet().getRuleFlowGroup().getValue());
        assertEquals("true",
                     businessRuleTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("true",
                     businessRuleTask.getExecutionSet().getIsAsync().getValue().toString());

        assertEquals("System.out.println(\"Hello\");",
                     businessRuleTask.getExecutionSet().getOnEntryAction().getValue());

        assertEquals("System.out.println(\"Bye\");",
                     businessRuleTask.getExecutionSet().getOnExitAction().getValue());

        assertEquals("java",
                     businessRuleTask.getExecutionSet().getScriptLanguage().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnmarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        assertDiagram(diagram,
                      7);
        assertEquals(diagram.getMetadata().getTitle(),
                     "XORGateway");
        Graph graph = diagram.getGraph();
        Node<? extends Definition, ?> gatewayNode = graph.getNode("_877EA035-1A14-42E9-8CAA-43E9BF908C70");
        ExclusiveDatabasedGateway xorGateway = (ExclusiveDatabasedGateway) gatewayNode.getContent().getDefinition();
        assertEquals("AgeSplit",
                     xorGateway.getGeneral().getName().getValue());
        assertEquals("under 10 : _5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD",
                     xorGateway.getExecutionSet().getDefaultRoute().getValue());
        SequenceFlow sequenceFlow1 = null;
        SequenceFlow sequenceFlow2 = null;
        List<Edge> outEdges = (List<Edge>) gatewayNode.getOutEdges();
        if (outEdges != null) {
            for (Edge edge : outEdges) {
                if ("_C72E00C3-70DC-4BC9-A08E-761B4263A239".equals(edge.getUUID())) {
                    sequenceFlow1 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                } else if ("_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD".equals(edge.getUUID())) {
                    sequenceFlow2 = (SequenceFlow) ((ViewConnector) edge.getContent()).getDefinition();
                }
            }
        }
        Node<? extends Definition, ?> sequenceFlowNode1 = graph.getNode("_C72E00C3-70DC-4BC9-A08E-761B4263A239");
        assertNotNull(sequenceFlow1);
        assertEquals("10 and over",
                     sequenceFlow1.getGeneral().getName().getValue());
        assertNotNull(sequenceFlow2);
        assertEquals("under 10",
                     sequenceFlow2.getGeneral().getName().getValue());
    }

    @Test
    public void testUnmarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        ReusableSubprocess reusableSubprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof ReusableSubprocess) {
                    reusableSubprocess = (ReusableSubprocess) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(reusableSubprocess);
        assertNotNull(reusableSubprocess.getExecutionSet());
        assertNotNull(reusableSubprocess.getExecutionSet().getCalledElement());
        assertNotNull(reusableSubprocess.getGeneral());

        BPMNGeneralSet generalSet = reusableSubprocess.getGeneral();
        ReusableSubprocessTaskExecutionSet executionSet = reusableSubprocess.getExecutionSet();
        assertNotNull(generalSet);
        assertNotNull(executionSet);

        assertEquals("my subprocess",
                     generalSet.getName().getValue());
        assertEquals("my-called-element",
                     executionSet.getCalledElement().getValue());
        assertEquals(false,
                     executionSet.getIndependent().getValue());
        assertEquals(false,
                     executionSet.getWaitForCompletion().getValue());

        String assignmentsInfo = reusableSubprocess.getDataIOSet().getAssignmentsinfo().getValue();
        assertEquals("|input1:String,input2:Float||output1:String,output2:Float|[din]pv1->input1,[din]pv2->input2,[dout]output1->pv1,[dout]output2->pv2",
                     assignmentsInfo);

        assertEquals("true",
                     reusableSubprocess.getExecutionSet().getIsAsync().getValue().toString());
    }

    @Test
    public void testUnmarshallMagnetDockers() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MAGNETDOCKERS);

        testMagnetDockers(diagram);
    }

    private void testMagnetDockers(Diagram<Graph, Metadata> diagram) throws Exception {
        Node userTaskNode = (Node) findElementByContentType(diagram,
                                                            UserTask.class);
        Node businessRuleTaskNode = (Node) findElementByContentType(diagram,
                                                                    BusinessRuleTask.class);
        Node scriptTaskNode = (Node) findElementByContentType(diagram,
                                                              ScriptTask.class);
        assertNotNull(userTaskNode);
        assertNotNull(businessRuleTaskNode);
        assertNotNull(scriptTaskNode);

        ViewConnector userTaskInEdgeConnector = getInEdgeViewConnector(userTaskNode);
        ViewConnector businessRuleTaskInEdgeConnector = getInEdgeViewConnector(businessRuleTaskNode);
        ViewConnector scriptTaskInEdgeConnector = getInEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskInEdgeConnector);
        assertNotNull(businessRuleTaskInEdgeConnector);
        assertNotNull(scriptTaskInEdgeConnector);

        ViewConnector userTaskOutEdgeConnector = getOutEdgeViewConnector(userTaskNode);
        ViewConnector businessRuleTaskOutEdgeConnector = getOutEdgeViewConnector(businessRuleTaskNode);
        ViewConnector scriptTaskOutEdgeConnector = getOutEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskOutEdgeConnector);
        assertNotNull(businessRuleTaskOutEdgeConnector);
        assertNotNull(scriptTaskOutEdgeConnector);

        // userTaskInEdgeConnector is from magnet top-middle to left-middle
        assertTrue(userTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskInEdgeConnector.getTargetConnection().isPresent());

        DiscreteConnection sourceConnection = (DiscreteConnection) userTaskInEdgeConnector.getSourceConnection().get();
        DiscreteConnection targetConnection = (DiscreteConnection) userTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(20d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // Assert both connections for userTaskInEdgeConnector are set to auto.
        assertTrue(sourceConnection.isAuto());
        assertTrue(targetConnection.isAuto());

        // businessRuleTaskInEdgeConnector is from magnet right-middle to top-left
        assertTrue(businessRuleTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(businessRuleTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) businessRuleTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) businessRuleTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(40d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(20d,
                     sourceConnection.getLocation().getY(),
                     0.1d);

        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // Assert both connections for businessRuleTaskInEdgeConnector are NOT set to auto.
        assertFalse(sourceConnection.isAuto());
        assertFalse(targetConnection.isAuto());

        // scriptTaskInEdgeConnector is from magnet left-bottom to left-bottom
        assertTrue(scriptTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) scriptTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) scriptTaskInEdgeConnector.getTargetConnection().get();

        assertEquals(0d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(40d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // userTaskOutEdgeConnector is from magnet right-middle to left-middle
        assertTrue(userTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) userTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) userTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // businessRuleTaskOutEdgeConnector is from magnet middle-bottom to middle-bottom
        assertTrue(businessRuleTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(businessRuleTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) businessRuleTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) businessRuleTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(68d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(28d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskOutEdgeConnector is from magnet left-top to left-top
        assertTrue(scriptTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (DiscreteConnection) scriptTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (DiscreteConnection) scriptTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(0d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);
    }

    @Test
    public void testUnmarshallMagnetsInLane() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_MAGNETSINLANE);

        testMagnetsInLane(diagram);
    }

    private void testMagnetsInLane(Diagram<Graph, Metadata> diagram) throws Exception {
        Node userTaskNode = (Node) findElementByContentType(diagram,
                                                            UserTask.class);
        Node scriptTaskNode = (Node) findElementByContentType(diagram,
                                                              ScriptTask.class);
        assertNotNull(userTaskNode);
        assertNotNull(scriptTaskNode);

        ViewConnector userTaskInEdgeConnector = getInEdgeViewConnector(userTaskNode);
        ViewConnector scriptTaskInEdgeConnector = getInEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskInEdgeConnector);
        assertNotNull(scriptTaskInEdgeConnector);

        ViewConnector userTaskOutEdgeConnector = getOutEdgeViewConnector(userTaskNode);
        ViewConnector scriptTaskOutEdgeConnector = getOutEdgeViewConnector(scriptTaskNode);
        assertNotNull(userTaskOutEdgeConnector);
        assertNotNull(scriptTaskOutEdgeConnector);

        // userTaskInEdgeConnector is from magnet right-middle to middle-top
        assertTrue(userTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskInEdgeConnector.getTargetConnection().isPresent());

        Connection sourceConnection = (Connection) userTaskInEdgeConnector.getSourceConnection().get();
        Connection targetConnection = (Connection) userTaskInEdgeConnector.getTargetConnection().get();
        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(24d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(68d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskInEdgeConnector is from magnet right-bottom to left-top
        assertTrue(scriptTaskInEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskInEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) scriptTaskInEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) scriptTaskInEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // userTaskOutEdgeConnector is from magnet right-bottom to left-top
        assertTrue(userTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(userTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) userTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) userTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(48d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getY(),
                     0.1d);

        // scriptTaskOutEdgeConnector is from magnet right-top to left-middle
        assertTrue(scriptTaskOutEdgeConnector.getSourceConnection().isPresent());
        assertTrue(scriptTaskOutEdgeConnector.getTargetConnection().isPresent());

        sourceConnection = (Connection) scriptTaskOutEdgeConnector.getSourceConnection().get();
        targetConnection = (Connection) scriptTaskOutEdgeConnector.getTargetConnection().get();

        assertEquals(136d,
                     sourceConnection.getLocation().getX(),
                     0.1d);
        assertEquals(0d,
                     sourceConnection.getLocation().getY(),
                     0.1d);
        assertEquals(0d,
                     targetConnection.getLocation().getX(),
                     0.1d);
        assertEquals(14d,
                     targetConnection.getLocation().getY(),
                     0.1d);
    }

    private ViewConnector getInEdgeViewConnector(Node node) {
        List<Edge> edges = node.getInEdges();
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getContent() instanceof ViewConnector) {
                    return (ViewConnector) edge.getContent();
                }
            }
        }
        return null;
    }

    private ViewConnector getOutEdgeViewConnector(Node node) {
        List<Edge> edges = node.getOutEdges();
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.getContent() instanceof ViewConnector) {
                    return (ViewConnector) edge.getContent();
                }
            }
        }
        return null;
    }

    private Element findElementByContentType(Diagram<Graph, Metadata> diagram,
                                             Class contentClass) {
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (contentClass.isInstance(oDefinition)) {
                    return element;
                }
            }
        }
        return null;
    }

    @Test
    public void testUnmarshallEmbeddedSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EMBEDDED_SUBPROCESS);
        EmbeddedSubprocess subprocess = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof EmbeddedSubprocess) {
                    subprocess = (EmbeddedSubprocess) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(subprocess);
    }

    @Test
    public void testUnmarshallSeveralDiagrams() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_EVALUATION);
        assertDiagram(diagram1,
                      8);
        assertEquals("Evaluation",
                     diagram1.getMetadata().getTitle());
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_LANES);
        assertDiagram(diagram,
                      7);
        assertEquals("Lanes test",
                     diagram.getMetadata().getTitle());
    }

    @Test
    public void testMarshallBasic() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BASIC);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
    }

    @Test
    public void testMarshallEvaluation() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);
    }

    @Test
    public void testMarshallNotBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_NOT_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      4);
    }

    @Test
    public void testMarshallBoundaryEvents() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BOUNDARY_EVENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      3);
        // Assert that the boundary event location and size are the expected ones.
        assertTrue(result.contains("Bounds height=\"30.0\" width=\"30.0\" x=\"312.0\" y=\"195.0\""));
    }

    @Test
    public void testMarshallProcessVariables() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSVARIABLES);
        JBPMBpmn2ResourceImpl resource = tested.marshallToBpmn2Resource(diagram);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);

        Definitions definitions = (Definitions) resource.getContents().get(0);
        assertNotNull(definitions);
        List<RootElement> rootElements = definitions.getRootElements();
        assertNotNull(rootElements);

        assertNotNull(getItemDefinition(rootElements,
                                        "_employeeItem",
                                        "java.lang.String"));
        assertNotNull(getItemDefinition(rootElements,
                                        "_reasonItem",
                                        "java.lang.String"));
        assertNotNull(getItemDefinition(rootElements,
                                        "_performanceItem",
                                        "java.lang.String"));

        Process process = getProcess(definitions);
        assertNotNull(process);
        List<Property> properties = process.getProperties();
        assertNotNull(properties);
        assertNotNull(getProcessProperty(properties,
                                         "employee",
                                         "_employeeItem"));
        assertNotNull(getProcessProperty(properties,
                                         "reason",
                                         "_reasonItem"));
        assertNotNull(getProcessProperty(properties,
                                         "performance",
                                         "_performanceItem"));
    }

    @Test
    public void testMarshallProcessProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_PROCESSPROPERTIES);
        JBPMBpmn2ResourceImpl resource = tested.marshallToBpmn2Resource(diagram);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        Definitions definitions = (Definitions) resource.getContents().get(0);
        assertNotNull(definitions);
        Process process = getProcess(definitions);
        assertNotNull(process);

        assertEquals("JDLProj.BPSimple",
                     process.getId());
        assertEquals("BPSimple",
                     process.getName());
        assertTrue(process.isIsExecutable());
        assertEquals("true",
                     getProcessPropertyValue(process,
                                             "adHoc"));
        assertEquals("org.jbpm",
                     getProcessPropertyValue(process,
                                             "packageName"));
        assertEquals("1.0",
                     getProcessPropertyValue(process,
                                             "version"));
        assertNotNull(process.getDocumentation());
        assertFalse(process.getDocumentation().isEmpty());
        assertEquals("<![CDATA[This is a\nsimple\nprocess]]>",
                     process.getDocumentation().get(0).getText());
        assertEquals("<![CDATA[This is the\nProcess\nInstance\nDescription]]>",
                     getProcessExtensionValue(process,
                                              "customDescription"));
    }

    @Test
    public void testMarshallUserTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNMENTS);
        JBPMBpmn2ResourceImpl resource = tested.marshallToBpmn2Resource(diagram);

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);

        Definitions definitions = (Definitions) resource.getContents().get(0);
        assertNotNull(definitions);
        Process process = getProcess(definitions);
        assertNotNull(process);
        org.eclipse.bpmn2.UserTask userTask = (org.eclipse.bpmn2.UserTask) getNamedFlowElement(process,
                                                                                               org.eclipse.bpmn2.UserTask.class,
                                                                                               "Self Evaluation");
        assertNotNull(userTask);
        DataInput dataInput = (DataInput) getDataInput(userTask,
                                                       "reason");
        validateDataInputOrOutput(dataInput,
                                  "_reasonInputX",
                                  "com.test.Reason",
                                  "_reasonInputXItem");
        DataOutput dataOutput = (DataOutput) getDataOutput(userTask,
                                                           "performance");
        validateDataInputOrOutput(dataOutput,
                                  "_performanceOutputX",
                                  "Object",
                                  "_performanceOutputXItem");

        ItemAwareElement sourceRef = getDataInputAssociationSourceRef(userTask,
                                                                      "reason");
        assertNotNull(sourceRef);

        ItemAwareElement targetRef = getDataInputAssociationTargetRef(userTask,
                                                                      "_reasonInputX");
        assertNotNull(targetRef);

        sourceRef = getDataOutputAssociationSourceRef(userTask,
                                                      "_performanceOutputX");
        assertNotNull(sourceRef);

        targetRef = getDataOutputAssociationTargetRef(userTask,
                                                      "performance");
        assertNotNull(targetRef);
    }

    @Test
    public void testMarshallBusinessRuleTaskAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataInput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputXItem\" name=\"input2\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputXItem\" name=\"output1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX\" drools:dtype=\"String\" itemSubjectRef=\"__45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input1InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_input2InputX</bpmn2:dataInputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output1OutputX</bpmn2:dataOutputRefs>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_45C2C340-D1D0-4D63-8419-EF38F9E73507_output2OutputX</bpmn2:dataOutputRefs>"));
    }

    @Test
    public void testMarshallStartTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTTIMEREVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("name=\"StartTimer\" isInterrupting=\"false\">"));
        assertTrue(result.contains("name=\"StartTimer\" isInterrupting=\"false\">"));
        assertTrue(result.contains("P4H</bpmn2:timeDuration>"));
        assertTrue(result.contains("language=\"cron\">*/2 * * * *</bpmn2:timeCycle>"));
    }

    @Test
    public void testMarshallStartSignalEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTSIGNALEVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        assertTrue(result.contains("<bpmn2:startEvent"));
        assertTrue(result.contains(" name=\"StartSignalEvent1\""));
        assertTrue(result.contains("<bpmn2:signal id=\"_47718ea6-a6a4-3ceb-9e93-2111bdad0b8c\" name=\"sig1\"/>"));
        assertTrue(result.contains("<bpmn2:signalEventDefinition"));
        assertTrue(result.contains("signalRef=\"_47718ea6-a6a4-3ceb-9e93-2111bdad0b8c\"/>"));
    }

    @Test
    public void testMarshallTimerIntermediateEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_INTERMEDIATE_TIMER_EVENT);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      1,
                      0);

        assertTrue(result.contains("<bpmn2:intermediateCatchEvent"));
        assertTrue(result.contains(" name=\"MyTimer\""));
        assertTrue(result.contains("<bpmn2:timerEventDefinition"));
        assertTrue(result.contains("<bpmn2:timeDate"));
        assertTrue(result.contains("<bpmn2:timeDuration"));
        assertTrue(result.contains("<bpmn2:timeCycle"));
    }

    @Test
    public void testMarshallStartEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_STARTEVENTASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1\" drools:dtype=\"String\" name=\"StartEventOutput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>employee</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataOutputRefs>_106AADA7-E381-4736-8BD6-7EB47A1D096B_StartEventOutput1</bpmn2:dataOutputRefs>"));
    }

    @Test
    public void testMarshallEndEventAssignments() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_ENDEVENTASSIGNMENTS);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      6);
        assertTrue(result.contains("<bpmn2:dataInput id=\"_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX\" drools:dtype=\"String\" name=\"EndTermEventInput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>employee</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_0493ECBF-3C0F-4086-9979-2391CE740AA8_EndTermEventInput1InputX</bpmn2:dataInputRefs>"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX\" drools:dtype=\"String\" name=\"EndNoneEventInput1\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>reason</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:dataInputRefs>_4824B9FD-6097-4E06-9E7E-2CE5E7601BC5_EndNoneEventInput1InputX</bpmn2:dataInputRefs>"));
    }

    @Test
    public void testMarshallReusableSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_REUSABLE_SUBPROCESS);
        assertDiagram(diagram,
                      4);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        assertTrue(result.contains("<bpmn2:callActivity id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB\" drools:independent=\"false\" drools:waitForCompletion=\"false\" name=\"my subprocess\" calledElement=\"my-called-element\">"));

        assertTrue(result.contains("<bpmn2:dataInput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX\" drools:dtype=\"String\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputXItem\" name=\"input1\"/>"));
        assertTrue(result.contains("<bpmn2:dataOutput id=\"_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX\" drools:dtype=\"Float\" itemSubjectRef=\"__FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputXItem\" name=\"output2\"/>"));
        assertTrue(result.contains("<bpmn2:sourceRef>pv1</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_input1InputX</bpmn2:targetRef>"));
        assertTrue(result.contains("<bpmn2:sourceRef>_FC6D8570-8C67-40C2-8B7B-953DE15765FB_output2OutputX</bpmn2:sourceRef>"));
        assertTrue(result.contains("<bpmn2:targetRef>pv2</bpmn2:targetRef>"));

        String flatResult = result.replace('\n',
                                           ' ').replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"elementname\"> <drools:metaValue><![CDATA[my subprocess]]></drools:metaValue> </drools:metaData>"));
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
    }

    @Test
    public void testMarshallEmbeddedSubprocess() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_EMBEDDED_SUBPROCESS);
        assertDiagram(diagram,
                      10);
        assertDocumentation(diagram,"_C3EBE7F1-8E57-4BB1-B380-40BB02E9464E", "Subprocess  Documentation Value");

        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      9,
                      7);

        assertTrue(result.contains("<bpmn2:subProcess id=\"_C3EBE7F1-8E57-4BB1-B380-40BB02E9464E\" "));
    }

    @Test
    public void testMarshallUserTaskAssignees() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKASSIGNEES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      5,
                      4);
        assertTrue(result.contains("<![CDATA[admin,kiemgmt]]>"));
        result = result.replace('\n',
                                ' ');
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user</bpmn2:formalExpression>(.*)"));
        assertTrue(result.matches("(.*)<bpmn2:resourceAssignmentExpression(.*)>user1</bpmn2:formalExpression>(.*)"));
    }

    @Test
    public void testMarshallUserTaskProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_USERTASKPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);
        assertTrue(result.contains("MyUserTask</bpmn2:from>"));
        String flatResult = result.replace('\n',
                                           ' ').replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
        assertTrue(flatResult.contains("<drools:metaData name=\"customAutoStart\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));

        assertTrue(flatResult.contains("<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">"));
        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Hello\");]]></drools:script>"));
        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Bye\");]]></drools:script>"));
    }

    @Test
    public void testMarshallSimulationProperties() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SIMULATIONPROPERTIES);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      3,
                      2);

        result = result.replaceAll("\\s+",
                                   " ");
        result = result.replaceAll("> <",
                                   "><");
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:PoissonDistribution mean=\"321.0\"/>"));
        assertTrue(result.contains("<bpsim:ResourceParameters xsi:type=\"bpsim:ResourceParameters\"><bpsim:Availability xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"999.0\"/>"));
        assertTrue(result.contains("<bpsim:Quantity xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"111.0\"/></bpsim:Quantity>"));
        assertTrue(result.contains("<bpsim:CostParameters xsi:type=\"bpsim:CostParameters\"><bpsim:UnitCost xsi:type=\"bpsim:Parameter\"><bpsim:FloatingParameter value=\"123.0\"/>"));
        assertTrue(result.contains("<bpsim:TimeParameters xsi:type=\"bpsim:TimeParameters\"><bpsim:ProcessingTime xsi:type=\"bpsim:Parameter\"><bpsim:UniformDistribution max=\"10.0\" min=\"5.0\"/>"));
    }

    @Test
    public void testMarshallEvaluationTwice() throws Exception {
        Diagram diagram = unmarshall(BPMN_EVALUATION);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      7,
                      7);
        Diagram diagram2 = unmarshall(BPMN_EVALUATION);
        String result2 = tested.marshall(diagram2);
        assertDiagram(result2,
                      1,
                      7,
                      7);
    }

    @Test
    public void testMarshallScriptTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SCRIPTTASK);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      4,
                      3);
        assertTrue(result.contains("name=\"Javascript Script Task\" scriptFormat=\"http://www.javascript.com/javascript\""));
        assertTrue(result.contains("name=\"Java Script Task\" scriptFormat=\"http://www.java.com/java\""));

        assertTrue(result.contains("<bpmn2:script><![CDATA[var str = FirstName + LastName;]]></bpmn2:script>"));
        assertTrue(result.contains("<bpmn2:script><![CDATA[if (name.toString().equals(\"Jay\")) {\n" +
                                           "\n" +
                                           "      System.out.println(\"Hello\\n\" + name.toString() + \"\\n\");\n" +
                                           "\n" +
                                           "} else {\n" +
                                           "\n" +
                                           "\n" +
                                           "  System.out.println(\"Hi\\n\" + name.toString() + \"\\n\");\n" +
                                           "\n" +
                                           "\n" +
                                           "}\n" +
                                           "]]></bpmn2:script>"));

        String flatResult = result.replace('\n',
                                           ' ').replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));
    }

    @Test
    public void testMarshallSequenceFlow() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_SEQUENCEFLOW);
        assertConditionLanguage(diagram,
                                "_C9F8F30D-E772-4504-A480-6EC894B289DC",
                                "javascript");
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      5);
        assertTrue(result.contains("language=\"http://www.javascript.com/javascript\"><![CDATA[age >= 10;]]></bpmn2:conditionExpression>"));
        assertTrue(result.contains("language=\"http://www.java.com/java\"><![CDATA[age\n" +
                                       "<\n" +
                                       "10;]]></bpmn2:conditionExpression>"));
    }

    private void assertConditionLanguage(Diagram<Graph, Metadata> diagram,
                                         String id,
                                         String value) {
        List<Node> nodes = getNodes(diagram);
        Optional<SequenceFlow> sequenceFlow =
            Stream.concat(nodes.stream().flatMap(node -> {
                              List<Edge> d = node.getInEdges();
                              return d.stream();
                          }),
                          nodes.stream().flatMap(node -> {
                              List<Edge> d = node.getOutEdges();
                              return d.stream();
                          }))
                .filter(edge -> edge.getUUID().equals(id))
                .map(node -> (View) node.getContent())
                .filter(view -> view.getDefinition() instanceof SequenceFlow)
                .map(view -> ((SequenceFlow) view.getDefinition()))
                .findFirst();

        String conditionLanguage = (sequenceFlow.isPresent() ? sequenceFlow.get().getExecutionSet().getConditionExpressionLanguage().getValue() : null);
        assertEquals(value,
                     conditionLanguage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMarshallBusinessRuleTask() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_BUSINESSRULETASKRULEFLOWGROUP);
        String result = tested.marshall(diagram);
        assertDiagram(diagram,
                      2);

        assertTrue(result.contains("<bpmn2:businessRuleTask "));
        String flatResult = result.replace('\n',
                                           ' ').replaceAll("( )+",
                                                           " ");
        assertTrue(flatResult.contains("<drools:metaData name=\"customAsync\"> <drools:metaValue><![CDATA[true]]></drools:metaValue>"));

        assertTrue(flatResult.contains("<drools:onEntry-script scriptFormat=\"http://www.java.com/java\">"));

        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Hello\");]]></drools:script>"));

        assertTrue(flatResult.contains("<drools:script><![CDATA[System.out.println(\"Bye\");]]></drools:script>"));
    }

    @Test
    public void testMarshallXorGateway() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_XORGATEWAY);
        String result = tested.marshall(diagram);
        assertDiagram(result,
                      1,
                      6,
                      5);
        assertTrue(result.contains("<bpmn2:exclusiveGateway id=\"_877EA035-1A14-42E9-8CAA-43E9BF908C70\" drools:dg=\"under 10 : _5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\" name=\"AgeSplit\" gatewayDirection=\"Diverging\" default=\"_5110D608-BDAD-47BF-A3F9-E1DBE43ED7CD\">"));
    }

    @Test
    public void testMarshallIntermediateTimerEvent() throws Exception {
        Diagram<Graph, Metadata> diagram = unmarshall(BPMN_TIMER_EVENT);
        IntermediateTimerEvent timerEvent = null;
        Iterator<Element> it = nodesIterator(diagram);
        while (it.hasNext()) {
            Element element = it.next();
            if (element.getContent() instanceof View) {
                Object oDefinition = ((View) element.getContent()).getDefinition();
                if (oDefinition instanceof IntermediateTimerEvent) {
                    timerEvent = (IntermediateTimerEvent) oDefinition;
                    break;
                }
            }
        }
        assertNotNull(timerEvent);
        assertNotNull(timerEvent.getGeneral());
        assertNotNull(timerEvent.getExecutionSet());

        assertEquals("myTimeDateValue",
                     timerEvent.getExecutionSet().getTimeDate().getValue());
        assertEquals("MyTimeDurationValue",
                     timerEvent.getExecutionSet().getTimeDuration().getValue());
        assertEquals("myTimeCycleValue",
                     timerEvent.getExecutionSet().getTimeCycle().getValue());
        assertEquals("cron",
                     timerEvent.getExecutionSet().getTimeCycleLanguage().getValue());
    }

    @Test
    public void testMarshallMagnetDockers() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_MAGNETDOCKERS);
        String result = tested.marshall(diagram1);
        assertDiagram(result,
                      1,
                      8,
                      7);
        Diagram<Graph, Metadata> diagram2 = unmarshall(new ByteArrayInputStream(result.getBytes()));
        testMagnetDockers(diagram2);
    }

    @Test
    public void testMarshallMagnetsInlane() throws Exception {
        Diagram<Graph, Metadata> diagram1 = unmarshall(BPMN_MAGNETSINLANE);
        String result = tested.marshall(diagram1);
        assertDiagram(result,
                      1,
                      6,
                      4);

        // Check the waypoints are as in the original process
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"371.0\" y=\"86.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"406.0\" y=\"324.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"692.0\" y=\"276.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"805.0\" y=\"76.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"81.0\" y=\"86.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"235.0\" y=\"86.0\"/>"));

        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"474.0\" y=\"372.0\"/>"));
        assertTrue(result.contains("<di:waypoint xsi:type=\"dc:Point\" x=\"556.0\" y=\"276.0\"/>"));

        // Test unmarshall
        Diagram<Graph, Metadata> diagram2 = unmarshall(new ByteArrayInputStream(result.getBytes()));
        testMagnetsInLane(diagram2);
    }

    private void assertDiagram(String result,
                               int diagramCount,
                               int nodeCount,
                               int edgeCount) {
        int d = count(result,
                      "<bpmndi:BPMNDiagram");
        int n = count(result,
                      "<bpmndi:BPMNShape");
        int e = count(result,
                      "<bpmndi:BPMNEdge");
        assertEquals(diagramCount,
                     d);
        assertEquals(nodeCount,
                     n);
        assertEquals(edgeCount,
                     e);
    }

    private void assertDiagram(Diagram<Graph, Metadata> diagram,
                               int nodesSize) {
        assertEquals(nodesSize,
                getNodes(diagram).size());
    }

    private List<Node> getNodes(Diagram<Graph, Metadata> diagram) {
        Graph graph = diagram.getGraph();
        assertNotNull(graph);
        Iterator<Node> nodesIterable = graph.nodes().iterator();
        List<Node> nodes = new ArrayList<>();
        nodesIterable.forEachRemaining(nodes::add);
        return nodes;
    }

    private void assertDocumentation(Diagram<Graph, Metadata> diagram, String id, String value) {
        Optional<BPMNDefinition> documentation = getNodes(diagram).stream()
                .filter(node -> node.getContent() instanceof View && node.getUUID().equals(id))
                .map(node -> (View) node.getContent())
                .filter(view -> view.getDefinition() instanceof BPMNDefinition)
                .map(view -> (BPMNDefinition) view.getDefinition())
                .findFirst();
        String documentationValue = (documentation.isPresent() ? documentation.get().getGeneral().getDocumentation().getValue() : null);
        assertEquals(value, documentationValue);
    }

    private Diagram<Graph, Metadata> unmarshall(String fileName) throws Exception {
        InputStream is = loadStream(fileName);
        return unmarshall(is);
    }

    private Diagram<Graph, Metadata> unmarshall(InputStream is) throws Exception {
        Metadata metadata =
                new MetadataImpl.MetadataImplBuilder(BindableAdapterUtils.getDefinitionSetId(BPMNDefinitionSet.class)).build();
        DiagramImpl result = new DiagramImpl(org.kie.workbench.common.stunner.core.util.UUID.uuid(),
                                             metadata);
        Graph graph = tested.unmarshall(metadata,
                                        is);
        result.setGraph(graph);
        // Update diagram's metadata attributes.
        tested.updateRootUUID(result.getMetadata(),
                              graph);
        tested.updateTitle(result.getMetadata(),
                           graph);
        return result;
    }

    @SuppressWarnings("unchecked")
    private Iterator<Element> nodesIterator(Diagram<Graph, Metadata> diagram) {
        return (Iterator<Element>) diagram.getGraph().nodes().iterator();
    }

    private InputStream loadStream(String path) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
    }

    private static int count(final String string,
                             final String substring) {
        int count = 0;
        int idx = 0;
        while ((idx = string.indexOf(substring,
                                     idx)) != -1) {
            idx++;
            count++;
        }
        return count;
    }

    private Process getProcess(Definitions definitions) {
        Object o = Arrays.stream(definitions.getRootElements().toArray())
                .filter(x -> Process.class.isInstance(x))
                .findFirst()
                .orElse(null);
        return (Process) o;
    }

    private ItemDefinition getItemDefinition(List<RootElement> rootElements,
                                             String id,
                                             String structureRef) {
        for (RootElement rootElement : rootElements) {
            if (id.equals(rootElement.getId()) && rootElement instanceof ItemDefinition) {
                ItemDefinition itemDefinition = (ItemDefinition) rootElement;
                if (structureRef.equals(itemDefinition.getStructureRef())) {
                    return itemDefinition;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private Property getProcessProperty(List<Property> properties,
                                        String id,
                                        String itemSubjectRef) {
        for (Property property : properties) {
            if (id.equals(property.getId())) {
                if (itemSubjectRef.equals(property.getItemSubjectRef().getId())) {
                    return property;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private String getProcessPropertyValue(Process process,
                                           String propertyName) {
        Iterator<FeatureMap.Entry> iter = process.getAnyAttribute().iterator();
        while (iter.hasNext()) {
            FeatureMap.Entry entry = iter.next();
            if (propertyName.equals(entry.getEStructuralFeature().getName())) {
                return entry.getValue().toString();
            }
        }
        return null;
    }

    private String getProcessExtensionValue(Process process,
                                            String propertyName) {
        List<ExtensionAttributeValue> extensionValues = process.getExtensionValues();
        for (ExtensionAttributeValue extensionValue : extensionValues) {
            FeatureMap featureMap = extensionValue.getValue();
            for (int i = 0; i < featureMap.size(); i++) {
                EStructuralFeatureImpl.SimpleFeatureMapEntry featureMapEntry = (EStructuralFeatureImpl.SimpleFeatureMapEntry) featureMap.get(i);
                MetaDataType featureMapValue = (MetaDataType) featureMapEntry.getValue();
                if (propertyName.equals(featureMapValue.getName())) {
                    return featureMapValue.getMetaValue();
                }
            }
        }
        return "";
    }

    private Object getNamedFlowElement(Process process,
                                       Class cls,
                                       String name) {
        List<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (cls.isInstance(flowElement) && name.equals(flowElement.getName())) {
                return flowElement;
            }
        }
        return null;
    }

    private DataInput getDataInput(Activity activity,
                                   String name) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification != null) {
            List<DataInput> dataInputs = ioSpecification.getDataInputs();
            if (dataInputs != null) {
                return Arrays.stream(dataInputs.toArray(new DataInput[dataInputs.size()]))
                        .filter(dataInput -> name.equals(dataInput.getName()))
                        .findFirst()
                        .orElse(null);
            }
        }

        return null;
    }

    private DataOutput getDataOutput(Activity activity,
                                     String name) {
        InputOutputSpecification ioSpecification = activity.getIoSpecification();
        if (ioSpecification != null) {
            List<DataOutput> dataOutputs = ioSpecification.getDataOutputs();
            if (dataOutputs != null) {
                return Arrays.stream(dataOutputs.toArray(new DataOutput[dataOutputs.size()]))
                        .filter(dataOutput -> name.equals(dataOutput.getName()))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    private void validateDataInputOrOutput(ItemAwareElement itemAwareElement,
                                           String idSuffix,
                                           String dataType,
                                           String itemSubjectRefSuffix) {
        assertNotNull(itemAwareElement);

        assertTrue(itemAwareElement.getId().endsWith(idSuffix));
        ItemDefinition itemDefinition = itemAwareElement.getItemSubjectRef();
        assertNotNull(itemDefinition);
        assertTrue(itemDefinition.getStructureRef().equals(dataType));
        assertTrue(itemDefinition.getId().endsWith(itemSubjectRefSuffix));
    }

    private ItemAwareElement getDataInputAssociationSourceRef(Activity activity,
                                                              String id) {
        List<DataInputAssociation> dataInputAssociations = activity.getDataInputAssociations();
        if (dataInputAssociations != null) {
            for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
                List<ItemAwareElement> sourceRef = dataInputAssociation.getSourceRef();
                if (sourceRef != null && !sourceRef.isEmpty()) {
                    ItemAwareElement result = Arrays.stream(sourceRef.toArray(new ItemAwareElement[sourceRef.size()]))
                            .filter(itemAwareElement -> id.equals(itemAwareElement.getId()))
                            .findFirst()
                            .orElse(null);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataInputAssociationTargetRef(Activity activity,
                                                              String idSuffix) {
        List<DataInputAssociation> dataInputAssociations = activity.getDataInputAssociations();
        if (dataInputAssociations != null) {
            for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
                ItemAwareElement targetRef = dataInputAssociation.getTargetRef();
                if (targetRef != null && targetRef.getId().endsWith(idSuffix)) {
                    return targetRef;
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataOutputAssociationSourceRef(Activity activity,
                                                               String idSuffix) {
        List<DataOutputAssociation> dataOutputAssociations = activity.getDataOutputAssociations();
        if (dataOutputAssociations != null) {
            for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
                List<ItemAwareElement> sourceRef = dataOutputAssociation.getSourceRef();
                if (sourceRef != null && !sourceRef.isEmpty()) {
                    ItemAwareElement result = Arrays.stream(sourceRef.toArray(new ItemAwareElement[sourceRef.size()]))
                            .filter(itemAwareElement -> itemAwareElement.getId().endsWith(idSuffix))
                            .findFirst()
                            .orElse(null);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private ItemAwareElement getDataOutputAssociationTargetRef(Activity activity,
                                                               String id) {
        List<DataOutputAssociation> dataOutputAssociations = activity.getDataOutputAssociations();
        if (dataOutputAssociations != null) {
            for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
                ItemAwareElement targetRef = dataOutputAssociation.getTargetRef();
                if (targetRef != null && id.equals(targetRef.getId())) {
                    return targetRef;
                }
            }
        }
        return null;
    }


}
