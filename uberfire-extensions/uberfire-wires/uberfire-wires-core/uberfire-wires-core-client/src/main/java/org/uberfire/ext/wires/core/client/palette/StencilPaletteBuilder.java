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
package org.uberfire.ext.wires.core.client.palette;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.ait.lienzo.client.core.event.NodeMouseDownEvent;
import com.ait.lienzo.client.core.event.NodeMouseDownHandler;
import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.ait.lienzo.shared.core.types.TextBaseLine;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.uberfire.ext.wires.core.api.events.ShapeDragCompleteEvent;
import org.uberfire.ext.wires.core.api.events.ShapeDragPreviewEvent;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxy;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyCompleteCallback;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyPreviewCallback;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.factories.ShapeGlyph;
import org.uberfire.ext.wires.core.api.shapes.WiresBaseShape;
import org.uberfire.ext.wires.core.client.util.ShapeFactoryUtil;

@ApplicationScoped
public class StencilPaletteBuilder {

    private static final int ZINDEX = Integer.MAX_VALUE;
    private static final double GLYPH_WIDTH = 65;
    private static final double GLYPH_HEIGHT = 65;

    @Inject
    private Event<ShapeDragCompleteEvent> shapeDragCompleteEvent;

    @Inject
    private Event<ShapeDragPreviewEvent> shapeDragPreviewEvent;

    public PaletteShape build( final LienzoPanel dragProxyParentPanel,
                               final FactoryHelper helper,
                               final ShapeFactory factory ) {
        final PaletteShape paletteShape = new PaletteShape();
        final ShapeGlyph glyph = drawGlyph( factory,
                                            helper );
        final Text description = drawDescription( factory,
                                                  helper );
        final Rectangle bounding = drawBoundingBox( factory,
                                                    helper );

        //Callback is invoked when the drag operation ends
        final ShapeDragProxyCompleteCallback dragCompleteCallback = new ShapeDragProxyCompleteCallback() {
            @Override
            public void callback( final double x,
                                  final double y ) {
                shapeDragCompleteEvent.fire( new ShapeDragCompleteEvent( factory.getShape( helper ),
                                                                         x,
                                                                         y ) );
            }
        };

        //Callback is invoked during the drag operation
        final WiresBaseShape shape = factory.getShape( helper );
        final ShapeDragProxyPreviewCallback dragPreviewCallback = new ShapeDragProxyPreviewCallback() {
            @Override
            public void callback( final double x,
                                  final double y ) {
                shapeDragPreviewEvent.fire( new ShapeDragPreviewEvent( shape,
                                                                       x,
                                                                       y ) );
            }
        };

        //Attach handles for drag operation
        final ShapeDragProxy dragProxy = factory.getDragProxy( helper,
                                                               dragPreviewCallback,
                                                               dragCompleteCallback );
        addDragHandlers( dragProxyParentPanel,
                         dragProxy,
                         paletteShape );

        //Build Palette Shape
        paletteShape.setBounding( bounding );
        paletteShape.setGroup( scaleGlyph( glyph ) );
        paletteShape.setDescription( description );

        return paletteShape;
    }

    /**
     * Return a ShapeGlyph that represents the Factory in the Palette.
     * This implementation delegates this to the ShapeFactory.
     * @param factory ShapeFactory that is capable of providing a default ShapeGlyph
     * @param helper FactoryHelper that might provide additional information to build a Glyph. Unused by this implementation.
     * @return A ShapeGlyph object or null if one is not required.
     */
    protected ShapeGlyph drawGlyph( final ShapeFactory factory,
                                    final @SuppressWarnings("unused") FactoryHelper helper ) {
        return factory.getGlyph();
    }

    /**
     * Scale the Shape provided by the ShapeFactory as the glyph to fit the PaletteShape.
     * @param glyph
     * @return
     */
    protected Group scaleGlyph( final ShapeGlyph glyph ) {
        final double sx = GLYPH_WIDTH / glyph.getWidth();
        final double sy = GLYPH_HEIGHT / glyph.getHeight();
        final Group group = glyph.getGroup();
        return group.setX( ShapeFactoryUtil.WIDTH_BOUNDING / 2 ).setY( ShapeFactoryUtil.WIDTH_BOUNDING / 2 ).setScale( sx,
                                                                                                                       sy );
    }

    /**
     * Return Text that represents the Factory in the Palette.
     * This implementation delegates this to the ShapeFactory.
     * @param factory ShapeFactory that is capable of providing a default description
     * @param helper FactoryHelper that might provide additional information to build a Glyph. Unused by this implementation.
     * @return A Text object or null if one is not required.
     */
    protected Text drawDescription( final ShapeFactory factory,
                                    final @SuppressWarnings("unused") FactoryHelper helper ) {
        Text text = new Text( factory.getShapeDescription(),
                              ShapeFactoryUtil.FONT_FAMILY_DESCRIPTION,
                              ShapeFactoryUtil.FONT_SIZE_DESCRIPTION );
        text.setFillColor( ShapeFactoryUtil.RGB_TEXT_DESCRIPTION );
        text.setTextBaseLine( TextBaseLine.MIDDLE );
        text.setX( 5 );
        text.setY( 85 );
        return text;
    }

