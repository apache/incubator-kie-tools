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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.activities;

import org.eclipse.bpmn2.CallActivity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.CallActivityPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.PropertyWriterFactory;
import org.kie.workbench.common.stunner.bpmn.definition.BaseReusableSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BaseReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ReusableSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.Factories.bpmn2;

public class ReusableSubprocessConverter {

    private final PropertyWriterFactory propertyWriterFactory;

    public ReusableSubprocessConverter(PropertyWriterFactory propertyWriterFactory) {
        this.propertyWriterFactory = propertyWriterFactory;
    }

    public PropertyWriter toFlowElement(Node<View<BaseReusableSubprocess>, ?> n) {
        CallActivity activity = bpmn2.createCallActivity();
        activity.setId(n.getUUID());

        CallActivityPropertyWriter p = propertyWriterFactory.of(activity);

        BaseReusableSubprocess definition = n.getContent().getDefinition();

        BPMNGeneralSet general = definition.getGeneral();
        p.setName(general.getName().getValue());
        p.setDocumentation(general.getDocumentation().getValue());

        BaseReusableSubprocessTaskExecutionSet executionSet = definition.getExecutionSet();
        p.setOnEntryAction(executionSet.getOnEntryAction());
        p.setOnExitAction(executionSet.getOnExitAction());
        p.setCalledElement(executionSet.getCalledElement().getValue());
        p.setAsync(executionSet.getIsAsync().getValue());
        p.setIndependent(executionSet.getIndependent().getValue());
        p.setWaitForCompletion(executionSet.getWaitForCompletion().getValue());

        p.setAssignmentsInfo(definition.getDataIOSet().getAssignmentsinfo());

        if (executionSet instanceof ReusableSubprocessTaskExecutionSet) {
            ReusableSubprocessTaskExecutionSet reusableExecutionSet = (ReusableSubprocessTaskExecutionSet) executionSet;
            if (Boolean.TRUE.equals(reusableExecutionSet.getIsMultipleInstance().getValue())) {
                p.setIsSequential(reusableExecutionSet.getMultipleInstanceExecutionMode().isSequential());
                p.setCollectionInput(reusableExecutionSet.getMultipleInstanceCollectionInput().getValue());
                p.setInput(reusableExecutionSet.getMultipleInstanceDataInput().getValue());
                p.setCollectionOutput(reusableExecutionSet.getMultipleInstanceCollectionOutput().getValue());
                p.setOutput(reusableExecutionSet.getMultipleInstanceDataOutput().getValue());
                p.setCompletionCondition(reusableExecutionSet.getMultipleInstanceCompletionCondition().getValue());
            }
        }

        p.setSimulationSet(definition.getSimulationSet());

        p.setAbsoluteBounds(n);

        p.setCase(executionSet.getIsCase().getValue());
        p.setAdHocAutostart(executionSet.getAdHocAutostart().getValue());

        return p;
    }
}
