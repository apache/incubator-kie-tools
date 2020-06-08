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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.HasTypeRef;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTableOrientation;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorModelEnricher;
import org.kie.workbench.common.dmn.client.editors.expressions.util.TypeRefUtils;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

import static org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils.isBuiltInType;
import static org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.ANY;

@ApplicationScoped
public class DecisionTableEditorDefinitionEnricher implements ExpressionEditorModelEnricher<DecisionTable> {

    private static final String DOT_CHAR = ".";
    private SessionManager sessionManager;
    private DMNGraphUtils dmnGraphUtils;
    private ItemDefinitionUtils itemDefinitionUtils;

    static class ClauseRequirement {

        String text;
        QName typeRef;

        ClauseRequirement(final String text,
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
                                                 final DMNGraphUtils dmnGraphUtils,
                                                 final ItemDefinitionUtils itemDefinitionUtils) {
        this.sessionManager = sessionManager;
        this.dmnGraphUtils = dmnGraphUtils;
        this.itemDefinitionUtils = itemDefinitionUtils;
    }

    @Override
    public void enrich(final Optional<String> nodeUUID,
                       final HasExpression hasExpression,
                       final Optional<DecisionTable> expression) {
        expression.ifPresent(dtable -> {
            dtable.setHitPolicy(HitPolicy.UNIQUE);
            dtable.setPreferredOrientation(DecisionTableOrientation.RULE_AS_ROW);

            final InputClause inputClause = new InputClause();
            final InputClauseLiteralExpression literalExpression = new InputClauseLiteralExpression();
            literalExpression.getText().setValue(DecisionTableDefaultValueUtilities.getNewInputClauseName(dtable));
            inputClause.setInputExpression(literalExpression);
            dtable.getInput().add(inputClause);

            final RuleAnnotationClause ruleAnnotationClause = new RuleAnnotationClause();
            ruleAnnotationClause.getName().setValue(DecisionTableDefaultValueUtilities.getNewRuleAnnotationClauseName(dtable));
            dtable.getAnnotations().add(ruleAnnotationClause);

            final DecisionRule decisionRule = new DecisionRule();
            final UnaryTests decisionRuleUnaryTest = new UnaryTests();
            decisionRuleUnaryTest.getText().setValue(DecisionTableDefaultValueUtilities.INPUT_CLAUSE_UNARY_TEST_TEXT);
            decisionRule.getInputEntry().add(decisionRuleUnaryTest);

            buildOutputClausesByDataType(hasExpression, dtable, decisionRule);

            final RuleAnnotationClauseText ruleAnnotationEntry = new RuleAnnotationClauseText();
            ruleAnnotationEntry.getText().setValue(DecisionTableDefaultValueUtilities.RULE_DESCRIPTION);
            decisionRule.getAnnotationEntry().add(ruleAnnotationEntry);

            dtable.getRule().add(decisionRule);

            //Setup parent relationships
            inputClause.setParent(dtable);
            decisionRule.setParent(dtable);
            literalExpression.setParent(inputClause);
            decisionRuleUnaryTest.setParent(decisionRule);
            ruleAnnotationEntry.setParent(dtable);

            if (nodeUUID.isPresent()) {
                enrichInputClauses(nodeUUID.get(), dtable);
            } else {
                enrichOutputClauses(dtable);
            }
        });
    }

    void buildOutputClausesByDataType(final HasExpression hasExpression, final DecisionTable dTable, final DecisionRule decisionRule) {
        final HasTypeRef hasTypeRef = getHasTypeRef(hasExpression, dTable);
        final QName typeRef = !Objects.isNull(hasTypeRef) ? hasTypeRef.getTypeRef() : BuiltInType.UNDEFINED.asQName();
        final String name = DecisionTableDefaultValueUtilities.getNewOutputClauseName(dTable);

        final List<ClauseRequirement> outputClausesRequirement = generateOutputClauseRequirements(dmnGraphUtils.getDefinitions(), typeRef, name);

        if (outputClausesRequirement.isEmpty()) {
            dTable.getOutput().add(
                    buildOutputClause(dTable, typeRef, name)
            );
            populateOutputEntries(decisionRule);
        } else {
            outputClausesRequirement
                    .stream()
                    .sorted(Comparator.comparing(outputClauseRequirement -> outputClauseRequirement.text))
                    .map(outputClauseRequirement -> buildOutputClause(dTable, outputClauseRequirement.typeRef, outputClauseRequirement.text))
                    .forEach(outputClause -> {
                        dTable.getOutput().add(outputClause);
                        populateOutputEntries(decisionRule);
                    });
        }
    }

