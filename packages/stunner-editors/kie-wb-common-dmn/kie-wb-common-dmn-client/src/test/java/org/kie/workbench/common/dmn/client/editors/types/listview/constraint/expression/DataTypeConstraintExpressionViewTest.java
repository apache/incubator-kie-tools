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
import elemental2.dom.HTMLTextAreaElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintExpressionViewTest {

    @Mock
    private HTMLTextAreaElement expression;

    @Mock
    private DataTypeConstraintExpression presenter;

    private DataTypeConstraintExpressionView view;

    @Before
    public void Setup() {
        view = new DataTypeConstraintExpressionView(expression);
    }

    @Test
    public void testGetExpressionValue() {

        final String expectedValue = "expression";

        expression.value = expectedValue;

        final String actualValue = view.getExpressionValue();

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetExpressionValue() {

        final String expectedValue = "expression";

        view.setExpressionValue(expectedValue);

        final String actualValue = expression.value;

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testSetPlaceholder() {

        final String attribute = "placeholder";
        final String value = "value";

        view.setPlaceholder(value);

        verify(expression).setAttribute(attribute, value);
    }
}
