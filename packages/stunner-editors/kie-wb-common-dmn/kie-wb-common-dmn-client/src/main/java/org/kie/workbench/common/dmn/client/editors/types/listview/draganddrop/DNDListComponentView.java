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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.Event;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Factory;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;

import static java.util.Collections.emptyList;
import static org.kie.workbench.common.dmn.client.editors.common.RemoveHelper.removeChildren;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.HIDDEN_Y_POSITION;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDraggable;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asHover;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.asNonDragging;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSPaddingLeft;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSTop;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.getCSSWidth;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.isGrip;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.querySelector;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSPaddingLeft;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSTop;
import static org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.setCSSWidth;

@Templated
public class DNDListComponentView implements DNDListComponent.View {

    @DataField("drag-area")
    private final HTMLDivElement dragArea;

    private List<HTMLElement> dependentElements = emptyList();

    private HTMLElement dragging;

    private DNDListComponent presenter;

    @Inject
    public DNDListComponentView(final HTMLDivElement dragArea) {
        this.dragArea = dragArea;
    }

    @Override
    public void init(final DNDListComponent presenter) {

        this.presenter = presenter;

        setupDragAreaHandlers();
    }

    @Override
    public HTMLElement registerItem(final HTMLElement htmlElement) {

        final HTMLElement item = createItem(htmlElement);

        Position.setY(item, getMaxPositionY() + 1d);
        Position.setX(item, 0);

        getDragArea().appendChild(item);

        return item;
    }

    @Override
    public void refreshItemsPosition() {

        final List<HTMLElement> items = querySelector(getDragArea()).getVisibleAndSortedDraggableElements();
        for (final HTMLElement draggable : items) {

            final int positionY = Position.getY(draggable);
            final int positionX = Position.getX(draggable);
            final int top = positionY * getItemHeight();
            final int margin = positionX * getLevelSize();

            setCSSTop(draggable, top);
            setCSSPaddingLeft(draggable, margin);
            setCSSWidth(draggable, 0);
        }

        refreshDragAreaSize();
    }

    @Override
    public void refreshItemsHTML() {

        final List<HTMLElement> draggableElements = querySelector(getDragArea()).getSortedDraggableElements();

        removeChildren(getDragArea());
        draggableElements.forEach(getDragArea()::appendChild);
    }

    @Override
    public void consolidateHierarchicalLevel(final boolean isElementsDraggedByUser) {

        final List<HTMLElement> draggableElements = querySelector(getDragArea()).getVisibleAndSortedDraggableElements();

        if (isElementsDraggedByUser) {
            if (!draggableElements.isEmpty()) {
                Position.setX(draggableElements.get(0), 0);
            }
        }

        if (draggableElements.size() < 2) {
            return;
        }

        for (int i = 0; i < draggableElements.size() - 1; i++) {

            final HTMLElement current = draggableElements.get(i);
            final HTMLElement next = draggableElements.get(i + 1);
            final int currentXPosition = Position.getX(current);
            final int minimalLevel = currentXPosition + 1;
            final int nextElementLevel = Position.getX(next);
            final int numberOfExtraLevels = nextElementLevel - minimalLevel;

            if (nextElementLevel > minimalLevel) {
                fixChildrenPosition(minimalLevel, numberOfExtraLevels, getDependentElements(current));
            }

            Position.setY(current, i);
            Position.setY(next, i + 1d);
        }
    }

    @Override
    public void consolidateYPosition() {

        final List<HTMLElement> draggableElements = querySelector(getDragArea()).getVisibleAndSortedDraggableElements();

        for (int i = 0; i < draggableElements.size(); i++) {
            Position.setY(draggableElements.get(i), i);
        }
    }

    @Override
    public void clear() {
        removeChildren(getDragArea());
    }

    @Override
    public Optional<HTMLElement> getPreviousElement(final Element reference) {
        final int positionY = Position.getY(reference);
        return querySelector(getDragArea()).getDraggableElement(positionY - 1);
    }

