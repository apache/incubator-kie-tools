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

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class OutputClauseUnaryTestsPropertyConverterTest {

    private static final String TEXT = "text";

    private static final JSITUnaryTests jsitUnaryTests = mock(JSITUnaryTests.class);

    @BeforeClass
    public static void setupAttributesCast() {

        UnaryTestsPropertyConverter.UNARY_TESTS_FACTORY = new UnaryTestsPropertyConverter.UnaryTestsFactory() {

            @Override
            JSITUnaryTests make() {
                return jsitUnaryTests;
            }
        };

        UnaryTestsPropertyConverter.ATTRIBUTES_UTILS = new UnaryTestsPropertyConverter.AttributesUtils() {

            @Override
            Map<QName, String> cast(final Map<QName, String> o) {
                return new HashMap<>();
            }
        };
    }

    @AfterClass
    public static void restoreAttributesCast() {
        UnaryTestsPropertyConverter.UNARY_TESTS_FACTORY = new UnaryTestsPropertyConverter.UnaryTestsFactory();
        UnaryTestsPropertyConverter.ATTRIBUTES_UTILS = new UnaryTestsPropertyConverter.AttributesUtils();
    }

    @Test
    public void testWBFromDMNWhenNull() {
        final OutputClauseUnaryTests wb = OutputClauseUnaryTestsPropertyConverter.wbFromDMN(null);
        assertThat(wb).isNotNull();
    }

    @Test
    public void testWBFromDMNWhenNonNull() {
        when(jsitUnaryTests.getText()).thenReturn(TEXT);
        final OutputClauseUnaryTests wb = OutputClauseUnaryTestsPropertyConverter.wbFromDMN(jsitUnaryTests);
        assertThat(wb).isNotNull();
        assertThat(wb.getText().getValue()).isEqualTo(TEXT);
    }

    @Test
    public void testDMNFromWBWhenNull() {
        final JSITUnaryTests dmn = OutputClauseUnaryTestsPropertyConverter.dmnFromWB(null);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenTextIsNull() {
        final OutputClauseUnaryTests wb = new OutputClauseUnaryTests();
        wb.setText(null);
        final JSITUnaryTests dmn = OutputClauseUnaryTestsPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenNonNullWithEmptyString() {
        final OutputClauseUnaryTests wb = new OutputClauseUnaryTests();
        wb.getText().setValue("");
        final JSITUnaryTests dmn = OutputClauseUnaryTestsPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNull();
    }

    @Test
    public void testDMNFromWBWhenNonNullWithNonEmptyString() {
        final OutputClauseUnaryTests wb = new OutputClauseUnaryTests();
        wb.getText().setValue(TEXT);
        final JSITUnaryTests dmn = OutputClauseUnaryTestsPropertyConverter.dmnFromWB(wb);
        assertThat(dmn).isNotNull();

        verify(jsitUnaryTests).setText(TEXT);
    }
}
