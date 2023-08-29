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

import java.util.Optional;
import java.util.function.Consumer;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntry;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class TextAnnotationConverter implements NodeConverter<JSITTextAnnotation, TextAnnotation> {

    private FactoryManager factoryManager;

    public TextAnnotationConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<TextAnnotation>, ?> nodeFromDMN(final NodeEntry nodeEntry) {

        final JSITTextAnnotation dmn = Js.uncheckedCast(nodeEntry.getDmnElement());

        @SuppressWarnings("unchecked")
        final Node<View<TextAnnotation>, ?> node = (Node<View<TextAnnotation>, ?>) factoryManager.newElement(nodeEntry.getId(),
                                                                                                             getDefinitionId(TextAnnotation.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Text text = new Text(dmn.getText());
        final TextFormat textFormat = new TextFormat(dmn.getTextFormat());
        final TextAnnotation textAnnotation = new TextAnnotation(id,
                                                                 description,
                                                                 text,
                                                                 textFormat,
                                                                 new StylingSet(),
                                                                 new GeneralRectangleDimensionsSet());
        textAnnotation.setDiagramId(nodeEntry.getDiagramId());
        node.getContent().setDefinition(textAnnotation);
        return node;
    }

    @Override
    public JSITTextAnnotation dmnFromNode(final Node<View<TextAnnotation>, ?> node,
                                          final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final TextAnnotation source = (TextAnnotation) DefinitionUtils.getElementDefinition(node);
        final JSITTextAnnotation result = JSITTextAnnotation.newInstance();
        result.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(result::setDescription);
        result.setText(source.getText().getValue());
        result.setTextFormat(source.getTextFormat().getValue());
        return result;
    }
}
