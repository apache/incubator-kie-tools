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

package org.kie.workbench.common.stunner.shapes.client.view.animatiion;

import com.ait.lienzo.client.core.animation.AnimationProperties;
import com.ait.lienzo.client.core.animation.AnimationProperty;
import com.ait.lienzo.client.core.animation.AnimationTweener;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.Text;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.shapes.client.BasicShape;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;

import java.util.LinkedList;
import java.util.List;

public class BasicShapeAnimation
        extends AbstractBasicAnimation<BasicShape> {

    private final List<AnimationProperty> shapeAnimationProperties = new LinkedList<>();
    private final List<AnimationProperty> decoratorAnimationProperties = new LinkedList<>();
    private final List<AnimationProperty> textAnimationProperties = new LinkedList<>();

    public BasicShapeAnimation() {
    }

    @Override
    public void run() {
        final AnimationTweener tweener = AnimationTweener.LINEAR;
        final BasicShapeView<?> view = getView();
        final Text text = view.getText();
        final long duration = getDuration();
        // Shape property animations.
        final Shape shape = view.getShape();
        final AnimationProperties _shapeAnimationProperties = translate( shapeAnimationProperties );
        shape.animate( tweener, _shapeAnimationProperties, duration, getAnimationCallback() );
        shapeAnimationProperties.clear();
        // Decorator property animations.
        final Shape decorator = view.getPath();
        if ( null != decorator ) {
            final AnimationProperties _decoratorAnimationProperties = translate( decoratorAnimationProperties );
            decorator.animate( tweener, _decoratorAnimationProperties, duration );
            decoratorAnimationProperties.clear();
        }
        // Text animations.
        if ( null != text ) {
            final AnimationProperties _textAnimationProperties = translate( textAnimationProperties );
            text.animate( tweener, _textAnimationProperties, duration );
            textAnimationProperties.clear();

        }

    }

    private AnimationProperties translate( final List<AnimationProperty> ps ) {
        final AnimationProperties _ps = new AnimationProperties();
        for ( final com.ait.lienzo.client.core.animation.AnimationProperty p : ps ) {
            _ps.push( p );
        }
        return _ps;
    }

    private BasicShapeView<?> getView() {
        return ( BasicShapeView<?> ) getSource().getShapeView();
    }

    public void clear() {
        shapeAnimationProperties.clear();
        decoratorAnimationProperties.clear();
        textAnimationProperties.clear();
    }

    public void animatePosition( final Double x,
                                 final Double y ) {
        if ( null != x ) {
            shapeAnimationProperties.add( AnimationProperty.Properties.X( x ) );
        }
        if ( null != y ) {
            shapeAnimationProperties.add( AnimationProperty.Properties.Y( y ) );
        }
    }

    public void animateSize( final Double w,
                             final Double h ) {
        if ( null != w && null != h ) {
            ( ( HasSize ) getView() ).setSize( w, h );
            shapeAnimationProperties.add( AnimationProperty.Properties.WIDTH( w ) );
            decoratorAnimationProperties.add( AnimationProperty.Properties.WIDTH( w ) );
            shapeAnimationProperties.add( AnimationProperty.Properties.HEIGHT( h ) );
            decoratorAnimationProperties.add( AnimationProperty.Properties.HEIGHT( h ) );
            getView().updateFillGradient( w, h );
        }
    }

    public void animateRadius( final Double value ) {
        if ( null != value ) {
            ( ( HasRadius ) getView() ).setRadius( value );
            shapeAnimationProperties.add( AnimationProperty.Properties.RADIUS( value ) );
            decoratorAnimationProperties.add( AnimationProperty.Properties.RADIUS( value ) );
            final double size = value * 2;
            getView().updateFillGradient( size, size );
        }
    }

    public void animateFillColor( final String value ) {
        shapeAnimationProperties.add( AnimationProperty.Properties.FILL_COLOR( value ) );
    }

    public void animateStrokeColor( final String value ) {
        shapeAnimationProperties.add( AnimationProperty.Properties.STROKE_COLOR( value ) );
    }

    public void animateStrokeWidth( final Double value ) {
        shapeAnimationProperties.add( AnimationProperty.Properties.STROKE_WIDTH( value ) );
    }

    public void animateFontSize( final Double value ) {
        textAnimationProperties.add( AnimationProperty.Properties.FONT_SIZE( value ) );
    }

    public void animateFontAlpha( final Double value ) {
        textAnimationProperties.add( AnimationProperty.Properties.ALPHA( value ) );
    }

}
