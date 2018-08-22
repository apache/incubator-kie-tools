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

import org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.dmn.api.property.dmn.TextFormat;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class TextAnnotationConverter implements NodeConverter<org.kie.dmn.model.api.TextAnnotation, org.kie.workbench.common.dmn.api.definition.v1_1.TextAnnotation> {

    private FactoryManager factoryManager;

    public TextAnnotationConverter(final FactoryManager factoryManager) {
        super();
        this.factoryManager = factoryManager;
    }

    @Override
    public Node<View<TextAnnotation>, ?> nodeFromDMN(final org.kie.dmn.model.api.TextAnnotation dmn) {
        @SuppressWarnings("unchecked")
        Node<View<TextAnnotation>, ?> node = (Node<View<TextAnnotation>, ?>) factoryManager.newElement(dmn.getId(),
                                                                                                       TextAnnotation.class).asNode();
        Id id = new Id(dmn.getId());
        Description description = DescriptionPropertyConverter.wbFromDMN(dmn.getDescription());
        Text text = new Text(dmn.getText());
        TextFormat textFormat = new TextFormat(dmn.getTextFormat());
        TextAnnotation textAnnotation = new TextAnnotation(id,
                                                           description,
                                                           text,
                                                           textFormat,
                                                           new BackgroundSet(),
                                                           new FontSet(),
                                                           new RectangleDimensionsSet());
        node.getContent().setDefinition(textAnnotation);
        return node;
    }

    @Override
    public org.kie.dmn.model.api.TextAnnotation dmnFromNode(final Node<View<TextAnnotation>, ?> node) {
        TextAnnotation source = node.getContent().getDefinition();
        org.kie.dmn.model.api.TextAnnotation result = new org.kie.dmn.model.v1_1.TTextAnnotation();
        result.setId(source.getId().getValue());
        result.setDescription(DescriptionPropertyConverter.dmnFromWB(source.getDescription()));
        result.setText(source.getText().getValue());
        result.setTextFormat(source.getTextFormat().getValue());
        return result;
    }
}
