/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class InputDataConverter implements NodeConverter<org.kie.dmn.model.api.InputData, org.kie.workbench.common.dmn.api.definition.v1_1.InputData> {

    private FactoryManager factoryManager;

    public InputDataConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<InputData>, ?> nodeFromDMN(final org.kie.dmn.model.api.InputData dmn,
                                                final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<InputData>, ?> node = (Node<View<InputData>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                   getDefinitionId(InputData.class)).asNode();
        final Id id = new Id(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(), dmn);
        final InputData inputData = new InputData(id,
                                                  description,
                                                  name,
                                                  informationItem,
                                                  new BackgroundSet(),
                                                  new FontSet(),
                                                  new GeneralRectangleDimensionsSet());
        node.getContent().setDefinition(inputData);

        if (informationItem != null) {
            informationItem.setParent(inputData);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, inputData);

        return node;
    }

    @Override
    public org.kie.dmn.model.api.InputData dmnFromNode(final Node<View<InputData>, ?> node,
                                                       final Consumer<ComponentWidths> componentWidthsConsumer) {
        final InputData source = node.getContent().getDefinition();
        final org.kie.dmn.model.api.InputData result = new org.kie.dmn.model.v1_2.TInputData();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setName(source.getName().getValue());
        final org.kie.dmn.model.api.InformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        if (variable != null) {
            variable.setParent(result);
        }
        result.setVariable(variable);
        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, result);
        return result;
    }
}