    void setupDragAreaHandlers() {
        getDragArea().onmousedown = (e) -> {
            onStartDrag(e);
            return true;
        };
        getDragArea().onmousemove = (e) -> {
            onDrag(e);
            return true;
        };
        getDragArea().onmouseup = (e) -> {
            onDrop();
            return true;
        };
        getDragArea().onmouseout = (e) -> {
            onDrop();
            return true;
        };
    }

    void onStartDrag(final Event event) {

        final HTMLElement target = (HTMLElement) event.target;
        final HTMLElement parent = (HTMLElement) target.parentNode;

        if (isGrip(target)) {
            holdDraggingElement(parent);
        }
    }

    void onDrag(final Event event) {

        if (isNotDragging()) {
            return;
        }

        updateDraggingElementY(event);
        updateDraggingElementX(event);
        updateHoverElement();
        updateDependentsPosition();
    }

    void onDrop() {

        if (isNotDragging()) {
            return;
        }

        updateDraggingElementsPosition();
        executeOnDropItemCallback();
        releaseDraggingElement();
        consolidateHierarchicalLevel(true);
        refreshItemsPosition();
        refreshItemsHTML();
        clearHover();
    }

    void updateDraggingElementsPosition() {

        final HTMLElement draggingElement = getDragging();
        final Optional<HTMLElement> previousElement = getPreviousElement(draggingElement);
        final boolean hasChildren = previousElement.map(this::hasChildren).orElse(false);
        final int currentXPosition = getCurrentXPosition(draggingElement);
        final double numberOfExtraLevels = hasChildren ? 1 : 0;

        Position.setX(draggingElement, currentXPosition + numberOfExtraLevels);
        getDependentElements().forEach(el -> Position.setX(el, numberOfExtraLevels + getCurrentXPosition(el)));
    }

    int getMaxPositionY() {
        return querySelector(getDragArea())
                .getDraggableElements()
                .stream()
                .mapToInt(Position::getY)
                .max()
                .orElse(HIDDEN_Y_POSITION);
    }

    private int getCurrentXPosition(final HTMLElement element) {
        final int margin = getCSSPaddingLeft(element) / getLevelSize();
        return margin > 0 ? margin : 0;
    }

    void executeOnDropItemCallback() {

        final Optional<HTMLElement> hoverElement = querySelector(getDragArea()).getHoverElement();

        hoverElement.ifPresent(hover -> {

            final int currentXPosition = Position.getX(hover);
            final int minimalLevel = currentXPosition + 1;
            final int numberOfExtraLevels = Position.getX(getDragging()) - minimalLevel;
            final List<HTMLElement> children = new ArrayList<>(getDependentElements());

            children.add(getDragging());
            Position.setX(getDragging(), currentXPosition + 1d);
            fixChildrenPosition(minimalLevel, numberOfExtraLevels, children);
        });

        presenter.executeOnDropItemCallback(getDragging(), hoverElement.orElse(null));
    }

    private int getDraggingYCoordinate() {
        return (int) (getDragging().offsetTop + (getItemHeight() / 2d));
    }

    void hover(final int hoverPosition) {

        final Optional<HTMLElement> hoverElement = querySelector(getDragArea()).getDraggableElement(hoverPosition);

        clearHover();

        hoverElement.ifPresent(hover -> {

            final boolean isNotDragging = !DNDListDOMHelper.isDraggingElement(hover);
            if (isNotDragging) {
                asHover(hover);
                highlightLevel(hover);
            }
        });
    }

    void clearHover() {
        querySelector(getDragArea())
                .getHoverElement()
                .ifPresent(DNDListDOMHelper::asNonHover);
    }

    void updateDependentsPosition() {

        final List<HTMLElement> elements = Optional.ofNullable(getDependentElements()).orElse(emptyList());

        for (int i = 0; i < elements.size(); i++) {

            final HTMLElement dependent = elements.get(i);
            final int dependentTop = getCSSTop(getDragging()) + (getItemHeight() * (i + 1));

            setCSSTop(dependent, dependentTop);
            setCSSWidth(dependent, getCSSWidth(getDragging()));
        }
    }

