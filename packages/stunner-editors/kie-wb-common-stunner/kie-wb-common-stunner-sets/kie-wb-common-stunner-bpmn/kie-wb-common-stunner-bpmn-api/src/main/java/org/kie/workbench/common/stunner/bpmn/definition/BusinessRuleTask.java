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

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FieldParam;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.settings.FieldPolicy;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOModel;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.rule.annotation.CanDock;
import org.kie.workbench.common.stunner.core.util.HashUtil;

import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.COLLAPSIBLE_CONTAINER;
import static org.kie.workbench.common.forms.adf.engine.shared.formGeneration.processing.fields.fieldInitializers.nestedForms.SubFormFieldInitializer.FIELD_CONTAINER_PARAM;

@Portable
@Bindable
@Definition
@CanDock(roles = {"IntermediateEventOnActivityBoundary"})
@Morph(base = BaseTask.class)
@FormDefinition(
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED,
        defaultFieldSettings = {@FieldParam(name = FIELD_CONTAINER_PARAM, value = COLLAPSIBLE_CONTAINER)}
)
public class BusinessRuleTask extends BaseTask implements DataIOModel {

    public static final String EXECUTION_SET = "executionSet";
    public static final String DATA_IO_SET = "dataIOSet";

    @Property
    @FormField(
            afterElement = "general"
    )
    @Valid
    protected BusinessRuleTaskExecutionSet executionSet;

    @Property
    @FormField(
            afterElement = "executionSet"
    )
    @Valid
    protected DataIOSet dataIOSet;

    public BusinessRuleTask() {
        this(new TaskGeneralSet(new Name("Task"),
                                new Documentation("")),
             new BusinessRuleTaskExecutionSet(),
             new DataIOSet(),
             new BackgroundSet(),
             new FontSet(),
             new RectangleDimensionsSet(),
             new SimulationSet(),
             new TaskType(TaskTypes.BUSINESS_RULE),
             new AdvancedData());
    }

    public BusinessRuleTask(final @MapsTo("general") TaskGeneralSet general,
                            final @MapsTo("executionSet") BusinessRuleTaskExecutionSet executionSet,
                            final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                            final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                            final @MapsTo("fontSet") FontSet fontSet,
                            final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                            final @MapsTo("simulationSet") SimulationSet simulationSet,
                            final @MapsTo("taskType") TaskType taskType,
                            final @MapsTo("advancedData") AdvancedData advancedData) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              taskType,
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

    public BusinessRuleTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setExecutionSet(final BusinessRuleTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public void setDataIOSet(final DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         executionSet.hashCode(),
                                         dataIOSet.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BusinessRuleTask) {
            BusinessRuleTask other = (BusinessRuleTask) o;
            return super.equals(other) &&
                    executionSet.equals(other.executionSet) &&
                    dataIOSet.equals(other.dataIOSet);
        }
        return false;
    }
}
