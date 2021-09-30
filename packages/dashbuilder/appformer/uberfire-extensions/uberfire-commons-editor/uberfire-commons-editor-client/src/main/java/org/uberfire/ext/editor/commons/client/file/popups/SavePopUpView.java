/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.GenericModalFooter;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class SavePopUpView implements SavePopUpPresenter.View,
                                      IsElement {

    @Inject
    @DataField("body")
    Div body;
    
    @Inject
    @DataField("commentTextBox")
    TextBox commentTextBox;

    @Inject
    private TranslationService translationService;

    private SavePopUpPresenter presenter;

    private BaseModal modal;

    @Override
    public void init(SavePopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }
    
    @Override
    public String getComment() {
        return commentTextBox.getValue();
    }

    @Override
    public void show() {
        commentTextBox.setValue("");
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(translate(Constants.SavePopUpView_SaveWithComments))
                .addBody(body)
                .addFooter(footer())
                .build();
        commentTextBox.setPlaceholder(translate(Constants.ToggleCommentView_EnterComment));
    }

    private ModalFooter footer() {
        GenericModalFooter footer = new GenericModalFooter();
        footer.addButton(translate(Constants.SavePopUpView_Cancel),
                         cancelCommand(),
                         ButtonType.DEFAULT);
        footer.addButton(translate(Constants.SavePopUpView_Save),
                         saveCommand(),
                         IconType.SAVE,
                         ButtonType.PRIMARY);
        return footer;
    }

    private String translate(final String key) {
        return translationService.format(key);
    }

    private Command saveCommand() {
        return () -> presenter.save();
    }

    private Command cancelCommand() {
        return () -> presenter.cancel();
    }
}
