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

import bpsim.ElementParameters;
import org.eclipse.bpmn2.CatchEvent;
import org.eclipse.bpmn2.DataObject;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.OutputSet;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.InitializedVariable.InitializedOutputVariable;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.customproperties.ParsedAssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.properties.SimulationAttributeSets;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;

import static org.kie.workbench.common.stunner.bpmn.client.marshall.converters.fromstunner.Factories.bpmn2;

public class CatchEventPropertyWriter extends EventPropertyWriter {

    private final CatchEvent event;
    private ElementParameters simulationParameters;
    private final Set<DataObject> dataObjects;


    public CatchEventPropertyWriter(CatchEvent event, VariableScope variableScope, Set<DataObject> dataObjects) {
        super(event, variableScope);
        this.event = event;
        this.dataObjects = dataObjects;
    }

    public void setAssignmentsInfo(AssignmentsInfo info) {
        ParsedAssignmentsInfo assignmentsInfo = ParsedAssignmentsInfo.of(info);
        List<InitializedOutputVariable> outputs =
                assignmentsInfo.createInitializedOutputVariables(
                        getId(), variableScope, dataObjects);

        if (outputs.isEmpty()) {
            return;
        }
        if (outputs.size() > 1) {
            throw new IllegalArgumentException("Output Associations should be at most 1 in Catch Events");
        }

        InitializedOutputVariable output = outputs.get(0);

        DataOutput dataOutput = output.getDataOutput();
        event.getDataOutputs().add(dataOutput);
        getOutputSet().getDataOutputRefs().add(dataOutput);

        this.addItemDefinition(dataOutput.getItemSubjectRef());
        DataOutputAssociation dataOutputAssociation = output.getDataOutputAssociation();
        if (dataOutputAssociation != null) {
            event.getDataOutputAssociation().add(dataOutputAssociation);
        }
    }

    private OutputSet getOutputSet() {
        OutputSet outputSet = event.getOutputSet();
        if (outputSet == null) {
            outputSet = bpmn2.createOutputSet();
            event.setOutputSet(outputSet);
        }
        return outputSet;
    }

    public void setSimulationSet(SimulationAttributeSet simulationSet) {
        this.simulationParameters =
                SimulationAttributeSets.toElementParameters(simulationSet);
        simulationParameters.setElementRef(getId());
    }

    public ElementParameters getSimulationParameters() {
        return simulationParameters;
    }

    @Override
    public void addEventDefinition(EventDefinition eventDefinition) {
        this.event.getEventDefinitions().add(eventDefinition);
    }

    public void setCancelActivity(Boolean value) {
        // this only makes sense for boundary events: ignore
    }
}
