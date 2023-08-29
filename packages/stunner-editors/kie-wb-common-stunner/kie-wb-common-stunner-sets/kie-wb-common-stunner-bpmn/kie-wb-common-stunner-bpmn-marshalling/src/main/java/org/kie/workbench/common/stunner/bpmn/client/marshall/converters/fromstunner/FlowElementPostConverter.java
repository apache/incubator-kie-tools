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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.EndCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.IntermediateCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.IntermediateLinkEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.events.IntermediateThrowCompensationEventPostConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.processes.EventSubProcessPostConverter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.BasePropertyWriter;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties.ProcessPropertyWriter;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNViewDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.EndCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.EventSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEvent;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateCompensationEventThrowing;
import org.kie.workbench.common.stunner.bpmn.definition.IntermediateLinkEventThrowing;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class FlowElementPostConverter {

    private final Map<Class<? extends BPMNViewDefinition>, PostConverterProcessor> postConverters = new HashMap<>();

    public FlowElementPostConverter() {
        postConverters.put(IntermediateCompensationEvent.class,
                           new IntermediateCompensationEventPostConverter());
        postConverters.put(IntermediateCompensationEventThrowing.class,
                           new IntermediateThrowCompensationEventPostConverter());
        postConverters.put(EndCompensationEvent.class,
                           new EndCompensationEventPostConverter());
        postConverters.put(EventSubprocess.class,
                           new EventSubProcessPostConverter());
        postConverters.put(IntermediateLinkEventThrowing.class,
                           new IntermediateLinkEventPostConverter());
    }

    public void postConvert(ProcessPropertyWriter processWriter,
                            BasePropertyWriter nodeWriter,
                            Node<View<? extends BPMNViewDefinition>, ?> node) {
        Optional<PostConverterProcessor> postConverter = getPostConverter(node);
        if (postConverter.isPresent()) {
            postConverter.get().process(processWriter,
                                        nodeWriter,
                                        node);
        }
    }

    private Optional<PostConverterProcessor> getPostConverter(Node<View<? extends BPMNViewDefinition>, ?> node) {
        return Optional.ofNullable(postConverters.get(node.getContent().getDefinition().getClass()));
    }
}
