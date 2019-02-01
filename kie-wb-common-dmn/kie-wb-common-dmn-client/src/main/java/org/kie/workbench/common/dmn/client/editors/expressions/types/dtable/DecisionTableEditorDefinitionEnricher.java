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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.v1_1.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.v1_1.OutputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class DecisionTableEditorDefinitionEnricher implements ExpressionEditorModelEnricher<DecisionTable> {

    private SessionManager sessionManager;
    private DMNGraphUtils dmnGraphUtils;

    private static class InputClauseRequirement {

        private String text;
        private QName typeRef;

        public InputClauseRequirement(final String text,
                                      final QName typeRef) {
            this.text = text;
            this.typeRef = typeRef;
        }
    }

    public DecisionTableEditorDefinitionEnricher() {
        //CDI proxy
    }

    @Inject
    public DecisionTableEditorDefinitionEnricher(final SessionManager sessionManager,
                                                 final DMNGraphUtils dmnGraphUtils) {
        this.sessionManager = sessionManager;
        this.dmnGraphUtils = dmnGraphUtils;
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<DecisionTable> expression) {
        expression.ifPresent(dtable -> {
            dtable.setHitPolicy(HitPolicy.UNIQUE);
            dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

            final InputClause inputClause = new InputClause();
            final LiteralExpression literalExpression = new LiteralExpression();
            literalExpression.getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
            inputClause.setInputExpression(literalExpression);
            dtable.getInput().add(inputClause);

            final OutputClause outputClause = new OutputClause();
            outputClause.setName(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable));
            dtable.getOutput().add(outputClause);

            final DecisionRule decisionRule = new DecisionRule();
            final UnaryTests decisionRuleUnaryTest = new UnaryTests();
            decisionRuleUnaryTest.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
            decisionRule.getInputEntry().add(decisionRuleUnaryTest);

            final LiteralExpression decisionRuleLiteralExpression = new LiteralExpression();
            decisionRuleLiteralExpression.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
            decisionRule.getOutputEntry().add(decisionRuleLiteralExpression);

            final Description description = new Description();
            description.setValue(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
            decisionRule.setDescription(description);

            dtable.getRule().add(decisionRule);

            //Setup parent relationships
            inputClause.setParent(dtable);
            outputClause.setParent(dtable);
            decisionRule.setParent(dtable);
            literalExpression.setParent(inputClause);
            decisionRuleUnaryTest.setParent(decisionRule);
            decisionRuleLiteralExpression.setParent(decisionRule);

            if (nodeUUID.isPresent()) {
                enrichInputClauses(nodeUUID.get(), dtable);
            } else {
                enrichOutputClauses(dtable);
            }

            if (dtable.getOutput().size() > 0) {
                final HasTypeRef hasTypeRef = TypeRefUtils.getTypeRefOfExpression(dtable, hasExpression);
                if (!Objects.isNull(hasTypeRef)) {
                    dtable.getOutput().get(0).setTypeRef(hasTypeRef.getTypeRef());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    void enrichInputClauses(final String uuid,
                            final DecisionTable dtable) {
        final Graph<?, Node> graph = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph();
        final Node<?, Edge> node = graph.getNode(uuid);
        if (Objects.isNull(node)) {
            return;
        }

        //Get all InputData nodes feeding into this DecisionTable
        final List<InputData> inputDataSet = node.getInEdges().stream()
                .map(Edge::getSourceNode)
                .map(Node::getContent)
                .filter(content -> content instanceof Definition)
                .map(content -> (Definition) content)
                .map(Definition::getDefinition)
                .filter(definition -> definition instanceof InputData)
                .map(definition -> (InputData) definition)
                .collect(Collectors.toList());
        if (inputDataSet.isEmpty()) {
            return;
        }

        //Extract individual components of InputData TypeRefs
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        final List<InputClauseRequirement> inputClauseRequirements = new ArrayList<>();
        inputDataSet.forEach(inputData -> addInputClauseRequirement(inputData.getVariable().getTypeRef(),
                                                                    definitions,
                                                                    inputClauseRequirements,
                                                                    inputData.getName().getValue()));

        //Add InputClause columns for each InputData TypeRef component, sorted alphabetically
        dtable.getInput().clear();
        dtable.getRule().stream().forEach(decisionRule -> decisionRule.getInputEntry().clear());
        inputClauseRequirements
                .stream()
                .sorted(Comparator.comparing(inputClauseRequirement -> inputClauseRequirement.text))
                .forEach(inputClauseRequirement -> {
                    final InputClause inputClause = new InputClause();
                    final LiteralExpression literalExpression = new LiteralExpression();
                    literalExpression.getText().setValue(inputClauseRequirement.text);
                    literalExpression.setTypeRef(inputClauseRequirement.typeRef);
                    inputClause.setInputExpression(literalExpression);
                    dtable.getInput().add(inputClause);

                    dtable.getRule().stream().forEach(decisionRule -> {
                        final UnaryTests decisionRuleUnaryTest = new UnaryTests();
                        decisionRuleUnaryTest.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
                        decisionRule.getInputEntry().add(decisionRuleUnaryTest);
                        decisionRuleUnaryTest.setParent(decisionRule);
                    });

                    inputClause.setParent(dtable);
                    literalExpression.setParent(inputClause);
                });
    }

    private void addInputClauseRequirement(final QName typeRef,
                                           final Definitions definitions,
                                           final List<InputClauseRequirement> inputClauseRequirements,
                                           final String text) {
        //TypeRef matches a BuiltInType
        for (BuiltInType bi : BuiltInType.values()) {
            for (String biName : bi.getNames()) {
                if (Objects.equals(biName, typeRef.getLocalPart())) {
                    inputClauseRequirements.add(new InputClauseRequirement(text, typeRef));
                    return;
                }
            }
        }

        //Otherwise lookup and expand ItemDefinition from the QName's LocalPart
        definitions.getItemDefinition()
                .stream()
                .filter(itemDef -> itemDef.getName().getValue().equals(typeRef.getLocalPart()))
                .findFirst()
                .ifPresent(itemDefinition -> addInputClauseRequirement(itemDefinition, inputClauseRequirements, text));
    }

    private void addInputClauseRequirement(final ItemDefinition itemDefinition,
                                           final List<InputClauseRequirement> inputClauseRequirements,
                                           final String text) {
        if (itemDefinition.getItemComponent().size() == 0) {
            inputClauseRequirements.add(new InputClauseRequirement(text,
                                                                   itemDefinition.getTypeRef()));
        } else {
            itemDefinition.getItemComponent()
                    .forEach(itemComponent -> addInputClauseRequirement(itemComponent,
                                                                        inputClauseRequirements,
                                                                        text + "." + itemComponent.getName().getValue()));
        }
    }

    void enrichOutputClauses(final DecisionTable dtable) {
        if (dtable.getParent() instanceof ContextEntry) {
            final ContextEntry contextEntry = (ContextEntry) dtable.getParent();
            dtable.getOutput().clear();
            dtable.getRule().stream().forEach(decisionRule -> decisionRule.getOutputEntry().clear());

            final OutputClause outputClause = new OutputClause();
            outputClause.setName(contextEntry.getVariable().getName().getValue());
            dtable.getOutput().add(outputClause);

            dtable.getRule().stream().forEach(decisionRule -> {
                final LiteralExpression decisionRuleLiteralExpression = new LiteralExpression();
                decisionRuleLiteralExpression.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
                decisionRule.getOutputEntry().add(decisionRuleLiteralExpression);
                decisionRuleLiteralExpression.setParent(decisionRule);
            });

            outputClause.setParent(dtable);
        }
    }
}
