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

package org.kie.workbench.common.stunner.lienzo;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.Triangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.client.core.types.Point2D;
import com.ait.lienzo.client.core.types.Shadow;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.IColor;
import com.ait.lienzo.shared.core.types.TextAlign;
import com.ait.lienzo.shared.core.types.TextBaseLine;

public class Tooltip extends Group {
    public static final double TRIANGLE_SIZE = 10;

    private static final double TOOLTIP_PADDING_WIDTH = 25;

    private static final double TOOLTIP_PADDING_HEIGHT = 25;

    private static final IColor TOOLTIP_COLOR = ColorName.WHITESMOKE;

    private static final String FONT_FAMILY = "Verdana";

    private static final String CATEGORIES_FONT_STYLE = "";

    private static final String VALUES_FONT_STYLE = "bold";

    private static final int FONT_SIZE = 10;

    private static final IColor LABEL_COLOR = ColorName.BLACK;

    private Rectangle rectangle;

    private Triangle triangle;

    private Triangle tmasking;

    private Text text;

    private Text title;

    private static final Shadow SHADOW = new Shadow( ColorName.BLACK.getColor().setA( 0.80 ), 10, 3, 3 );

    public Tooltip() {
        build();
    }

    protected Tooltip build() {
        rectangle = new Rectangle( 1, 1 ).setFillColor( TOOLTIP_COLOR ).setCornerRadius( 5 ).setStrokeWidth( 1 ).setShadow( SHADOW );
        triangle = new Triangle( new Point2D( 1, 1 ), new Point2D( 1, 1 ), new Point2D( 1, 1 ) ).setFillColor( TOOLTIP_COLOR ).setStrokeWidth( 1 ).setShadow( SHADOW );
        tmasking = new Triangle( new Point2D( 1, 1 ), new Point2D( 1, 1 ), new Point2D( 1, 1 ) ).setFillColor( TOOLTIP_COLOR );
        text = new Text( "", FONT_FAMILY, CATEGORIES_FONT_STYLE, FONT_SIZE ).setFillColor( LABEL_COLOR ).setTextAlign( TextAlign.LEFT ).setTextBaseLine( TextBaseLine.MIDDLE );
        title = new Text( "", FONT_FAMILY, VALUES_FONT_STYLE, FONT_SIZE ).setFillColor( LABEL_COLOR ).setTextAlign( TextAlign.LEFT ).setTextBaseLine( TextBaseLine.MIDDLE );
        add( rectangle );
        add( triangle );
        add( tmasking );
        add( text );
        add( title );
        text.moveToTop();
        title.moveToTop();
        setVisible( false );
        setListening( false );
        return this;
    }

    public Tooltip show( final String title, final String text ) {
        this.text.setText( text );
        BoundingBox bb = this.text.getBoundingBox();
        final double ctw = bb.getWidth();
        final double cth = bb.getHeight();
        this.title.setText( title );
        bb = this.title.getBoundingBox();
        final double vtw = bb.getWidth();
        final double vth = bb.getHeight();
        final double rw = ( ctw > vtw ? ctw : vtw ) + TOOLTIP_PADDING_WIDTH;
        final double rh = ( cth + vth ) + TOOLTIP_PADDING_HEIGHT;
        rectangle.setWidth( rw ).setHeight( rh ).setCornerRadius( 5 );
        final double rx = rectangle.getX();
        final double ry = rectangle.getY();
        triangle.setPoints( new Point2D( rx + rw / 2 - TRIANGLE_SIZE, ry + rh ), new Point2D( rx + rw / 2, rh + TRIANGLE_SIZE ), new Point2D( rx + rw / 2 + TRIANGLE_SIZE, ry + rh ) );
        tmasking.setPoints( new Point2D( rx + rw / 2 - TRIANGLE_SIZE - 3, ry + rh - 3 ), new Point2D( rx + rw / 2, rh + TRIANGLE_SIZE - 3 ), new Point2D( rx + rw / 2 + TRIANGLE_SIZE + 3, ry + rh - 3 ) );
        final double vtx = rw / 2 - vtw / 2;
        final double ctx = rw / 2 - ctw / 2;
        final double vty = rh / 2 - vth / 2;
        final double cty = vty + cth + 1;
        this.text.setX( ctx ).setY( cty );
        this.title.setX( vtx ).setY( vty );
        setX( getX() - rw / 2 );
        setY( getY() - rh );
        moveToTop();
        setVisible( true );
        getLayer().batch();
        return this;
    }

    public Tooltip hide() {
        setVisible( false );
        if ( getLayer() != null ) getLayer().batch();
        return this;
    }
}
