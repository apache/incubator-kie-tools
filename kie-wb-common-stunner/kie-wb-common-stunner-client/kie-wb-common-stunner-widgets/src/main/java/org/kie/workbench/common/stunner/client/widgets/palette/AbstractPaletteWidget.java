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

package org.kie.workbench.common.stunner.client.widgets.palette;

import org.kie.workbench.common.stunner.core.client.ShapeManager;
import org.kie.workbench.common.stunner.core.client.components.palette.AbstractPalette;
import org.kie.workbench.common.stunner.core.client.components.palette.model.PaletteDefinition;
import org.kie.workbench.common.stunner.core.client.service.ClientFactoryService;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;

public abstract class AbstractPaletteWidget<D extends PaletteDefinition, V extends PaletteWidgetView>
        extends AbstractPalette<D>
        implements PaletteWidget<D, V> {

    protected final ClientFactoryService clientFactoryServices;
    protected ItemDropCallback itemDropCallback;
    protected V view;
    protected int maxWidth;
    protected int maxHeight;

    public AbstractPaletteWidget( final ShapeManager shapeManager,
                                  final ClientFactoryService clientFactoryServices,
                                  final V view ) {
        super( shapeManager );
        this.clientFactoryServices = clientFactoryServices;
        this.view = view;
    }

    protected abstract ShapeFactory getShapeFactory();

    public abstract double getIconSize();

    @Override
    public PaletteWidget<D, V> onItemDrop( final ItemDropCallback callback ) {
        this.itemDropCallback = callback;
        return this;
    }

    @Override
    protected void beforeBind() {
        super.beforeBind();
        getView().clear();
        getView().showEmptyView( false );
    }

    @Override
    public void unbind() {
        // Only unbind if any definition is already bind.
        if ( null != paletteDefinition ) {
            getView().clear();
            getView().showEmptyView( true );
            this.paletteDefinition = null;

        }

    }

    @Override
    public PaletteWidget<D, V> setMaxWidth( final int maxWidth ) {
        this.maxWidth = maxWidth;
        return this;
    }

    @Override
    public PaletteWidget<D, V> setMaxHeight( final int maxHeight ) {
        this.maxHeight = maxHeight;
        return this;
    }

    public void onDragProxyMove( final String definitionId,
                                 final double x,
                                 final double y ) {
    }

    public void onDragProxyComplete( final String definitionId ) {
        onDragProxyComplete( definitionId, -1, -1 );

    }

    @SuppressWarnings( "unchecked" )
    public void onDragProxyComplete( final String definitionId,
                                     final double x,
                                     final double y ) {
        if ( null != itemDropCallback ) {
            final Object definition = clientFactoryServices.getClientFactoryManager().newDefinition( definitionId );
            final ShapeFactory<?, ?, ? extends Shape> factory = getShapeFactory();
            // Fire the callback as shape dropped onto the target canvas.
            itemDropCallback.onDropItem( definition, factory, x, y );
        }
    }

    public Glyph<?> getShapeGlyph( final String definitionId ) {
        return getShapeFactory().glyph( definitionId, getIconSize(), getIconSize() );
    }

    @Override
    protected void doDestroy() {
        getView().destroy();
        this.itemDropCallback = null;

    }

    @Override
    public V getView() {
        return view;
    }

}
