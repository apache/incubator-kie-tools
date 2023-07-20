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
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.IsUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Annotation;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Cell;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ClauseUnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Column;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextEntryProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableRule;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.EntryInfo;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FeelFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InputClauseProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.OutputClauseProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Row;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RuleEntry;

import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;

public class ExpressionPropsFiller {

    public static ExpressionProps buildAndFillJsInteropProp(final Expression wrappedExpression, final String expressionName, final String dataType, final String expressionId) {
        if (wrappedExpression instanceof IsLiteralExpression) {
            final LiteralExpression literalExpression = (LiteralExpression) wrappedExpression;
            final Double width = literalExpression.getComponentWidths().get(0);
            return new LiteralProps(expressionId, expressionName, dataType, literalExpression.getText().getValue(), literalExpression.getDescription().getValue(), literalExpression.getExpressionLanguage().getValue(), width);
        } else if (wrappedExpression instanceof Context) {
            final Context contextExpression = (Context) wrappedExpression;
            final Double entryInfoWidth = contextExpression.getComponentWidths().get(1);
            final Double entryExpressionWidth = contextExpression.getComponentWidths().get(2);
            return new ContextProps(expressionId, expressionName, dataType, contextEntriesConvertForContextProps(contextExpression), contextResultConvertForContextProps(contextExpression), entryInfoWidth, entryExpressionWidth);
        } else if (wrappedExpression instanceof Relation) {
            final Relation relationExpression = (Relation) wrappedExpression;
            return new RelationProps(expressionId, expressionName, dataType, columnsConvertForRelationProps(relationExpression), rowsConvertForRelationProps(relationExpression));
        } else if (wrappedExpression instanceof List) {
            final List listExpression = (List) wrappedExpression;
            return new ListProps(expressionId, expressionName, dataType, itemsConvertForListProps(listExpression));
        } else if (wrappedExpression instanceof Invocation) {
            final Invocation invocationExpression = (Invocation) wrappedExpression;
            final Double entryInfoWidth = invocationExpression.getComponentWidths().get(1);
            final Double entryExpressionWidth = invocationExpression.getComponentWidths().get(2);
            return new InvocationProps(expressionId, expressionName, dataType, createInvocationFunctionProps(invocationExpression), bindingsConvertForInvocationProps(invocationExpression), entryInfoWidth, entryExpressionWidth);
        } else if (wrappedExpression instanceof FunctionDefinition) {
            final FunctionDefinition functionExpression = (FunctionDefinition) wrappedExpression;
            final EntryInfo[] formalParameters = formalParametersConvertForFunctionProps(functionExpression);
            final Double parametersWidth = functionExpression.getComponentWidths().get(1);
            return specificFunctionPropsBasedOnFunctionKind(expressionId, expressionName, dataType, functionExpression, formalParameters, parametersWidth);
        } else if (wrappedExpression instanceof DecisionTable) {
            final DecisionTable decisionTableExpression = (DecisionTable) wrappedExpression;
            final String hitPolicy = decisionTableExpression.getHitPolicy() != null ? decisionTableExpression.getHitPolicy().value() : null;
            final String aggregation = decisionTableExpression.getAggregation() != null ? decisionTableExpression.getAggregation().getCode() : "";
            return new DecisionTableProps(expressionId, expressionName, dataType, hitPolicy, aggregation,
                                          annotationsConvertForDecisionTableProps(decisionTableExpression),
                                          inputConvertForDecisionTableProps(decisionTableExpression),
                                          outputConvertForDecisionTableProps(decisionTableExpression, expressionName, dataType),
                                          rulesConvertForDecisionTableProps(decisionTableExpression));
        }
        return new ExpressionProps(expressionId, expressionName, dataType, UNDEFINED.getText());
    }

