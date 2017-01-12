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

package org.kie.workbench.common.stunner.shapes.client;

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.shapes.client.view.BasicShapeView;
import org.kie.workbench.common.stunner.shapes.def.BasicShapeWithTitleDef;

public abstract class AbstractBasicShapeWithTitle<W, V extends BasicShapeView, P extends BasicShapeWithTitleDef<W>>
        extends BasicShapeWithTitle<W, V> {

    protected final transient P proxy;

    public AbstractBasicShapeWithTitle( final V view,
                                        final P proxy ) {
        super( view );
        this.proxy = proxy;
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void applyProperties( final Node<View<W>, Edge> element,
                                 final MutationContext mutationContext ) {
        // Apply font position and rotation for title.
        getShapeView().setTitlePosition( proxy.getFontPosition( getDefinition( element ) ) );
        getShapeView().setTitleRotation( proxy.getFontRotation( getDefinition( element ) ) );
        // Apply other font styles on parent.
        super.applyProperties( element,
                               mutationContext );
    }

    @Override
    protected String getBackgroundColor( final Node<View<W>, Edge> element ) {
        return proxy.getBackgroundColor( getDefinition( element ) );
    }

    @Override
    protected Double getBackgroundAlpha( final Node<View<W>, Edge> element ) {
        return proxy.getBackgroundAlpha( getDefinition( element ) );
    }

    @Override
    protected String getBorderColor( final Node<View<W>, Edge> element ) {
        return proxy.getBorderColor( getDefinition( element ) );
    }

    @Override
    protected Double getBorderSize( final Node<View<W>, Edge> element ) {
        return proxy.getBorderSize( getDefinition( element ) );
    }

    @Override
    protected Double getBorderAlpha( final Node<View<W>, Edge> element ) {
        return proxy.getBorderAlpha( getDefinition( element ) );
    }

    @Override
    protected String getNamePropertyValue( final Node<View<W>, Edge> element ) {
        return proxy.getNamePropertyValue( getDefinition( element ) );
    }

    @Override
    protected String getFontFamily( final Node<View<W>, Edge> element ) {
        return proxy.getFontFamily( getDefinition( element ) );
    }

    @Override
    protected String getFontColor( final Node<View<W>, Edge> element ) {
        return proxy.getFontColor( getDefinition( element ) );
    }

    @Override
    protected Double getFontSize( final Node<View<W>, Edge> element ) {
        return proxy.getFontSize( getDefinition( element ) );
    }

    @Override
    protected Double getFontBorderSize( final Node<View<W>, Edge> element ) {
        return proxy.getFontBorderSize( getDefinition( element ) );
    }
}
