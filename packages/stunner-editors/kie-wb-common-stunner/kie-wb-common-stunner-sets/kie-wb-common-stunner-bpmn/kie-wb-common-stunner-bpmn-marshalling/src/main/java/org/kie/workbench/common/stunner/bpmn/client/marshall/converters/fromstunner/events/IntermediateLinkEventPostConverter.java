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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events;

import java.util.List;

import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.LinkEventDefinition;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.ThrowEvent;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class IntermediateLinkEventPostConverter implements PostConverterProcessor {

    @Override
    public void process(ProcessPropertyWriter processWriter, BasePropertyWriter nodeWriter, Node<View<? extends BPMNViewDefinition>, ?> node) {
        ThrowEvent throwEvent = (ThrowEvent) nodeWriter.getElement();

        List<EventDefinition> linkEvents = throwEvent.getEventDefinitions();
        if (linkEvents != null && !linkEvents.isEmpty()) {
            addTargetRef(processWriter.getProcess(),
                         throwEvent.getId(),
                         (LinkEventDefinition) linkEvents.get(0));
        }
    }

    void addTargetRef(Process process,
                      String nodeId,
                      LinkEventDefinition source) {
        String linkName = source.getName();
        if (!isEmpty(linkName)) {
            final LinkEventDefinition target = findTarget(process,
                                                          nodeId,
                                                          linkName);
            if (target != null) {
                source.setTarget(target);
                target.getSource().add(source);
            }
        }
    }

    LinkEventDefinition findTarget(FlowElementsContainer container,
                                   String nodeId,
                                   String linkName) {
        FlowElementsContainer throwEventContainer = findContainer(container, nodeId);
        if (throwEventContainer != null) {
            return getCatchLinkEventWithSameName(throwEventContainer, linkName);
        }
        return null;
    }

    LinkEventDefinition getCatchLinkEventWithSameName(FlowElementsContainer container, String linkName) {
        for (FlowElement flowElement : container.getFlowElements()) {
            if (flowElement instanceof CatchEvent) {
                CatchEvent event = (CatchEvent) flowElement;
                List<EventDefinition> definitions = event.getEventDefinitions();
                if (definitions == null || definitions.isEmpty()) {
                    continue;
                }
                EventDefinition definition = definitions.get(0);
                if (definition instanceof LinkEventDefinition) {
                    LinkEventDefinition linkDefinition = (LinkEventDefinition) definition;
                    if (linkName.equals(linkDefinition.getName())) {
                        return linkDefinition;
                    }
                }
            }
        }
        return null;
    }

    private FlowElementsContainer findContainer(FlowElementsContainer container,
                                                String nodeId) {
        for (FlowElement flowElement : container.getFlowElements()) {
            if (flowElement instanceof ThrowEvent) {
                if (nodeId.equals(flowElement.getId())) {
                    return container;
                }
            } else if (flowElement instanceof SubProcess) {
                if (findContainer((SubProcess) flowElement, nodeId) != null) {
                    return (FlowElementsContainer) flowElement;
                }
            }
        }
        return null;
    }
}
