/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.util;

import java.util.Optional;
import java.util.stream.IntStream;

import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Annotation;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Clause;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FeelFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Row;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;

public class ExpressionPropsFiller {

    public static ExpressionProps buildAndFillJsInteropProp(final Expression wrappedExpression, final String expressionName, final String dataType) {
        if (wrappedExpression instanceof IsLiteralExpression) {
            final LiteralExpression literalExpression = (LiteralExpression) wrappedExpression;
            final Double width = literalExpression.getComponentWidths().get(0);
            return new LiteralProps(expressionName, dataType, literalExpression.getText().getValue(), width);
        } else if (wrappedExpression instanceof Context) {
            final Context contextExpression = (Context) wrappedExpression;
            final Double entryInfoWidth = contextExpression.getComponentWidths().get(1);
            final Double entryExpressionWidth = contextExpression.getComponentWidths().get(2);
            return new ContextProps(expressionName, dataType, contextEntriesConvertForContextProps(contextExpression), contextResultConvertForContextProps(contextExpression), entryInfoWidth, entryExpressionWidth);
        } else if (wrappedExpression instanceof Relation) {
            final Relation relationExpression = (Relation) wrappedExpression;
            return new RelationProps(expressionName, dataType, columnsConvertForRelationProps(relationExpression), rowsConvertForRelationProps(relationExpression));
        } else if (wrappedExpression instanceof List) {
            final List listExpression = (List) wrappedExpression;
            final Double width = listExpression.getComponentWidths().get(1);
            return new ListProps(expressionName, dataType, itemsConvertForListProps(listExpression), width);
        } else if (wrappedExpression instanceof Invocation) {
            final Invocation invocationExpression = (Invocation) wrappedExpression;
            final String invokedFunction = ((LiteralExpression) Optional.ofNullable(invocationExpression.getExpression()).orElse(new LiteralExpression())).getText().getValue();
            final Double entryInfoWidth = invocationExpression.getComponentWidths().get(1);
            final Double entryExpressionWidth = invocationExpression.getComponentWidths().get(2);
            return new InvocationProps(expressionName, dataType, invokedFunction, bindingsConvertForInvocationProps(invocationExpression), entryInfoWidth, entryExpressionWidth);
        } else if (wrappedExpression instanceof FunctionDefinition) {
            final FunctionDefinition functionExpression = (FunctionDefinition) wrappedExpression;
            final EntryInfo[] formalParameters = formalParametersConvertForFunctionProps(functionExpression);
            final Double parametersWidth = functionExpression.getComponentWidths().get(1);
            return specificFunctionPropsBasedOnFunctionKind(expressionName, dataType, functionExpression, formalParameters, parametersWidth);
        } else if (wrappedExpression instanceof DecisionTable) {
            final DecisionTable decisionTableExpression = (DecisionTable) wrappedExpression;
            final String hitPolicy = decisionTableExpression.getHitPolicy() != null ? decisionTableExpression.getHitPolicy().value() : null;
            final String aggregation = decisionTableExpression.getAggregation() != null ? decisionTableExpression.getAggregation().getCode() : "";
            return new DecisionTableProps(expressionName, dataType, hitPolicy, aggregation,
                                          annotationsConvertForDecisionTableProps(decisionTableExpression),
                                          inputConvertForDecisionTableProps(decisionTableExpression),
                                          outputConvertForDecisionTableProps(decisionTableExpression, expressionName, dataType),
                                          rulesConvertForDecisionTableProps(decisionTableExpression));
        }
        return new ExpressionProps(expressionName, dataType, null);
    }

    private static ExpressionProps contextResultConvertForContextProps(final Context contextExpression) {
        final ContextEntry resultContextEntry = !contextExpression.getContextEntry().isEmpty() ?
                contextExpression.getContextEntry().get(contextExpression.getContextEntry().size() - 1) :
                new ContextEntry();
        return buildAndFillJsInteropProp(resultContextEntry.getExpression(), "Result Expression", UNDEFINED.getText());
    }

