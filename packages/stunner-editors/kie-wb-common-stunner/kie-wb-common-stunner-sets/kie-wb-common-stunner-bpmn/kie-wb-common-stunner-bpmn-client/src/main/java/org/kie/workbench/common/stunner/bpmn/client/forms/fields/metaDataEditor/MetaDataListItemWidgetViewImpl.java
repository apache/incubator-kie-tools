/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import io.crysknife.client.IsElement;
import io.crysknife.ui.databinding.client.api.AutoBound;
import io.crysknife.ui.databinding.client.api.Bound;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.templates.client.annotation.DataField;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.Templated;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.event.logical.shared.ValueChangeEvent;
import org.kie.workbench.common.stunner.bpmn.client.StunnerSpecific;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerFormsClientFieldsConstants;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.StringUtils;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.AttributeValueTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;

@Dependent
@Templated("MetaDataListItemWidgetViewImpl.html#metaDataRow")
public class MetaDataListItemWidgetViewImpl implements MetaDataListItemWidgetView,
                                                       IsElement {

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
        if (Objects.isNull(currentName)) {
            currentValue = model.getValue();
            currentName = model.getAttribute();
        }
        initVariableControls();
        metaDataRow.setModel(model);

    }

    @Override
    public void setValue(MetaDataRow value) {
        setModel(value);
    }

    @Override
    public MetaDataRow getValue() {
        return getModel();
    }

    @Override
    public void setValue(final String value) {
        getModel().setValue(value);

        initVariableControls();

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