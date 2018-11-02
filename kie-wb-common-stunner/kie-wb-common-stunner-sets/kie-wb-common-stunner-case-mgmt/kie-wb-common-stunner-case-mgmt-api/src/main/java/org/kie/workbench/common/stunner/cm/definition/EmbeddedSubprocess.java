/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.definition;

import java.util.Objects;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.subProcess.execution.EmbeddedSubprocessExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
// This is a clone of org.kie.workbench.common.stunner.bpmn.definition.EmbeddedSubprocess with different labels, containment rule and title.
// Unfortunately extending the foregoing and providing a new set of labels leads to errai-data-binding to barf
// presumably because there are two fields called "labels" (although I've also tried with a different name field
// and it leads to the same errors).
public class EmbeddedSubprocess extends BaseSubprocess implements DataIOModel {

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    private EmbeddedSubprocessExecutionSet executionSet;

    @PropertySet
    @FormField(afterElement = "executionSet")
    @Valid
    private ProcessData processData;

    public EmbeddedSubprocess() {
        this(
                new BPMNGeneralSet("Subprocess"),
                new BackgroundSet(),
                new FontSet(),
                new RectangleDimensionsSet(),
                new SimulationSet(),
                new EmbeddedSubprocessExecutionSet(),
                new ProcessData());
    }

    public EmbeddedSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                              final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                              final @MapsTo("fontSet") FontSet fontSet,
                              final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                              final @MapsTo("simulationSet") SimulationSet simulationSet,
                              final @MapsTo("executionSet") EmbeddedSubprocessExecutionSet executionSet,
                              final @MapsTo("processData") ProcessData processData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.executionSet = executionSet;
        this.processData = processData;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return false;
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return false;
    }

    @Override
    protected void initLabels() {
        super.initLabels();
        labels.add("cm_activity");
        labels.remove("cm_stage");
    }

    public ProcessData getProcessData() {
        return processData;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public EmbeddedSubprocessExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(EmbeddedSubprocessExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(executionSet),
                                         Objects.hashCode(processData));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EmbeddedSubprocess) {
            EmbeddedSubprocess other = (EmbeddedSubprocess) o;
            return super.equals(other) &&
                    Objects.equals(executionSet,
                                   other.executionSet) &&
                    Objects.equals(processData,
                                   other.processData);
        }
        return false;
    }
}