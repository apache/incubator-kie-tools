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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assignmentsEditor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
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
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.uberfire.workbench.events.NotificationEvent;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable.VariableType.OUTPUT;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.isEmpty;
import static org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils.nonEmpty;

/**
 * A templated widget that will be used to display a row in a table of
 * {@link AssignmentRow}s.
 * <p>
 * The Name field of AssignmentRow is Bound, but other fields are not bound because
 * they use a combination of ListBox and TextBox to implement a drop-down combo
 * to hold the values.
 */
@Templated("ActivityDataIOEditorWidget.html#assignment")
public class AssignmentListItemWidgetViewImpl extends Composite implements AssignmentListItemWidgetView,
                                                                           ComboBoxView.ModelPresenter {

    private static final String EMPTY_VALUE = "";

    private static final String ALLOWED_CHARS = "^[a-zA-Z0-9\\-\\_\\ \\+\\/\\*\\?\\'\\.]*$";

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
    private String duplicateNameErrorMessage = EMPTY_VALUE;

    @DataField
    protected ValueListBox<String> dataType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : EMPTY_VALUE;
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

    @DataField
    protected ValueListBox<String> processVar = new ValueListBox<>(new Renderer<String>() {
        public String render(final String object) {
            return object != null ? object : EMPTY_VALUE;
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
    protected TextBox expression;

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

    private String oldType = null;

    @Override
    public void setTextBoxModelValue(final TextBox textBox,
                                     final String value) {
        if (textBox == customDataType) {
            parentWidget.addDataType(value, oldType);
            setCustomDataType(value);
            oldType = value;
        } else if (textBox == expression) {
            setExpression(value);
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
            return isEmpty(value) ? getDataType() : value;
        }

        if (listBox == processVar) {
            String value = getExpression();
            return isEmpty(value) ? getProcessVar() : value;
        }

        return EMPTY_VALUE;
    }

    @PostConstruct
    public void init() {
        name.setRegExp(ALLOWED_CHARS,
                       StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                       StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());
        name.addChangeHandler(event -> {
            String value = name.getText();
            String notifyMessage = null;
            if (isMultipleInstanceVariable(value)) {
                notifyMessage = StunnerFormsClientFieldsConstants.CONSTANTS.AssignmentNameAlreadyInUseAsMultipleInstanceInputOutputVariable(value);
            } else if (!allowDuplicateNames && isDuplicateName(value)) {
                notifyMessage = duplicateNameErrorMessage;
            }
            if (notifyMessage != null) {
                notification.fire(new NotificationEvent(notifyMessage, NotificationEvent.NotificationType.ERROR));
                name.setValue(EMPTY_VALUE);
                ValueChangeEvent.fire(name, EMPTY_VALUE);
            }
        });
        customDataType.setRegExp(StringUtils.ALPHA_NUM_UNDERSCORE_DOT_GT_LT_REGEXP,
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name(),
                                 StunnerFormsClientFieldsConstants.CONSTANTS.Unbalanced_GT_LT_from_name());
        customDataType.addKeyDownHandler(event -> {
            int iChar = event.getNativeKeyCode();
            if (iChar == ' ') {
                event.preventDefault();
            }
        });
        dataTypeComboBox.init(this,
                              false,
                              dataType,
                              customDataType,
                              false,
                              true,
                              CUSTOM_PROMPT,
                              ENTER_TYPE_PROMPT);
        processVarComboBox.init(this,
                                false,
                                processVar,
                                expression,
                                true,
                                true,
                                EXPRESSION_PROMPT,
                                ENTER_EXPRESSION_PROMPT);
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
    public String getExpression() {
        return getModel().getExpression();
    }

    @Override
    public void setExpression(final String expression) {
        getModel().setExpression(expression);
    }

    @Override
    public void setDataTypes(final ListBoxValues dataTypeListBoxValues) {
        dataTypeComboBox.setCurrentTextValue(EMPTY_VALUE);
        dataTypeComboBox.setListBoxValues(dataTypeListBoxValues);
        dataTypeComboBox.setShowCustomValues(true);
        String cdt = getCustomDataType();
        if (cdt != null && !cdt.isEmpty()) {
            dataTypeComboBox.addCustomValueToListBoxValues(cdt, EMPTY_VALUE);
        }
    }

    @Override
    public void setProcessVariables(final ListBoxValues processVarListBoxValues) {
        processVarComboBox.setCurrentTextValue(EMPTY_VALUE);
        ListBoxValues copyProcessVarListBoxValues = new ListBoxValues(processVarListBoxValues, false);
        processVarComboBox.setListBoxValues(copyProcessVarListBoxValues);
        String exp = getExpression();
        // processVar set here because the ListBoxValues must already have been set
        if (nonEmpty(exp)) {
            String displayValue = processVarComboBox.addCustomValueToListBoxValues(exp, EMPTY_VALUE);
            processVar.setValue(displayValue);
        }
    }

    @Override
    public void setShowExpressions(final boolean showExpressions) {
        processVarComboBox.setShowCustomValues(showExpressions);
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
    public boolean isMultipleInstanceVariable(final String name) {
        return parentWidget.isMultipleInstanceVariable(name);
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
        if (getVariableType() == OUTPUT) {
            expression.setVisible(false);
        }
        String cdt = getCustomDataType();
        if (nonEmpty(cdt)) {
            customDataType.setValue(cdt);
            dataType.setValue(cdt);
        } else if (getDataType() != null) {
            dataType.setValue(getDataType());
        }
        String exp = getExpression();
        if (nonEmpty(exp)) {
            // processVar ListBox is set in setProcessVariables because its ListBoxValues are required
            expression.setValue(exp);
        } else if (getProcessVar() != null) {
            processVar.setValue(getProcessVar());
        }
    }

    public void notifyModelChanged() {
        // Ignore
    }
}
