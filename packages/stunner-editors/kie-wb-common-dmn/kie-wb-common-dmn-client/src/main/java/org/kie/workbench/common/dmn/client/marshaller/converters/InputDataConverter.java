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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.InformationItemPrimary;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInformationItem;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class InputDataConverter implements NodeConverter<JSITInputData, InputData> {

    private FactoryManager factoryManager;

    public InputDataConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<InputData>, ?> nodeFromDMN(final NodeEntry nodeEntry) {

        final JSITInputData dmn = Js.uncheckedCast(nodeEntry.getDmnElement());

        @SuppressWarnings("unchecked")
        final Node<View<InputData>, ?> node = (Node<View<InputData>, ?>) factoryManager.newElement(nodeEntry.getId(),
                                                                                                   getDefinitionId(InputData.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Name name = new Name(dmn.getName());
        final InformationItemPrimary informationItem = InformationItemPrimaryPropertyConverter.wbFromDMN(dmn.getVariable(),
                                                                                                         dmn);
        final InputData inputData = new InputData(id,
                                                  description,
                                                  name,
                                                  informationItem,
                                                  new StylingSet(),
                                                  new GeneralRectangleDimensionsSet());
        inputData.setDiagramId(nodeEntry.getDiagramId());
        node.getContent().setDefinition(inputData);

        if (Objects.nonNull(informationItem)) {
            informationItem.setParent(inputData);
        }

        DMNExternalLinksToExtensionElements.loadExternalLinksFromExtensionElements(dmn, inputData);

        return node;
    }

    @Override
    public JSITInputData dmnFromNode(final Node<View<InputData>, ?> node,
                                     final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final InputData source = (InputData) DefinitionUtils.getElementDefinition(node);
        final JSITInputData result = JSITInputData.newInstance();
        result.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(result::setDescription);
        result.setName(source.getName().getValue());
        final JSITInformationItem variable = InformationItemPrimaryPropertyConverter.dmnFromWB(source.getVariable(), source);
        result.setVariable(variable);
        DMNExternalLinksToExtensionElements.loadExternalLinksIntoExtensionElements(source, result);
        return result;
    }
}
