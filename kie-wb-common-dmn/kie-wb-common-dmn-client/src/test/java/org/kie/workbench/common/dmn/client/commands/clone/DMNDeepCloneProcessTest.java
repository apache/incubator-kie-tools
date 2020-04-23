/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.clone;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
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
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.definition.clone.AbstractCloneProcessTest;
import org.kie.workbench.common.stunner.core.util.ClassUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition.Kind.JAVA;

public class DMNDeepCloneProcessTest extends AbstractCloneProcessTest {

    public static final String SOURCE_ID = "source-id";
    public static final String INPUT_DATA_NAME = "input-data";
    public static final String FIRST_URL = "firstURL";
    public static final String SECOND_URL = "secondURL";
    public static final String DECISION_SERVICE_NAME = "decision-service";
    public static final String KNOWLEDGE_SOURCE_NAME = "knowledge-source";
    public static final String FUNCTION_ID = "function-id";
    public static final String CONTEXT_ID = "context-id";
    public static final String BKM_SOURCE_NAME = "bkm-source";
    public static final String DECISION_SOURCE_NAME = "decision-source";
    public static final String QUESTION = "question?";
    public static final String ANSWER = "answer";
    private DMNDeepCloneProcess dmnDeepCloneProcess;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        dmnDeepCloneProcess = new DMNDeepCloneProcess(factoryManager, adapterManager, new ClassUtils());
    }

    @Test
    public void testCloneWhenSourceIsInputData() {
        final InputData source = buildInputData();
        setLinks(source, FIRST_URL, SECOND_URL);

        final InputData cloned = dmnDeepCloneProcess.clone(source, new InputData());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(INPUT_DATA_NAME);
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
                new BackgroundSet(),
                new FontSet(),
                new GeneralRectangleDimensionsSet()
        );
    }

    @Test
    public void testCloneWhenSourceIsDecisionService() {
        final DecisionService source = buildDecisionService();

        final DecisionService cloned = dmnDeepCloneProcess.clone(source, new DecisionService());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(DECISION_SERVICE_NAME);
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
                new BackgroundSet(),
                new FontSet(),
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
        assertThat(cloned.getName().getValue()).isEqualTo(KNOWLEDGE_SOURCE_NAME);
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
                new BackgroundSet(),
                new FontSet(),
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
        assertThat(cloned.getName().getValue()).isEqualTo(BKM_SOURCE_NAME);
        assertThat(cloned.getLinksHolder().getValue().getLinks())
                .hasSize(2)
                .extracting(DMNExternalLink::getUrl).contains(FIRST_URL, SECOND_URL);
        assertThat(cloned.getVariable().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(cloned.getEncapsulatedLogic()).isNotNull();
        assertThat(cloned.getEncapsulatedLogic().getId().getValue()).isNotEqualTo(FUNCTION_ID);
        assertThat(cloned.getEncapsulatedLogic().getKind()).isEqualTo(JAVA);
        assertThat(cloned.getEncapsulatedLogic().getTypeRef()).isEqualTo(BuiltInType.BOOLEAN.asQName());
        assertThat(cloned.getEncapsulatedLogic().getExpression()).isInstanceOf(Context.class);
        assertThat(cloned.getEncapsulatedLogic().getExpression().getId()).isNotEqualTo(CONTEXT_ID);
        assertThat(cloned.getEncapsulatedLogic().getExpression().getTypeRef()).isEqualTo(BuiltInType.NUMBER.asQName());
    }

    @Test
    public void testCloneWhenSourceIsDecision() {
        final Decision source = buildDecision();
        setLinks(source, FIRST_URL, SECOND_URL);

        final Decision cloned = dmnDeepCloneProcess.clone(source, new Decision());

        assertThat(cloned).isNotNull();
        assertThat(cloned.getId().getValue()).isNotEqualTo(SOURCE_ID);
        assertThat(cloned.getName().getValue()).isEqualTo(DECISION_SOURCE_NAME);
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
        assertThat(((FunctionDefinition) cloned.getExpression()).getExpression().getId()).isNotEqualTo(CONTEXT_ID);
        assertThat(((FunctionDefinition) cloned.getExpression()).getExpression().getTypeRef()).isEqualTo(BuiltInType.NUMBER.asQName());
    }

    private BusinessKnowledgeModel buildBusinessKnowledgeModel() {
        return new BusinessKnowledgeModel(
                new Id(SOURCE_ID),
                new Description(),
                new Name(BKM_SOURCE_NAME),
                buildInformationItemPrimary(BuiltInType.BOOLEAN),
                buildFunctionDefinition(),
                new BackgroundSet(),
                new FontSet(),
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
                new BackgroundSet(),
                new FontSet(),
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
}
