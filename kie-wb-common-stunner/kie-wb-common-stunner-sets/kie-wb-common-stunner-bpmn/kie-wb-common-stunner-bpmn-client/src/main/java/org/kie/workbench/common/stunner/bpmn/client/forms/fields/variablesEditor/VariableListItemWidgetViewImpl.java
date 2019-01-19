/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.uberfire.client.workbench.widgets.common.ErrorPopupPresenter;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link VariableRow}s.
 * <p/>
 * The Name field of VariableRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated("VariablesEditorWidget.html#variableRow")
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

    @Inject
    protected ErrorPopupPresenter errorPopupPresenter;

    private String currentValue;
    private String currentName;

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
    protected TextBox customDataType;

    @Inject
    protected ComboBox dataTypeComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected Button deleteButton;

    /**
     * Required for implementation of Delete button.
     */
    private VariablesEditorWidgetView.Presenter parentWidget;

    public void setParentWidget(final VariablesEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        setCustomDataType(value);
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        setDataTypeDisplayName(value);
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        String value = getCustomDataType();
        if (value == null || value.isEmpty()) {
            value = getDataTypeDisplayName();
        }
        return value;
    }

    @PostConstruct
    public void init() {
        // Configure dataType and customDataType controls
        dataTypeComboBox.init(this,
                              true,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
        name.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                       StunnerFormsClientFieldsConstants.INSTANCE.Removed_invalid_characters_from_name(),
                       StunnerFormsClientFieldsConstants.INSTANCE.Invalid_character_in_name());
        customDataType.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });
        name.addChangeHandler(event -> {
            String value = name.getText();
            if (isDuplicateName(value)) {
                notification.fire(new NotificationEvent(StunnerFormsClientFieldsConstants.INSTANCE.DuplicatedVariableNameError(value),
                                                        NotificationEvent.NotificationType.ERROR));
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            } else if (isBoundToNodes(currentName)) {
                errorPopupPresenter.showMessage(StunnerFormsClientFieldsConstants.INSTANCE.RenameDiagramVariableError());
                name.setValue(currentName);
                ValueChangeEvent.fire(name, currentName);
            }
            notifyModelChanged();
        });
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
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        dataTypeComboBox.setReadOnly(readOnly);
        name.setEnabled(!readOnly);
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
}
