/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.definition;

import java.util.Map;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.soup.commons.util.Maps;
import org.kie.workbench.common.forms.adf.definitions.annotations.SkipFormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNCategories;
import org.kie.workbench.common.stunner.bpmn.definition.BaseNonContainerSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseUserTask;
import org.kie.workbench.common.stunner.bpmn.definition.BusinessRuleTask;
import org.kie.workbench.common.stunner.bpmn.definition.NoneTask;
import org.kie.workbench.common.stunner.bpmn.definition.ScriptTask;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.cm.definition.property.task.UserTaskExecutionSet;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphPropertyValueBinding;
import org.kie.workbench.common.stunner.core.util.HashUtil;

/**
 * Created this class to support unmarshalling with the old marshaller.
 */
@MorphBase(defaultType = NoneTask.class, targets = {BaseNonContainerSubprocess.class})
public abstract class CaseManagementBaseUserTask extends BaseUserTask<UserTaskExecutionSet> {

    @Category
    public static final transient String category = BPMNCategories.ACTIVITIES;

    public static class CaseManagementTaskTypeMorphPropertyBinding implements MorphPropertyValueBinding<TaskType, TaskTypes> {

        private static final Map<TaskTypes, Class<?>> MORPH_TARGETS =
                new Maps.Builder<TaskTypes, Class<?>>()
                        .put(TaskTypes.NONE, NoneTask.class)
                        .put(TaskTypes.USER, UserTask.class)
                        .put(TaskTypes.SCRIPT, ScriptTask.class)
                        .put(TaskTypes.BUSINESS_RULE, BusinessRuleTask.class)
                        .build();

        @Override
        public TaskTypes getValue(final TaskType property) {
            return property.getValue();
        }

        @Override
        public Map<TaskTypes, Class<?>> getMorphTargets() {
            return MORPH_TARGETS;
        }
    }

    @Property
    @SkipFormField
    @MorphProperty(binder = CaseManagementTaskTypeMorphPropertyBinding.class)
    protected TaskType caseManagementTaskType;

    public CaseManagementBaseUserTask(@MapsTo("general") TaskGeneralSet general,
                                      @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                                      @MapsTo("fontSet") FontSet fontSet,
                                      @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                                      @MapsTo("simulationSet") SimulationSet simulationSet,
                                      @MapsTo("taskType") TaskType taskType,
                                      @MapsTo("caseManagementTaskType") TaskType caseManagementTaskType) {
        super(general, backgroundSet, fontSet, dimensionsSet, simulationSet, taskType);

        this.caseManagementTaskType = caseManagementTaskType;
    }

    public String getCategory() {
        return category;
    }

    public TaskType getCaseManagementTaskType() {
        return caseManagementTaskType;
    }

    public void setCaseManagementTaskType(final TaskType taskType) {
        this.caseManagementTaskType = taskType;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(super.hashCode(),
                                         Objects.hashCode(caseManagementTaskType));
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CaseManagementBaseUserTask) {
            CaseManagementBaseUserTask other = (CaseManagementBaseUserTask) o;

            return super.equals(other) &&
                    Objects.equals(caseManagementTaskType, other.caseManagementTaskType);
        }
        return false;
    }
}
