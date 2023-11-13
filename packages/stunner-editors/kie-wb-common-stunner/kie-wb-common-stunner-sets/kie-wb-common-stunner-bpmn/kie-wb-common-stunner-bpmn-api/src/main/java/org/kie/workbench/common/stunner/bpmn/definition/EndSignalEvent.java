/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
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
@Morph(base = BaseEndEvent.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class EndSignalEvent extends BaseEndEvent {

    @Property
    @FormField(afterElement = "general")
    @Valid
    protected ScopedSignalEventExecutionSet executionSet;

    @Property
    @FormField(afterElement = "executionSet")
    protected DataIOSet dataIOSet;

    public EndSignalEvent() {
        this(new BPMNGeneralSet(""),
             new BackgroundSet(),
             new FontSet(),
             new CircleDimensionSet(new Radius()),
             new ScopedSignalEventExecutionSet(),
             new AdvancedData(),
             new DataIOSet());
    }

    public EndSignalEvent(final @MapsTo("general") BPMNGeneralSet general,
                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                          final @MapsTo("fontSet") FontSet fontSet,
                          final @MapsTo("dimensionsSet") CircleDimensionSet dimensionsSet,
                          final @MapsTo("executionSet") ScopedSignalEventExecutionSet executionSet,
                          final @MapsTo("advancedData") AdvancedData advancedData,
                          final @MapsTo("dataIOSet") DataIOSet dataIOSet) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              advancedData);
        this.executionSet = executionSet;
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
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode(),
                                         labels.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EndSignalEvent) {
            EndSignalEvent other = (EndSignalEvent) o;
            return super.equals(other) &&
                    Objects.equals(executionSet, other.executionSet) &&
                    Objects.equals(dataIOSet, other.dataIOSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
