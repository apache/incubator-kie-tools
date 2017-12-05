/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.client.cms.widget;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.dashbuilder.client.cms.resources.i18n.ContentManagerConstants;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;

@Templated
@Dependent
public class NewPerspectivePopUpView implements NewPerspectivePopUp.View, IsElement {

    NewPerspectivePopUp presenter;
    BaseModal modal;
    ButtonPressed buttonPressed = ButtonPressed.CLOSE;
    ContentManagerConstants i18n = ContentManagerConstants.INSTANCE;

    @Inject
    @DataField
    Div body;

    @Inject
    @DataField
    Div footer;

    @Inject
    @DataField
    Div formDiv;

    @DataField
    @Inject
    Input nameInput;

    @DataField
    @Inject
    Span nameLabel;

    @DataField
    @Inject
    Span nameHelp;

    @Inject
    @DataField
    Span nameErrorLabel;

    @DataField
    @Inject
    Select styleSelect;

    @DataField
    @Inject
    Span styleLabel;

    @DataField
    @Inject
    Span styleHelp;

    @DataField
    @Inject
    Option fluidOption;

    @DataField
    @Inject
    Option pageOption;

    @DataField
    @Inject
    Button okButton;

    @DataField
    @Inject
    Button cancelButton;

    @Override
    public void init(NewPerspectivePopUp presenter) {
        this.presenter = presenter;

        modal = new CommonModalBuilder()
                .addHeader(i18n.newPerspectivePopUpViewTitle())
                .addBody(body)
                .addFooter(footer)
                .build();

        modal.addHiddenHandler(hiddenEvent -> {
            if (ButtonPressed.CLOSE.equals(buttonPressed)) {
                presenter.onCancel();
            }
        });
    }

    public Modal getModal() {
        return modal;
    }

    @Override
    public void show() {
        nameInput.setValue("");
        nameErrorLabel.setTextContent("");
        formDiv.setClassName("form-group");
        fluidOption.setSelected(true);
        pageOption.setSelected(false);

        nameLabel.setTextContent(i18n.newPerspectivePopUpViewName());
        nameHelp.setTitle(i18n.newPerspectivePopUpViewNameHelp());
        styleLabel.setTextContent(i18n.newPerspectivePopUpViewStyle());
        styleHelp.setTitle(i18n.newPerspectivePopUpViewStyleHelp());
        okButton.setTextContent(i18n.newPerspectivePopUpViewOk());
        cancelButton.setTextContent(i18n.newPerspectivePopUpViewCancel());
        fluidOption.setTextContent(i18n.newPerspectivePopUpViewFluid());
        pageOption.setTextContent(i18n.newPerspectivePopUpViewPage());

        modal.setTitle(i18n.newPerspectivePopUpViewTitle());
        modal.show();
        nameInput.focus();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public String getName() {
        return nameInput.getValue();
    }

    @Override
    public String getStyle() {
        String val = styleSelect.getValue();
        return val;
    }

    @Override
    public void errorEmptyName() {
        showNameError(i18n.newPerspectivePopUpViewErrorEmptyName());
    }

    @Override
    public void errorInvalidName() {
        showNameError(i18n.newPerspectivePopUpViewErrorInvalidName());
    }

    @Override
    public void errorDuplicatedName() {
        showNameError(i18n.newPerspectivePopUpViewErrorDuplicatedName());
    }

    private void showNameError(String error) {
        formDiv.setClassName("form-group has-error");
        nameErrorLabel.setTextContent(error);
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("okButton")
    public void okClick(final Event event) {
        buttonPressed = ButtonPressed.OK;
        presenter.onOK();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("cancelButton")
    public void cancelClick(final Event event) {
        buttonPressed = ButtonPressed.CANCEL;
        presenter.onCancel();
    }
}
