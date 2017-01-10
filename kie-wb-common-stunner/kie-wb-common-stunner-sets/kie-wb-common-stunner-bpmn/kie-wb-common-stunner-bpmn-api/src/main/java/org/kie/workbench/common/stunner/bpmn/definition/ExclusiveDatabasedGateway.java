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
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.CircleDimensionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.dimensions.Radius;
import org.kie.workbench.common.stunner.bpmn.definition.property.font.FontSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.gateway.ExclusiveGatewayExecutionSet;
import org.kie.workbench.common.stunner.bpmn.definition.property.general.BPMNGeneralSet;
import org.kie.workbench.common.stunner.bpmn.shape.def.ExclusiveDatabasedGatewayShapeDef;
import org.kie.workbench.common.stunner.core.definition.annotation.Definition;
import org.kie.workbench.common.stunner.core.definition.annotation.Description;
import org.kie.workbench.common.stunner.core.definition.annotation.PropertySet;
import org.kie.workbench.common.stunner.core.definition.annotation.Shape;
import org.kie.workbench.common.stunner.core.definition.annotation.definition.Title;
import org.kie.workbench.common.stunner.core.definition.annotation.morph.Morph;
import org.kie.workbench.common.stunner.core.factory.graph.NodeFactory;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import javax.validation.Valid;

@Portable
@Bindable
@Definition( graphFactory = NodeFactory.class, builder = ExclusiveDatabasedGateway.ExclusiveDatabasedGatewayBuilder.class )
@Shape( factory = BasicShapesFactory.class, def = ExclusiveDatabasedGatewayShapeDef.class )
@Morph( base = BaseGateway.class )
public class ExclusiveDatabasedGateway extends BaseGateway {

    @Title
    public static final transient String title = "Exclusive Data-based Gateway";

    @Description
    public static final transient String description = "Exclusive Data-based Gateway";

    @PropertySet
 //   @FieldDef( label = FIELDDEF_IMPLEMENTATION_EXECUTION, position = 1 )
    @Valid
    ExclusiveGatewayExecutionSet executionSet;

    private static long nextID = 0;
    private long Id = 0;

    @NonPortable
    public static class ExclusiveDatabasedGatewayBuilder extends BaseGatewayBuilder<ExclusiveDatabasedGateway> {

        @Override
        public ExclusiveDatabasedGateway build() {
            return new ExclusiveDatabasedGateway( new BPMNGeneralSet( "Gateway" ),
                    new ExclusiveGatewayExecutionSet(),
                    new BackgroundSet( COLOR, BORDER_COLOR, BORDER_SIZE ),
                    new FontSet(),
                    new CircleDimensionSet( new Radius( RADIUS ) ) );
        }

    }

    public ExclusiveDatabasedGateway() {
        this.Id = nextID++;
    }

    public ExclusiveDatabasedGateway( @MapsTo( "general" ) BPMNGeneralSet general,
                                      @MapsTo( "executionSet" ) ExclusiveGatewayExecutionSet executionSet,
                                      @MapsTo( "backgroundSet" ) BackgroundSet backgroundSet,
                                      @MapsTo( "fontSet" ) FontSet fontSet,
                                      @MapsTo( "dimensionsSet" ) CircleDimensionSet dimensionsSet ) {
        super( general, backgroundSet, fontSet, dimensionsSet );
        this.executionSet = executionSet;
        this.Id = nextID++;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ExclusiveGatewayExecutionSet getExecutionSet() {
        return executionSet;
    }

    public void setExecutionSet( ExclusiveGatewayExecutionSet executionSet ) {
        this.executionSet = executionSet;
    }

    public long getId() {
        return Id;
    }

    public void setId( long Id ) {
        this.Id = Id;
    }

}
