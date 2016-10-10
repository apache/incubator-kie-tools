/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.bpmn.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneral;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.SimulationSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskType;
import org.kie.workbench.common.stunner.bpmn.definition.property.task.TaskTypes;
import org.kie.workbench.common.stunner.bpmn.shape.def.TaskShapeDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Property;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphBase;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphProperty;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.MorphPropertyValueBinding;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Shape( factory = BasicShapesFactory.class, def = TaskShapeDef.class )
@MorphBase( defaultType = NoneTask.class, targets = { ReusableSubprocess.class } )
public abstract class BaseTask implements BPMNDefinition {

    @Category
    public static final transient String category = Categories.ACTIVITIES;

    @Description
    public static final transient String description = "A task is a unit of work - the job to be performed";

    @PropertySet
    @FieldDef( label = "General Settings", position = 0 )
    @Valid
    protected BPMNGeneral general;

    @PropertySet
    @FieldDef( label = "Task Data", position = 2 )
    @Valid
    protected DataIOSet dataIOSet;

    @PropertySet
    @FieldDef( label = "Background Settings", position = 3 )
    @Valid
    protected BackgroundSet backgroundSet;

    @PropertySet
    @FieldDef( label = "Font Settings", position = 4 )
    protected FontSet fontSet;

    @PropertySet
    @FieldDef( label = "Process Simulation", position = 5 )
    protected SimulationSet simulationSet;

    @PropertySet
    @FieldDef( label = "Shape Dimensions", position = 6 )
    protected RectangleDimensionsSet dimensionsSet;

    @Property
    @FieldDef( label = "Task Type", property = "value", position = 7 )
    @MorphProperty( binder = TaskTypeMorphPropertyBinding.class )
    protected TaskType taskType;

    public static class TaskTypeMorphPropertyBinding implements MorphPropertyValueBinding<TaskType, TaskTypes> {

        private static final Map<TaskTypes, Class<?>> MORPH_TARGETS =
                new HashMap<TaskTypes, Class<?>>( 4 ) {{
                    put( TaskTypes.NONE, NoneTask.class );
                    put( TaskTypes.USER, UserTask.class );
                    put( TaskTypes.SCRIPT, ScriptTask.class );
                    put( TaskTypes.BUSINESS_RULE, BusinessRuleTask.class );
                }};

        @Override
        public TaskTypes getValue( final TaskType property ) {
            return property.getValue();
        }

        @Override
        public Map<TaskTypes, Class<?>> getMorphTargets() {
            return MORPH_TARGETS;
        }

    }

    @Labels
    protected final Set<String> labels = new HashSet<String>() {{
        add( "all" );
        add( "sequence_start" );
        add( "sequence_end" );
        add( "from_task_event" );
        add( "to_task_event" );
        add( "FromEventbasedGateway" );
        add( "messageflow_start" );
        add( "messageflow_end" );
        add( "fromtoall" );
        add( "ActivitiesMorph" );
    }};

    @NonPortable
    static abstract class BaseTaskBuilder<T extends BaseTask> implements Builder<T> {

        public static final String COLOR = "#f9fad2";
        public static final Double WIDTH = 136d;
        public static final Double HEIGHT = 48d;
        public static final Double BORDER_SIZE = 1d;
        public static final String BORDER_COLOR = "#000000";

    }

    protected BaseTask( final TaskTypes type ) {
        this.taskType = new TaskType( type );
    }

    public BaseTask( @MapsTo( "general" ) BPMNGeneral general,
                     @MapsTo( "dataIOSet" ) DataIOSet dataIOSet,
                     @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                     @MapsTo( "fontSet" ) FontSet fontSet,
                     @MapsTo( "dimensionsSet" ) RectangleDimensionsSet dimensionsSet,
                     @MapsTo( "simulationSet" ) SimulationSet simulationSet,
                     @MapsTo( "taskType" ) TaskType taskType ) {
        this.general = general;
        this.dataIOSet = dataIOSet;
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

    public BPMNGeneral getGeneral() {
        return general;
    }

    public DataIOSet getDataIOSet() {
        return dataIOSet;
    }

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setGeneral( BPMNGeneral general ) {
        this.general = general;
    }

    public void setDataIOSet( DataIOSet dataIOSet ) {
        this.dataIOSet = dataIOSet;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet( FontSet fontSet ) {
        this.fontSet = fontSet;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType( TaskType taskType ) {
        this.taskType = taskType;
    }

    public SimulationSet getSimulationSet() {
        return simulationSet;
    }

    public void setSimulationSet( SimulationSet simulationSet ) {
        this.simulationSet = simulationSet;
    }

    public RectangleDimensionsSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet( RectangleDimensionsSet dimensionsSet ) {
        this.dimensionsSet = dimensionsSet;
    }
}
