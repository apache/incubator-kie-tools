/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.shape.MutableShapeDef;

/**
 * An util class that handles the shape's view properties that are coming from a MutableShapeDef type.
 * <p>
 * It adds some checks and constraints that can be used  across different implementation
 * for updating the views using the shape definition instance as input.
 * @param <W> The bean type.
 * @param <V> The view type.
 * @param <D> The mutable shape definition type..
 */
public class ShapeDefViewHandler<W, V extends ShapeView, D extends MutableShapeDef<W>> {

    private final D shapeDefinition;
    private final ShapeViewHandler<V> viewHandler;

    public ShapeDefViewHandler(final D shapeDefinition,
                               final V view) {
        this(shapeDefinition,
             new ShapeViewHandler<V>(view));
    }

    ShapeDefViewHandler(final D shapeDefinition,
                        final ShapeViewHandler<V> viewHandler) {
        this.shapeDefinition = shapeDefinition;
        this.viewHandler = viewHandler;
    }

    public void applyProperties(final W element,
                                final MutationContext mutationContext) {
        // Shape alpha.
        final double alpha = getAlpha(element);
        viewHandler.applyAlpha(alpha,
                               mutationContext);
        // Fill color.
        final String color = getBackgroundColor(element);
        viewHandler.applyFillColor(color,
                                   mutationContext);
        // Fill alpha.
        final double fillAlpha = getBackgroundAlpha(element);
        viewHandler.applyFillAlpha(fillAlpha,
                                   mutationContext);
        // Apply border styles.
        final String _strokeColor = getBorderColor(element);
        final double _strokeWidth = getBorderSize(element);
        viewHandler.applyBorders(_strokeColor,
                                 _strokeWidth,
                                 mutationContext);
        // Apply border alpha.
        final double _strokeAlpha = getBorderAlpha(element);
        viewHandler.applyBorderAlpha(_strokeAlpha,
                                     mutationContext);
    }

    public void applyTitle(final String title,
                           final W element,
                           final MutationContext mutationContext) {
        // Apply title's value.
        viewHandler.applyTitle(title,
                               mutationContext);
        // Apply title's font styles.
        applyFont(element,
                  mutationContext);
    }

    public ShapeViewHandler<V> getViewHandler() {
        return viewHandler;
    }

    public D getShapeDefinition() {
        return shapeDefinition;
    }

    private void applyFont(final W element,
                           final MutationContext mutationContext) {
        final String family = getFontFamily(element);
        final String fillColor = getFontColor(element);
        final String strokeColor = getFontBorderColor(element);
        final Double size = getFontSize(element);
        final Double borderSize = getFontBorderSize(element);
        final Double alpha = getFontAlpha(element);
        final HasTitle.Position position = getPosition(element);
        final Double rotation = getRotation(element);
        viewHandler.applyFont(family,
                              fillColor,
                              strokeColor,
                              size,
                              borderSize,
                              alpha,
                              position,
                              rotation,
                              mutationContext);
    }

    private double getAlpha(final W element) {
        return shapeDefinition.getAlpha(element);
    }

    private String getBackgroundColor(final W element) {
        return shapeDefinition.getBackgroundColor(element);
    }

    private double getBackgroundAlpha(final W element) {
        return shapeDefinition.getBackgroundAlpha(element);
    }

    private String getBorderColor(final W element) {
        return shapeDefinition.getBorderColor(element);
    }

    private double getBorderSize(final W element) {
        return shapeDefinition.getBorderSize(element);
    }

    private double getBorderAlpha(final W element) {
        return shapeDefinition.getBorderAlpha(element);
    }

    private String getFontFamily(final W element) {
        return shapeDefinition.getFontFamily(element);
    }

    private String getFontColor(final W element) {
        return shapeDefinition.getFontColor(element);
    }

    private String getFontBorderColor(final W element) {
        return shapeDefinition.getFontBorderColor(element);
    }

    private double getFontSize(final W element) {
        return shapeDefinition.getFontSize(element);
    }

    private double getFontAlpha(final W element) {
        return 1d;
    }

    private HasTitle.Position getPosition(final W element) {
        return shapeDefinition.getFontPosition(element);
    }

    private double getRotation(final W element) {
        return shapeDefinition.getFontRotation(element);
    }

    private double getFontBorderSize(final W element) {
        return shapeDefinition.getFontBorderSize(element);
    }
}
