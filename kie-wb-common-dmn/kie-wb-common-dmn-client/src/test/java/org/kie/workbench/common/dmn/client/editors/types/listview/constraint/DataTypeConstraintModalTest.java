/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent.Type;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.DataTypeConstraintEnumeration;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression.DataTypeConstraintExpression;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.range.DataTypeConstraintRange;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.CONSTRAINT_INITIAL_VALUE;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraintModal.WIDTH;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent.Type.ENUMERATION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent.Type.EXPRESSION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.DataTypeConstraintComponent.Type.RANGE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintModalTest {

    @Mock
    private DataTypeConstraintModal.View view;

    @Mock
    private DataTypeConstraintEnumeration constraintEnumeration;

    @Mock
    private DataTypeConstraintExpression constraintExpression;

    @Mock
    private DataTypeConstraintRange constraintRange;

    private DataTypeConstraintModal modal;

    @Before
    public void setup() {
        modal = spy(new DataTypeConstraintModal(view, constraintEnumeration, constraintExpression, constraintRange));
    }

    @Test
    public void testSetup() {

        doNothing().when(modal).superSetup();
        doNothing().when(modal).setWidth(WIDTH);

        modal.setup();

        verify(modal).superSetup();
        verify(modal).setWidth(WIDTH);
        verify(view).init(modal);
    }

    @Test
    public void testSave() {

        final DataTypeConstraintComponent constrainComponent = mock(DataTypeConstraintComponent.class);
        final String value = "value";

        doNothing().when(modal).doSave(anyString());
        doReturn(constrainComponent).when(modal).getCurrentComponent();
        when(constrainComponent.getValue()).thenReturn(value);

        modal.save();

        verify(modal).doSave(value);
    }

    @Test
    public void testClearAll() {

        doNothing().when(modal).doSave(anyString());

        modal.clearAll();

        verify(modal).doSave(CONSTRAINT_INITIAL_VALUE);
    }

    @Test
    public void testDoSave() {

        final String expectedConstraint = "1,2,3";
        final Consumer<String> onSave = mock(Consumer.class);

        doNothing().when(modal).hide();
        doReturn(onSave).when(modal).getOnSave();

        modal.doSave(expectedConstraint);

        final String actualConstraint = modal.getConstraintValue();

        assertEquals(expectedConstraint, actualConstraint);
        verify(onSave).accept(expectedConstraint);
        verify(modal).hide();
    }

    @Test
    public void testLoad() {

        final String constraint = "1,2,3";
        final String type = "string";

        modal.load(type, constraint);

        verify(modal).prepareView(type, constraint);
    }

    @Test
    public void testPrepareViewWhenConstraintValueIsBlank() {

        final String constraint = "";
        final String type = "string";

        modal.prepareView(type, constraint);

        verify(view).setType(type);
        verify(view).setupEmptyContainer();
    }

    @Test
    public void testPrepareViewWhenConstraintValueIsNotBlank() {

        final String constraint = "1,2,3";
        final String type = "string";

        modal.prepareView(type, constraint);

        verify(view).setType(type);
        verify(view).loadComponent(ENUMERATION.name());
    }

    @Test
    public void testPrepareViewWhenConstraintValueSimilarToRange() {

        final String constraint = "..";
        final String type = "string";

        modal.prepareView(type, constraint);

        verify(view).setType(type);
        verify(view).loadComponent(RANGE.name());
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsEnumeration() {

        final String type = ENUMERATION.name();
        final String constraint = "1,2,3";

        doReturn(constraint).when(modal).getConstraintValue();

        modal.setupComponent(type);

        verify(constraintEnumeration).setValue(constraint);
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsExpression() {

        final String type = EXPRESSION.name();
        final String constraint = "expression";

        doReturn(constraint).when(modal).getConstraintValue();

        modal.setupComponent(type);

        verify(constraintExpression).setValue(constraint);
    }

    @Test
    public void testSetupComponentWhenConstraintTypeIsRange() {

        final String type = RANGE.name();
        final String constraint = "(1..2)";

        doReturn(constraint).when(modal).getConstraintValue();

        modal.setupComponent(type);

        verify(constraintRange).setValue(constraint);
    }

    @Test
    public void testInferComponentTypeWhenItReturnsRange() {

        final Type expectedType = RANGE;
        final Type actualType = modal.inferComponentType("(1..3]");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testInferComponentTypeWhenItReturnsEnumeration() {

        final Type expectedType = ENUMERATION;
        final Type actualType = modal.inferComponentType("1,2,3");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testInferComponentTypeWhenItReturnsExpression() {

        final Type expectedType = EXPRESSION;
        final Type actualType = modal.inferComponentType("expression");

        assertEquals(expectedType, actualType);
    }

    @Test
    public void testShow() {

        final Consumer<String> expectedOnSave = (s) -> { /* Nothing. */ };

        doNothing().when(modal).superShow();

        modal.show(expectedOnSave);

        final Consumer<String> actualOnSave = modal.getOnSave();

        assertEquals(expectedOnSave, actualOnSave);
        verify(modal).superShow();
        verify(view).onShow();
    }
}
