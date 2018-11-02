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
import org.kie.workbench.common.stunner.bpmn.definition.property.task.AdHocSubprocessTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@CanContain(roles = {"cm_activity", "cm_stage", "IntermediateEventsMorph", "GatewaysMorph"})
@CanDock(roles = {"IntermediateEventOnSubprocessBoundary"})
@Definition(graphFactory = NodeFactory.class)
@Morph(base = BaseSubprocess.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class AdHocSubprocess
        extends BaseSubprocess {

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected AdHocSubprocessTaskExecutionSet executionSet;

    @PropertySet
    @FormField(afterElement = "executionSet")
    @Valid
    private ProcessData processData;

    public AdHocSubprocess() {
        this("Sub-process");
    }

    public AdHocSubprocess(String label) {
        this(new BPMNGeneralSet(label),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(),
             new SimulationSet(),
             new AdHocSubprocessTaskExecutionSet(),
             new ProcessData());
    }

    public AdHocSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                           final @MapsTo("fontSet") FontSet fontSet,
                           final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                           final @MapsTo("simulationSet") SimulationSet simulationSet,
                           final @MapsTo("executionSet") AdHocSubprocessTaskExecutionSet executionSet,
                           final @MapsTo("processData") ProcessData processData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.executionSet = executionSet;
        this.processData = processData;
    }

    public AdHocSubprocessTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final AdHocSubprocessTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public ProcessData getProcessData() {
        return processData;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(executionSet),
                                         Objects.hashCode(processData));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AdHocSubprocess) {
            AdHocSubprocess other = (AdHocSubprocess) o;
            return super.equals(other) &&
                    Objects.equals(executionSet, other.executionSet) &&
                    Objects.equals(processData, other.processData);
        }
        return false;
    }
}
