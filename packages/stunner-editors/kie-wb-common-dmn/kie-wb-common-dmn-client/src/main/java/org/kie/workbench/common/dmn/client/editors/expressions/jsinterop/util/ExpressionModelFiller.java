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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.kie.dmn.model.api.FunctionKind;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.Expression;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.editors.types.BuiltInTypeUtils;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.QNameHolder;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Annotation;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Cell;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Clause;
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
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.OutputClauseProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.Row;
import org.kie.workbench.common.dmn.client.editors.expressions.types.context.ContextGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.invocation.InvocationGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.list.ListGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.literal.LiteralExpressionGrid;
import org.kie.workbench.common.dmn.client.editors.expressions.types.undefined.UndefinedExpressionColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridColumn;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT;
import static org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.CONTEXT;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.DECISION_TABLE;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.FUNCTION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.INVOCATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.LIST;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.LITERAL_EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.RELATION;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType.UNDEFINED;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition.VARIABLE_CLASS;
import static org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.java.JavaFunctionEditorDefinition.VARIABLE_METHOD_SIGNATURE;

public class ExpressionModelFiller {

    public static void fillLiteralExpression(final LiteralExpression literalExpression, final LiteralProps literalProps) {
        literalExpression.setId(new Id(literalProps.id));
        literalExpression.getComponentWidths().set(0, literalProps.width);
        literalExpression.setDescription(new Description(literalProps.description));
        literalExpression.setExpressionLanguage(new ExpressionLanguage(literalProps.expressionLanguage));
        literalExpression.setText(new Text(literalProps.content));
    }

    public static void fillContextExpression(final Context contextExpression,
                                             final ContextProps contextProps,
                                             final UnaryOperator<QName> qNameNormalizer) {
        contextExpression.setId(new Id(contextProps.id));
        contextExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        contextExpression.getComponentWidths().set(1, contextProps.entryInfoWidth);
        contextExpression.getComponentWidths().set(2, retrieveNestedExpressionWidth(retrieveContextExpressionWithWidth(contextProps)));
        contextExpression.getContextEntry().clear();
        contextExpression.getContextEntry().addAll(contextEntriesConvertForContextExpression(contextProps, qNameNormalizer));
        contextExpression.getContextEntry().add(entryResultConvertForContextExpression(contextProps, qNameNormalizer));
    }

    private static ExpressionProps retrieveContextExpressionWithWidth(final ContextProps contextProps) {
        final int allContextEntries = contextProps.contextEntries == null ? 0 : contextProps.contextEntries.length;
        return IntStream.range(0, allContextEntries)
                .mapToObj(i -> contextProps.contextEntries[i])
                .filter(cep -> cep.entryExpression.logicType != UNDEFINED.getText())
                .findFirst()
                .map(cep -> cep.entryExpression)
                .orElse(contextProps.result);
    }

    public static void fillRelationExpression(final Relation relationExpression,
                                              final RelationProps relationProps,
                                              final UnaryOperator<QName> qNameNormalizer) {
        relationExpression.setId(new Id(relationProps.id));
        relationExpression.getColumn().clear();
        relationExpression.getColumn().addAll(columnsConvertForRelationExpression(relationProps, qNameNormalizer));
        final int columnsLength = relationProps.columns == null ? 0 : relationProps.columns.length;
        relationExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        IntStream.range(0, columnsLength)
                .forEach(index -> relationExpression.getComponentWidths().set(index + 1, Objects.requireNonNull(relationProps.columns)[index].width));
        relationExpression.getRow().clear();
        relationExpression.getRow().addAll(rowsConvertForRelationExpression(relationProps));
    }

    public static void fillListExpression(final List listExpression,
                                          final ListProps listProps,
                                          final UnaryOperator<QName> qNameNormalizer) {
        listExpression.setId(new Id(listProps.id));
        listExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        listExpression.getComponentWidths().set(1, retrieveNestedExpressionWidth(retrieveListExpressionWithWidth(listProps)));
        listExpression.getExpression().clear();
        listExpression.getExpression().addAll(itemsConvertForListExpression(listProps, listExpression, qNameNormalizer));
    }

