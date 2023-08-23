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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDocument;
import elemental2.dom.HTMLElement;
import elemental2.dom.NodeList;

import static java.util.stream.Collectors.toList;

public class DNDListDOMHelper {

    public static final String DRAGGING = "kie-dnd-current-dragging";

    static final String DRAGGABLE = "kie-dnd-draggable";

    static final String HOVER = "kie-dnd-hover";

    static final String GRIP = "kie-dnd-grip";

    static final String DATA_Y_POSITION = "data-y-position";

    static final int HIDDEN_Y_POSITION = -1;

    static final String DATA_X_POSITION = "data-x-position";

    // -- Position

    static class Position {

        static void setX(final Element element,
                         final double positionX) {
            element.setAttribute(DATA_X_POSITION, positionX);
        }

        static Integer getX(final Element element) {
            return parseInt(element.getAttribute(DATA_X_POSITION));
        }

        static void setY(final Element element,
                         final double positionY) {
            element.setAttribute(DATA_Y_POSITION, positionY);
        }

        static Integer getY(final Element element) {
            return parseInt(element.getAttribute(DATA_Y_POSITION));
        }

        static Double getDoubleY(final Element element) {
            return parseDouble(element.getAttribute(DATA_Y_POSITION));
        }
    }

    // -- QuerySelector

    static QuerySelector querySelector(final Element container) {
        return new QuerySelector(container);
    }

    static class QuerySelector {

        private final Element container;

        QuerySelector(final Element container) {
            this.container = container;
        }

        Optional<HTMLElement> getDraggableElement(final int yPosition) {
            final String selector = "." + DRAGGABLE + "[" + DATA_Y_POSITION + "=\"" + yPosition + "\"]";
            return Optional.ofNullable(container.querySelector(selector)).map(e -> (HTMLElement) e);
        }

        Optional<HTMLElement> getHoverElement() {
            final String selector = "." + HOVER;
            return Optional.ofNullable(container.querySelector(selector)).map(e -> (HTMLElement) e);
        }

        List<HTMLElement> getDraggableElements() {
            final String selector = "." + DRAGGABLE;
            return asList(container.querySelectorAll(selector));
        }

        List<HTMLElement> getSortedDraggableElements() {
            return getDraggableElements()
                    .stream()
                    .sorted(Comparator.comparing(Position::getDoubleY))
                    .collect(toList());
        }

        List<HTMLElement> getVisibleDraggableElements() {
            return getDraggableElements()
                    .stream()
                    .filter(e -> Position.getY(e) > HIDDEN_Y_POSITION)
                    .collect(toList());
        }

        List<HTMLElement> getVisibleAndSortedDraggableElements() {
            return getDraggableElements()
                    .stream()
                    .filter(e -> Position.getY(e) > HIDDEN_Y_POSITION)
                    .sorted(Comparator.comparing(Position::getDoubleY))
                    .collect(toList());
        }

        private List<HTMLElement> asList(final NodeList<Element> nodeList) {
            final List<HTMLElement> list = new ArrayList<>();
            for (int i = 0; i < nodeList.length; i++) {
                list.add((HTMLElement) nodeList.getAt(i));
            }
            return list;
        }
    }

    // -- Property handlers

    static void setCSSTop(final HTMLElement element,
                          final int value) {
        element.style.setProperty("top", value + "px");
    }

    static void setCSSPaddingLeft(final HTMLElement element,
                                  final int value) {
        element.style.setProperty("padding-left", value + "px");
    }

    static void setCSSWidth(final HTMLElement element,
                            final int value) {
        element.style.setProperty("width", "calc(100% - " + value + "px)");
    }

    static int getCSSTop(final HTMLElement element) {
        return parseInt(element.style.getPropertyValue("top"));
    }

    static int getCSSPaddingLeft(final HTMLElement element) {
        return parseInt(element.style.getPropertyValue("padding-left"));
    }

    static int getCSSWidth(final HTMLElement element) {
        final String width = element.style.getPropertyValue("width");
        return parseInt(width.replace("calc(100% - ", "").replace("px)", ""));
    }

    // -- Class handlers

    static HTMLElement asHover(final HTMLElement element) {
        element.classList.add(HOVER);
        return element;
    }

    static HTMLElement asNonHover(final HTMLElement element) {
        element.classList.remove(HOVER);
        return element;
    }

    static HTMLElement asDragging(final HTMLElement element) {
        element.classList.add(DRAGGING);
        return element;
    }

    static HTMLElement asNonDragging(final HTMLElement element) {
        element.classList.remove(DRAGGING);
        return element;
    }

    static HTMLElement asDraggable(final HTMLElement element) {
        element.classList.add(DRAGGABLE);
        return element;
    }

    static boolean isDraggingElement(final HTMLElement element) {
        return element.classList.contains(DRAGGING);
    }

    static boolean isGrip(final HTMLElement element) {
        return element.classList.contains(GRIP);
    }

    // -- Factory

    static class Factory {

        static final String ICON_CLASS = "fa";

        static final String ELLIPSIS_CLASS = "fa-ellipsis-v";

        static HTMLDocument DOCUMENT = DomGlobal.document;

        static HTMLElement createGripElement() {
            final HTMLElement grip = createDiv();
            grip.classList.add(GRIP);
            grip.appendChild(createEllipsisElement());
            grip.appendChild(createEllipsisElement());
            return grip;
        }

        static HTMLElement createDiv() {
            return createElement("div");
        }

        private static HTMLElement createEllipsisElement() {
            final HTMLElement i = createElement("i");
            i.classList.add(ICON_CLASS);
            i.classList.add(ELLIPSIS_CLASS);
            return i;
        }

        private static HTMLElement createElement(final String tagName) {
            return (HTMLElement) DOCUMENT.createElement(tagName);
        }
    }

    // -- Parsers

    static Integer parseInt(final String value) {
        return parseDouble(value).intValue();
    }

    static Double parseDouble(final String value) {
        try {
            return Double.valueOf(withoutPX(value));
        } catch (final NumberFormatException e) {
            return 0d;
        }
    }

    private static String withoutPX(final String value) {
        return Optional
                .ofNullable(value)
                .map(e -> e.replaceAll("px", ""))
                .orElse("");
    }
}