    boolean hasChildren(final Element element) {

        final Element next = getNextElement(element, nextElement -> {
            final boolean isNotDragging = !Objects.equals(nextElement, getDragging());
            final boolean isNotDependentElement = !getDependentElements().contains(nextElement);
            return isNotDragging && isNotDependentElement;
        });

        if (next == null) {
            return false;
        }

        final int currentPositionX = Position.getX(element);
        final int nextPositionX = Position.getX(next);

        return currentPositionX == nextPositionX - 1;
    }

    private Element getNextElement(final Element element,
                                   final Function<HTMLElement, Boolean> function) {

        final int nextElementPosition = Position.getY(element) + 1;
        final HTMLElement next = querySelector(getDragArea()).getDraggableElement(nextElementPosition).orElse(null);

        if (function.apply(next) || next == null) {
            return next;
        }

        return getNextElement(next, function);
    }

    @Override
    public void refreshDragAreaSize() {

        final int numberOfElements = querySelector(getDragArea()).getVisibleDraggableElements().size();
        final int border = 1;
        final int elementHeight = getItemHeight();
        final int height = numberOfElements * elementHeight + border;

        getDragArea().style.setProperty("height", height + "px");
    }

    void fixChildrenPosition(final int minimalXPosition,
                             final int numberOfExtraLevels,
                             final List<HTMLElement> children) {

        for (int i = 0; i < children.size(); i++) {

            final HTMLElement dependentElement = children.get(i);
            final int elementPosition = Position.getX(dependentElement);
            final boolean isElementPositionValid = i > 0 && elementPosition >= Position.getX(children.get(i - 1));

            if (!isElementPositionValid) {
                final int positionX = elementPosition - numberOfExtraLevels;
                final int newElementPosition = positionX < minimalXPosition ? minimalXPosition : positionX;
                Position.setX(dependentElement, newElementPosition);
            }
        }
    }

    private DNDMinMaxTuple getMinMaxDraggingYCoordinates() {

        final int draggingYCoordinate = getDraggingYCoordinate();
        final int padding = getDragPadding();
        final int max = (draggingYCoordinate + padding) / getItemHeight();
        final int min = (draggingYCoordinate - padding) / getItemHeight();

        return new DNDMinMaxTuple(max, min);
    }

    void updateHoverElement() {

        final int draggingYPosition = Position.getY(getDragging());
        final int hoverPosition = getDraggingYCoordinate() / getItemHeight();
        final DNDMinMaxTuple minMaxTuple = getMinMaxDraggingYCoordinates();

        if (draggingYPosition < minMaxTuple.max || draggingYPosition > minMaxTuple.min) {
            hover(hoverPosition);
        }
    }

    void updateDraggingElementY(final Event event) {

        final int draggingYPosition = Position.getY(getDragging());
        final int mouseYPosition = getDraggingYCoordinate() / getItemHeight();
        final DNDMinMaxTuple minMaxTuple = getMinMaxDraggingYCoordinates();

        if (draggingYPosition < minMaxTuple.min) {
            updateDraggingElementY(mouseYPosition, draggingYPosition, mouseYPosition + getDependentElements().size());
        }

        if (draggingYPosition > minMaxTuple.max) {
            updateDraggingElementY(mouseYPosition, mouseYPosition + getDependentElements().size() + 1, mouseYPosition);
        }

        setCSSTop(getDragging(), getNewDraggingYPosition(event));
    }

    void updateDraggingElementX(final Event event) {
        setCSSWidth(getDragging(), getNewDraggingXPosition(event));
    }

    private void updateDraggingElementY(final int mouseYPosition,
                                        final int newSiblingYPosition,
                                        final int oldSiblingYPosition) {

        Optional<HTMLElement> draggableElement = querySelector(getDragArea()).getDraggableElement(oldSiblingYPosition);
        draggableElement.ifPresent(siblingElement -> {

            Position.setY(siblingElement, newSiblingYPosition);
            Position.setY(getDragging(), mouseYPosition);

            clearHover();

            for (int i = 0; i < getDependentElements().size(); i++) {
                Position.setY(getDependentElements().get(i), mouseYPosition + i + 1d);
            }

            refreshItemsPosition();
        });
    }