    private static ExpressionProps retrieveListExpressionWithWidth(final ListProps listProps) {
        final int allContextEntries = listProps.items == null ? 0 : listProps.items.length;
        return IntStream.range(0, allContextEntries)
                .mapToObj(i -> listProps.items[i])
                .filter(item -> item.logicType != UNDEFINED.getText())
                .findFirst()
                .orElse(listProps.items[0]);
    }

    public static void fillInvocationExpression(final Invocation invocationExpression,
                                                final InvocationProps invocationProps,
                                                final UnaryOperator<QName> qNameNormalizer) {
        final LiteralExpression invokedFunction = new LiteralExpression();
        invocationExpression.setId(new Id(invocationProps.id));
        invocationExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        invocationExpression.getComponentWidths().set(1, invocationProps.entryInfoWidth);
        invocationExpression.getComponentWidths().set(2, retrieveNestedExpressionWidth(retrieveInvocationExpressionWithWidth(invocationProps)));
        invokedFunction.setId(new Id(invocationProps.invokedFunction.id));
        invokedFunction.setText(new Text(invocationProps.invokedFunction.name));
        invocationExpression.setExpression(invokedFunction);
        invocationExpression.getBinding().clear();
        invocationExpression.getBinding().addAll(bindingsConvertForInvocationExpression(invocationProps, qNameNormalizer));
    }

    private static ExpressionProps retrieveInvocationExpressionWithWidth(final InvocationProps invocationProps) {
        final int allContextEntries = invocationProps.bindingEntries == null ? 0 : invocationProps.bindingEntries.length;
        return IntStream.range(0, allContextEntries)
                .mapToObj(i -> invocationProps.bindingEntries[i])
                .filter(iep -> iep.entryExpression.logicType != UNDEFINED.getText())
                .findFirst()
                .map(iep -> iep.entryExpression)
                .orElse(invocationProps.bindingEntries[0].entryExpression);
    }

    public static void fillFunctionExpression(final FunctionDefinition functionExpression,
                                              final FunctionProps functionProps,
                                              final UnaryOperator<QName> qNameNormalizer) {
        final FunctionDefinition.Kind functionKind = FunctionDefinition.Kind.fromValue(functionProps.functionKind);
        functionExpression.setId(new Id(functionProps.id));
        functionExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        functionExpression.getComponentWidths().set(1, retrieveFunctionExpressionWidth(functionProps));
        functionExpression.getFormalParameter().clear();
        functionExpression.getFormalParameter().addAll(formalParametersConvertForFunctionExpression(functionProps, qNameNormalizer));
        functionExpression.setKind(functionKind);
        functionExpression.setExpression(wrappedExpressionBasedOnKind(functionKind, functionProps, qNameNormalizer));
    }

    private static Double retrieveFunctionExpressionWidth(FunctionProps props) {
        Double functionExpressionWidth = null;
        if (props.functionKind.equals(FunctionKind.FEEL.value())) {
            functionExpressionWidth = retrieveNestedExpressionWidth(((FeelFunctionProps) props).expression);
        } else if (props.functionKind.equals(FunctionKind.JAVA.value())) {
            functionExpressionWidth = props.classAndMethodNamesWidth;
        } else if (props.functionKind.equals(FunctionKind.PMML.value())) {
            /* PMML FUnction expressions have FIXED widths - Using defaults with NULL */
            functionExpressionWidth = null;
        }
        return functionExpressionWidth;
    }

    public static void fillDecisionTableExpression(final DecisionTable decisionTableExpression,
                                                   final DecisionTableProps decisionTableProps,
                                                   final UnaryOperator<QName> qNameNormalizer) {
        decisionTableExpression.setId(new Id(decisionTableProps.id));
        if (StringUtils.nonEmpty(decisionTableProps.hitPolicy)) {
            decisionTableExpression.setHitPolicy(HitPolicy.fromValue(decisionTableProps.hitPolicy));
        }
        if (StringUtils.nonEmpty(decisionTableProps.aggregation) && !decisionTableProps.aggregation.equals("?")) {
            decisionTableExpression.setAggregation(BuiltinAggregator.fromCode(decisionTableProps.aggregation));
        } else {
            decisionTableExpression.setAggregation(null);
        }
        decisionTableExpression.getAnnotations().clear();
        decisionTableExpression.getAnnotations().addAll(annotationsConvertForDecisionTableExpression(decisionTableProps));
        decisionTableExpression.getInput().clear();
        decisionTableExpression.getInput().addAll(inputConvertForDecisionTableExpression(decisionTableProps, qNameNormalizer));
        decisionTableExpression.getOutput().clear();
        decisionTableExpression.getOutput().addAll(outputConvertForDecisionTableExpression(decisionTableProps, qNameNormalizer));
        updateComponentWidthsForDecisionTableExpression(decisionTableExpression, decisionTableProps);
        decisionTableExpression.getRule().clear();
        decisionTableExpression.getRule().addAll(rulesConvertForDecisionTableExpression(decisionTableProps));
    }

