/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.wires.core.grids.client.widget.scrollbars;

import java.util.Optional;

import com.ait.lienzo.client.core.event.NodeMouseMoveEvent;
import com.ait.lienzo.client.core.shape.Viewport;
import com.ait.lienzo.client.core.types.Transform;
import com.ait.lienzo.client.widget.LienzoPanel;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import org.uberfire.ext.wires.core.grids.client.model.Bounds;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.DefaultGridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;
import org.uberfire.ext.wires.core.grids.client.widget.layer.pinning.impl.RestrictedMousePanMediator;

/*
 * Responsible for the setup and control of every scroll related event.
 */

public class GridLienzoScrollHandler {

    private final GridLienzoPanel panel;

    private final GridLienzoScrollBounds gridLienzoScrollBounds;

    static final int DEFAULT_INTERNAL_SCROLL_HEIGHT = 1;

    static final int DEFAULT_INTERNAL_SCROLL_WIDTH = 1;

    private RestrictedMousePanMediator mousePanMediator;

    public GridLienzoScrollHandler(final GridLienzoPanel panel) {
        this.panel = panel;
        this.gridLienzoScrollBounds = new GridLienzoScrollBounds(this);
    }

    public void init() {
        setupGridLienzoScrollStyle();
        setupScrollBarSynchronization();
        setupMouseDragSynchronization();
    }

    public Integer scrollbarWidth() {
        return getMainPanel().getElement().getOffsetWidth() - getMainPanel().getElement().getClientWidth();
    }

    public Integer scrollbarHeight() {
        return getMainPanel().getElement().getOffsetHeight() - getMainPanel().getElement().getClientHeight();
    }

    void setupGridLienzoScrollStyle() {
        gridLienzoScrollUI().setup();
    }

    GridLienzoScrollUI gridLienzoScrollUI() {
        return new GridLienzoScrollUI(this);
    }

    void setupScrollBarSynchronization() {
        getMainPanel().addDomHandler(onScroll(),
                                     ScrollEvent.getType());
        synchronizeScrollSize();
    }

    void setupMouseDragSynchronization() {

        mousePanMediator = makeRestrictedMousePanMediator();

        getLienzoPanel().getViewport().getMediators().push(mousePanMediator);
    }

    RestrictedMousePanMediator makeRestrictedMousePanMediator() {
        return new RestrictedMousePanMediator() {
            @Override
            protected void onMouseMove(final NodeMouseMoveEvent event) {
                refreshScrollPosition();
            }

            @Override
            protected Viewport getLayerViewport() {
                return getViewport();
            }
        };
    }

    ScrollHandler onScroll() {
        return (ScrollEvent event) -> {
            final Boolean mouseIsNotDragging = !getMousePanMediator().isDragging();

            if (mouseIsNotDragging) {
                updateGridLienzoPosition();
            }
        };
    }

    public void refreshScrollPosition() {

        synchronizeScrollSize();

        setScrollBarsPosition(scrollPosition().currentRelativeX(),
                              scrollPosition().currentRelativeY());
    }

    void updateGridLienzoPosition() {

        final Double percentageX = scrollBars().getHorizontalScrollPosition();
        final Double percentageY = scrollBars().getVerticalScrollPosition();

        final Double currentXPosition = scrollPosition().currentPositionX(percentageX);
        final Double currentYPosition = scrollPosition().currentPositionY(percentageY);

        updateGridLienzoTransform(currentXPosition,
                                  currentYPosition);
    }

    void updateGridLienzoTransform(final Double currentXPosition,
                                   final Double currentYPosition) {

        final Transform oldTransform = getViewport().getTransform();
        final Double dx = currentXPosition - (oldTransform.getTranslateX() / oldTransform.getScaleX());
        final Double dy = currentYPosition - (oldTransform.getTranslateY() / oldTransform.getScaleY());

        final Transform newTransform = oldTransform.copy().translate(dx,
                                                                     dy);

        getViewport().setTransform(newTransform);
        getDefaultGridLayer().batch();
    }

    void synchronizeScrollSize() {
        getInternalScrollPanel().setPixelSize(calculateInternalScrollPanelWidth(),
                                              calculateInternalScrollPanelHeight());
    }

    Integer calculateInternalScrollPanelWidth() {
        final Double absWidth = scrollBounds().maxBoundX() - scrollBounds().minBoundX();

        if (getViewport() != null && scrollPosition().deltaX() != 0) {
            final Double scaleX = getViewport().getTransform().getScaleX();
            final Double width = absWidth * scaleX;

            return width.intValue();
        }

        return DEFAULT_INTERNAL_SCROLL_WIDTH;
    }

    Integer calculateInternalScrollPanelHeight() {
        final Double absHeight = scrollBounds().maxBoundY() - scrollBounds().minBoundY();

        if (getViewport() != null && scrollPosition().deltaY() != 0) {
            final Double scaleY = getViewport().getTransform().getScaleY();
            final Double height = absHeight * scaleY;

            return height.intValue();
        }

        return DEFAULT_INTERNAL_SCROLL_HEIGHT;
    }

    void setScrollBarsPosition(final Double xPercentage,
                               final Double yPercentage) {

        scrollBars().setHorizontalScrollPosition(xPercentage);
        scrollBars().setVerticalScrollPosition(yPercentage);
    }

    RestrictedMousePanMediator getMousePanMediator() {
        return mousePanMediator;
    }

    AbsolutePanel getMainPanel() {
        return panel.getMainPanel();
    }

    AbsolutePanel getInternalScrollPanel() {
        return panel.getInternalScrollPanel();
    }

    AbsolutePanel getDomElementContainer() {
        return panel.getDomElementContainer();
    }

    LienzoPanel getLienzoPanel() {
        return panel.getLienzoPanel();
    }

    DefaultGridLayer getDefaultGridLayer() {
        return Optional.ofNullable(panel.getDefaultGridLayer()).orElse(emptyLayer());
    }

    Viewport getViewport() {
        return getDefaultGridLayer().getViewport();
    }

    DefaultGridLayer emptyLayer() {
        return new DefaultGridLayer();
    }

    GridLienzoScrollBars scrollBars() {
        return new GridLienzoScrollBars(this);
    }

    GridLienzoScrollPosition scrollPosition() {
        return new GridLienzoScrollPosition(this);
    }

    GridLienzoScrollBounds scrollBounds() {
        return gridLienzoScrollBounds;
    }

    public void setBounds(final Bounds bounds) {
        scrollBounds().setDefaultBounds(bounds);
    }
}
