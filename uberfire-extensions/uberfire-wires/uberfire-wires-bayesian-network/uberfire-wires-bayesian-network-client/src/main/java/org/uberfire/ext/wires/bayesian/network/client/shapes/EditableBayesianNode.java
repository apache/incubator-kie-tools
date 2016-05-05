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

import java.util.List;
import java.util.Map;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.google.common.collect.Maps;
import org.uberfire.ext.wires.bayesian.network.client.utils.BayesianUtils;
import org.uberfire.ext.wires.bayesian.network.parser.client.model.BayesVariable;
import org.uberfire.ext.wires.core.api.shapes.OverridesFactoryDescription;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.util.ShapesUtils;

public class EditableBayesianNode extends WiresBaseShape implements OverridesFactoryDescription {

    private static final int BOUNDARY_SIZE = 10;

    private Rectangle header;
    private Text textHeader;
    private Map<Text, List<Rectangle>> porcentualBars;

    private final Rectangle rectangle;
    private final Rectangle bounding;

    private final BayesVariable variable;

    public EditableBayesianNode() {
        this( 0,
              0,
              0,
              0,
              "" );
    }

    public EditableBayesianNode( final Rectangle shape ) {
        this( shape.getWidth(),
              shape.getHeight(),
              shape.getX(),
              shape.getY(),
              BayesianUtils.getNodeColors()[ 0 ][ 0 ] );
    }

    public EditableBayesianNode( final double width,
                                 final double height,
                                 final double positionXNode,
                                 final double positionYNode,
                                 final String fillColor ) {
        this( width,
              height,
              positionXNode,
              positionYNode,
              fillColor,
              new BayesVariable() );
    }

    public EditableBayesianNode( final double width,
                                 final double height,
                                 final double positionXNode,
                                 final double positionYNode,
                                 final String fillColor,
                                 final BayesVariable variable ) {
        this.variable = variable;

        rectangle = new Rectangle( width,
                                   height );
        rectangle.setStrokeColor( ShapesUtils.RGB_STROKE_SHAPE );
        rectangle.setStrokeWidth( ShapesUtils.RGB_STROKE_WIDTH_SHAPE );
        rectangle.setFillColor( fillColor );

        bounding = new Rectangle( width + BOUNDARY_SIZE,
                                  height + BOUNDARY_SIZE,
                                  rectangle.getCornerRadius() );
        bounding.setX( getX() - ( BOUNDARY_SIZE / 2 ) );
        bounding.setY( getY() - ( BOUNDARY_SIZE / 2 ) );
        bounding.setStrokeWidth( BOUNDARY_SIZE );
        bounding.setAlpha( 0.1 );

        add( rectangle );

        setX( positionXNode );
        setY( positionYNode );

        this.porcentualBars = Maps.newHashMap();
    }

    @Override
    public void setSelected( final boolean isSelected ) {
        if ( isSelected ) {
            add( bounding );
        } else {
            remove( bounding );
        }
    }

    @Override
    public boolean contains( double cx,
                             double cy ) {
        //We don't have any ControlPoints so no need to worry about whether we contain a given point
        return false;
    }

    public void buildNode() {
        add( this.header );
        add( this.textHeader );
        for ( Map.Entry<Text, List<Rectangle>> porcenualBar : this.porcentualBars.entrySet() ) {
            for ( Rectangle rec : porcenualBar.getValue() ) {
                add( rec );
            }
            add( porcenualBar.getKey() );
        }
    }

    public Rectangle getParentNode() {
        return rectangle;
    }

    public Rectangle getHeader() {
        return header;
    }

    public void setHeader( final Rectangle header ) {
        this.header = header;
    }

    public Text getTextHeader() {
        return textHeader;
    }

    public void setTextHeader( final Text textHeader ) {
        this.textHeader = textHeader;
    }

    public Map<Text, List<Rectangle>> getPorcentualsBar() {
        return porcentualBars;
    }

    public void setPorcentualBars( final Map<Text, List<Rectangle>> porcentualBars ) {
        this.porcentualBars = porcentualBars;
    }

    public double getWidth() {
        return rectangle.getWidth();
    }

    public BayesVariable getVariable() {
        return variable;
    }

    @Override
    public String getDescription() {
        return variable.getName();
    }
}