    private static Expression buildAndFillNestedExpression(final ExpressionProps props,
                                                           final UnaryOperator<QName> qNameNormalizer) {
        if (Objects.equals(LITERAL_EXPRESSION.getText(), props.logicType)) {
            final LiteralExpression literalExpression = new LiteralExpression();
            fillLiteralExpression(literalExpression, (LiteralProps) props);
            return literalExpression;
        } else if (Objects.equals(CONTEXT.getText(), props.logicType)) {
            final Context contextExpression = new Context();
            fillContextExpression(contextExpression, (ContextProps) props, qNameNormalizer);
            return contextExpression;
        } else if (Objects.equals(RELATION.getText(), props.logicType)) {
            final Relation relationExpression = new Relation();
            fillRelationExpression(relationExpression, (RelationProps) props, qNameNormalizer);
            return relationExpression;
        } else if (Objects.equals(LIST.getText(), props.logicType)) {
            final List listExpression = new List();
            fillListExpression(listExpression, (ListProps) props, qNameNormalizer);
            return listExpression;
        } else if (Objects.equals(INVOCATION.getText(), props.logicType)) {
            final Invocation invocationExpression = new Invocation();
            fillInvocationExpression(invocationExpression, (InvocationProps) props, qNameNormalizer);
            return invocationExpression;
        } else if (Objects.equals(FUNCTION.getText(), props.logicType)) {
            final FunctionDefinition functionExpression = new FunctionDefinition();
            fillFunctionExpression(functionExpression, (FunctionProps) props, qNameNormalizer);
            return functionExpression;
        } else if (Objects.equals(DECISION_TABLE.getText(), props.logicType)) {
            final DecisionTable decisionTableExpression = new DecisionTable();
            fillDecisionTableExpression(decisionTableExpression, (DecisionTableProps) props, qNameNormalizer);
            return decisionTableExpression;
        }
        return null;
    }

