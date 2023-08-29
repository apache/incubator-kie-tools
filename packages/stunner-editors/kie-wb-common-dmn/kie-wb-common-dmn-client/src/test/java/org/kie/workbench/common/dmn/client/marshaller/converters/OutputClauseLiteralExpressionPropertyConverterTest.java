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
package org.kie.workbench.common.dmn.client.marshaller.converters;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class OutputClauseLiteralExpressionPropertyConverterTest {

    private static final String TEXT = "text";

    private static final JSITLiteralExpression jsitLiteralExpression = mock(JSITLiteralExpression.class);

    @BeforeClass
    public static void setupAttributesCast() {
        LiteralExpressionPropertyConverter.LITERAL_EXPRESSION_PROVIDER = new LiteralExpressionPropertyConverter.JSITLiteralExpressionFactory() {
            @Override
            JSITLiteralExpression make() {
                return jsitLiteralExpression;
            }
        };
    }

    @AfterClass
    public static void restoreAttributesCast() {
        LiteralExpressionPropertyConverter.LITERAL_EXPRESSION_PROVIDER = new LiteralExpressionPropertyConverter.JSITLiteralExpressionFactory();
    }

    @Test
    public void testWBFromDMNWhenNull() {
        final OutputClauseLiteralExpression wb = OutputClauseLiteralExpressionPropertyConverter.wbFromDMN(null);
        assertThat(wb).isNotNull();
    }

    @Test
    public void testWBFromDMNWhenNonNull() {
        when(jsitLiteralExpression.getText()).thenReturn(TEXT);
        final OutputClauseLiteralExpression wb = OutputClauseLiteralExpressionPropertyConverter.wbFromDMN(jsitLiteralExpression);
        assertThat(wb).isNotNull();
        assertThat(wb.getText().getValue()).isEqualTo(TEXT);
    }

    @Test
    public void testDMNFromWBWhenNull() {
        final JSITLiteralExpression dmn = OutputClauseLiteralExpressionPropertyConverter.dmnFromWB(null);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenTextIsNull() {
        final OutputClauseLiteralExpression wb = new OutputClauseLiteralExpression();
        wb.setText(null);
        final JSITLiteralExpression dmn = OutputClauseLiteralExpressionPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenNonNullWithEmptyString() {
        final OutputClauseLiteralExpression wb = new OutputClauseLiteralExpression();
        wb.getText().setValue("");
        final JSITLiteralExpression dmn = OutputClauseLiteralExpressionPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenNonNullWithNonEmptyString() {
        final OutputClauseLiteralExpression wb = new OutputClauseLiteralExpression();
        wb.getText().setValue(TEXT);
        final JSITLiteralExpression dmn = OutputClauseLiteralExpressionPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNotNull();

        verify(jsitLiteralExpression).setText(TEXT);
    }
}
