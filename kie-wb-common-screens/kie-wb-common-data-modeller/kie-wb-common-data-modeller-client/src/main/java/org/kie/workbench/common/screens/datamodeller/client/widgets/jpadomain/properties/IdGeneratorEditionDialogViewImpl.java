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
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.extras.select.client.ui.Select;
import org.kie.workbench.common.screens.datamodeller.client.resources.i18n.Constants;
import org.kie.workbench.common.screens.datamodeller.client.util.UIUtil;
import org.kie.workbench.common.screens.datamodeller.model.jpadomain.GenerationType;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Dependent
public class IdGeneratorEditionDialogViewImpl
        extends BaseModal
        implements IdGeneratorEditionDialog.View {

    @UiField
    Select generatorType;

    @UiField
    TextBox generatorName;

    private ModalFooterOKCancelButtons footer;

    IdGeneratorEditionDialog presenter;

    interface Binder extends UiBinder<Widget, IdGeneratorEditionDialogViewImpl> {

    }

    private static IdGeneratorEditionDialogViewImpl.Binder uiBinder = GWT.create(IdGeneratorEditionDialogViewImpl.Binder.class);

    public IdGeneratorEditionDialogViewImpl() {
        setTitle(Constants.INSTANCE.persistence_domain_id_generator_dialog_title());
        setBody(uiBinder.createAndBindUi(IdGeneratorEditionDialogViewImpl.this));

        footer = new ModalFooterOKCancelButtons(
                () -> presenter.onOK(),
                () -> presenter.onCancel()
        );
        add(footer);

        generatorType.add(UIUtil.newOption(Constants.INSTANCE.persistence_domain_id_generator_dialog_not_configured_option_label(), UIUtil.NOT_SELECTED));
        generatorType.add(UIUtil.newOption(GenerationType.SEQUENCE.name(), GenerationType.SEQUENCE.name()));
        generatorType.add(UIUtil.newOption(GenerationType.TABLE.name(), GenerationType.TABLE.name()));
        generatorType.add(UIUtil.newOption(GenerationType.IDENTITY.name(), GenerationType.IDENTITY.name()));
        generatorType.add(UIUtil.newOption(GenerationType.AUTO.name(), GenerationType.AUTO.name()));
        UIUtil.refreshSelect(generatorType);
    }

    @Override
    public void init(IdGeneratorEditionDialog presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setGeneratorType(String generatorType) {
        UIUtil.setSelectedValue(this.generatorType, generatorType);
    }

    @Override
    public String getGeneratorType() {
        return generatorType.getValue();
    }

    @Override
    public void setGeneratorName(String generatorName) {
        this.generatorName.setText(generatorName);
    }

    @Override
    public String getGeneratorName() {
        return generatorName.getText();
    }

    @Override
    public void setEnabled(boolean enabled) {
        generatorType.setEnabled(enabled);
        generatorName.setEnabled(enabled);
        footer.enableOkButton(enabled);
    }
}