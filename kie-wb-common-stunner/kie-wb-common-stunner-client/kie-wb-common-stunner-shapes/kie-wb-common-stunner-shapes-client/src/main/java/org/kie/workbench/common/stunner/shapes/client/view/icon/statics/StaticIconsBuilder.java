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

package org.kie.workbench.common.stunner.shapes.client.view.icon.statics;

import com.ait.lienzo.client.core.shape.Circle;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.SVGPath;
import com.ait.lienzo.shared.core.types.ColorName;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons;

public final class StaticIconsBuilder {

    private static final String BLACK = ColorName.BLACK.getColorString();

    public static Group build( final Icons icon ) {
        if ( null != icon ) {
            switch ( icon ) {
                case USER:
                    return user();
                case SCRIPT:
                    return script();
                case BUSINESS_RULE:
                    return businessRule();
                case TIMER:
                    return timer();

            }

        }
        return null;
    }

    private static Group businessRule() {
        final Group group = new Group();
        final Rectangle r1 = new Rectangle( 22, 4 )
                .setX( 0 )
                .setY( 0 )
                .setFillColor( "#B3B1B3" )
                .setFillAlpha( 1 )
                .setStrokeColor( ColorName.BLACK )
                .setStrokeAlpha( 1 );
        group.add( r1 );
        final Rectangle r2 = new Rectangle( 22, 12 )
                .setX( 0 )
                .setY( 4 )
                .setFillAlpha( 1 )
                .setStrokeColor( ColorName.BLACK )
                .setStrokeAlpha( 1 );
        group.add( r2 );
        final SVGPath path3 = createSVGPath( "M 0 10 L 22 10", null, 1, BLACK );
        group.add( path3 );
        final SVGPath path4 = createSVGPath( "M 7 4 L 7 16", null, 1, BLACK );
        group.add( path4 );
        return group;
    }

    private static Group script() {
        final Group group = new Group();
        final SVGPath path1 = createSVGPath( "M6.402,0.5h14.5c0,0-5.833,2.833-5.833,5.583s4.417,6,4.417,9.167s-4.167,5." +
                "083-4.167,5.083H0.235c0,0,5-2.667,5-5s-4.583-6.75-4.583-9.25S6.402,0.5,6.402,0.5z", null, 1, BLACK );
        group.add( path1 );
        final SVGPath path2 = createSVGPath( "M 3.5 4.5 L 13.5 4.5", null, 1, BLACK )
                .setStrokeWidth( 1.5 );
        group.add( path2 );
        final SVGPath path3 = createSVGPath( "M 3.8 8.5 L 13.8 8.5", null, 1, BLACK )
                .setStrokeWidth( 1.5 );
        group.add( path3 );
        final SVGPath path4 = createSVGPath( "M 6.3 12.5 L 16.3 12.5", null, 1, BLACK )
                .setStrokeWidth( 1.5 );
        group.add( path4 );
        final SVGPath path5 = createSVGPath( "M 6.5 16.5 L 16.5 16.5", null, 1, BLACK )
                .setStrokeWidth( 1.5 );
        group.add( path5 );
        return group;
    }

    private static Group user() {
        final Group group = new Group();
        final SVGPath path1 = createSVGPath( "M0.585,24.167h24.083v-7.833c0,0-2.333-3.917-7.083-5.167h-9.25\n" +
                "\t\t\tc-4.417,1.333-7.833,5.75-7.833,5.75L0.585,24.167z", "#F4F6F7", 1, BLACK );
        group.add( path1 );
        final SVGPath path2 = createSVGPath( "M 6 20 L 6 24", null, 1, BLACK );
        group.add( path2 );
        final SVGPath path3 = createSVGPath( "M 20 20 L 20 24", null, 1, BLACK );
        group.add( path3 );
        final Circle circle =
                new Circle( 5.417 )
                        .setX( 13.002 )
                        .setY( 5.916 )
                        .setFillColor( ColorName.BLACK )
                        .setStrokeColor( ColorName.BLACK );
        group.add( circle );
        final SVGPath path4 = createSVGPath( "M8.043,7.083c0,0,2.814-2.426,5.376-1.807s4.624-0.693,4.624-0.693\n" +
                "\t\t\tc0.25,1.688,0.042,3.75-1.458,5.584c0,0,1.083,0.75,1.083,1.5s0.125,1.875-1,3s-5.5,1.25-6.75,0S8.668,12.834,8.668,12\n" +
                "\t\t\ts0.583-1.25,1.25-1.917C8.835,9.5,7.419,7.708,8.043,7.083z", "#F0EFF0", 1, BLACK );
        group.add( path4 );
        return group;
    }

    private static Group timer() {
        final Group group = new Group();
        final SVGPath path1 = createSVGPath( "M 16 6 L 16 9" +
                "   M 21 7 L 19.5 10" +
                "   M 25 11 L 22 12.5" +
                "   M 26 16 L 23 16" +
                "   M 25 21 L 22 19.5" +
                "   M 21 25 L 19.5 22" +
                "   M 16 26 L 16 23" +
                "   M 11 25 L 12.5 22" +
                "   M 7 21 L 10 19.5" +
                "   M 6 16 L 9 16" +
                "   M 7 11 L 10 12.5" +
                "   M 11 7 L 12.5 10" +
                "   M 18 9 L 16 16 L 20 16", null, 1, BLACK );
        group.add( path1.setX( -20 ).setY( -20 ) );
        return group;
    }

    private static SVGPath createSVGPath( final String path,
                                          final String fillColor,
                                          final double fillAlpha,
                                          final String strokeColor ) {
        SVGPath result = createSVGPath( path )
                .setFillAlpha( fillAlpha )
                .setStrokeColor( strokeColor );
        if ( null != fillColor ) {
            result.setFillColor( fillColor );

        }
        return result;
    }

    private static SVGPath createSVGPath( final String path ) {
        return new SVGPath( path )
                .setDraggable( false );
    }

}
