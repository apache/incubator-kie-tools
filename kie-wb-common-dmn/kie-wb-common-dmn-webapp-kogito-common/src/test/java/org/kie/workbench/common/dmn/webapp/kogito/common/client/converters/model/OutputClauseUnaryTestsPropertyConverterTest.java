/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.QName;

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.OutputClauseUnaryTests;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITUnaryTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class OutputClauseUnaryTestsPropertyConverterTest {

    private static final String TEXT = "text";

    private static Function<Object, Map<QName, String>> attributesCast;

    @GwtMock
    @SuppressWarnings("unused")
    private JSITUnaryTests jsitUnaryTests;

    @BeforeClass
    public static void setupAttributesCast() {
        attributesCast = UnaryTestsPropertyConverter.ATTRIBUTES_CAST;

        UnaryTestsPropertyConverter.ATTRIBUTES_CAST = (o) -> new HashMap<>();
    }

    @AfterClass
    public static void restoreAttributesCast() {
        UnaryTestsPropertyConverter.ATTRIBUTES_CAST = attributesCast;
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
