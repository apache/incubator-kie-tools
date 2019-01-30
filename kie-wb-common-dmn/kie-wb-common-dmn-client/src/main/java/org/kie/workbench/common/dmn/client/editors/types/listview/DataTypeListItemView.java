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

import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.Element.OnclickCallbackFn;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import elemental2.dom.Text;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.MenuInitializer;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQueryTooltip.$;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asDownArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asNonFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asRightArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isRightArrow;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_ArrowKeysTooltip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_List;

@Dependent
@Templated
public class DataTypeListItemView implements DataTypeListItem.View {

    public static final String UUID_ATTR = "data-row-uuid";

    public static final String NAME_DATA_FIELD = "name-input";

    static final String PARENT_UUID_ATTR = "data-parent-row-uuid";

    static final String ARROW_BUTTON_SELECTOR = "[data-type-field=\"arrow-button\"]";

    private static final int PIXELS_PER_LEVEL = 35;

    @DataField("view")
    private final HTMLDivElement view;

    private final TranslationService translationService;

    private DataTypeListItem presenter;

    @Inject
    public DataTypeListItemView(final HTMLDivElement view,
                                final TranslationService translationService) {
        this.view = view;
        this.translationService = translationService;
    }

    @PostConstruct
    public void setupKebabElement() {
        new MenuInitializer(getKebabMenu(), ".dropdown").init();
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

        setupRowCSSClass(dataType);
    }

    void setupRowCSSClass(final DataType dataType) {

        final String hasSubDataTypesCSSClass = "has-sub-data-types";

        if (dataType.hasSubDataTypes()) {
            getElement().classList.add(hasSubDataTypesCSSClass);
        } else {
            getElement().classList.remove(hasSubDataTypesCSSClass);
        }
    }

    void setupArrow(final DataType dataType) {
        toggleArrow(dataType.hasSubDataTypes());
    }

    void setupIndentationLevel() {

        final int indentationLevel = presenter.getLevel();
        final int marginPixels = PIXELS_PER_LEVEL * indentationLevel;
        final String nestingLevelSelector = ".nesting-level";
        final NodeList<Element> levelElements = getElement().querySelectorAll(nestingLevelSelector);

        for (int i = 0; i < levelElements.length; i++) {

            final Element element = levelElements.getAt(i);
            final String propertyName = "style";
            final String propertyValue = "margin-left: " + marginPixels + "px";

            element.setAttribute(propertyName, propertyValue);
        }
    }

    void setupReadOnly(final DataType dataType) {
        hide(getNameInput());
        setName(dataType.getName());
    }

    void setupActionButtons() {
        showEditButton();
    }

