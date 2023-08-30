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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.shared.api.annotations.AutoBound;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.AttributeValueTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;

@Templated("MetaDataEditorWidget.html#metaDataRow")
public class MetaDataListItemWidgetViewImpl implements MetaDataListItemWidgetView {

    @Inject
    @AutoBound
    protected DataBinder<MetaDataRow> metaDataRow;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected VariableNameTextBox attribute;

    @Inject
    @Bound
    @DataField
    @StunnerSpecific
    protected AttributeValueTextBox value;

    private String currentValue;
    private String currentName;

    @Inject
    @DataField
    protected Button deleteButton;

    private MetaDataEditorWidgetView.Presenter parentWidget;

    public void setParentWidget(final MetaDataEditorWidgetView.Presenter parentWidget) {
        this.parentWidget = parentWidget;
    }

    @PostConstruct
    public void init() {
        attribute.setRegExp(StringUtils.ALPHA_NUM_REGEXP,
                            StunnerFormsClientFieldsConstants.CONSTANTS.Removed_invalid_characters_from_name(),
                            StunnerFormsClientFieldsConstants.CONSTANTS.Invalid_character_in_name());

        attribute.addChangeHandler(event -> {
            String attributeValue = attribute.getText();
            if (isDuplicateName(attributeValue)) {
                parentWidget.showErrorMessage(StunnerFormsClientFieldsConstants.CONSTANTS.DuplicatedAttributeNameError(attributeValue));
                attribute.setValue(currentName);
                ValueChangeEvent.fire(attribute, currentName);
            }

            notifyModelChanged();
        });

        value.addChangeHandler(event -> notifyModelChanged());
    }

    @Override
    public MetaDataRow getModel() {
        return metaDataRow.getModel();
    }

    @Override
    public void setModel(final MetaDataRow model) {
        metaDataRow.setModel(model);
        initVariableControls();
        currentValue = getModel().toString();
        currentName = getModel().getAttribute();
    }

    @Override
    public String getValue() {
        return getModel().getValue();
    }

    @Override
    public void setValue(final String value) {
        getModel().setValue(value);
    }

    @Override
    public void setReadOnly(final boolean readOnly) {
        deleteButton.setEnabled(!readOnly);
        value.setReadOnly(readOnly);
        attribute.setEnabled(!readOnly);
    }

    private boolean isDuplicateName(final String attribute) {
        return parentWidget.isDuplicateAttribute(attribute);
    }

    @EventHandler("deleteButton")
    public void handleDeleteButton(final ClickEvent e) {
        parentWidget.removeMetaData(getModel());
    }

    private void initVariableControls() {
        deleteButton.setIcon(IconType.TRASH);
    }

    @Override
    public void notifyModelChanged() {
        String oldValue = currentValue;
        currentValue = getModel().toString();
        currentName = getModel().getAttribute();
        if (oldValue == null || !oldValue.equals(currentValue)) {
            parentWidget.notifyModelChanged();
        }
    }
}