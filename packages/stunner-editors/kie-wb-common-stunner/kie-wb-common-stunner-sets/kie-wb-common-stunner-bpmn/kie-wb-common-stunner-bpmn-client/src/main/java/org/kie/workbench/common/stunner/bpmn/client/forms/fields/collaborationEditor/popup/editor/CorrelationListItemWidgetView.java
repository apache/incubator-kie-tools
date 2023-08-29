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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.collaborationEditor.popup.editor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Input;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.html.Span;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.widget.HasModel;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor.VariableListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.kie.workbench.common.stunner.bpmn.definition.property.collaboration.Correlation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

@Templated("CorrelationsEditorWidget.html#correlation")
public class CorrelationListItemWidgetView
        extends Composite
        implements HasModel<Correlation>,
                   ComboBoxView.ModelPresenter {

    protected static final String CUSTOM_PROMPT = "Custom" + ListBoxValues.EDIT_SUFFIX;
    protected static final String ENTER_TYPE_PROMPT = "Enter type" + ListBoxValues.EDIT_SUFFIX;
    protected static final String EMPTY_STRING = "";

    @Inject
    @AutoBound
    protected DataBinder<Correlation> correlationDataBinder;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox id;

    @Inject
    @DataField
    protected Span idError;

    @Inject
    @Bound
    @DataField
    protected Input name;

    @Inject
    @DataField
    protected Span nameError;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox propertyId;

    @Inject
    @DataField
    protected Span propertyIdError;

    @Inject
    @Bound
    @DataField
    protected Input propertyName;

    @Inject
    @DataField
    protected Span propertyNameError;

    @DataField
    protected ValueListBox<String> propertyType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : "";
        }

        public void render(final String object,
                           final Appendable appendable) throws IOException {
            String s = render(object);
            appendable.append(s);
        }
    });

    @Inject
    @DataField
    protected CustomDataTypeTextBox customPropertyType;

    @Inject
    protected ComboBox typesComboBox;

    @Inject
    @DataField
    protected Span propertyTypeError;

    @Inject
    @DataField
    protected Button deleteButton;

    @Inject
    protected ClientTranslationService translationService;

    private CorrelationsEditorWidget parentWidget;

    @PostConstruct
    public void init() {
        id.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                     StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                     StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());

        propertyId.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                             StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                             StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());

    }

    @Override
    public Correlation getModel() {
        return correlationDataBinder.getModel();
    }

    @Override
    public void setModel(final Correlation model) {
        correlationDataBinder.setModel(model);
        initControls();
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox, final String value) {
        if (value != null && !value.isEmpty()) {
            getModel().setPropertyType(value);
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox, final String displayName) {
        String value = parentWidget.getPropertyType(displayName);
        getModel().setPropertyType(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        return getModel().getPropertyType();
    }

    @Override
    public void notifyModelChanged() {
        parentWidget.addDataTypes();
        ValueChangeEvent.fire(parentWidget, parentWidget.getCorrelations());
    }

    public void setParentWidget(final CorrelationsEditorWidgetView.Presenter parentWidget) {
        if (this.parentWidget != parentWidget) {
            this.parentWidget = (CorrelationsEditorWidget) parentWidget;

            initPropertyType();
            initControls();
        }
    }

    public String getId() {
        return getModel().getId();
    }

    public void setId(String id) {
        getModel().setId(id);
    }

    public String getName() {
        return getModel().getName();
    }

    public void setName(String name) {
        getModel().setName(name);
    }

    public String getPropertyId() {
        return getModel().getPropertyId();
    }

    public void setPropertyId(String propertyId) {
        getModel().setPropertyId(propertyId);
    }

    public String getPropertyName() {
        return getModel().getPropertyName();
    }

    public void setPropertyName(String propertyName) {
        getModel().setPropertyName(propertyName);
    }

    public String getPropertyType() {
        return getModel().getPropertyType();
    }

    public void setPropertyType(String propertyType) {
        getModel().setPropertyType(propertyType);
    }

    public void update(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        setIDStyle(correlationsEditorValidationItem, showErrors);
        setNameStyle(correlationsEditorValidationItem, showErrors);
        setPropertyIDStyle(correlationsEditorValidationItem, showErrors);
        setPropertyNameStyle(correlationsEditorValidationItem, showErrors);
        setPropertyTypeStyle(correlationsEditorValidationItem, showErrors);
    }

    public void syncIDName(String name) {
        setName(name);
    }

    private void initControls() {
        deleteButton.setIcon(IconType.TRASH);
        id.setText(getId());
        name.setText(getName());
        propertyId.setText(getPropertyId());
        propertyName.setText(getPropertyName());
        propertyType.setValue(getPropertyType());

        customPropertyType.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });
    }

    private void initPropertyType() {
        Map<String, String> propertyTypes = parentWidget.getPropertyTypes();
        ListBoxValues typeNameListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT,
                                                                "Edit" + " ",
                                                                null);
        List<String> displayNames = new ArrayList<>(propertyTypes.values());
        typeNameListBoxValues.addValues(displayNames);
        typesComboBox.setListBoxValues(typeNameListBoxValues);

        String propertyTypeName = getModel().getPropertyType();
        if (propertyTypeName == null || propertyTypeName.isEmpty()) {
            propertyTypeName = Object.class.getSimpleName();
        }

        String displayName = parentWidget.getPropertyType(propertyTypeName);

        if ((propertyTypeName.equals(displayName))) {
            displayName = parentWidget.getPropertyTypes().get(propertyTypeName);
        }

        propertyType.setValue(displayName);
        typesComboBox.setShowCustomValues(true);
        typesComboBox.init(this,
                           true,
                           this.propertyType,
                           customPropertyType,
                           false,
                           true,
                           CUSTOM_PROMPT,
                           ENTER_TYPE_PROMPT);
    }

    private void setIDStyle(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        idError.setVisible(showErrors);

        if (correlationsEditorValidationItem.isEmptyID()) {
            id.getElement().getParentElement().addClassName("has-error");

            String emptyID = translationService.getValue(StunnerBPMNConstants.CORRELATION_ID_EMPTY_ERROR);
            idError.setText(emptyID);
        } else {
            id.getElement().getParentElement().removeClassName("has-error");
            idError.setText(EMPTY_STRING);
        }
    }

    private void setNameStyle(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        nameError.setVisible(showErrors);

        if (correlationsEditorValidationItem.isEmptyName() ||
                correlationsEditorValidationItem.isDivergingName()) {
            name.getElement().getParentElement().addClassName("has-error");
        } else {
            name.getElement().getParentElement().removeClassName("has-error");
        }

        name.setEnabled(!correlationsEditorValidationItem.isDuplicateID() ||
                                correlationsEditorValidationItem.isDivergingName() ||
                                correlationsEditorValidationItem.isEmptyName());

        String message = EMPTY_STRING;
        if (correlationsEditorValidationItem.isEmptyName()) {
            message += translationService.getNotNullValue(StunnerBPMNConstants.CORRELATION_NAME_EMPTY_ERROR);
        } else if (correlationsEditorValidationItem.isDivergingName()) {
            message += translationService.getNotNullValue(StunnerBPMNConstants.CORRELATION_NAME_DIVERGING_ERROR);
        }

        nameError.setText(message);
    }

    private void setPropertyIDStyle(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        propertyIdError.setVisible(showErrors);

        if (correlationsEditorValidationItem.isEmptyPropertyID() ||
                correlationsEditorValidationItem.isDuplicatePropertyID()) {
            propertyId.getElement().getParentElement().addClassName("has-error");
        } else {
            propertyId.getElement().getParentElement().removeClassName("has-error");
        }

        String message = EMPTY_STRING;
        if (correlationsEditorValidationItem.isEmptyPropertyID()) {
            message += translationService.getNotNullValue(StunnerBPMNConstants.CORRELATION_PROPERTY_ID_EMPTY_ERROR);
        } else if (correlationsEditorValidationItem.isDuplicatePropertyID()) {
            if (!message.isEmpty()) {
                message += "\r\n";
            }

            message += translationService.getNotNullValue(StunnerBPMNConstants.CORRELATION_PROPERTY_ID_DUPLICATE_ERROR);
        }

        propertyIdError.setText(message);
    }

    private void setPropertyNameStyle(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        propertyNameError.setVisible(showErrors);

        if (correlationsEditorValidationItem.isEmptyPropertyName()) {
            propertyName.getElement().getParentElement().addClassName("has-error");

            String emptyID = translationService.getValue(StunnerBPMNConstants.CORRELATION_PROPERTY_NAME_EMPTY_ERROR);
            propertyNameError.setText(emptyID);
        } else {
            propertyName.getElement().getParentElement().removeClassName("has-error");
            propertyNameError.setText(EMPTY_STRING);
        }
    }

    private void setPropertyTypeStyle(CorrelationsEditorValidationItem correlationsEditorValidationItem, boolean showErrors) {
        propertyTypeError.setVisible(showErrors);

        if (correlationsEditorValidationItem.isEmptyPropertyType()) {
            propertyType.getElement().getParentElement().addClassName("has-error");

            String emptyID = translationService.getValue(StunnerBPMNConstants.CORRELATION_PROPERTY_TYPE_EMPTY_ERROR);
            propertyTypeError.setText(emptyID);
        } else {
            propertyType.getElement().getParentElement().removeClassName("has-error");
            propertyTypeError.setText(EMPTY_STRING);
        }
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeCorrelation(getModel());
    }
}