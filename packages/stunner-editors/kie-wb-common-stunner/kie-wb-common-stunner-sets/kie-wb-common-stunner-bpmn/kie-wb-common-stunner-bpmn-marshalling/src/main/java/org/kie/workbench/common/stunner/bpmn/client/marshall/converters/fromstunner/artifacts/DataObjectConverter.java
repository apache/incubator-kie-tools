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

import org.eclipse.bpmn2.DataObjectReference;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.DataObjectPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.util.ConverterUtils;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class DataObjectConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public DataObjectConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toElement(Node<View<DataObject>, ?> node) {
        final DataObject def = node.getContent().getDefinition();
        if (def != null) {
            DataObjectReference element = bpmn2.createDataObjectReference();
            element.setId(node.getUUID());

            DataObjectPropertyWriter writer = propertyWriterFactory.of(element);

            DataObject definition = node.getContent().getDefinition();
            writer.setName(StringUtils.replaceIllegalCharsForDataObjects(definition.getName().getValue()));
            writer.setType(definition.getType().getValue().getType());
            writer.setAbsoluteBounds(node);
            writer.setMetaData(definition.getAdvancedData().getMetaDataAttributes());
            return writer;
        }
        return ConverterUtils.notSupported(def);
    }
}

