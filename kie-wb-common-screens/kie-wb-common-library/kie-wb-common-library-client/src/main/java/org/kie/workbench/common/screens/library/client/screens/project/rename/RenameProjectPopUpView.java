/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.rename;

import javax.inject.Inject;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLInputElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.common.PopUpUtils;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Templated
public class RenameProjectPopUpView implements RenameProjectPopUpScreen.View,
                                               IsElement {

    private RenameProjectPopUpScreen presenter;
    private BaseModal modal;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("rename")
    private HTMLInputElement rename;

    @Override
    public void show() {
        this.modal.show();
    }

    @Override
    public void hide() {
        this.modal.hide();
    }

    @Override
    public void init(RenameProjectPopUpScreen presenter) {
        this.presenter = presenter;
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.Rename))
                .addBody(body)
                .addFooter(PopUpUtils.footer(cancelButton(),
                                             renameButton()))
                .build();
    }

    private Button renameButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.Rename),
                                 () -> presenter.rename(rename.value),
                                 ButtonType.PRIMARY);
    }

    private Button cancelButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.Cancel),
                                 () -> presenter.cancel(),
                                 ButtonType.DEFAULT);
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }
}