    @Override
    public void toggleArrow(final boolean show) {
        if (show) {
            show(getArrow());
        } else {
            hide(getArrow());
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
        show(getEditButton());
        hide(getSaveButton());
        hide(getCloseButton());
    }

    @Override
    public void showSaveButton() {
        hide(getEditButton());
        show(getSaveButton());
        show(getCloseButton());
    }

    @Override
    public void enableFocusMode() {

        final Element rowElement = getRowElement(getDataType());

        asFocusedDataType(rowElement);
        forEachChildElement(rowElement, ListItemViewCssHelper::asFocusedDataType);

        getNameInput().select();
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
    public boolean isOnFocusMode() {
        return isFocusedDataType(getRowElement(getDataType()));
    }

    @Override
    public String getName() {
        return getNameInput().value;
    }

    @Override
    public void setName(final String name) {
        getNameText().textContent = name;
        getNameInput().value = name;
    }

    @Override
    public void showDataTypeNameInput() {
        hide(getNameText());
        show(getNameInput());
        showLabels();
    }

    @Override
    public void hideDataTypeNameInput() {

        getNameText().textContent = getNameInput().value.isEmpty() ? "-" : getNameInput().value;

        hide(getNameInput());
        show(getNameText());
        hideLabels();
    }

    void showLabels() {
        final NodeList<Element> labels = getLabels();
        for (int i = 0; i < labels.length; i++) {
            show(labels.getAt(i));
        }
    }

    void hideLabels() {
        final NodeList<Element> labels = getLabels();
        for (int i = 0; i < labels.length; i++) {
            hide(labels.getAt(i));
        }
    }

    @Override
    public void setupSelectComponent(final DataTypeSelect typeSelect) {

        final HTMLElement element = typeSelect.getElement();

        getType().innerHTML = "";
        getType().appendChild(element);
    }

    @Override
    public void setupConstraintComponent(final DataTypeConstraint dataTypeConstraintComponent) {
        getConstraintContainer().innerHTML = "";
        getConstraintContainer().appendChild(dataTypeConstraintComponent.getElement());
    }

    @Override
    public void setupListComponent(final SmallSwitchComponent dataTypeListComponent) {
        getListContainer().innerHTML = "";
        getListContainer().appendChild(listTextNode());
        getListContainer().appendChild(dataTypeListComponent.getElement());
    }

    Text listTextNode() {
        return DomGlobal.document.createTextNode(list());
    }

    @Override
    public void showListContainer() {
        show(getListContainer());
    }

    @Override
    public void hideKebabMenu() {
        hide(getKebabMenu());
    }

    @Override
    public void showKebabMenu() {
        show(getKebabMenu());
    }

    @Override
    public void hideListContainer() {
        hide(getListContainer());
    }

    @Override
    public void showListYesLabel() {
        show(getListYes());
    }

    @Override
    public void hideListYesLabel() {
        hide(getListYes());
    }

    @Override
    public boolean isCollapsed() {
        return isCollapsed(getArrow());
    }

    Element getRowElement(final DataType dataType) {
        return getRowElement(dataType.getUUID());
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
        setupReadOnly(dataType);
        setupActionButtons();
        setupEventHandlers();
        setupShortcutsTooltips();
    }

    void setupShortcutsTooltips() {

        final String arrowKeysTooltip = translationService.format(DataTypeListItemView_ArrowKeysTooltip);

        setTitleAttribute(getEditButton(), "Ctrl + E");
        setTitleAttribute(getSaveButton(), "Ctrl + S");
        setTitleAttribute(getInsertNestedField(), "Ctrl + B");
        setTitleAttribute(getInsertFieldAbove(), "Ctrl + U");
        setTitleAttribute(getInsertFieldBelow(), "Ctrl + D");
        setTitleAttribute(getRemoveButton(), "Ctrl + Backspace");
        setTitleAttribute(getCloseButton(), "Esc");
        setTitleAttribute(getArrow(), arrowKeysTooltip);
        setupTooltips();
    }

    private void setTitleAttribute(final Element element,
                                   final String value) {
        final String attribute = "title";
        element.setAttribute(attribute, value);
    }

    void setupTooltips() {
        $(getElement().querySelectorAll("[data-toggle='tooltip']")).tooltip();
    }

    void setupEventHandlers() {
        getEditButton().onclick = getOnEditAction();
        getSaveButton().onclick = getOnSaveAction();
        getCloseButton().onclick = getOnCloseAction();
        getArrow().onclick = getOnArrowClickAction();
        getInsertFieldAbove().onclick = getOnInsertFieldAboveAction();
        getInsertFieldBelow().onclick = getOnInsertFieldBelowAction();
        getInsertNestedField().onclick = getOnInsertNestedFieldAction();
        getRemoveButton().onclick = getOnRemoveButtonAction();
    }

    OnclickCallbackFn getOnEditAction() {
        return (e) -> {
            presenter.enableEditMode();
            return true;
        };
    }

    OnclickCallbackFn getOnSaveAction() {
        return (e) -> {
            presenter.saveAndCloseEditMode();
            return true;
        };
    }

    OnclickCallbackFn getOnCloseAction() {
        return (e) -> {
            presenter.disableEditMode();
            return true;
        };
    }

    OnclickCallbackFn getOnArrowClickAction() {
        return (e) -> {
            presenter.expandOrCollapseSubTypes();
            return true;
        };
    }

    OnclickCallbackFn getOnInsertFieldAboveAction() {
        return (e) -> {
            presenter.insertFieldAbove();
            return true;
        };
    }

    OnclickCallbackFn getOnInsertFieldBelowAction() {
        return (e) -> {
            presenter.insertFieldBelow();
            return true;
        };
    }

    OnclickCallbackFn getOnInsertNestedFieldAction() {
        return (e) -> {
            presenter.insertNestedField();
            return true;
        };
    }

    OnclickCallbackFn getOnRemoveButtonAction() {
        return (e) -> {
            presenter.remove();
            return true;
        };
    }

    private String list() {
        return translationService.format(DataTypeListItemView_List);
    }

    Element getArrow() {
        return querySelector("arrow-button");
    }

    Element getNameText() {
        return querySelector("name-text");
    }

    HTMLInputElement getNameInput() {
        return (HTMLInputElement) querySelector(NAME_DATA_FIELD);
    }

    Element getType() {
        return querySelector("type");
    }

    Element getConstraintContainer() {
        return querySelector("constraint-container");
    }

    Element getListContainer() {
        return querySelector("list-container");
    }

    Element getListYes() {
        return querySelector("list-yes");
    }

    Element getEditButton() {
        return querySelector("edit-button");
    }

    Element getSaveButton() {
        return querySelector("save-button");
    }

    Element getCloseButton() {
        return querySelector("close-button");
    }

    Element getRemoveButton() {
        return querySelector("remove-button");
    }

    Element getInsertFieldAbove() {
        return querySelector("insert-field-above");
    }

    Element getInsertFieldBelow() {
        return querySelector("insert-field-below");
    }

    Element getInsertNestedField() {
        return querySelector("insert-nested-field");
    }

    Element getKebabMenu() {
        return querySelector("kebab-menu");
    }

    NodeList<Element> getLabels() {
        return getElement().querySelectorAll(".data-type-label");
    }

    Element querySelector(final String fieldName) {
        return getElement().querySelector("[data-type-field=\"" + fieldName + "\"]");
    }
}