    private static Double retrieveNestedExpressionWidth(final ExpressionProps props) {
        if (Objects.equals(LITERAL_EXPRESSION.getText(), props.logicType)) {
            Double width = ((LiteralProps) props).width;
            return ((LiteralProps) props).width != null ? width : LiteralExpressionGrid.LITERAL_EXPRESSION_DEFAULT_WIDTH;
        } else if (Objects.equals(CONTEXT.getText(), props.logicType)) {
            Double entryInfoWidth = ((ContextProps) props).entryInfoWidth;
            Double entryExpressionWidth = retrieveNestedExpressionWidth(retrieveContextExpressionWithWidth(((ContextProps) props)));
            return RowNumberColumn.DEFAULT_WIDTH +
                    (entryInfoWidth != null ? entryInfoWidth : ContextGrid.CONTEXT_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH) +
                    (entryExpressionWidth != null ? entryExpressionWidth : UndefinedExpressionColumn.DEFAULT_WIDTH);
        } else if (Objects.equals(RELATION.getText(), props.logicType)) {
            final int columnsLength = ((RelationProps) props).columns == null ? 0 : ((RelationProps) props).columns.length;
            double allColumnsWidth = IntStream.range(0, columnsLength).mapToDouble(i -> {
                Double columnWidth = ((RelationProps) props).columns[i].width;
                return columnWidth != null? columnWidth : DMNGridColumn.DEFAULT_WIDTH;
            }).sum();
            return RowNumberColumn.DEFAULT_WIDTH + allColumnsWidth;
        } else if (Objects.equals(LIST.getText(), props.logicType)) {
            Double listWidth = retrieveNestedExpressionWidth(retrieveListExpressionWithWidth((ListProps) props));
            return RowNumberColumn.DEFAULT_WIDTH + (listWidth != null ? listWidth : ListGrid.LIST_DEFAULT_WIDTH);
        } else if (Objects.equals(INVOCATION.getText(), props.logicType)) {
            Double entryInfoWidth = ((InvocationProps) props).entryInfoWidth;
            Double entryExpressionWidth = retrieveNestedExpressionWidth(retrieveInvocationExpressionWithWidth(((InvocationProps) props)));
            return RowNumberColumn.DEFAULT_WIDTH +
                    (entryInfoWidth != null ? entryInfoWidth : InvocationGrid.INVOCATION_EXPRESSION_ENTRY_INFO_DEFAULT_WIDTH) +
                    (entryExpressionWidth != null ? entryExpressionWidth : UndefinedExpressionColumn.DEFAULT_WIDTH);
        } else if (Objects.equals(FUNCTION.getText(), props.logicType)) {
            Double functionExpressionWidth = retrieveFunctionExpressionWidth(((FeelFunctionProps) props));
            return RowNumberColumn.DEFAULT_WIDTH + (functionExpressionWidth != null ? functionExpressionWidth : UndefinedExpressionColumn.DEFAULT_WIDTH);
        } else if (Objects.equals(DECISION_TABLE.getText(), props.logicType)) {
            final int inputColumnsLength = ((DecisionTableProps) props).input == null ? 0 : ((DecisionTableProps) props).input.length;
            double inputColumnsWidth = IntStream.range(0, inputColumnsLength).mapToDouble(i -> {
                Double columnWidth = ((DecisionTableProps) props).input[i].width;
                return columnWidth != null? columnWidth : DMNGridColumn.DEFAULT_WIDTH;
            }).sum();
            final int outputColumnsLength = ((DecisionTableProps) props).output == null ? 0 : ((DecisionTableProps) props).output.length;
            double outputColumnsWidth = IntStream.range(0, outputColumnsLength).mapToDouble(i -> {
                Double columnWidth = ((DecisionTableProps) props).output[i].width;
                return columnWidth != null? columnWidth : DMNGridColumn.DEFAULT_WIDTH;
            }).sum();
            final int annotationColumnsLength = ((DecisionTableProps) props).annotations == null ? 0 : ((DecisionTableProps) props).annotations.length;
            double annotationColumnsWidth = IntStream.range(0, annotationColumnsLength).mapToDouble(i -> {
                Double columnWidth = ((DecisionTableProps) props).annotations[i].width;
                return columnWidth != null? columnWidth : DMNGridColumn.DEFAULT_WIDTH;
            }).sum();
            return RowNumberColumn.DEFAULT_WIDTH + inputColumnsWidth + outputColumnsWidth + annotationColumnsWidth;
        }
        return null;
    }

    private static Collection<ContextEntry> contextEntriesConvertForContextExpression(final ContextProps contextProps,
                                                                                      final UnaryOperator<QName> qNameNormalizer) {
        return Arrays.stream(Optional.ofNullable(contextProps.contextEntries).orElse(new ContextEntryProps[0])).map(entryRow -> {
            final ContextEntry contextEntry = new ContextEntry();
            contextEntry.setVariable(buildInformationItem(entryRow.entryInfo.id,
                                                          entryRow.entryInfo.name,
                                                          entryRow.entryInfo.dataType,
                                                          entryRow.entryInfo.description,
                                                          qNameNormalizer));
            contextEntry.setExpression(buildAndFillNestedExpression(entryRow.entryExpression, qNameNormalizer));
            return contextEntry;
        }).collect(Collectors.toList());
    }

    private static ContextEntry entryResultConvertForContextExpression(final ContextProps contextProps,
                                                                       final UnaryOperator<QName> qNameNormalizer) {
        final ContextEntry contextEntryResult = new ContextEntry();
        if (contextProps.result != null) {
            contextEntryResult.setExpression(buildAndFillNestedExpression(contextProps.result, qNameNormalizer));
        }
        return contextEntryResult;
    }

