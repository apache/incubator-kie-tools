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

import java.util.List;
import java.util.Optional;

import org.eclipse.bpmn2.Activity;
import org.eclipse.bpmn2.DataInput;
import org.eclipse.bpmn2.DataInputAssociation;
import org.eclipse.bpmn2.DataOutput;
import org.eclipse.bpmn2.DataOutputAssociation;
import org.eclipse.bpmn2.InputOutputSpecification;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.emf.common.util.ECollections;
import org.kie.workbench.common.stunner.bpmn.client.marshall.converters.tostunner.DefinitionResolver;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.AssignmentsInfo;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.ScriptTypeListValue;

public class ActivityPropertyReader extends FlowElementPropertyReader {

    protected static final String EMPTY_ASSIGNMENTS = "||||";
    protected final Activity activity;
    protected final DefinitionResolver definitionResolver;

    public ActivityPropertyReader(Activity activity, BPMNDiagram diagram, DefinitionResolver definitionResolver) {
        super(activity, diagram, definitionResolver.getShape(activity.getId()), definitionResolver.getResolutionFactor());
        this.activity = activity;
        this.definitionResolver = definitionResolver;
    }

    public ScriptTypeListValue getOnEntryAction() {
        return Scripts.onEntry(element.getExtensionValues());
    }

    public ScriptTypeListValue getOnExitAction() {
        return Scripts.onExit(element.getExtensionValues());
    }

    public SimulationSet getSimulationSet() {
        return definitionResolver.resolveSimulationParameters(activity.getId())
                .map(SimulationSets::of)
                .orElse(new SimulationSet());
    }

    public AssignmentsInfo getAssignmentsInfo() {
        AssignmentsInfo info = AssignmentsInfos.of(getDataInputs(),
                                                   getDataInputAssociations(),
                                                   getDataOutputs(),
                                                   getDataOutputAssociations(),
                                                   getIOSpecification().isPresent());
        // do not break compatibility with old marshallers: return
        // empty delimited fields instead of empty string
        if (info.getValue().isEmpty()) {
            info.setValue(EMPTY_ASSIGNMENTS);
        }
        return info;
    }

    protected Optional<InputOutputSpecification> getIOSpecification() {
        return Optional.ofNullable(activity.getIoSpecification());
    }

    protected List<DataInput> getDataInputs() {
        return getIOSpecification().map(InputOutputSpecification::getDataInputs).orElse(ECollections.emptyEList());
    }

    protected List<DataOutput> getDataOutputs() {
        return getIOSpecification().map(InputOutputSpecification::getDataOutputs).orElse(ECollections.emptyEList());
    }

    protected List<DataInputAssociation> getDataInputAssociations() {
        return activity.getDataInputAssociations();
    }

    protected List<DataOutputAssociation> getDataOutputAssociations() {
        return activity.getDataOutputAssociations();
    }
}
