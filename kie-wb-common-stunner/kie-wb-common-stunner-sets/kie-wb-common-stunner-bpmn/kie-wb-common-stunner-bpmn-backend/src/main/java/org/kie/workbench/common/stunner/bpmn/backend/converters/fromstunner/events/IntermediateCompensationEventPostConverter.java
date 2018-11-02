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

import org.eclipse.bpmn2.Activity;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class IntermediateCompensationEventPostConverter
        extends AbstractCompensationEventPostConverter {

    @Override
    public void process(ProcessPropertyWriter processWriter,
                        BasePropertyWriter nodeWriter,
                        Node<View<? extends BPMNViewDefinition>, ?> node) {
        if (!node.getOutEdges().isEmpty()) {
            final Edge<?, ?> edge = node.getOutEdges().iterator().next();
            if (edge != null && edge.getTargetNode() != null) {
                final Activity activity = findActivity(processWriter.getProcess(),
                                                       edge.getTargetNode().getUUID());
                if (activity != null) {
                    activity.setIsForCompensation(true);
                }
            }
        }
    }
}