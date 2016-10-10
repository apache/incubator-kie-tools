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
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.metaModel.FieldDef;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneral;
import org.kie.workbench.common.stunner.bpmn.shape.def.IntermediateTimerEventShapeDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Category;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Labels;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.builder.Builder;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Portable
@Bindable
@Definition( graphFactory = NodeFactory.class, builder = IntermediateTimerEvent.IntermediateTimerEventBuilder.class )
@Shape( factory = BasicShapesFactory.class, def = IntermediateTimerEventShapeDef.class )
public class IntermediateTimerEvent implements BPMNDefinition {

    @Category
    public static final transient String category = Categories.EVENTS;

    @Title
    public static final transient String title = "Timer Intermediate Event";

    @Description
    public static final transient String description = "Process execution is delayed until a certain point in time " +
            "is reached or a particular duration is over.";

    @PropertySet
    @FieldDef( label = "General Settings", position = 0 )
    @Valid
    private BPMNGeneral general;

    @PropertySet
    @FieldDef( label = "Background Settings", position = 2 )
    @Valid
    private BackgroundSet backgroundSet;

    @PropertySet
    @FieldDef( label = "Font Settings" )
    private FontSet fontSet;

    @PropertySet
    @FieldDef( label = "Shape Dimensions", position = 5 )
    private CircleDimensionSet dimensionsSet;

    @Labels
    private final Set<String> labels = new HashSet<String>() {{
        add( "all" );
        add( "sequence_start" );
        add( "sequence_end" );
        add( "to_task_event" );
        add( "from_task_event" );
        add( "fromtoall" );
        add( "choreography_sequence_start" );
        add( "choreography_sequence_end" );
        add( "FromEventbasedGateway" );
        add( "IntermediateEventOnSubprocessBoundary" );
        add( "IntermediateEventOnActivityBoundary" );
        add( "EventOnChoreographyActivityBoundary" );
        add( "IntermediateEventsMorph" );
    }};

    @NonPortable
    public static class IntermediateTimerEventBuilder implements Builder<IntermediateTimerEvent> {

        public static final String COLOR = "#f5deb3";
        public static final Double BORDER_SIZE = 1d;
        public static final String BORDER_COLOR = "#a0522d";
        public static final Double RADIUS = 15d;

        @Override
        public IntermediateTimerEvent build() {
            return new IntermediateTimerEvent( new BPMNGeneral( "Timer" ),
                    new BackgroundSet( COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet(),
                    new CircleDimensionSet( RADIUS ) );
        }

    }

    public IntermediateTimerEvent() {
    }

    public IntermediateTimerEvent( @MapsTo( "general" ) BPMNGeneral general,
                                   @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                                   @MapsTo( "fontSet" ) FontSet fontSet,
                                   @MapsTo( "dimensionsSet" ) CircleDimensionSet dimensionsSet ) {
        this.general = general;
        this.backgroundSet = backgroundSet;
        this.fontSet = fontSet;
        this.dimensionsSet = dimensionsSet;
    }

    public String getCategory() {
        return category;
    }

    public String getTitle() {
        return title;
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

    public BackgroundSet getBackgroundSet() {
        return backgroundSet;
    }

    public FontSet getFontSet() {
        return fontSet;
    }

    public void setGeneral( BPMNGeneral general ) {
        this.general = general;
    }

    public void setBackgroundSet( BackgroundSet backgroundSet ) {
        this.backgroundSet = backgroundSet;
    }

    public void setFontSet( FontSet fontSet ) {
        this.fontSet = fontSet;
    }

    public CircleDimensionSet getDimensionsSet() {
        return dimensionsSet;
    }

    public void setDimensionsSet( CircleDimensionSet dimensionsSet ) {
        this.dimensionsSet = dimensionsSet;
    }
}
