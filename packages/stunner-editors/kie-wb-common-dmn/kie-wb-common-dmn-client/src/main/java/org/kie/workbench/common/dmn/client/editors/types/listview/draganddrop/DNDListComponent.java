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

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeList;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;
import org.uberfire.client.mvp.UberElemental;

public class DNDListComponent {

    private final View view;

    private final DataTypeList dataTypeList;

    private BiConsumer<Element, Element> onDropItem = (current, hover) -> {/* Nothing. */};

    static int DEFAULT_ITEM_HEIGHT = 70;

    static int DEFAULT_INDENTATION_SIZE = 60;

    @Inject
    public DNDListComponent(final View view,
                            final DataTypeList dataTypeList) {
        this.view = view;
        this.dataTypeList = dataTypeList;
    }

    @PostConstruct
    void init() {
        view.init(this);
    }

    public void refreshItemsPosition() {
        view.refreshItemsPosition();
    }

    public void refreshItemsCSSAndHTMLPosition() {
        consolidateHierarchicalLevel();
        refreshItemsPosition();
    }

    public HTMLElement registerNewItem(final HTMLElement htmlElement) {
        return view.registerItem(htmlElement);
    }

    public HTMLElement getElement() {
        return view.getElement();
    }

    public HTMLElement getDragArea() {
        return view.getDragArea();
    }

    public void consolidateYPosition() {
        view.consolidateYPosition();
    }

    public Optional<HTMLElement> getPreviousElement(final Element reference) {
        return view.getPreviousElement(reference);
    }

    public void clear() {
        view.clear();
    }

    public void setPositionX(final Element element,
                             final double positionX) {
        Position.setX(element, positionX);
    }

    public void setPositionY(final Element element,
                             final double positionY) {
        Position.setY(element, positionY);
    }

    public int getPositionY(final Element element) {
        return Position.getY(element);
    }

    public void setOnDropItem(final BiConsumer<Element, Element> onDropItem) {
        this.onDropItem = onDropItem;
    }

    void executeOnDropItemCallback(final Element current,
                                   final Element hover) {
        onDropItem.accept(current, hover);
    }

    int getItemHeight() {
        return DEFAULT_ITEM_HEIGHT;
    }

    int getIndentationSize() {
        return DEFAULT_INDENTATION_SIZE;
    }

    Optional<Element> getPreviousElement(final Element reference,
                                         final Predicate<? super Element> predicate) {
        if (reference == null) {
            return Optional.empty();
        }

        return getPreviousElement(reference)
                .map(previousElement -> {
                    if (predicate.test(previousElement)) {
                        return previousElement;
                    } else {
                        return getPreviousElement(previousElement, predicate).orElse(null);
                    }
                });
    }

    private void consolidateHierarchicalLevel() {
        view.consolidateHierarchicalLevel(false);
    }

    public void refreshDragAreaSize() {
        view.refreshDragAreaSize();
    }

    public void setInitialPositionY(final HTMLElement dragAndDropElement,
                                    final List<HTMLElement> children) {

        final Integer parentY = Position.getY(dragAndDropElement);
        final double incrementValue = 0.001;

        for (int j = 1; j <= children.size(); j++) {

            final double childPositionY = parentY + (j * incrementValue);
            final HTMLElement child = children.get(j - 1);

            Position.setY(child, childPositionY);
        }
    }

    public void setInitialHiddenPositionY(final HTMLElement itemElement) {
        Position.setY(itemElement, DNDListDOMHelper.HIDDEN_Y_POSITION);
    }

    public void highlightLevel(final HTMLElement htmlElement) {
        dataTypeList.highlightLevel(htmlElement);
    }

    public interface View extends UberElemental<DNDListComponent>,
                                  IsElement {

        HTMLElement registerItem(final HTMLElement htmlElement);

        Optional<HTMLElement> getPreviousElement(final Element reference);

        void refreshItemsPosition();

        void refreshItemsHTML();

        void consolidateHierarchicalLevel(final boolean adjustFirstElementPositionX);

        void clear();

        void consolidateYPosition();

        void refreshDragAreaSize();

        HTMLDivElement getDragArea();
    }
}
