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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionImpl;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.ANY;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.NUMBER;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class DecisionTableEditorDefinitionEnricherTest extends BaseDecisionTableEditorDefinitionTest {

    private static final String NODE_UUID = UUID.uuid();

    private static final String INPUT_DATA_NAME_1 = "z-inputData1";

    private static final String INPUT_DATA_NAME_2 = "a-inputData2";

    private static final QName INPUT_DATA_QNAME_1 = STRING.asQName();

    private static final QName INPUT_DATA_QNAME_2 = NUMBER.asQName();

    private static final String DECISION_NAME_1 = "b-decision1";

    private static final String DEFAULT_OUTPUT_NAME = "output-1";

    private static final String TYPE_PERSON = "tPerson";

    private static final String TYPE_COMPANY = "tCompany";

    private static final QName DECISION_QNAME_1 = STRING.asQName();

    private static final QName OUTPUT_DATA_QNAME = BuiltInType.DATE.asQName();

    private static final QName T_ADDRESS_QNAME = new QName("", "tAddress");

    private DMNDiagram diagram;

    private InputData inputData1;

    private InputData inputData2;

    private Decision decision1;

    @Before
    public void setup() {
        super.setup();

        this.diagram = new DMNDiagram();
        this.inputData1 = new InputData();
        this.inputData2 = new InputData();
        this.decision1 = new Decision();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithDecision() {
        setupGraphWithDiagram();
        setupGraphWithDecision();

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(1);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(DECISION_NAME_1);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(DECISION_QNAME_1);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithInputData() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(2);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_1);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithDecisionAndInputData() {
        setupGraphWithDiagram();
        setupGraphWithDecision();
        setupGraphWithInputData();

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(3);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(DECISION_NAME_1);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(DECISION_QNAME_1);
        assertThat(input.get(2).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(2).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1);
        assertThat(input.get(2).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_1);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithInputDataAndSimpleCustomType() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Definitions definitions = diagram.getDefinitions();

        final String simpleItemDefinitionName = "tSmurf";
        final QName simpleItemDefinitionTypeRef = new QName(QName.NULL_NS_URI, BuiltInType.DATE.getName());
        final ItemDefinition simpleItemDefinition = new ItemDefinition();
        simpleItemDefinition.setName(new Name(simpleItemDefinitionName));
        simpleItemDefinition.setTypeRef(simpleItemDefinitionTypeRef);
        definitions.getItemDefinition().add(simpleItemDefinition);

        mockItemDefinitionConstraint(simpleItemDefinition, "", ConstraintType.NONE);

        final QName inputData1TypeRef = new QName(QName.NULL_NS_URI, simpleItemDefinitionName);
        inputData1.getVariable().setTypeRef(inputData1TypeRef);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(2);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(simpleItemDefinitionTypeRef);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithInputDataAndComplexCustomType() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Definitions definitions = diagram.getDefinitions();

        final String complexItemDefinitionName = "tSmurf";
        final String complexItemDefinitionPart1Name = "tDateOfBirth";
        final String complexItemDefinitionPart2Name = "tIsBlue";
        final QName complexItemDefinitionPart1TypeRef = new QName(QName.NULL_NS_URI, BuiltInType.DATE.getName());
        final QName complexItemDefinitionPart2TypeRef = new QName(QName.NULL_NS_URI, BuiltInType.BOOLEAN.getName());
        final ItemDefinition complexItemDefinition = new ItemDefinition();
        complexItemDefinition.setName(new Name(complexItemDefinitionName));
        final ItemDefinition part1ItemDefinition = new ItemDefinition() {{
            setName(new Name(complexItemDefinitionPart1Name));
            setTypeRef(complexItemDefinitionPart1TypeRef);
        }};
        final ItemDefinition part2ItemDefinition = new ItemDefinition() {{
            setName(new Name(complexItemDefinitionPart2Name));
            setTypeRef(complexItemDefinitionPart2TypeRef);
        }};
        mockItemDefinitionConstraint(part1ItemDefinition, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(part2ItemDefinition, "", ConstraintType.NONE);
        complexItemDefinition.getItemComponent().add(part1ItemDefinition);
        complexItemDefinition.getItemComponent().add(part2ItemDefinition);

        definitions.getItemDefinition().add(complexItemDefinition);

        final QName inputData1TypeRef = new QName(QName.NULL_NS_URI, complexItemDefinitionName);
        inputData1.getVariable().setTypeRef(inputData1TypeRef);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(3);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + complexItemDefinitionPart1Name);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(complexItemDefinitionPart1TypeRef);
        assertThat(input.get(2).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(2).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + complexItemDefinitionPart2Name);
        assertThat(input.get(2).getInputExpression().getTypeRef()).isEqualTo(complexItemDefinitionPart2TypeRef);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenInputDataHasConstraints() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Definitions definitions = diagram.getDefinitions();

        final String complexItemDefinitionName = "tSmurf";
        final String complexItemDefinitionPart1Name = "tDateOfBirth";
        final String complexItemDefinitionPart2Name = "tIsBlue";
        final QName complexItemDefinitionPart1TypeRef = new QName(QName.NULL_NS_URI, BuiltInType.DATE.getName());
        final QName complexItemDefinitionPart2TypeRef = new QName(QName.NULL_NS_URI, BuiltInType.BOOLEAN.getName());
        final ItemDefinition complexItemDefinition = new ItemDefinition();
        complexItemDefinition.setName(new Name(complexItemDefinitionName));
        final ItemDefinition part1ItemDefinition = new ItemDefinition() {{
            setName(new Name(complexItemDefinitionPart1Name));
            setTypeRef(complexItemDefinitionPart1TypeRef);
        }};
        final ItemDefinition part2ItemDefinition = new ItemDefinition() {{
            setName(new Name(complexItemDefinitionPart2Name));
            setTypeRef(complexItemDefinitionPart2TypeRef);
        }};
        mockItemDefinitionConstraint(part1ItemDefinition, "date(\"2023-01-01\")", ConstraintType.EXPRESSION);
        mockItemDefinitionConstraint(part2ItemDefinition, "", ConstraintType.NONE);
        complexItemDefinition.getItemComponent().add(part1ItemDefinition);
        complexItemDefinition.getItemComponent().add(part2ItemDefinition);

        definitions.getItemDefinition().add(complexItemDefinition);

        final QName inputData1TypeRef = new QName(QName.NULL_NS_URI, complexItemDefinitionName);
        inputData1.getVariable().setTypeRef(inputData1TypeRef);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(3);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + complexItemDefinitionPart1Name);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(complexItemDefinitionPart1TypeRef);
        assertThat(input.get(1).getInputValues().getText().getValue()).isEqualTo("date(\"2023-01-01\")");
        assertThat(input.get(1).getInputValues().getConstraintType()).isEqualTo(ConstraintType.EXPRESSION);
        assertThat(input.get(2).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(2).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + complexItemDefinitionPart2Name);
        assertThat(input.get(2).getInputExpression().getTypeRef()).isEqualTo(complexItemDefinitionPart2TypeRef);
        assertThat(input.get(2).getInputValues().getText().getValue()).isEqualTo("");
        assertThat(input.get(2).getInputValues().getConstraintType()).isEqualTo(ConstraintType.NONE);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithInputDataAndRecursiveCustomType() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Definitions definitions = diagram.getDefinitions();

        final String tSmurfName = "tSmurf";
        final String tDateOfBirthName = "tDateOfBirth";
        final String tIsBlueName = "tIsBlue";
        final String tParentName = "tParent";
        final QName dateBuiltInType = new QName(QName.NULL_NS_URI, BuiltInType.DATE.getName());
        final QName booleanBuiltInType = new QName(QName.NULL_NS_URI, BuiltInType.BOOLEAN.getName());
        final QName parentCustomType = new QName(QName.NULL_NS_URI, tSmurfName);
        final ItemDefinition tSmurfCustomDataType = new ItemDefinition();
        tSmurfCustomDataType.setName(new Name(tSmurfName));
        final ItemDefinition birthDateItemDefinition = new ItemDefinition() {{
            setName(new Name(tDateOfBirthName));
            setTypeRef(dateBuiltInType);
        }};
        final ItemDefinition isBlueItemDefinition = new ItemDefinition() {{
            setName(new Name(tIsBlueName));
            setTypeRef(booleanBuiltInType);
        }};
        final ItemDefinition parentItemDefinition = new ItemDefinition() {{
            setName(new Name(tParentName));
            setTypeRef(parentCustomType);
        }};
        mockItemDefinitionConstraint(birthDateItemDefinition, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(isBlueItemDefinition, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(parentItemDefinition, "", ConstraintType.NONE);
        tSmurfCustomDataType.getItemComponent().add(birthDateItemDefinition);
        tSmurfCustomDataType.getItemComponent().add(isBlueItemDefinition);
        tSmurfCustomDataType.getItemComponent().add(parentItemDefinition);

        definitions.getItemDefinition().add(tSmurfCustomDataType);

        final QName inputData1TypeRef = new QName(QName.NULL_NS_URI, tSmurfName);
        inputData1.getVariable().setTypeRef(inputData1TypeRef);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(4);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + tDateOfBirthName);
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(dateBuiltInType);
        assertThat(input.get(2).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(2).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + tIsBlueName);
        assertThat(input.get(2).getInputExpression().getTypeRef()).isEqualTo(booleanBuiltInType);
        assertThat(input.get(3).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(3).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + "." + tParentName);
        assertThat(input.get(3).getInputExpression().getTypeRef()).isEqualTo(parentCustomType);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testModelEnrichmentWhenTopLevelDecisionTableWithMultipleHierarchyCustomTypes() {
        setupGraphWithDiagram();
        setupGraphWithInputData();

        final Definitions definitions = diagram.getDefinitions();

        final String tSmurf = "tSmurf";
        final String tSmurfAddress = "tSmurfAddress";
        final QName dateBuiltInType = new QName(QName.NULL_NS_URI, BuiltInType.DATE.getName());
        final QName stringBuiltInType = new QName(QName.NULL_NS_URI, STRING.getName());

        final ItemDefinition tSmurfAddressCustomDataType = new ItemDefinition();
        tSmurfAddressCustomDataType.setName(new Name(tSmurfAddress));
        final ItemDefinition line1ItemDefinition = new ItemDefinition() {{
            setName(new Name("line1"));
            setTypeRef(stringBuiltInType);
        }};
        final ItemDefinition line2ItemDefinition = new ItemDefinition() {{
            setName(new Name("line2"));
            setTypeRef(stringBuiltInType);
        }};
        mockItemDefinitionConstraint(line1ItemDefinition, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(line2ItemDefinition, "", ConstraintType.NONE);
        tSmurfAddressCustomDataType.getItemComponent().add(line1ItemDefinition);
        tSmurfAddressCustomDataType.getItemComponent().add(line2ItemDefinition);

        final ItemDefinition tSmurfCustomDataType = new ItemDefinition();
        tSmurfCustomDataType.setName(new Name(tSmurf));
        final ItemDefinition dobItemDefinition = new ItemDefinition() {{
            setName(new Name("dob"));
            setTypeRef(dateBuiltInType);
        }};
        final ItemDefinition addressItemDefinition = new ItemDefinition() {{
            setName(new Name("address"));
            getItemComponent().add(tSmurfAddressCustomDataType);
        }};
        mockItemDefinitionConstraint(dobItemDefinition, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(addressItemDefinition, "", ConstraintType.NONE);
        tSmurfCustomDataType.getItemComponent().add(dobItemDefinition);
        tSmurfCustomDataType.getItemComponent().add(addressItemDefinition);

        definitions.getItemDefinition().add(tSmurfCustomDataType);

        final QName inputData1TypeRef = new QName(QName.NULL_NS_URI, tSmurf);
        inputData1.getVariable().setTypeRef(inputData1TypeRef);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(NODE_UUID), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);

        final List<InputClause> input = model.getInput();
        assertThat(input.size()).isEqualTo(4);
        assertThat(input.get(0).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(0).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_2);
        assertThat(input.get(0).getInputExpression().getTypeRef()).isEqualTo(INPUT_DATA_QNAME_2);
        assertThat(input.get(1).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(1).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + ".address." + tSmurfAddress + ".line1");
        assertThat(input.get(1).getInputExpression().getTypeRef()).isEqualTo(stringBuiltInType);
        assertThat(input.get(2).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(2).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + ".address." + tSmurfAddress + ".line2");
        assertThat(input.get(2).getInputExpression().getTypeRef()).isEqualTo(stringBuiltInType);
        assertThat(input.get(3).getInputExpression()).isInstanceOf(InputClauseLiteralExpression.class);
        assertThat(input.get(3).getInputExpression().getText().getValue()).isEqualTo(INPUT_DATA_NAME_1 + ".dob");
        assertThat(input.get(3).getInputExpression().getTypeRef()).isEqualTo(dateBuiltInType);

        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    private void setupGraphWithDiagram() {
        final Node<Definition, Edge> diagramNode = new NodeImpl<>(UUID.uuid());
        final Definition<DMNDiagram> diagramDefinition = new DefinitionImpl<>(diagram);
        diagramNode.setContent(diagramDefinition);
        graph.addNode(diagramNode);
    }

    @SuppressWarnings("unchecked")
    private void setupGraphWithDecision() {
        Node<Definition, Edge> targetNode = graph.getNode(NODE_UUID);
        if (Objects.isNull(targetNode)) {
            targetNode = new NodeImpl<>(NODE_UUID);
            graph.addNode(targetNode);
        }

        final Node<Definition, Edge> sourceNode1 = new NodeImpl<>(UUID.uuid());
        decision1.getName().setValue(DECISION_NAME_1);
        final QName decision1QName = new QName(QName.NULL_NS_URI, STRING.getName());
        decision1.getVariable().setTypeRef(decision1QName);

        final Definition<Decision> sourceNode1Definition = new DefinitionImpl<>(decision1);
        sourceNode1.setContent(sourceNode1Definition);

        final Edge edge1 = new EdgeImpl<>(UUID.uuid());
        edge1.setTargetNode(targetNode);
        edge1.setSourceNode(sourceNode1);

        targetNode.getInEdges().add(edge1);
        sourceNode1.getOutEdges().add(edge1);

        graph.addNode(sourceNode1);
    }

    @SuppressWarnings("unchecked")
    private void setupGraphWithInputData() {
        Node<Definition, Edge> targetNode = graph.getNode(NODE_UUID);
        if (Objects.isNull(targetNode)) {
            targetNode = new NodeImpl<>(NODE_UUID);
            graph.addNode(targetNode);
        }

        final Node<Definition, Edge> sourceNode1 = new NodeImpl<>(UUID.uuid());
        final Node<Definition, Edge> sourceNode2 = new NodeImpl<>(UUID.uuid());
        inputData1.getName().setValue(INPUT_DATA_NAME_1);
        inputData2.getName().setValue(INPUT_DATA_NAME_2);
        final QName inputData1QName = new QName(QName.NULL_NS_URI, STRING.getName());
        final QName inputData2QName = new QName(QName.NULL_NS_URI, NUMBER.getName());
        inputData1.getVariable().setTypeRef(inputData1QName);
        inputData2.getVariable().setTypeRef(inputData2QName);

        final Definition<InputData> sourceNode1Definition = new DefinitionImpl<>(inputData1);
        final Definition<InputData> sourceNode2Definition = new DefinitionImpl<>(inputData2);
        sourceNode1.setContent(sourceNode1Definition);
        sourceNode2.setContent(sourceNode2Definition);

        final Edge edge1 = new EdgeImpl<>(UUID.uuid());
        final Edge edge2 = new EdgeImpl<>(UUID.uuid());
        edge1.setTargetNode(targetNode);
        edge1.setSourceNode(sourceNode1);
        edge2.setTargetNode(targetNode);
        edge2.setSourceNode(sourceNode2);

        targetNode.getInEdges().add(edge1);
        targetNode.getInEdges().add(edge2);
        sourceNode1.getOutEdges().add(edge1);
        sourceNode2.getOutEdges().add(edge2);

        graph.addNode(sourceNode1);
        graph.addNode(sourceNode2);
    }

    @Test
    public void testModelEnrichmentWhenTopLevelDecisionTableWithoutInputData() {
        final String uuid = UUID.uuid();
        final Node<Definition, Edge> node = new NodeImpl<>(uuid);
        graph.addNode(node);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        definition.enrich(Optional.of(uuid), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);
        assertStandardOutputClauseEnrichment(model);
        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenParentIsContextEntry() {
        final Decision decision = mock(Decision.class);
        final String name = "context-entry";
        final Context context = new Context();
        final ContextEntry contextEntry = new ContextEntry();
        context.getContextEntry().add(contextEntry);
        contextEntry.setVariable(new InformationItem(new Id(), new Description(), new Name(name), OUTPUT_DATA_QNAME));

        final Optional<DecisionTable> oModel = definition.getModelClass();
        oModel.get().setParent(contextEntry);
        contextEntry.setParent(context);
        context.setParent(decision);
        when(decision.asDMNModelInstrumentedBase()).thenReturn(contextEntry);

        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(OUTPUT_DATA_QNAME);

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenParentIsBKM() {
        final FunctionDefinition functionDefinition = mock(FunctionDefinition.class);
        final DMNModelInstrumentedBase dmnModelInstrumentedBase = mock(DMNModelInstrumentedBase.class);
        final BusinessKnowledgeModel businessKnowledgeModel = new BusinessKnowledgeModel();
        businessKnowledgeModel.setVariable(new InformationItemPrimary(new Id(), new Name(), OUTPUT_DATA_QNAME));
        businessKnowledgeModel.setEncapsulatedLogic(functionDefinition);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        oModel.get().setParent(functionDefinition);
        when(functionDefinition.asDMNModelInstrumentedBase()).thenReturn(dmnModelInstrumentedBase);
        when(dmnModelInstrumentedBase.getParent()).thenReturn(businessKnowledgeModel);

        definition.enrich(Optional.empty(), functionDefinition, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(OUTPUT_DATA_QNAME);

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenParentIsContextEntryDefaultResult() {
        final String name = "decision";

        final Context context = new Context();
        final ContextEntry contextEntry = new ContextEntry();
        context.getContextEntry().add(contextEntry);
        contextEntry.setParent(context);
        context.setParent(decision);

        decision.setName(new Name(name));
        decision.getVariable().setTypeRef(OUTPUT_DATA_QNAME);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        oModel.get().setParent(contextEntry);

        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(OUTPUT_DATA_QNAME);

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenParentIsNestedContextEntryDefaultResult() {
        final Decision decision = mock(Decision.class);
        final String name = "context-entry";
        final Context innerContext = new Context();
        final ContextEntry innerContextEntry = new ContextEntry();
        innerContext.getContextEntry().add(innerContextEntry);
        innerContextEntry.setParent(innerContext);

        final Context outerContext = new Context();
        final ContextEntry outerContextEntry = new ContextEntry();
        outerContext.getContextEntry().add(outerContextEntry);
        outerContextEntry.setParent(outerContext);
        innerContext.setParent(outerContextEntry);
        outerContext.setParent(decision);

        outerContextEntry.setVariable(new InformationItem(new Id(), new Description(), new Name(name), OUTPUT_DATA_QNAME));
        when(decision.asDMNModelInstrumentedBase()).thenReturn(outerContextEntry);

        final Optional<DecisionTable> oModel = definition.getModelClass();
        oModel.get().setParent(innerContextEntry);

        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(OUTPUT_DATA_QNAME);

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenHasExpressionIsHasVariable() {
        decision.setVariable(new InformationItemPrimary(new Id(), new Name(), OUTPUT_DATA_QNAME));

        final Optional<DecisionTable> oModel = definition.getModelClass();

        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(OUTPUT_DATA_QNAME);

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenHasExpressionIsNotHasVariable() {
        final Optional<DecisionTable> oModel = definition.getModelClass();

        definition.enrich(Optional.empty(), decision, oModel);

        final DecisionTable model = oModel.get();
        assertBasicEnrichment(model);
        assertStandardInputClauseEnrichment(model);

        final List<OutputClause> output = model.getOutput();
        assertThat(output.size()).isEqualTo(1);
        assertThat(output.get(0).getName()).isEqualTo(DEFAULT_OUTPUT_NAME);
        assertThat(output.get(0).getTypeRef()).isEqualTo(BuiltInType.UNDEFINED.asQName());

        assertStandardDecisionRuleEnrichment(model);
        assertParentHierarchyEnrichment(model);
    }

    @Test
    public void testModelEnrichmentWhenDecisionTypeRefIsStructureWithMultipleFields() {

        final DMNGraphUtils dmnGraphUtils = mock(DMNGraphUtils.class);
        final Definitions definitions = mock(Definitions.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Decision decision = mock(Decision.class);
        final InformationItemPrimary informationItemPrimary = mock(InformationItemPrimary.class);
        final ItemDefinition tPerson = mockTPersonStructure();

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(Collections.singletonList(tPerson));
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(decision.getVariable()).thenReturn(informationItemPrimary);
        when(informationItemPrimary.getTypeRef()).thenReturn(new QName("", TYPE_PERSON));

        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, dmnGraphUtils, itemDefinitionUtils);
        final Optional<DecisionTable> oModel = definition.getModelClass();
        final DecisionTable model = oModel.get();
        enricher.buildOutputClausesByDataType(hasExpression, model, new DecisionRule(), null);

        final List<OutputClause> outputClauses = model.getOutput();
        assertThat(outputClauses.size()).isEqualTo(2);

        final OutputClause outputClause1 = outputClauses.get(0);
        final OutputClause outputClause2 = outputClauses.get(1);

        assertEquals("age", outputClause1.getName());
        assertEquals(NUMBER.asQName(), outputClause1.getTypeRef());

        assertEquals("name", outputClause2.getName());
        assertEquals(STRING.asQName(), outputClause2.getTypeRef());
    }

    @Test
    public void testModelEnrichmentWhenDecisionTypeRefIsStructureAndOneSubfieldIsStructure() {

        final DMNGraphUtils dmnGraphUtils = mock(DMNGraphUtils.class);
        final Definitions definitions = mock(Definitions.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Decision decision = mock(Decision.class);
        final InformationItemPrimary informationItemPrimary = mock(InformationItemPrimary.class);
        final ItemDefinition tCompany = mockTCompanyStructure();

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(Collections.singletonList(tCompany));
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(decision.getVariable()).thenReturn(informationItemPrimary);
        when(informationItemPrimary.getTypeRef()).thenReturn(new QName("", TYPE_COMPANY));

        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, dmnGraphUtils, itemDefinitionUtils);
        final Optional<DecisionTable> oModel = definition.getModelClass();
        final DecisionTable model = oModel.get();
        enricher.buildOutputClausesByDataType(hasExpression, model, new DecisionRule(), null);

        final List<OutputClause> outputClauses = model.getOutput();
        assertThat(outputClauses.size()).isEqualTo(2);

        final OutputClause outputClause1 = outputClauses.get(0);
        final OutputClause outputClause2 = outputClauses.get(1);

        assertEquals("address", outputClause1.getName());
        assertEquals(ANY.asQName(), outputClause1.getTypeRef());

        assertEquals("name", outputClause2.getName());
        assertEquals(STRING.asQName(), outputClause2.getTypeRef());
    }

    private ItemDefinition mockTCompanyStructure() {
        final ItemDefinition tCompany = mock(ItemDefinition.class);
        final ItemDefinition name = mock(ItemDefinition.class);
        final ItemDefinition tAddress = mock(ItemDefinition.class);

        /* === ItemDefinition ===
         * - tCompany (Structure)
         *   - name     (String)
         *   - tAddress (Structure)
         * ======================
         */

        when(name.getName()).thenReturn(new Name("name"));
        when(name.getTypeRef()).thenReturn(STRING.asQName());
        when(name.getItemComponent()).thenReturn(emptyList());

        when(tAddress.getName()).thenReturn(new Name("address"));
        when(tAddress.getTypeRef()).thenReturn(T_ADDRESS_QNAME);
        when(tAddress.getItemComponent()).thenReturn(singletonList(name));

        when(tCompany.getName()).thenReturn(new Name(TYPE_COMPANY));
        when(tCompany.getTypeRef()).thenReturn(null);
        when(tCompany.getItemComponent()).thenReturn(asList(name, tAddress));

        mockItemDefinitionConstraint(name, "", ConstraintType.NONE);

        return tCompany;
    }

    @Test
    public void testModelEnrichmentWhenDecisionTypeRefIsStructureWithNoFields() {

        final DMNGraphUtils dmnGraphUtils = mock(DMNGraphUtils.class);
        final Definitions definitions = mock(Definitions.class);
        final HasExpression hasExpression = mock(HasExpression.class);
        final Decision decision = mock(Decision.class);
        final InformationItemPrimary informationItemPrimary = mock(InformationItemPrimary.class);
        final ItemDefinition tPerson = mock(ItemDefinition.class);
        final QName tPersonTypeRef = new QName("", TYPE_PERSON);

        when(tPerson.getName()).thenReturn(new Name(TYPE_PERSON));
        when(tPerson.getTypeRef()).thenReturn(tPersonTypeRef);
        when(tPerson.getItemComponent()).thenReturn(emptyList());

        when(dmnGraphUtils.getModelDefinitions()).thenReturn(definitions);
        when(definitions.getItemDefinition()).thenReturn(Collections.singletonList(tPerson));
        when(hasExpression.asDMNModelInstrumentedBase()).thenReturn(decision);
        when(decision.getVariable()).thenReturn(informationItemPrimary);
        when(informationItemPrimary.getTypeRef()).thenReturn(tPersonTypeRef);

        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, dmnGraphUtils, itemDefinitionUtils);
        final Optional<DecisionTable> oModel = definition.getModelClass();
        final DecisionTable model = oModel.get();
        enricher.buildOutputClausesByDataType(hasExpression, model, new DecisionRule(), null);

        final List<OutputClause> outputClauses = model.getOutput();
        assertThat(outputClauses.size()).isEqualTo(1);

        final OutputClause outputClause = outputClauses.get(0);

        assertEquals(TYPE_PERSON, outputClause.getName());
        assertEquals(tPersonTypeRef, outputClause.getTypeRef());
    }

    @Test
    public void testAddInputClauseRequirement() {

        final List<DecisionTableEditorDefinitionEnricher.ClauseRequirement> inputClauseRequirements = new ArrayList<>();
        final String inputData = "InputData";
        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, null, itemDefinitionUtils);
        final ItemDefinition tPerson = mockTPersonStructure();

        enricher.addInputClauseRequirement(tPerson, inputClauseRequirements, inputData);

        assertEquals(2, inputClauseRequirements.size());

        final DecisionTableEditorDefinitionEnricher.ClauseRequirement inputClause1 = inputClauseRequirements.get(0);
        final DecisionTableEditorDefinitionEnricher.ClauseRequirement inputClause2 = inputClauseRequirements.get(1);

        assertEquals("InputData.name", inputClause1.text);
        assertEquals(STRING.getName(), inputClause1.typeRef.getLocalPart());

        assertEquals("InputData.age", inputClause2.text);
        assertEquals(NUMBER.getName(), inputClause2.typeRef.getLocalPart());
    }

    @Test
    public void testAddInputClauseRequirement_simpleCustomType() {

        final List<DecisionTableEditorDefinitionEnricher.ClauseRequirement> inputClauseRequirements = new ArrayList<>();
        final String inputData = "InputData";
        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, null, itemDefinitionUtils);
        final ItemDefinition tSuperString = mockTSuperString();

        when(itemDefinitionUtils.findByName("tSuperString")).thenReturn(Optional.of(tSuperString));
        doCallRealMethod().when(itemDefinitionUtils).normaliseTypeRef(any());

        enricher.addInputClauseRequirement(tSuperString, inputClauseRequirements, inputData);

        assertEquals(1, inputClauseRequirements.size());

        final DecisionTableEditorDefinitionEnricher.ClauseRequirement inputClause1 = inputClauseRequirements.get(0);

        assertEquals("InputData", inputClause1.text);
        assertEquals("tSuperString", inputClause1.typeRef.getLocalPart());

        reset(itemDefinitionUtils);
    }

    private ItemDefinition mockTPersonStructure() {
        final ItemDefinition tPerson = mock(ItemDefinition.class);
        final ItemDefinition name = mock(ItemDefinition.class);
        final ItemDefinition age = mock(ItemDefinition.class);

        /* === ItemDefinition ===
         * - tPerson (Structure)
         *   - name  (String)
         *   - age   (Number)
         * ======================
         */

        when(name.getName()).thenReturn(new Name("name"));
        when(name.getTypeRef()).thenReturn(STRING.asQName());
        when(name.getItemComponent()).thenReturn(emptyList());

        when(age.getName()).thenReturn(new Name("age"));
        when(age.getTypeRef()).thenReturn(NUMBER.asQName());
        when(age.getItemComponent()).thenReturn(emptyList());

        when(tPerson.getName()).thenReturn(new Name(TYPE_PERSON));
        when(tPerson.getTypeRef()).thenReturn(null);
        when(tPerson.getItemComponent()).thenReturn(asList(name, age));

        mockItemDefinitionConstraint(name, "", ConstraintType.NONE);
        mockItemDefinitionConstraint(age, "", ConstraintType.NONE);

        return tPerson;
    }

    private ItemDefinition mockTSuperString() {
        
        final ItemDefinition superString = mock(ItemDefinition.class);

        when(superString.getName()).thenReturn(new Name("tSuperString"));
        when(superString.getTypeRef()).thenReturn(STRING.asQName());
        when(superString.getItemComponent()).thenReturn(emptyList());

        mockItemDefinitionConstraint(superString, "", ConstraintType.NONE);

        return superString;
    }

    private void mockItemDefinitionConstraint(final ItemDefinition itemDefinition, final String constraint, final ConstraintType constraintType) {
        when(itemDefinitionUtils.getConstraintText(itemDefinition)).thenReturn(constraint);
        when(itemDefinitionUtils.getConstraintType(itemDefinition)).thenReturn(constraintType);
    }

    @Test
    public void testAddInputClauseRequirementWhenDataTypeIsStructureAndDontHaveFields() {

        final ItemDefinition tPerson = mock(ItemDefinition.class);
        final String inputData = "InputData";
        final List<DecisionTableEditorDefinitionEnricher.ClauseRequirement> inputClauseRequirements = new ArrayList<>();
        final DecisionTableEditorDefinitionEnricher enricher = new DecisionTableEditorDefinitionEnricher(null, null, itemDefinitionUtils);

        when(tPerson.getName()).thenReturn(new Name(TYPE_PERSON));
        when(tPerson.getTypeRef()).thenReturn(null);
        when(tPerson.getItemComponent()).thenReturn(emptyList());

        when(itemDefinitionUtils.findByName(TYPE_PERSON)).thenReturn(Optional.of(tPerson));
        doCallRealMethod().when(itemDefinitionUtils).normaliseTypeRef(any());

        enricher.addInputClauseRequirement(tPerson, inputClauseRequirements, inputData);

        assertEquals(1, inputClauseRequirements.size());

        assertEquals("InputData", inputClauseRequirements.get(0).text);
        assertEquals(TYPE_PERSON, inputClauseRequirements.get(0).typeRef.getLocalPart());

        reset(itemDefinitionUtils);
    }

    @Test
    public void testComputingClauseNameWithoutModelRef() {
        final ItemDefinition itemComponent = new ItemDefinition();
        itemComponent.setName(new Name("model.age"));
        itemComponent.setAllowOnlyVisualChange(true);

        assertEquals("age", new DecisionTableEditorDefinitionEnricher(null, null, null)
                .computeClauseName(itemComponent));
    }

    @Test
    public void testComputingClauseNameWithoutModelRefWhenModelRefHasMultipleLevels() {
        final ItemDefinition itemComponent = new ItemDefinition();
        itemComponent.setName(new Name("a.b.c.age"));
        itemComponent.setAllowOnlyVisualChange(true);

        assertEquals("age", new DecisionTableEditorDefinitionEnricher(null, null, null)
                .computeClauseName(itemComponent));
    }

    @Test
    public void testComputingClauseNameWithoutModelRefWhenModelRefAlreadyMissing() {
        final ItemDefinition itemComponent = new ItemDefinition();
        itemComponent.setName(new Name("age"));
        itemComponent.setAllowOnlyVisualChange(true);

        assertEquals("age", new DecisionTableEditorDefinitionEnricher(null, null, null)
                .computeClauseName(itemComponent));
    }

    @Test
    public void testComputingClauseNameWithoutModelRefWhenLocalPartEndsWithDot() {
        final ItemDefinition itemComponent = new ItemDefinition();
        itemComponent.setName(new Name("age."));
        itemComponent.setAllowOnlyVisualChange(true);

        assertEquals("age.", new DecisionTableEditorDefinitionEnricher(null, null, null)
                .computeClauseName(itemComponent));
    }
}
