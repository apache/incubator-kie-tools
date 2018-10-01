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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.CSSProperties.MarginLeftUnionType;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.KebabMenuInitializer;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper;

import static org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asDownArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asNonFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asRightArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isRightArrow;

@Dependent
@Templated
public class DataTypeListItemView implements DataTypeListItem.View {

    static final String UUID_ATTR = "data-row-uuid";

    static final String PARENT_UUID_ATTR = "data-parent-row-uuid";

    static final String ARROW_BUTTON_SELECTOR = "[data-field=\"arrow-button\"]";

    private static final int PIXELS_PER_LEVEL = 35;

    @DataField("view")
    private final HTMLDivElement view;

    @DataField("level")
    private final HTMLElement level;

    @DataField("arrow-button")
    private final HTMLElement arrow;

    @DataField("name-text")
    private final HTMLElement nameText;

    @DataField("name-input")
    private final HTMLInputElement nameInput;

    @DataField("type")
    private final HTMLElement type;

    @DataField("edit-button")
    private final HTMLButtonElement editButton;

    @DataField("save-button")
    private final HTMLButtonElement saveButton;

    @DataField("close-button")
    private final HTMLButtonElement closeButton;

    @DataField("remove-button")
    private final HTMLAnchorElement removeButton;

    @DataField("kebab-menu")
    private HTMLDivElement kebabMenu;

    private DataTypeListItem presenter;

    @Inject
    public DataTypeListItemView(final HTMLDivElement view,
                                final @Named("span") HTMLElement level,
                                final @Named("span") HTMLElement arrow,
                                final @Named("span") HTMLElement nameText,
                                final HTMLInputElement nameInput,
                                final @Named("span") HTMLElement type,
                                final HTMLButtonElement editButton,
                                final HTMLButtonElement saveButton,
                                final HTMLButtonElement closeButton,
                                final HTMLAnchorElement removeButton,
                                final HTMLDivElement kebabMenu) {
        this.view = view;
        this.level = level;
        this.arrow = arrow;
        this.nameText = nameText;
        this.nameInput = nameInput;
        this.type = type;
        this.editButton = editButton;
        this.saveButton = saveButton;
        this.closeButton = closeButton;
        this.removeButton = removeButton;
        this.kebabMenu = kebabMenu;
    }

    @PostConstruct
    public void setupKebabElement() {
        new KebabMenuInitializer(kebabMenu).init();
    }

    @Override
    public void init(final DataTypeListItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    void setupRowMetadata(final DataType dataType) {
        getElement().setAttribute(UUID_ATTR, dataType.getUUID());
        getElement().setAttribute(PARENT_UUID_ATTR, dataType.getParentUUID());
    }

    void setupArrow(final DataType dataType) {
        toggleArrow(dataType.hasSubDataTypes());
    }

    void setupIndentationLevel() {

        final int indentationLevel = presenter.getLevel();
        final int marginPixels = PIXELS_PER_LEVEL * indentationLevel;

        this.level.style.marginLeft = margin(marginPixels);
    }

    void setupNameComponent(final DataType dataType) {
        hide(nameInput);
        setName(dataType.getName());
    }

    void setupActionButtons() {
        showEditButton();
    }

    @Override
    public void toggleArrow(final boolean show) {
        if (show) {
            show(arrow);
        } else {
            hide(arrow);
        }
    }

    @Override
    public void expand() {

        final Element parent = getRowElement(getDataType());

        asDownArrow(getArrow());
        forEachChildElement(parent, child -> {
            show(child);
            return !isCollapsed(child.querySelector(ARROW_BUTTON_SELECTOR));
        });
    }

    @Override
    public void collapse() {

        final Element parent = getRowElement(getDataType());

        asRightArrow(getArrow());
        forEachChildElement(parent, HiddenHelper::hide);
    }

    @Override
    public void showEditButton() {
        show(editButton);
        hide(saveButton);
        hide(closeButton);
    }

    @Override
    public void showSaveButton() {
        hide(editButton);
        show(saveButton);
        show(closeButton);
    }

    @EventHandler("edit-button")
    public void onEditClick(final ClickEvent e) {
        presenter.enableEditMode();
    }

    @EventHandler("save-button")
    public void onSaveClick(final ClickEvent e) {
        presenter.saveAndCloseEditMode();
    }

    @EventHandler("close-button")
    public void onCloseClick(final ClickEvent e) {
        presenter.disableEditMode();
    }

    @EventHandler("arrow-button")
    public void onArrowClickEvent(final ClickEvent e) {
        presenter.expandOrCollapseSubTypes();
    }

    @EventHandler("remove-button")
    public void onRemoveButton(final ClickEvent e) {
        presenter.remove();
    }

    @Override
    public void enableFocusMode() {
        final Element rowElement = getRowElement(getDataType());
        asFocusedDataType(rowElement);
        forEachChildElement(rowElement, ListItemViewCssHelper::asFocusedDataType);
    }

    @Override
    public void disableFocusMode() {
        final Element rowElement = getRowElement(getDataType());
        if (rowElement != null) {
            asNonFocusedDataType(rowElement);
            forEachChildElement(rowElement, ListItemViewCssHelper::asNonFocusedDataType);
        }
    }

    @Override
    public String getName() {
        return nameInput.value;
    }

    @Override
    public void setName(final String name) {
        nameText.textContent = name;
        nameInput.value = name;
    }

    @Override
    public void showDataTypeNameInput() {
        hide(nameText);
        show(nameInput);
    }

    @Override
    public void hideDataTypeNameInput() {

        nameText.textContent = nameInput.value.isEmpty() ? "-" : nameInput.value;

        hide(nameInput);
        show(nameText);
    }

    @Override
    public void setupSelectComponent(final DataTypeSelect typeSelect) {
        type.innerHTML = "";
        type.appendChild(typeSelect.getElement());
    }

    @Override
    public boolean isCollapsed() {
        return isCollapsed(getArrow());
    }

    HTMLElement getArrow() {
        return arrow;
    }

    Element getRowElement(final DataType dataType) {
        return getRowElement(dataType.getUUID());
    }

    MarginLeftUnionType margin(final int pixels) {
        return MarginLeftUnionType.of(pixels + "px");
    }

    boolean isCollapsed(final Element arrow) {
        return isRightArrow(arrow);
    }

    private void forEachChildElement(final Element parent,
                                     final Consumer<Element> consumer) {
        forEachChildElement(parent, element -> {
            consumer.accept(element);
            return true;
        });
    }

    private void forEachChildElement(final Element parent,
                                     final Function<Element, Boolean> consumer) {

        final NodeList<Element> children = getChildren(parent);

        for (int i = 0; i < children.length; i++) {
            final Element child = children.getAt(i);
            if (consumer.apply(child)) {
                forEachChildElement(child, consumer);
            }
        }
    }

    private Element getRowElement(final String uuid) {
        return dataTypeListElement().querySelector("[" + UUID_ATTR + "=\"" + uuid + "\"]");
    }

    private NodeList<Element> getChildren(final Element parent) {
        final String childrenSelector = "[" + PARENT_UUID_ATTR + "=\"" + parent.getAttribute(UUID_ATTR) + "\"]";
        return dataTypeListElement().querySelectorAll(childrenSelector);
    }

    HTMLElement dataTypeListElement() {
        return presenter.getDataTypeList().getElement();
    }

    DataType getDataType() {
        return presenter.getDataType();
    }

    @Override
    public void setDataType(final DataType dataType) {
        setupRowMetadata(dataType);
        setupArrow(dataType);
        setupIndentationLevel();
        setupNameComponent(dataType);
        setupActionButtons();
    }
}
