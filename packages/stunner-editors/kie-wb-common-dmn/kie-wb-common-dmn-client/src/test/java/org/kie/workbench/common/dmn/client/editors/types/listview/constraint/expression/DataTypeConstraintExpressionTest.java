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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.expression;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.common.ConstraintPlaceholderHelper;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintExpressionTest {

    @Mock
    private DataTypeConstraintExpression.View view;

    @Mock
    private ConstraintPlaceholderHelper placeholderHelper;

    private DataTypeConstraintExpression constraintExpression;

    @Before
    public void setup() {
        constraintExpression = new DataTypeConstraintExpression(view, placeholderHelper);
    }

    @Test
    public void testSetup() {
        constraintExpression.setup();

        verify(view).init(constraintExpression);
    }

    @Test
    public void testGetValue() {

        final String expectedValue = "expression";

        when(view.getExpressionValue()).thenReturn(expectedValue);

        final String actualValue = constraintExpression.getValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetValue() {

        final String expression = "expression";

        constraintExpression.setValue(expression);

        verify(view).setExpressionValue(expression);
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final Element actualElement = constraintExpression.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testSetConstraintValueType() {

        final String type = "string";
        final String placeholder = "placeholder";

        when(placeholderHelper.getPlaceholderSentence(type)).thenReturn(placeholder);

        constraintExpression.setConstraintValueType(type);

        verify(view).setPlaceholder(placeholder);
    }
}
