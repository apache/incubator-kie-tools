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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.v1_2.TContextEntry;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ContextEntryPropertyConverterTest {

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
        final org.kie.dmn.model.api.ContextEntry dmn = new TContextEntry();
        final org.kie.dmn.model.api.InformationItem informationItem = new TInformationItem();
        dmn.setVariable(informationItem);

        final org.kie.dmn.model.api.LiteralExpression literalExpression = new TLiteralExpression();
        literalExpression.setId(EXPRESSION_UUID);

        dmn.setExpression(literalExpression);

        final ContextEntry wb = ContextEntryPropertyConverter.wbFromDMN(dmn, hasComponentWidthsConsumer);

        assertThat(wb).isNotNull();
        assertThat(wb.getVariable()).isNotNull();
        assertThat(wb.getExpression()).isNotNull();
        assertThat(wb.getExpression().getId().getValue()).isEqualTo(EXPRESSION_UUID);

        verify(hasComponentWidthsConsumer).accept(eq(EXPRESSION_UUID),
                                                  hasComponentWidthsCaptor.capture());

        final HasComponentWidths hasComponentWidths = hasComponentWidthsCaptor.getValue();
        assertThat(hasComponentWidths).isNotNull();
        assertThat(hasComponentWidths).isEqualTo(wb.getExpression());
    }

    @Test
    public void testDMNFromWB() {
        final ContextEntry wb = new ContextEntry();
        final InformationItem informationItem = new InformationItem();
        wb.setVariable(informationItem);

        final LiteralExpression literalExpression = new LiteralExpression();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);

        wb.setExpression(literalExpression);

        final org.kie.dmn.model.api.ContextEntry dmn = ContextEntryPropertyConverter.dmnFromWB(wb, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getVariable()).isNotNull();
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
