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

import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ImportedValues;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImportedValues;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.dmn.api.definition.model.DMNModelInstrumentedBase.Namespace.KIE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class LiteralExpressionPropertyConverterTest {

    private static final String UUID = "uuid";

    private static final String TEXT = "text";

    private static final String DESCRIPTION = "description";

    private static final String IMPORTED_ELEMENT = "imported-element";

    private static final String EXPRESSION_LANGUAGE = "expression-language";

    private static final String TYPE_REF = "type-ref";

    @GwtMock
    @SuppressWarnings("unused")
    private LiteralExpression wb;

    @GwtMock
    @SuppressWarnings("unused")
    private JSITImportedValues jsitImportedValues;

    @GwtMock
    @SuppressWarnings("unused")
    private ImportedValues importedValues;

    private static final JSITLiteralExpression literalExpression = mock(JSITLiteralExpression.class);

    @BeforeClass
    public static void setupAttributesCast() {
        LiteralExpressionPropertyConverter.LITERAL_EXPRESSION_PROVIDER = new LiteralExpressionPropertyConverter.JSITLiteralExpressionFactory() {
            @Override
            JSITLiteralExpression make() {
                return literalExpression;
            }
        };
    }

    @AfterClass
    public static void restoreAttributesCast() {
        LiteralExpressionPropertyConverter.LITERAL_EXPRESSION_PROVIDER = new LiteralExpressionPropertyConverter.JSITLiteralExpressionFactory();
    }

    @Test
    public void testWBFromDMN() {
        when(literalExpression.getId()).thenReturn(UUID);
        when(literalExpression.getDescription()).thenReturn(DESCRIPTION);
        when(literalExpression.getTypeRef()).thenReturn(TYPE_REF);
        when(literalExpression.getText()).thenReturn(TEXT);
        when(literalExpression.getExpressionLanguage()).thenReturn(EXPRESSION_LANGUAGE);
        when(literalExpression.getImportedValues()).thenReturn(jsitImportedValues);
        when(jsitImportedValues.getImportedElement()).thenReturn(IMPORTED_ELEMENT);

        final LiteralExpression result = LiteralExpressionPropertyConverter.wbFromDMN(literalExpression);

        assertThat(result.getId().getValue()).isEqualTo(UUID);
        assertThat(result.getDescription().getValue()).isEqualTo(DESCRIPTION);
        assertThat(result.getTypeRef().getNamespaceURI()).isEmpty();
        assertThat(result.getTypeRef().getLocalPart()).isEqualTo(TYPE_REF);
        assertThat(result.getText().getValue()).isEqualTo(TEXT);
        assertThat(result.getExpressionLanguage().getValue()).isEqualTo(EXPRESSION_LANGUAGE);
        assertThat(result.getImportedValues().getImportedElement()).isEqualTo(IMPORTED_ELEMENT);
        assertThat(result.getImportedValues().getParent()).isEqualTo(result);
    }

    @Test
    public void testDMNFromWB() {
        when(wb.getId()).thenReturn(new Id(UUID));
        when(wb.getDescription()).thenReturn(new Description(DESCRIPTION));
        when(wb.getTypeRef()).thenReturn(new QName(KIE.getUri(), TYPE_REF, KIE.getPrefix()));
        when(wb.getText()).thenReturn(new Text(TEXT));
        when(wb.getExpressionLanguage()).thenReturn(new ExpressionLanguage(EXPRESSION_LANGUAGE));

        final JSITLiteralExpression result = LiteralExpressionPropertyConverter.dmnFromWB(wb);

        verify(result).setId(UUID);
        verify(result).setDescription(DESCRIPTION);
        verify(result).setTypeRef("{" + KIE.getUri() + "}" + TYPE_REF);
        verify(result).setText(TEXT);
        verify(result).setExpressionLanguage(EXPRESSION_LANGUAGE);
    }
}