    private HasTypeRef getHasTypeRef(final HasExpression hasExpression, final DecisionTable dTable) {
        if (hasExpression instanceof FunctionDefinition) {
            final DMNModelInstrumentedBase parent = hasExpression.asDMNModelInstrumentedBase().getParent();
            if (parent instanceof HasVariable) {
                return ((HasVariable) parent).getVariable();
            }
        }
        return TypeRefUtils.getTypeRefOfExpression(dTable, hasExpression);
    }

    private List<ClauseRequirement> generateOutputClauseRequirements(final Definitions definitions, final QName typeRef, final String name) {
        if (isBuiltInType(typeRef.getLocalPart())) {
            return Collections.singletonList(new ClauseRequirement(name, typeRef));
        }

        return definitions.getItemDefinition()
                .stream()
                .filter(typeRefIsCustom(typeRef))
                .findFirst()
                .map(this::generateOutputClauseRequirementsForFirstLevel)
                .orElse(Collections.emptyList());
    }

    private List<ClauseRequirement> generateOutputClauseRequirementsForFirstLevel(final ItemDefinition itemDefinition) {
        return itemDefinition.getItemComponent()
                .stream()
                .map(this::definitionToClauseRequirementMapper)
                .collect(Collectors.toList());
    }

    private ClauseRequirement definitionToClauseRequirementMapper(final ItemDefinition itemDefinition) {
        final QName typeRef = itemDefinition.getTypeRef();
        final String name = computeClauseName(itemDefinition);

        if (Objects.isNull(typeRef) || typeRefDoesNotMatchAnyDefinition(typeRef)) {
            return new ClauseRequirement(name, ANY.asQName());
        }
        return new ClauseRequirement(name, typeRef);
    }

    private boolean typeRefDoesNotMatchAnyDefinition(final QName typeRef) {
        return !isBuiltInType(typeRef.getLocalPart()) &&
                dmnGraphUtils.getDefinitions().getItemDefinition()
                        .stream()
                        .noneMatch(typeRefIsCustom(typeRef));
    }

    private OutputClause buildOutputClause(final DecisionTable dtable, final QName typeRef, final String text) {
        final OutputClause outputClause = new OutputClause();
        outputClause.setName(text);
        outputClause.setTypeRef(typeRef);
        outputClause.setParent(dtable);
        return outputClause;
    }

    private void populateOutputEntries(final DecisionRule decisionRule) {
        final LiteralExpression decisionRuleLiteralExpression = new LiteralExpression();
        decisionRuleLiteralExpression.getText().setValue(DecisionTableDefaultValueUtilities.OUTPUT_CLAUSE_EXPRESSION_TEXT);
        decisionRuleLiteralExpression.setParent(decisionRule);
        decisionRule.getOutputEntry().add(decisionRuleLiteralExpression);
    }

    @SuppressWarnings("unchecked")
    void enrichInputClauses(final String uuid,
                            final DecisionTable dtable) {
        final Graph<?, Node> graph = sessionManager.getCurrentSession().getCanvasHandler().getDiagram().getGraph();
        final Node<?, Edge> node = graph.getNode(uuid);
        if (Objects.isNull(node)) {
            return;
        }

        //Get all Decision nodes feeding into this DecisionTable
        final List<Decision> decisionSet = node.getInEdges().stream()
                .map(Edge::getSourceNode)
                .map(Node::getContent)
                .filter(content -> content instanceof Definition)
                .map(content -> (Definition) content)
                .map(Definition::getDefinition)
                .filter(definition -> definition instanceof Decision)
                .map(definition -> (Decision) definition)
                .collect(Collectors.toList());

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
        if (decisionSet.isEmpty() && inputDataSet.isEmpty()) {
            return;
        }

        //Extract individual components of InputData TypeRefs
        final Definitions definitions = dmnGraphUtils.getDefinitions();
        final List<ClauseRequirement> inputClauseRequirements = new ArrayList<>();
        decisionSet.forEach(decision -> addInputClauseRequirement(decision.getVariable().getTypeRef(),
                                                                  definitions,
                                                                  inputClauseRequirements,
                                                                  decision.getName().getValue()));
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
                    final InputClauseLiteralExpression literalExpression = new InputClauseLiteralExpression();
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
                                           final List<ClauseRequirement> inputClauseRequirements,
                                           final String text) {
        //TypeRef matches a BuiltInType
        if (isBuiltInType(typeRef.getLocalPart())) {
            inputClauseRequirements.add(new ClauseRequirement(text, typeRef));
            return;
        }

        //Otherwise lookup and expand ItemDefinition from the QName's LocalPart
        definitions.getItemDefinition()
                .stream()
                .filter(typeRefIsCustom(typeRef))
                .findFirst()
                .ifPresent(itemDefinition -> addInputClauseRequirement(itemDefinition, inputClauseRequirements, text));
    }

