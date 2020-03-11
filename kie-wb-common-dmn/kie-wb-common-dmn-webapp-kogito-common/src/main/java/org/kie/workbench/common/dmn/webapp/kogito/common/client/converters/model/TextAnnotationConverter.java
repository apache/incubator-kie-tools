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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters.model;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.kie.workbench.common.dmn.api.definition.HasComponentWidths;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
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
    public Node<View<TextAnnotation>, ?> nodeFromDMN(final JSITTextAnnotation dmn,
                                                     final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<TextAnnotation>, ?> node = (Node<View<TextAnnotation>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                             getDefinitionId(TextAnnotation.class)).asNode();
        final Id id = IdPropertyConverter.wbFromDMN(dmn.getId());
        final Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        final Text text = new Text(dmn.getText());
        final TextFormat textFormat = new TextFormat(dmn.getTextFormat());
        final TextAnnotation textAnnotation = new TextAnnotation(id,
                                                                 description,
                                                                 text,
                                                                 textFormat,
                                                                 new BackgroundSet(),
                                                                 new FontSet(),
                                                                 new GeneralRectangleDimensionsSet());
        node.getContent().setDefinition(textAnnotation);
        return node;
    }

    @Override
    public JSITTextAnnotation dmnFromNode(final Node<View<TextAnnotation>, ?> node,
                                          final Consumer<JSITComponentWidths> componentWidthsConsumer) {
        final TextAnnotation source = (TextAnnotation) DefinitionUtils.getElementDefinition(node);
        final JSITTextAnnotation result = new JSITTextAnnotation();
        result.setId(source.getId().getValue());
        final Optional<String> description = Optional.ofNullable(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        description.ifPresent(result::setDescription);
        result.setText(source.getText().getValue());
        result.setTextFormat(source.getTextFormat().getValue());
        return result;
    }
}
