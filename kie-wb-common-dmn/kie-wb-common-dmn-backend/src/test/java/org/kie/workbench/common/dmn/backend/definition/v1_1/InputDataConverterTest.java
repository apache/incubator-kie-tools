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
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;
import org.kie.workbench.common.stunner.core.util.UUID;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InputDataConverterTest {

    private static final String INPUT_DATA_UUID = "id-uuid";

    private static final String INPUT_DATA_NAME = "id-name";

    private static final String INPUT_DATA_DESCRIPTION = "id-description";

    @Mock
    private BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer;

    @Mock
    private Consumer<ComponentWidths> componentWidthsConsumer;

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Element element;

    private InputDataConverter converter;

    @Before
    public void setup() {
        this.converter = new InputDataConverter(factoryManager);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWBFromDMN() {
        final Node<View<InputData>, ?> factoryNode = new NodeImpl<>(UUID.uuid());
        final View<InputData> view = new ViewImpl<>(new InputData(), Bounds.create());
        factoryNode.setContent(view);

        when(factoryManager.newElement(anyString(), eq(getDefinitionId(InputData.class)))).thenReturn(element);
        when(element.asNode()).thenReturn(factoryNode);

        final org.kie.dmn.model.api.InputData dmn = new TInputData();
        final org.kie.dmn.model.api.InformationItem informationItem = new TInformationItem();

        dmn.setId(INPUT_DATA_UUID);
        dmn.setName(INPUT_DATA_NAME);
        dmn.setDescription(INPUT_DATA_DESCRIPTION);
        dmn.setVariable(informationItem);

        final Node<View<InputData>, ?> node = converter.nodeFromDMN(dmn, hasComponentWidthsConsumer);
        final InputData wb = (InputData) DefinitionUtils.getElementDefinition(node);

        assertThat(wb).isNotNull();
        assertThat(wb.getId()).isNotNull();
        assertThat(wb.getId().getValue()).isEqualTo(INPUT_DATA_UUID);
        assertThat(wb.getName()).isNotNull();
        assertThat(wb.getName().getValue()).isEqualTo(INPUT_DATA_NAME);
        assertThat(wb.getDescription()).isNotNull();
        assertThat(wb.getDescription().getValue()).isEqualTo(INPUT_DATA_DESCRIPTION);
        assertThat(wb.getVariable()).isNotNull();
        assertThat(wb.getVariable().getName().getValue()).isEqualTo(INPUT_DATA_NAME);

        verifyNoMoreInteractions(hasComponentWidthsConsumer);
    }

    @Test
    public void testDMNFromWB() {
        final InputData wb = new InputData();
        final InformationItemPrimary informationItem = new InformationItemPrimary();

        wb.getId().setValue(INPUT_DATA_UUID);
        wb.getName().setValue(INPUT_DATA_NAME);
        wb.getDescription().setValue(INPUT_DATA_DESCRIPTION);
        wb.setVariable(informationItem);

        final Node<View<InputData>, ?> node = new NodeImpl<>(UUID.uuid());
        final View<InputData> view = new ViewImpl<>(wb, Bounds.create());
        node.setContent(view);

        final org.kie.dmn.model.api.InputData dmn = converter.dmnFromNode(node, componentWidthsConsumer);

        assertThat(dmn).isNotNull();
        assertThat(dmn.getId()).isNotNull();
        assertThat(dmn.getId()).isEqualTo(INPUT_DATA_UUID);
        assertThat(dmn.getName()).isNotNull();
        assertThat(dmn.getName()).isEqualTo(INPUT_DATA_NAME);
        assertThat(dmn.getDescription()).isNotNull();
        assertThat(dmn.getDescription()).isEqualTo(INPUT_DATA_DESCRIPTION);
        assertThat(dmn.getVariable()).isNotNull();
        assertThat(dmn.getVariable().getName()).isEqualTo(INPUT_DATA_NAME);

        verifyNoMoreInteractions(componentWidthsConsumer);
    }
}
