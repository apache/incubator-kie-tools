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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Composite;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.AssignmentRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.uberfire.workbench.events.NotificationEvent;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link AssignmentRow}s.
 * <p/>
 * The Name field of AssignmentRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated("ActivityDataIOEditorWidget.html#assignment")
public class AssignmentListItemWidgetViewImpl extends Composite implements AssignmentListItemWidgetView,
                                                                           ComboBoxView.ModelPresenter {

    /**
     * Errai's data binding module will automatically bind the provided instance
     * of the model (see {@link #setModel(AssignmentRow)}) to all fields annotated
     * with {@link Bound}. If not specified otherwise, the bindings occur based on
     * matching field names (e.g. assignment.name will automatically be kept in
     * sync with the data-field "name")
     */
    @Inject
    @AutoBound
    protected DataBinder<AssignmentRow> assignment;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox name;

    private boolean allowDuplicateNames = true;
    private String duplicateNameErrorMessage = "";

    @DataField
    protected ValueListBox<String> dataType = new ValueListBox<String>(new Renderer<String>() {
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

    @DataField
    protected ValueListBox<String> processVar = new ValueListBox<String>(new Renderer<String>() {
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
    protected ComboBox dataTypeComboBox;

    @Inject
    ComboBox processVarComboBox;

    @Inject
    protected Event<NotificationEvent> notification;

    @Inject
    @DataField
    protected TextBox constant;

    @Inject
    @DataField
    protected Button deleteButton;

    /**
     * Widget the current assignment is in.
     * Required for implementation of Delete button.
     */
    private ActivityDataIOEditorWidget parentWidget;

    public void setParentWidget(final ActivityDataIOEditorWidget parentWidget) {
        this.parentWidget = parentWidget;
    }

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        if (textBox == customDataType) {
            setCustomDataType(value);
        } else if (textBox == constant) {
            setConstant(value);
        }
    }

    @Override
    public void setListBoxModelValue(final ValueListBox<String> listBox,
                                     final String value) {
        if (listBox == dataType) {
            setDataType(value);
        } else if (listBox == processVar) {
            setProcessVar(value);
        }
    }

    @Override
    public String getModelValue(final ValueListBox<String> listBox) {
        if (listBox == dataType) {
            String value = getCustomDataType();
            if (value == null || value.isEmpty()) {
                value = getDataType();
            }
            return value;
        } else if (listBox == processVar) {
            String value = getConstant();
            if (value == null || value.isEmpty()) {
                value = getProcessVar();
            }
            return value;
        } else {
            return "";
        }
    }

    @PostConstruct
    public void init() {
        // Configure dataType and customDataType controls
        dataTypeComboBox.init(this,
                              false,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
        // Configure processVar and constant controls
        processVarComboBox.init(this,
                                false,
                                processVar,
                                constant,
                                true,
                                true,
                                CONSTANT_PROMPT,
                                ENTER_CONSTANT_PROMPT);
        name.setRegExp("^[a-zA-Z0-9\\-\\.\\_]*$",
                       StunnerFormsClientFieldsConstants.INSTANCE.Removed_invalid_characters_from_name(),
                       StunnerFormsClientFieldsConstants.INSTANCE.Invalid_character_in_name());
        customDataType.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                int iChar = event.getNativeKeyCode();
                if (iChar == ' ') {
                    event.preventDefault();
                }
            }
        });
        name.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                if (!allowDuplicateNames) {
                    String value = name.getText();
                    if (isDuplicateName(value)) {
                        notification.fire(new NotificationEvent(duplicateNameErrorMessage,
                                                                NotificationEvent.NotificationType.ERROR));
                        name.setValue("");
                        ValueChangeEvent.fire(name,
                                              "");
                    }
                }
            }
        });
    }

    @Override
    public AssignmentRow getModel() {
        return assignment.getModel();
    }

    @Override
    public void setModel(final AssignmentRow model) {
        assignment.setModel(model);
        initAssignmentControls();
    }

    @Override
    public VariableType getVariableType() {
        return getModel().getVariableType();
    }

    @Override
    public String getDataType() {
        return getModel().getDataType();
    }

    @Override
    public void setDataType(final String dataType) {
        getModel().setDataType(dataType);
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
    public String getProcessVar() {
        return getModel().getProcessVar();
    }

    @Override
    public void setProcessVar(final String processVar) {
        getModel().setProcessVar(processVar);
    }

    @Override
    public String getConstant() {
        return getModel().getConstant();
    }

    @Override
    public void setConstant(final String constant) {
        getModel().setConstant(constant);
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
    public void setProcessVariables(final ListBoxValues processVarListBoxValues) {
        processVarComboBox.setCurrentTextValue("");
        ListBoxValues copyProcessVarListBoxValues = new ListBoxValues(processVarListBoxValues,
                                                                      false);
        processVarComboBox.setListBoxValues(copyProcessVarListBoxValues);
        String con = getConstant();
        // processVar set here because the ListBoxValues must already have been set
        if (con != null && !con.isEmpty()) {
            String displayValue = processVarComboBox.addCustomValueToListBoxValues(con,
                                                                                   "");
            processVar.setValue(displayValue);
        }
    }

    @Override
    public void setShowConstants(final boolean showConstants) {
        processVarComboBox.setShowCustomValues(showConstants);
    }

    @Override
    public void setDisallowedNames(final Set<String> disallowedNames,
                                   final String disallowedNameErrorMessage) {
        name.setInvalidValues(disallowedNames,
                              false,
                              disallowedNameErrorMessage);
    }

    @Override
    public void setAllowDuplicateNames(final boolean allowDuplicateNames,
                                       final String duplicateNameErrorMessage) {
        this.allowDuplicateNames = allowDuplicateNames;
        this.duplicateNameErrorMessage = duplicateNameErrorMessage;
    }

    @Override
    public boolean isDuplicateName(final String name) {
        return parentWidget.isDuplicateName(name);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        name.setReadOnly(readOnly);
        dataType.setEnabled(!readOnly);
        processVar.setEnabled(!readOnly);
        deleteButton.setEnabled(!readOnly);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeAssignment(getModel());
    }

    /**
     * Updates the display of this row according to the state of the
     * corresponding {@link AssignmentRow}.
     */
    private void initAssignmentControls() {
        deleteButton.setIcon(IconType.TRASH);
        if (getVariableType() == VariableType.OUTPUT) {
            constant.setVisible(false);
        }
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            customDataType.setValue(cdt);
            dataType.setValue(cdt);
        } else if (getDataType() != null) {
            dataType.setValue(getDataType());
        }
        String con = getConstant();
        if (con != null && !con.isEmpty()) {
            // processVar ListBox is set in setProcessVariables because its ListBoxValues are required
            constant.setValue(con);
        } else if (getProcessVar() != null) {
            processVar.setValue(getProcessVar());
        }
    }

    public void notifyModelChanged() {
        // Ignore
    }
}
