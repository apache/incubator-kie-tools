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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.artifacts;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.TextAnnotationPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class TextAnnotationConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public TextAnnotationConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toElement(Node<View<TextAnnotation>, ?> node) {

        final TextAnnotation def = node.getContent().getDefinition();
        if (def != null) {
            TextAnnotation definition = node.getContent().getDefinition();

            org.eclipse.bpmn2.TextAnnotation element = bpmn2.createTextAnnotation();
            element.setId(node.getUUID());

            TextAnnotationPropertyWriter writer = propertyWriterFactory.of(element);

            BPMNGeneralSet general = definition.getGeneral();
            writer.setName(general.getName().getValue());
            writer.setDocumentation(general.getDocumentation().getValue());
            writer.setMetaData(definition.getAdvancedData().getMetaDataAttributes());
            writer.setAbsoluteBounds(node);

            return writer;
        }
        return ConverterUtils.notSupported(def);
    }
}
