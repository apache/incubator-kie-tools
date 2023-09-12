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

package org.kie.workbench.common.dmn.client.editors.types.listview;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.Element;
import elemental2.dom.Element.OnclickFn;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.NodeList;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.client.editors.common.RemoveHelper;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper;
import org.kie.workbench.common.dmn.client.editors.types.listview.common.SmallSwitchComponent;
import org.kie.workbench.common.dmn.client.editors.types.listview.constraint.DataTypeConstraint;
import org.kie.workbench.common.stunner.core.client.ReadOnlyProvider;
import org.uberfire.client.workbench.ouia.OuiaAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponent;
import org.uberfire.client.workbench.ouia.OuiaComponentIdAttribute;
import org.uberfire.client.workbench.ouia.OuiaComponentTypeAttribute;

import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.hide;
import static org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper.show;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.JQueryTooltip.$;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asDownArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asNonFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.asRightArrow;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isFocusedDataType;
import static org.kie.workbench.common.dmn.client.editors.types.listview.common.ListItemViewCssHelper.isRightArrow;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_AddRowBelow;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_ArrowKeysTooltip;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Cancel;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Edit;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Remove;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DataTypeListItemView_Save;

@Dependent
@Templated
public class DataTypeListItemView implements DataTypeListItem.View,
                                             OuiaComponent {

    public static final String UUID_ATTR = "data-row-uuid";

    public static final String NAME_DATA_FIELD = "name-input";

    public static final String PARENT_UUID_ATTR = "data-parent-row-uuid";

    static final String ARROW_BUTTON_SELECTOR = "[data-type-field=\"arrow-button\"]";

    @DataField("view")
    private final HTMLDivElement view;

    private final TranslationService translationService;

    private final ReadOnlyProvider readOnlyProvider;

    private DataTypeListItem presenter;

    @Inject
    public DataTypeListItemView(final HTMLDivElement view,
                                final TranslationService translationService,
                                final ReadOnlyProvider readOnlyProvider) {
        this.view = view;
        this.translationService = translationService;
        this.readOnlyProvider = readOnlyProvider;
    }

    @Override
    public void init(final DataTypeListItem presenter) {
        this.presenter = presenter;
    }

    @Override
    public HTMLElement getElement() {
        return view;
    }

    @Override
    public OuiaComponentTypeAttribute ouiaComponentType() {
        return new OuiaComponentTypeAttribute("dmn-data-type-item");
    }

    @Override
    public OuiaComponentIdAttribute ouiaComponentId() {
        return new OuiaComponentIdAttribute(presenter != null && presenter.getDataType() != null ?
                                                    presenter.getDataType().getName() : "unknown");
    }

    @Override
    public Consumer<OuiaAttribute> ouiaAttributeRenderer() {
        return ouiaAttribute -> view.setAttribute(ouiaAttribute.getName(),
                                                  ouiaAttribute.getValue());
    }

    void setupRowMetadata(final DataType dataType) {

        getDragAndDropElement().setAttribute(UUID_ATTR, dataType.getUUID());
        getDragAndDropElement().setAttribute(PARENT_UUID_ATTR, dataType.getParentUUID());

        setupRowCSSClass(dataType);
        initOuiaComponentAttributes();
    }

    void setupRowCSSClass(final DataType dataType) {
        setupSubDataTypesCSSClass(dataType);
        setupReadOnlyCSSClass(dataType);
    }

    void setupSubDataTypesCSSClass(final DataType dataType) {
        final String hasSubDataTypesCSSClass = "has-sub-data-types";

        if (dataType.hasSubDataTypes()) {
            getDragAndDropElement().classList.add(hasSubDataTypesCSSClass);
        } else {
            getDragAndDropElement().classList.remove(hasSubDataTypesCSSClass);
        }
    }

    void setupReadOnlyCSSClass(final DataType dataType) {

        final String readOnlyCSSClass = "read-only";

        if (dataType.isReadOnly() || readOnlyProvider.isReadOnlyDiagram()) {
            getDragAndDropElement().classList.add(readOnlyCSSClass);
        } else {
            getDragAndDropElement().classList.remove(readOnlyCSSClass);
        }
    }

    void setupArrow(final DataType dataType) {
        toggleArrow(dataType.hasSubDataTypes());
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
        final int parentPositionY = presenter.getPositionY(parent);

        final AtomicInteger i = new AtomicInteger(1);

        asDownArrow(getArrow());
        final int childrenCount = getChildren(parent).length;
        forEachChildElement(parent, child -> {

            show(child);
            double positionY = parentPositionY + (i.getAndIncrement() / (childrenCount + 1.0));
            presenter.setPositionY(child, positionY);

            return !isCollapsed(child.querySelector(ARROW_BUTTON_SELECTOR));
        });

        presenter.refreshItemsCSSAndHTMLPosition();
    }

    @Override
    public void collapse() {

        final Element parent = getRowElement(getDataType());

        asRightArrow(getArrow());
        forEachChildElement(parent, element -> {

            presenter.setPositionY(element, -2);

            HiddenHelper.hide(element);
        });

        presenter.refreshItemsCSSAndHTMLPosition();
    }

    @Override
    public void showEditButton() {
        show(getEditButton());
        show(getAddDataTypeRowButton());
        show(getRemoveButton());
        hide(getSaveButton());
        hide(getCloseButton());
    }

    @Override
    public void showSaveButton() {
        hide(getEditButton());
        hide(getAddDataTypeRowButton());
        hide(getRemoveButton());
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

        RemoveHelper.removeChildren(getType());
        getType().appendChild(element);
    }

    @Override
    public void setupConstraintComponent(final DataTypeConstraint dataTypeConstraintComponent) {
        RemoveHelper.removeChildren(getConstraintContainer());
        getConstraintContainer().appendChild(dataTypeConstraintComponent.getElement());
    }

    @Override
    public void setupListComponent(final SmallSwitchComponent dataTypeListComponent) {
        RemoveHelper.removeChildren(getListCheckBoxContainer());
        getListCheckBoxContainer().appendChild(dataTypeListComponent.getElement());
    }

    @Override
    public void showListContainer() {
        show(getListContainer());
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
        return getDragAndDropListElement().querySelector("[" + UUID_ATTR + "=\"" + uuid + "\"]");
    }

    private NodeList<Element> getChildren(final Element parent) {
        final String childrenSelector = "[" + PARENT_UUID_ATTR + "=\"" + parent.getAttribute(UUID_ATTR) + "\"]";
        return getDragAndDropListElement().querySelectorAll(childrenSelector);
    }

    DataType getDataType() {
        return presenter.getDataType();
    }

    @Override
    public void setDataType(final DataType dataType) {
        setupRowMetadata(dataType);
        setupArrow(dataType);
        setupReadOnly(dataType);
        setupActionButtons();
        setupEventHandlers();
        setupShortcutsTooltips();
    }

    void setupShortcutsTooltips() {

        final String arrowKeysTooltip = translationService.format(DataTypeListItemView_ArrowKeysTooltip);

        setTitleAttribute(getEditButton(), translationService.format(DataTypeListItemView_Edit));
        setTitleAttribute(getSaveButton(), translationService.format(DataTypeListItemView_Save));
        setTitleAttribute(getAddDataTypeRowButton(), translationService.format(DataTypeListItemView_AddRowBelow));
        setTitleAttribute(getRemoveButton(), translationService.format(DataTypeListItemView_Remove));
        setTitleAttribute(getCloseButton(), translationService.format(DataTypeListItemView_Cancel));
        setTitleAttribute(getArrow(), arrowKeysTooltip);
        setupTooltips();
    }

    private void setTitleAttribute(final Element element,
                                   final String value) {
        final String attribute = "data-title";
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
        getAddDataTypeRowButton().onclick = getOnAddDataTypeRowAction();
        getRemoveButton().onclick = getOnRemoveButtonAction();
    }

    private HTMLElement getDragAndDropListElement() {
        return presenter.getDragAndDropListElement();
    }

    private HTMLElement getDragAndDropElement() {
        return presenter.getDragAndDropElement();
    }

    OnclickFn getOnEditAction() {
        return (e) -> {
            presenter.enableEditMode();
            return true;
        };
    }

    OnclickFn getOnSaveAction() {
        return (e) -> {
            presenter.saveAndCloseEditMode();
            ouiaAttributeRenderer().accept(ouiaComponentId());
            return true;
        };
    }

    OnclickFn getOnCloseAction() {
        return (e) -> {
            presenter.disableEditMode();
            return true;
        };
    }

    OnclickFn getOnArrowClickAction() {
        return (e) -> {
            presenter.expandOrCollapseSubTypes();
            return true;
        };
    }

    OnclickFn getOnAddDataTypeRowAction() {
        return (e) -> {
            presenter.addDataTypeRow();
            return true;
        };
    }

    OnclickFn getOnRemoveButtonAction() {
        return (e) -> {
            presenter.remove();
            return true;
        };
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

    Element getListCheckBoxContainer() {
        return querySelector("list-checkbox-container");
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

    Element getAddDataTypeRowButton() {
        return querySelector("add-data-type-row-button");
    }

    NodeList<Element> getLabels() {
        return getElement().querySelectorAll(".data-type-label");
    }

    Element querySelector(final String fieldName) {
        return getElement().querySelector("[data-type-field=\"" + fieldName + "\"]");
    }
}
