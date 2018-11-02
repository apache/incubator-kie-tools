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

import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.backend.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * Contract for a component that implements post conversion operations on a node. Ideally all conversion operations
 * must be resolved within the converters, but there are some edge cases were a node needs a second pass after the full
 * nodes tree has been completed.
 */
public interface PostConverterProcessor {

    /**
     * Executes the node post conversion processing.
     * @param processWriter the top level process property writer.
     * @param nodeWriter current node property writer.
     * @param node the node to be post processed.
     */
    void process(ProcessPropertyWriter processWriter,
                 BasePropertyWriter nodeWriter,
                 Node<View<? extends BPMNViewDefinition>, ?> node);
}
