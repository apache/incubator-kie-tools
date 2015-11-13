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
package org.uberfire.ext.wires.core.client.factories;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Shape;
import org.uberfire.ext.wires.core.api.factories.FactoryHelper;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxy;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyCompleteCallback;
import org.uberfire.ext.wires.core.api.factories.ShapeDragProxyPreviewCallback;
import org.uberfire.ext.wires.core.api.factories.ShapeFactory;
import org.uberfire.ext.wires.core.api.factories.ShapeGlyph;

/**
 * Base implementation of a ShapeFactory to avoid unnecessary boiler-plate code
 */
public abstract class AbstractBaseFactory<T extends Shape<T>> implements ShapeFactory<T> {

    @Override
    public ShapeGlyph getGlyph() {
        final T shape = makeShape();
        final Group group = new Group();
        group.add( shape );

        return new ShapeGlyph() {
            @Override
            public Group getGroup() {
                return group;
            }

            @Override
            public double getWidth() {
                return AbstractBaseFactory.this.getWidth();
            }

            @Override
            public double getHeight() {
                return AbstractBaseFactory.this.getHeight();
            }
        };
    }

    @Override
    public ShapeDragProxy getDragProxy( final @SuppressWarnings("unused") FactoryHelper helper,
                                        final ShapeDragProxyPreviewCallback dragPreviewCallback,
                                        final ShapeDragProxyCompleteCallback dragEndCallBack ) {
        final T shape = makeShape();
        final Group group = new Group();
        group.add( shape );

        return new ShapeDragProxy() {
            @Override
            public Group getDragGroup() {
                return group;
            }

            @Override
            public void onDragPreview( final double x,
                                       final double y ) {
                dragPreviewCallback.callback( x,
                                              y );
            }

            @Override
            public void onDragComplete( final double x,
                                        final double y ) {
                dragEndCallBack.callback( x,
                                          y );
            }

            @Override
            public double getWidth() {
                return AbstractBaseFactory.this.getWidth();
            }

            @Override
            public double getHeight() {
                return AbstractBaseFactory.this.getHeight();
            }

        };
    }

    protected abstract T makeShape();

    protected abstract double getWidth();

    protected abstract double getHeight();

}
