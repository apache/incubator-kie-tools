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

import java.util.HashSet;
import java.util.Set;
import javax.validation.Valid;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.bpmn.definition.Categories;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.TaskGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.cm.shapes.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.shapes.factory.CaseManagementShapesFactory;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;

@Shape(factory = CaseManagementShapesFactory.class, def = CaseManagementTaskShapeDef.class)
public abstract class CaseManagementBaseTask implements BPMNDefinition {

    @Category
    public static final transient String category = Categories.ACTIVITIES;

    @Description
    public static final transient String description = "A task is a unit of work - the job to be performed";

    @PropertySet
    @FormField(labelKey = "general")
    @Valid
    protected TaskGeneralSet general;

    @Property
    protected TaskType taskType;

    @PropertySet
    @FormField(
            labelKey = "backgroundSet",
            afterElement = "general"
    )
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    //@FieldDef( label = FIELDDEF_FONT_SETTINGS, position = 5 )
    protected FontSet fontSet;

    @PropertySet
    //@FieldDef( label = FIELDDEF_PROCESS_SIMULATION, position = 6 )
    protected SimulationSet simulationSet;

    @PropertySet
    //@FieldDef( label = FIELDDEF_SHAPE_DIMENSIONS, position = 7 )
    protected RectangleDimensionsSet dimensionsSet;

    @Labels
    protected final Set<String> labels = new HashSet<String>() {{
        add("activity");
    }};

    @NonPortable
    static abstract class BaseTaskBuilder<T extends CaseManagementBaseTask> implements Builder<T> {

        public static final String COLOR = "#00ff00";
        public static final Double WIDTH = 100d;
        public static final Double HEIGHT = 50d;
        public static final Double BORDER_SIZE = 1d;
        public static final String BORDER_COLOR = "#000000";
    }

    protected CaseManagementBaseTask(final TaskTypes type) {
        this.taskType = new TaskType(type);
    }

    public CaseManagementBaseTask(final @MapsTo("general") TaskGeneralSet general,
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

    public String getDescription() {
        return description;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public TaskGeneralSet getGeneral() {
        return general;
    }

    public void setGeneral(final TaskGeneralSet general) {
        this.general = general;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public void setBackgroundSet(final BackgroundSet backgroundSet) {
        this.backgroundSet = backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
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
}
