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
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.event.signal.ScopedSignalEventExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class)
@Morph(base = BaseThrowingIntermediateEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class IntermediateSignalEventThrowing extends BaseThrowingIntermediateEvent {

    @PropertySet
    @FormField(afterElement = "general")
    @Valid
    private ScopedSignalEventExecutionSet executionSet;

    @PropertySet
    @FormField(afterElement = "executionSet")
    @Valid
    private DataIOSet dataIOSet;

    public IntermediateSignalEventThrowing() {
        this(new BPMNGeneralSet(""),
             new DataIOSet(),
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(new Radius()),
             new ScopedSignalEventExecutionSet());
    }

    public IntermediateSignalEventThrowing(final @MapsTo("general") BPMNGeneralSet general,
                                           final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                           final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                           final @MapsTo("fontSet") FontSet fontSet,
                                           final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                                           final @MapsTo("executionSet") ScopedSignalEventExecutionSet executionSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet);
        this.dataIOSet = dataIOSet;
        this.executionSet = executionSet;
    }

    public ScopedSignalEventExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(ScopedSignalEventExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public boolean hasInputVars() {
        return true;
    }

    @Override
    public boolean isSingleInputVar() {
        return true;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(executionSet),
                                         Objects.hashCode(dataIOSet));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof IntermediateSignalEventThrowing) {
            IntermediateSignalEventThrowing other = (IntermediateSignalEventThrowing) o;
            return super.equals(other) &&
                    Objects.equals(executionSet, other.executionSet) &&
                    Objects.equals(dataIOSet, other.dataIOSet);
        }
        return false;
    }
}