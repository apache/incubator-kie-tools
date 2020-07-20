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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.arifacts;

import org.kie.workbench.common.stunner.bpmn.backend.converters.NodeMatch;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.DataObjectPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.TextAnnotationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BaseArtifacts;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class ArtifactsConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public ArtifactsConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toElement(Node<View<BaseArtifacts>, ?> node) {
        return NodeMatch.fromNode(BaseArtifacts.class, PropertyWriter.class)
                .when(TextAnnotation.class, this::toTextAnnotation)
                .when(DataObject.class, this::toDataObjectAnnotation)
                .ignore(Object.class)
                .apply(node)
                .value();
    }

    private PropertyWriter toTextAnnotation(Node<View<TextAnnotation>, ?> node) {
        org.eclipse.bpmn2.TextAnnotation element = bpmn2.createTextAnnotation();
        element.setId(node.getUUID());

        TextAnnotationPropertyWriter writer = propertyWriterFactory.of(element);

        TextAnnotation definition = node.getContent().getDefinition();
        BPMNGeneralSet general = definition.getGeneral();
        writer.setName(general.getName().getValue());
        writer.setDocumentation(general.getDocumentation().getValue());
        writer.setAbsoluteBounds(node);

        return writer;
    }

    private PropertyWriter toDataObjectAnnotation(Node<View<DataObject>, ?> node) {

        org.eclipse.bpmn2.DataObjectReference element = bpmn2.createDataObjectReference();
        element.setId(node.getUUID());

        DataObjectPropertyWriter writer = propertyWriterFactory.of(element);

        DataObject definition = node.getContent().getDefinition();
        writer.setName(StringUtils.replaceIllegalCharsAttribute(StringUtils.replaceIllegalCharsForDataObjects(definition.getName().getValue())));
        writer.setType(definition.getType().getValue().getType());
        writer.setAbsoluteBounds(node);

        return writer;
    }
}
