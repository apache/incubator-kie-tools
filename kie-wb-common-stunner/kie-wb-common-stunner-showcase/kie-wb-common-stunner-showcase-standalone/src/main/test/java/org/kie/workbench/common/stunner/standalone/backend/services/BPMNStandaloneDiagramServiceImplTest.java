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

package org.kie.workbench.common.stunner.standalone.backend.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.backend.BPMNBackendService;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.AdHoc;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.DiagramSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Executable;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.GlobalVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.ProcessInstanceDescription;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Version;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.imports.Imports;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.SLADueDate;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.factory.BPMNDiagramFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.api.ShapeManager;
import org.kie.workbench.common.stunner.core.definition.DefinitionSetResourceType;
import org.kie.workbench.common.stunner.core.definition.service.DiagramMarshaller;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.registry.definition.TypeDefinitionSetRegistry;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BPMNStandaloneDiagramServiceImplTest {

    private BPMNStandaloneDiagramServiceImpl service;

    @Mock
    private DefinitionManager definitionManager;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private BPMNBackendService backendService;

    @Mock
    private DiagramMarshaller marshaller;

    @Mock
    private BPMNDiagramFactory diagramFactory;

    @Mock
    private ShapeManager shapeManager;

    @Mock
    private Promises promises;

    @Mock
    public TypeDefinitionSetRegistry definitionSetRegistry;

    @Mock
    Graph<DefinitionSet, Node> graph;

    @Mock
    DefinitionSetResourceType resourceType;

    private Name processName;

    private Documentation processDocumentation;

    private Id processId;

    private Package packageProperty;

    private Version version;

    private AdHoc adHoc;

    private ProcessInstanceDescription processInstanceDescription;

    private Executable executable;

    private ProcessData processData;

    @Mock
    private Imports imports;

    private GlobalVariables globalVariables;

    @Mock
    private SLADueDate slaDueDate;

    private DiagramSet diagramSet;

    private BPMNDiagramImpl bpmnDiagram;

    private List<Node> nodes;

    @Before
    public void setUp() {

        service = new BPMNStandaloneDiagramServiceImpl(definitionManager, factoryManager, backendService, diagramFactory);

        //DiagramSet
        processName = new Name("someName");
        processDocumentation = new Documentation("someDocumentation");
        packageProperty = new Package("some.package");
        version = new Version("1.0");
        adHoc = new AdHoc(false);
        processInstanceDescription = new ProcessInstanceDescription("description");
        executable = new Executable(false);
        processId = new Id("someUUID");
        globalVariables = new GlobalVariables("GL1:java.lang.String:false,GL2:java.lang.Boolean:false");
        slaDueDate = new SLADueDate("");

        diagramSet = new DiagramSet(processName,
                                    processDocumentation,
                                    processId,
                                    packageProperty,
                                    version,
                                    adHoc,
                                    processInstanceDescription,
                                    globalVariables,
                                    imports,
                                    executable,
                                    slaDueDate);

        bpmnDiagram = new BPMNDiagramImpl(
                diagramSet,
                processData,
                new CaseManagementSet(),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet()
        );

        nodes = Arrays.asList(createNode(bpmnDiagram));
    }

    private Node createNode(Object content) {
        NodeImpl node = new NodeImpl(UUID.uuid());
        node.setContent(new ViewImpl<>(content, new Bounds(new Bound(0d, 0d), new Bound(1d, 1d))));
        return node;
    }

    @Test
    public void testNameAndIdAsFileName() throws IOException {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(backendService.getDiagramMarshaller()).thenReturn(marshaller);
        when(marshaller.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        when(backendService.getResourceType()).thenReturn(resourceType);
        doReturn(BPMNDefinitionSet.class).when(resourceType).getDefinitionSetType();

        service.transform("someFile", "<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\r\\n<bpmn2:definitions\\r\\n  id=\\\"_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282\\\"\\r\\n  exporter=\\\"jBPM Process Modeler\\\"\\r\\n  exporterVersion=\\\"2.0\\\"\\r\\n  targetNamespace=\\\"http:\\/\\/www.omg.org\\/bpmn20\\\"\\r\\n  xmlns:bpmn2=\\\"http:\\/\\/www.omg.org\\/spec\\/BPMN\\/20100524\\/MODEL\\\"\\r\\n  xmlns:bpmndi=\\\"http:\\/\\/www.omg.org\\/spec\\/BPMN\\/20100524\\/DI\\\"\\r\\n  xmlns:bpsim=\\\"http:\\/\\/www.bpsim.org\\/schemas\\/1.0\\\"\\r\\n  xmlns:dc=\\\"http:\\/\\/www.omg.org\\/spec\\/DD\\/20100524\\/DC\\\"\\r\\n  xmlns:di=\\\"http:\\/\\/www.omg.org\\/spec\\/DD\\/20100524\\/DI\\\"\\r\\n  xmlns:drools=\\\"http:\\/\\/www.jboss.org\\/drools\\\"><bpmn2:itemDefinition id=\\\"_tripItem\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessageType\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessageType\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"__25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputXItem\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:message id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessage\\\" itemRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessageType\\\"\\/><bpmn2:message id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessage\\\" itemRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessageType\\\"\\/>\\r\\n  <bpmn2:interface id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceInterface\\\" name=\\\"org.acme.travels.service.FlightBookingService\\\" implementationRef=\\\"org.acme.travels.service.FlightBookingService\\\">\\r\\n    <bpmn2:operation id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceOperation\\\" name=\\\"bookFlight\\\" implementationRef=\\\"bookFlight\\\">\\r\\n      <bpmn2:inMessageRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessage<\\/bpmn2:inMessageRef>\\r\\n      <bpmn2:outMessageRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessage<\\/bpmn2:outMessageRef>\\r\\n    <\\/bpmn2:operation>\\r\\n  <\\/bpmn2:interface>\\r\\n  <bpmn2:process id=\\\"flights\\\" drools:packageName=\\\"org.acme.travels\\\" drools:version=\\\"1.0\\\" drools:adHoc=\\\"false\\\" name=\\\"Flights\\\" isExecutable=\\\"true\\\" processType=\\\"Public\\\"><bpmn2:property id=\\\"trip\\\" itemSubjectRef=\\\"_tripItem\\\" name=\\\"trip\\\"\\/>\\r\\n    <bpmn2:sequenceFlow id=\\\"_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C\\\" sourceRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\" targetRef=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"isAutoConnection.source\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n        <drools:metaData name=\\\"isAutoConnection.target\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n    <\\/bpmn2:sequenceFlow>\\r\\n    <bpmn2:sequenceFlow id=\\\"_4C4024AC-D6CA-4B58-BCEF-D667DA948201\\\" sourceRef=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\" targetRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"isAutoConnection.source\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n        <drools:metaData name=\\\"isAutoConnection.target\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n    <\\/bpmn2:sequenceFlow>\\r\\n    <bpmn2:endEvent id=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\">\\r\\n      <bpmn2:incoming>_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C<\\/bpmn2:incoming>\\r\\n    <\\/bpmn2:endEvent>\\r\\n    <bpmn2:serviceTask\\r\\n      id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\"\\r\\n      drools:serviceimplementation=\\\"Java\\\"\\r\\n      drools:serviceinterface=\\\"org.acme.travels.service.FlightBookingService\\\"\\r\\n      drools:serviceoperation=\\\"bookFlight\\\"\\r\\n      name=\\\"Task\\\"\\r\\n      implementation=\\\"Java\\\"\\r\\n      operationRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceOperation\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"elementname\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[Task]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n      <bpmn2:incoming>_4C4024AC-D6CA-4B58-BCEF-D667DA948201<\\/bpmn2:incoming>\\r\\n      <bpmn2:outgoing>_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C<\\/bpmn2:outgoing>\\r\\n      <bpmn2:ioSpecification id=\\\"_xjPdgNF2EDeo0dqbkXENZQ\\\"><bpmn2:dataInput id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX\\\" drools:dtype=\\\"org.acme.travels.Trip\\\" itemSubjectRef=\\\"__25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputXItem\\\" name=\\\"Parameter\\\"\\/>\\r\\n        <bpmn2:inputSet id=\\\"_xjQEkNF2EDeo0dqbkXENZQ\\\">\\r\\n          <bpmn2:dataInputRefs>_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX<\\/bpmn2:dataInputRefs>\\r\\n        <\\/bpmn2:inputSet>\\r\\n      <\\/bpmn2:ioSpecification>\\r\\n      <bpmn2:dataInputAssociation id=\\\"_xjQEkdF2EDeo0dqbkXENZQ\\\">\\r\\n        <bpmn2:sourceRef>trip<\\/bpmn2:sourceRef>\\r\\n        <bpmn2:targetRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX<\\/bpmn2:targetRef>\\r\\n      <\\/bpmn2:dataInputAssociation>\\r\\n    <\\/bpmn2:serviceTask>\\r\\n    <bpmn2:startEvent id=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\">\\r\\n      <bpmn2:outgoing>_4C4024AC-D6CA-4B58-BCEF-D667DA948201<\\/bpmn2:outgoing>\\r\\n    <\\/bpmn2:startEvent>\\r\\n  <\\/bpmn2:process>\\r\\n  <bpmndi:BPMNDiagram>\\r\\n    <bpmndi:BPMNPlane bpmnElement=\\\"flights\\\">\\r\\n      <bpmndi:BPMNShape id=\\\"shape__578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\" bpmnElement=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\"><dc:Bounds height=\\\"56\\\" width=\\\"56\\\" x=\\\"100\\\" y=\\\"100\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNShape id=\\\"shape__25ADBBE9-1825-4910-99C5-1409422D46AB\\\" bpmnElement=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\"><dc:Bounds height=\\\"102\\\" width=\\\"154\\\" x=\\\"236\\\" y=\\\"77\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNShape id=\\\"shape__3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\" bpmnElement=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\"><dc:Bounds height=\\\"56\\\" width=\\\"56\\\" x=\\\"470\\\" y=\\\"100\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNEdge id=\\\"edge_shape__578F1284-FFB5-4A5A-B628-A27238DD2B5B_to_shape__25ADBBE9-1825-4910-99C5-1409422D46AB\\\" bpmnElement=\\\"_4C4024AC-D6CA-4B58-BCEF-D667DA948201\\\"><di:waypoint x=\\\"156\\\" y=\\\"128\\\"\\/><di:waypoint x=\\\"236\\\" y=\\\"128\\\"\\/><\\/bpmndi:BPMNEdge>\\r\\n      <bpmndi:BPMNEdge id=\\\"edge_shape__25ADBBE9-1825-4910-99C5-1409422D46AB_to_shape__3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\" bpmnElement=\\\"_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C\\\"><di:waypoint x=\\\"390\\\" y=\\\"128\\\"\\/><di:waypoint x=\\\"470\\\" y=\\\"128\\\"\\/><\\/bpmndi:BPMNEdge>\\r\\n    <\\/bpmndi:BPMNPlane>\\r\\n  <\\/bpmndi:BPMNDiagram>\\r\\n  <bpmn2:relationship id=\\\"_xjRSsNF2EDeo0dqbkXENZQ\\\" type=\\\"BPSimData\\\">\\r\\n    <bpmn2:extensionElements>\\r\\n      <bpsim:BPSimData>\\r\\n        <bpsim:Scenario id=\\\"default\\\" name=\\\"Simulationscenario\\\"><bpsim:ScenarioParameters\\/>\\r\\n          <bpsim:ElementParameters elementRef=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\">\\r\\n            <bpsim:TimeParameters>\\r\\n              <bpsim:ProcessingTime><bpsim:NormalDistribution mean=\\\"0\\\" standardDeviation=\\\"0\\\"\\/><\\/bpsim:ProcessingTime>\\r\\n            <\\/bpsim:TimeParameters>\\r\\n          <\\/bpsim:ElementParameters>\\r\\n          <bpsim:ElementParameters elementRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\">\\r\\n            <bpsim:TimeParameters>\\r\\n              <bpsim:ProcessingTime><bpsim:NormalDistribution mean=\\\"0\\\" standardDeviation=\\\"0\\\"\\/><\\/bpsim:ProcessingTime>\\r\\n            <\\/bpsim:TimeParameters>\\r\\n            <bpsim:ResourceParameters>\\r\\n              <bpsim:Availability><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:Availability>\\r\\n              <bpsim:Quantity><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:Quantity>\\r\\n            <\\/bpsim:ResourceParameters>\\r\\n            <bpsim:CostParameters>\\r\\n              <bpsim:UnitCost><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:UnitCost>\\r\\n            <\\/bpsim:CostParameters>\\r\\n          <\\/bpsim:ElementParameters>\\r\\n        <\\/bpsim:Scenario>\\r\\n      <\\/bpsim:BPSimData>\\r\\n    <\\/bpmn2:extensionElements>\\r\\n    <bpmn2:source>_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282<\\/bpmn2:source>\\r\\n    <bpmn2:target>_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282<\\/bpmn2:target>\\r\\n  <\\/bpmn2:relationship>\\r\\n<\\/bpmn2:definitions>");

        assertEquals(diagramSet.getName().getValue(), "someFile");
        assertEquals(diagramSet.getId().getValue(), "someFile");
    }

    @Test
    public void testNameAndIdAsXML() throws IOException {

        when(definitionManager.definitionSets()).thenReturn(definitionSetRegistry);
        when(backendService.getDiagramMarshaller()).thenReturn(marshaller);
        when(marshaller.unmarshall(any(), any())).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        when(backendService.getResourceType()).thenReturn(resourceType);
        doReturn(BPMNDefinitionSet.class).when(resourceType).getDefinitionSetType();

        service.transform("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\r\\n<bpmn2:definitions\\r\\n  id=\\\"_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282\\\"\\r\\n  exporter=\\\"jBPM Process Modeler\\\"\\r\\n  exporterVersion=\\\"2.0\\\"\\r\\n  targetNamespace=\\\"http:\\/\\/www.omg.org\\/bpmn20\\\"\\r\\n  xmlns:bpmn2=\\\"http:\\/\\/www.omg.org\\/spec\\/BPMN\\/20100524\\/MODEL\\\"\\r\\n  xmlns:bpmndi=\\\"http:\\/\\/www.omg.org\\/spec\\/BPMN\\/20100524\\/DI\\\"\\r\\n  xmlns:bpsim=\\\"http:\\/\\/www.bpsim.org\\/schemas\\/1.0\\\"\\r\\n  xmlns:dc=\\\"http:\\/\\/www.omg.org\\/spec\\/DD\\/20100524\\/DC\\\"\\r\\n  xmlns:di=\\\"http:\\/\\/www.omg.org\\/spec\\/DD\\/20100524\\/DI\\\"\\r\\n  xmlns:drools=\\\"http:\\/\\/www.jboss.org\\/drools\\\"><bpmn2:itemDefinition id=\\\"_tripItem\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessageType\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessageType\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:itemDefinition id=\\\"__25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputXItem\\\" structureRef=\\\"org.acme.travels.Trip\\\"\\/><bpmn2:message id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessage\\\" itemRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessageType\\\"\\/><bpmn2:message id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessage\\\" itemRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessageType\\\"\\/>\\r\\n  <bpmn2:interface id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceInterface\\\" name=\\\"org.acme.travels.service.FlightBookingService\\\" implementationRef=\\\"org.acme.travels.service.FlightBookingService\\\">\\r\\n    <bpmn2:operation id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceOperation\\\" name=\\\"bookFlight\\\" implementationRef=\\\"bookFlight\\\">\\r\\n      <bpmn2:inMessageRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_InMessage<\\/bpmn2:inMessageRef>\\r\\n      <bpmn2:outMessageRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_OutMessage<\\/bpmn2:outMessageRef>\\r\\n    <\\/bpmn2:operation>\\r\\n  <\\/bpmn2:interface>\\r\\n  <bpmn2:process id=\\\"flights\\\" drools:packageName=\\\"org.acme.travels\\\" drools:version=\\\"1.0\\\" drools:adHoc=\\\"false\\\" name=\\\"Flights\\\" isExecutable=\\\"true\\\" processType=\\\"Public\\\"><bpmn2:property id=\\\"trip\\\" itemSubjectRef=\\\"_tripItem\\\" name=\\\"trip\\\"\\/>\\r\\n    <bpmn2:sequenceFlow id=\\\"_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C\\\" sourceRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\" targetRef=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"isAutoConnection.source\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n        <drools:metaData name=\\\"isAutoConnection.target\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n    <\\/bpmn2:sequenceFlow>\\r\\n    <bpmn2:sequenceFlow id=\\\"_4C4024AC-D6CA-4B58-BCEF-D667DA948201\\\" sourceRef=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\" targetRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"isAutoConnection.source\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n        <drools:metaData name=\\\"isAutoConnection.target\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[true]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n    <\\/bpmn2:sequenceFlow>\\r\\n    <bpmn2:endEvent id=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\">\\r\\n      <bpmn2:incoming>_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C<\\/bpmn2:incoming>\\r\\n    <\\/bpmn2:endEvent>\\r\\n    <bpmn2:serviceTask\\r\\n      id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\"\\r\\n      drools:serviceimplementation=\\\"Java\\\"\\r\\n      drools:serviceinterface=\\\"org.acme.travels.service.FlightBookingService\\\"\\r\\n      drools:serviceoperation=\\\"bookFlight\\\"\\r\\n      name=\\\"Task\\\"\\r\\n      implementation=\\\"Java\\\"\\r\\n      operationRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ServiceOperation\\\">\\r\\n      <bpmn2:extensionElements>\\r\\n        <drools:metaData name=\\\"elementname\\\">\\r\\n          <drools:metaValue>\\r\\n            <![CDATA[Task]]>\\r\\n          <\\/drools:metaValue>\\r\\n        <\\/drools:metaData>\\r\\n      <\\/bpmn2:extensionElements>\\r\\n      <bpmn2:incoming>_4C4024AC-D6CA-4B58-BCEF-D667DA948201<\\/bpmn2:incoming>\\r\\n      <bpmn2:outgoing>_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C<\\/bpmn2:outgoing>\\r\\n      <bpmn2:ioSpecification id=\\\"_xjPdgNF2EDeo0dqbkXENZQ\\\"><bpmn2:dataInput id=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX\\\" drools:dtype=\\\"org.acme.travels.Trip\\\" itemSubjectRef=\\\"__25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputXItem\\\" name=\\\"Parameter\\\"\\/>\\r\\n        <bpmn2:inputSet id=\\\"_xjQEkNF2EDeo0dqbkXENZQ\\\">\\r\\n          <bpmn2:dataInputRefs>_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX<\\/bpmn2:dataInputRefs>\\r\\n        <\\/bpmn2:inputSet>\\r\\n      <\\/bpmn2:ioSpecification>\\r\\n      <bpmn2:dataInputAssociation id=\\\"_xjQEkdF2EDeo0dqbkXENZQ\\\">\\r\\n        <bpmn2:sourceRef>trip<\\/bpmn2:sourceRef>\\r\\n        <bpmn2:targetRef>_25ADBBE9-1825-4910-99C5-1409422D46AB_ParameterInputX<\\/bpmn2:targetRef>\\r\\n      <\\/bpmn2:dataInputAssociation>\\r\\n    <\\/bpmn2:serviceTask>\\r\\n    <bpmn2:startEvent id=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\">\\r\\n      <bpmn2:outgoing>_4C4024AC-D6CA-4B58-BCEF-D667DA948201<\\/bpmn2:outgoing>\\r\\n    <\\/bpmn2:startEvent>\\r\\n  <\\/bpmn2:process>\\r\\n  <bpmndi:BPMNDiagram>\\r\\n    <bpmndi:BPMNPlane bpmnElement=\\\"flights\\\">\\r\\n      <bpmndi:BPMNShape id=\\\"shape__578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\" bpmnElement=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\"><dc:Bounds height=\\\"56\\\" width=\\\"56\\\" x=\\\"100\\\" y=\\\"100\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNShape id=\\\"shape__25ADBBE9-1825-4910-99C5-1409422D46AB\\\" bpmnElement=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\"><dc:Bounds height=\\\"102\\\" width=\\\"154\\\" x=\\\"236\\\" y=\\\"77\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNShape id=\\\"shape__3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\" bpmnElement=\\\"_3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\"><dc:Bounds height=\\\"56\\\" width=\\\"56\\\" x=\\\"470\\\" y=\\\"100\\\"\\/><\\/bpmndi:BPMNShape>\\r\\n      <bpmndi:BPMNEdge id=\\\"edge_shape__578F1284-FFB5-4A5A-B628-A27238DD2B5B_to_shape__25ADBBE9-1825-4910-99C5-1409422D46AB\\\" bpmnElement=\\\"_4C4024AC-D6CA-4B58-BCEF-D667DA948201\\\"><di:waypoint x=\\\"156\\\" y=\\\"128\\\"\\/><di:waypoint x=\\\"236\\\" y=\\\"128\\\"\\/><\\/bpmndi:BPMNEdge>\\r\\n      <bpmndi:BPMNEdge id=\\\"edge_shape__25ADBBE9-1825-4910-99C5-1409422D46AB_to_shape__3636D882-5DC6-4BEC-BC37-A00F3ED38FF6\\\" bpmnElement=\\\"_C15A51AF-DECF-454E-8DAA-52C1B4D5E25C\\\"><di:waypoint x=\\\"390\\\" y=\\\"128\\\"\\/><di:waypoint x=\\\"470\\\" y=\\\"128\\\"\\/><\\/bpmndi:BPMNEdge>\\r\\n    <\\/bpmndi:BPMNPlane>\\r\\n  <\\/bpmndi:BPMNDiagram>\\r\\n  <bpmn2:relationship id=\\\"_xjRSsNF2EDeo0dqbkXENZQ\\\" type=\\\"BPSimData\\\">\\r\\n    <bpmn2:extensionElements>\\r\\n      <bpsim:BPSimData>\\r\\n        <bpsim:Scenario id=\\\"default\\\" name=\\\"Simulationscenario\\\"><bpsim:ScenarioParameters\\/>\\r\\n          <bpsim:ElementParameters elementRef=\\\"_578F1284-FFB5-4A5A-B628-A27238DD2B5B\\\">\\r\\n            <bpsim:TimeParameters>\\r\\n              <bpsim:ProcessingTime><bpsim:NormalDistribution mean=\\\"0\\\" standardDeviation=\\\"0\\\"\\/><\\/bpsim:ProcessingTime>\\r\\n            <\\/bpsim:TimeParameters>\\r\\n          <\\/bpsim:ElementParameters>\\r\\n          <bpsim:ElementParameters elementRef=\\\"_25ADBBE9-1825-4910-99C5-1409422D46AB\\\">\\r\\n            <bpsim:TimeParameters>\\r\\n              <bpsim:ProcessingTime><bpsim:NormalDistribution mean=\\\"0\\\" standardDeviation=\\\"0\\\"\\/><\\/bpsim:ProcessingTime>\\r\\n            <\\/bpsim:TimeParameters>\\r\\n            <bpsim:ResourceParameters>\\r\\n              <bpsim:Availability><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:Availability>\\r\\n              <bpsim:Quantity><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:Quantity>\\r\\n            <\\/bpsim:ResourceParameters>\\r\\n            <bpsim:CostParameters>\\r\\n              <bpsim:UnitCost><bpsim:FloatingParameter value=\\\"0\\\"\\/><\\/bpsim:UnitCost>\\r\\n            <\\/bpsim:CostParameters>\\r\\n          <\\/bpsim:ElementParameters>\\r\\n        <\\/bpsim:Scenario>\\r\\n      <\\/bpsim:BPSimData>\\r\\n    <\\/bpmn2:extensionElements>\\r\\n    <bpmn2:source>_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282<\\/bpmn2:source>\\r\\n    <bpmn2:target>_A1133E9B-4DCB-4BD2-A174-1F9AA73ED282<\\/bpmn2:target>\\r\\n  <\\/bpmn2:relationship>\\r\\n<\\/bpmn2:definitions>");

        assertEquals(diagramSet.getName().getValue(), BPMNStandaloneDiagramServiceImpl.DEFAULT_PROCESS_ID);
        assertEquals(diagramSet.getId().getValue(), BPMNStandaloneDiagramServiceImpl.DEFAULT_PROCESS_ID);
    }
}
