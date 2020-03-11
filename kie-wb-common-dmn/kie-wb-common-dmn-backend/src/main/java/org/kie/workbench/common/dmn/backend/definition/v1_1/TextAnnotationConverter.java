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
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.GeneralRectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.backend.definition.v1_1.dd.ComponentWidths;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class TextAnnotationConverter implements NodeConverter<org.kie.dmn.model.api.TextAnnotation, org.kie.workbench.common.dmn.api.definition.model.TextAnnotation> {

    private FactoryManager factoryManager;

    public TextAnnotationConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<TextAnnotation>, ?> nodeFromDMN(final org.kie.dmn.model.api.TextAnnotation dmn,
                                                     final BiConsumer<String, HasComponentWidths> hasComponentWidthsConsumer) {
        @SuppressWarnings("unchecked")
        final Node<View<TextAnnotation>, ?> node = (Node<View<TextAnnotation>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                             getDefinitionId(TextAnnotation.class)).asNode();
        final Id id = new Id(dmn.getId());
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
    public org.kie.dmn.model.api.TextAnnotation dmnFromNode(final Node<View<TextAnnotation>, ?> node,
                                                            final Consumer<ComponentWidths> componentWidthsConsumer) {
        final TextAnnotation source = (TextAnnotation) DefinitionUtils.getElementDefinition(node);
        final org.kie.dmn.model.api.TextAnnotation result = new org.kie.dmn.model.v1_2.TTextAnnotation();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setText(source.getText().getValue());
        result.setTextFormat(source.getTextFormat().getValue());
        return result;
    }
}
