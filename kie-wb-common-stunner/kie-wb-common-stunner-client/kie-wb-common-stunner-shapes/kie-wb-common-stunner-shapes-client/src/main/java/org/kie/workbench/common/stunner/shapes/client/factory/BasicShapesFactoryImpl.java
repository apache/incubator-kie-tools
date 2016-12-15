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

package org.kie.workbench.common.stunner.shapes.client.factory;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.AbstractCompositeShape;
import org.kie.workbench.common.stunner.core.client.shape.AbstractShape;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.MutableShape;
import org.kie.workbench.common.stunner.core.client.shape.factory.AbstractShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.client.*;
import org.kie.workbench.common.stunner.shapes.client.view.*;
import org.kie.workbench.common.stunner.shapes.client.view.icon.dynamics.DynamicIconShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.icon.statics.StaticIconShapeView;
import org.kie.workbench.common.stunner.shapes.def.*;
import org.kie.workbench.common.stunner.shapes.def.icon.dynamics.Icons;
import org.kie.workbench.common.stunner.shapes.def.icon.statics.IconShapeDef;
import org.kie.workbench.common.stunner.shapes.def.picture.PictureShapeDef;
import org.kie.workbench.common.stunner.shapes.factory.BasicShapesFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class BasicShapesFactoryImpl
        extends AbstractShapeDefFactory<Object, ShapeView, MutableShape<Object, ShapeView>, ShapeDef<Object>>
        implements BasicShapesFactory<Object, AbstractCanvasHandler> {

    private final ShapeViewFactory shapeViewFactory;
    private final GlyphBuilderFactory glyphBuilderFactory;

    protected BasicShapesFactoryImpl() {
        this( null, null, null, null );
    }

    @Inject
    public BasicShapesFactoryImpl( final FactoryManager factoryManager,
                                   final ShapeViewFactory shapeViewFactory,
                                   final DefinitionManager definitionManager,
                                   final GlyphBuilderFactory glyphBuilderFactory ) {
        super( definitionManager, factoryManager );
        this.shapeViewFactory = shapeViewFactory;
        this.glyphBuilderFactory = glyphBuilderFactory;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public MutableShape<Object, ShapeView> build( final Object definition,
                                                  final AbstractCanvasHandler context ) {
        final String id = definitionManager.adapters().forDefinition().getId( definition );
        final ShapeDef<Object> proxy = getShapeDef( id );
        return build( definition, proxy, context );
    }

    @SuppressWarnings( "unchecked" )
    protected MutableShape<Object, ShapeView> build( final Object definition,
                                                     final ShapeDef<Object> proxy,
                                                     final AbstractCanvasHandler context ) {
        boolean found = false;
        MutableShape<Object, ShapeView> shape = null;
        if ( isCircle( proxy ) ) {
            final CircleShapeDef<Object> circleProxy = ( CircleShapeDef<Object> ) proxy;
            final double radius = circleProxy.getRadius( definition );
            final CircleView view =
                    shapeViewFactory.circle( radius );
            shape = new CircleShape( view, circleProxy );
            found = true;
        }
        if ( isRing( proxy ) ) {
            final RingShapeDef<Object> ringProxy = ( RingShapeDef<Object> ) proxy;
            final double oRadius = ringProxy.getOuterRadius( definition );
            final RingView view =
                    shapeViewFactory.ring( oRadius );
            shape = new RingShape( view, ringProxy );
            found = true;
        }
        if ( isRectangle( proxy ) ) {
            final RectangleShapeDef<Object> rectangleProxy = ( RectangleShapeDef<Object> ) proxy;
            final double width = rectangleProxy.getWidth( definition );
            final double height = rectangleProxy.getHeight( definition );
            final double cr = rectangleProxy.getCornerRadius( definition );
            final RectangleView view =
                    shapeViewFactory.rectangle( width, height, cr );
            shape = new RectangleShape( view, rectangleProxy );
            found = true;
        }
        if ( isPolygon( proxy ) ) {
            final PolygonShapeDef<Object> polygonProxy = ( PolygonShapeDef<Object> ) proxy;
            final double radius = polygonProxy.getRadius( definition );
            final String fillColor = polygonProxy.getBackgroundColor( definition );
            final PolygonView view =
                    shapeViewFactory.polygon( radius,
                            fillColor );
            shape = new PolygonShape( view, polygonProxy );
            found = true;
        }
        if ( isConnector( proxy ) ) {
            final ConnectorShapeDef<Object> polygonProxy = ( ConnectorShapeDef<Object> ) proxy;
            final ConnectorView view = shapeViewFactory.connector( 0, 0, 100, 100 );
            shape = new ConnectorShape( view, polygonProxy );
            found = true;
        }
        if ( isStaticIcon( proxy ) ) {
            final IconShapeDef<Object> iconProxy =
                    ( IconShapeDef<Object> ) proxy;
            final org.kie.workbench.common.stunner.shapes.def.icon.statics.Icons icon = iconProxy.getIcon( definition );
            final StaticIconShapeView view =
                    shapeViewFactory.staticIcon( icon );
            shape = new StaticIconShape( view, iconProxy );
            found = true;
        }
        if ( isPicture( proxy ) ) {
            final PictureShapeDef pictureProxy = ( PictureShapeDef ) proxy;
            final Object pictureSource = pictureProxy.getPictureSource( definition );
            if ( null != pictureSource ) {
                final double width = pictureProxy.getWidth( definition );
                final double height = pictureProxy.getHeight( definition );
                final PictureShapeView view = shapeViewFactory.picture( pictureSource, width, height );
                shape = new PictureShape( view, pictureProxy );
            }
            found = true;
        }
        if ( isDynamicIcon( proxy ) ) {
            final org.kie.workbench.common.stunner.shapes.def.icon.dynamics.IconShapeDef iconProxy =
                    ( org.kie.workbench.common.stunner.shapes.def.icon.dynamics.IconShapeDef ) proxy;
            final Icons icon = DynamicIconShape.getIcon( definition, iconProxy );
            final double width = iconProxy.getWidth( definition );
            final double height = iconProxy.getHeight( definition );
            final DynamicIconShapeView view =
                    shapeViewFactory.dynamicIcon( icon, width, height );
            shape = new DynamicIconShape( view, iconProxy );
            found = true;
        }
        // Add children, if any.
        if ( null != shape
                && shape instanceof AbstractCompositeShape
                && proxy instanceof HasChildShapeDefs ) {
            final HasChildShapeDefs<Object> hasChildren = ( HasChildShapeDefs<Object> ) proxy;
            final Map<ShapeDef<Object>, HasChildren.Layout> childShapeDefs = hasChildren.getChildShapeDefs();
            if ( null != childShapeDefs && !childShapeDefs.isEmpty() ) {
                for ( final Map.Entry<ShapeDef<Object>, HasChildren.Layout> entry : childShapeDefs.entrySet() ) {
                    final ShapeDef<Object> child = entry.getKey();
                    final HasChildren.Layout layout = entry.getValue();
                    final MutableShape<Object, ShapeView> childShape = this.build( definition, child, context );
                    if ( null != childShape ) {
                        ( ( AbstractCompositeShape ) shape ).addChild( ( AbstractShape ) childShape, layout );
                    }
                }
            }
        }
        if ( !found ) {
            final String id = definitionManager.adapters().forDefinition().getId( definition );
            throw new RuntimeException( "This factory supports [" + id + "] but cannot built a shape for it." );
        }
        return shape;
    }

    private boolean isCircle( final ShapeDef<Object> proxy ) {
        return proxy instanceof CircleShapeDef;
    }

    private boolean isRing( final ShapeDef<Object> proxy ) {
        return proxy instanceof RingShapeDef;
    }

    private boolean isRectangle( final ShapeDef<Object> proxy ) {
        return proxy instanceof RectangleShapeDef;
    }

    private boolean isPolygon( final ShapeDef<Object> proxy ) {
        return proxy instanceof PolygonShapeDef;
    }

    private boolean isConnector( final ShapeDef<Object> proxy ) {
        return proxy instanceof ConnectorShapeDef;
    }

    private boolean isDynamicIcon( final ShapeDef<Object> proxy ) {
        return proxy instanceof org.kie.workbench.common.stunner.shapes.def.icon.dynamics.IconShapeDef;
    }

    private boolean isStaticIcon( final ShapeDef<Object> proxy ) {
        return proxy instanceof IconShapeDef;
    }

    private boolean isPicture( final ShapeDef<Object> proxy ) {
        return proxy instanceof PictureShapeDef;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    protected Glyph glyph( final Class<?> clazz,
                           final double width,
                           final double height ) {
        final ShapeDef<Object> shapeDef = getShapeDef( clazz );
        final GlyphDef<Object> glyphDef = shapeDef.getGlyphDef();
        return glyphBuilderFactory
                .getBuilder( glyphDef )
                .definitionType( clazz )
                .glyphDef( glyphDef )
                .factory( this )
                .height( height )
                .width( width )
                .build();
    }

}