    private static Collection<List> rowsConvertForRelationExpression(final RelationProps relationProps) {
        return Arrays
                .stream(Optional.ofNullable(relationProps.rows).orElse(new Row[0]))
                .map(row -> {
                    final List list = new List();
                    list.setId(new Id(row.id));
                    list.getExpression().addAll(
                            IntStream.range(0, Optional.ofNullable(relationProps.columns).orElse(new Column[0]).length).mapToObj(columnIndex -> {
                                final Cell cell = row.cells.length <= columnIndex ? new Cell(UUID.uuid(), "", null, null) : row.cells[columnIndex];
                                final LiteralExpression wrappedExpression = new LiteralExpression();
                                wrappedExpression.setId(new Id(cell.id));
                                wrappedExpression.setDescription(new Description(cell.description));
                                wrappedExpression.setExpressionLanguage(new ExpressionLanguage(cell.expressionLanguage));
                                wrappedExpression.setText(new Text(cell.content));

                                return HasExpression.wrap(list, wrappedExpression);
                            }).collect(Collectors.toList())
                    );
                    return list;
                })
                .collect(Collectors.toList());
    }

    private static Collection<InformationItem> columnsConvertForRelationExpression(final RelationProps relationProps,
                                                                                   final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(relationProps.columns).orElse(new Column[0]))
                .map(column -> buildInformationItem(column.id, column.name, column.dataType, column.description, qNameNormalizer))
                .collect(Collectors.toList());
    }

    private static Collection<HasExpression> itemsConvertForListExpression(final ListProps listProps,
                                                                           final List listExpression,
                                                                           final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(listProps.items).orElse(new ExpressionProps[0]))
                .map(props -> HasExpression.wrap(listExpression, buildAndFillNestedExpression(props, qNameNormalizer)))
                .collect(Collectors.toList());
    }

    private static Collection<Binding> bindingsConvertForInvocationExpression(final InvocationProps invocationProps,
                                                                              final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(invocationProps.bindingEntries).orElse(new ContextEntryProps[0]))
                .map(binding -> {
                    final Binding bindingModel = new Binding();
                    bindingModel.setVariable(buildInformationItem(binding.entryInfo.id,
                                                                  binding.entryInfo.name,
                                                                  binding.entryInfo.dataType,
                                                                  binding.entryInfo.description,
                                                                  qNameNormalizer));
                    bindingModel.setExpression(buildAndFillNestedExpression(binding.entryExpression, qNameNormalizer));
                    return bindingModel;
                })
                .collect(Collectors.toList());
    }

    private static InformationItem buildInformationItem(final String id,
                                                        final String name,
                                                        final String dataType,
                                                        final String description,
                                                        final UnaryOperator<QName> qNameNormalizer) {
        final InformationItem informationItem = new InformationItem();
        informationItem.setId(new Id(id));
        informationItem.setName(new Name(name));
        QName qName = qNameNormalizer.apply(makeQName(dataType));
        informationItem.setTypeRef(qName);
        informationItem.setDescription(new Description(description));
        return informationItem;
    }

    private static Collection<InformationItem> formalParametersConvertForFunctionExpression(final FunctionProps functionProps,
                                                                                            final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(functionProps.formalParameters).orElse(new EntryInfo[0]))
                .map(entryInfo -> {
                    final InformationItem informationItem = new InformationItem();
                    informationItem.setId(new Id(entryInfo.id));
                    informationItem.setName(new Name(entryInfo.name));
                    QName qName = qNameNormalizer.apply(makeQName(entryInfo.dataType));
                    informationItem.setTypeRef(qName);
                    return informationItem;
                })
                .collect(Collectors.toList());
    }

    private static Expression wrappedExpressionBasedOnKind(final FunctionDefinition.Kind functionKind,
                                                           final FunctionProps functionProps,
                                                           final UnaryOperator<QName> qNameNormalizer) {
        switch (functionKind) {
            case JAVA:
                final JavaFunctionProps javaFunctionProps = (JavaFunctionProps) functionProps;
                final Context javaWrappedContext = new Context();
                javaWrappedContext.getContextEntry().add(buildContextEntry(new LiteralExpression(), javaFunctionProps.classFieldId, javaFunctionProps.className, VARIABLE_CLASS));
                javaWrappedContext.getContextEntry().add(buildContextEntry(new LiteralExpression(), javaFunctionProps.methodFieldId, javaFunctionProps.methodName, VARIABLE_METHOD_SIGNATURE));
                return javaWrappedContext;
            case PMML:
                final PmmlFunctionProps pmmlFunctionProps = (PmmlFunctionProps) functionProps;
                final Context pmmlWrappedContext = new Context();
                pmmlWrappedContext.getContextEntry().add(buildContextEntry(new LiteralExpressionPMMLDocument(), pmmlFunctionProps.documentFieldId, pmmlFunctionProps.document, VARIABLE_DOCUMENT));
                pmmlWrappedContext.getContextEntry().add(buildContextEntry(new LiteralExpressionPMMLDocumentModel(), pmmlFunctionProps.modelFieldId, pmmlFunctionProps.model, VARIABLE_MODEL));
                return pmmlWrappedContext;
            default:
            case FEEL:
                final FeelFunctionProps feelFunctionProps = (FeelFunctionProps) functionProps;
                return buildAndFillNestedExpression(
                        Optional.ofNullable(feelFunctionProps.expression)
                                .orElse(new LiteralProps(new Id().getValue(), "Nested Literal Expression", UNDEFINED.getText(), "", null, null, null)),
                        qNameNormalizer
                );
        }
    }

    private static ContextEntry buildContextEntry(final LiteralExpression entryExpression, final String textFieldId, final String expressionText, final String variableName) {
        final ContextEntry entry = new ContextEntry();
        final InformationItem entryVariable = new InformationItem();
        entryVariable.setName(new Name(variableName));
        entryVariable.setTypeRef(BuiltInType.STRING.asQName());
        entryExpression.setId(new Id(textFieldId));
        entryExpression.setText(new Text(expressionText));
        entry.setVariable(entryVariable);
        entry.setExpression(entryExpression);
        return entry;
    }

    private static Collection<DecisionRule> rulesConvertForDecisionTableExpression(final DecisionTableProps decisionTableProps) {
        return Arrays
                .stream(Optional.ofNullable(decisionTableProps.rules).orElse(new DecisionTableRule[0]))
                .map(rule -> {
                    final DecisionRule decisionRule = new DecisionRule();
                    decisionRule.setId(new Id(rule.id));
                    decisionRule.getAnnotationEntry().addAll(Arrays.stream(rule.annotationEntries).map(annotationEntry -> {
                        final RuleAnnotationClauseText ruleAnnotationClauseText = new RuleAnnotationClauseText();
                        ruleAnnotationClauseText.setText(new Text(annotationEntry));
                        return ruleAnnotationClauseText;
                    }).collect(Collectors.toList()));
                    decisionRule.getOutputEntry().addAll(Arrays.stream(rule.outputEntries).map(outputEntry -> {
                        final LiteralExpression literalExpression = new LiteralExpression();
                        literalExpression.setText(new Text(outputEntry.content));
                        literalExpression.setId(new Id(outputEntry.id));
                        literalExpression.setDescription(new Description(outputEntry.description));
                        literalExpression.setExpressionLanguage(new ExpressionLanguage(outputEntry.expressionLanguage));
                        return literalExpression;
                    }).collect(Collectors.toList()));
                    decisionRule.getInputEntry().addAll(Arrays.stream(rule.inputEntries).map(inputEntry -> {
                        final UnaryTests unaryTests = new UnaryTests();
                        unaryTests.setText(new Text(inputEntry.content));
                        unaryTests.setId(new Id(inputEntry.id));
                        unaryTests.setDescription(new Description(inputEntry.description));
                        unaryTests.setExpressionLanguage(new ExpressionLanguage(inputEntry.expressionLanguage));
                        return unaryTests;
                    }).collect(Collectors.toList()));
                    return decisionRule;
                })
                .collect(Collectors.toList());
    }

    private static Collection<InputClause> inputConvertForDecisionTableExpression(final DecisionTableProps decisionTableProps,
                                                                                  final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(decisionTableProps.input).orElse(new InputClauseProps[0]))
                .map(input -> {
                    final InputClause inputClause = new InputClause();
                    inputClause.setId(new Id(input.id));
                    inputClause.getInputExpression().setId(new Id(input.idLiteralExpression));
                    inputClause.getInputExpression().setText(new Text(input.name));
                    inputClause.setDescription(new Description(input.description));
                    QName qName = qNameNormalizer.apply(makeQName(input.dataType));
                    inputClause.getInputExpression().setTypeRef(qName);
                    inputClause.getInputExpression().setTypeRefHolder(makeQNameHolder(qName));
                    if (input.clauseUnaryTests != null) {
                        inputClause.setInputValues(convertInputClauseUnaryTest(input.clauseUnaryTests));
                    }

                    return inputClause;
                })
                .collect(Collectors.toList());
    }

    private static Collection<OutputClause> outputConvertForDecisionTableExpression(final DecisionTableProps decisionTableProps,
                                                                                    final UnaryOperator<QName> qNameNormalizer) {
        return Arrays
                .stream(Optional.ofNullable(decisionTableProps.output).orElse(new OutputClauseProps[0]))
                .map(output -> {
                    final OutputClause outputClause = new OutputClause();
                    outputClause.setId(new Id(output.id));
                    outputClause.setName(output.name);
                    outputClause.setDescription(new Description(output.description));
                    QName qName = qNameNormalizer.apply(makeQName(output.dataType));
                    outputClause.setTypeRef(qName);
                    if (output.defaultOutputValue != null && Objects.equals(LITERAL_EXPRESSION.getText(), output.defaultOutputValue.logicType)) {
                        OutputClauseLiteralExpression outputClauseLiteralExpression =
                                new OutputClauseLiteralExpression(new Id(output.defaultOutputValue.id),
                                                                  new Description(),
                                                                  new QName(),
                                                                  new Text(((LiteralProps) output.defaultOutputValue).content),
                                                                  null);
                        outputClause.setDefaultOutputEntry(outputClauseLiteralExpression);
                    }
                    if (output.clauseUnaryTests != null) {
                        outputClause.setOutputValues(convertOutputClauseUnaryTest(output.clauseUnaryTests));
                    }

                    return outputClause;
                })
                .collect(Collectors.toList());
    }

    private static InputClauseUnaryTests convertInputClauseUnaryTest(ClauseUnaryTests clauseUnaryTests) {
        return new InputClauseUnaryTests(new Id(clauseUnaryTests.id),
                                         new Text(clauseUnaryTests.text),
                                         ConstraintType.fromString(clauseUnaryTests.constraintType));
    }

    private static OutputClauseUnaryTests convertOutputClauseUnaryTest(ClauseUnaryTests clauseUnaryTests) {
        return new OutputClauseUnaryTests(new Id(clauseUnaryTests.id),
                                          new Text(clauseUnaryTests.text),
                                          ConstraintType.fromString(clauseUnaryTests.constraintType));
    }

    private static Collection<RuleAnnotationClause> annotationsConvertForDecisionTableExpression(final DecisionTableProps decisionTableProps) {
        return Arrays
                .stream(Optional.ofNullable(decisionTableProps.annotations).orElse(new Annotation[0]))
                .map(annotation -> {
                    final RuleAnnotationClause ruleAnnotationClause = new RuleAnnotationClause();
                    ruleAnnotationClause.setName(new Name(annotation.name));
                    return ruleAnnotationClause;
                })
                .collect(Collectors.toList());
    }

    private static void updateComponentWidthsForDecisionTableExpression(final DecisionTable decisionTableExpression, final DecisionTableProps decisionTableProps) {
        final Clause[] inputProps = Optional.ofNullable(decisionTableProps.input).orElse(new InputClauseProps[0]);
        final Clause[] outputProps = Optional.ofNullable(decisionTableProps.output).orElse(new OutputClauseProps[0]);
        final Annotation[] annotationProps = Optional.ofNullable(decisionTableProps.annotations).orElse(new Annotation[0]);
        decisionTableExpression.getComponentWidths().set(0, RowNumberColumn.DEFAULT_WIDTH);
        IntStream.range(0, inputProps.length)
                .forEach(index -> decisionTableExpression.getComponentWidths().set(index + 1, inputProps[index].width));
        IntStream.range(0, outputProps.length)
                .forEach(index -> decisionTableExpression.getComponentWidths().set(
                        inputProps.length + index + 1, outputProps[index].width)
                );
        IntStream.range(0, annotationProps.length)
                .forEach(index -> decisionTableExpression.getComponentWidths().set(
                        inputProps.length + outputProps.length + index + 1, annotationProps[index].width)
                );
    }

    private static QName makeQName(String dataType) {
        return BuiltInTypeUtils.isBuiltInType(dataType) ?
                BuiltInTypeUtils.findBuiltInTypeByName(dataType).orElse(BuiltInType.UNDEFINED).asQName() :
                new QName(QName.NULL_NS_URI, dataType);
    }

    private static QNameHolder makeQNameHolder(QName qname) {
        return new QNameHolder(qname);
    }
}
