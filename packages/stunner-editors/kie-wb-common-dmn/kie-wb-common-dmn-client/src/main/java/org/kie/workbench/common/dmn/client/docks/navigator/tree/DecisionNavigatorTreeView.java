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

package org.kie.workbench.common.dmn.client.docks.navigator.tree;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLUListElement;
import elemental2.dom.Text;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.docks.navigator.DecisionNavigatorItem;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.mvp.LockRequiredEvent;
import org.uberfire.client.workbench.ouia.OuiaAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponent;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

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
        RemoveHelper.removeChildren(items);
    }

    @Override
    public void setup(final List<DecisionNavigatorItem> items) {
        this.items.appendChild(makeTree(items));
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
        if (!Objects.isNull(items)) {
            items.forEach(item -> {
                ulElement.appendChild(makeTreeItemElement(item));
            });
        }

        return ulElement;
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
    public static class TreeItem implements IsElement,
                                            OuiaComponent {

        private final ReadOnlyProvider readOnlyProvider;
        @DataField("text-content")
        private HTMLElement textContent;

        @DataField("input-text")
        private HTMLInputElement inputText;

        @DataField("icon")
        private HTMLElement icon;

        @DataField("sub-items")
        private HTMLUListElement subItems;

        @DataField("save")
        private HTMLElement save;

        @DataField("edit")
        private HTMLElement edit;

        @DataField("remove")
        private HTMLElement remove;

        private DecisionNavigatorItem item;

        private final Event<LockRequiredEvent> locker;

        @Inject
        public TreeItem(final @Named("span") HTMLElement textContent,
                        final HTMLInputElement inputText,
                        final @Named("span") HTMLElement icon,
                        final HTMLUListElement subItems,
                        final @Named("i") HTMLElement save,
                        final @Named("i") HTMLElement edit,
                        final @Named("i") HTMLElement remove,
                        final Event<LockRequiredEvent> locker,
                        final ReadOnlyProvider readOnlyProvider) {
            this.textContent = textContent;
            this.inputText = inputText;
            this.icon = icon;
            this.subItems = subItems;
            this.save = save;
            this.edit = edit;
            this.remove = remove;
            this.locker = locker;
            this.readOnlyProvider = readOnlyProvider;
        }

        @EventHandler("icon")
        public void onIconClick(final ClickEvent event) {
            toggle();
            event.stopPropagation();
        }

        @EventHandler("text-content")
        public void onTextContentClick(final ClickEvent event) {
            getItem().onClick();
        }

        @EventHandler("input-text")
        public void onInputTextKeyPress(final KeyDownEvent event) {
            if (event.getNativeEvent().getKeyCode() == 13) {
                save();
            }
        }

        @EventHandler("input-text")
        public void onInputTextBlur(final BlurEvent event) {
            save();
        }

        @EventHandler("save")
        public void onSaveClick(final ClickEvent event) {
            save();
        }

        @EventHandler("edit")
        public void onEditClick(final ClickEvent event) {
            getElement().getClassList().add("editing");
        }

        @EventHandler("remove")
        public void onRemoveClick(final ClickEvent event) {
            locker.fire(new LockRequiredEvent());
            getItem().onRemove();
        }

        void save() {
            locker.fire(new LockRequiredEvent());
            getItem().setLabel(inputText.value);
            getElement().getClassList().remove("editing");
            updateLabel();
            getItem().onUpdate();
        }

        public TreeItem setup(final DecisionNavigatorItem item,
                              final Element children) {

            this.item = item;

            updateDataUUID();
            updateTitle();
            updateCSSClass();
            updateLabel();
            updateSubItems(children);
            initOuiaComponentAttributes();
            return this;
        }

        @Override
        public Consumer<OuiaAttribute> ouiaAttributeRenderer() {
            return ouiaAttribute -> getElement().setAttribute(ouiaAttribute.getName(), ouiaAttribute.getValue());
        }

        @Override
        public OuiaComponentTypeAttribute ouiaComponentType() {
            return new OuiaComponentTypeAttribute(componentType());
        }

        @Override
        public OuiaComponentIdAttribute ouiaComponentId() {
            return new OuiaComponentIdAttribute(componentType() + "-" + getItem().getLabel());
        }

        private String componentType() {
            if (getItem() != null) {
                return "dmn-graph-navigator-" + getItem().getType().name().toLowerCase();
            } else {
                throw new IllegalStateException("Decision Navigator item can not be null");
            }
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

            if (!readOnlyProvider.isReadOnlyDiagram()) {
                if (getItem().isEditable()) {
                    getElement().getClassList().add("editable");
                }
            }
        }

        void updateLabel() {
            final String label = getItem().getLabel();
            inputText.value = label;
            textContent.appendChild(getTextNode(label));
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
