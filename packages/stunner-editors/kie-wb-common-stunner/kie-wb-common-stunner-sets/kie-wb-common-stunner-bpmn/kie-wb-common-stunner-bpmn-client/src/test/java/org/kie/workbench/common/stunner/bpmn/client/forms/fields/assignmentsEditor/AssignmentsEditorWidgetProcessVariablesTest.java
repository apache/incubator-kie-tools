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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.util.ArrayList;
import java.util.List;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.session.impl.DefaultViewerSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.Silent.class)
public class AssignmentsEditorWidgetProcessVariablesTest {

    private static final String TASK_NAME = "TASK";
    private static final String SELECTED_ELEMENT_UUID = "SELECTED_ELEMENT_UUID";
    private static final String ASSIGNMENTS_INFO = "|input1:com.test.Employee,input2:String,input3:String,input4:String,Skippable||output1:com.test.Employee,output2:String|[din]employee->input1,[din]input2=ab%7Ccd%7Cef,[din]input3=yes,[din]input4=%22Hello%22+then+%22Goodbye%22,[dout]output1->employee,[dout]output2->reason";
    private static final String PROCESS_VARIABLES = "PV1:java.lang.String,PV2:java.lang.String";
    private static final String PARENT_NODE_PROCESS_VARIABLES = "ESPV1:java.lang.String,ESPV2:java.lang.String";
    List<Node> graphNodes = new ArrayList<>();
    @Mock
    private BPMNDefinition bpmnModel;
    @GwtMock
    private AssignmentsEditorWidget widget;
    @Mock
    private SessionManager canvasSessionManager;
    @Mock
    private EditorSession clientSession;
    @Mock
    private DefaultViewerSession viewerSession;
    @Mock
    private UserTask userTask;
    @Mock
    private TaskGeneralSet taskGeneralSet;
    @Mock
    private Name taskName;
    @Mock
    private AbstractCanvasHandler canvasHandler;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;
    @Mock
    private GraphUtils graphUtils;
    @Mock
    private Graph graph;
    @Mock
    private SelectionControl selectionControl;
    @Mock
    private BPMNDiagramImpl bpmnDiagram;
    @Mock
    private View nodeView;
    @Mock
    private View<Object> otherView;
    @Mock
    private View parentNodeView;
    @Mock
    private ProcessData bpmnDiagramProcessData;
    @Mock
    private ProcessData parentNodeProcessData;
    @Mock
    private ProcessVariables bpmnDiagramProcessVariables;
    @Mock
    private ProcessVariables parentNodeProcessVariables;
    @Mock
    private Node parentNode;
    @Mock
    private Edge edge;
    @Mock
    private Node node;
    @Mock
    private Child child;
    @Mock
    private EventSubprocess eventSubprocess;
    @Mock
    private AdHocSubprocess adhocSubprocess;
    @Mock
    private MultipleInstanceSubprocess multipleInstanceSubprocess;
    @Mock
    private EmbeddedSubprocess embeddedSubprocess;
    @Mock
    private View<DataObject> dataObjectView;
    @Mock
    private Node<View<DataObject>, ?> dataObjectNode;
    @Mock
    private Node dataObjectparentNode;

    @Before
    public void setup() {
        GwtMockito.initMocks(this);
        when(userTask.getGeneral()).thenReturn(taskGeneralSet);
        when(taskGeneralSet.getName()).thenReturn(taskName);
        when(taskName.getValue()).thenReturn(TASK_NAME);
        when(canvasSessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getCanvasRootUUID()).thenReturn("rootId");
        when(diagram.getGraph()).thenReturn(graph);
        List<Edge> edges = new ArrayList<>();
        List<String> selectedNodes = new ArrayList<>();
        selectedNodes.add(SELECTED_ELEMENT_UUID);
        edges.add(edge);
        when(graph.nodes()).thenReturn(graphNodes);
        when(graph.getNode(SELECTED_ELEMENT_UUID)).thenReturn(node);
        when(edge.getContent()).thenReturn(child);
        when(node.getInEdges()).thenReturn(edges);
        when(edge.getSourceNode()).thenReturn(parentNode);
        when(canvasSessionManager.getCurrentSession()).thenReturn(clientSession);
        when(clientSession.getSelectionControl()).thenReturn(selectionControl);
        when(selectionControl.getSelectedItems()).thenReturn(selectedNodes);
        when(widget.getSelectedElementUUID(clientSession)).thenReturn(SELECTED_ELEMENT_UUID);
        when(widget.getSelectedElement()).thenReturn(node);
        when(node.getUUID()).thenReturn("nodeId");
        when(node.getContent()).thenReturn(nodeView);
        when(parentNode.getContent()).thenReturn(parentNodeView);
        when(nodeView.getDefinition()).thenReturn(bpmnDiagram);
        when(bpmnDiagram.getProcessData()).thenReturn(bpmnDiagramProcessData);
        when(bpmnDiagramProcessData.getProcessVariables()).thenReturn(bpmnDiagramProcessVariables);
        when(bpmnDiagramProcessVariables.getValue()).thenReturn(PROCESS_VARIABLES);
        when(parentNodeProcessData.getProcessVariables()).thenReturn(parentNodeProcessVariables);
        when(parentNodeProcessVariables.getValue()).thenReturn(PARENT_NODE_PROCESS_VARIABLES);
        when(graphUtils.getParent(node)).thenReturn(parentNode);
        when(parentNode.getUUID()).thenReturn("rootId");
        when(widget.isBPMNDefinition(any())).thenCallRealMethod();
    }