    void addInputClauseRequirement(final ItemDefinition itemDefinition,
                                   final List<ClauseRequirement> inputClauseRequirements,
                                   final String text) {
        if (itemDefinition.getItemComponent().size() == 0) {
            inputClauseRequirements.add(new ClauseRequirement(text,
                                                              getQName(itemDefinition)));
        } else {
            itemDefinition.getItemComponent()
                    .forEach(itemComponent -> addInputClauseRequirement(itemComponent,
                                                                        inputClauseRequirements,
                                                                        text + DOT_CHAR + computeClauseName(itemComponent)));
        }
    }

    String computeClauseName(final ItemDefinition itemComponent) {
        final String originalName = itemComponent.getName().getValue();
        String nameWithoutModelRef = originalName;
        if (itemComponent.isImported()) {
            final int positionOfLastDot = originalName.lastIndexOf(DOT_CHAR) + 1;
            if (positionOfLastDot > 0 && positionOfLastDot != originalName.length()) {
                nameWithoutModelRef = originalName.substring(positionOfLastDot);
            }
        }
        return nameWithoutModelRef;
    }

    private Predicate<ItemDefinition> typeRefIsCustom(final QName typeRef) {
        return itemDef -> Objects.equals(itemDef.getName().getValue(), typeRef.getLocalPart());
    }

    private QName getQName(final ItemDefinition itemDefinition) {
        return Optional
                .ofNullable(itemDefinition.getTypeRef())
                .orElse(getQNameFromItemDefinitionName(itemDefinition));
    }

    private QName getQNameFromItemDefinitionName(final ItemDefinition itemDefinition) {

        final Name name = itemDefinition.getName();
        final QName typeRef = new QName(NULL_NS_URI, name.getValue());

        return itemDefinitionUtils.normaliseTypeRef(typeRef);
    }

    void enrichOutputClauses(final DecisionTable dtable) {
        if (dtable.getParent() instanceof ContextEntry && dtable.getOutput().isEmpty()) {
            final ContextEntry contextEntry = (ContextEntry) dtable.getParent();

            final OutputClause outputClause = new OutputClause();
            outputClause.setName(getOutputClauseName(contextEntry).orElse(DecisionTableDefaultValueUtilities.getNewOutputClauseName(dtable)));
            outputClause.setTypeRef(getOutputClauseTypeRef(contextEntry).orElse(BuiltInType.UNDEFINED.asQName()));
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

    private Optional<String> getOutputClauseName(final HasVariable hasVariable) {
        final IsInformationItem variable = hasVariable.getVariable();
        if (variable instanceof InformationItem) {
            return Optional.ofNullable((variable).getName().getValue());
        }

        final DMNModelInstrumentedBase base = hasVariable.asDMNModelInstrumentedBase().getParent();
        final DMNModelInstrumentedBase parent = base.getParent();
        if (parent instanceof HasName) {
            return Optional.ofNullable(((HasName) parent).getName().getValue());
        }
        if (parent instanceof HasVariable) {
            return getOutputClauseName((HasVariable) parent);
        }
        return Optional.empty();
    }

    private Optional<QName> getOutputClauseTypeRef(final HasVariable hasVariable) {
        final IsInformationItem variable = hasVariable.getVariable();
        if (Objects.nonNull(variable)) {
            return Optional.ofNullable(variable.getTypeRef());
        }

        final DMNModelInstrumentedBase base = hasVariable.asDMNModelInstrumentedBase().getParent();
        final DMNModelInstrumentedBase parent = base.getParent();
        if (parent instanceof HasTypeRef) {
            return Optional.ofNullable(((HasTypeRef) parent).getTypeRef());
        }
        if (parent instanceof HasVariable) {
            return getOutputClauseTypeRef((HasVariable) parent);
        }
        return Optional.empty();
    }
}
