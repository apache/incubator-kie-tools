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

package org.kie.workbench.common.stunner.bpmn.definition;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.stunner.bpmn.definition.property.background.BackgroundSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dataio.DataIOSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.simulation.CatchEventAttributes;
import org.kie.workbench.common.stunner.bpmn.shape.def.StartNoneEventShapeDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

@Portable
@Bindable
@Definition( graphFactory = NodeFactory.class, builder = StartNoneEvent.StartNoneEventBuilder.class )
@Shape( factory = BasicShapesFactory.class, def = StartNoneEventShapeDef.class )
@Morph( base = BaseStartEvent.class )
public class StartNoneEvent extends BaseStartEvent {

    @Title
    public static final transient String title = "Start None Event";

    @Description
    public static final transient String description = "Untyped start event";

    @NonPortable
    public static class StartNoneEventBuilder extends BaseStartEventBuilder<StartNoneEvent> {

        @Override
        public StartNoneEvent build() {
            return new StartNoneEvent( new BPMNGeneralSet(),
                    new DataIOSet(),
                    new BackgroundSet( BG_COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet(),
                    new CatchEventAttributes(),
                    new CircleDimensionSet( new Radius( RADIUS ) ) );
        }

    }

    public StartNoneEvent() {
    }

    public StartNoneEvent( @MapsTo( "general" ) BPMNGeneralSet general,
                           @MapsTo( "dataIOSet" ) DataIOSet dataIOSet,
                           @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                           @MapsTo( "fontSet" ) FontSet fontSet,
                           @MapsTo( "catchEventAttributes" ) CatchEventAttributes catchEventAttributes,
                           @MapsTo( "dimensionsSet" ) CircleDimensionSet dimensionsSet ) {
        super( general, dataIOSet, backgroundSet, fontSet, catchEventAttributes, dimensionsSet );
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
