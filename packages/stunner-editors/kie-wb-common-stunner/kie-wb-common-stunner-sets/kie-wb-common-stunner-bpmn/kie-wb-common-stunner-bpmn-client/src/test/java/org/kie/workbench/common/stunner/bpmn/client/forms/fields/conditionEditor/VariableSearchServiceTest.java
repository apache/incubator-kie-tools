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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.PromiseMock;
import org.kie.workbench.common.stunner.bpmn.definition.AdHocSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagramImpl;
import org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.MultipleInstanceSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseFileVariables;
import org.kie.workbench.common.stunner.bpmn.definition.property.cm.CaseManagementSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessVariables;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.FieldMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadata;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQuery;
import org.kie.workbench.common.stunner.bpmn.forms.conditions.TypeMetadataQueryResult;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.commons.Pair;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchCallback;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchResults;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.FunctionSearchServiceTest.verifyContains;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.FunctionSearchServiceTest.verifyNotContains;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.conditionEditor.VariableSearchService.unboxDefaultType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class VariableSearchServiceTest {

    protected static final String CANVAS_ROOT_ID = "CANVAS_ROOT_ID";
    protected static final String SELECTED_ITEM = "SELECTED_ITEM";
    protected static final String SOURCE_NODE = "SOURCE_NODE";
    protected static final String PARENT_NODE1 = "PARENT_NODE1";
    protected static final String PARENT_NODE2 = "PARENT_NODE2";
    protected static final String PARENT_NODE3 = "PARENT_NODE3";
    protected static final String PARENT_NODE4 = "PARENT_NODE4";

    protected static final String MULTIPLE_INSTANCE_SUBPROCESS = "MultipleInstanceSubprocess";
    protected static final String EMBEDDED_SUBPROCESS = "EmbeddedSubprocess";
    protected static final String ADHOC_SUBPROCESS = "AdHocSubprocess";
    protected static final String EVENT_SUBPROCESS = "EventSubprocess";
    protected static final String MAIN_PROCESS = "MainProcess";
    protected static final String CASE_FILE = "CaseFile";

    private static final String CASE_VARIABLE_LABEL_PREFIX = "CASE_VARIABLE_LABEL_PREFIX";

    @Mock
    protected ConditionEditorMetadataService metadataService;

    @Mock
    protected ClientTranslationService translationService;

    private VariableSearchService searchService;

    @Mock
    private EditorSession clientSession;

    @Mock
    private SelectionControl selectionControl;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Metadata metadata;

    @Mock
    private Path path;

    @Mock
    private LiveSearchCallback<String> searchCallback;

    @Captor
    private ArgumentCaptor<LiveSearchResults<String>> searchResultsCaptor;

    private List<Pair<String, String>> mockedVariableNames = new ArrayList<>();

    private Map<String, String> mockedVariableTypes = new HashMap<>();

    @Before
    public void setUp() {
        when(clientSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
        when(metadata.getCanvasRootUUID()).thenReturn(CANVAS_ROOT_ID);
        when(translationService.getValue("VariableSearchService.CaseVariableLabelPrefix")).thenReturn(CASE_VARIABLE_LABEL_PREFIX);
        searchService = newSearchService();
    }

    protected VariableSearchService newSearchService() {
        return new VariableSearchService(metadataService, translationService);
    }

    @Test
    public void testInitSession() {
        prepareAndInitSession();
        mockedVariableNames.forEach(entry -> verifyVariable(buildExpectedVariableName(entry.getK1(), entry.getK2()), buildExpectedVariableLabel(entry.getK1(), entry.getK2())));
    }

    @Test
    public void testSearchWithResults() {
        prepareAndInitSession();
        searchService.search(MULTIPLE_INSTANCE_SUBPROCESS, 20, searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        LiveSearchResults<String> results = searchResultsCaptor.getValue();
        List<Pair<String, String>> expectedVariables1 = buildExpectedVariableNames(MULTIPLE_INSTANCE_SUBPROCESS, 17);

        verifyResultsContains(results, expectedVariables1);

        searchService.search("MultipleIns", 20, searchCallback);
        verify(searchCallback, times(2)).afterSearch(searchResultsCaptor.capture());
        results = searchResultsCaptor.getValue();
        verifyResultsContains(results, expectedVariables1);

        searchService.search(ADHOC_SUBPROCESS, 20, searchCallback);
        verify(searchCallback, times(3)).afterSearch(searchResultsCaptor.capture());
        results = searchResultsCaptor.getValue();
        List<Pair<String, String>> expectedVariables2 = buildExpectedVariableNames(ADHOC_SUBPROCESS, 17);
        verifyResultsContains(results, expectedVariables2);
        verifyResultsNotContains(results, expectedVariables1);
    }

    @Test
    public void testSearchWithoutResults() {
        prepareAndInitSession();
        searchService.search("non-existing", 20, searchCallback);
        verify(searchCallback).afterSearch(searchResultsCaptor.capture());
        LiveSearchResults<String> results = searchResultsCaptor.getValue();
        assertEquals(0, results.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchEntryWithResults() {
        prepareAndInitSession();
        List<Pair<String, String>> expectedVariables = buildExpectedVariableNames(MULTIPLE_INSTANCE_SUBPROCESS, 17);
        for (int i = 0; i < expectedVariables.size(); i++) {
            searchService.searchEntry(expectedVariables.get(i).getK1(), searchCallback);
            verify(searchCallback, times(i + 1)).afterSearch(searchResultsCaptor.capture());
            verifyContains(searchResultsCaptor.getValue(), expectedVariables.get(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSearchEntryWithoutResults() {
        prepareAndInitSession();
        List<String> checkedVariables = Arrays.asList("not-existing1", "not-existing2", "not-existing3", "and_so_on");
        for (int i = 0; i < checkedVariables.size(); i++) {
            searchService.searchEntry(checkedVariables.get(i), searchCallback);
            verify(searchCallback, times(i + 1)).afterSearch(searchResultsCaptor.capture());
            verifyNotContains(searchResultsCaptor.getValue(), new Pair<>(checkedVariables.get(i), checkedVariables.get(i)));
        }
    }

    @Test
    public void testGetOptionTypeWithResults() {
        prepareAndInitSession();
        List<Pair<String, String>> checkedVariables = buildExpectedVariableNames(MULTIPLE_INSTANCE_SUBPROCESS, 17);
        checkedVariables.forEach(variable -> assertEquals("Option type wasn't properly calculated for variable: " + variable,
                                                          mockedVariableTypes.get(variable.getK1()), searchService.getOptionType(variable.getK1())));
    }

    @Test
    public void testGetOptionTypeWithoutResults() {
        prepareAndInitSession();
        List<String> checkedVariables = Arrays.asList("not-existing1", "not-existing2", "not-existing3", "and_so_on");
        checkedVariables.forEach(checkedVariable -> {
            String type = searchService.getOptionType(checkedVariable);
            assertNotEquals(checkedVariable, type);
        });
    }

    @Test
    public void testUnboxDefaultTypes() {
        assertEquals(Short.class.getName(), unboxDefaultType("short"));
        assertEquals(Short.class.getName(), unboxDefaultType("Short"));
        assertEquals(Integer.class.getName(), unboxDefaultType("int"));
        assertEquals(Integer.class.getName(), unboxDefaultType("Integer"));
        assertEquals(Long.class.getName(), unboxDefaultType("long"));
        assertEquals(Long.class.getName(), unboxDefaultType("Long"));
        assertEquals(Float.class.getName(), unboxDefaultType("float"));
        assertEquals(Float.class.getName(), unboxDefaultType("Float"));
        assertEquals(Double.class.getName(), unboxDefaultType("double"));
        assertEquals(Double.class.getName(), unboxDefaultType("Double"));
        assertEquals(Character.class.getName(), unboxDefaultType("char"));
        assertEquals(Character.class.getName(), unboxDefaultType("Character"));
        assertEquals(String.class.getName(), unboxDefaultType("String"));
        assertEquals(Object.class.getName(), unboxDefaultType("Object"));
        assertEquals("Other_value", unboxDefaultType("Other_value"));
        assertEquals(null, unboxDefaultType(null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testClear() {
        prepareAndInitSession();
        List<Pair<String, String>> expectedVariables = buildExpectedVariableNames(MULTIPLE_INSTANCE_SUBPROCESS, 17);
        for (int i = 0; i < expectedVariables.size(); i++) {
            searchService.searchEntry(expectedVariables.get(i).getK1(), searchCallback);
            verify(searchCallback, times(i + 1)).afterSearch(searchResultsCaptor.capture());
            verifyContains(searchResultsCaptor.getValue(), expectedVariables.get(i));
            assertEquals(mockedVariableTypes.get(expectedVariables.get(i).getK1()), searchService.getOptionType(expectedVariables.get(i).getK1()));
        }
        searchService.clear();
        int testedSize = expectedVariables.size();
        for (int i = 0; i < expectedVariables.size(); i++) {
            searchService.searchEntry(expectedVariables.get(i).getK1(), searchCallback);
            verify(searchCallback, times(i + 1 + testedSize)).afterSearch(searchResultsCaptor.capture());
            assertEquals(0, searchResultsCaptor.getValue().size());
            assertNull(searchService.getOptionType(expectedVariables.get(i).getK1()));
        }
    }

    @SuppressWarnings("unchecked")
    protected void prepareAndInitSession() {
        prepareSelectedItem();
        List<Node> nodes = mockNodes();
        when(graph.nodes()).thenReturn(nodes);
        Set<TypeMetadata> typeMetadatas = new HashSet<>();
        typeMetadatas.add(mockBean1Metadata());
        TypeMetadataQueryResult queryResult = new TypeMetadataQueryResult(typeMetadatas, new HashSet<>());
        doReturn(PromiseMock.success(queryResult)).when(metadataService).call(any(TypeMetadataQuery.class));
        searchService.init(clientSession);
    }

    protected List<Node> mockNodes() {
        Node sourceNode = mockSourceNode(SOURCE_NODE, SELECTED_ITEM);

        Node parentNode1 = mockNode(PARENT_NODE1, mockMultipleInstanceSubprocess(mockVariables(MULTIPLE_INSTANCE_SUBPROCESS)));
        setParentNode(sourceNode, parentNode1);

        Node parentNode2 = mockNode(PARENT_NODE2, mockEmbeddedSubprocess(mockVariables(EMBEDDED_SUBPROCESS)));
        setParentNode(parentNode1, parentNode2);

        Node parentNode3 = mockNode(PARENT_NODE3, mockAdHocSubProcess(mockVariables(ADHOC_SUBPROCESS)));
        setParentNode(parentNode2, parentNode3);

        Node parentNode4 = mockNode(PARENT_NODE4, mockEventSubProcess(mockVariables(EVENT_SUBPROCESS)));
        setParentNode(parentNode3, parentNode4);

        Node canvasRoot = mockNode(CANVAS_ROOT_ID, mockBPMNDiagram(mockVariables(MAIN_PROCESS), mockVariables(CASE_FILE)));
        setParentNode(parentNode4, canvasRoot);

        ArrayList<Node> nodes = new ArrayList<>();
        nodes.add(sourceNode);
        nodes.add(parentNode1);
        nodes.add(parentNode2);
        nodes.add(parentNode3);
        nodes.add(parentNode4);
        nodes.add(canvasRoot);
        return nodes;
    }

    private void verifyVariable(String variableKey, String variableValue) {
        int i = 0;
        searchService.searchEntry(variableKey, results -> assertTrue("It seems like variable <" + variableKey + ", " + variableValue + "> is not present in results",
                                                                     results.stream()
                                                                             .anyMatch(entry -> variableKey.equals(entry.getKey()) && variableValue.equals(entry.getValue()))));
    }

    private void verifyResultsContains(LiveSearchResults<String> results, List<Pair<String, String>> expectedVariables) {
        verifyContains(results, expectedVariables.stream().map(expectedVariable -> new Pair<>(expectedVariable.getK1(), expectedVariable.getK2())).collect(Collectors.toList()));
    }

    private void verifyResultsNotContains(LiveSearchResults<String> results, List<Pair<String, String>> expectedVariables) {
        verifyNotContains(results, expectedVariables.stream().map(expectedVariable -> new Pair<>(expectedVariable.getK1(), expectedVariable.getK2())).collect(Collectors.toList()));
    }

    private void prepareSelectedItem() {
        when(diagram.getGraph()).thenReturn(graph);
        List<String> selectedItems = new ArrayList<>();
        selectedItems.add(SELECTED_ITEM);
        when(selectionControl.getSelectedItems()).thenReturn(selectedItems);
        when(clientSession.getSelectionControl()).thenReturn(selectionControl);
    }

    @SuppressWarnings("unchecked")
    protected Node mockSourceNode(String UUID, String edgeId) {
        Node sourceNode = mockNode(UUID, null);
        EdgeImpl edge = mock(EdgeImpl.class);
        when(edge.getUUID()).thenReturn(edgeId);
        List<Edge> outEdges = new ArrayList<>();
        outEdges.add(edge);
        when(sourceNode.getOutEdges()).thenReturn(outEdges);
        return sourceNode;
    }

    protected void setParentNode(Node childNode, Node parentNode) {
        List<Edge> inEdges = new ArrayList<>();
        Edge edge = mock(Edge.class);
        String edgeUUID = "from_" + parentNode.getUUID() + "_to_" + childNode;
        when(edge.getUUID()).thenReturn(edgeUUID);
        Child childContent = mock(Child.class);
        when(edge.getContent()).thenReturn(childContent);
        inEdges.add(edge);
        when(childNode.getInEdges()).thenReturn(inEdges);
        when(edge.getSourceNode()).thenReturn(parentNode);
    }

    protected Node mockNode(String UUID, Object definition) {
        Node node = mock(Node.class);
        when(node.getUUID()).thenReturn(UUID);
        when(node.asNode()).thenReturn(node);
        View view = mock(View.class);
        when(view.getDefinition()).thenReturn(definition);
        when(node.getContent()).thenReturn(view);
        return node;
    }

    protected EventSubprocess mockEventSubProcess(String variables) {
        EventSubprocess eventSubprocess = mock(EventSubprocess.class);
        ProcessData processData = mockProcessData(variables);
        when(eventSubprocess.getProcessData()).thenReturn(processData);
        return eventSubprocess;
    }

    protected AdHocSubprocess mockAdHocSubProcess(String variables) {
        AdHocSubprocess adHocSubprocess = mock(AdHocSubprocess.class);
        ProcessData processData = mockProcessData(variables);
        when(adHocSubprocess.getProcessData()).thenReturn(processData);
        return adHocSubprocess;
    }

    protected EmbeddedSubprocess mockEmbeddedSubprocess(String variables) {
        EmbeddedSubprocess embeddedSubprocess = mock(EmbeddedSubprocess.class);
        ProcessData processData = mockProcessData(variables);
        when(embeddedSubprocess.getProcessData()).thenReturn(processData);
        return embeddedSubprocess;
    }

    protected MultipleInstanceSubprocess mockMultipleInstanceSubprocess(String variables) {
        MultipleInstanceSubprocess multipleInstanceSubprocess = mock(MultipleInstanceSubprocess.class);
        ProcessData processData = mockProcessData(variables);
        when(multipleInstanceSubprocess.getProcessData()).thenReturn(processData);
        return multipleInstanceSubprocess;
    }

    protected BPMNDiagramImpl mockBPMNDiagram(String variables, String caseVariables) {
        BPMNDiagramImpl bpmnDiagram = mock(BPMNDiagramImpl.class);
        ProcessData processData = mockProcessData(variables);
        when(bpmnDiagram.getProcessData()).thenReturn(processData);
        CaseManagementSet caseManagementSet = mock(CaseManagementSet.class);
        CaseFileVariables caseFileVariables = mock(CaseFileVariables.class);
        when(caseManagementSet.getCaseFileVariables()).thenReturn(caseFileVariables);
        when(caseFileVariables.getValue()).thenReturn(caseVariables);
        when(bpmnDiagram.getCaseManagementSet()).thenReturn(caseManagementSet);
        return bpmnDiagram;
    }

    protected ProcessData mockProcessData(String variables) {
        ProcessData processData = mock(ProcessData.class);
        ProcessVariables processVariables = mock(ProcessVariables.class);
        when(processData.getProcessVariables()).thenReturn(processVariables);
        when(processVariables.getValue()).thenReturn(variables);
        return processData;
    }

    protected String mockVariables(String prefix) {
        StringBuilder variables = new StringBuilder();
        int index = 0;
        variables.append(mockVariable(prefix, index++, Short.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "short"));
        variables.append("," + mockVariable(prefix, index++, Integer.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "int"));
        variables.append("," + mockVariable(prefix, index++, Long.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "long"));
        variables.append("," + mockVariable(prefix, index++, Float.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "float"));
        variables.append("," + mockVariable(prefix, index++, Double.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "double"));
        variables.append("," + mockVariable(prefix, index++, Boolean.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "boolean"));
        variables.append("," + mockVariable(prefix, index++, Character.class.getName()));
        variables.append("," + mockVariable(prefix, index++, "char"));
        variables.append("," + mockVariable(prefix, index++, String.class.getName()));
        variables.append("," + mockVariable(prefix, index++, Object.class.getName()));
        variables.append("," + mockVariable(prefix, index++, Bean1.class.getName()));
        return variables.toString();
    }

    private String mockVariable(String prefix, int index, String type) {
        String variableName = mockVariableName(prefix, index);
        mockedVariableTypes.put(variableName, unboxDefaultType(type));
        return variableName + ":" + type;
    }

    private String mockVariableName(String prefix, int index) {
        String variable = prefix + "Variable" + index;
        mockedVariableNames.add(new Pair<>(prefix, variable));
        return variable;
    }

    private String buildExpectedVariableName(String prefix, String name) {
        if (prefix.equals(CASE_FILE)) {
            return "caseFile_" + name;
        } else {
            return name;
        }
    }

    private String buildExpectedVariableLabel(String prefix, String variable) {
        if (prefix.equals(CASE_FILE)) {
            return CASE_VARIABLE_LABEL_PREFIX + " " + variable;
        } else {
            return variable;
        }
    }

    private List<Pair<String, String>> buildExpectedVariableNames(String prefix, int count) {
        List<Pair<String, String>> result = new ArrayList<>();
        String varName;
        for (int i = 0; i < count; i++) {
            varName = mockVariableName(prefix, i);
            result.add(new Pair<>(buildExpectedVariableName(prefix, varName), buildExpectedVariableLabel(prefix, varName)));
        }
        Pair<String, String> bean1Variable = result.get(count - 1);
        Pair<String, String> name = new Pair<>(buildExpectedVariableName(prefix, bean1Variable.getK1() + ".getName()"), buildExpectedVariableLabel(prefix, bean1Variable.getK2() + ".name"));
        Pair<String, String> surname = new Pair<>(buildExpectedVariableName(prefix, bean1Variable.getK1() + ".getSurname()"), buildExpectedVariableLabel(prefix, bean1Variable.getK2() + ".surname"));
        Pair<String, String> age = new Pair<>(buildExpectedVariableName(prefix, bean1Variable.getK1() + ".getAge()"), buildExpectedVariableLabel(prefix, bean1Variable.getK2() + ".age"));
        result.add(name);
        result.add(surname);
        result.add(age);
        mockedVariableNames.add(name);
        mockedVariableTypes.put(name.getK1(), String.class.getName());
        mockedVariableNames.add(surname);
        mockedVariableTypes.put(surname.getK1(), String.class.getName());
        mockedVariableNames.add(age);
        mockedVariableTypes.put(age.getK1(), Integer.class.getName());
        return result;
    }

    private TypeMetadata mockBean1Metadata() {
        List<FieldMetadata> fieldMetadatas = new ArrayList<>();
        fieldMetadatas.add(new FieldMetadata("name", String.class.getName(), "getName", "setName"));
        fieldMetadatas.add(new FieldMetadata("surname", String.class.getName(), "getSurname", "setSurname"));
        fieldMetadatas.add(new FieldMetadata("age", "int", "getAge", "setAge"));
        return new TypeMetadata(Bean1.class.getName(), fieldMetadatas);
    }
}
