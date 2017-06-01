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
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;

/**
 * An util class that handles the different calls to a ShapeView.
 * <p>
 * It adds some checks and constraints that can be used  across different implementation
 * for updating the views.
 * @param <V>
 */
public class ShapeViewHandler<V extends ShapeView> {

    private final V view;

    public ShapeViewHandler(final V view) {
        this.view = view;
    }

    public void applyTitle(final String title,
                           final MutationContext mutationContext) {
        if (title != null && getShapeView() instanceof HasTitle) {
            final HasTitle hasTitle = (HasTitle) getShapeView();
            hasTitle.setTitle(title);
        }
    }

    public void applyAlpha(final Double alpha,
                           final MutationContext mutationContext) {
        if (null != alpha) {
            getShapeView().setAlpha(alpha);
        }
    }

    public void applyFillColor(final String color,
                               final MutationContext mutationContext) {
        if (color != null && color.trim().length() > 0) {
            final boolean hasGradient = getShapeView() instanceof HasFillGradient;
            if (!hasGradient) {
                getShapeView().setFillColor(color);
            } else {
                ((HasFillGradient) getShapeView()).setFillGradient(HasFillGradient.Type.LINEAR,
                                                                   color,
                                                                   "#FFFFFF");
            }
        }
    }

    public void applyFillAlpha(final Double alpha,
                               final MutationContext mutationContext) {
        if (null != alpha) {
            getShapeView().setFillAlpha(alpha);
        }
    }

    public void applyBorders(final String color,
                             final Double width,
                             final MutationContext mutationContext) {
        if (color != null && color.trim().length() > 0) {
            getShapeView().setStrokeColor(color);
        }
        if (width != null) {
            getShapeView().setStrokeWidth(width);
        }
    }

    public void applyBorderAlpha(final Double alpha,
                                 final MutationContext mutationContext) {
        if (null != alpha) {
            getShapeView().setStrokeAlpha(alpha);
        }
    }

    public void applyFont(final String fontFamily,
                          final String fontColor,
                          final String strokeColor,
                          final Double size,
                          final Double borderSize,
                          final Double alpha,
                          final HasTitle.Position position,
                          final Double rotationDegrees,
                          final MutationContext mutationContext) {
        final HasTitle hasTitle = (HasTitle) getShapeView();
        if (fontFamily != null && fontFamily.trim().length() > 0) {
            hasTitle.setTitleFontFamily(fontFamily);
        }
        if (fontColor != null && fontColor.trim().length() > 0) {
            hasTitle.setTitleFontColor(fontColor);
        }
        if (strokeColor != null && strokeColor.trim().length() > 0) {
            hasTitle.setTitleStrokeColor(strokeColor);
        }
        if (size != null && size > 0) {
            hasTitle.setTitleFontSize(size);
        }
        if (borderSize != null && borderSize > 0) {
            hasTitle.setTitleStrokeWidth(borderSize);
        }
        if (null != alpha) {
            hasTitle.setTitleAlpha(alpha);
        }
        if (null != position) {
            hasTitle.setTitlePosition(position);
        }
        if (null != rotationDegrees) {
            hasTitle.setTitleRotation(rotationDegrees);
        }
    }

    public void applySize(final double width,
                          final double height,
                          final MutationContext mutationContext) {
        ((HasSize) getShapeView()).setSize(width,
                                           height);
    }

    public void applyRadius(final double radius,
                            final MutationContext mutationContext) {
        if (radius > 0) {
            ((HasRadius) getShapeView()).setRadius(radius);
        }
    }

    public V getShapeView() {
        return view;
    }
}
