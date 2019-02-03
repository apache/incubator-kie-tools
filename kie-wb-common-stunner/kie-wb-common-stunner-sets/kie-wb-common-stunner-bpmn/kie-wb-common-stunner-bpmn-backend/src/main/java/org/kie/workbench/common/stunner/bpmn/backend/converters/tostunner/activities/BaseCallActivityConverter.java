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
package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.TypedFactoryManager;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.BpmnNode;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.ActivityPropertyReader;
import org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner.properties.PropertyReaderFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public abstract class BaseCallActivityConverter<R extends BaseReusableSubprocess,
        E extends BaseReusableSubprocessTaskExecutionSet> {

    protected final TypedFactoryManager factoryManager;
    private final PropertyReaderFactory propertyReaderFactory;

    public BaseCallActivityConverter(TypedFactoryManager factoryManager,
                                     PropertyReaderFactory propertyReaderFactory) {
        this.factoryManager = factoryManager;
        this.propertyReaderFactory = propertyReaderFactory;
    }

    @SuppressWarnings("unchecked")
    public BpmnNode convert(CallActivity activity) {
        ActivityPropertyReader p = propertyReaderFactory.of(activity);

        Node<View<R>, Edge> node = createNode(activity, p);

        R definition = node.getContent().getDefinition();

        definition.setGeneral(new BPMNGeneralSet(
                new Name(p.getName()),
                new Documentation(p.getDocumentation())
        ));

        definition.setExecutionSet(createReusableSubprocessTaskExecutionSet(activity, p));

        definition.setDataIOSet(new DataIOSet(p.getAssignmentsInfo()));

        node.getContent().setBounds(p.getBounds());

        definition.setSimulationSet(p.getSimulationSet());

        definition.setDimensionsSet(p.getRectangleDimensionsSet());
        definition.setFontSet(p.getFontSet());
        definition.setBackgroundSet(p.getBackgroundSet());

        return BpmnNode.of(node);
    }

    protected abstract Node<View<R>, Edge> createNode(CallActivity activity, ActivityPropertyReader p);

    protected abstract E createReusableSubprocessTaskExecutionSet(CallActivity activity,
                                                                  ActivityPropertyReader p);
}
