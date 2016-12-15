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

package org.kie.workbench.common.stunner.client.lienzo.components.glyph;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.RootPanel;
import org.kie.workbench.common.stunner.core.client.components.glyph.ShapeGlyphDragHandler;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

import javax.enterprise.context.Dependent;

// TODO: Refactor implementing DragProxy<T>
@Dependent
public class ShapeGlyphDragHandlerImpl implements ShapeGlyphDragHandler<Group> {

    private static final int ZINDEX = Integer.MAX_VALUE;

    @Override
    public void show( final Glyph<Group> shapeGlyph,
                      final double x,
                      final double y,
                      final Callback callback ) {
        final double proxyWidth = shapeGlyph.getWidth();
        final double proxyHeight = shapeGlyph.getHeight();
        final Group dragShape = shapeGlyph.getGroup();
        dragShape.setX( proxyWidth / 2 );
        dragShape.setY( proxyHeight / 2 );
        final LienzoPanel dragProxyPanel = new LienzoPanel( ( ( int ) proxyWidth * 2 ),
                ( ( int ) proxyHeight * 2 ) );
        dragProxyPanel.getElement().getStyle().setCursor( Style.Cursor.MOVE );
        final Layer dragProxyLayer = new Layer();
        dragProxyLayer.add( dragShape );
        dragProxyPanel.add( dragProxyLayer );
        dragProxyLayer.batch();
        setDragProxyPosition(
                dragProxyPanel,
                proxyWidth,
                proxyHeight,
                x, y );
        attachDragProxyHandlers( dragProxyPanel, callback );
        RootPanel.get().add( dragProxyPanel );
    }

    private void setDragProxyPosition( final LienzoPanel dragProxyPanel,
                                       final double proxyWidth,
                                       final double proxyHeight,
                                       final double x,
                                       final double y ) {
        Style style = dragProxyPanel.getElement().getStyle();
        style.setPosition( Style.Position.ABSOLUTE );
        style.setLeft( x - ( proxyWidth / 2 ),
                Style.Unit.PX );
        style.setTop( y - ( proxyHeight / 2 ),
                Style.Unit.PX );
        style.setZIndex( ZINDEX );
    }

    private void attachDragProxyHandlers( final LienzoPanel floatingPanel, final Callback callback ) {
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
                final double x = mouseMoveEvent.getX();
                final double y = mouseMoveEvent.getY();
                callback.onMove( x, y );
            }
        }, MouseMoveEvent.getType() );
        //MouseUpEvent
        handlerRegs[ 1 ] = RootPanel.get().addDomHandler( new MouseUpHandler() {

            @Override
            public void onMouseUp( final MouseUpEvent mouseUpEvent ) {
                handlerRegs[ 0 ].removeHandler();
                handlerRegs[ 1 ].removeHandler();
                RootPanel.get().remove( floatingPanel );
                final double x = mouseUpEvent.getX();
                final double y = mouseUpEvent.getY();
                callback.onComplete( x, y );
            }
        }, MouseUpEvent.getType() );
    }

}
