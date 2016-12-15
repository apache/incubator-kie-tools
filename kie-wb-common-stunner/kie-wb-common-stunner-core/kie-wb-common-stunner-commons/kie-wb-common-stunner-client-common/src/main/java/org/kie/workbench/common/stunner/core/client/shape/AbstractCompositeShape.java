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

package org.kie.workbench.common.stunner.core.client.shape;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompositeShape<W, E extends Node<View<W>, Edge>, V extends ShapeView>
        extends AbstractShape<W, E, V>
        implements HasChildren<AbstractShape<W, Node<View<W>, Edge>, ?>> {

    private final List<AbstractShape<W, Node<View<W>, Edge>, ?>> children = new LinkedList<AbstractShape<W, Node<View<W>, Edge>, ?>>();

    public AbstractCompositeShape( final V view ) {
        super( view );
    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void addChild( final AbstractShape<W, Node<View<W>, Edge>, ?> child,
                          final Layout layout ) {
        final HasChildren<ShapeView<?>> view =
                ( HasChildren<ShapeView<?>> ) getShapeView();
        children.add( child );
        view.addChild( child.getShapeView(), layout );

    }

    @Override
    @SuppressWarnings( "unchecked" )
    public void removeChild( final AbstractShape<W, Node<View<W>, Edge>, ?> child ) {
        final HasChildren<ShapeView<?>> view =
                ( HasChildren<ShapeView<?>> ) getShapeView();
        children.remove( child );
        view.removeChild( child.getShapeView() );

    }

    @Override
    public Iterable<AbstractShape<W, Node<View<W>, Edge>, ?>> getChildren() {
        return children;
    }

    @Override
    public void applyProperties( final E element,
                                 final MutationContext mutationContext ) {
        super.applyProperties( element, mutationContext );
        // Apply properties to children shapes.
        for ( final AbstractShape<W, Node<View<W>, Edge>, ?> child : children ) {
            child.applyProperties( element, mutationContext );
        }

    }

    @Override
    public void applyProperty( final E element,
                               final String propertyId,
                               final Object value,
                               final MutationContext mutationContext ) {
        super.applyProperty( element, propertyId, value, mutationContext );
        // Apply property to children shapes.
        for ( final AbstractShape<W, Node<View<W>, Edge>, ?> child : children ) {
            child.applyProperty( element, propertyId, value, mutationContext );
        }

    }

    @Override
    protected void doDestroy() {
        if ( !children.isEmpty() ) {
            for ( final AbstractShape<W, Node<View<W>, Edge>, ?> child : children ) {
                child.destroy();

            }

        }
        children.clear();

    }

}
