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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.artifacts;

import org.eclipse.bpmn2.FlowElement;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Match;
import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.NodeConverter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.DataObjectPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.TextAnnotationPropertyReader;
import org.kie.workbench.common.stunner.bpmn.definition.DataObject;
import org.kie.workbench.common.stunner.bpmn.definition.TextAnnotation;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectType;
import org.kie.workbench.common.stunner.bpmn.definition.property.artifacts.DataObjectTypeValue;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.util.StringUtils.revertIllegalCharsAttribute;

public class ArtifactsConverter implements NodeConverter<org.eclipse.bpmn2.FlowElement> {

    private final TypedFactoryManager typedFactoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    public ArtifactsConverter(TypedFactoryManager typedFactoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.typedFactoryManager = typedFactoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @Override
    public Result<BpmnNode> convert(org.eclipse.bpmn2.FlowElement element) {
        return Match.of(FlowElement.class, BpmnNode.class)
                .when(org.eclipse.bpmn2.TextAnnotation.class, this::toTextAnnotation)
                .when(org.eclipse.bpmn2.DataObjectReference.class, this::toDataObject)
                .apply(element);
    }

    private BpmnNode toTextAnnotation(org.eclipse.bpmn2.TextAnnotation element) {
        TextAnnotationPropertyReader p = propertyReaderFactory.of(element);

        Node<View<TextAnnotation>, Edge> node = typedFactoryManager.newNode(element.getId(), TextAnnotation.class);
        TextAnnotation definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node, p);
    }

    private BpmnNode toDataObject(org.eclipse.bpmn2.DataObjectReference element) {
        DataObjectPropertyReader p = propertyReaderFactory.of(element);
        Node<View<DataObject>, Edge> node = typedFactoryManager.newNode(element.getId(),
                                                                        DataObject.class);

        DataObject definition = node.getContent().getDefinition();
        definition.setName(new Name(revertIllegalCharsAttribute(p.getName())));
        definition.setType(new DataObjectType(new DataObjectTypeValue(p.getType())));
        node.getContent().setBounds(p.getBounds());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node, p);
    }
}