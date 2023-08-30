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

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import org.kie.workbench.common.stunner.bpmn.definition.property.variables.AdvancedData;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphPropertyValueBinding;
import org.kie.workbench.common.stunner.core.util.HashUtil;

@MorphBase(defaultType = NoneTask.class, targets = {BaseNonContainerSubprocess.class})
public abstract class BaseTask implements BPMNViewDefinition {

    public static final Set<String> TASK_LABELS = Stream.of("all",
                                                            "lane_child",
                                                            "sequence_start",
                                                            "sequence_end",
                                                            "from_task_event",
                                                            "to_task_event",
                                                            "messageflow_start",
                                                            "messageflow_end",
                                                            "fromtoall",
                                                            "ActivitiesMorph",
                                                            "cm_activity")
            .collect(Collectors.toSet());

    @Category
    public static final transient String category = BPMNCategories.ACTIVITIES;

    @Property
    @FormField
    @Valid
    protected TaskGeneralSet general;

    @Property
    @MorphProperty(binder = TaskTypeMorphPropertyBinding.class)
    protected TaskType taskType;

    @Property
    @Valid
    protected BackgroundSet backgroundSet;

    @Property
    protected FontSet fontSet;

    @Property
    protected SimulationSet simulationSet;

    @Property
    protected RectangleDimensionsSet dimensionsSet;

    @Property
    @FormField(
            afterElement = "dimensionsSet"
    )
    @Valid
    protected AdvancedData advancedData;

    public static class TaskTypeMorphPropertyBinding implements MorphPropertyValueBinding<TaskType, TaskTypes> {

        private static final Map<TaskTypes, Class<?>> MORPH_TARGETS = Stream.of(
                        new AbstractMap.SimpleEntry<>(TaskTypes.NONE, NoneTask.class),
                        new AbstractMap.SimpleEntry<>(TaskTypes.USER, UserTask.class),
                        new AbstractMap.SimpleEntry<>(TaskTypes.SCRIPT, ScriptTask.class),
                        new AbstractMap.SimpleEntry<>(TaskTypes.BUSINESS_RULE, BusinessRuleTask.class),
                        new AbstractMap.SimpleEntry<>(TaskTypes.SERVICE_TASK, GenericServiceTask.class))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

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
    protected final Set<String> labels = new HashSet<>(TASK_LABELS);

    protected BaseTask(final @MapsTo("general") TaskGeneralSet general,
                       final @MapsTo("backgroundSet") BackgroundSet backgroundSet,
                       final @MapsTo("fontSet") FontSet fontSet,
                       final @MapsTo("dimensionsSet") RectangleDimensionsSet dimensionsSet,
                       final @MapsTo("simulationSet") SimulationSet simulationSet,
                       final @MapsTo("taskType") TaskType taskType,
                       final@MapsTo("advancedData") AdvancedData advancedData) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
        this.simulationSet = simulationSet;
        this.taskType = taskType;
        this.advancedData = advancedData;
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

    public AdvancedData getAdvancedData() {
        return advancedData;
    }

    public void setAdvancedData(AdvancedData advancedData) {
        this.advancedData = advancedData;
    }

    @Override
    public int hashCode() {
        return HashUtil.combineHashCodes(Objects.hashCode(getClass()),
                                         Objects.hashCode(general),
                                         Objects.hashCode(taskType),
                                         Objects.hashCode(backgroundSet),
                                         Objects.hashCode(fontSet),
                                         Objects.hashCode(simulationSet),
                                         Objects.hashCode(dimensionsSet),
                                         Objects.hashCode(labels),
                                         Objects.hashCode(advancedData));
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
                    Objects.equals(labels, other.labels) &&
                    Objects.equals(advancedData, other.advancedData);
        }
        return false;
    }
}
