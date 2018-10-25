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
import elemental2.dom.DomGlobal;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import elemental2.dom.Text;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.KebabMenuInitializer;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asDownArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asNonFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asRightArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isRightArrow;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Collection;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Constraints;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

@Dependent
@Templated
public class DataTypeListItemView implements DataTypeListItem.View {

    public static final String UUID_ATTR = "data-row-uuid";

    public static final String NAME_DATA_FIELD = "name-input";

    static final String PARENT_UUID_ATTR = "data-parent-row-uuid";

    static final String ARROW_BUTTON_SELECTOR = "[data-field=\"arrow-button\"]";

    private static final int PIXELS_PER_LEVEL = 35;

    @DataField("view")
    private final HTMLDivElement view;

    @DataField("arrow-button")
    private final HTMLElement arrow;

    @DataField("name-text")
    private final HTMLElement nameText;

    @DataField("constraint-text")
    private final HTMLElement constraintText;

    @DataField(NAME_DATA_FIELD)
    private final HTMLInputElement nameInput;

    @DataField("type")
    private final HTMLElement type;

    @DataField("constraint")
    private final HTMLDivElement constraint;

    @DataField("collection-container")
    private final HTMLDivElement collectionContainer;

    @DataField("collection-yes")
    private final HTMLDivElement collectionYes;

    @DataField("constraint-container")
    private final HTMLDivElement constraintContainer;

    @DataField("edit-button")
    private final HTMLButtonElement editButton;

    @DataField("save-button")
    private final HTMLButtonElement saveButton;

    @DataField("close-button")
    private final HTMLButtonElement closeButton;

    @DataField("remove-button")
    private final HTMLAnchorElement removeButton;

    @DataField("insert-field-above")
    private final HTMLAnchorElement insertFieldAbove;

    @DataField("insert-field-below")
    private final HTMLAnchorElement insertFieldBelow;

    @DataField("insert-nested-field")
    private final HTMLAnchorElement insertNestedField;

    @DataField("kebab-menu")
    private final HTMLDivElement kebabMenu;

    private final TranslationService translationService;

    private DataTypeListItem presenter;

    @Inject
    public DataTypeListItemView(final HTMLDivElement view,
                                final @Named("span") HTMLElement arrow,
                                final @Named("span") HTMLElement nameText,
                                final @Named("span") HTMLElement constraintText,
                                final HTMLInputElement nameInput,
                                final @Named("span") HTMLElement type,
                                final HTMLDivElement collectionContainer,
                                final HTMLDivElement collectionYes,
                                final HTMLDivElement constraint,
                                final HTMLDivElement constraintContainer,
                                final HTMLButtonElement editButton,
                                final HTMLButtonElement saveButton,
                                final HTMLButtonElement closeButton,
                                final HTMLAnchorElement removeButton,
                                final HTMLAnchorElement insertFieldAbove,
                                final HTMLAnchorElement insertFieldBelow,
                                final HTMLAnchorElement insertNestedField,
                                final HTMLDivElement kebabMenu,
                                final TranslationService translationService) {
        this.view = view;
        this.arrow = arrow;
        this.nameText = nameText;
        this.constraintText = constraintText;
        this.nameInput = nameInput;
        this.type = type;
        this.collectionContainer = collectionContainer;
        this.collectionYes = collectionYes;
        this.constraint = constraint;
        this.constraintContainer = constraintContainer;
        this.editButton = editButton;
        this.saveButton = saveButton;
        this.closeButton = closeButton;
        this.removeButton = removeButton;
        this.insertFieldAbove = insertFieldAbove;
        this.insertFieldBelow = insertFieldBelow;
        this.insertNestedField = insertNestedField;
        this.kebabMenu = kebabMenu;
        this.translationService = translationService;
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
        hide(nameInput);
        setName(dataType.getName());
        setConstraint(dataType.getConstraint());
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

    @EventHandler("insert-field-above")
    public void onInsertFieldAbove(final ClickEvent e) {
        presenter.insertFieldAbove();
    }

    @EventHandler("insert-field-below")
    public void onInsertFieldBelow(final ClickEvent e) {
        presenter.insertFieldBelow();
    }

    @EventHandler("insert-nested-field")
    public void onInsertNestedField(final ClickEvent e) {
        presenter.insertNestedField();
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

        nameInput.select();
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
    public void setConstraint(final String constraint) {
        if (isEmpty(constraint)) {
            constraintText.textContent = "";
            hide(constraintText);
        } else {
            constraintText.textContent = translationService.format(DataTypeListItemView_Constraints, constraint);
            show(constraintText);
        }
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
    public void setupConstraintComponent(final DataTypeConstraint dataTypeConstraintComponent) {
        constraint.innerHTML = "";
        constraint.appendChild(dataTypeConstraintComponent.getElement());
    }

    @Override
    public void setupCollectionComponent(final SmallSwitchComponent dataTypeCollectionComponent) {
        collectionContainer.innerHTML = "";
        collectionContainer.appendChild(collectionTextNode());
        collectionContainer.appendChild(dataTypeCollectionComponent.getElement());
    }

    Text collectionTextNode() {
        return DomGlobal.document.createTextNode(collection());
    }

    @Override
    public void showConstraintContainer() {
        show(constraintContainer);
    }

    @Override
    public void hideConstraintContainer() {
        hide(constraintContainer);
    }

    @Override
    public void showCollectionContainer() {
        show(collectionContainer);
    }

    @Override
    public void hideCollectionContainer() {
        hide(collectionContainer);
    }

    @Override
    public void showCollectionYesLabel() {
        show(collectionYes);
    }

    @Override
    public void hideCollectionYesLabel() {
        hide(collectionYes);
    }

    @Override
    public void showConstraintText() {
        if (!isEmpty(constraintText.textContent)) {
            show(constraintText);
        }
    }

    @Override
    public void hideConstraintText() {
        hide(constraintText);
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
    }

    private String collection() {
        return translationService.format(DataTypeListItemView_Collection);
    }
}
