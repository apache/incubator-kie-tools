/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.kie.workbench.common.stunner.bpmn.definition.property.event.none.InterruptingNoneEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationAttributeSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = StartNoneEvent.StartNoneEventBuilder.class)
@Morph(base = BaseStartEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class StartNoneEvent extends BaseStartEvent {

    @Title
    public static final transient String title = "Start Event";

    @Description
    public static final transient String description = "Untyped start event";

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected InterruptingNoneEventExecutionSet executionSet;

    @NonPortable
    public static class StartNoneEventBuilder extends BaseStartEventBuilder<StartNoneEvent> {

        @Override
        public StartNoneEvent build() {
            return new StartNoneEvent(new BPMNGeneralSet(""),
                                      new DataIOSet(),
                                      new BackgroundSet(BG_COLOR,
                                                        BORDER_COLOR,
                                                        BORDER_SIZE),
                                      new FontSet(),
                                      new CircleDimensionSet(new Radius(RADIUS)),
                                      new SimulationAttributeSet(),
                                      new InterruptingNoneEventExecutionSet());
        }
    }

    public StartNoneEvent() {
    }

    public StartNoneEvent(final @MapsTo("general") BPMNGeneralSet general,
                          final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                          final @MapsTo("fontSet") FontSet fontSet,
                          final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                          final @MapsTo("simulationSet") SimulationAttributeSet simulationSet,
                          final @MapsTo("executionSet") InterruptingNoneEventExecutionSet executionSet) {
        super(general,
              dataIOSet,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet);
        this.executionSet = executionSet;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public InterruptingNoneEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(InterruptingNoneEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StartNoneEvent)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        StartNoneEvent that = (StartNoneEvent) o;

        return executionSet.equals(that.executionSet);
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }
}
