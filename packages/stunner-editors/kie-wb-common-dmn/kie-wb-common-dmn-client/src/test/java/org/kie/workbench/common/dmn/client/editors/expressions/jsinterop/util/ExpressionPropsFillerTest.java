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

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.model.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.HitPolicy;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.InputClause;
import org.kie.workbench.common.dmn.api.definition.model.InputClauseLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;
import org.kie.workbench.common.dmn.api.definition.model.List;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.api.definition.model.OutputClause;
import org.kie.workbench.common.dmn.api.definition.model.Relation;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClause;
import org.kie.workbench.common.dmn.api.definition.model.RuleAnnotationClauseText;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ContextProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.DecisionTableProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ExpressionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FeelFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.FunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.InvocationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.JavaFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.ListProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.LiteralProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.PmmlFunctionProps;
import org.kie.workbench.common.dmn.client.editors.expressions.jsinterop.props.RelationProps;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionType;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionPropsFillerTest {

    private static final String ENTRY_NAME = "information item";
    private static final QName ENTRY_DATA_TYPE = BuiltInType.BOOLEAN.asQName();

    @Test
    public void testFillLiteralProps() {
        final LiteralExpression literalExpression = new LiteralExpression();
        final String contentValue = "Content value";
        literalExpression.setText(new Text(contentValue));

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(literalExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(LiteralProps.class);
        assertThat(((LiteralProps) expressionProps).content).isEqualTo(contentValue);
        assertThat(((LiteralProps) expressionProps).width).isEqualTo(literalExpression.getComponentWidths().get(0));
    }

    @Test
    public void testFillContextProps() {
        final Context contextExpression = new Context();
        contextExpression.getContextEntry().add(buildContextEntry(new LiteralExpression()));
        contextExpression.getContextEntry().add(buildResultEntry());

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(contextExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(ContextProps.class);
        assertThat(((ContextProps) expressionProps).contextEntries)
                .isNotNull()
                .hasSize(1);
        assertThat(((ContextProps) expressionProps).contextEntries[0]).isNotNull();
        assertThat(((ContextProps) expressionProps).contextEntries[0].entryInfo.name).isEqualTo(ENTRY_NAME);
        assertThat(((ContextProps) expressionProps).contextEntries[0].entryInfo.dataType).isEqualTo(ENTRY_DATA_TYPE.getLocalPart());
        assertThat(((ContextProps) expressionProps).result).isNotNull();
        assertThat(((ContextProps) expressionProps).result.logicType).isEqualTo(ExpressionType.LITERAL_EXPRESSION.getText());
        assertThat(((ContextProps) expressionProps).entryInfoWidth).isEqualTo(contextExpression.getComponentWidths().get(1));
        assertThat(((ContextProps) expressionProps).entryExpressionWidth).isEqualTo(contextExpression.getComponentWidths().get(2));
    }

    @Test
    public void testFillRelationProps() {
        final Relation relationExpression = new Relation();
        final String firstColumnId = "First Column id";
        final String firstColumnName = "First Column";
        final String firstColumnDescription = "First Description";
        final QName firstColumnDataType = BuiltInType.BOOLEAN.asQName();
        relationExpression.getColumn().add(new InformationItem(new Id(firstColumnId), new Description(firstColumnDescription), new Name(firstColumnName), firstColumnDataType));
        final String secondColumnId = "Second Column id";
        final String secondColumnName = "Second Column";
        final String secondColumnDescription = "Second Description";
        final QName secondColumnDataType = BuiltInType.NUMBER.asQName();
        relationExpression.getColumn().add(new InformationItem(new Id(secondColumnId), new Description(secondColumnDescription), new Name(secondColumnName), secondColumnDataType));
        final String firstCellValue = "first cell value";
        final String secondCellValue = "second cell value";
        relationExpression.getRow().add(buildListWithTwoLiteralExpressions(firstCellValue, secondCellValue));

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(relationExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(RelationProps.class);
        assertThat(((RelationProps) expressionProps).columns)
                .isNotNull()
                .hasSize(2);
        assertThat(((RelationProps) expressionProps).columns[0].name).isEqualTo(firstColumnName);
        assertThat(((RelationProps) expressionProps).columns[0].dataType).isEqualTo(firstColumnDataType.getLocalPart());
        assertThat(((RelationProps) expressionProps).columns[0].width).isEqualTo(relationExpression.getComponentWidths().get(1));
        assertThat(((RelationProps) expressionProps).columns[0].description).isEqualTo(firstColumnDescription);
        assertThat(((RelationProps) expressionProps).columns[1].name).isEqualTo(secondColumnName);
        assertThat(((RelationProps) expressionProps).columns[1].dataType).isEqualTo(secondColumnDataType.getLocalPart());
        assertThat(((RelationProps) expressionProps).columns[1].width).isEqualTo(relationExpression.getComponentWidths().get(2));
        assertThat(((RelationProps) expressionProps).columns[1].description).isEqualTo(secondColumnDescription);
        assertThat(((RelationProps) expressionProps).rows)
                .isNotNull()
                .hasSize(1);
        assertThat(((RelationProps) expressionProps).rows[0].cells).hasSize(2);
        assertThat(((RelationProps) expressionProps).rows[0].cells[0].content).isEqualTo(firstCellValue);
        assertThat(((RelationProps) expressionProps).rows[0].cells[1].content).isEqualTo(secondCellValue);
    }

    @Test
    public void testFillListProps() {
        final String firstValue = "first value";
        final String secondValue = "second value";
        final List listExpression = buildListWithTwoLiteralExpressions(firstValue, secondValue);

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(listExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(ListProps.class);
        assertThat(((ListProps) expressionProps).items)
                .isNotNull()
                .hasSize(2);
        final ExpressionProps firstItem = ((ListProps) expressionProps).items[0];
        assertThat(firstItem).isExactlyInstanceOf(LiteralProps.class);
        assertThat(((LiteralProps) firstItem).content).isEqualTo(firstValue);
        final ExpressionProps secondItem = ((ListProps) expressionProps).items[1];
        assertThat(secondItem).isExactlyInstanceOf(LiteralProps.class);
        assertThat(((LiteralProps) secondItem).content).isEqualTo(secondValue);

    }

    @Test
    public void testFillInvocationProps() {
        final Invocation invocationExpression = new Invocation();
        final LiteralExpression invokedFunctionExpression = new LiteralExpression();
        final String invokedFunctionText = "Invoked function text";
        final String invokedFunctionID = "ID-F";
        invokedFunctionExpression.setText(new Text(invokedFunctionText));
        invokedFunctionExpression.setId(new Id(invokedFunctionID));
        invocationExpression.setExpression(invokedFunctionExpression);
        final Binding binding = new Binding();
        binding.setVariable(buildVariable());
        binding.setExpression(new LiteralExpression());
        invocationExpression.getBinding().add(binding);

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(invocationExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(InvocationProps.class);
        assertThat(((InvocationProps) expressionProps).invokedFunction.name).isEqualTo(invokedFunctionText);
        assertThat(((InvocationProps) expressionProps).invokedFunction.id).isEqualTo(invokedFunctionID);
        assertThat(((InvocationProps) expressionProps).bindingEntries)
                .isNotNull()
                .hasSize(1);
        assertThat(((InvocationProps) expressionProps).bindingEntries[0].entryInfo).isNotNull();
        assertThat(((InvocationProps) expressionProps).bindingEntries[0].entryInfo.name).isEqualTo(ENTRY_NAME);
        assertThat(((InvocationProps) expressionProps).bindingEntries[0].entryInfo.dataType).isEqualTo(ENTRY_DATA_TYPE.getLocalPart());
        assertThat(((InvocationProps) expressionProps).bindingEntries[0].entryExpression)
                .isNotNull()
                .isExactlyInstanceOf(LiteralProps.class);
        assertThat(((InvocationProps) expressionProps).entryInfoWidth).isEqualTo(invocationExpression.getComponentWidths().get(1));
        assertThat(((InvocationProps) expressionProps).entryExpressionWidth).isEqualTo(invocationExpression.getComponentWidths().get(2));
    }

    @Test
    public void testFillPmmlFunctionProps() {
        final FunctionDefinition pmmlFunctionExpression = new FunctionDefinition();
        pmmlFunctionExpression.setKind(FunctionDefinition.Kind.PMML);
        pmmlFunctionExpression.getFormalParameter().add(buildVariable());
        final String documentName = "document name";
        final String modelName = "model name";
        final ContextEntry firstEntry = buildContextEntry(new LiteralExpressionPMMLDocument());
        ((LiteralExpressionPMMLDocument) firstEntry.getExpression()).setText(new Text(documentName));
        final ContextEntry secondEntry = buildContextEntry(new LiteralExpressionPMMLDocumentModel());
        ((LiteralExpressionPMMLDocumentModel) secondEntry.getExpression()).setText(new Text(modelName));
        pmmlFunctionExpression.setExpression(buildWrappedContext(firstEntry, secondEntry));

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(pmmlFunctionExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(PmmlFunctionProps.class);
        assertThat(((PmmlFunctionProps) expressionProps).functionKind).isEqualTo(FunctionDefinition.Kind.PMML.name());
        assertFormalParameters((PmmlFunctionProps) expressionProps);
        assertThat(((PmmlFunctionProps) expressionProps).document).isEqualTo(documentName);
        assertThat(((PmmlFunctionProps) expressionProps).model).isEqualTo(modelName);
        assertThat(((PmmlFunctionProps) expressionProps).classAndMethodNamesWidth).isEqualTo(pmmlFunctionExpression.getComponentWidths().get(1));
    }

    @Test
    public void testFillJavaFunctionProps() {
        final FunctionDefinition javaFunctionExpression = new FunctionDefinition();
        javaFunctionExpression.setKind(FunctionDefinition.Kind.JAVA);
        javaFunctionExpression.getFormalParameter().add(buildVariable());
        final String className = "class name";
        final String methodName = "method name";
        final ContextEntry firstEntry = buildContextEntry(new LiteralExpression());
        ((LiteralExpression) firstEntry.getExpression()).setText(new Text(className));
        final ContextEntry secondEntry = buildContextEntry(new LiteralExpression());
        ((LiteralExpression) secondEntry.getExpression()).setText(new Text(methodName));
        javaFunctionExpression.setExpression(buildWrappedContext(firstEntry, secondEntry));

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(javaFunctionExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(JavaFunctionProps.class);
        assertThat(((JavaFunctionProps) expressionProps).functionKind).isEqualTo("Java");
        assertFormalParameters((JavaFunctionProps) expressionProps);
        assertThat(((JavaFunctionProps) expressionProps).className).isEqualTo(className);
        assertThat(((JavaFunctionProps) expressionProps).methodName).isEqualTo(methodName);
        assertThat(((JavaFunctionProps) expressionProps).classAndMethodNamesWidth).isEqualTo(javaFunctionExpression.getComponentWidths().get(1));
    }

    @Test
    public void testFillFeelFunctionProps() {
        final FunctionDefinition feelFunctionExpression = new FunctionDefinition();
        feelFunctionExpression.setKind(FunctionDefinition.Kind.FEEL);
        feelFunctionExpression.getFormalParameter().add(buildVariable());
        feelFunctionExpression.setExpression(new Relation());

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(feelFunctionExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(FeelFunctionProps.class);
        assertThat(((FeelFunctionProps) expressionProps).functionKind).isEqualTo(FunctionDefinition.Kind.FEEL.name());
        assertFormalParameters((FeelFunctionProps) expressionProps);
        assertThat(((FeelFunctionProps) expressionProps).expression).isExactlyInstanceOf(RelationProps.class);
        assertThat(((FeelFunctionProps) expressionProps).classAndMethodNamesWidth).isEqualTo(feelFunctionExpression.getComponentWidths().get(1));
    }

    @Test
    public void testFillDecisionTableProps() {
        final DecisionTable decisionTableExpression = new DecisionTable();
        decisionTableExpression.setHitPolicy(HitPolicy.ANY);
        final String inputColumnName = "input column name";
        final String outputColumnName = "output column name";
        final String annotationColumnName = "annotation column name";
        final String annotationValue = "annotation value";
        final String inputValue = "input value";
        final String outputValue = "output value";
        final QName inputColumnTypeRef = BuiltInType.BOOLEAN.asQName();
        final QName outputColumnTypeRef = BuiltInType.STRING.asQName();
        buildDecisionTableColumns(decisionTableExpression, inputColumnName, outputColumnName, annotationColumnName, inputColumnTypeRef, outputColumnTypeRef);
        decisionTableExpression.getRule().add(buildDecisionRule(annotationValue, inputValue, outputValue));

        final ExpressionProps expressionProps = ExpressionPropsFiller.buildAndFillJsInteropProp(decisionTableExpression, null, null);

        assertThat(expressionProps)
                .isNotNull()
                .isExactlyInstanceOf(DecisionTableProps.class);
        assertThat(((DecisionTableProps) expressionProps).hitPolicy).isEqualTo(HitPolicy.ANY.value());
        assertThat(((DecisionTableProps) expressionProps).aggregation).isNullOrEmpty();
        assertThat(((DecisionTableProps) expressionProps).annotations)
                .isNotNull()
                .hasSize(1);
        assertThat(((DecisionTableProps) expressionProps).annotations[0].name).isEqualTo(annotationColumnName);
        assertThat(((DecisionTableProps) expressionProps).annotations[0].width).isEqualTo(decisionTableExpression.getComponentWidths().get(3));
        assertThat(((DecisionTableProps) expressionProps).output)
                .isNotNull()
                .hasSize(1);
        assertThat(((DecisionTableProps) expressionProps).output[0].name).isEqualTo(outputColumnName);
        assertThat(((DecisionTableProps) expressionProps).output[0].dataType).isEqualTo(outputColumnTypeRef.getLocalPart());
        assertThat(((DecisionTableProps) expressionProps).output[0].width).isEqualTo(decisionTableExpression.getComponentWidths().get(2));
        assertThat(((DecisionTableProps) expressionProps).input)
                .isNotNull()
                .hasSize(1);
        assertThat(((DecisionTableProps) expressionProps).input[0].name).isEqualTo(inputColumnName);
        assertThat(((DecisionTableProps) expressionProps).input[0].dataType).isEqualTo(inputColumnTypeRef.getLocalPart());
        assertThat(((DecisionTableProps) expressionProps).input[0].width).isEqualTo(decisionTableExpression.getComponentWidths().get(1));
    }

    private ContextEntry buildResultEntry() {
        final ContextEntry resultEntry = new ContextEntry();
        resultEntry.setExpression(new LiteralExpression());
        return resultEntry;
    }

    private ContextEntry buildContextEntry(final LiteralExpression wrappedEntry) {
        final ContextEntry entry = buildResultEntry();
        final InformationItem variable = buildVariable();
        entry.setVariable(variable);
        entry.setExpression(wrappedEntry);
        return entry;
    }

    private InformationItem buildVariable() {
        final InformationItem variable = new InformationItem();
        variable.setName(new Name(ENTRY_NAME));
        variable.setTypeRef(ENTRY_DATA_TYPE);
        return variable;
    }

    private List buildListWithTwoLiteralExpressions(final String firstCellValue, final String secondCellValue) {
        final List list = new List();
        final LiteralExpression firstCell = new LiteralExpression();
        firstCell.setText(new Text(firstCellValue));
        final LiteralExpression secondCell = new LiteralExpression();
        secondCell.setText(new Text(secondCellValue));
        list.getExpression().add(HasExpression.wrap(list.asDMNModelInstrumentedBase(), firstCell));
        list.getExpression().add(HasExpression.wrap(list.asDMNModelInstrumentedBase(), secondCell));
        return list;
    }

    private Context buildWrappedContext(final ContextEntry firstEntry, final ContextEntry secondEntry) {
        final Context wrappedContext = new Context();
        wrappedContext.getContextEntry().add(firstEntry);
        wrappedContext.getContextEntry().add(secondEntry);
        return wrappedContext;
    }

    private void assertFormalParameters(FunctionProps expressionProps) {
        assertThat(expressionProps.formalParameters)
                .isNotNull()
                .hasSize(1);
        assertThat(expressionProps.formalParameters[0].name).isEqualTo(ENTRY_NAME);
        assertThat(expressionProps.formalParameters[0].dataType).isEqualTo(ENTRY_DATA_TYPE.getLocalPart());
    }

    private void buildDecisionTableColumns(final DecisionTable decisionTableExpression, final String inputColumnName, final String outputColumnName, final String annotationColumnName, final QName inputColumnTypeRef, final QName outputColumnTypeRef) {
        final InputClause inputClause = new InputClause();
        final InputClauseLiteralExpression inputClauseLiteralExpression = new InputClauseLiteralExpression();
        inputClauseLiteralExpression.setText(new Text(inputColumnName));
        inputClauseLiteralExpression.setTypeRef(inputColumnTypeRef);
        inputClause.setInputExpression(inputClauseLiteralExpression);
        decisionTableExpression.getInput().add(inputClause);
        final OutputClause outputClause = new OutputClause();
        outputClause.setName(outputColumnName);
        outputClause.setTypeRef(outputColumnTypeRef);
        decisionTableExpression.getOutput().add(outputClause);
        final RuleAnnotationClause ruleAnnotationClause = new RuleAnnotationClause();
        ruleAnnotationClause.setName(new Name(annotationColumnName));
        decisionTableExpression.getAnnotations().add(ruleAnnotationClause);
    }

    private DecisionRule buildDecisionRule(final String annotationValue, final String inputValue, final String outputValue) {
        final DecisionRule decisionRule = new DecisionRule();
        final RuleAnnotationClauseText ruleAnnotationClauseText = new RuleAnnotationClauseText();
        ruleAnnotationClauseText.setText(new Text(annotationValue));
        decisionRule.getAnnotationEntry().add(ruleAnnotationClauseText);
        final UnaryTests unaryTests = new UnaryTests();
        unaryTests.setText(new Text(inputValue));
        decisionRule.getInputEntry().add(unaryTests);
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.setText(new Text(outputValue));
        decisionRule.getOutputEntry().add(literalExpression);
        return decisionRule;
    }
}
