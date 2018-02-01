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
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.IsAsync;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.ProcessData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.rule.annotation.CanContain;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = EventSubprocess.EventSubprocessBuilder.class)
@Morph(base = BaseSubprocess.class)
@CanContain(roles = {"all"})
@CanDock(roles = {"IntermediateEventOnSubprocessBoundary"})
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)

public class EventSubprocess extends BaseSubprocess implements DataIOModel {

    @Property
    @FormField(
            afterElement = "general"
    )

    @Valid
    private IsAsync isAsync;
    @PropertySet
    @FormField(
            afterElement = "isAsync"
    )
    @Valid
    private ProcessData processData;

    public EventSubprocess() {
        super();
    }

    public EventSubprocess(final @MapsTo("general") BPMNGeneralSet general,
                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                           final @MapsTo("fontSet") FontSet fontSet,
                           final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                           final @MapsTo("simulationSet") SimulationSet simulationSet,
                           final @MapsTo("isAsync") IsAsync isAsync,
                           final @MapsTo("processData") ProcessData processData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);

        this.isAsync = isAsync;
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
        labels.add("canContainArtifacts");
    }

    public ProcessData getProcessData() {
        return processData;
    }

    public void setProcessData(final ProcessData processData) {
        this.processData = processData;
    }

    public IsAsync getIsAsync() {
        return isAsync;
    }

    public void setIsAsync(final IsAsync isAsync) {
        this.isAsync = isAsync;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         isAsync.hashCode(),
                                         processData.hashCode(),
                                         labels.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EventSubprocess) {
            EventSubprocess other = (EventSubprocess) o;
            return super.equals(other) &&
                    Objects.equals(isAsync, other.isAsync) &&
                    Objects.equals(processData, other.processData) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }

    @NonPortable
    public static class EventSubprocessBuilder implements Builder<EventSubprocess> {

        @Override
        public EventSubprocess build() {
            return new EventSubprocess(
                    new BPMNGeneralSet("Event Sub-process"),
                    new BackgroundSet(),
                    new FontSet(),
                    new RectangleDimensionsSet(),
                    new SimulationSet(),
                    new IsAsync(),
                    new ProcessData());
        }
    }
}