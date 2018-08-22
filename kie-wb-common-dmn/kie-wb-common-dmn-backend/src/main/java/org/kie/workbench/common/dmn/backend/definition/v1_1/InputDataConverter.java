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

import org.kie.workbench.common.dmn.api.definition.v1_1.InformationItem;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputData;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class InputDataConverter implements NodeConverter<org.kie.dmn.model.api.InputData, org.kie.workbench.common.dmn.api.definition.v1_1.InputData> {

    private FactoryManager factoryManager;

    public InputDataConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<InputData>, ?> nodeFromDMN(final org.kie.dmn.model.api.InputData dmn) {
        @SuppressWarnings("unchecked")
        Node<View<InputData>, ?> node = (Node<View<InputData>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                             InputData.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Name name = new Name(dmn.getName());
        InformationItem informationItem = InformationItemPropertyConverter.wbFromDMN(dmn.getVariable());
        InputData inputData = new InputData(id,
                                            description,
                                            name,
                                            informationItem,
                                            new BackgroundSet(),
                                            new FontSet(),
                                            new RectangleDimensionsSet());
        node.getContent().setDefinition(inputData);
        return node;
    }

    @Override
    public org.kie.dmn.model.api.InputData dmnFromNode(final Node<View<InputData>, ?> node) {
        InputData source = node.getContent().getDefinition();
        org.kie.dmn.model.api.InputData result = new org.kie.dmn.model.v1_1.TInputData();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setName(source.getName().getValue());
        result.setVariable(InformationItemPropertyConverter.dmnFromWB(source.getVariable()));
        return result;
    }
}
