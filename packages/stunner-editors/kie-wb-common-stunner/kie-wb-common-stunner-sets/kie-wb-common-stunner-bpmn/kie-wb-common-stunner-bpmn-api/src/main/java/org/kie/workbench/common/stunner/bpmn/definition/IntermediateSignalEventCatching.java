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
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.CancellingSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@Morph(base = BaseCatchingIntermediateEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class IntermediateSignalEventCatching extends BaseCatchingIntermediateEvent {

    @Property
    @FormField(afterElement = "general")
    @Valid
    protected CancellingSignalEventExecutionSet executionSet;

    public IntermediateSignalEventCatching() {
        this(new BPMNGeneralSet(""),
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(new Radius()),
             new DataIOSet(),
             new AdvancedData(),
             new CancellingSignalEventExecutionSet());
    }

    public IntermediateSignalEventCatching(final @MapsTo("general") BPMNGeneralSet general,
                                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                           final @MapsTo("fontSet") FontSet fontSet,
                                           final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                                           final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                           final @MapsTo("advancedData") AdvancedData advancedData,
                                           final @MapsTo("executionSet") CancellingSignalEventExecutionSet executionSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              dataIOSet,
              advancedData);
        this.executionSet = executionSet;
    }

    @Override
    protected void initLabels() {
        super.initLabels();
        labels.add("FromEventbasedGateway");
    }

    public CancellingSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(CancellingSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IntermediateSignalEventCatching) {
            IntermediateSignalEventCatching other = (IntermediateSignalEventCatching) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet);
        }
        return false;
    }
}