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

import java.util.Collection;
import java.util.function.Consumer;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
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

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionModelFillerTest {

    public static final String EXPRESSION_ID = "id1";
    public static final String EXPRESSION_NAME = "Expression Name";
    public static final String DATA_TYPE = BuiltInType.UNDEFINED.asQName().getLocalPart();
    private static final String ENTRY_INFO_NAME = "Entry Info";
    private static final String ENTRY_INFO_DATA_TYPE = BuiltInType.STRING.asQName().getLocalPart();
    private static final String ENTRY_INFO_DESCRIPTION = "This is an entry info";
    private static final String ENTRY_EXPRESSION_CONTENT = "content";
    private static final String ENTRY_EXPRESSION_DESCRIPTION = "This is a content";
    private static final String ENTRY_EXPRESSION_EXPRESSION_LANGUAGE = "expLan";
    private static final Double ENTRY_INFO_WIDTH = 200d;
    private static final Double ENTRY_EXPRESSION_WIDTH = 350d;
    private static final Double EMPTY_EXPRESSION_WIDTH = 190d;
    private static final Double PARAMETERS_WIDTH = 450d;
    private static final String PARAM_ID = "param-id";
    private static final String PARAM_NAME = "p-1";
    private static final String PARAM_DATA_TYPE = BuiltInType.BOOLEAN.asQName().getLocalPart();

    @Test
    public void testFillLiteralExpression() {
        final LiteralExpression literalExpression = new LiteralExpression();
        final String content = "content";
        final String description = "desc";
        final String expressionLanguage = "eL";
        final double width = 100d;
        final LiteralProps literalProps = new LiteralProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, content, description, expressionLanguage, width);

        ExpressionModelFiller.fillLiteralExpression(literalExpression, literalProps);

        assertThat(literalExpression)
                .isNotNull();
        assertThat(literalExpression.getText())
                .isNotNull();
        assertThat(literalExpression.getText().getValue())
                .isNotNull().isEqualTo(content);
        assertThat(literalExpression.getComponentWidths())
                .isNotEmpty()
                .first().isEqualTo(width);
        assertThat(literalExpression.getDescription().getValue())
                .isNotNull().isEqualTo(description);
        assertThat(literalExpression.getExpressionLanguage().getValue())
                .isNotNull().isEqualTo(expressionLanguage);
    }

    @Test
    public void testFillContextExpression() {
        final Context contextExpression = new Context();
        final ContextEntryProps[] contextEntries = new ContextEntryProps[]{
                buildContextEntryProps()
        };
        final ExpressionProps result = new LiteralProps("result-id", "Result Expression", BuiltInType.DATE.asQName().getLocalPart(), "", "", "", null);
        final ContextProps contextProps = new ContextProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, contextEntries, result, ENTRY_INFO_WIDTH, ENTRY_EXPRESSION_WIDTH);

        ExpressionModelFiller.fillContextExpression(contextExpression, contextProps, qName -> qName);

        assertThat(contextExpression).isNotNull();
        assertThat(contextExpression.getContextEntry())
                .isNotNull()
                .hasSize(2);
        assertThat(contextExpression.getContextEntry())
                .first()
                .satisfies(contextEntry -> {
                    assertThat(contextEntry).extracting(ContextEntry::getVariable).isNotNull();
                    assertThat(contextEntry).extracting(entry -> entry.getVariable().getValue().getValue()).isEqualTo(ENTRY_INFO_NAME);
                    assertThat(contextEntry).extracting(entry -> entry.getVariable().getTypeRef().getLocalPart()).isEqualTo(ENTRY_INFO_DATA_TYPE);
                    assertThat(contextEntry).extracting(entry -> entry.getVariable().getDescription().getValue()).isEqualTo(ENTRY_INFO_DESCRIPTION);
                    assertThat(contextEntry).extracting(ContextEntry::getExpression)
                            .isNotNull()
                            .isExactlyInstanceOf(LiteralExpression.class);
                    assertThat(contextEntry).extracting(entry -> entry.getExpression().getDescription().getValue()).isEqualTo(ENTRY_EXPRESSION_DESCRIPTION);
                    assertThat(contextEntry).extracting(entry -> ((LiteralExpression) entry.getExpression()).getExpressionLanguage().getValue()).isEqualTo(ENTRY_EXPRESSION_EXPRESSION_LANGUAGE);
                });

        assertThat(contextExpression.getContextEntry())
                .last()
                .satisfies(contextEntry -> {
                    assertThat(contextEntry).extracting(ContextEntry::getVariable).isNull();
                    assertThat(contextEntry).extracting(ContextEntry::getExpression)
                            .isNotNull()
                            .isExactlyInstanceOf(LiteralExpression.class);
                });

        assertEntryWidths(contextExpression.getComponentWidths());
    }

    @Test
    public void testFillRelationExpression() {
        final Relation relationExpression = new Relation();
        final String firstColumnId = "Column Id";
        final String firstColumnName = "Column Name";
        final String firstColumnDataType = BuiltInType.BOOLEAN.asQName().getLocalPart();
        final double firstColumnWidth = 200d;
        final String firstColumnDescription = "1st";
        final String secondColumnId = "Another Column Name";
        final String secondColumnName = "Another Column Name";
        final String secondColumnDataType = BuiltInType.DATE.asQName().getLocalPart();
        final double secondColumnWidth = 315d;
        final String secondColumnDescription = "2nd";
        final Cell firstCell = new Cell("firstCellId", "first cell", "", "");
        final Cell secondCell = new Cell("secondCellId", "second cell", "", "");
        final Cell thirdCell = new Cell("thirdCellId", "third cell", "", "");
        final Cell fourthCell = new Cell("fourthCellId", "fourth cell", "", "");
        final Column[] columns = new Column[]{new Column(firstColumnId, firstColumnName, firstColumnDataType, firstColumnDescription, firstColumnWidth), new Column(secondColumnId, secondColumnName, secondColumnDataType, secondColumnDescription, secondColumnWidth)};
        final Row[] rows = new Row[]{new Row("first-row", new Cell[]{firstCell, secondCell}), new Row("second-id", new Cell[]{thirdCell, fourthCell})};
        final RelationProps relationProps = new RelationProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, columns, rows);

        ExpressionModelFiller.fillRelationExpression(relationExpression, relationProps, qName -> qName);

        assertThat(relationExpression).isNotNull();
        assertThat(relationExpression.getColumn())
                .isNotNull()
                .hasSize(2);
        assertThat(relationExpression.getColumn())
                .first()
                .satisfies(checkRelationColumn(firstColumnName, firstColumnDataType, firstColumnDescription));
        assertThat(relationExpression.getColumn())
                .last()
                .satisfies(checkRelationColumn(secondColumnName, secondColumnDataType, secondColumnDescription));

        assertThat(relationExpression.getRow())
                .isNotNull()
                .hasSize(2);
        assertThat(relationExpression.getRow())
                .first()
                .satisfies(checkRelationRow(firstCell, secondCell));
        assertThat(relationExpression.getRow())
                .last()
                .satisfies(checkRelationRow(thirdCell, fourthCell));
        assertThat(relationExpression.getComponentWidths()).element(1).isEqualTo(firstColumnWidth);
        assertThat(relationExpression.getComponentWidths()).element(2).isEqualTo(secondColumnWidth);
    }

    @Test
    public void testFillListExpression() {
        final List listExpression = new List();
        final String nestedContent = "nested content";
        final ExpressionProps[] items = new ExpressionProps[]{new LiteralProps("nested-literal", "Nested Literal Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), nestedContent, "", "", null)};
        final ListProps listProps = new ListProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, items);

        ExpressionModelFiller.fillListExpression(listExpression, listProps, qName -> qName);

        assertThat(listExpression).isNotNull();
        assertThat(listExpression.getExpression())
                .isNotNull()
                .hasSize(1);
        assertThat(listExpression.getExpression()).first().extracting(HasExpression::getExpression)
                .isNotNull()
                .isExactlyInstanceOf(LiteralExpression.class);
        assertThat(listExpression.getExpression()).first().extracting(item -> ((LiteralExpression) item.getExpression()).getText().getValue()).isEqualTo(nestedContent);
    }

    @Test
    public void testFillInvocationExpression() {
        final Invocation invocationExpression = new Invocation();
        final String functionID = "ID-F";
        final String invokedFunction = "f()";
        final ContextEntryProps[] bindingEntries = new ContextEntryProps[]{
                buildContextEntryProps()
        };
        final InvocationFunctionProps invocationFunctionProps = new InvocationFunctionProps(functionID, invokedFunction);
        final InvocationProps invocationProps = new InvocationProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, invocationFunctionProps, bindingEntries, ENTRY_INFO_WIDTH, ENTRY_EXPRESSION_WIDTH);

        ExpressionModelFiller.fillInvocationExpression(invocationExpression, invocationProps, qName -> qName);

        assertThat(invocationExpression).isNotNull();
        assertThat(invocationExpression.getExpression())
                .isNotNull()
                .isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) invocationExpression.getExpression()).getText().getValue()).isEqualTo(invokedFunction);
        assertThat((invocationExpression.getExpression()).getId().getValue()).isEqualTo(functionID);
        assertThat(invocationExpression.getBinding())
                .isNotNull()
                .hasSize(1);
        assertThat(invocationExpression.getBinding()).first()
                .satisfies(binding -> {
                    assertThat(binding).extracting(Binding::getVariable).isNotNull();
                    assertThat(binding).extracting(entry -> entry.getVariable().getValue().getValue()).isEqualTo(ENTRY_INFO_NAME);
                    assertThat(binding).extracting(entry -> entry.getVariable().getTypeRef().getLocalPart()).isEqualTo(ENTRY_INFO_DATA_TYPE);
                    assertThat(binding).extracting(Binding::getExpression)
                            .isNotNull()
                            .isExactlyInstanceOf(LiteralExpression.class);
                });
        assertEntryWidths(invocationExpression.getComponentWidths());
    }

    @Test
    public void testFillPMMLFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String documentName = "document name";
        final String modelName = "model name";
        final PmmlFunctionProps functionProps = new PmmlFunctionProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_ID, PARAM_NAME, PARAM_DATA_TYPE, null)}, PARAMETERS_WIDTH, documentName, modelName, "document-id", "model-id");

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps, qName -> qName);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression, null);
        assertNestedContextEntries(functionExpression, documentName, modelName);
    }

    @Test
    public void testFillJavaFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String className = "class name";
        final String methodName = "method name";
        final JavaFunctionProps functionProps = new JavaFunctionProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_ID, PARAM_NAME, PARAM_DATA_TYPE, null)}, PARAMETERS_WIDTH, className, methodName, "class-id", "method-id");

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps, qName -> qName);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression, PARAMETERS_WIDTH);
        assertNestedContextEntries(functionExpression, className, methodName);
    }

    @Test
    public void testFillFeelFunctionExpression() {
        final FunctionDefinition functionExpression = new FunctionDefinition();
        final String nestedContent = "Nested Content";
        final String literalDescription = "Nested Description";
        final String literalExpressionLanguage = "Nested Literal Expression";
        final FeelFunctionProps functionProps = new FeelFunctionProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, new EntryInfo[]{new EntryInfo(PARAM_ID, PARAM_NAME, PARAM_DATA_TYPE, null)}, PARAMETERS_WIDTH,
                                                                      new LiteralProps("nested-literal", "Nested Literal Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), nestedContent, literalDescription, literalExpressionLanguage, null));

        ExpressionModelFiller.fillFunctionExpression(functionExpression, functionProps, qName -> qName);

        assertThat(functionExpression).isNotNull();
        assertFormalParameters(functionExpression);
        assertParameterWidth(functionExpression, EMPTY_EXPRESSION_WIDTH);
        assertThat(functionExpression.getExpression())
                .isNotNull()
                .isExactlyInstanceOf(LiteralExpression.class);
        assertThat(((LiteralExpression) functionExpression.getExpression()).getText().getValue()).isEqualTo(nestedContent);
        assertThat(functionExpression.getExpression().getDescription().getValue()).isEqualTo(literalDescription);
        assertThat(((LiteralExpression) functionExpression.getExpression()).getExpressionLanguage().getValue()).isEqualTo(literalExpressionLanguage);
    }

    @Test
    public void testFillDecisionTableExpression() {
        final DecisionTable decisionTableExpression = new DecisionTable();
        final String annotationName = "Annotation name";
        final double annotationWidth = 456d;
        final Annotation[] annotations = new Annotation[]{new Annotation(annotationName, annotationWidth)};
        final String inputId = "Input id";
        final String inputLiteralExpressionId = "Input le id";
        final String inputColumn = "Input column";
        final String inputDataType = BuiltInType.DATE_TIME.asQName().getLocalPart();
        final String inputDescription = "First Input";
        final double inputWidth = 123d;
        final String inputId2 = "Input id2";
        final String inputLiteralExpressionId2 = "Input le id2";
        final String inputColumn2 = "Input column 2";
        final String inputDataType2 = "tCustom";
        final String inputDescription2 = "Second Input";
        final double inputWidth2 = 234d;
        final String inputClauseUnaryTestsId = "icud id";
        final String inputClauseUnaryTestsText = "text";
        final String inputClauseUnaryTestsConstraintType = "enumeration";
        final ClauseUnaryTests inputClauseUnaryTest = new ClauseUnaryTests(inputClauseUnaryTestsId, inputClauseUnaryTestsText, inputClauseUnaryTestsConstraintType);
        final InputClauseProps[] input = new InputClauseProps[]{new InputClauseProps(inputId, inputColumn, inputDataType, inputWidth, inputClauseUnaryTest, inputLiteralExpressionId, inputDescription), new InputClauseProps(inputId2, inputColumn2, inputDataType2, inputWidth2, null, inputLiteralExpressionId2, inputDescription2)};
        final String outputId = "Output id";
        final String outputColumn = "Output column";
        final String outputDataType = BuiltInType.STRING.asQName().getLocalPart();
        final String outputDescription = "First description";
        final double outputWidth = 223d;
        final String outputId2 = "Output id2";
        final String outputColumn2 = "Output column 2";
        final String outputDataType2 = "tTest";
        final String outputDescription2 = "Second description";
        final double outputWidth2 = 432d;
        final String outputClauseUnaryTestsId = "ocud id";
        final String outputClauseUnaryTestsText = "";
        final String outputClauseUnaryTestsConstraintType = "none";
        final ClauseUnaryTests outputClauseUnaryTest = new ClauseUnaryTests(outputClauseUnaryTestsId, outputClauseUnaryTestsText, outputClauseUnaryTestsConstraintType);
        final String defaultOutputValueId = "dovID";
        final String defaultOutputValueContent = "Default-Test";
        final LiteralProps defaultOutputValue = new LiteralProps(defaultOutputValueId, null, BuiltInType.UNDEFINED.asQName().getLocalPart(), defaultOutputValueContent, null, null, null);
        final OutputClauseProps[] output = new OutputClauseProps[]{new OutputClauseProps(outputId, outputColumn, outputDataType, outputWidth, outputClauseUnaryTest, defaultOutputValue, outputDescription), new OutputClauseProps(outputId2, outputColumn2, outputDataType2, outputWidth2, null, null, outputDescription2)};
        final String inputValue = "input value";
        final String inputDesc = "input description";
        final String outputValue = "output value";
        final String outputDesc = "output description";
        final String annotationValue = "annotation value";
        DecisionTableRule[] rules = new DecisionTableRule[]{new DecisionTableRule("rule-1", new RuleEntry[]{new RuleEntry("someId", inputValue, inputDesc, "")}, new RuleEntry[]{new RuleEntry("anotherId", outputValue, outputDesc, "")}, new String[]{annotationValue})};
        final DecisionTableProps decisionTableProps = new DecisionTableProps(EXPRESSION_ID, EXPRESSION_NAME, DATA_TYPE, HitPolicy.COLLECT.value(), BuiltinAggregator.MAX.getCode(), annotations, input, output, rules);

        ExpressionModelFiller.fillDecisionTableExpression(decisionTableExpression, decisionTableProps, qName -> qName);

        assertThat(decisionTableExpression).isNotNull();
        assertThat(decisionTableExpression.getHitPolicy()).isEqualTo(HitPolicy.COLLECT);
        assertThat(decisionTableExpression.getAggregation()).isEqualTo(BuiltinAggregator.MAX).isNotNull();
        assertThat(decisionTableExpression.getAnnotations())
                .hasSize(1)
                .first().extracting(annotation -> annotation.getValue().getValue()).isEqualTo(annotationName);
        assertThat(decisionTableExpression.getInput())
                .isNotNull()
                .hasSize(2)
                .first()
                .satisfies(inputRef -> {
                    assertThat(inputRef).extracting(InputClause::getInputExpression).isNotNull();
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getText().getValue()).isEqualTo(inputColumn);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getDescription().getValue()).isEqualTo(inputDescription);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getTypeRef().getLocalPart()).isEqualTo(inputDataType);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getId().getValue()).isEqualTo(inputLiteralExpressionId);
                    assertThat(inputRef).extracting(InputClause::getInputValues).extracting(inputClauseUnaryTests -> inputClauseUnaryTests.getId().getValue()).isEqualTo(inputClauseUnaryTestsId);
                    assertThat(inputRef).extracting(InputClause::getInputValues).extracting(inputClauseUnaryTests -> inputClauseUnaryTests.getText().getValue()).isEqualTo(inputClauseUnaryTestsText);
                    assertThat(inputRef).extracting(InputClause::getInputValues).extracting(inputClauseUnaryTests -> inputClauseUnaryTests.getConstraintType().value()).isEqualTo(inputClauseUnaryTestsConstraintType);
                });
        assertThat(decisionTableExpression.getInput().get(1))
                .satisfies(inputRef -> {
                    assertThat(inputRef).extracting(InputClause::getInputExpression).isNotNull();
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getText().getValue()).isEqualTo(inputColumn2);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getDescription().getValue()).isEqualTo(inputDescription2);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getTypeRef().getLocalPart()).isEqualTo(inputDataType2);
                    assertThat(inputRef).extracting(inputClause -> inputClause.getInputExpression().getId().getValue()).isEqualTo(inputLiteralExpressionId2);

                });
        assertThat(decisionTableExpression.getOutput())
                .isNotNull()
                .hasSize(2)
                .first()
                .satisfies(outputRef -> {
                    assertThat(outputRef).extracting(OutputClause::getName).isEqualTo(outputColumn);
                    assertThat(outputRef).extracting(outputClause -> outputClause.getTypeRef().getLocalPart()).isEqualTo(outputDataType);
                    assertThat(outputRef).extracting(outputClause -> outputClause.getDescription().getValue()).isEqualTo(outputDescription);
                    assertThat(outputRef).extracting(OutputClause::getOutputValues).extracting(outputClauseUnaryTests -> outputClauseUnaryTests.getId().getValue()).isEqualTo(outputClauseUnaryTestsId);
                    assertThat(outputRef).extracting(OutputClause::getOutputValues).extracting(outputClauseUnaryTests -> outputClauseUnaryTests.getText().getValue()).isEqualTo(outputClauseUnaryTestsText);
                    assertThat(outputRef).extracting(OutputClause::getOutputValues).extracting(outputClauseUnaryTests -> outputClauseUnaryTests.getConstraintType().value()).isEqualTo(outputClauseUnaryTestsConstraintType);
                    assertThat(outputRef).extracting(OutputClause::getDefaultOutputEntry).extracting(defaultOutputEntry -> defaultOutputEntry.getId().getValue()).isEqualTo(defaultOutputValueId);
                    assertThat(outputRef).extracting(OutputClause::getDefaultOutputEntry).extracting(defaultOutputEntry -> defaultOutputEntry.getText().getValue()).isEqualTo(defaultOutputValueContent);
                });
        assertThat(decisionTableExpression.getOutput().get(1))
                .satisfies(outputRef -> {
                    assertThat(outputRef).extracting(OutputClause::getName).isEqualTo(outputColumn2);
                    assertThat(outputRef).extracting(outputClause -> outputClause.getTypeRef().getLocalPart()).isEqualTo(outputDataType2);
                    assertThat(outputRef).extracting(outputClause -> outputClause.getDescription().getValue()).isEqualTo(outputDescription2);
                });

        assertThat(decisionTableExpression.getRule())
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(ruleRef -> {
                    assertThat(ruleRef).extracting(rule -> rule.getInputEntry().size()).isEqualTo(1);
                    assertThat(ruleRef).extracting(DecisionRule::getInputEntry).extracting(inputEntry -> inputEntry.get(0).getText().getValue()).isEqualTo(inputValue);
                    assertThat(ruleRef).extracting(DecisionRule::getOutputEntry).extracting(outputEntry -> outputEntry.get(0).getText().getValue()).isEqualTo(outputValue);
                    assertThat(ruleRef).extracting(DecisionRule::getAnnotationEntry).extracting(annotationEntry -> annotationEntry.get(0).getText().getValue()).isEqualTo(annotationValue);
                });
        assertThat(decisionTableExpression.getComponentWidths()).element(1).isEqualTo(inputWidth);
        assertThat(decisionTableExpression.getComponentWidths()).element(2).isEqualTo(inputWidth2);
        assertThat(decisionTableExpression.getComponentWidths()).element(3).isEqualTo(outputWidth);
        assertThat(decisionTableExpression.getComponentWidths()).element(4).isEqualTo(outputWidth2);
        assertThat(decisionTableExpression.getComponentWidths()).element(5).isEqualTo(annotationWidth);
    }

    private ContextEntryProps buildContextEntryProps() {
        return new ContextEntryProps(new EntryInfo("entry-info-id", ENTRY_INFO_NAME, ENTRY_INFO_DATA_TYPE, ENTRY_INFO_DESCRIPTION), new LiteralProps("nested-literal", "Nested Expression", BuiltInType.UNDEFINED.asQName().getLocalPart(), ENTRY_EXPRESSION_CONTENT, ENTRY_EXPRESSION_DESCRIPTION, ENTRY_EXPRESSION_EXPRESSION_LANGUAGE, null));
    }

    private void assertEntryWidths(final Collection<Double> componentWidths) {
        assertThat(componentWidths)
                .isNotNull()
                .hasSize(3);
        assertThat(componentWidths).element(1).isEqualTo(ENTRY_INFO_WIDTH);
        assertThat(componentWidths).element(2).isEqualTo(EMPTY_EXPRESSION_WIDTH);
    }

    private Consumer<List> checkRelationRow(final Cell firstCell, final Cell secondCell) {
        return param -> {
            assertThat(param).extracting(list -> list.getExpression().size()).isEqualTo(2);
            assertThat(param).extracting(list -> list.getExpression().get(0).getExpression()).isExactlyInstanceOf(LiteralExpression.class);
            assertThat(param).extracting(list -> ((LiteralExpression) list.getExpression().get(0).getExpression()).getText().getValue()).isEqualTo(firstCell.content);
            assertThat(param).extracting(list -> ((LiteralExpression) list.getExpression().get(1).getExpression()).getText().getValue()).isEqualTo(secondCell.content);
        };
    }

    private Consumer<InformationItem> checkRelationColumn(final String columnName, final String columnDataType, final String columnDescription) {
        return param -> {
            assertThat(param).extracting(HasName::getValue).isNotNull();
            assertThat(param).extracting(informationItem -> informationItem.getValue().getValue()).isEqualTo(columnName);
            assertThat(param).extracting(informationItem -> informationItem.getDescription().getValue()).isEqualTo(columnDescription);
            assertThat(param).extracting(informationItem -> informationItem.getTypeRef().getLocalPart()).isEqualTo(columnDataType);
        };
    }

    private void assertFormalParameters(FunctionDefinition functionExpression) {
        assertThat(functionExpression.getFormalParameter())
                .isNotNull()
                .hasSize(1)
                .first()
                .satisfies(param -> {
                    assertThat(param.getId().getValue()).isEqualTo(PARAM_ID);
                    assertThat(param.getValue().getValue()).isEqualTo(PARAM_NAME);
                    assertThat(param.getTypeRef().getLocalPart()).isEqualTo(PARAM_DATA_TYPE);
                });
    }

    private void assertParameterWidth(FunctionDefinition functionExpression, Double expectedWidth) {
        assertThat(functionExpression.getComponentWidths())
                .isNotNull()
                .hasSize(2)
                .element(1).isEqualTo(expectedWidth);
    }

    private void assertNestedContextEntries(FunctionDefinition functionExpression, String documentName, String modelName) {
        assertThat(functionExpression.getExpression())
                .isNotNull()
                .isExactlyInstanceOf(Context.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry())
                .isNotNull()
                .hasSize(2);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).first().extracting(ContextEntry::getExpression).isInstanceOf(LiteralExpression.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).first().extracting(contextEntry -> ((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEqualTo(documentName);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).last().extracting(ContextEntry::getExpression).isInstanceOf(LiteralExpression.class);
        assertThat(((Context) functionExpression.getExpression()).getContextEntry()).last().extracting(contextEntry -> ((LiteralExpression) contextEntry.getExpression()).getText().getValue()).isEqualTo(modelName);
    }
}
