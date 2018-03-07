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

package org.drools.workbench.screens.guided.rule.client.widget.operator;

import java.util.function.Function;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.editor.CEPOperatorsDropdown;
import org.drools.workbench.screens.guided.rule.client.editor.ConstraintValueEditor;
import org.drools.workbench.screens.guided.rule.client.editor.OperatorSelection;
import org.drools.workbench.screens.guided.rule.client.resources.GuidedRuleEditorResources;
import org.drools.workbench.screens.guided.rule.client.widget.FactPatternWidget;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.OperatorsOracle;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.resources.i18n.HumanReadableConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.client.callbacks.Callback;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@WithClassesToStub({CEPOperatorsDropdown.class, DateTimeFormat.class})
@RunWith(GwtMockitoTestRunner.class)
public class SingleFieldConstraintOperatorSelectorTest {

    @Mock
    private SingleFieldConstraint constraint;

    @Mock
    private ConstraintValueEditor constraintValueEditor;

    @Mock
    private FlexTable wrapper;

    private final int rowIndex = 123;

    private final int columnIndex = 456;

    @Mock
    private FactPatternWidget parent;

    @Mock
    private HorizontalPanel placeholderForDropdown;

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private Function<SingleFieldConstraint, ConstraintValueEditor> constraintValueEditorProducer;

    @Captor
    private ArgumentCaptor<Callback<String[]>> operatorsCallbackCaptor;

    @Captor
    private ArgumentCaptor<ValueChangeHandler<OperatorSelection>> operatorChangeHandlerCaptor;

    @Mock
    private CEPOperatorsDropdown operatorsDropdown;

    @Mock
    private OperatorSelection operatorSelection;

    @Mock
    private ValueChangeEvent<OperatorSelection> operatorValueChangeEvent;

    private SingleFieldConstraintOperatorSelector testedSelector;

    @Before
    public void setUp() throws Exception {
        testedSelector = spy(new SingleFieldConstraintOperatorSelector());
    }

    @Test
    public void testOperatorNotChanged() throws Exception {
        final String selectedOperator = "==";
        final String selectedOperatorDisplayText = HumanReadableConstants.INSTANCE.isEqualTo();
        final String originalOperator = "==";
        final String constraintValue = null;
        invokeHandlers(selectedOperator, selectedOperatorDisplayText, originalOperator, constraintValue);

        verify(constraint, never()).setOperator(anyString());
        verifyZeroInteractions(constraintValueEditor);
    }

    @Test
    public void testNothingSelected() throws Exception {
        final String selectedOperator = "";
        final String selectedOperatorDisplayText = "";
        final String originalOperator = "==";
        final String constraintValue = null;
        invokeHandlers(selectedOperator, selectedOperatorDisplayText, originalOperator, constraintValue);

        verify(constraint).setOperator(null);
        verify(constraintValueEditor, never()).showError();
        verify(constraintValueEditor).setVisible(false);
    }

    @Test
    public void testNewOperatorSelectedAndValueNeeded() throws Exception {
        final String selectedOperator = "!=";
        final String selectedOperatorDisplayText = HumanReadableConstants.INSTANCE.isNotEqualTo();
        final String originalOperator = "==";
        final String constraintValue = null;
        invokeHandlers(selectedOperator, selectedOperatorDisplayText, originalOperator, constraintValue);

        verify(constraint).setOperator(selectedOperator);
        verify(constraintValueEditor).showError();
        verify(constraintValueEditor).setVisible(true);
    }

    @Test
    public void testNewOperatorSelectedAndValuePresent() throws Exception {
        final String selectedOperator = "!=";
        final String selectedOperatorDisplayText = HumanReadableConstants.INSTANCE.isNotEqualTo();
        final String originalOperator = "==";
        final String constraintValue = "123";
        invokeHandlers(selectedOperator, selectedOperatorDisplayText, originalOperator, constraintValue);

        verify(constraint).setOperator(selectedOperator);
        verify(constraintValueEditor).hideError();
        verify(constraintValueEditor).setVisible(true);
    }

