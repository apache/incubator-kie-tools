/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.shape;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDecorators;
import org.kie.workbench.common.stunner.core.client.shape.view.HasDragBounds;
import org.kie.workbench.common.stunner.core.client.shape.view.HasEventHandlers;
import org.kie.workbench.common.stunner.core.client.shape.view.HasFillGradient;
import org.kie.workbench.common.stunner.core.client.shape.view.HasRadius;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.HasTitle;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEvent;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewHandler;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;

public class ShapeViewExtStub
        extends ShapeViewStub
        implements ShapeView<Object>,
                   HasEventHandlers<ShapeViewExtStub, Object>,
                   HasControlPoints<ShapeViewExtStub>,
                   HasDecorators<Object>,
                   HasFillGradient<Object>,
                   HasTitle<Object>,
                   HasSize<Object>,
                   HasRadius<Object>,
                   HasDragBounds<Object> {

    private final List<Object> decorators = new ArrayList<>();
    private final Optional<HasEventHandlers<ShapeViewExtStub, Object>> hasEventHandlers;
    private final Optional<HasControlPoints<ShapeViewExtStub>> hasControlPoints;

    public ShapeViewExtStub() {
        this.hasEventHandlers = Optional.empty();
        this.hasControlPoints = Optional.empty();
    }

    public ShapeViewExtStub(final HasEventHandlers<ShapeViewExtStub, Object> hasEventHandlers,
                            final HasControlPoints<ShapeViewExtStub> hasControlPoints) {
        this.hasEventHandlers = Optional.ofNullable(hasEventHandlers);
        this.hasControlPoints = Optional.ofNullable(hasControlPoints);
    }

    @Override
    public Object setFillGradient(final Type type,
                                  final String startColor,
                                  final String endColor) {
        return this;
    }

    @Override
    public Object setTitle(final String title) {
        return this;
    }

    @Override
    public Object setTitlePosition(final VerticalAlignment verticalAlignment,
                                   final HorizontalAlignment horizontalAlignment, final ReferencePosition referencePosition,
                                   final Orientation orientation) {
        return this;
    }

    @Override
    public Object setTitleSizeConstraints(final Size sizeConstraints) {
        return this;
    }

    @Override
    public Object setMargins(final Map<Enum, Double> margins) {
        return this;
    }

    @Override
    public Object setTitleXOffsetPosition(final Double xOffset) {
        return this;
    }

    @Override
    public Object setTitleYOffsetPosition(final Double yOffset) {
        return this;
    }

    @Override
    public Object setTitleRotation(final double degrees) {
        return this;
    }

    @Override
    public Object setTitleStrokeColor(final String color) {
        return this;
    }

    @Override
    public Object setTitleStrokeAlpha(final double alpha) {
        return this;
    }

    @Override
    public Object setTitleFontFamily(final String fontFamily) {
        return this;
    }

    @Override
    public Object setTitleFontSize(final double fontSize) {
        return this;
    }

    @Override
    public Object setTitleFontColor(final String fillColor) {
        return this;
    }

    @Override
    public Object setTitleStrokeWidth(final double strokeWidth) {
        return this;
    }

    @Override
    public String getTitleFontFamily() {
        return "";
    }

    @Override
    public double getTitleFontSize() {
        return 0;
    }

    @Override
    public String getTitlePosition() {
        return null;
    }

    @Override
    public String getOrientation() {
        return null;
    }

    @Override
    public double getMarginX() {
        return 0;
    }

    @Override
    public String getFontPosition() {
        return null;
    }

    @Override
    public String getFontAlignment() {
        return null;
    }

    @Override
    public Object setTitleAlpha(final double alpha) {
        return this;
    }

    @Override
    public void batch() {
    }

    @Override
    public Object moveTitleToTop() {
        return this;
    }

    @Override
    public Object setRadius(final double radius) {
        return this;
    }

    @Override
    public Object setMinRadius(Double minRadius) {
        return this;
    }

    @Override
    public Object setMaxRadius(Double maxRadius) {
        return this;
    }

    @Override
    public Object setSize(final double width,
                          final double height) {
        return this;
    }

    @Override
    public Object setMinWidth(Double minWidth) {
        return this;
    }

    @Override
    public Object setMaxWidth(Double minWidth) {
        return this;
    }

    @Override
    public Object setMinHeight(Double minWidth) {
        return this;
    }

    @Override
    public Object setMaxHeight(Double minWidth) {
        return this;
    }

    @Override
    public List<Object> getDecorators() {
        return decorators;
    }

    @Override
    public boolean supports(final ViewEventType type) {
        return hasEventHandlers.isPresent() && hasEventHandlers.get().supports(type);
    }

    @Override
    public ShapeViewExtStub addHandler(final ViewEventType type,
                                       final ViewHandler<? extends ViewEvent> eventHandler) {
        hasEventHandlers.ifPresent(h -> h.addHandler(type,
                                                     eventHandler));
        return this;
    }

    @Override
    public ShapeViewExtStub removeHandler(final ViewHandler<? extends ViewEvent> eventHandler) {
        hasEventHandlers.ifPresent(h -> h.removeHandler(eventHandler));
        return this;
    }

    @Override
    public ShapeViewExtStub enableHandlers() {
        hasEventHandlers.ifPresent(HasEventHandlers::enableHandlers);
        return this;
    }

    @Override
    public ShapeViewExtStub disableHandlers() {
        hasEventHandlers.ifPresent(HasEventHandlers::disableHandlers);
        return this;
    }

    @Override
    public Object getAttachableShape() {
        return hasEventHandlers.map(HasEventHandlers::getAttachableShape).orElse(null);
    }

    @Override
    public ShapeViewExtStub showControlPoints(final ControlPointType type) {
        hasControlPoints.ifPresent(h -> h.showControlPoints(type));
        return this;
    }

    @Override
    public ShapeViewExtStub hideControlPoints() {
        hasControlPoints.ifPresent(HasControlPoints::hideControlPoints);
        return this;
    }

    @Override
    public boolean areControlsVisible() {
        return hasControlPoints.isPresent() && hasControlPoints.get().areControlsVisible();
    }

    @Override
    public Object setDragBounds(final Bounds bounds) {
        return this;
    }
}
