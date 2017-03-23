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
import org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.checkBox.type.CheckBoxFieldType;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IntermediateTimerEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.IsInterrupting;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.CatchEventAttributes;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = StartTimerEvent.StartTimerEventBuilder.class)
@Morph(base = BaseStartEvent.class)
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class StartTimerEvent extends BaseStartEvent {

    @Title
    public static final transient String title = "Start Timer Event";

    @Description
    public static final transient String description = "Start Timer event";

    @PropertySet
    @FormField(
            labelKey = "executionSet",
            afterElement = "general"
    )
    @Valid
    protected IntermediateTimerEventExecutionSet executionSet;

    @Property
    @FormField(
            type = CheckBoxFieldType.class,
            labelKey = "isInterrupting",
            afterElement = "executionSet"
    )
    @Valid
    protected IsInterrupting isInterrupting;

    @NonPortable
    public static class StartTimerEventBuilder extends BaseStartEventBuilder<StartTimerEvent> {

        @Override
        public StartTimerEvent build() {
            return new StartTimerEvent(new BPMNGeneralSet(""),
                                       new IntermediateTimerEventExecutionSet(),
                                       new IsInterrupting(true),
                                       new DataIOSet(),
                                       new BackgroundSet(BG_COLOR,
                                                         BORDER_COLOR,
                                                         BORDER_SIZE),
                                       new FontSet(),
                                       new CatchEventAttributes(),
                                       new CircleDimensionSet(new Radius(RADIUS)));
        }
    }

    public StartTimerEvent() {
    }

    public StartTimerEvent(final @MapsTo("general") BPMNGeneralSet general,
                           final @MapsTo("executionSet") IntermediateTimerEventExecutionSet executionSet,
                           final @MapsTo("isInterrupting") IsInterrupting isInterrupting,
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
        this.isInterrupting = isInterrupting;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IntermediateTimerEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public IsInterrupting getIsInterrupting() {
        return isInterrupting;
    }

    public void setExecutionSet(final IntermediateTimerEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public void setIsInterrupting(final IsInterrupting isInterrupting) {
        this.isInterrupting = isInterrupting;
    }
}