    private static ContextEntryProps[] contextEntriesConvertForContextProps(final Context contextExpression) {
        return contextExpression.getContextEntry()
                .stream()
                .limit(contextExpression.getContextEntry().size() - 1L)
                .map(contextEntry -> fromModelToPropsContextEntryMapper(contextEntry.getVariable(), contextEntry.getExpression()))
                .toArray(ContextEntryProps[]::new);
    }

    private static Column[] columnsConvertForRelationProps(final Relation relationExpression) {
        return IntStream.range(0, relationExpression.getColumn().size())
                .mapToObj(index -> {
                    final InformationItem informationItem = relationExpression.getColumn().get(index);
                    final Double columnWidth = relationExpression.getComponentWidths().get(index + 1);
                    return new Column(informationItem.getId().getValue(), informationItem.getName().getValue(), informationItem.getTypeRef().getLocalPart(), columnWidth);
                })
                .toArray(Column[]::new);
    }

    private static Row[] rowsConvertForRelationProps(final Relation relationExpression) {
        return relationExpression
                .getRow()
                .stream()
                .map(list -> {
                         final String[] cells = list
                                 .getExpression()
                                 .stream()
                                 .map(wrappedLiteralExpression -> ((LiteralExpression) wrappedLiteralExpression.getExpression()).getText().getValue()).toArray(String[]::new);
                         return new Row(list.getId().getValue(), cells);
                     }
                )
                .toArray(Row[]::new);
    }

    private static ExpressionProps[] itemsConvertForListProps(final List listExpression) {
        return listExpression
                .getExpression()
                .stream()
                .map(expression -> buildAndFillJsInteropProp(expression.getExpression(), "List item", UNDEFINED.getText()))
                .toArray(ExpressionProps[]::new);
    }

    private static ContextEntryProps[] bindingsConvertForInvocationProps(final Invocation invocationExpression) {
        return invocationExpression
                .getBinding()
                .stream()
                .map(invocation -> fromModelToPropsContextEntryMapper(invocation.getVariable(), invocation.getExpression()))
                .toArray(ContextEntryProps[]::new);
    }

    private static ContextEntryProps fromModelToPropsContextEntryMapper(final InformationItem contextEntryVariable, final Expression expression) {
        final String entryName = contextEntryVariable.getName().getValue();
        final String entryDataType = contextEntryVariable.getTypeRef().getLocalPart();
        final EntryInfo entryInfo = new EntryInfo(entryName, entryDataType);
        final ExpressionProps entryExpression = buildAndFillJsInteropProp(expression, entryName, entryDataType);
        return new ContextEntryProps(entryInfo, entryExpression);
    }

    private static EntryInfo[] formalParametersConvertForFunctionProps(final FunctionDefinition functionExpression) {
        return functionExpression
                .getFormalParameter()
                .stream()
                .map(parameter -> new EntryInfo(parameter.getName().getValue(), parameter.getTypeRefHolder().getValue().getLocalPart()))
                .toArray(EntryInfo[]::new);
    }

    private static FunctionProps specificFunctionPropsBasedOnFunctionKind(final String expressionName, final String dataType, final FunctionDefinition functionExpression, final EntryInfo[] formalParameters, final Double parametersWidth) {
        switch (functionExpression.getKind()) {
            case JAVA:
                final String classNameExpression = getEntryAt(functionExpression.getExpression(), 0);
                final String methodNameExpression = getEntryAt(functionExpression.getExpression(), 1);
                return new JavaFunctionProps(expressionName, dataType, formalParameters, parametersWidth, classNameExpression, methodNameExpression);
            case PMML:
                final String documentExpression = getEntryAt(functionExpression.getExpression(), 0);
                final String modelExpression = getEntryAt(functionExpression.getExpression(), 1);
                return new PmmlFunctionProps(expressionName, dataType, formalParameters, parametersWidth, documentExpression, modelExpression);
            default:
            case FEEL:
                return new FeelFunctionProps(expressionName, dataType, formalParameters, parametersWidth,
                                             buildAndFillJsInteropProp(functionExpression.getExpression(), "Feel Expression", UNDEFINED.getText()));
        }
    }

