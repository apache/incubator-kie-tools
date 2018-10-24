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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintTest {

    @Mock
    private DataTypeConstraint.View view;

    private DataTypeConstraint constraintComponent;

    @Before
    public void setup() {
        constraintComponent = spy(new DataTypeConstraint(view));
    }

    @Test
    public void testInit() {

        final DataType expectedDataType = mock(DataType.class);

        constraintComponent.init(expectedDataType);

        final DataType actualDataType = constraintComponent.getDataType();

        assertEquals(expectedDataType, actualDataType);
        verify(constraintComponent).refreshView();
    }

    @Test
    public void testRefreshView() {

        doNothing().when(constraintComponent).updateConstraintInput();
        doNothing().when(constraintComponent).toggleConstraintInput();

        constraintComponent.refreshView();

        verify(constraintComponent).updateConstraintInput();
        verify(constraintComponent).toggleConstraintInput();
    }

    @Test
    public void testUpdateConstraintInput() {

    }

    @Test
    public void testToggleConstraintInputWhenDataTypeHasConstraint() {

        final DataType dataType = mock(DataType.class);
        final String constraint = "(1..26)";

        when(dataType.getConstraint()).thenReturn(constraint);
        doReturn(dataType).when(constraintComponent).getDataType();

        constraintComponent.toggleConstraintInput();

        verify(view).enableConstraint();
    }

    @Test
    public void testToggleConstraintInputWhenDataTypeDoesNotHaveConstraint() {

        final DataType dataType = mock(DataType.class);

        when(dataType.getConstraint()).thenReturn(null);
        doReturn(dataType).when(constraintComponent).getDataType();

        constraintComponent.toggleConstraintInput();

        verify(view).disableConstraint();
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "value";
        when(view.getConstraintValue()).thenReturn(expectedValue);

        final String actualValue = constraintComponent.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);
        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = constraintComponent.getElement();

        assertEquals(expectedElement, actualElement);
    }
}
