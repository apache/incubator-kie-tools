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

import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeViewHandler;

/**
 * The default view attributes handler to generic shape views.
 * <p>
 * It allows specifying functions which provide the different view attributes
 * managed by this handler, like colors or borders, by having as an input the domain
 * object's instance.
 * <p>
 * This way view attribute values can be the ones present in some
 * instance's property value, or just some hardcoded ones.
 * <p>
 * If domain object's instance property values are not present (can be <code>null</code>),
 * this handler does not apply updates to the view, so the default attributes from the view
 * will be the ones used to render it.
 * @param <W> The domain's object type.
 * @param <V> The shape view type.
 */
public class ViewAttributesHandler<W, V extends ShapeView>
        implements ShapeViewHandler<W, V> {

    private final Function<W, Double> alphaProvider;
    private final Function<W, String> fillColorProvider;
    private final Function<W, Double> fillAlphaProvider;
    private final Function<W, String> strokeColorProvider;
    private final Function<W, Double> strokeWidthProvider;
    private final Function<W, Double> strokeAlphaProvider;

    private ViewAttributesHandler(final Function<W, Double> alphaProvider,
                                  final Function<W, String> fillColorProvider,
                                  final Function<W, Double> fillAlphaProvider,
                                  final Function<W, String> strokeColorProvider,
                                  final Function<W, Double> strokeWidthProvider,
                                  final Function<W, Double> strokeAlphaProvider) {
        this.alphaProvider = alphaProvider;
        this.fillColorProvider = fillColorProvider;
        this.fillAlphaProvider = fillAlphaProvider;
        this.strokeColorProvider = strokeColorProvider;
        this.strokeWidthProvider = strokeWidthProvider;
        this.strokeAlphaProvider = strokeAlphaProvider;
    }

    @Override
    public void handle(final W element,
                       final V view) {
        // Alpha.
        final Double alpha = alphaProvider.apply(element);
        if (null != alpha) {
            view.setAlpha(alpha);
        }
        // Fill color.
        final String color = fillColorProvider.apply(element);
        if (color != null && color.trim().length() > 0) {
            final boolean hasGradient = view instanceof HasFillGradient;
            if (!hasGradient) {
                view.setFillColor(color);
            } else {
                ((HasFillGradient) view).setFillGradient(HasFillGradient.Type.LINEAR,
                                                         color,
                                                         "#FFFFFF");
            }
        }
        // Fill alpha.
        final Double fillAlpha = fillAlphaProvider.apply(element);
        if (null != fillAlpha) {
            view.setFillAlpha(fillAlpha);
        }
        // Stroke color.
        final String strokeColor = strokeColorProvider.apply(element);
        if (strokeColor != null && strokeColor.trim().length() > 0) {
            view.setStrokeColor(strokeColor);
        }
        // Stroke width.
        final Double strokeWidth = strokeWidthProvider.apply(element);
        if (strokeWidth != null) {
            view.setStrokeWidth(strokeWidth);
        }
        // Stroke alpha.
        final Double strokeAlpha = strokeAlphaProvider.apply(element);
        if (null != strokeAlpha) {
            view.setStrokeAlpha(strokeAlpha);
        }
    }

    public static class Builder<W, V extends ShapeView> {

        private Function<W, Double> alphaProvider;
        private Function<W, String> fillColorProvider;
        private Function<W, Double> fillAlphaProvider;
        private Function<W, String> strokeColorProvider;
        private Function<W, Double> strokeWidthProvider;
        private Function<W, Double> strokeAlphaProvider;

        public Builder() {
            this.alphaProvider = value -> null;
            this.fillColorProvider = value -> null;
            this.fillAlphaProvider = value -> null;
            this.strokeColorProvider = value -> null;
            this.strokeWidthProvider = value -> null;
            this.strokeAlphaProvider = value -> null;
        }

        public Builder<W, V> alpha(Function<W, Double> alphaProvider) {
            this.alphaProvider = alphaProvider;
            return this;
        }

        public Builder<W, V> fillColor(Function<W, String> provider) {
            this.fillColorProvider = provider;
            return this;
        }

        public Builder<W, V> fillAlpha(Function<W, Double> provider) {
            this.fillAlphaProvider = provider;
            return this;
        }

        public Builder<W, V> strokeColor(Function<W, String> provider) {
            this.strokeColorProvider = provider;
            return this;
        }

        public Builder<W, V> strokeWidth(Function<W, Double> provider) {
            this.strokeWidthProvider = provider;
            return this;
        }

        public Builder<W, V> strokeAlpha(Function<W, Double> provider) {
            this.strokeAlphaProvider = provider;
            return this;
        }

        public ViewAttributesHandler<W, V> build() {
            return new ViewAttributesHandler<>(alphaProvider,
                                               fillColorProvider,
                                               fillAlphaProvider,
                                               strokeColorProvider,
                                               strokeWidthProvider,
                                               strokeAlphaProvider);
        }
    }
}