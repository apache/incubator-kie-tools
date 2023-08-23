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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.function.BiConsumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintParserWarningEvent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.kie.workbench.common.dmn.client.editors.types.shortcuts.DataTypeShortcuts;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.ENUMERATION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.EXPRESSION;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.NONE;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.RANGE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.CONSTRAINT_INITIAL_VALUE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.WIDTH;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintModalTest {

    @Mock
    private DataTypeConstraintModal.View view;

    @Mock
    private DataTypeShortcuts dataTypeShortcuts;

    @Mock
    private DataTypeConstraintEnumeration constraintEnumeration;

    @Mock
    private DataTypeConstraintExpression constraintExpression;

    @Mock
    private DataTypeConstraintRange constraintRange;

    @Mock
    private Element element;

    private DataTypeConstraintModal modal;

    @Before
    public void setup() {
        modal = spy(new DataTypeConstraintModal(view, dataTypeShortcuts, constraintEnumeration, constraintExpression, constraintRange));
    }

    @Test
    public void testSetup() {

        doNothing().when(modal).superSetup();
        doNothing().when(modal).setWidth(WIDTH);

        modal.setup();

        verify(constraintRange).setModal(modal);
        verify(modal).superSetup();
        verify(modal).setWidth(WIDTH);
        verify(view).init(modal);
    }

    @Test
    public void testSave() {

        final DataTypeConstraintComponent constrainComponent = mock(DataTypeConstraintComponent.class);
        final String value = "value";

        doNothing().when(modal).doSave(Mockito.<String>any());
        doReturn(constrainComponent).when(modal).getCurrentComponent();
        when(constrainComponent.getValue()).thenReturn(value);

        modal.save();

        verify(modal).doSave(value);
    }

    @Test
    public void testClearAll() {

        doNothing().when(modal).doSave(Mockito.<String>any());

        modal.clearAll();

        verify(modal).doSave(CONSTRAINT_INITIAL_VALUE);
    }

    @Test
    public void testDoSave() {

        final String expectedConstraint = "1,2,3";
        final ConstraintType expectedConstraintType = ENUMERATION;
        final BiConsumer<String, ConstraintType> onSave = mock(BiConsumer.class);

        doNothing().when(modal).hide();
        doReturn(onSave).when(modal).getOnSave();
        modal.setConstraintType(expectedConstraintType);

        modal.doSave(expectedConstraint);

        final String actualConstraint = modal.getConstraintValue();

        assertEquals(expectedConstraint, actualConstraint);
        verify(onSave).accept(expectedConstraint, expectedConstraintType);
        verify(modal).hide();
    }

    @Test
    public void testLoadWhenConstraintTypeIsNone() {

        final String expectedConstraintValueType = "string";
        final String expectedConstraintValue = "[1..3]";
        final ConstraintType expectedConstraintType = RANGE;

        final DataType expectedDataType = mock(DataType.class);
        doReturn(expectedConstraintValue).when(expectedDataType).getConstraint();
        doReturn(expectedConstraintType).when(expectedDataType).getConstraintType();

        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        doReturn(expectedDataType).when(dataTypeListItem).getDataType();
        doReturn(expectedConstraintValueType).when(dataTypeListItem).getType();

        doReturn(expectedConstraintType).when(modal).inferComponentType(expectedConstraintValue);

        modal.load(dataTypeListItem);

        final String actualConstraintValueType = modal.getConstraintValueType();
        final String actualConstraintValue = modal.getConstraintValue();
        final ConstraintType actualConstraintType = modal.getConstraintType();

        verify(modal).prepareView();
        assertEquals(expectedConstraintValueType, actualConstraintValueType);
        assertEquals(expectedConstraintValue, actualConstraintValue);
        assertEquals(expectedConstraintType, actualConstraintType);
    }

    @Test
    public void testLoadWhenConstraintTypeIsNotNone() {

        final String expectedConstraintValueType = "string";
        final String expectedConstraintValue = "1,2,3";
        final ConstraintType expectedConstraintType = ENUMERATION;

        final DataType expectedDataType = mock(DataType.class);
        doReturn(expectedConstraintValue).when(expectedDataType).getConstraint();
        doReturn(expectedConstraintType).when(expectedDataType).getConstraintType();

        final DataTypeListItem dataTypeListItem = mock(DataTypeListItem.class);
        doReturn(expectedDataType).when(dataTypeListItem).getDataType();
        doReturn(expectedConstraintValueType).when(dataTypeListItem).getType();

        modal.load(dataTypeListItem);

        final String actualConstraintValueType = modal.getConstraintValueType();
        final String actualConstraintValue = modal.getConstraintValue();
        final ConstraintType actualConstraintType = modal.getConstraintType();

        verify(modal).prepareView();
        assertEquals(expectedConstraintValueType, actualConstraintValueType);
        assertEquals(expectedConstraintValue, actualConstraintValue);
        assertEquals(expectedConstraintType, actualConstraintType);
    }

    @Test
    public void testPrepareView() {

        final String type = "string";
        final String constraint = "1,2,3";

        doReturn(type).when(modal).getConstraintValueType();
        doReturn(ENUMERATION).when(modal).getConstraintType();
        doReturn(constraint).when(modal).getConstraintValue();

        modal.prepareView();

        verify(view).setType(type);
        verify(view).loadComponent(ENUMERATION);
    }

    @Test
    public void testPrepareViewWhenConstraintValueIsBlank() {

        final String type = "string";
        final String constraint = "";

        doReturn(type).when(modal).getConstraintValueType();
        doReturn(constraint).when(modal).getConstraintValue();

        modal.prepareView();

        verify(view).setType(type);
        verify(view).setupEmptyContainer();
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsExpression() {

        final ConstraintType type = EXPRESSION;
        final String constraint = "expression";
        final String constraintValueType = "string";

        doReturn(constraint).when(modal).getConstraintValue();
        doReturn(constraintValueType).when(modal).getConstraintValueType();
        when(constraintExpression.getElement()).thenReturn(element);

        modal.setupComponent(type);

        assertEquals(constraintExpression, modal.getCurrentComponent());

        final InOrder inOrder = inOrder(constraintExpression);
        inOrder.verify(constraintExpression).setConstraintValueType(constraintValueType);
        inOrder.verify(constraintExpression).setValue(constraint);

        verify(modal).enableOkButton();
        verify(element).setAttribute("class", "kie-string");
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsRange() {

        final ConstraintType type = RANGE;
        final String constraint = "(1..2)";
        final String constraintValueType = "number";

        doReturn(constraint).when(modal).getConstraintValue();
        doReturn(constraintValueType).when(modal).getConstraintValueType();
        when(constraintRange.getElement()).thenReturn(element);

        modal.setupComponent(type);

        assertEquals(constraintRange, modal.getCurrentComponent());

        final InOrder inOrder = inOrder(constraintRange);
        inOrder.verify(constraintRange).setConstraintValueType(constraintValueType);
        inOrder.verify(constraintRange).setValue(constraint);

        verify(element).setAttribute("class", "kie-number");
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsRangeAndValueIsEmpty() {

        final ConstraintType type = RANGE;
        final String constraint = "";
        final String constraintValueType = "number";

        doReturn(constraint).when(modal).getConstraintValue();
        doReturn(constraintValueType).when(modal).getConstraintValueType();
        when(constraintRange.getElement()).thenReturn(element);

        modal.setupComponent(type);

        assertEquals(constraintRange, modal.getCurrentComponent());

        final InOrder inOrder = inOrder(constraintRange);
        inOrder.verify(constraintRange).setConstraintValueType(constraintValueType);
        inOrder.verify(constraintRange).setValue(constraint);

        verify(modal, never()).enableOkButton();
        verify(element).setAttribute("class", "kie-number");
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsNone() {

        final ConstraintType type = NONE;
        final String constraint = "(1..2)";
        final String constraintValueType = "number";

        doReturn(ENUMERATION).when(modal).inferComponentType(constraint);
        doReturn(constraint).when(modal).getConstraintValue();
        doReturn(constraintValueType).when(modal).getConstraintValueType();
        when(constraintEnumeration.getElement()).thenReturn(element);

        modal.setupComponent(type);

        assertEquals(constraintEnumeration, modal.getCurrentComponent());

        final InOrder inOrder = inOrder(constraintEnumeration);
        inOrder.verify(constraintEnumeration).setConstraintValueType(constraintValueType);
        inOrder.verify(constraintEnumeration).setValue(constraint);

        verify(element).setAttribute("class", "kie-number");
    }

    @Test
    public void testIsNoneWhenConstraintTypeIsENUMERATION() {
        assertFalse(modal.isNone(ENUMERATION));
    }

    @Test
    public void testIsNoneWhenConstraintTypeIsNONE() {
        assertTrue(modal.isNone(NONE));
    }

    @Test
    public void testIsNoneWhenConstraintTypeIsNull() {
        assertTrue(modal.isNone(null));
    }

    @Test
    public void testInferComponentTypeWhenItReturnsRange() {

        final ConstraintType expectedType = RANGE;
        final ConstraintType actualType = modal.inferComponentType("(1..3]");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testInferComponentTypeWhenItReturnsEnumeration() {

        final ConstraintType expectedType = ENUMERATION;
        final ConstraintType actualType = modal.inferComponentType("1,2,3");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testInferComponentTypeWhenItReturnsExpression() {

        final ConstraintType expectedType = EXPRESSION;
        final ConstraintType actualType = modal.inferComponentType("expression");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testShow() {

        final BiConsumer<String, ConstraintType> expectedOnSave = (s, c) -> { /* Nothing. */ };
        final ArgumentCaptor<Command> onHide = ArgumentCaptor.forClass(Command.class);

        doNothing().when(modal).superShow();

        modal.show(expectedOnSave);

        final BiConsumer<String, ConstraintType> actualOnSave = modal.getOnSave();

        assertEquals(expectedOnSave, actualOnSave);
        verify(modal).superShow();
        verify(dataTypeShortcuts).disable();
        verify(view).onShow();
        verify(view).setupOnHideHandler(onHide.capture());
        onHide.getValue().execute();
        verify(modal).onHide();
    }

    @Test
    public void testHide() {

        doNothing().when(modal).superHide();

        modal.hide();

        verify(modal).superHide();
        verify(dataTypeShortcuts).enable();
    }

    @Test
    public void testOnDataTypeConstraintParserWarningEvent() {

        modal.onDataTypeConstraintParserWarningEvent(mock(DataTypeConstraintParserWarningEvent.class));

        verify(view).showConstraintWarningMessage();
    }

    @Test
    public void testEnableOkButton() {
        modal.enableOkButton();
        verify(view).enableOkButton();
    }

    @Test
    public void testDisableOkButton() {
        modal.disableOkButton();
        verify(view).disableOkButton();
    }
}
