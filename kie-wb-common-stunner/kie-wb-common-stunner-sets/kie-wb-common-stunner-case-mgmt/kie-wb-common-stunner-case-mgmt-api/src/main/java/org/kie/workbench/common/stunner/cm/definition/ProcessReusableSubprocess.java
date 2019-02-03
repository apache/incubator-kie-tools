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
package org.kie.workbench.common.stunner.cm.definition;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.cm.definition.property.task.ProcessReusableSubprocessTaskExecutionSet;
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
public class ProcessReusableSubprocess
        extends ReusableSubprocess<ProcessReusableSubprocessTaskExecutionSet> {

    @PropertySet
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected ProcessReusableSubprocessTaskExecutionSet executionSet;

    public ProcessReusableSubprocess() {
        this(new BPMNGeneralSet("Subprocess"),
             new ProcessReusableSubprocessTaskExecutionSet(),
             new DataIOSet(),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(),
             new SimulationSet());
    }

    public ProcessReusableSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                                     final @MapsTo("executionSet") ProcessReusableSubprocessTaskExecutionSet executionSet,
                                     final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                     final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                     final @MapsTo("fontSet") FontSet fontSet,
                                     final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                                     final @MapsTo("simulationSet") SimulationSet simulationSet) {
        super(general,
              dataIOSet,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.executionSet = executionSet;
    }

    public ProcessReusableSubprocessTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final ProcessReusableSubprocessTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ProcessReusableSubprocess) {
            ProcessReusableSubprocess other = (ProcessReusableSubprocess) o;

            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}
