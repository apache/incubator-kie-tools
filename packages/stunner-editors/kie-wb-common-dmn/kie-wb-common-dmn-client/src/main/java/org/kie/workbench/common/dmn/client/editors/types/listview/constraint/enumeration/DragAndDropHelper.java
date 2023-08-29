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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration;

import java.util.Objects;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import elemental2.dom.NodeList;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.client.editors.types.listview.constraint.enumeration.item.DataTypeConstraintEnumerationItemView.DATA_POSITION;

class DragAndDropHelper {

    private final HTMLElement dragArea;
    private final HTMLElement addButtonContainer;
    private int clickedYPosition;
    private int startYPosition;
    private HTMLElement draggingElement;
    static final String DRAGGABLE_ITEM_CLASS = ".draggable";
    static final String TOP = "top";
    static final String PX = "px";

    public DragAndDropHelper(final HTMLElement dragArea,
                             final HTMLElement addButtonContainer) {

        this.dragArea = dragArea;
        this.addButtonContainer = addButtonContainer;
        init();
    }

    private void init() {
        dragArea.onmousedown = this::onDragAreaMouseDown;
        dragArea.onmouseup = this::onDragAreaMouseUp;
        dragArea.onmousemove = this::onDragAreaMouseMove;
    }

    void refreshItemsPosition() {

        final NodeList<Element> draggableItems = dragArea.querySelectorAll(DRAGGABLE_ITEM_CLASS);
        final int addButtonTop;

        if (!Objects.isNull(draggableItems) && draggableItems.length > 0) {
            final double itemHeight = getItemHeight((HTMLElement) draggableItems.getAt(0));
            addButtonTop = (int) (draggableItems.length * itemHeight);

            for (int i = 0; i < draggableItems.length; i++) {
                final HTMLElement element = (HTMLElement) draggableItems.getAt(i);
                final int top = (int) (position(element) * itemHeight);
                setTop(element, top);
            }
        } else {
            addButtonTop = 0;
        }

        setTop(addButtonContainer, addButtonTop);
    }

    int position(final Element element) {
        return Integer.valueOf(element.getAttribute(DATA_POSITION));
    }

    Element findElementByPosition(final int position) {
        return dragArea.querySelector("[" + DATA_POSITION + "=\"" + position + "\"]");
    }

    void swapElements(final Element element, final Element element2) {

        if (Objects.isNull(element) || Objects.isNull(element2)) {
            return;
        }

        final int pos1 = position(element);
        final int pos2 = position(element2);

        element.setAttribute(DATA_POSITION, pos2);
        element2.setAttribute(DATA_POSITION, pos1);

        refreshItemsPosition();
    }

    Object onDragAreaMouseDown(final Event e) {

        final MouseEvent event = (MouseEvent) e;

        if (!Objects.isNull(event.target)) {

            final Element grabber = ((Element) event.target).closest(".drag-grabber");
            if (Objects.isNull(grabber)) {
                return this;
            }

            draggingElement = (HTMLElement) grabber.closest(DRAGGABLE_ITEM_CLASS);
            clickedYPosition = (int) event.clientY;
            startYPosition = getTop(getDragging());
        }

        return this;
    }

    int getStartYPosition() {
        return startYPosition;
    }

    int getClickedYPosition() {
        return clickedYPosition;
    }

    HTMLElement getDragging() {
        return draggingElement;
    }

    Object onDragAreaMouseUp(final Event event) {
        draggingElement = null;
        clickedYPosition = 0;
        startYPosition = 0;
        refreshItemsPosition();
        return this;
    }

    int getTop(final HTMLElement element) {

        final String topString = element.style.getPropertyValue(TOP)
                .replace(PX, "");

        if (StringUtils.isEmpty(topString)) {
            return 0;
        }
        return Integer.valueOf(topString);
    }

    void setTop(final HTMLElement element,
                final int top) {
        element.style.setProperty(TOP, top + PX);
    }

    Object onDragAreaMouseMove(final Event e) {

        if (Objects.isNull(getDragging())) {
            return this;
        }

        final MouseEvent event = (MouseEvent) e;
        final int delta = getDelta(event);
        final int newPosition = getNewPosition();
        final int oldPosition = position(getDragging());

        if (newPosition != oldPosition) {
            swapElements(findElementByPosition(newPosition), getDragging());
        }

        setTop(getDragging(), startYPosition + delta);

        return this;
    }

    private double getItemHeight(final HTMLElement element) {
        return element.offsetHeight;
    }

    /**
     * Gets the new position of the object being dragged.
     * The position is changed when the half of item being dragged is after/before
     * the half of the previous/next item.
     * @return The position of the item being dragged
     */
    int getNewPosition() {
        final int itemHeight = (int) getItemHeight(getDragging());
        final int topReference = getTop(getDragging()) + itemHeight / 2;
        final int newPosition = topReference / (itemHeight == 0 ? 1 : itemHeight);
        return newPosition;
    }

    int getDelta(final MouseEvent event) {
        return (int) event.clientY - getClickedYPosition();
    }
}