    int getNewDraggingYPosition(final Event event) {

        final Double absoluteMouseY = getAbsoluteMouseY(event);
        final Double newYPosition = absoluteMouseY - (getItemHeight() / 2d);
        final Double maxYPosition = getDragAreaY() + getDragArea().offsetHeight;

        if (newYPosition > maxYPosition) {
            return maxYPosition.intValue();
        }

        return newYPosition.intValue();
    }

    private int getNewDraggingXPosition(final Event event) {

        // Represents the "padding" between the dragging element border and the cursor
        final int padding = 10;
        final double absoluteMouseX = getAbsoluteMouseX(event);
        final int newXPosition = (int) absoluteMouseX - padding;
        final int maxXPosition = getItemWidth() - getLevelSize();

        if (newXPosition < 0) {
            return 0;
        }

        if (newXPosition > maxXPosition) {
            return maxXPosition;
        }

        return newXPosition;
    }

    boolean isNotDragging() {
        return !Optional.ofNullable(getDragging()).isPresent();
    }

    private double getAbsoluteMouseX(final Event event) {
        final MouseEvent mouseEvent = (MouseEvent) event;
        return mouseEvent.x - getDragAreaX();
    }

    private double getAbsoluteMouseY(final Event event) {
        final MouseEvent mouseEvent = (MouseEvent) event;
        return mouseEvent.y - getDragAreaY();
    }

    void holdDraggingElement(final HTMLElement element) {

        setDragging(element);
        setDependentElements(getDependentElements(element));

        asDragging(getDragging());
        getDependentElements().forEach(DNDListDOMHelper::asDragging);
    }

    void releaseDraggingElement() {

        asNonDragging(getDragging());
        getDependentElements().forEach(DNDListDOMHelper::asNonDragging);

        setDependentElements(emptyList());
        setDragging(null);
    }

    List<HTMLElement> getDependentElements(final HTMLElement element) {

        final int minimalLevel = Position.getX(element) + 1;
        final List<HTMLElement> initial = new ArrayList<>();

        return getNextDependents(initial, element, minimalLevel);
    }

    private List<HTMLElement> getNextDependents(final List<HTMLElement> dependents,
                                                final Element element,
                                                final int minimalLevel) {

        final int positionY = Position.getY(element);
        final HTMLElement next = querySelector(getDragArea()).getDraggableElement(positionY + 1).orElse(null);

        if (next == null || Position.getX(next) < minimalLevel) {
            return dependents;
        } else {
            dependents.add(next);
            return getNextDependents(dependents, next, minimalLevel);
        }
    }

    private int getItemWidth() {
        return (int) getDragArea().offsetWidth;
    }

    private double getDragAreaY() {
        return getDragArea().getBoundingClientRect().top;
    }

    private double getDragAreaX() {
        return getDragArea().getBoundingClientRect().left;
    }

    private int getLevelSize() {
        return presenter.getIndentationSize();
    }

    private int getItemHeight() {
        return presenter.getItemHeight();
    }

    private int getDragPadding() {
        return getItemHeight() / 3;
    }

    HTMLElement createItem(final HTMLElement htmlElement) {

        final HTMLElement item = Factory.createDiv();
        final HTMLElement gripElement = Factory.createGripElement();

        item.appendChild(gripElement);
        item.appendChild(htmlElement);

        return asDraggable(item);
    }

    HTMLElement getDragging() {
        return dragging;
    }

    List<HTMLElement> getDependentElements() {
        return dependentElements;
    }

    @Override
    public HTMLDivElement getDragArea() {
        return dragArea;
    }

    private void setDependentElements(final List<HTMLElement> dependentElements) {
        this.dependentElements = dependentElements;
    }

    private void setDragging(HTMLElement dragging) {
        this.dragging = dragging;
    }

    private void highlightLevel(final HTMLElement htmlElement) {
        presenter.highlightLevel(htmlElement);
    }

    private class DNDMinMaxTuple {

        private final int min;
        private final int max;

        DNDMinMaxTuple(final int max,
                       final int min) {
            this.max = max;
            this.min = min;
        }
    }
}
