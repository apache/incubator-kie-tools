/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.datamodeller.client.widgets.jpadomain.properties;

import javax.enterprise.context.Dependent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class SequenceGeneratorEditionDialogViewImpl
        extends BaseModal
        implements SequenceGeneratorEditionDialog.View {

    @UiField
    TextBox generatorName;

    @UiField
    TextBox sequenceName;

    @UiField
    FormGroup initialValueGroup;

    @UiField
    TextBox initialValue;

    @UiField
    HelpBlock initialValueError;

    @UiField
    FormGroup allocationSizeGroup;

    @UiField
    TextBox allocationSize;

    @UiField
    HelpBlock allocationSizeError;

    private ModalFooterOKCancelButtons footer;

    private SequenceGeneratorEditionDialog presenter;

    interface Binder extends UiBinder<Widget, SequenceGeneratorEditionDialogViewImpl> {

    }

    private static SequenceGeneratorEditionDialogViewImpl.Binder uiBinder = GWT.create(SequenceGeneratorEditionDialogViewImpl.Binder.class);

    public SequenceGeneratorEditionDialogViewImpl() {
        setTitle(Constants.INSTANCE.persistence_domain_relationship_sequence_generator_dialog_title());
        add(new ModalBody() {{
            add(uiBinder.createAndBindUi(SequenceGeneratorEditionDialogViewImpl.this));
        }});

        footer = new ModalFooterOKCancelButtons(
                () -> presenter.onOK(),
                () -> presenter.onCancel()
        );
        add(footer);

        initialValue.addValueChangeHandler(event -> presenter.onInitialValueChange());
        allocationSize.addValueChangeHandler(event -> presenter.onAllocationSizeChange());
    }

    @Override
    public void init(SequenceGeneratorEditionDialog presenter) {
        this.presenter = presenter;
    }

    @Override
    public String getGeneratorName() {
        return generatorName.getText();
    }

    @Override
    public void setGeneratorName(String generatorName) {
        this.generatorName.setText(generatorName);
    }

    @Override
    public String getSequenceName() {
        return sequenceName.getText();
    }

    @Override
    public void setSequenceName(String sequenceName) {
        this.sequenceName.setText(sequenceName);
    }

    @Override
    public String getInitialValue() {
        return initialValue.getText();
    }

    @Override
    public void setInitialValue(String initialValue) {
        this.initialValue.setText(initialValue);
    }

    @Override
    public void setInitialValueError(String error) {
        initialValueGroup.setValidationState(ValidationState.ERROR);
        initialValueError.setError(error);
    }

    @Override
    public void clearInitialValueError() {
        initialValueGroup.setValidationState(ValidationState.NONE);
        initialValueError.clearError();
    }

    @Override
    public String getAllocationSize() {
        return allocationSize.getText();
    }

    @Override
    public void setAllocationSize(String allocationSize) {
        this.allocationSize.setText(allocationSize);
    }

    @Override
    public void setAllocationSizeError(String error) {
        allocationSizeGroup.setValidationState(ValidationState.ERROR);
        allocationSizeError.setError(error);
    }

    @Override
    public void clearAllocationSizeError() {
        allocationSizeGroup.setValidationState(ValidationState.NONE);
        allocationSizeError.clearError();
    }

    @Override
    public void setEnabled(boolean enabled) {
        sequenceName.setEnabled(enabled);
        generatorName.setEnabled(enabled);
        initialValue.setEnabled(enabled);
        allocationSize.setEnabled(enabled);
        footer.enableOkButton(enabled);
    }

    @Override
    public void enableOkAction(boolean enabled) {
        footer.enableOkButton(enabled);
    }
}