    @Test
    public void testGetProcessVariableEventSubprocess() {
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableEventSubprocessViewerSessionUnselected() {
        when(viewerSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasSessionManager.getCurrentSession()).thenReturn(viewerSession);
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableEventSubprocessSelectionNull() {
        when(selectionControl.getSelectedItems()).thenReturn(null);
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableEventSubprocessSelectionEmpty() {
        when(selectionControl.getSelectedItems()).thenReturn(new ArrayList<>());
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableEventSubprocessSelectionControlNull() {
        when(clientSession.getSelectionControl()).thenReturn(null);

        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();

        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testViewAndContent() {
        Node<View<DataObject>, ?> dataObjectNode;

        DataObject dataObject = new DataObject();
        dataObject.getGeneral().getDocumentation().setValue("doc");
        dataObject.setName(new Name("name"));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue("name")));

        dataObjectNode = new NodeImpl<>(UUID.uuid());
        dataObjectNode.setContent(dataObjectView);
        when(dataObjectView.getDefinition()).thenReturn(dataObject);

        boolean isBPMNDefinition = widget.isBPMNDefinition(dataObjectNode);
        assertTrue(" Must be a BPMN Definition", isBPMNDefinition);

        Node<View<Object>, ?> objectNode = new NodeImpl<>(UUID.uuid());
        objectNode.setContent(otherView);
        when(otherView.getDefinition()).thenReturn(new Object());

        isBPMNDefinition = widget.isBPMNDefinition(objectNode);
        assertFalse(" Must not be a BPMN Definition", isBPMNDefinition);

        Node<Element, ?> objectNode2 = new NodeImpl<>(UUID.uuid());
        Element someElement = mock(Element.class);
        objectNode2.setContent(someElement);
        when(otherView.getDefinition()).thenReturn(new Object());

        isBPMNDefinition = widget.isBPMNDefinition(objectNode2);
        assertFalse(" Must not be a BPMN Definition", isBPMNDefinition);
    }

    @Test
    public void testGetProcessVariableAdHocSubprocess() {
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(adhocSubprocess);
        when(adhocSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableEmbeddedSubprocess() {
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(embeddedSubprocess);
        when(embeddedSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableMultipleInstanceSubprocess() {
        testAddGraphNodes();
        when(parentNodeView.getDefinition()).thenReturn(multipleInstanceSubprocess);
        when(multipleInstanceSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PROCESS_VARIABLES + "," + PARENT_NODE_PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableParentNodeBeforeNode() {
        testAddGraphNodesParentBeforeNode();
        when(parentNodeView.getDefinition()).thenReturn(eventSubprocess);
        when(eventSubprocess.getProcessData()).thenReturn(parentNodeProcessData);
        createWidget();
        assertEquals(PARENT_NODE_PROCESS_VARIABLES + "," + PROCESS_VARIABLES, widget.getProcessVariables());
    }

    @Test
    public void testGetProcessVariableFromDataObjects() {

        DataObject dataObject = new DataObject();
        dataObject.getGeneral().getDocumentation().setValue("doc");
        dataObject.setName(new Name("name"));
        dataObject.setType(new DataObjectType(new DataObjectTypeValue("com.myType.dataObject")));

        final String uuid = UUID.uuid();
        when(dataObjectNode.getUUID()).thenReturn("uuid");
        when(dataObjectNode.getContent()).thenReturn(dataObjectView);
        when(dataObjectView.getDefinition()).thenReturn(dataObject);

        graphNodes.clear();
        graphNodes.add(dataObjectNode);
        graphNodes.add(parentNode);

        when(parentNodeView.getDefinition()).thenReturn(dataObjectNode);
        when(dataObjectNode.getUUID()).thenReturn("rootId");

        createWidget();
        assertEquals(dataObject.getName().getValue() + ":" + dataObject.getType().getValue().getType(), widget.getProcessVariables());
    }

    private void testAddGraphNodes() {
        graphNodes.clear();
        graphNodes.add(node);
        graphNodes.add(parentNode);
    }

    private void testAddGraphNodesParentBeforeNode() {
        graphNodes.clear();
        graphNodes.add(parentNode);
        graphNodes.add(node);
    }

    private void createWidget() {
        widget = new AssignmentsEditorWidget(bpmnModel,
                                             ASSIGNMENTS_INFO,
                                             true,
                                             false,
                                             true,
                                             false,
                                             canvasSessionManager,
                                             graphUtils);
    }
}