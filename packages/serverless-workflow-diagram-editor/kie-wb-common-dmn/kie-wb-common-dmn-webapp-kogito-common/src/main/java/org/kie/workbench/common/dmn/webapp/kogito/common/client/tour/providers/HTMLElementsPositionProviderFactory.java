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

import java.util.Optional;

import elemental2.dom.DOMRect;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import org.appformer.kogito.bridge.client.guided.tour.GuidedTourCustomSelectorPositionProvider.PositionProviderFunction;
import org.appformer.kogito.bridge.client.guided.tour.service.api.Rect;

public class HTMLElementsPositionProviderFactory implements PositionProviderFactory {

    public PositionProviderFunction createPositionProvider() {
        return selector ->
                Optional.ofNullable(document().querySelector(selector))
                        .map(this::makeRect)
                        .orElse(Rect.NONE());
    }

    private Rect makeRect(final Element element) {

        final DOMRect clientRect = element.getBoundingClientRect();

        final int bottom = (int) clientRect.bottom;
        final int top = (int) clientRect.top;
        final int left = (int) clientRect.left;
        final int right = (int) clientRect.right;
        final int height = (int) clientRect.height;
        final int width = (int) clientRect.width;

        return makeRect(bottom, top, left, right, height, width);
    }

    Rect makeRect(final int bottom,
                  final int top,
                  final int left,
                  final int right,
                  final int height,
                  final int width) {
        final Rect rect = new Rect();
        rect.setBottom(bottom);
        rect.setHeight(height);
        rect.setLeft(left);
        rect.setRight(right);
        rect.setTop(top);
        rect.setWidth(width);
        rect.setX(left);
        rect.setY(top);
        return rect;
    }

    HTMLDocument document() {
        return DomGlobal.document;
    }
}
