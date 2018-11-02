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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.events;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.PostConverterProcessor;

public abstract class AbstractCompensationEventPostConverter implements PostConverterProcessor {

    protected Activity findActivity(FlowElementsContainer container,
                                    String uuid) {
        final List<FlowElementsContainer> subContainers = new ArrayList<>();
        for (FlowElement flowElement : container.getFlowElements()) {
            if (flowElement instanceof Activity) {
                if (flowElement.getId().equals(uuid)) {
                    return (Activity) flowElement;
                } else if (flowElement instanceof SubProcess) {
                    subContainers.add((SubProcess) flowElement);
                }
            }
        }
        Activity result;
        for (FlowElementsContainer subContainer : subContainers) {
            result = findActivity(subContainer,
                                  uuid);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
