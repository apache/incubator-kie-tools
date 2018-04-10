/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.decision.tree;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Text;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.decision.DecisionNavigatorItem;

import static java.util.Optional.ofNullable;

@Templated
public class DecisionNavigatorTreeView implements DecisionNavigatorTreePresenter.View {

    @DataField("view")
    private HTMLDivElement view;

    @DataField("items")
    private HTMLDivElement items;

    private ManagedInstance<TreeItem> managedInstance;

    private Elemental2DomUtil util;

    private DecisionNavigatorTreePresenter presenter;

    private Element selectedElement;

    @Inject
    public DecisionNavigatorTreeView(final HTMLDivElement view,
                                     final HTMLDivElement items,
                                     final ManagedInstance<TreeItem> managedInstance,
                                     final Elemental2DomUtil util) {
        this.view = view;
        this.items = items;
        this.managedInstance = managedInstance;
        this.util = util;
    }

    @Override
    public void init(final DecisionNavigatorTreePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    @Override
    public void clean() {
        items.innerHTML = "";
    }

    @Override
    public void setup(final List<DecisionNavigatorItem> items) {
        this.items.appendChild(makeTree(items));
    }

    @Override
    public void addItem(final DecisionNavigatorItem item,
                        final DecisionNavigatorItem nextItem) {

        final String uuid = item.getParentUUID();
        final Element parentElement = findTreeItemElement(uuid);
        final Element parentChildrenElement = findTreeItemChildrenElement(uuid);
        final Element newChild = makeTreeItemElement(item);
        final Element refChild = findItem(nextItem);

        parentElement.classList.add("parent-node");
        parentChildrenElement.insertBefore(newChild, refChild);
    }

    @Override
    public void update(final DecisionNavigatorItem item,
                       final DecisionNavigatorItem nextItem) {

        final Element parentElement = findTreeItemChildrenElement(item.getParentUUID());
        final Element oldChild = findItem(item);
        final Element newChild = makeTreeItemElement(item);
        final Element refChild = findItem(nextItem);

        oldChild.remove();

        parentElement.insertBefore(newChild, refChild);
    }

    @Override
    public boolean hasItem(final DecisionNavigatorItem item) {
        return findItem(item) != null;
    }

    @Override
    public void remove(final DecisionNavigatorItem item) {
        final Optional<Element> optionalElement = Optional.ofNullable(findItem(item));
        optionalElement.ifPresent(Element::remove);
    }

    @Override
    public void select(final String uuid) {

        final Element newElement = findTreeItemTextElement(uuid);
        final Element oldElement = getSelectedElement();

        deselect(oldElement);
        select(newElement);
        setSelectedElement(newElement);
    }

    @Override
    public void deselect() {
        deselect(getSelectedElement());
    }

    Element getSelectedElement() {
        return selectedElement;
    }

    void setSelectedElement(final Element selectedElement) {
        this.selectedElement = selectedElement;
    }

    Element makeTree(final Collection<DecisionNavigatorItem> items) {

        final Element ulElement = createElement("ul");

        items.forEach(item -> {
            ulElement.appendChild(makeTreeItemElement(item));
        });

        return ulElement;
    }

    Element findItem(final DecisionNavigatorItem decisionNavigatorItem) {

        final Optional<DecisionNavigatorItem> item = Optional.ofNullable(decisionNavigatorItem);

        if (item.isPresent()) {
            return findTreeItemElement(item.get().getUUID());
        }

        return null;
    }

    Element makeTreeItemElement(final DecisionNavigatorItem item) {

        final Element childrenTree = makeTree(item.getChildren());
        final TreeItem treeItem = managedInstance.get().setup(item, childrenTree);

        return util.asHTMLElement(treeItem.getElement());
    }

    void select(final Element element) {
        ofNullable(element).ifPresent(e -> e.classList.add("selected"));
    }

    void deselect(final Element element) {
        ofNullable(element).ifPresent(e -> e.classList.remove("selected"));
    }

    Element findTreeItemElement(final String uuid) {
        return itemsQuerySelector("[data-uuid=\"" + uuid + "\"]");
    }

    Element findTreeItemChildrenElement(final String uuid) {
        return itemsQuerySelector("[data-uuid=\"" + uuid + "\"] ul");
    }

    Element findTreeItemTextElement(final String uuid) {
        return itemsQuerySelector("[data-uuid=\"" + uuid + "\"] div");
    }

    Element createElement(final String tagName) {
        return DomGlobal.document.createElement(tagName);
    }

    Element itemsQuerySelector(final String selector) {
        return items.querySelector(selector);
    }

    @Templated("DecisionNavigatorTreeView.html#item")
    public static class TreeItem implements IsElement {

        @DataField("text")
        private HTMLDivElement text;

        @DataField("icon")
        private HTMLElement icon;

        @DataField("sub-items")
        private HTMLUListElement subItems;

        private DecisionNavigatorItem item;

        @Inject
        public TreeItem(final HTMLDivElement text,
                        final @Named("span") HTMLElement icon,
                        final HTMLUListElement subItems) {
            this.text = text;
            this.icon = icon;
            this.subItems = subItems;
        }

        @EventHandler("icon")
        public void onIconClick(final ClickEvent event) {
            toggle();
            event.stopPropagation();
        }

        @EventHandler("text")
        public void onTextClick(final ClickEvent event) {
            getItem().onClick();
        }

        public TreeItem setup(final DecisionNavigatorItem item,
                              final Element children) {

            this.item = item;

            updateDataUUID();
            updateTitle();
            updateCSSClass();
            updateLabel();
            updateSubItems(children);

            return this;
        }

        void updateDataUUID() {
            getElement().setAttribute("data-uuid", getItem().getUUID());
        }

        void updateTitle() {
            getElement().setAttribute("title", getItem().getLabel());
        }

        void updateCSSClass() {

            getElement().getClassList().add(getCSSClass(getItem()));

            if (getItem().getChildren().size() > 0) {
                getElement().getClassList().add("parent-node");
            }
        }

        void updateLabel() {
            text.appendChild(getTextNode(getItem().getLabel()));
        }

        void updateSubItems(final Element children) {
            subItems.parentNode.replaceChild(children, subItems);
        }

        void toggle() {
            getElement().getClassList().toggle("closed");
        }

        String getCSSClass(final DecisionNavigatorItem item) {

            final String typeName = item.getType().name();

            return "kie-" + typeName.toLowerCase().replace('_', '-');
        }

        DecisionNavigatorItem getItem() {
            return item;
        }

        Text getTextNode(final String label) {
            return DomGlobal.document.createTextNode(label);
        }
    }
}
