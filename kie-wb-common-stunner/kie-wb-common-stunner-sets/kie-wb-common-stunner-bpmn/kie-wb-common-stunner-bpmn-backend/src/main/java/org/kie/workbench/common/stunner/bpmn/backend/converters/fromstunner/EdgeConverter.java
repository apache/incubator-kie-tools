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

package org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner;

import org.kie.workbench.common.stunner.bpmn.backend.converters.Result;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.Association;
import org.kie.workbench.common.stunner.bpmn.definition.SequenceFlow;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

public class EdgeConverter {

    private final ConverterFactory converterFactory;

    public EdgeConverter(ConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    public Result<BasePropertyWriter> toFlowElement(Edge<?, ?> edge,
                                                    ElementContainer process) {
        if (edge.getContent() instanceof ViewConnector && ((ViewConnector) edge.getContent()).getDefinition() instanceof SequenceFlow) {
            return converterFactory.sequenceFlowConverter().toFlowElement(edge,
                                                                          process);
        } else if (edge.getContent() instanceof ViewConnector && ((ViewConnector) edge.getContent()).getDefinition() instanceof Association) {
            return converterFactory.associationFlowConverter().toFlowElement(edge,
                                                                             process);
        }
        return Result.failure("Converter is not implemented for edge content type: " + edge.getContent());
    }
}
