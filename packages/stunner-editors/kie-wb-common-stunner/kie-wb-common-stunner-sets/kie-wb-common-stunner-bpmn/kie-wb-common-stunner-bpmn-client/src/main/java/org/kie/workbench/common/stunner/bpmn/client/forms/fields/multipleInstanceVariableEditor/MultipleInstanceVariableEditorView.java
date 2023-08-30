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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.google.gwt.text.shared.Renderer;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.ValueListBox;
import org.jboss.errai.common.client.dom.Event;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.forms.DataTypeNamesService;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor.VariableListItemWidgetView;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ListBoxValues;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.ComboBoxView;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.CustomDataTypeTextBox;

import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getDataType;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getDisplayName;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getFirstIfExistsOrSecond;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getListObjectThenOnFulfilledCallbackFn;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getNonNullName;
import static org.kie.workbench.common.stunner.bpmn.client.forms.fields.multipleInstanceVariableEditor.MultipleInstanceVariableEditorPresenter.getRealType;

@Templated
public class MultipleInstanceVariableEditorView
        implements IsElement,
                   MultipleInstanceVariableEditorPresenter.View,
                   ComboBoxView.ModelPresenter {

    @Inject
    @DataField("variableName")
    private TextInput variableName;

    protected Variable variable;

    @Inject
    protected ComboBox dataTypeComboBox;

    protected static Set<ComboBox> comboBoxes = new HashSet<ComboBox>();

    @Inject
    protected DataTypeNamesService clientDataTypesService;

    @Inject
    @DataField
    protected CustomDataTypeTextBox customDataType;

    @DataField
    private ValueListBox<String> dataType = new ValueListBox<>(new Renderer<String>() {
        public String render(final String value) {
            return getNonNullName(value);
        }

        public void render(final String value,
                           final Appendable appendable) throws IOException {
            String s = render(value);
            appendable.append(s);
        }
    });

    private MultipleInstanceVariableEditorPresenter presenter;

    @Override
    public void init(MultipleInstanceVariableEditorPresenter presenter) {
        this.presenter = presenter;

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

        ListBoxValues dataTypeListBoxValues = new ListBoxValues(VariableListItemWidgetView.CUSTOM_PROMPT, "Edit ", null);
        clientDataTypesService
                .call(presenter.getDiagramPath())
                .then(getListObjectThenOnFulfilledCallbackFn(presenter.getSimpleDataTypes(), dataTypeListBoxValues))
                .catch_(exception -> {
                    dataTypeListBoxValues.addValues(presenter.getSimpleDataTypes());
                    return null;
                });

        dataTypeComboBox.setCurrentTextValue("");
        dataTypeComboBox.setListBoxValues(dataTypeListBoxValues);
        dataTypeComboBox.setShowCustomValues(true);
        comboBoxes.add(dataTypeComboBox);
    }

    @Override
    public void setVariableName(String variableName) {
        this.variableName.setValue(getNonNullName(variableName));
    }

    @Override
    public void setVariableType(String variableType) {
        this.dataType.setValue(variableType);
    }

    @Override
    public String getVariableName() {
        return variableName.getValue();
    }

    @Override
    public String getVariableType() {
        return getDisplayName(getFirstIfExistsOrSecond(getCustomDataType(), dataType.getValue()));
    }

    @Override
    public String getCustomDataType() {
        return customDataType.getValue();
    }

    @Override
    public String getDataTypeDisplayName() {
        return getModel().getDataType();
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        variableName.setDisabled(readOnly);
        dataType.setEnabled(!readOnly);
    }

    @EventHandler
    private void onVariableChange(@ForEvent("change") final Event event) {
        presenter.onVariableChange();
    }

    @Override
    public void setTextBoxModelValue(TextBox textBox, String value) {
        if (value != null && !value.isEmpty()) {
            clientDataTypesService.add(value, null);
            final ListBoxValues listBoxValues = dataTypeComboBox.getListBoxValues();
            comboBoxes.forEach(item -> {
                item.setListBoxValues(listBoxValues);
            });
        }
    }

    @Override
    public void setListBoxModelValue(ValueListBox<String> listBox, String value) {
    }

    @Override
    public String getModelValue(ValueListBox<String> listBox) {
        return getFirstIfExistsOrSecond(getCustomDataType(), getDataTypeDisplayName());
    }

    @Override
    public void notifyModelChanged() {
    }

    @Override
    public Variable getModel() {
        return variable;
    }

    @Override
    public void setModel(Variable variable) {
        this.variable = variable;
        initVariableControls(variable);
    }

    /**
     * Updates the display of this variable according to the state of the
     * corresponding {@link Variable}.
     */
    private void initVariableControls(Variable variable) {
        setVariableName(variable.getName());
        String cdt = getRealType(variable.getCustomDataType());
        String dt = getRealType(getDataType(variable));
        customDataType.setValue(cdt);
        dataType.setValue(dt);
    }
}
