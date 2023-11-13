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


package org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.properties;

import java.util.List;
import java.util.Set;

import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.InputSet;
import org.eclipse.bpmn2.ThrowEvent;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedInputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;
import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.AssignmentsInfos.isReservedIdentifier;

public class ThrowEventPropertyWriter extends EventPropertyWriter {

    private final ThrowEvent throwEvent;
    private final  Set<DataObject> dataObjects;

    public ThrowEventPropertyWriter(ThrowEvent flowElement, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(flowElement, variableScope);
        this.throwEvent = flowElement;
        this.dataObjects = dataObjects;
    }

    @Override
    public void setAssignmentsInfo(AssignmentsInfo info) {
        ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        List<InitializedInputVariable> inputs =
                assignmentsInfo.createInitializedInputVariables(getId(), variableScope, dataObjects);

        if (inputs.isEmpty()) {
            return;
        }
        if (inputs.size() > 1) {
            throw new IllegalArgumentException("Input Associations should be at most 1 in Throw Events");
        }

        InitializedInputVariable input = inputs.get(0);

        if (isReservedIdentifier(input.getIdentifier())) {
            return;
        }

        DataInput dataInput = input.getDataInput();
        throwEvent.getDataInputs().add(dataInput);
        getInputSet().getDataInputRefs().add(dataInput);

        this.addItemDefinition(input.getItemDefinition());
        DataInputAssociation dataInputAssociation = input.getDataInputAssociation();
        if (dataInputAssociation != null) {
            throwEvent.getDataInputAssociation().add(dataInputAssociation);
        }
    }

    private InputSet getInputSet() {
        InputSet inputSet = throwEvent.getInputSet();
        if (inputSet == null) {
            inputSet = bpmn2.createInputSet();
            throwEvent.setInputSet(inputSet);
        }
        return inputSet;
    }

    @Override
    protected void addEventDefinition(EventDefinition eventDefinition) {
        this.throwEvent.getEventDefinitions().add(eventDefinition);
    }
}
