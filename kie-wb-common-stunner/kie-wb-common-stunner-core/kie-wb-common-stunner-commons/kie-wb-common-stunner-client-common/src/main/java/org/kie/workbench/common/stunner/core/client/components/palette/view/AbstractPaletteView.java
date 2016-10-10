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

package org.kie.workbench.common.stunner.core.client.components.palette.view;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPaletteView<T, L, I extends PaletteElementView> implements PaletteView<T, L, I> {

    protected final List<I> items = new LinkedList<I>();
    protected double x = 0;
    protected double y = 0;

    protected abstract void doClear();

    @Override
    @SuppressWarnings( "unchecked" )
    public T setX( final double x ) {
        this.x = x;
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T setY( final double y ) {
        this.y = y;
        return ( T ) this;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T add( final I item ) {
        items.add( item );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T set( final int pos,
                  final I item ) {
        items.set( pos, item );
        return ( T ) this;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public T remove( final int pos ) {
        items.remove( pos );
        return ( T ) this;
    }

    @Override
    public T clear() {
        doClear();
        items.clear();
        return ( T ) this;
    }

}
