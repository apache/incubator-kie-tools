/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.organizationalunit.manager.client.editor.popups;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.organizationalunit.manager.client.editor.OrganizationalUnitManagerPresenter;
import org.guvnor.organizationalunit.manager.client.resources.i18n.OrganizationalUnitManagerConstants;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.HelpBlock;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.uberfire.client.mvp.UberView;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

public class EditOrganizationalUnitPopup extends BaseModal implements UberView<OrganizationalUnitManagerPresenter> {

    interface EditOrganizationalUnitPopupBinder
            extends
            UiBinder<Widget, EditOrganizationalUnitPopup> {

    }

    private static EditOrganizationalUnitPopupBinder uiBinder = GWT.create(EditOrganizationalUnitPopupBinder.class);

    @UiField
    TextBox nameTextBox;

    @UiField
    TextBox ownerTextBox;

    @UiField
    FormGroup defaultGroupIdGroup;

    @UiField
    TextBox defaultGroupIdTextBox;

    @UiField
    HelpBlock defaultGroupIdHelpInline;

    private OrganizationalUnit organizationalUnit;

    private OrganizationalUnitManagerPresenter presenter;

    private final Command okCommand = new Command() {
        @Override
        public void execute() {
            onOKButtonClick();
        }
    };

    private final Command cancelCommand = new Command() {
        @Override
        public void execute() {
            hide();
        }
    };

    private final ModalFooterOKCancelButtons footer = new ModalFooterOKCancelButtons(okCommand,
                                                                                     cancelCommand);

    public EditOrganizationalUnitPopup() {
        setTitle(OrganizationalUnitManagerConstants.INSTANCE.EditOrganizationalUnitPopupTitle());

        setBody(uiBinder.createAndBindUi(this));
        add(footer);
    }

    @Override
    public void init(final OrganizationalUnitManagerPresenter presenter) {
        this.presenter = presenter;
    }

    private void onOKButtonClick() {
        if (defaultGroupIdTextBox.getText() == null || defaultGroupIdTextBox.getText().trim().isEmpty()) {
            defaultGroupIdGroup.setValidationState(ValidationState.ERROR);
            defaultGroupIdHelpInline.setText(OrganizationalUnitManagerConstants.INSTANCE.DefaultGroupIdIsMandatory());
        } else {
            presenter.checkValidGroupId(defaultGroupIdTextBox.getText(),
                                        new RemoteCallback<Boolean>() {
                                            @Override
                                            public void callback(Boolean valid) {
                                                if (!valid) {
                                                    defaultGroupIdGroup.setValidationState(ValidationState.ERROR);
                                                    defaultGroupIdHelpInline.setText(OrganizationalUnitManagerConstants.INSTANCE.InvalidGroupId());
                                                } else {
                                                    presenter.saveOrganizationalUnit(nameTextBox.getText(),
                                                                                     ownerTextBox.getText(),
                                                                                     defaultGroupIdTextBox.getText());
                                                    hide();
                                                }
                                            }
                                        });
        }
    }

    public void setOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    @Override
    public void show() {
        defaultGroupIdGroup.setValidationState(ValidationState.NONE);
        defaultGroupIdHelpInline.setText("");

        if (organizationalUnit == null) {
            nameTextBox.setText("");
            defaultGroupIdTextBox.setText("");
            ownerTextBox.setText("");
            super.show();
        } else {
            presenter.getSanitizedGroupId(organizationalUnit.getName(),
                                          new RemoteCallback<String>() {
                                              @Override
                                              public void callback(final String sanitizedGroupId) {
                                                  nameTextBox.setText(organizationalUnit.getName());

                                                  if (organizationalUnit.getDefaultGroupId() == null || organizationalUnit.getDefaultGroupId().trim().isEmpty()) {
                                                      defaultGroupIdTextBox.setText(sanitizedGroupId);
                                                  } else {
                                                      defaultGroupIdTextBox.setText(organizationalUnit.getDefaultGroupId());
                                                  }

                                                  ownerTextBox.setText(organizationalUnit.getOwner());
                                                  EditOrganizationalUnitPopup.super.show();
                                              }
                                          });
        }
    }
}
