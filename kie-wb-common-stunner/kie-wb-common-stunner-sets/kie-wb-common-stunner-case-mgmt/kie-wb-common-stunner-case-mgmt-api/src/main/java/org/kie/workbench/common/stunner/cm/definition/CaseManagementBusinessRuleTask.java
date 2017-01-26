/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.cm.definition;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Documentation;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.Name;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.BusinessRuleTaskExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;

@Portable
@Bindable
@Definition(graphFactory = NodeFactory.class, builder = CaseManagementBusinessRuleTask.BusinessRuleTaskBuilder.class)
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "BPMNProperties"),
        startElement = "general",
        policy = FieldPolicy.ONLY_MARKED
)
public class CaseManagementBusinessRuleTask extends CaseManagementBaseTask {

    @Title
    public static final transient String title = "Business Rule Task";

    @PropertySet
    @FormField(
            labelKey = "executionSet",
            afterElement = "general"
    )
    @Valid
    protected BusinessRuleTaskExecutionSet executionSet;

    @PropertySet
    @FormField(
            labelKey = "dataIOSet",
            afterElement = "executionSet"
    )
    @Valid
    protected DataIOSet dataIOSet;

    @NonPortable
    public static class BusinessRuleTaskBuilder extends BaseTaskBuilder<CaseManagementBusinessRuleTask> {

        @Override
        public CaseManagementBusinessRuleTask build() {
            return new CaseManagementBusinessRuleTask(new TaskGeneralSet(new Name("Task"),
                                                                         new Documentation("")),
                                                      new BusinessRuleTaskExecutionSet(),
                                                      new DataIOSet(),
                                                      new BackgroundSet(COLOR,
                                                                        BORDER_COLOR,
                                                                        BORDER_SIZE),
                                                      new FontSet(),
                                                      new RectangleDimensionsSet(WIDTH,
                                                                                 HEIGHT),
                                                      new SimulationSet(),
                                                      new TaskType(TaskTypes.BUSINESS_RULE)
            );
        }
    }

    public CaseManagementBusinessRuleTask() {
        super(TaskTypes.BUSINESS_RULE);
    }

    public CaseManagementBusinessRuleTask(final @MapsTo("general") TaskGeneralSet general,
                                          final @MapsTo("executionSet") BusinessRuleTaskExecutionSet executionSet,
                                          final @MapsTo("dataIOSet") DataIOSet dataIOSet,
                                          final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                          final @MapsTo("fontSet") FontSet fontSet,
                                          final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                                          final @MapsTo("simulationSet") SimulationSet simulationSet,
                                          final @MapsTo("taskType") TaskType taskType) {
        super(general,
              backgroundSet,
              fontSet,
              dimensionsSet,
              simulationSet,
              taskType);
        this.executionSet = executionSet;
        this.dataIOSet = dataIOSet;
    }

    public String getTitle() {
        return title;
    }

    public BusinessRuleTaskExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet(final BusinessRuleTaskExecutionSet executionSet) {
        this.executionSet = executionSet;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public void setDataIOSet(final DataIOSet dataIOSet) {
        this.dataIOSet = dataIOSet;
    }
}
