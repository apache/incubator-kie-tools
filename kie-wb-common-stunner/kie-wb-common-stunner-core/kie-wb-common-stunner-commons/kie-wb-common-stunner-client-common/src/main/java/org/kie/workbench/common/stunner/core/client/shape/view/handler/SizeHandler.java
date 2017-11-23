/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.view.handler;

import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.core.graph.content.view.BoundsImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
/**
 * The default view size related attributes handler to generic shape views.
 *
 * It allows specifying functions which provide the different attributes for
 * handling the resulting canvas shape's size.
 *
 * @param <W> The domain's object type.
 * @param <V> The shape view type.
 */
public class SizeHandler<W, V extends ShapeView> implements ShapeViewHandler<View<W>, V> {

    private final Function<W, Double> widthProvider;
    private final Function<W, Double> heightProvider;
    private final Function<W, Double> radiusProvider;

    private SizeHandler(final Function<W, Double> widthProvider,
                        final Function<W, Double> heightProvider,
                        final Function<W, Double> radiusProvider) {
        this.widthProvider = widthProvider;
        this.heightProvider = heightProvider;
        this.radiusProvider = radiusProvider;
    }

    @Override
    public void handle(final View<W> content,
                       final V view) {
        final W bean = content.getDefinition();
        final double boundsWidth = ((BoundsImpl) content.getBounds()).getWidth();
        final double boundsHeight = ((BoundsImpl) content.getBounds()).getHeight();
        if (view instanceof HasSize) {
            final Double beanWidth = widthProvider.apply(bean);
            final Double beanHeight = heightProvider.apply(bean);
            final double width = null != beanWidth ? beanWidth : boundsWidth;
            final double height = null != beanHeight ? beanHeight : boundsHeight;
            if (width > 0 && height > 0) {
                ((HasSize) view).setSize(width, height);
            }
        }
        if (view instanceof HasRadius) {
            final Double beanRadius = radiusProvider.apply(bean);
            final double radius = null != beanRadius ? beanRadius :
                    (boundsWidth > boundsHeight ?
                        boundsWidth / 2 :
                        boundsHeight / 2);
            if (radius > 0) {
                ((HasRadius) view).setRadius(radius);
            }
        }
    }

    public static class Builder<W, V extends ShapeView> {

        private Function<W, Double> widthProvider;
        private Function<W, Double> heightProvider;
        private Function<W, Double> radiusProvider;

        public Builder() {
            this.widthProvider = value -> null;
            this.heightProvider = value -> null;
        }

        public Builder<W, V> width(Function<W, Double> provider) {
            this.widthProvider = provider;
            return this;
        }

        public Builder<W, V> height(Function<W, Double> provider) {
            this.heightProvider = provider;
            return this;
        }

        public Builder<W, V> radius(Function<W, Double> provider) {
            this.radiusProvider = provider;
            return this;
        }

        public SizeHandler<W, V> build() {
            return new SizeHandler<>(widthProvider,
                                     heightProvider,
                                     radiusProvider);
        }
    }
}
