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

package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Shape;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

public abstract class AbstractShapeView<T> extends WiresShape
        implements
        ShapeView<T> {

    private String uuid;
    private int zindex;

    public AbstractShapeView( final MultiPath path ) {
        super( path );
    }

    protected abstract void doDestroy();

    public Shape<?> getShape() {
        return getPath();
    }

    @Override
    public T setUUID( final String uuid ) {
        this.uuid = uuid;
        this.getGroup().setUserData( UUID_PREFFIX + uuid );
        return ( T ) this;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public T setZIndex( final int zindez ) {
        this.zindex = zindez;
        return ( T ) this;
    }

    @Override
    public int getZIndex() {
        return zindex;
    }

    @Override
    public T setDragEnabled( boolean isDraggable ) {
        this.setDraggable( isDraggable );
        return ( T ) this;
    }

    @Override
    public double getShapeX() {
        return getContainer().getAttributes().getX();
    }

    @Override
    public double getShapeY() {
        return getContainer().getAttributes().getY();
    }

    @Override
    public T setShapeX( final double x ) {
        getContainer().getAttributes().setX( x );
        return ( T ) this;
    }

    @Override
    public T setShapeY( final double y ) {
        getContainer().getAttributes().setY( y );
        return ( T ) this;
    }

    @Override
    public String getFillColor() {
        return getShape().getFillColor();
    }

    @Override
    public T setFillColor( final String color ) {
        getShape().setFillColor( color );
        return ( T ) this;
    }

    @Override
    public double getFillAlpha() {
        return getShape().getFillAlpha();
    }

    @Override
    public T setFillAlpha( final double alpha ) {
        getShape().setFillAlpha( alpha );
        return ( T ) this;
    }

    @Override
    public String getStrokeColor() {
        return getShape().getStrokeColor();
    }

    @Override
    public T setStrokeColor( final String color ) {
        getShape().setStrokeColor( color );
        return ( T ) this;
    }

    @Override
    public double getStrokeAlpha() {
        return getShape().getStrokeAlpha();
    }

    @Override
    public T setStrokeAlpha( final double alpha ) {
        getShape().setStrokeAlpha( alpha );
        return ( T ) this;
    }

    @Override
    public double getStrokeWidth() {
        return getShape().getStrokeWidth();
    }

    @Override
    public T setStrokeWidth( final double width ) {
        getShape().setStrokeWidth( width );
        return ( T ) this;
    }

    @Override
    public T moveToTop() {
        getContainer().moveToTop();
        return ( T ) this;
    }

    @Override
    public T moveToBottom() {
        getContainer().moveToBottom();
        return ( T ) this;
    }

    @Override
    public T moveUp() {
        getContainer().moveUp();
        return ( T ) this;
    }

    @Override
    public T moveDown() {
        getContainer().moveDown();
        return ( T ) this;
    }

    @Override
    public void destroy() {
        // Implementations can clear its state here.
        doDestroy();
        // Remove me.
        this.removeFromParent();

    }

}
