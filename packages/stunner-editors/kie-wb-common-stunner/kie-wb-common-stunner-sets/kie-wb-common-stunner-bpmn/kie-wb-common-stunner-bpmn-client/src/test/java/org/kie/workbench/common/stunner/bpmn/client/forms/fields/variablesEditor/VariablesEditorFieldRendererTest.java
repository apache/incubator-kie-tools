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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.dynamic.client.rendering.formGroups.impl.def.DefaultFormGroup;
import org.kie.workbench.common.forms.dynamic.service.shared.RenderMode;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.UserTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Id;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsMultipleInstance;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceCollectionOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataInput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.MultipleInstanceDataOutput;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.mockito.Mock;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class VariablesEditorFieldRendererTest {

    private static final String PROCESS_ID = "process_ID";

    @Mock
    private VariablesEditorWidgetView variablesEditorWidgetView;

    @Mock
    private VariableListItemWidgetView variableListItemWidgetView;

    @Mock
    private SessionManager abstractClientSessionManager;

    @Mock
    private Graph graph;

    @Mock
    private VariableRow variableRow;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private EditorSession clientFullSession;

    @Mock
    private Iterable nodes;

    @Mock
    private ManagedInstance<DefaultFormGroup> formGroupsInstanceMock;

    @Mock
    private DefaultFormGroup formGroup;

    private VariablesEditorFieldRenderer variablesEditor;

    @Before
    public void setup() {
        when(formGroupsInstanceMock.get()).thenReturn(formGroup);
        variablesEditor = new VariablesEditorFieldRenderer(variablesEditorWidgetView,
                                                           abstractClientSessionManager) {
            {
                formGroupsInstance = formGroupsInstanceMock;
            }
        };
    }

    @Test
    public void testAddVariable() {
        when(variablesEditorWidgetView.getVariableWidget(anyInt())).thenReturn(variableListItemWidgetView);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(1);
        variablesEditor.addVariable();
        verify(variablesEditorWidgetView,
               times(1)).setTableDisplayStyle();
        verify(variablesEditorWidgetView,
               times(1)).getVariableRowsCount();
        verify(variablesEditorWidgetView,
               times(1)).getVariableWidget(0);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(2);
        variablesEditor.addVariable();
        verify(variablesEditorWidgetView,
               times(2)).getVariableRowsCount();
        verify(variablesEditorWidgetView,
               times(1)).getVariableWidget(1);
    }

    @Test
    public void testRemoveVariableWhenBoundToNodes() {
        prepareRemoveVariableTest(true);
        variablesEditor.removeVariable(variableRow);
        verify(variablesEditorWidgetView).getVariableRows();
        verify(variablesEditorWidgetView, never()).doSave();
    }

    @Test
    public void testRemoveVariableWhenNotBoundToNodes() {
        prepareRemoveVariableTest(false);
        variablesEditor.removeVariable(variableRow);
        verify(variablesEditorWidgetView, times(2)).getVariableRows();
        verify(variablesEditorWidgetView).doSave();
    }

    private void prepareRemoveVariableTest(boolean makeVariableBounded) {
        when(variablesEditorWidgetView.getVariableWidget(anyInt())).thenReturn(variableListItemWidgetView);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(1);
        when(variableRow.getName()).thenReturn("variableName");
        when(abstractClientSessionManager.getCurrentSession()).thenReturn(clientFullSession);
        when(clientFullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);

        final List nodes = new ArrayList<>();
        if (makeVariableBounded) {
            final Node node = mockArbitraryNodeWithVariableBound("variableName");
            nodes.add(node);
        }
        when(graph.nodes()).thenReturn(nodes);
        variablesEditor.getFormGroup(RenderMode.EDIT_MODE);
        variablesEditor.addVariable();
    }

    private Node mockArbitraryNodeWithVariableBound(String variableName) {
        String assignmentsInfoValue = "|input1:String|||[din]" + variableName + "->input1";
        View view = mock(View.class);
        Node node = mock(Node.class);
        when(node.getContent()).thenReturn(view);
        UserTask content = mock(UserTask.class);
        TaskGeneralSet generalSet = mock(TaskGeneralSet.class);
        Name nameProperty = mock(Name.class);
        when(generalSet.getName()).thenReturn(nameProperty);
        when(content.getGeneral()).thenReturn(generalSet);
        UserTaskExecutionSet executionSet = mock(UserTaskExecutionSet.class);
        when(content.getExecutionSet()).thenReturn(executionSet);
        when(executionSet.getIsMultipleInstance()).thenReturn(mock(IsMultipleInstance.class));
        when(executionSet.getMultipleInstanceCollectionInput()).thenReturn(mock(MultipleInstanceCollectionInput.class));
        when(executionSet.getMultipleInstanceCollectionOutput()).thenReturn(mock(MultipleInstanceCollectionOutput.class));
        when(executionSet.getMultipleInstanceDataInput()).thenReturn(mock(MultipleInstanceDataInput.class));
        when(executionSet.getMultipleInstanceDataOutput()).thenReturn(mock(MultipleInstanceDataOutput.class));
        AssignmentsInfo assignmentsInfo = mock(AssignmentsInfo.class);
        when(assignmentsInfo.getValue()).thenReturn(assignmentsInfoValue);
        when(executionSet.getAssignmentsinfo()).thenReturn(assignmentsInfo);
        when(view.getDefinition()).thenReturn(content);
        return node;
    }

    @Test
    public void testDeserializeVariables() {
        List<String> dataTypes = new ArrayList<String>(Arrays.asList("Boolean",
                                                                     "Float",
                                                                     "Integer",
                                                                     "Object",
                                                                     "org.veg.Potato",
                                                                     "String"));
        List<String> dataTypeDisplayNames = new ArrayList<String>(Arrays.asList("Boolean",
                                                                                "Float",
                                                                                "Integer",
                                                                                "Potato [org.veg]",
                                                                                "Object",
                                                                                "String"));

        variablesEditor.setDataTypes(dataTypes,
                                     dataTypeDisplayNames);
        List<VariableRow> variableRows = variablesEditor.deserializeVariables("var1:String:[internal;input],var2:Integer:[output],var3:org.stuff.Potato:,var4:com.myCustomDataType:[]");
        assertEquals(4,
                     variableRows.size());
        VariableRow var = variableRows.get(0);
        assertEquals("var1",
                     var.getName());
        assertEquals("String",
                     var.getDataTypeDisplayName());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());
        assertEquals(2, var.getTags().size());
        assertEquals("internal", var.getTags().get(0));
        assertEquals("input", var.getTags().get(1));

        var = variableRows.get(1);
        assertEquals("var2",
                     var.getName());
        assertEquals("Integer",
                     var.getDataTypeDisplayName());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());

        assertEquals(1, var.getTags().size());
        assertEquals("output", var.getTags().get(0));

        var = variableRows.get(2);
        assertEquals("var3",
                     var.getName());
        assertEquals("org.stuff.Potato",
                     var.getCustomDataType());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());

        assertEquals(0, var.getTags().size());

        var = variableRows.get(3);
        assertEquals("var4",
                     var.getName());
        assertEquals("com.myCustomDataType",
                     var.getCustomDataType());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());

        assertEquals(0, var.getTags().size()); // Meaning Empty List
    }

    @Test
    public void testSerializeVariables() {
        Map<String, String> mapDataTypeDisplayNamesToNames = new HashMap<String, String>();
        mapDataTypeDisplayNamesToNames.put("String",
                                           "String");
        mapDataTypeDisplayNamesToNames.put("Integer",
                                           "Integer");
        mapDataTypeDisplayNamesToNames.put("Potato [org.veg]",
                                           "org.veg.Potato");
        variablesEditor.mapDataTypeDisplayNamesToNames = mapDataTypeDisplayNamesToNames;

        List<String> tags = new ArrayList<>(Arrays.asList("Tag_1", "Tag_2"));

        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var1",
                                         "String",
                                         null,
                                         tags));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null,
                                         tags));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var3",
                                         "org.veg.Potato",
                                         null,
                                         tags));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var4",
                                         null,
                                         null,
                                         tags));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var5",
                                         "Boolean",
                                         null));
        String s = variablesEditor.serializeVariables(variableRows);
        assertEquals("var1:String:Tag_1;Tag_2,var2:Integer:Tag_1;Tag_2,var3:org.veg.Potato:Tag_1;Tag_2,var4::Tag_1;Tag_2,var5:Boolean:", s);
    }

    @Test
    public void testSerializeVariablesWithTags() {
        Map<String, String> mapDataTypeDisplayNamesToNames = new HashMap<String, String>();
        mapDataTypeDisplayNamesToNames.put("String",
                                           "String");
        mapDataTypeDisplayNamesToNames.put("Integer",
                                           "Integer");
        mapDataTypeDisplayNamesToNames.put("Potato [org.veg]",
                                           "org.veg.Potato");
        variablesEditor.mapDataTypeDisplayNamesToNames = mapDataTypeDisplayNamesToNames;

        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var1",
                                         "String",
                                         null,
                                         Arrays.asList("internal")));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null,
                                         Arrays.asList("input"
                                         )));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var3",
                                         "org.veg.Potato",
                                         null,
                                         Arrays.asList("input", "output")));

        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var4",
                                         "myCustomTag",
                                         null,
                                         null));

        variableRows.add(new VariableRow(Variable.VariableType.INPUT,
                                         "var5",
                                         "myCustomTag2",
                                         "myType",
                                         Arrays.asList("thisTagShouldNotBeWritten")));

        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var6",
                                         null,
                                         null,
                                         Arrays.asList("thisTagShouldBeWritten")));

        String s = variablesEditor.serializeVariables(variableRows);
        assertEquals("var1:String:internal,var2:Integer:input,var3:org.veg.Potato:input;output,var4:myCustomTag:,var5:myType:,var6::thisTagShouldBeWritten",
                     s);
    }

    @Test
    public void testIsDuplicateName() {
        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var1",
                                         "String",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var3",
                                         "org.stuff.Potato",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         null,
                                         "Object",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         null,
                                         null,
                                         null));
        when(variablesEditorWidgetView.getVariableRows()).thenReturn(variableRows);
        assertTrue(variablesEditor.isDuplicateName("var2"));
        assertFalse(variablesEditor.isDuplicateName("var1"));
    }

    @Test
    public void testIdDuplicateID() {
        Id id = new Id(PROCESS_ID);
        BPMNDiagramImpl bpmnDiagram = new BPMNDiagramImpl();
        bpmnDiagram.getDiagramSet().setId(id);

        Node node = mock(Node.class);
        View view = mock(View.class);

        List<Node> nodes = new ArrayList<>();
        nodes.add(node);

        when(node.getContent()).thenReturn(view);
        when(view.getDefinition()).thenReturn(bpmnDiagram);
        when(abstractClientSessionManager.getCurrentSession()).thenReturn(clientFullSession);
        when(clientFullSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(nodes);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);

        variablesEditor.getFormGroup(RenderMode.READ_ONLY_MODE);

        assertTrue(variablesEditor.isDuplicateID(PROCESS_ID));
        assertFalse(variablesEditor.isDuplicateID("NOT_PROCESS_ID"));
    }

    @Test
    public void testSetReadOnlyTrue() {
        variablesEditor.setReadOnly(true);
        verify(variablesEditorWidgetView,
               times(1)).setReadOnly(true);
    }

    @Test
    public void testSetReadOnlyFalse() {
        variablesEditor.setReadOnly(false);
        verify(variablesEditorWidgetView,
               times(1)).setReadOnly(false);
    }

    @Test
    public void testCreateVariableRow() {
        VariableRow variableRow = VariablesEditorFieldRenderer.createVariableRow();
        assertEquals(Variable.VariableType.PROCESS, variableRow.getVariableType());
        assertEquals("Object", variableRow.getDataTypeDisplayName());
    }
}