    /**
     * Return a Rectangle that is the bounding box for the PaletteShape.
     * This implementation does not use the ShapeFactory but sub-classes could.
     * @param factory ShapeFactory that might be useful for sub-classes to build a bounding Rectangle
     * @param helper FactoryHelper that might provide additional information to build a Glyph. Unused by this implementation.
     * @return A Rectangle object or null if one is not required.
     */
    protected Rectangle drawBoundingBox( final @SuppressWarnings("unused") ShapeFactory factory,
                                         final @SuppressWarnings("unused") FactoryHelper helper ) {
        final Rectangle boundingBox = new Rectangle( ShapeFactoryUtil.WIDTH_BOUNDING,
                                                     ShapeFactoryUtil.HEIGHT_BOUNDING );
        boundingBox.setStrokeColor( ShapeFactoryUtil.RGB_STROKE_BOUNDING )
                .setStrokeWidth( 1 )
                .setFillColor( ShapeFactoryUtil.RGB_FILL_BOUNDING )
                .setDraggable( false );
        return boundingBox;
    }

    private void addDragHandlers( final LienzoPanel dragProxyParentPanel,
                                  final ShapeDragProxy proxy,
                                  final Group shape ) {
        shape.addNodeMouseDownHandler( getShapeDragStartHandler( dragProxyParentPanel,
                                                                 proxy ) );
    }

    private NodeMouseDownHandler getShapeDragStartHandler( final LienzoPanel dragProxyParentPanel,
                                                           final ShapeDragProxy proxy ) {
        return new NodeMouseDownHandler() {

            @Override
            public void onNodeMouseDown( final NodeMouseDownEvent event ) {
                final double proxyWidth = proxy.getWidth();
                final double proxyHeight = proxy.getHeight();
                final Group dragShape = proxy.getDragGroup();
                dragShape.setX( proxyWidth / 2 );
                dragShape.setY( proxyHeight / 2 );

                final LienzoPanel dragProxyPanel = new LienzoPanel( (int) proxyWidth,
                                                                    (int) proxyHeight );
                final Layer dragProxyLayer = new Layer();
                dragProxyLayer.add( dragShape );
                dragProxyPanel.add( dragProxyLayer );
                dragProxyLayer.batch();

                setDragProxyPosition( dragProxyParentPanel,
                                      dragProxyPanel,
                                      proxyWidth,
                                      proxyHeight,
                                      event );
                attachDragProxyHandlers( dragProxyPanel,
                                         proxy );

                RootPanel.get().add( dragProxyPanel );
            }
        };
    }

    private void setDragProxyPosition( final LienzoPanel dragProxyParentPanel,
                                       final LienzoPanel dragProxyPanel,
                                       final double proxyWidth,
                                       final double proxyHeight,
                                       final NodeMouseDownEvent event ) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setPosition( Style.Position.ABSOLUTE );
        style.setLeft( dragProxyParentPanel.getAbsoluteLeft() + event.getX() - ( proxyWidth / 2 ),
                       Style.Unit.PX );
        style.setTop( dragProxyParentPanel.getAbsoluteTop() + event.getY() - ( proxyHeight / 2 ),
                      Style.Unit.PX );
        style.setZIndex( ZINDEX );
    }

    private void attachDragProxyHandlers( final LienzoPanel floatingPanel,
                                          final ShapeDragProxy proxy ) {
        final Style style = floatingPanel.getElement().getStyle();
        final HandlerRegistration[] handlerRegs = new HandlerRegistration[ 2 ];

        //MouseMoveEvents
        handlerRegs[ 0 ] = RootPanel.get().addDomHandler( new MouseMoveHandler() {

            @Override
            public void onMouseMove( final MouseMoveEvent mouseMoveEvent ) {
                style.setLeft( mouseMoveEvent.getX() - ( floatingPanel.getWidth() / 2 ),
                               Style.Unit.PX );
                style.setTop( mouseMoveEvent.getY() - ( floatingPanel.getHeight() / 2 ),
                              Style.Unit.PX );
                proxy.onDragPreview( mouseMoveEvent.getX(),
                                     mouseMoveEvent.getY() );
            }
        }, MouseMoveEvent.getType() );

        //MouseUpEvent
        handlerRegs[ 1 ] = RootPanel.get().addDomHandler( new MouseUpHandler() {

            @Override
            public void onMouseUp( final MouseUpEvent mouseUpEvent ) {
                handlerRegs[ 0 ].removeHandler();
                handlerRegs[ 1 ].removeHandler();
                RootPanel.get().remove( floatingPanel );
                proxy.onDragComplete( mouseUpEvent.getX(),
                                      mouseUpEvent.getY() );
            }
        }, MouseUpEvent.getType() );
    }

}