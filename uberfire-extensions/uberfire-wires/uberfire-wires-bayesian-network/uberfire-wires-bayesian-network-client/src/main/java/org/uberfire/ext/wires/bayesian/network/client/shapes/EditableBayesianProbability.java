/*
 * Copyright 2015 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.ext.wires.bayesian.network.client.shapes;

import java.util.Map;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.common.collect.Maps;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;

public class EditableBayesianProbability extends WiresBaseShape {

    private Map<Text, Rectangle> parentNode;
    private Map<Text, Rectangle> porcentualOptions;
    private Map<Text, Rectangle> porcentualValues;
    private Map<Map<Text, Rectangle>, Map<Text, Rectangle>> incomingNodes;

    private final Rectangle rectangle;

    public EditableBayesianProbability() {
        this( 0,
              0,
              0,
              0 );
    }

    public EditableBayesianProbability( final double width,
                                        final double height,
                                        final double positionXNode,
                                        final double positionYNode ) {
        rectangle = new Rectangle( width,
                                   height );
        rectangle.setStrokeColor( ColorName.WHITE.getValue() );

        add( rectangle );

        setX( positionXNode );
        setY( positionYNode );
        setDraggable( false );

        this.parentNode = Maps.newHashMap();
        this.porcentualOptions = Maps.newHashMap();
        this.porcentualValues = Maps.newHashMap();
        this.incomingNodes = Maps.newHashMap();
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        //It's not possible to select these Shapes
    }

    @Override
    public boolean contains( double cx,
                             double cy ) {
        //We don't have any ControlPoints so no need to worry about whether we contain a given point
        return false;
    }

    public void buildGrid() {
        drawComponents( this.parentNode );
        drawComponents( this.porcentualOptions );
        drawComponents( this.porcentualValues );
        if ( this.incomingNodes != null && !this.incomingNodes.isEmpty() ) {
            for ( Map.Entry<Map<Text, Rectangle>, Map<Text, Rectangle>> porc : incomingNodes.entrySet() ) {
                drawComponents( porc.getValue() );
                drawComponents( porc.getKey() );
            }

        }
    }

    private void drawComponents( final Map<Text, Rectangle> hash ) {
        for ( Map.Entry<Text, Rectangle> parent : hash.entrySet() ) {
            add( parent.getValue() );
            add( parent.getKey() );
        }
    }

    public Map<Text, Rectangle> getParentNode() {
        return parentNode;
    }

    public void setParentNode( final Map<Text, Rectangle> parentNode ) {
        this.parentNode = parentNode;
    }

    public Map<Text, Rectangle> getPorcentualOptions() {
        return porcentualOptions;
    }

    public void setPorcentualOptions( final Map<Text, Rectangle> porcentualOptions ) {
        this.porcentualOptions = porcentualOptions;
    }

    public Map<Text, Rectangle> getPorcentualValues() {
        return porcentualValues;
    }

    public void setPorcentualValues( final Map<Text, Rectangle> porcentualValues ) {
        this.porcentualValues = porcentualValues;
    }

    public Map<Map<Text, Rectangle>, Map<Text, Rectangle>> getIncomingNodes() {
        return incomingNodes;
    }

    public void setIncomingNodes( final Map<Map<Text, Rectangle>, Map<Text, Rectangle>> incomingNodes ) {
        this.incomingNodes = incomingNodes;
    }

}
