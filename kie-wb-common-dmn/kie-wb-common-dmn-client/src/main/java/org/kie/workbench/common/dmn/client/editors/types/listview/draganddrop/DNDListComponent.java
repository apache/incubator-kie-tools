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

package org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.kie.workbench.common.dmn.client.editors.types.listview.draganddrop.DNDListDOMHelper.Position;
import org.uberfire.client.mvp.UberElemental;

public class DNDListComponent {

    private final View view;

    private BiConsumer<Element, Element> onDropItem = (current, hover) -> {/* Nothing. */};

    static int DEFAULT_ITEM_HEIGHT = 70;

    static int DEFAULT_INDENTATION_SIZE = 60;

    @Inject
    public DNDListComponent(final View view) {
        this.view = view;
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
        view.consolidateHierarchicalLevel();
    }

    public interface View extends UberElemental<DNDListComponent>,
                                  IsElement {

        HTMLElement registerItem(final HTMLElement htmlElement);

        Optional<HTMLElement> getPreviousElement(final Element reference);

        void refreshItemsPosition();

        void refreshItemsHTML();

        void consolidateHierarchicalLevel();

        void clear();

        void consolidateYPosition();

        HTMLDivElement getDragArea();
    }
}
