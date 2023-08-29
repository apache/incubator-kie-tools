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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ThrowEvent;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

public class ThrowEventPropertyReader extends EventPropertyReader {

    private final ThrowEvent throwEvent;

    public ThrowEventPropertyReader(ThrowEvent throwEvent, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(throwEvent, diagram, definitionResolver);
        this.throwEvent = throwEvent;
    }

    @Override
    public AssignmentsInfo getAssignmentsInfo() {
        return AssignmentsInfos.of(
                throwEvent.getDataInputs(),
                throwEvent.getDataInputAssociation(),
                Collections.emptyList(),
                Collections.emptyList(), false);
    }

    @Override
    public List<EventDefinition> getEventDefinitions() {
        return combineEventDefinitions(throwEvent.getEventDefinitions(), throwEvent.getEventDefinitionRefs());
    }
}
