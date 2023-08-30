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


package org.kie.workbench.common.stunner.bpmn.definition;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EventSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.HasProcessData;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@Morph(base = BaseSubprocess.class)
@CanContain(roles = {"all"})
@CanDock(roles = {"IntermediateEventOnSubprocessBoundary"})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)

public class EventSubprocess extends BaseSubprocess implements HasProcessData<ProcessData> {

    @Property
    @FormField(afterElement = "general")
    @Valid
    private EventSubprocessExecutionSet executionSet;

    @Property
    @FormField(afterElement = "executionSet")
    @Valid
    private ProcessData processData;

    public EventSubprocess() {
        this(new BPMNGeneralSet("Event Sub-process"),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(),
             new SimulationSet(),
             new EventSubprocessExecutionSet(),
             new ProcessData(),
             new AdvancedData());
    }

    public EventSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                           final @MapsTo("fontSet") FontSet fontSet,
                           final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                           final @MapsTo("simulationSet") SimulationSet simulationSet,
                           final @MapsTo("executionSet") EventSubprocessExecutionSet executionSet,
                           final @MapsTo("processData") ProcessData processData,
                           final @MapsTo("advancedData") AdvancedData advancedData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              advancedData);

        this.executionSet = executionSet;
        this.processData = processData;
    }

    @Override
    protected void initLabels() {
        super.initLabels();
        labels.add("canContainArtifacts");
        labels.remove("sequence_start");
        labels.remove("sequence_end");
    }

    @Override
    public ProcessData getProcessData() {
        return processData;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public EventSubprocessExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(EventSubprocessExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         processData.hashCode(),
                                         labels.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EventSubprocess) {
            EventSubprocess other = (EventSubprocess) o;
            return super.equals(other) &&
                    Objects.equals(executionSet, other.executionSet) &&
                    Objects.equals(processData, other.processData) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}