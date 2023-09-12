/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.shape.view.handler;

import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

/**
 * The default view size related attributes handler to generic shape views.
 * <p>
 * It allows specifying functions which provide the different attributes for
 * handling the resulting canvas shape's size.
 * @param <W> The domain's object type.
 * @param <V> The shape view type.
 */
public class SizeHandler<W, V extends ShapeView> implements ShapeViewHandler<View<W>, V> {

    private final Function<W, Double> widthProvider;
    private final Function<W, Double> minWidthProvider;
    private final Function<W, Double> maxWidthProvider;
    private final Function<W, Double> heightProvider;
    private final Function<W, Double> minHeightProvider;
    private final Function<W, Double> maxHeightProvider;
    private final Function<W, Double> radiusProvider;
    private final Function<W, Double> minRadiusProvider;
    private final Function<W, Double> maxRadiusProvider;

    private SizeHandler(final Function<W, Double> widthProvider,
                        final Function<W, Double> minWidthProvider,
                        final Function<W, Double> maxWidthProvider,
                        final Function<W, Double> heightProvider,
                        final Function<W, Double> minHeightProvider,
                        final Function<W, Double> maxHeightProvider,
                        final Function<W, Double> radiusProvider,
                        final Function<W, Double> minRadiusProvider,
                        final Function<W, Double> maxRadiusProvider) {
        this.widthProvider = widthProvider;
        this.minWidthProvider = minWidthProvider;
        this.maxWidthProvider = maxWidthProvider;
        this.heightProvider = heightProvider;
        this.minHeightProvider = minHeightProvider;
        this.maxHeightProvider = maxHeightProvider;
        this.radiusProvider = radiusProvider;
        this.minRadiusProvider = minRadiusProvider;
        this.maxRadiusProvider = maxRadiusProvider;
    }

    @Override
    public void handle(final View<W> content,
                       final V view) {
        final W bean = content.getDefinition();
        final double boundsWidth = content.getBounds().getWidth();
        final double boundsHeight = content.getBounds().getHeight();
        if (view instanceof HasSize) {
            final Double beanWidth = widthProvider.apply(bean);
            final Double beanHeight = heightProvider.apply(bean);
            final double width = null != beanWidth ? beanWidth : boundsWidth;
            final double height = null != beanHeight ? beanHeight : boundsHeight;
            final Double minWidth = minWidthProvider.apply(bean);
            final Double maxWidth = maxWidthProvider.apply(bean);
            final Double minHeight = minHeightProvider.apply(bean);
            final Double maxHeight = maxHeightProvider.apply(bean);

            HasSize hasSizeView = (HasSize) view;

            if (width > 0 && height > 0) {
                hasSizeView.setSize(width, height);
            }

            if (isValidSizeConstraint(minWidth)) {
                hasSizeView.setMinWidth(minWidth);
            }

            if (isValidSizeConstraint(maxWidth)) {
                hasSizeView.setMaxWidth(maxWidth);
            }

            if (isValidSizeConstraint(minHeight)) {
                hasSizeView.setMinHeight(minHeight);
            }

            if (isValidSizeConstraint(maxHeight)) {
                hasSizeView.setMaxHeight(maxHeight);
            }
        }
        if (view instanceof HasRadius) {
            final Double beanRadius = radiusProvider.apply(bean);
            final double radius = null != beanRadius ? beanRadius :
                    ShapeUtils.getRadiusForBoundingBox(boundsWidth, boundsHeight);
            final Double minRadius = minRadiusProvider.apply(bean);
            final Double maxRadius = maxRadiusProvider.apply(bean);
            if (radius > 0) {
                ((HasRadius) view).setRadius(radius);
            }

            if (isValidSizeConstraint(minRadius)) {
                ((HasRadius) view).setMinRadius(minRadius);
            }

            if (isValidSizeConstraint(maxRadius)) {
                ((HasRadius) view).setMaxRadius(maxRadius);
            }
        }
    }

    private static boolean isValidSizeConstraint(Double value) {
        if (value == null) {
            return true;
        }

        if (value > 0) {
            return true;
        }

        return false;
    }

    public static class Builder<W, V extends ShapeView> {

        private Function<W, Double> widthProvider;
        private Function<W, Double> minWidthProvider;
        private Function<W, Double> maxWidthProvider;
        private Function<W, Double> heightProvider;
        private Function<W, Double> minHeightProvider;
        private Function<W, Double> maxHeightProvider;
        private Function<W, Double> radiusProvider;
        private Function<W, Double> minRadiusProvider;
        private Function<W, Double> maxRadiusProvider;

        public Builder() {
            this.widthProvider = value -> null;
            this.minWidthProvider = value -> 25d;
            this.maxWidthProvider = value -> 30000d;
            this.heightProvider = value -> null;
            this.minHeightProvider = value -> 25d;
            this.maxHeightProvider = value -> 3000d;
            this.radiusProvider = value -> null;
            this.minRadiusProvider = value -> 10d;
            this.maxRadiusProvider = value -> 3000d;
        }

        public Builder<W, V> width(Function<W, Double> provider) {
            this.widthProvider = provider;
            return this;
        }

        public Builder<W, V> minWidth(Function<W, Double> provider) {
            this.minWidthProvider = provider;
            return this;
        }

        public Builder<W, V> maxWidth(Function<W, Double> provider) {
            this.maxWidthProvider = provider;
            return this;
        }

        public Builder<W, V> height(Function<W, Double> provider) {
            this.heightProvider = provider;
            return this;
        }

        public Builder<W, V> minHeight(Function<W, Double> provider) {
            this.minHeightProvider = provider;
            return this;
        }

        public Builder<W, V> maxHeight(Function<W, Double> provider) {
            this.maxHeightProvider = provider;
            return this;
        }

        public Builder<W, V> radius(Function<W, Double> provider) {
            this.radiusProvider = provider;
            return this;
        }

        public Builder<W, V> minRadius(Function<W, Double> provider) {
            this.minRadiusProvider = provider;
            return this;
        }

        public Builder<W, V> maxRadius(Function<W, Double> provider) {
            this.maxRadiusProvider = provider;
            return this;
        }

        public SizeHandler<W, V> build() {
            return new SizeHandler<>(widthProvider,
                                     minWidthProvider,
                                     maxWidthProvider,
                                     heightProvider,
                                     minHeightProvider,
                                     maxHeightProvider,
                                     radiusProvider,
                                     minRadiusProvider,
                                     maxRadiusProvider);
        }
    }
}
