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

package org.kie.workbench.common.stunner.lienzo.palette;

import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.shared.core.types.ColorName;
import com.google.gwt.user.client.Timer;

public class HoverPalette extends AbstractPalette<HoverPalette> {

    public interface CloseCallback {

        void onClose();
    }

    private Rectangle decorator;
    private Timer timer;
    private CloseCallback closeCallback;
    private int timeout = 800;

    public HoverPalette setCloseCallback( final CloseCallback callback ) {
        this.closeCallback = callback;
        return this;
    }

    public HoverPalette setTimeout( final int timeout ) {
        this.timeout = timeout;
        return this;
    }

    @Override
    protected void doShowItem( final int index,
                               final double x,
                               final double y,
                               final double itemX,
                               final double itemY ) {
        super.doShowItem( index,
                          x,
                          y,
                          itemX,
                          itemY );
        stopTimeout();
    }

    @Override
    protected void doItemOut( final int index ) {
        super.doItemOut( index );
        startTimeout();
    }

    @Override
    public HoverPalette build( final Item... items ) {
        HoverPalette result = super.build( items );
        drawPaletteDecorator();
        startTimeout();
        return result;
    }

    @Override
    public void doRedraw() {
        drawPaletteDecorator();
    }

    @Override
    public HoverPalette clear() {
        HoverPalette result = super.clear();
        return result;
    }

    @Override
    public HoverPalette clearItems() {
        super.clearItems();
        removePaletteDecorator();
        return this;
    }

    @Override
    protected void beforeBuild() {
        super.beforeBuild();
        registerHoverEventHandlers();
    }

    @Override
    protected void afterBuild() {
        super.afterBuild();
    }

    private void addPaletteDecorator() {
        if ( null == decorator ) {
            decorator = new Rectangle( 1,
                                       1 )
                    .setFillColor( ColorName.LIGHTGREY );
            this.add( decorator );
            itemsGroup.moveToTop();
            handlerRegistrationManager.register(
                    decorator.addNodeMouseEnterHandler( event -> stopTimeout() )
            );
            handlerRegistrationManager.register(
                    decorator.addNodeMouseExitHandler( event -> startTimeout() )
            );
        }
    }

    private void removePaletteDecorator() {
        stopTimeout();
        this.handlerRegistrationManager.removeHandler();
        if ( null != decorator ) {
            this.decorator.removeFromParent();
            this.decorator = null;
        }
    }

    private void drawPaletteDecorator() {
        if ( null == decorator ) {
            addPaletteDecorator();
        }
        if ( null != decorator ) {
            final double halfOfPadding = padding != 0 ? padding / 2 : 0;
            final BoundingBox boundingBox = itemsGroup.getBoundingBox();
            final double width = boundingBox.getWidth();
            final double height = boundingBox.getHeight();
            final double w = width + halfOfPadding;
            final double h = height + halfOfPadding;
            decorator
                    .setWidth( w )
                    .setHeight( h )
                    .setX( x + ( halfOfPadding / 2 ) )
                    .setY( y + halfOfPadding );
        }
    }

    private void registerHoverEventHandlers() {
        handlerRegistrationManager.register(
                this.addNodeMouseEnterHandler( event -> stopTimeout() )
        );
        handlerRegistrationManager.register(
                this.addNodeMouseExitHandler( event -> startTimeout() )
        );
    }

    public void startTimeout() {
        if ( null == timer || !timer.isRunning() ) {
            timer = new Timer() {
                @Override
                public void run() {
                    if ( null != HoverPalette.this.closeCallback ) {
                        HoverPalette.this.closeCallback.onClose();
                    }
                }
            };
            timer.schedule( timeout );
        }
    }

    public void stopTimeout() {
        if ( null != timer && timer.isRunning() ) {
            timer.cancel();
        }
    }
}
