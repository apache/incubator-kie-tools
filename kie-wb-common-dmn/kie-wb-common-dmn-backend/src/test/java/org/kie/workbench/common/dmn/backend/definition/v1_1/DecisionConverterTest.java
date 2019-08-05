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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.LiteralExpression;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DecisionConverterTest {

    private static final String DECISION_UUID = "d-uuid";

    private static final String DECISION_NAME = "d-name";

    private static final String DECISION_DESCRIPTION = "d-description";

    private static final String EXPRESSION_UUID = "uuid";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<ComponentWidths> componentWidthsConsumer;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Element element;

    @Captor
    private ArgumentCaptor<HasComponentWidths> hasComponentWidthsCaptor;

    @Captor
    private ArgumentCaptor<ComponentWidths> componentWidthsCaptor;

    private DecisionConverter converter;

    @Before
    public void setup() {
        this.converter = new DecisionConverter(factoryManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWBFromDMN() {
        final Node<View<Decision>, ?> factoryNode = new NodeImpl<>(UUID.uuid());
        final View<Decision> view = new ViewImpl<>(new Decision(), Bounds.create());
        factoryNode.setContent(view);

        when(factoryManager.newElement(anyString(), eq(getDefinitionId(Decision.class)))).thenReturn(element);
        when(element.asNode()).thenReturn(factoryNode);

        final org.kie.dmn.model.api.Decision dmn = new TDecision();
        final org.kie.dmn.model.api.LiteralExpression literalExpression = new TLiteralExpression();
        final org.kie.dmn.model.api.InformationItem informationItem = new TInformationItem();
        literalExpression.setId(EXPRESSION_UUID);

        dmn.setId(DECISION_UUID);
        dmn.setName(DECISION_NAME);
        dmn.setDescription(DECISION_DESCRIPTION);
        dmn.setVariable(informationItem);
        dmn.setExpression(literalExpression);

        final Node<View<Decision>, ?> node = converter.nodeFromDMN(dmn, hasComponentWidthsConsumer);
        final Decision wb = node.getContent().getDefinition();

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(DECISION_UUID);
        assertThat(wb.getName()).isNotNull();
        assertThat(wb.getName().getValue()).isEqualTo(DECISION_NAME);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(DECISION_DESCRIPTION);
        assertThat(wb.getVariable()).isNotNull();
        assertThat(wb.getVariable().getName().getValue()).isEqualTo(DECISION_NAME);
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
        final Decision wb = new Decision();
        final LiteralExpression literalExpression = new LiteralExpression();
        final InformationItemPrimary informationItem = new InformationItemPrimary();
        literalExpression.getComponentWidths().set(0, 200.0);
        literalExpression.getId().setValue(EXPRESSION_UUID);

        wb.getId().setValue(DECISION_UUID);
        wb.getName().setValue(DECISION_NAME);
        wb.getDescription().setValue(DECISION_DESCRIPTION);
        wb.setVariable(informationItem);
        wb.setExpression(literalExpression);

        final Node<View<Decision>, ?> node = new NodeImpl<>(UUID.uuid());
        final View<Decision> view = new ViewImpl<>(wb, Bounds.create());
        node.setContent(view);

        final org.kie.dmn.model.api.Decision dmn = converter.dmnFromNode(node, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(DECISION_UUID);
        assertThat(dmn.getName()).isNotNull();
        assertThat(dmn.getName()).isEqualTo(DECISION_NAME);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(DECISION_DESCRIPTION);
        assertThat(dmn.getVariable()).isNotNull();
        assertThat(dmn.getVariable().getName()).isEqualTo(DECISION_NAME);
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
