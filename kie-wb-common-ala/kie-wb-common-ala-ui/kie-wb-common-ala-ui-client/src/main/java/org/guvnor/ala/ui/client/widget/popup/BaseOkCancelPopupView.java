/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.ala.ui.client.widget.popup;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.BusyPopup;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKCancelButtons;

@Templated
@Dependent
public class BaseOkCancelPopupView
        implements IsElement,
                   BaseOkCancelPopup.View {

    @Inject
    @DataField("content-div")
    private Div contentDiv;

    private BaseModal modal;

    private BaseOkCancelPopup presenter;

    @PostConstruct
    private void init() {
        this.modal = new BaseModal();
        this.modal.setBody(ElementWrapperWidget.getWidget(this.getElement()));
        this.modal.setClosable(false);
        modal.add(new ModalFooterOKCancelButtons(this::onOK,
                                                 this::onCancel));
    }

    @Override
    public void init(final BaseOkCancelPopup presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(final String title) {
        this.modal.setTitle(title);
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public void setContent(final HTMLElement element) {
        contentDiv.appendChild(element);
    }

    @Override
    public void showBusyIndicator(final String message) {
        BusyPopup.showMessage(message);
    }

    @Override
    public void hideBusyIndicator() {
        BusyPopup.close();
    }

    private void onOK() {
        if (presenter != null) {
            presenter.onOK();
        }
    }

    private void onCancel() {
        if (presenter != null) {
            presenter.onCancel();
        }
    }
}