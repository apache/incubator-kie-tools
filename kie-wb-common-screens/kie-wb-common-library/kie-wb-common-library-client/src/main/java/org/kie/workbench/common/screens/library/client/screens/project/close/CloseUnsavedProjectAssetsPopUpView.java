/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.project.close;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.kie.workbench.common.screens.library.client.widgets.common.PopUpUtils;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

@Templated
public class CloseUnsavedProjectAssetsPopUpView implements CloseUnsavedProjectAssetsPopUpPresenter.View,
                                                           IsElement {

    private CloseUnsavedProjectAssetsPopUpPresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    private Elemental2DomUtil domUtil;

    private BaseModal modal;

    @Inject
    @DataField("body")
    HTMLDivElement body;

    @Inject
    @DataField("warning-message")
    @Named("span")
    HTMLElement warningMessage;

    @Inject
    @DataField("assets")
    HTMLUListElement assets;

    @Override
    public void init(final CloseUnsavedProjectAssetsPopUpPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    @Override
    public void addPlace(final CloseUnsavedProjectAssetsPopUpListItemPresenter.View placeListItem) {
        assets.appendChild(placeListItem.getElement());
    }

    @Override
    public void show(final String projectName) {
        this.warningMessage.textContent = ts.format(LibraryConstants.CloseUnsavedProjectAssetsWarningMessage,
                                                       projectName);
        modal.show();
    }

    @Override
    @PreDestroy
    public void hide() {
        modal.hide();
    }

    @Override
    public void clearPlaces() {
        domUtil.removeAllElementChildren(assets);
    }

    private void modalSetup() {
        this.modal = new CommonModalBuilder()
                .addHeader(ts.format(LibraryConstants.CloseUnsavedProjectAssets))
                .addBody(body)
                .addFooter(PopUpUtils.footer(cancelButton(),
                                             proceedButton()))
                .build();
    }

    private Button proceedButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.Proceed),
                                 () -> presenter.proceed(),
                                 ButtonType.PRIMARY);
    }

    private Button cancelButton() {
        return PopUpUtils.button(ts.format(LibraryConstants.Cancel),
                                 () -> presenter.cancel(),
                                 ButtonType.DEFAULT);
    }
}
