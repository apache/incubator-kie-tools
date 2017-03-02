/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
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
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.SignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.CatchEventAttributes;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = StartSignalEvent.StartSignalEventBuilder.class)
@Morph(base = BaseStartEvent.class)
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class StartSignalEvent extends BaseStartEvent {

    @Title
    public static final transient String title = "Start Signal Event";

    @Description
    public static final transient String description = "Start Signal event";

    @PropertySet
    @FormField(
            labelKey = "executionSet",
            afterElement = "general"
    )
    @Valid
    protected SignalEventExecutionSet executionSet;

    @NonPortable
    public static class StartSignalEventBuilder extends BaseStartEventBuilder<StartSignalEvent> {

        @Override
        public StartSignalEvent build() {
            return new StartSignalEvent(new BPMNGeneralSet("Start Signal"),
                                        new SignalEventExecutionSet(),
                                        new DataIOSet(),
                                        new BackgroundSet(BG_COLOR,
                                                          BORDER_COLOR,
                                                          BORDER_SIZE),
                                        new FontSet(),
                                        new CatchEventAttributes(),
                                        new CircleDimensionSet(new Radius(RADIUS)));
        }
    }

    public StartSignalEvent() {
    }

    public StartSignalEvent(final @MapsTo("general") BPMNGeneralSet general,
                            final @MapsTo("executionSet") SignalEventExecutionSet executionSet,
                            final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                            final @MapsTo("fontSet") FontSet fontSet,
                            final @MapsTo("catchEventAttributes") CatchEventAttributes catchEventAttributes,
                            final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet) {
        super(general,
              dataIOSet,
              backgroundSet,
              fontSet,
              catchEventAttributes,
              dimensionsSet);
        this.executionSet = executionSet;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final SignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }
}