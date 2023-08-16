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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes;

import org.eclipse.bpmn2.SubProcess;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.PostConverterProcessor;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.StartCompensationEvent;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class EventSubProcessPostConverter implements PostConverterProcessor {

    @Override
    public void process(ProcessPropertyWriter processWriter,
                        BasePropertyWriter nodeWriter,
                        Node<View<? extends BPMNViewDefinition>, ?> node) {
        boolean isForCompensation = GraphUtils.getChildNodes(node).stream()
                .filter(currentNode -> currentNode.getContent() instanceof View && ((View) currentNode.getContent()).getDefinition() instanceof StartCompensationEvent)
                .findFirst()
                .isPresent();
        if (isForCompensation) {
            ((SubProcess) nodeWriter.getElement()).setIsForCompensation(true);
        }
    }
}
