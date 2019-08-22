/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.drools.workbench.screens.guided.rule.client.editor;

import java.util.HashMap;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.assertj.core.api.Assertions;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.CompositeFieldConstraint;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.screens.guided.rule.client.widget.EnumDropDown;
import org.guvnor.common.services.workingset.client.WorkingSetManager;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.kie.workbench.common.widgets.client.widget.LiteralTextBox;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.services.shared.preferences.ApplicationPreferences.DATE_FORMAT;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WithClassesToStub(AddConstraintButton.class)
@RunWith(GwtMockitoTestRunner.class)
public class ConstraintValueEditorTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private SingleFieldConstraint constraint;

    @Mock
    private WorkingSetManager wsManager;

    @GwtMock
    private RuleModeller ruleModeller;

    @Mock
    private TemplateKeyTextBox templateKeyTextBox;

    @Before
    public void setup() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DATE_FORMAT,
                "dd MMM yyyy");
        ApplicationPreferences.setUp(map);

        when(ruleModeller.getDataModelOracle()).thenReturn(oracle);
        when(oracle.getFieldType(anyString(),
                                 anyString())).thenReturn(DataType.TYPE_STRING);
        when(constraint.getConstraintValueType()).thenReturn(SingleFieldConstraint.TYPE_LITERAL);
    }

    @Test
    public void correctWidgetForStringField() {
        ConstraintValueEditor editor = createEditor(constraint);
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    @Test
    public void correctWidgetForStringFieldValueInList() {
        when(constraint.getOperator()).thenReturn("in");
        ConstraintValueEditor editor = createEditor(constraint);
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    @Test
    public void correctWidgetForStringFieldValueNotInList() {
        when(constraint.getOperator()).thenReturn("not in");
        ConstraintValueEditor editor = createEditor(constraint);
        assertTrue(editor.getConstraintWidget() instanceof LiteralTextBox);
    }

    @Test
    public void defaultTextBoxHasHandlersAttachedInCorrectOrder() {
        final TextBox defaultTextBox = mock(TextBox.class);
        final ConstraintValueEditor editor = spy(createEditor(constraint));
        final InOrder inOrder = inOrder(editor);

        doReturn(defaultTextBox).when(editor).getDefaultTextBox(any(String.class));

        editor.getNewTextBox(DataType.TYPE_STRING);

        inOrder.verify(editor).setUpTextBoxStyleAndHandlers(eq(defaultTextBox),
                                                            any(Command.class));
        verify(defaultTextBox,
               times(1)).setText(any(String.class));
        inOrder.verify(editor).attachDisplayLengthHandler(eq(defaultTextBox));
    }

    @Test
    public void templateKeyEditorHasHandlersAttachedInCorrectOrder() {
        final ConstraintValueEditor editor = spy(createEditor(constraint));

        final InOrder inOrder = inOrder(editor);

        editor.templateKeyEditor();

        inOrder.verify(editor).setUpTextBoxStyleAndHandlers(eq(templateKeyTextBox),
                                                            any(Command.class));
        verify(templateKeyTextBox,
               times(1)).setValue(any(String.class),
                                  any(Boolean.class));
        inOrder.verify(editor).attachDisplayLengthHandler(eq(templateKeyTextBox));
    }

    @Test
    public void testGetDrowpDown() {
        final String factType = "Car";
        final String factField = "color";
        final SingleFieldConstraint singleFieldConstraint = new SingleFieldConstraint() {{
            setFactType(factType);
            setFieldName(factField);
        }};
        final ConstraintValueEditor editor = spy(createEditor(singleFieldConstraint));

        // reset oracle due to calls in ConstraintValueEditor constructor
        reset(oracle);
        editor.initDropDownData();

        verify(oracle).getEnums(eq(factType), eq(factField), anyMap());
    }

    @Test
    public void testInitDropDown_BooleanFieldType() {
        final String factType = "Car";
        final String factField = "cheap";

        // reset oracle due to calls in @Before method
        reset(oracle);

        when(oracle.getFieldType(factType, factField)).thenReturn(DataType.TYPE_BOOLEAN);

        final SingleFieldConstraint singleFieldConstraint = new SingleFieldConstraint() {{
            setFactType(factType);
            setFieldName(factField);
            setConstraintValueType(TYPE_LITERAL);
        }};
        final ConstraintValueEditor editor = spy(createEditor(singleFieldConstraint));

        // EnumDropDown should be created because no enumeration loaded and field is Boolean type
        editor.initDropDownData();
        editor.refresh();
        Assertions.assertThat(editor.getConstraintWidget()).isInstanceOf(EnumDropDown.class);
    }

    @Test
    public void testInitializationOrder() {
        // DROOLS-2662 and DROOLS-2781
        // Create the dropdown before constraint value editor helper.
        final SingleFieldConstraint singleFieldConstraint = mock(SingleFieldConstraint.class);
        final ConstraintValueEditor editor = spy(createEditor(singleFieldConstraint));

        editor.init();

        final InOrder inOrder = Mockito.inOrder(editor);
        inOrder.verify(editor).initDropDownData();
        inOrder.verify(editor).constructConstraintValueEditorHelper();
        inOrder.verify(editor).refresh();
    }

    private ConstraintValueEditor createEditor(final BaseSingleFieldConstraint baseConstraint) {
        final ConstraintValueEditor editor = new ConstraintValueEditor(baseConstraint,
                                                                       mock(CompositeFieldConstraint.class),
                                                                       ruleModeller,
                                                                       mock(EventBus.class),
                                                                       false) {
            @Override
            void setBoxSize(final TextBox box) {
                //do nothing - avoid calling JavaScriptObject.cast()
            }

            @Override
            Widget wrap(Widget widget) {
                return widget;
            }

            @Override
            WorkingSetManager getWorkingSetManager() {
                return wsManager;
            }

            @Override
            TemplateKeyTextBox getTemplateKeyTextBox() {
                return templateKeyTextBox;
            }
        };

        editor.init();

        return editor;
    }
}