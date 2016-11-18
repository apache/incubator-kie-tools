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

package org.kie.workbench.common.stunner.shapes.client.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Ring;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.LayoutContainer;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import com.ait.lienzo.client.core.shape.wires.event.*;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.event.HandlerRegistrationImpl;

/**
 * The lienzo view implementation for the Ring shape.
 *
 * TODO: Disabling for now the resize for rings - ARC resize is not implemented yet on lienzo side.
 */
public class RingView extends BasicShapeView<RingView>
        implements HasRadius<RingView> {

    private static final int INNER_RADIUS_FACTOR = 2;

    private Ring ring;
    private final HandlerRegistrationImpl registrations = new HandlerRegistrationImpl();

    public RingView( final double radius ) {
        super( initPath( new MultiPath(), radius ) );
        ring = new Ring( getInnerRadius( radius ), getOuterRadius( radius ) );
        addChild( ring, LayoutContainer.Layout.CENTER );
        // TODO: initResizeHandlers();
        super.setResizable( false );
    }

    @Override
    public Shape<?> getShape() {
        return ring;
    }

    @Override
    public RingView setRadius( final double radius ) {
        initPath( getPath().clear(), radius );
        updateFillGradient( radius * 2, radius * 2 );
        final double o = getOuterRadius( radius );
        final double i = getInnerRadius( radius );
        ring.setOuterRadius( o );
        ring.setInnerRadius( i );
        refresh();
        return this;
    }

    @SuppressWarnings( "unchecked" )
    public RingView setOuterRadius( final double radius ) {
        return setRadius( radius );

    }

    @SuppressWarnings( "unchecked" )
    public RingView setInnerRadius( final double inner ) {
        return setOuterRadius( inner * INNER_RADIUS_FACTOR );

    }

    @Override
    public WiresShape setResizable( final boolean resizable ) {
        return super.setResizable( false );
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        registrations.removeHandler();
        ring.removeFromParent();
    }

    private void initResizeHandlers() {
        registrations.register(
                this.addWiresResizeStartHandler( new WiresResizeStartHandler() {
                    @Override
                    public void onShapeResizeStart( WiresResizeStartEvent wiresResizeStartEvent ) {
                        resize( wiresResizeStartEvent.getWidth(), wiresResizeStartEvent.getHeight() );
                    }
                } )
        );
        registrations.register(
                this.addWiresResizeStepHandler( new WiresResizeStepHandler() {
                    @Override
                    public void onShapeResizeStep( WiresResizeStepEvent wiresResizeStepEvent ) {
                        resize( wiresResizeStepEvent.getWidth(), wiresResizeStepEvent.getHeight() );
                    }
                } )
        );
        registrations.register(
                this.addWiresResizeEndHandler( new WiresResizeEndHandler() {
                    @Override
                    public void onShapeResizeEnd( WiresResizeEndEvent wiresResizeEndEvent ) {
                        resize( wiresResizeEndEvent.getWidth(), wiresResizeEndEvent.getHeight() );
                    }
                } )
        );
    }

    private void resize( final double width, final double height ) {
        this.setRadius( width >= height ? height : width );
    }

    private static MultiPath initPath( final MultiPath path, final double radius ) {
        return path.rect( 0, 0, radius * 2, radius * 2 )
                .setStrokeWidth( 0 )
                .setStrokeAlpha( 0 );
    }

    private static double getOuterRadius( final double radius ) {
        return radius;
    }

    private static double getInnerRadius( final double radius ) {
        return radius / INNER_RADIUS_FACTOR;
    }

}
