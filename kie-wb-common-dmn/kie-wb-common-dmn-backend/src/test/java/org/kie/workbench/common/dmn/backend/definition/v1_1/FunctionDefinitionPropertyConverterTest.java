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
package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.FunctionKind;
import org.kie.dmn.model.v1_2.TContext;
import org.kie.dmn.model.v1_2.TContextEntry;
import org.kie.dmn.model.v1_2.TFunctionDefinition;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;
import org.kie.workbench.common.dmn.api.definition.model.IsLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocument;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpressionPMMLDocumentModel;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FunctionDefinitionPropertyConverterTest {

    private static final String FUNCTION_DEFINITION_UUID = "fd-uuid";

    private static final String FUNCTION_DEFINITION_DESCRIPTION = "fd-description";

    private static final String FUNCTION_DEFINITION_QNAME_LOCALPART = "fd-local";

    private static final String EXPRESSION_UUID = "uuid";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<ComponentWidths> componentWidthsConsumer;

    @Captor
    private ArgumentCaptor<HasComponentWidths> hasComponentWidthsCaptor;

    @Captor
    private ArgumentCaptor<ComponentWidths> componentWidthsCaptor;

    @Test
    public void testWBFromDMN() {
        final org.kie.dmn.model.api.FunctionDefinition dmn = new TFunctionDefinition();
        final org.kie.dmn.model.api.LiteralExpression literalExpression = new TLiteralExpression();
        literalExpression.setId(EXPRESSION_UUID);

        dmn.setId(FUNCTION_DEFINITION_UUID);
        dmn.setDescription(FUNCTION_DEFINITION_DESCRIPTION);
        dmn.setTypeRef(new QName(FUNCTION_DEFINITION_QNAME_LOCALPART));
        dmn.setKind(FunctionKind.JAVA);
        dmn.setExpression(literalExpression);

        final FunctionDefinition wb = FunctionDefinitionPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(FUNCTION_DEFINITION_UUID);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(FUNCTION_DEFINITION_DESCRIPTION);
        assertThat(wb.getTypeRef()).isNotNull();
        assertThat(wb.getTypeRef().getLocalPart()).isEqualTo(FUNCTION_DEFINITION_QNAME_LOCALPART);
        assertThat(wb.getKind()).isNotNull();
        assertThat(wb.getKind()).isEqualTo(FunctionDefinition.Kind.JAVA);
        assertThat(wb.getExpression()).isNotNull();
        assertThat(wb.getExpression().getId().getValue()).isEqualTo(EXPRESSION_UUID);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getExpression());
    }

    @Test
    public void testWBFromDMNWithJavaContext() {
        doTestWBFromDMNWithContextEntry(FunctionKind.JAVA,
                                        "cheese",
                                        LiteralExpression.class);
    }

    @Test
    public void testWBFromDMNWithPMMLContextDocumentEntry() {
        doTestWBFromDMNWithContextEntry(FunctionKind.PMML,
                                        LiteralExpressionPMMLDocument.VARIABLE_DOCUMENT,
                                        LiteralExpressionPMMLDocument.class);
    }

    @Test
    public void testWBFromDMNWithPMMLContextDocumentModelEntry() {
        doTestWBFromDMNWithContextEntry(FunctionKind.PMML,
                                        LiteralExpressionPMMLDocumentModel.VARIABLE_MODEL,
                                        LiteralExpressionPMMLDocumentModel.class);
    }

    private void doTestWBFromDMNWithContextEntry(final FunctionKind kind,
                                                 final String variableName,
                                                 final Class<? extends IsLiteralExpression> literalExpressionClass) {
        final org.kie.dmn.model.api.FunctionDefinition dmn = new TFunctionDefinition();
        final org.kie.dmn.model.api.Context contextExpression = new TContext();
        final org.kie.dmn.model.api.ContextEntry contextEntry = new TContextEntry();
        final org.kie.dmn.model.api.InformationItem variable = new TInformationItem();
        variable.setName(variableName);
        contextEntry.setVariable(variable);
        contextEntry.setExpression(new TLiteralExpression());
        contextExpression.getContextEntry().add(contextEntry);
        dmn.setKind(kind);
        dmn.setExpression(contextExpression);

        final FunctionDefinition wb = FunctionDefinitionPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertThat(wb.getExpression()).isInstanceOf(Context.class);
        assertThat(((Context) wb.getExpression()).getContextEntry().get(0).getExpression()).isInstanceOf(literalExpressionClass);
    }

    @Test
    public void testDMNFromWB() {
        final FunctionDefinition wb = new FunctionDefinition();
        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);

        wb.getId().setValue(FUNCTION_DEFINITION_UUID);
        wb.getDescription().setValue(FUNCTION_DEFINITION_DESCRIPTION);
        wb.setTypeRef(new org.kie.workbench.common.dmn.api.property.dmn.QName(org.kie.workbench.common.dmn.api.property.dmn.QName.NULL_NS_URI,
                                                                              FUNCTION_DEFINITION_QNAME_LOCALPART));
        wb.setKind(FunctionDefinition.Kind.JAVA);
        wb.setExpression(literalExpression);

        final org.kie.dmn.model.api.FunctionDefinition dmn = FunctionDefinitionPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(FUNCTION_DEFINITION_UUID);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(FUNCTION_DEFINITION_DESCRIPTION);
        assertThat(dmn.getTypeRef()).isNotNull();
        assertThat(dmn.getTypeRef().getLocalPart()).isEqualTo(FUNCTION_DEFINITION_QNAME_LOCALPART);
        assertThat(dmn.getKind()).isNotNull();
        assertThat(dmn.getKind()).isEqualTo(FunctionKind.JAVA);
        assertThat(dmn.getExpression()).isNotNull();
        assertThat(dmn.getExpression().getId()).isEqualTo(EXPRESSION_UUID);

        verify(componentWidthsConsumer).accept(componentWidthsCaptor.capture());

        final ComponentWidths componentWidths = componentWidthsCaptor.getValue();
        assertThat(componentWidths).isNotNull();
        assertThat(componentWidths.getDmnElementRef().getLocalPart()).isEqualTo(EXPRESSION_UUID);
        assertThat(componentWidths.getWidths().size()).isEqualTo(literalExpression.getRequiredComponentWidthCount());
        assertThat(componentWidths.getWidths().get(0)).isEqualTo(200.0);
    }
}