    public static ExpressionProps buildAndFillJsInteropProp(final Expression wrappedExpression, final String expressionName, final String dataType) {
        final String expressionId = Optional.ofNullable(wrappedExpression).map(DMNElement::getId).orElse(new Id()).getValue();
        return buildAndFillJsInteropProp(wrappedExpression, expressionName, dataType, expressionId);
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
                    return new Column(informationItem.getId().getValue(), informationItem.getName().getValue(), informationItem.getTypeRef().getLocalPart(), informationItem.getDescription().getValue(), columnWidth);
                })
                .toArray(Column[]::new);
    }

    private static Row[] rowsConvertForRelationProps(final Relation relationExpression) {
        return relationExpression
                .getRow()
                .stream()
                .map(list -> {
                         final Cell[] cells = list
                                 .getExpression()
                                 .stream()
                                 .map(wrappedLiteralExpression -> new Cell(wrappedLiteralExpression.getExpression().getId().getValue(),
                                                                           ((LiteralExpression) wrappedLiteralExpression.getExpression()).getText().getValue(),
                                                                           wrappedLiteralExpression.getExpression().getDescription().getValue(),
                                                                           ((LiteralExpression) wrappedLiteralExpression.getExpression()).getExpressionLanguage().getValue())
                                 )
                                 .toArray(Cell[]::new);
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

    private static InvocationFunctionProps createInvocationFunctionProps(final Invocation invocationExpression) {
        String functionId = Optional.ofNullable(invocationExpression.getExpression()).map(DMNElement::getId).orElse(new Id()).getValue();
        String functionName = ((LiteralExpression) Optional.ofNullable(invocationExpression.getExpression()).orElse(new LiteralExpression())).getText().getValue();

        return new InvocationFunctionProps(functionId, functionName);
    }

    private static ContextEntryProps fromModelToPropsContextEntryMapper(final InformationItem contextEntryVariable, final Expression expression) {
        final String entryId = contextEntryVariable.getId().getValue();
        final String entryName = contextEntryVariable.getName().getValue();
        final String entryDataType = contextEntryVariable.getTypeRef().getLocalPart();
        final String description = contextEntryVariable.getDescription().getValue();
        final EntryInfo entryInfo = new EntryInfo(entryId, entryName, entryDataType, description);
        final ExpressionProps entryExpression = buildAndFillJsInteropProp(expression, entryName, entryDataType);
        return new ContextEntryProps(entryInfo, entryExpression);
    }

    private static EntryInfo[] formalParametersConvertForFunctionProps(final FunctionDefinition functionExpression) {
        return functionExpression
                .getFormalParameter()
                .stream()
                .map(parameter -> new EntryInfo(parameter.getId().getValue(), parameter.getName().getValue(), parameter.getTypeRefHolder().getValue().getLocalPart(),  parameter.getDescription().getValue()))
                .toArray(EntryInfo[]::new);
    }

    private static FunctionProps specificFunctionPropsBasedOnFunctionKind(final String expressionId, final String expressionName, final String dataType, final FunctionDefinition functionExpression, final EntryInfo[] formalParameters, final Double parametersWidth) {
        switch (functionExpression.getKind()) {
            case JAVA:
                final LiteralExpression classLiteralExpression = getWrappedEntryLiteralExpression(functionExpression.getExpression(), 0);
                final LiteralExpression methodLiteralExpression = getWrappedEntryLiteralExpression(functionExpression.getExpression(), 1);
                final String className = getWrappedEntryText(classLiteralExpression);
                final String methodName = getWrappedEntryText(methodLiteralExpression);
                final String classFieldId = classLiteralExpression.getId().getValue();
                final String methodFieldId = methodLiteralExpression.getId().getValue();
                return new JavaFunctionProps(expressionId, expressionName, dataType, formalParameters, parametersWidth, className, methodName, classFieldId, methodFieldId);
            case PMML:
                final LiteralExpressionPMMLDocument documentPmmlLiteralExpression = getWrappedPmmlDocumentLiteralExpression(functionExpression.getExpression());
                final LiteralExpressionPMMLDocumentModel modelPmmlLiteralExpression = getWrappedPmmlModelLiteralExpression(functionExpression.getExpression());
                final String document = getWrappedEntryText(documentPmmlLiteralExpression);
                final String model = getWrappedEntryText(modelPmmlLiteralExpression);
                final String documentFieldId = documentPmmlLiteralExpression.getId().getValue();
                final String modelFieldId = modelPmmlLiteralExpression.getId().getValue();
                return new PmmlFunctionProps(expressionId, expressionName, dataType, formalParameters, parametersWidth, document, model, documentFieldId, modelFieldId);
            default:
            case FEEL:
                return new FeelFunctionProps(expressionId, expressionName, dataType, formalParameters, parametersWidth,
                                             buildAndFillJsInteropProp(functionExpression.getExpression(), "Feel Expression", UNDEFINED.getText()));
        }
    }

    private static LiteralExpression getWrappedEntryLiteralExpression(final Expression wrappedExpression, final int index) {
        final Context wrappedContext = (Context) (Optional.ofNullable(wrappedExpression).orElse(new Context()));
        LiteralExpression entryExpression = new LiteralExpression();
        if (wrappedContext.getContextEntry().size() > index && wrappedContext.getContextEntry().get(index).getExpression() instanceof LiteralExpression) {
            entryExpression = (LiteralExpression) wrappedContext.getContextEntry().get(index).getExpression();
        }
        return entryExpression;
    }

    private static String getWrappedEntryText(final LiteralExpression entryExpression) {
        String wrappedTextValue = "";
        if (entryExpression.getText() != null && entryExpression.getText().getValue() != null) {
            wrappedTextValue = entryExpression.getText().getValue();
        }
        return wrappedTextValue;
    }

    private static LiteralExpressionPMMLDocument getWrappedPmmlDocumentLiteralExpression(final Expression wrappedExpression) {
        final Context wrappedContext = (Context) (Optional.ofNullable(wrappedExpression).orElse(new Context()));
        if (!wrappedContext.getContextEntry().isEmpty() && wrappedContext.getContextEntry().get(0).getExpression() instanceof LiteralExpressionPMMLDocument) {
            return (LiteralExpressionPMMLDocument) wrappedContext.getContextEntry().get(0).getExpression();
        }
        return new LiteralExpressionPMMLDocument();
    }

    private static LiteralExpressionPMMLDocumentModel getWrappedPmmlModelLiteralExpression(final Expression wrappedExpression) {
        final Context wrappedContext = (Context) (Optional.ofNullable(wrappedExpression).orElse(new Context()));
        if (!wrappedContext.getContextEntry().isEmpty() && wrappedContext.getContextEntry().get(1).getExpression() instanceof LiteralExpressionPMMLDocumentModel) {
            return (LiteralExpressionPMMLDocumentModel) wrappedContext.getContextEntry().get(1).getExpression();
        }
        return new LiteralExpressionPMMLDocumentModel();
    }

    private static DecisionTableRule[] rulesConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return decisionTableExpression
                .getRule()
                .stream()
                .map(rule -> new DecisionTableRule(
                        rule.getId().getValue(),
                        rule.getInputEntry().stream().map(inputEntry -> new RuleEntry(inputEntry.getDomainObjectUUID(),
                                                                                      inputEntry.getText().getValue(),
                                                                                      inputEntry.getDescription().getValue(),
                                                                                      inputEntry.getExpressionLanguage().getValue())).toArray(RuleEntry[]::new),
                        rule.getOutputEntry().stream().map(outputEntry -> new RuleEntry(outputEntry.getDomainObjectUUID(),
                                                                                        outputEntry.getText().getValue(),
                                                                                        outputEntry.getDescription().getValue(),
                                                                                        outputEntry.getExpressionLanguage().getValue())).toArray(RuleEntry[]::new),
                        rule.getAnnotationEntry().stream().map(annotationClauseText -> annotationClauseText.getText().getValue()).toArray(String[]::new)))
                .toArray(DecisionTableRule[]::new);
    }

    private static InputClauseProps[] inputConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return IntStream.range(0, decisionTableExpression.getInput().size())
                .mapToObj(index -> {
                    final InputClause inputClause = decisionTableExpression.getInput().get(index);
                    final String id = inputClause.getId().getValue();
                    final String idLiteralExpression = inputClause.getInputExpression().getId().getValue();
                    final String name = inputClause.getInputExpression().getText().getValue();
                    final String dataType = inputClause.getInputExpression().getTypeRefHolder().getValue().getLocalPart();
                    final Double width = decisionTableExpression.getComponentWidths().get(index + 1);
                    return new InputClauseProps(id, name, dataType, width, convertToClauseUnaryTests(inputClause.getInputValues()), idLiteralExpression, inputClause.getDescription().getValue());
                })
                .toArray(InputClauseProps[]::new);
    }

    private static OutputClauseProps[] outputConvertForDecisionTableProps(final DecisionTable decisionTableExpression, final String expressionName, final String expressionDataType) {
        return IntStream.range(0, decisionTableExpression.getOutput().size())
                .mapToObj(index -> {
                    final OutputClause outputClause = decisionTableExpression.getOutput().get(index);
                    final String id = outputClause.getId().getValue();
                    final String name = outputClause.getName();
                    final String dataType = outputClause.getTypeRef().getLocalPart();
                    final String description = outputClause.getDescription().getValue();
                    final ExpressionProps defaultOutputValue =
                            (outputClause.getDefaultOutputEntry() != null && outputClause.getDefaultOutputEntry().getText() != null) ?
                                    new LiteralProps(outputClause.getDefaultOutputEntry().getDomainObjectUUID(),
                                                     null,
                                                     UNDEFINED.getText(),
                                                     outputClause.getDefaultOutputEntry().getText().getValue(),
                                                     outputClause.getDefaultOutputEntry().getDescription().getValue(),
                                                     null,
                                                     null) :
                                    null;
                    final Double width = decisionTableExpression.getComponentWidths().get(decisionTableExpression.getInput().size() + index + 1);
                    // When output clause is empty, then we should use expression name and dataType for it
                    if (name == null) {
                        return new OutputClauseProps(id, expressionName, expressionDataType, width, convertToClauseUnaryTests(outputClause.getOutputValues()), defaultOutputValue, description);
                    }
                    return new OutputClauseProps(id, name, dataType, width, convertToClauseUnaryTests(outputClause.getOutputValues()), defaultOutputValue, description);
                })
                .toArray(OutputClauseProps[]::new);
    }

    public static ClauseUnaryTests convertToClauseUnaryTests(IsUnaryTests isUnaryTests) {
        if (isUnaryTests != null) {
            return new ClauseUnaryTests(isUnaryTests.getId().getValue(),
                                        isUnaryTests.getText().getValue(),
                                        isUnaryTests.getConstraintType().value());
        } else {
            return null;
        }
    }

    private static Annotation[] annotationsConvertForDecisionTableProps(final DecisionTable decisionTableExpression) {
        return IntStream.range(0, decisionTableExpression.getAnnotations().size())
                .mapToObj(index -> {
                    final RuleAnnotationClause ruleAnnotationClause = decisionTableExpression.getAnnotations().get(index);
                    final Double width = decisionTableExpression.getComponentWidths()
                            .get(decisionTableExpression.getInput().size() + decisionTableExpression.getOutput().size() + index + 1);
                    final String annotationName = ruleAnnotationClause.getName().getValue();
                    return new Annotation(annotationName, width);
                })
                .toArray(Annotation[]::new);
    }
}
