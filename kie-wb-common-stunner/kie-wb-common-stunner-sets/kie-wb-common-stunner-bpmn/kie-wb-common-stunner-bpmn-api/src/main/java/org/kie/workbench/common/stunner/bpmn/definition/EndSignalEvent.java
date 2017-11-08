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
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = EndSignalEvent.EndSignalEventBuilder.class)
@Morph(base = BaseEndEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class EndSignalEvent extends BaseEndEvent {

    @Title
    public static final transient String title = "End Signal Event";

    @Description
    public static final transient String description = "At the end of the process, a signal is thrown. (One signal thrown can be caught multiple times";
    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    protected ScopedSignalEventExecutionSet executionSet;

    public EndSignalEvent() {
    }

    public EndSignalEvent(final @MapsTo("general") BPMNGeneralSet general,
                          final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                          final @MapsTo("fontSet") FontSet fontSet,
                          final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                          final @MapsTo("executionSet") ScopedSignalEventExecutionSet executionSet) {
        super(general,
              dataIOSet,
              backgroundSet,
              fontSet,
              dimensionsSet);
        this.executionSet = executionSet;
    }

    public ScopedSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(ScopedSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EndSignalEvent) {
            EndSignalEvent other = (EndSignalEvent) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }

    @NonPortable
    public static class EndSignalEventBuilder extends BaseEndEventBuilder<EndSignalEvent> {

        @Override
        public EndSignalEvent build() {
            return new EndSignalEvent(new BPMNGeneralSet(""),
                                      new DataIOSet(),
                                      new BackgroundSet(BG_COLOR,
                                                        BORDER_COLOR,
                                                        BORDER_SIZE),
                                      new FontSet(),
                                      new CircleDimensionSet(new Radius(RADIUS)),
                                      new ScopedSignalEventExecutionSet());
        }
    }
}
