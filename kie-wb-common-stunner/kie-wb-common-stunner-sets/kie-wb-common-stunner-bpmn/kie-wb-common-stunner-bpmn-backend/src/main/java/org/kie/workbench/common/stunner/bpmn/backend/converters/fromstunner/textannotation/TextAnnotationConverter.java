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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.textannotation;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.TextAnnotationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class TextAnnotationConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public TextAnnotationConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toElement(Node<View<TextAnnotation>, ?> node) {
        return NodeMatch.fromNode(BPMNViewDefinition.class, TextAnnotationPropertyWriter.class)
                .when(TextAnnotation.class, n -> {
                    org.eclipse.bpmn2.TextAnnotation element = bpmn2.createTextAnnotation();
                    element.setId(n.getUUID());

                    TextAnnotationPropertyWriter writer = propertyWriterFactory.of(element);

                    TextAnnotation definition = n.getContent().getDefinition();
                    BPMNGeneralSet general = definition.getGeneral();
                    writer.setName(general.getName().getValue());
                    writer.setDocumentation(general.getDocumentation().getValue());
                    writer.setAbsoluteBounds(n);

                    return writer;
                })
                .ignore(Object.class)
                .apply(node)
                .value();
    }
}
