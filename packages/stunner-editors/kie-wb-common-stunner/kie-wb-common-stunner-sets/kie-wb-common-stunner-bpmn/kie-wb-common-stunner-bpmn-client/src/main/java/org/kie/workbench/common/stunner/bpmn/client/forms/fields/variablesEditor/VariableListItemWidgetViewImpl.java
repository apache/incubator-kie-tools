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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.common.base.Strings;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import elemental2.dom.CSSProperties;
import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLLabelElement;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.uberfire.workbench.events.NotificationEvent;

import static jsinterop.annotations.JsPackage.GLOBAL;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link VariableRow}s.
 * <p>
 * The Name field of VariableRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated(value = "VariablesEditorWidget.html#variableRow", stylesheet = "VariablesEditorWidget.css")
public class VariableListItemWidgetViewImpl implements VariableListItemWidgetView,
                                                       ComboBoxView.ModelPresenter {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(VariableRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. variableRow.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    protected DataBinder<VariableRow> variableRow;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox name;

    private String currentValue;
    private String currentName;
    private Set<String> tagSet = new HashSet<>();

    protected boolean isOpen = false;

    @Inject
    private elemental2.dom.Document document;

    @Inject
    @DataField("variable-tags-settings")
    protected HTMLAnchorElement variableTagsSettings;

    @Inject
    @DataField("tags-div")
    protected HTMLDivElement tagsDiv;

    @Inject
    @DataField
    HTMLDivElement tagsContainer;

    @Inject
    @DataField
    protected Button closeButton;

    @Inject
    @DataField
    private HTMLLabelElement acceptButton;

    @Inject
    @DataField
    protected HTMLLabelElement tagCount;

    @Inject
    @DataField
    protected CustomDataTypeTextBox customTagName;

    protected Map<String, HTMLAnchorElement> removeButtons = new HashMap<>();

    @Inject
    protected ComboBox tagNamesComboBox;

    protected List<String> tagNamesList = new ArrayList<>();

    protected String overlayTopPosition = null;

    private static final String OVERLAY_MAX_WIDTH = "280px";
    private static final String OVERLAY_MIN_HEIGHT = "280px";
    private static final String OVERLAY_DISPLAY = "block";

    public Set<String> getTagSet() {
        return tagSet;
    }

    public void setTagSet(Set<String> tagSet) {
        this.tagSet = tagSet;
    }

    public String getPreviousCustomValue() {
        return previousCustomValue;
    }

    public void setPreviousCustomValue(String previousCustomValue) {
        this.previousCustomValue = previousCustomValue;
    }

    private String previousCustomValue = "";

    @DataField
    protected ValueListBox<String> dataType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            String s = "";
            if (object != null) {
                s = object.toString();
            }
            return s;
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected CustomDataTypeTextBox customDataType;

    @Inject
    protected ComboBox dataTypeComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    @DataField
    protected TableCellElement tagsTD = Document.get().createTDElement();

    @DataField
    protected ValueListBox<String> defaultTagNames = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    /**
     * Required for implementation of Delete button.
     */
    private VariablesEditorWidgetView.Presenter parentWidget;

    public void setParentWidget(final VariablesEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = parentWidget;
    }

    private String lastCustomValueForTags = "";
    private String lastCustomValueForDataType = "";

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        if (textBox == customDataType) {
            parentWidget.addDataType(value, lastCustomValueForDataType);
            setCustomDataType(value);
            lastCustomValueForDataType = value;
        } else {
            lastCustomValueForTags = value;
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        if (listBox == dataType) {
            setDataTypeDisplayName(value);
        }
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        String value = lastCustomValueForTags;

        if (listBox == dataType) {
            value = getCustomDataType();
            if (value == null || value.isEmpty()) {
                value = getDataTypeDisplayName();
            }
        }

        return value;
    }

    @PostConstruct
    public void init() {
        name.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                       StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                       StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());

        name.addChangeHandler(event -> {
            String value = name.getText();
            if (isDuplicateID(value)) {
                notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.DuplicatedVariableIDError(value),
                                                        NotificationEvent.NotificationType.ERROR));
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            } else if (isDuplicateName(value)) {
                notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.CONSTANTS.DuplicatedVariableNameError(value),
                                                        NotificationEvent.NotificationType.ERROR));
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            } else if (isBoundToNodes(currentName)) {
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            }
            notifyModelChanged();
        });
        dataTypeComboBox.init(this,
                              true,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
        customDataType.setRegExp(StringUtils.ALPHA_NUM_UNDERSCORE_DOT_GT_LT_REGEXP,
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name(),
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Unbalanced_GT_LT_from_name());
        customDataType.addKeyDownHandler(this::preventSpaces);

        PopOver.jQuery(variableTagsSettings).popovers();

        setTagTittle("Tags: ");

        variableTagsSettings.onclick = e -> {
            e.preventDefault();
            openOverlayActions();
            return null;
        };

        customTagName.addFocusHandler(focusEvent -> setPreviousCustomValue(customTagName.getValue()));
        customTagName.addKeyDownHandler(this::preventSpaces);

        loadDefaultTagNames();
        setTagsListItems();
    }

    protected void openOverlayActions() {
        isOpen = !isOpen;
        if (!isOpen) { // Closing Overlay
            parentWidget.setLastOverlayOpened(null);
            return;
        }

        final HTMLDivElement overlayDiv = getNextElementSibling();
        parentWidget.closeLastOverlay();

        final Element lastNode = getLastElementChild(overlayDiv);
        lastNode.innerHTML = "";
        lastNode.appendChild(tagsDiv);

        if (overlayTopPosition != null) {
            overlayDiv.style.top = overlayTopPosition;
        }

        final Double offsetX = (tagCount.getBoundingClientRect().left - overlayDiv.getBoundingClientRect().left);
        overlayDiv.style.left = (parseDimension(overlayDiv.style.left) - offsetX + 10) + "px";
        setOverlayDimensions(overlayDiv);
        tagsDiv.style.display = OVERLAY_DISPLAY;
        parentWidget.setLastOverlayOpened(this.closeButton);
        parentWidget.notifyModelChanged();
        overlayTopPosition = overlayDiv.style.top;
    }

    protected void setOverlayDimensions(HTMLDivElement overlayDiv) {
        overlayDiv.style.maxWidth = CSSProperties.MaxWidthUnionType.of(OVERLAY_MAX_WIDTH);
        overlayDiv.style.minHeight = CSSProperties.MinHeightUnionType.of(OVERLAY_MIN_HEIGHT);
    }

    protected Element getLastElementChild(final HTMLDivElement overlayDiv) {
        return overlayDiv.lastElementChild;
    }

    protected HTMLDivElement getNextElementSibling() {
        return (HTMLDivElement) variableTagsSettings.nextElementSibling;
    }

    private void preventSpaces(KeyDownEvent event) {
        int iChar = event.getNativeKeyCode();
        if (iChar == ' ') {
            event.preventDefault();
        }
    }

    private static double parseDimension(final String dimension) {
        return (Double.parseDouble(dimension.replace("px", "")));
    }

    private void loadDefaultTagNames() {
        tagNamesList.addAll(setToList(VariablesEditorFieldRenderer.getDefaultTagsSet()));
    }

    private static List<String> setToList(Set<String> set) {
        return new ArrayList<>(set);
    }

    private void setTagsListItems() {
        ListBoxValues classNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                 "Edit" + " ",
                                                                 null);

        classNameListBoxValues.addValues(tagNamesList);
        tagNamesComboBox.setShowCustomValues(true);
        tagNamesComboBox.setListBoxValues(classNameListBoxValues);

        defaultTagNames.setValue("");

        tagNamesComboBox.init(this,
                              false,
                              defaultTagNames,
                              customTagName,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TAG_PROMPT);
    }

    private void setTagTittle(final String title) {
        variableTagsSettings.setAttribute("title", title);
        tagCount.setAttribute("title", title);
    }

    @Override
    public VariableRow getModel() {
        return variableRow.getModel();
    }

    @Override
    public void setModel(final VariableRow model) {
        variableRow.setModel(model);
        initVariableControls();
        currentValue = getModel().toString();
        currentName = getModel().getName();
    }

    @Override
    public VariableType getVariableType() {
        return getModel().getVariableType();
    }

    @Override
    public String getDataTypeDisplayName() {
        return getModel().getDataTypeDisplayName();
    }

    @Override
    public void setDataTypeDisplayName(final String dataTypeDisplayName) {
        getModel().setDataTypeDisplayName(dataTypeDisplayName);
    }

    @Override
    public String getCustomDataType() {
        return getModel().getCustomDataType();
    }

    @Override
    public void setCustomDataType(final String customDataType) {
        getModel().setCustomDataType(customDataType);
    }

    @Override
    public void setCustomTags(final List<String> tags) {
        getModel().setTags(tags);
        tagNamesList = tags;
    }

    @Override
    public List<String> getCustomTags() {
        return setToList(tagSet);
    }

    @Override
    public void setDataTypes(final ListBoxValues dataTypeListBoxValues) {
        dataTypeComboBox.setCurrentTextValue("");
        dataTypeComboBox.setListBoxValues(dataTypeListBoxValues);
        dataTypeComboBox.setShowCustomValues(true);

        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            dataTypeComboBox.addCustomValueToListBoxValues(cdt,
                                                           "");
        }
    }

    @Override
    public void setTagTypes(final List<String> tagTypes) {
        tagNamesComboBox.setCurrentTextValue("");
        tagNamesComboBox.setShowCustomValues(true);

        for (final String tag : tagTypes) {
            if (!VariablesEditorFieldRenderer.getDefaultTagsSet().contains(tag)) {
                tagNamesComboBox.addCustomValueToListBoxValues(tag, "");
            }
        }
        tagSet.clear();
        tagSet.addAll(tagTypes);
        renderTagElementsBadges();
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        dataTypeComboBox.setReadOnly(readOnly);
        name.setEnabled(!readOnly);
    }

    protected boolean isDuplicateID(final String id) {
        return parentWidget.isDuplicateID(id);
    }

    private boolean isDuplicateName(final String name) {
        return parentWidget.isDuplicateName(name);
    }

    private boolean isBoundToNodes(final String name) {
        return parentWidget.isBoundToNodes(name);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeVariable(getModel());
    }

    @EventHandler("closeButton")
    public void handleCloseButton(final ClickEvent e) {
        variableTagsSettings.dispatchEvent(new MouseEvent("click"));
    }

    @EventHandler("acceptButton")
    public void handleAcceptButton(final ClickEvent e) {

        final String tagAdded = tagNamesComboBox.getValue();

        if (!Strings.isNullOrEmpty(tagAdded) && !tagSet.contains(tagAdded)) {
            for (final HTMLAnchorElement closeAnchor : removeButtons.values()) {
                closeAnchor.onclick.onInvoke(new elemental2.dom.Event("DoNotUpdateModel"));
            }
            removeButtons.clear();

            tagSet.add(tagAdded);
            setCustomTags(setToList(tagSet));
            notifyModelChanged();

            if (!VariablesEditorFieldRenderer.getDefaultTagsSet().contains(tagAdded) && getPreviousCustomValue() != null && !tagAdded.equals(getPreviousCustomValue())) { // Is custom
                tagSet.remove(getPreviousCustomValue());
            }

            renderTagElementsBadges();
        }
    }

    protected void renderTagElementsBadges() {
        for (final String tag : tagSet) {

            final HTMLLabelElement tagLabel = getBadgeElement(tag);
            final HTMLAnchorElement badgeCloseButton = getBadgeCloseButton();

            badgeCloseButton.onclick = ex -> {
                handleBadgeCloseEvent(tag, tagLabel, badgeCloseButton, ex);
                return null;
            };

            tagLabel.appendChild(badgeCloseButton);
            tagsContainer.appendChild(tagLabel);
            updateTagCount();
            removeButtons.put(tag, badgeCloseButton);
        }
    }

    protected void handleBadgeCloseEvent(String tag, HTMLLabelElement tagLabel, HTMLAnchorElement badgeCloseButton, elemental2.dom.Event ex) {
        tagLabel.remove();
        badgeCloseButton.remove();
        ex.preventDefault();

        if (!ex.type.equals("DoNotUpdateModel")) {
            tagSet.remove(tag);
            setCustomTags(setToList(tagSet));
            notifyModelChanged();
        }

        updateTagCount();
        setCustomTags(setToList(tagSet));
    }

    protected HTMLAnchorElement getBadgeCloseButton() {
        final HTMLAnchorElement badgeCloseButton = (HTMLAnchorElement) document.createElement("a");
        badgeCloseButton.id = "closeButton";
        badgeCloseButton.textContent = "x";
        badgeCloseButton.className = "close tagCloseButton tagBadges";
        return badgeCloseButton;
    }

    protected HTMLLabelElement getBadgeElement(String tag) {
        final HTMLLabelElement tagLabel = (HTMLLabelElement) document.createElement("label");
        tagLabel.textContent = tag;
        tagLabel.className = "badge tagBadge  tagBadges";
        tagLabel.htmlFor = "closeButton";
        return tagLabel;
    }

    private void updateTagCount() {
        tagCount.textContent = !tagSet.isEmpty() ? String.valueOf(tagSet.size()) : "";
        setTagTittle("Tags: " + tagSet.toString());
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link VariableRow}.
     */
    private void initVariableControls() {
        deleteButton.setIcon(IconType.TRASH);
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            customDataType.setValue(cdt);
            dataType.setValue(cdt);
        } else if (getDataTypeDisplayName() != null) {
            dataType.setValue(getDataTypeDisplayName());
        }
    }

    @Override
    public void notifyModelChanged() {
        String oldValue = currentValue;
        currentValue = getModel().toString();
        currentName = getModel().getName();
        if (oldValue == null) {
            if (currentValue != null && currentValue.length() > 0) {
                parentWidget.notifyModelChanged();
            }
        } else if (!oldValue.equals(currentValue)) {
            parentWidget.notifyModelChanged();
        }
    }

    @Override
    public void setTagsNotEnabled() {
        this.tagsTD.removeFromParent();
    }

    @JsType(isNative = true)
    private abstract static class PopOver {

        @JsMethod(namespace = GLOBAL, name = "jQuery")
        public static native PopOver jQuery(final elemental2.dom.Node selector);

        public native void popovers();
    }
}
