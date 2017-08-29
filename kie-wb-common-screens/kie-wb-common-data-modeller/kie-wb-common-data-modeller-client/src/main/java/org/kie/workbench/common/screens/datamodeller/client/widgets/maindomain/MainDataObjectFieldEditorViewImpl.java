/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datamodeller.client.widgets.maindomain;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.CheckBox;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.FormLabel;
import org.gwtbootstrap3.client.ui.TextArea;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.uberfire.commons.data.Pair;

@Dependent
public class MainDataObjectFieldEditorViewImpl
        extends MainEditorAbstractView<MainDataObjectFieldEditorView.Presenter>
        implements MainDataObjectFieldEditorView {

    interface DataObjectFieldEditorUIBinder
            extends UiBinder<Widget, MainDataObjectFieldEditorViewImpl> {

    }

    private static DataObjectFieldEditorUIBinder uiBinder = GWT.create(DataObjectFieldEditorUIBinder.class);

    @UiField
    FormLabel nameLabel;

    @UiField
    FormGroup nameFormGroup;

    @UiField
    TextBox name;

    @UiField
    TextBox label;

    @UiField
    TextArea description;

    @UiField
    Select typeSelector;

    @UiField
    CheckBox isTypeMultiple;

    public MainDataObjectFieldEditorViewImpl() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @PostConstruct
    protected void init() {
        typeSelector.addValueChangeHandler(e -> presenter.onTypeChange());

        isTypeMultiple.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.onTypeMultipleChange();
            }
        });

        name.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onNameChange();
            }
        });

        label.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onLabelChange();
            }
        });

        description.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.onDescriptionChange();
            }
        });

        setReadonly(true);
    }

    @Override
    public String getName() {
        return name.getText();
    }

    @Override
    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setNameOnError(boolean onError) {
        nameFormGroup.setValidationState(onError ? ValidationState.ERROR : ValidationState.NONE);
    }

    @Override
    public void selectAllNameText() {
        name.selectAll();
    }

    @Override
    public String getLabel() {
        return label.getText();
    }

    @Override
    public void setLabel(String label) {
        this.label.setText(label);
    }

    @Override
    public String getDescription() {
        return description.getText();
    }

    @Override
    public void setDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public String getType() {
        return typeSelector.getValue();
    }

    @Override
    public void setType(String type) {
        UIUtil.setSelectedValue(typeSelector,
                                type);
    }

    @Override
    public boolean getMultipleType() {
        return isTypeMultiple.getValue();
    }

    @Override
    public void setMultipleType(boolean multipleType) {
        this.isTypeMultiple.setValue(multipleType);
    }

    @Override
    public void setMultipleTypeEnabled(boolean enabled) {
        isTypeMultiple.setEnabled(enabled);
    }

    @Override
    public void setReadonly(boolean readonly) {
        boolean value = !readonly;

        name.setEnabled(value);
        label.setEnabled(value);
        description.setEnabled(value);
        typeSelector.setEnabled(value);
        UIUtil.refreshSelect(typeSelector);
        isTypeMultiple.setEnabled(value);
    }

    @Override
    public void initTypeList(List<Pair<String, String>> options,
                             String selectedValue,
                             boolean includeEmptyItem) {
        UIUtil.initList(typeSelector,
                        options,
                        selectedValue,
                        includeEmptyItem);
    }
}