    @Test
    public void testNewOperatorSelectedButValueNotNeeded() throws Exception {
        final String selectedOperator = "!= null";
        final String selectedOperatorDisplayText = HumanReadableConstants.INSTANCE.isNotEqualToNull();
        final String originalOperator = "==";
        final String constraintValue = "";
        invokeHandlers(selectedOperator, selectedOperatorDisplayText, originalOperator, constraintValue);

        verify(constraint).setOperator(selectedOperator);
        verify(constraintValueEditor).hideError();
        verify(constraintValueEditor).setVisible(false);
    }

    private void invokeHandlers(final String selectedOperator,
                                final String selectedOperatorDisplayText,
                                final String originalOperator,
                                final String constraintValue) {
        final String factType = "Person";
        final String fieldName = "age";
        when(constraint.getFactType()).thenReturn(factType);
        when(constraint.getFieldName()).thenReturn(fieldName);
        when(constraint.getOperator()).thenReturn(originalOperator);
        when(constraint.getValue()).thenReturn(constraintValue);
        when(testedSelector.getNewOperatorDropdown(OperatorsOracle.STANDARD_OPERATORS, constraint))
                .thenReturn(operatorsDropdown);
        when(operatorValueChangeEvent.getValue()).thenReturn(operatorSelection);
        when(operatorSelection.getValue()).thenReturn(selectedOperator);
        when(operatorSelection.getDisplayText()).thenReturn(selectedOperatorDisplayText);
        when(wrapper.getWidget(rowIndex, columnIndex)).thenReturn(constraintValueEditor);

        testedSelector.configure(constraint,
                                 () -> constraintValueEditor,
                                 constraintValueEditorProducer,
                                 parent,
                                 placeholderForDropdown,
                                 wrapper,
                                 rowIndex,
                                 columnIndex,
                                 oracle);

        // test operators are loaded
        verify(oracle).getOperatorCompletions(eq(factType),
                                              eq(fieldName),
                                              operatorsCallbackCaptor.capture());

        // test dropdown shown to user
        operatorsCallbackCaptor.getValue().callback(OperatorsOracle.STANDARD_OPERATORS);
        verify(placeholderForDropdown).add(operatorsDropdown);
        verify(operatorsDropdown).addPlaceholder(GuidedRuleEditorResources.CONSTANTS.pleaseChoose(),
                                                 "");
        verify(operatorsDropdown).addValueChangeHandler(operatorChangeHandlerCaptor.capture());

        // test operator change is handled
        operatorChangeHandlerCaptor.getValue().onValueChange(operatorValueChangeEvent);
    }

    @Test
    public void testIsWidgetForValueNeededIsNull() throws Exception {
        final String operator = HumanReadableConstants.INSTANCE.isEqualToNull();
        Assertions.assertThat(testedSelector.isWidgetForValueNeeded(operator)).isFalse();
    }

    @Test
    public void testIsWidgetForValueNeededIsNotNull() throws Exception {
        final String operator = HumanReadableConstants.INSTANCE.isNotEqualToNull();
        Assertions.assertThat(testedSelector.isWidgetForValueNeeded(operator)).isFalse();
    }

    @Test
    public void testIsWidgetForValueNeededEmpty() throws Exception {
        final String operator = "";
        Assertions.assertThat(testedSelector.isWidgetForValueNeeded(operator)).isFalse();
    }

    @Test
    public void testIsWidgetForValueNeededIsEqualtTo() throws Exception {
        final String operator = HumanReadableConstants.INSTANCE.isEqualTo();
        Assertions.assertThat(testedSelector.isWidgetForValueNeeded(operator)).isTrue();
    }

    @Test
    public void testIsValueMissingOne() throws Exception {
        final String operator = "==";
        final String value = "";
        Assertions.assertThat(testedSelector.isValueMissing(operator, value)).isTrue();
    }

    @Test
    public void testIsValueMissingTwo() throws Exception {
        final String operator = "==";
        final String value = "123";
        Assertions.assertThat(testedSelector.isValueMissing(operator, value)).isFalse();
    }

    @Test
    public void testIsValueMissingThree() throws Exception {
        final String operator = "== null";
        final String value = "";
        Assertions.assertThat(testedSelector.isValueMissing(operator, value)).isFalse();
    }

    @Test
    public void testIsValueMissingFour() throws Exception {
        final String operator = "== null";
        final String value = "123";
        Assertions.assertThat(testedSelector.isValueMissing(operator, value)).isFalse();
    }
}
