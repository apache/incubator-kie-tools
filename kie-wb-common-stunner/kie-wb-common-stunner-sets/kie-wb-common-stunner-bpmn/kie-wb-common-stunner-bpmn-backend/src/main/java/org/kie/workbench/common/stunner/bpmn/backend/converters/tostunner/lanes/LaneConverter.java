/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.lanes;

import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.LanePropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.Lane;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class LaneConverter {

    private final TypedFactoryManager typedFactoryManager;
    private PropertyReaderFactory propertyReaderFactory;

    public LaneConverter(TypedFactoryManager typedFactoryManager, PropertyReaderFactory propertyReaderFactory) {
        this.typedFactoryManager = typedFactoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    public BpmnNode convert(org.eclipse.bpmn2.Lane lane) {
        return convert(lane, propertyReaderFactory.of(lane));
    }

    public BpmnNode convert(org.eclipse.bpmn2.Lane lane, org.eclipse.bpmn2.Lane parent) {
        return convert(lane, propertyReaderFactory.of(lane, parent));
    }

    private BpmnNode convert(org.eclipse.bpmn2.Lane lane, LanePropertyReader p) {
        Node<View<Lane>, Edge> node = typedFactoryManager.newNode(lane.getId(), Lane.class);
        Lane definition = node.getContent().getDefinition();

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
}
