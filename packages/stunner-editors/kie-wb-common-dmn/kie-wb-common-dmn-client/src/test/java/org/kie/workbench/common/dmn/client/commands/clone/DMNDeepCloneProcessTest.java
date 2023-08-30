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

package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasText;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dimensions.DecisionServiceRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.AllowedAnswers;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.KnowledgeSourceType;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Question;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.ClassUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind.JAVA;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class DMNDeepCloneProcessTest extends AbstractCloneProcessTest {

    private static final String SOURCE_ID = "source-id";
    private static final String INPUT_DATA_NAME = "input-data";
    private static final String FIRST_URL = "firstURL";
    private static final String SECOND_URL = "secondURL";
    private static final String DECISION_SERVICE_NAME = "decision-service";
    private static final String KNOWLEDGE_SOURCE_NAME = "knowledge-source";
    private static final String FUNCTION_ID = "function-id";
    private static final String CONTEXT_ID = "context-id";
    private static final String BKM_SOURCE_NAME = "bkm-source";
    private static final String DECISION_SOURCE_NAME = "decision-source";
    private static final String QUESTION = "question?";
    private static final String ANSWER = "answer";
    private static final String TEXT_DATA = "text-data";
    private static final String FIRST_INDEX_IN_SUFFIX = "-1";
    private static final String SECOND_INDEX_IN_SUFFIX = "-2";
    private static final String THIRD_INDEX_IN_SUFFIX = "-3";
    private static final String FORTH_INDEX_IN_SUFFIX = "-4";
    private DMNDeepCloneProcess dmnDeepCloneProcess;

    @Mock
    private SessionManager sessionManager;

    @Mock
    private ClientSession currentSession;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Diagram diagram;

    @Mock
    private Graph graph;

    @Mock
    private Node nodeWithName, nodeWithText, nodeWithNone;

    @Mock
    private View namedElementContent, textElementContent, noneContent;

    @Mock
    private NamedElement namedElementDefinition;

    @Mock
    private HasText hasTextDefinition;

    @Mock
    private Name name;

    @Mock
    private Text text;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        when(sessionManager.getCurrentSession()).thenReturn(currentSession);
        when(currentSession.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getGraph()).thenReturn(graph);
        when(graph.nodes()).thenReturn(Collections.emptyList());

        dmnDeepCloneProcess = new DMNDeepCloneProcess(factoryManager, adapterManager, new ClassUtils(), sessionManager);
    }

    @Test
    public void testCloneWhenSourceIsInputData() {
        final InputData source = buildInputData();
        setLinks(source, FIRST_URL, SECOND_URL);

        final InputData cloned = dmnDeepCloneProcess.clone(source, new InputData());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(INPUT_DATA_NAME + FIRST_INDEX_IN_SUFFIX);
        assertThat(cloned.getVariable().getTypeRef()).isEqualTo(BuiltInType.STRING.asQName());
        assertThat(cloned.getLinksHolder().getValue().getLinks())
                .hasSize(2)
                .extracting(DMNExternalLink::getUrl).contains(FIRST_URL, SECOND_URL);
    }

    private InputData buildInputData() {
        return new InputData(
                new Id(SOURCE_ID),
                new Description(),
                new Name(INPUT_DATA_NAME),
                buildInformationItemPrimary(BuiltInType.STRING),
                new StylingSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    @Test
    public void testCloneWhenSourceIsTextAnnotation() {
        final TextAnnotation source = buildTextAnnotation();
        final TextAnnotation target = new TextAnnotation();
        target.getId().setValue(SOURCE_ID);

        final TextAnnotation cloned = dmnDeepCloneProcess.clone(source, target);

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getText().getValue()).isEqualTo(TEXT_DATA + FIRST_INDEX_IN_SUFFIX);
    }

    private TextAnnotation buildTextAnnotation() {
        return new TextAnnotation(
                new Id(SOURCE_ID),
                new Description(),
                new Text(TEXT_DATA),
                new TextFormat(),
                new StylingSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    @Test
    public void testCloneWhenSourceIsDecisionService() {
        final DecisionService source = buildDecisionService();

        final DecisionService cloned = dmnDeepCloneProcess.clone(source, new DecisionService());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(DECISION_SERVICE_NAME + FIRST_INDEX_IN_SUFFIX);
        assertThat(cloned.getVariable().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
    }

    private DecisionService buildDecisionService() {
        return new DecisionService(
                new Id(SOURCE_ID),
                new Description(),
                new Name(DECISION_SERVICE_NAME),
                buildInformationItemPrimary(BuiltInType.BOOLEAN),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new StylingSet(),
                new DecisionServiceRectangleDimensionsSet(),
                new DecisionServiceDividerLineY()
        );
    }

    @Test
    public void testCloneWhenSourceIsKnowledgeSource() {
        final KnowledgeSource source = buildKnowledgeSource();
        setLinks(source, FIRST_URL, SECOND_URL);

        final KnowledgeSource cloned = dmnDeepCloneProcess.clone(source, new KnowledgeSource());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(KNOWLEDGE_SOURCE_NAME + FIRST_INDEX_IN_SUFFIX);
        assertThat(cloned.getLinksHolder().getValue().getLinks())
                .hasSize(2)
                .extracting(DMNExternalLink::getUrl).contains(FIRST_URL, SECOND_URL);
    }

    private KnowledgeSource buildKnowledgeSource() {
        return new KnowledgeSource(
                new Id(SOURCE_ID),
                new Description(),
                new Name(KNOWLEDGE_SOURCE_NAME),
                new KnowledgeSourceType(),
                new LocationURI(),
                new StylingSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    @Test
    public void testCloneWhenSourceIsBusinessKnowledgeModel() {
        final BusinessKnowledgeModel source = buildBusinessKnowledgeModel();
        setLinks(source, FIRST_URL, SECOND_URL);

        final BusinessKnowledgeModel cloned = dmnDeepCloneProcess.clone(source, new BusinessKnowledgeModel());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(BKM_SOURCE_NAME + FIRST_INDEX_IN_SUFFIX);
        assertThat(cloned.getLinksHolder().getValue().getLinks())
                .hasSize(2)
                .extracting(DMNExternalLink::getUrl).contains(FIRST_URL, SECOND_URL);
        assertThat(cloned.getVariable().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(cloned.getEncapsulatedLogic()).isNotNull();
        assertThat(cloned.getEncapsulatedLogic().getId().getValue()).isNotEqualTo(FUNCTION_ID);
        assertThat(cloned.getEncapsulatedLogic().getKind()).isEqualTo(JAVA);
        assertThat(cloned.getEncapsulatedLogic().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(cloned.getEncapsulatedLogic().getExpression()).isInstanceOf(Context.class);
        assertThat(cloned.getEncapsulatedLogic().getExpression().getId().getValue()).isNotEqualTo(CONTEXT_ID);
        assertThat(cloned.getEncapsulatedLogic().getExpression().getTypeRef()).isEqualTo(BuiltInType.NUMBER.asQName());
    }

    @Test
    public void testCloneWhenSourceIsDecision() {
        final Decision source = buildDecision();
        setLinks(source, FIRST_URL, SECOND_URL);

        final Decision cloned = dmnDeepCloneProcess.clone(source, new Decision());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(DECISION_SOURCE_NAME + FIRST_INDEX_IN_SUFFIX);
        assertThat(cloned.getLinksHolder().getValue().getLinks())
                .hasSize(2)
                .extracting(DMNExternalLink::getUrl).contains(FIRST_URL, SECOND_URL);
        assertThat(cloned.getVariable().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(cloned.getQuestion().getValue()).isEqualTo(QUESTION);
        assertThat(cloned.getAllowedAnswers().getValue()).isEqualTo(ANSWER);

        assertThat(cloned.getExpression()).isNotNull();
        assertThat(cloned.getExpression()).isInstanceOf(FunctionDefinition.class);
        assertThat(cloned.getExpression().getId().getValue()).isNotEqualTo(FUNCTION_ID);
        assertThat(cloned.getExpression().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(((FunctionDefinition) cloned.getExpression()).getExpression()).isInstanceOf(Context.class);
        assertThat(((FunctionDefinition) cloned.getExpression()).getExpression().getId().getValue()).isNotEqualTo(CONTEXT_ID);
        assertThat(((FunctionDefinition) cloned.getExpression()).getExpression().getTypeRef()).isEqualTo(BuiltInType.NUMBER.asQName());
    }

    private BusinessKnowledgeModel buildBusinessKnowledgeModel() {
        return new BusinessKnowledgeModel(
                new Id(SOURCE_ID),
                new Description(),
                new Name(BKM_SOURCE_NAME),
                buildInformationItemPrimary(BuiltInType.BOOLEAN),
                buildFunctionDefinition(),
                new StylingSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    private Decision buildDecision() {
        return new Decision(
                new Id(SOURCE_ID),
                new Description(),
                new Name(DECISION_SOURCE_NAME),
                new Question(QUESTION),
                new AllowedAnswers(ANSWER),
                buildInformationItemPrimary(BuiltInType.BOOLEAN),
                buildFunctionDefinition(),
                new StylingSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    private FunctionDefinition buildFunctionDefinition() {
        final FunctionDefinition encapsulatedLogic = new FunctionDefinition(
                new Id(FUNCTION_ID),
                new Description(),
                new QName(BuiltInType.BOOLEAN),
                new Context(new Id(CONTEXT_ID), new Description(), new QName(BuiltInType.NUMBER))
        );
        encapsulatedLogic.setKind(JAVA);
        return encapsulatedLogic;
    }

    private InformationItemPrimary buildInformationItemPrimary(final BuiltInType builtInType) {
        final InformationItemPrimary informationItemPrimary = new InformationItemPrimary();
        informationItemPrimary.setTypeRef(new QName(builtInType));
        return informationItemPrimary;
    }

    private void setLinks(final DRGElement drgElement, final String... links) {
        Stream.of(links)
                .forEach(link ->
                                 drgElement.getLinksHolder()
                                         .getValue()
                                         .getLinks()
                                         .add(new DMNExternalLink(link, "description"))
                );
    }

    @Test
    public void testComposingUniqueNodeName() {
        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(INPUT_DATA_NAME))
                .isEqualTo(INPUT_DATA_NAME + FIRST_INDEX_IN_SUFFIX);
    }

    @Test
    public void testComposingUniqueNodeNameWhenItAlreadyContainsIndexedSuffix() {
        mockSingleNodeInTheGraph();

        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(INPUT_DATA_NAME + FIRST_INDEX_IN_SUFFIX))
                .isEqualTo(INPUT_DATA_NAME + SECOND_INDEX_IN_SUFFIX);
    }

    private void mockSingleNodeInTheGraph() {
        when(graph.nodes()).thenReturn(Collections.singletonList(nodeWithName));

        when(nodeWithName.getContent()).thenReturn(namedElementContent);
        when(namedElementContent.getDefinition()).thenReturn(namedElementDefinition);
        when(namedElementDefinition.getName()).thenReturn(name);
        when(name.getValue()).thenReturn(INPUT_DATA_NAME + FIRST_INDEX_IN_SUFFIX);
    }

    @Test
    public void testComposingUniqueNodeNameWhenItContainsNotIndexedSuffix() {
        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(INPUT_DATA_NAME + "-A3"))
                .isEqualTo(INPUT_DATA_NAME + "-A3" + FIRST_INDEX_IN_SUFFIX);
    }

    @Test
    public void testComposingUniqueNodeNameWhenNextIndexInSequenceAlreadyPresent() {
        mockMultipleNodesInTheGraph();

        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(INPUT_DATA_NAME + FIRST_INDEX_IN_SUFFIX))
                .isEqualTo(INPUT_DATA_NAME + FORTH_INDEX_IN_SUFFIX);
    }

    private void mockMultipleNodesInTheGraph() {
        when(graph.nodes()).thenReturn(Arrays.asList(nodeWithName, nodeWithText, nodeWithNone));

        when(nodeWithName.getContent()).thenReturn(namedElementContent);
        when(namedElementContent.getDefinition()).thenReturn(namedElementDefinition);
        when(namedElementDefinition.getName()).thenReturn(name);
        when(name.getValue()).thenReturn(INPUT_DATA_NAME + SECOND_INDEX_IN_SUFFIX);

        when(nodeWithText.getContent()).thenReturn(textElementContent);
        when(textElementContent.getDefinition()).thenReturn(hasTextDefinition);
        when(hasTextDefinition.getText()).thenReturn(text);
        when(text.getValue()).thenReturn(INPUT_DATA_NAME + THIRD_INDEX_IN_SUFFIX);

        when(nodeWithNone.getContent()).thenReturn(noneContent);
        when(noneContent.getDefinition()).thenReturn(new Object());
    }

    @Test
    public void testComposingUniqueNodeNameWhenItIsEmpty() {
        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(""))
                .isEqualTo(FIRST_INDEX_IN_SUFFIX);
    }

    @Test
    public void testComposingUniqueNodeNameWhenItIsNull() {
        assertThat(dmnDeepCloneProcess.composeUniqueNodeName(null))
                .isEqualTo(FIRST_INDEX_IN_SUFFIX);
    }
}
