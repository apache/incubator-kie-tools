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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.error.InterruptingErrorEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = StartErrorEvent.StartErrorEventBuilder.class)
@Morph(base = BaseStartEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class StartErrorEvent extends BaseStartEvent {

    @Title
    public static final transient String title = "Start Error Event";

    @Description
    public static final transient String description = "Starts an event subprocess by catching a named error. + " +
            "This event is only used inside of a event subprocess.";

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected InterruptingErrorEventExecutionSet executionSet;

    @PropertySet
    @FormField(afterElement = "executionSet")
    @Valid
    protected DataIOSet dataIOSet;

    @NonPortable
    public static class StartErrorEventBuilder implements Builder<StartErrorEvent> {

        @Override
        public StartErrorEvent build() {
            return new StartErrorEvent(new BPMNGeneralSet(""),
                                       new BackgroundSet(),
                                       new FontSet(),
                                       new CircleDimensionSet(new Radius()),
                                       new SimulationAttributeSet(),
                                       new DataIOSet(),
                                       new InterruptingErrorEventExecutionSet());
        }
    }

    public StartErrorEvent() {
    }

    public StartErrorEvent(final @MapsTo("general") BPMNGeneralSet general,
                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                           final @MapsTo("fontSet") FontSet fontSet,
                           final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                           final @MapsTo("simulationSet") SimulationAttributeSet simulationSet,
                           final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                           final @MapsTo("executionSet") InterruptingErrorEventExecutionSet executionSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.dataIOSet = dataIOSet;
        this.executionSet = executionSet;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public InterruptingErrorEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final InterruptingErrorEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public boolean hasOutputVars() {
        return true;
    }

    @Override
    public boolean isSingleOutputVar() {
        return true;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         dataIOSet.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StartErrorEvent) {
            StartErrorEvent other = (StartErrorEvent) o;
            return super.equals(other) &&
                    dataIOSet.equals(other.dataIOSet) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}
