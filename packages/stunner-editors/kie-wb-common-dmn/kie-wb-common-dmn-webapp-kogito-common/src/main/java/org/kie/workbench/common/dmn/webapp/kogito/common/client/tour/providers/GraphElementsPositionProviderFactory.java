/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.providers;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

import elemental2.dom.DOMRect;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.webapp.kogito.common.client.tour.common.GuidedTourUtils;
import org.kie.workbench.common.stunner.client.lienzo.canvas.wires.WiresCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.impl.NodeImpl;

public class GraphElementsPositionProviderFactory implements PositionProviderFactory {

    private final DMNGraphUtils dmnGraphUtils;

    private final GuidedTourUtils guidedTourUtils;

    private final Elemental2DomUtil elemental2DomUtil;

    @Inject
    public GraphElementsPositionProviderFactory(final DMNGraphUtils dmnGraphUtils,
                                                final GuidedTourUtils guidedTourUtils,
                                                final Elemental2DomUtil elemental2DomUtil) {
        this.dmnGraphUtils = dmnGraphUtils;
        this.guidedTourUtils = guidedTourUtils;
        this.elemental2DomUtil = elemental2DomUtil;
    }

    public PositionProviderFunction createPositionProvider() {
        return selector ->
                getGraphNodes()
                        .map(guidedTourUtils::asNodeImpl)
                        .filter(node -> Objects.equals(getName(node), selector))
                        .findFirst()
                        .map(this::calculateNodeRelativePosition)
                        .orElse(Rect.NONE());
    }

    private Rect calculateNodeRelativePosition(final NodeImpl<View> node) {

        final Bounds bounds = node.getContent().getBounds();
        final Optional<DOMRect> containerRect = getContainerRect();
        final double canvasLeft = containerRect.map(rect -> rect.left).orElse(0d);
        final double canvasTop = containerRect.map(rect -> rect.top).orElse(0d);

        final int left = (int) (canvasLeft + bounds.getX());
        final int top = (int) (canvasTop + bounds.getY());
        final int height = (int) bounds.getHeight();
        final int width = (int) bounds.getWidth();

        return makeRect(left, top, height, width);
    }

    Rect makeRect(final int left,
                  final int top,
                  final int height,
                  final int width) {
        final Rect rect = new Rect();

        rect.setLeft(left);
        rect.setTop(top);
        rect.setHeight(height);
        rect.setWidth(width);
        rect.setRight(left + width);
        rect.setBottom(top + height);

        return rect;
    }

    private Optional<DOMRect> getContainerRect() {
        final Optional<HTMLElement> containerElement = getWiresCanvas().map(wiresCanvas -> elemental2DomUtil.asHTMLElement(wiresCanvas.getView().getElement()));
        return containerElement.map(Element::getBoundingClientRect);
    }

    private Optional<WiresCanvas> getWiresCanvas() {
        final CanvasHandler canvasHandler = getCanvasHandler();
        if (canvasHandler != null && canvasHandler.getCanvas() instanceof WiresCanvas) {
            return Optional.of((WiresCanvas) canvasHandler.getCanvas());
        }
        return Optional.empty();
    }

    private String getName(final NodeImpl<View> node) {
        return guidedTourUtils.getName(node);
    }

    private CanvasHandler getCanvasHandler() {
        return dmnGraphUtils.getCanvasHandler();
    }

    private Stream<Node> getGraphNodes() {
        return dmnGraphUtils.getNodeStream();
    }
}
