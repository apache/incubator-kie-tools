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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphPropertyValueBinding;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = NoneTask.class, targets = {ReusableSubprocess.class})
public abstract class BaseTask implements BPMNViewDefinition {

    public static final Set<String> TASK_LABELS = new HashSet<String>() {{
        add("all");
        add("sequence_start");
        add("sequence_end");
        add("from_task_event");
        add("to_task_event");
        add("FromEventbasedGateway");
        add("messageflow_start");
        add("messageflow_end");
        add("fromtoall");
        add("ActivitiesMorph");
        add("cm_activity");
    }};

    @Category
    public static final transient String category = BPMNCategories.ACTIVITIES;

    @PropertySet
    @FormField
    @Valid
    protected TaskGeneralSet general;

    @Property
    @MorphProperty(binder = TaskTypeMorphPropertyBinding.class)
    protected TaskType taskType;

    @PropertySet
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    protected FontSet fontSet;

    @PropertySet
    protected SimulationSet simulationSet;

    @PropertySet
    protected RectangleDimensionsSet dimensionsSet;

    public static class TaskTypeMorphPropertyBinding implements MorphPropertyValueBinding<TaskType, TaskTypes> {

        private static final Map<TaskTypes, Class<?>> MORPH_TARGETS =
                new HashMap<TaskTypes, Class<?>>(4) {{
                    put(TaskTypes.NONE,
                        NoneTask.class);
                    put(TaskTypes.USER,
                        UserTask.class);
                    put(TaskTypes.SCRIPT,
                        ScriptTask.class);
                    put(TaskTypes.BUSINESS_RULE,
                        BusinessRuleTask.class);
                }};

        @Override
        public TaskTypes getValue(final TaskType property) {
            return property.getValue();
        }

        @Override
        public Map<TaskTypes, Class<?>> getMorphTargets() {
            return MORPH_TARGETS;
        }
    }

    @Labels
    protected final Set<String> labels = new HashSet<String>(TASK_LABELS);

    public BaseTask(final @MapsTo("general") TaskGeneralSet general,
                    final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                    final @MapsTo("fontSet") FontSet fontSet,
                    final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                    final @MapsTo("simulationSet") SimulationSet simulationSet,
                    final @MapsTo("taskType") TaskType taskType) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.simulationSet = simulationSet;
        this.taskType = taskType;
    }

    public String getCategory() {
        return category;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public TaskGeneralSet getGeneral() {
        return general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setGeneral(final TaskGeneralSet general) {
        this.general = general;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet(final FontSet fontSet) {
        this.fontSet = fontSet;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(final TaskType taskType) {
        this.taskType = taskType;
    }

    public SimulationSet getSimulationSet() {
        return simulationSet;
    }

    public void setSimulationSet(final SimulationSet simulationSet) {
        this.simulationSet = simulationSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet(final RectangleDimensionsSet dimensionsSet) {
        this.dimensionsSet = dimensionsSet;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(general.hashCode(),
                                         taskType.hashCode(),
                                         backgroundSet.hashCode(),
                                         fontSet.hashCode(),
                                         simulationSet.hashCode(),
                                         dimensionsSet.hashCode(),
                                         labels.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BaseTask) {
            BaseTask other = (BaseTask) o;
            return Objects.equals(general, other.general) &&
                    Objects.equals(taskType, other.taskType) &&
                    Objects.equals(backgroundSet, other.backgroundSet) &&
                    Objects.equals(fontSet, other.fontSet) &&
                    Objects.equals(simulationSet, other.simulationSet) &&
                    Objects.equals(dimensionsSet, other.dimensionsSet) &&
                    Objects.equals(labels, other.labels);
        }
        return false;
    }
}