    private static String getEntryAt(final Expression wrappedExpression, final int index) {
        final Context wrappedContext = (Context) (Optional.ofNullable(wrappedExpression).orElse(new Context()));
        LiteralExpression entryExpression = new LiteralExpression();
        String wrappedTextValue = "";
        if (wrappedContext.getContextEntry().size() > index && wrappedContext.getContextEntry().get(index).getExpression() instanceof LiteralExpression) {
            entryExpression = (LiteralExpression) wrappedContext.getContextEntry().get(index).getExpression();
        }
        if (entryExpression.getText() != null && entryExpression.getText().getValue() != null) {
            wrappedTextValue = entryExpression.getText().getValue();
        }
        return wrappedTextValue;
    }

    private static DecisionTableRule[] rulesConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return decisionTableExpression
                .getRule()
                .stream()
                .map(rule -> new DecisionTableRule(
                        rule.getId().getValue(),
                        rule.getInputEntry().stream().map(inputEntry -> inputEntry.getText().getValue()).toArray(String[]::new),
                        rule.getOutputEntry().stream().map(outputEntry -> outputEntry.getText().getValue()).toArray(String[]::new),
                        rule.getAnnotationEntry().stream().map(annotationClauseText -> annotationClauseText.getText().getValue()).toArray(String[]::new)))
                .toArray(DecisionTableRule[]::new);
    }

    private static Clause[] inputConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return IntStream.range(0, decisionTableExpression.getInput().size())
                .mapToObj(index -> {
                    final InputClause inputClause = decisionTableExpression.getInput().get(index);
                    final String id = inputClause.getId().getValue();
                    final String name = inputClause.getInputExpression().getText().getValue();
                    final String dataType = inputClause.getInputExpression().getTypeRefHolder().getValue().getLocalPart();
                    final Double width = decisionTableExpression.getComponentWidths().get(index + 1);
                    return new Clause(id, name, dataType, width);
                })
                .toArray(Clause[]::new);
    }

    private static Clause[] outputConvertForDecisionTableProps(final DecisionTable decisionTableExpression, final String expressionName, final String expressionDataType) {
        return IntStream.range(0, decisionTableExpression.getOutput().size())
                .mapToObj(index -> {
                    final OutputClause outputClause = decisionTableExpression.getOutput().get(index);
                    final String id = outputClause.getId().getValue();
                    final String name = outputClause.getName();
                    final String dataType = outputClause.getTypeRef().getLocalPart();
                    final Double width = decisionTableExpression.getComponentWidths().get(decisionTableExpression.getInput().size() + index + 1);
                    // When output clause is empty, then we should use expression name and dataType for it
                    if (name == null) {
                        return new Clause(id, expressionName, expressionDataType, width);
                    }
                    return new Clause(id, name, dataType, width);
                })
                .toArray(Clause[]::new);
    }

    private static Annotation[] annotationsConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return IntStream.range(0, decisionTableExpression.getAnnotations().size())
                .mapToObj(index -> {
                    final RuleAnnotationClause ruleAnnotationClause = decisionTableExpression.getAnnotations().get(index);
                    final Double width = decisionTableExpression.getComponentWidths()
                            .get(decisionTableExpression.getInput().size() + decisionTableExpression.getOutput().size() + index + 1);
                    final String annotationId = Optional.ofNullable(ruleAnnotationClause.getId()).orElse(new Id()).getValue();
                    final String annotationName = ruleAnnotationClause.getName().getValue();
                    return new Annotation(annotationId, annotationName, width);
                })
                .toArray(Annotation[]::new);
    }
}
