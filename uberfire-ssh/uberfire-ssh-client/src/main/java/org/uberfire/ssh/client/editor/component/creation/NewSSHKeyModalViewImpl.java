/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ssh.client.editor.component.creation;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLTextAreaElement;
import org.gwtbootstrap3.client.ui.ModalSize;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterYesNoCancelButtons;
import org.uberfire.ssh.client.resources.i18n.AppformerSSHConstants;

@Templated
public class NewSSHKeyModalViewImpl extends BaseModal implements NewSSHKeyModalView {

    private Presenter presenter;

    @Inject
    @DataField
    private HTMLDivElement validation;

    @Inject
    @DataField
    private HTMLDivElement content;

    @Inject
    @DataField
    private HTMLDivElement nameGroup;

    @Inject
    @DataField
    private HTMLInputElement name;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement nameHelpBlock;

    @Inject
    @DataField
    private HTMLDivElement keyGroup;

    @Inject
    @DataField
    private HTMLTextAreaElement key;

    @Inject
    @Named("span")
    @DataField
    private HTMLElement keyHelpBlock;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    private TranslationService translationService;

    @PostConstruct
    private void init() {
        this.setSize(ModalSize.MEDIUM);
        this.setTitle(translationService.getTranslation(AppformerSSHConstants.SSHKeysDisplayerViewImplAdd));
        this.setBody(ElementWrapperWidget.getWidget(content));

        this.add(new ModalFooterYesNoCancelButtons(this, null, null, null, null,
                                                   this::cancel,
                                                   translationService.getTranslation(AppformerSSHConstants.SSHKeyEditorViewImplCancel),
                                                   ButtonType.DEFAULT,
                                                   null,
                                                   this::add,
                                                   translationService.getTranslation(AppformerSSHConstants.SSHKeysDisplayerViewImplAdd),
                                                   ButtonType.PRIMARY,
                                                   null) {
            {
                setCloseModalAfterAction(false);
            }
        });

        name.setAttribute("placeHolder", translationService.getTranslation(AppformerSSHConstants.NewSSHKeyModalViewImplNamePlaceHolder));
        key.setAttribute("placeHolder", translationService.getTranslation(AppformerSSHConstants.NewSSHKeyModalViewImplKeyPlaceHolder));
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show() {
        reset();

        super.show();
    }

    @Override
    public void resetValidation() {
        validation.hidden = true;
        org.jboss.errai.common.client.dom.HTMLElement groupElement = elemental2DomUtil.asHTMLElement(nameGroup);
        DOMUtil.removeEnumStyleName(groupElement, ValidationState.ERROR);
        nameHelpBlock.textContent = "";

        org.jboss.errai.common.client.dom.HTMLElement keyElement = elemental2DomUtil.asHTMLElement(keyGroup);
        DOMUtil.removeEnumStyleName(keyElement, ValidationState.ERROR);
        keyHelpBlock.textContent = "";
    }

    @Override
    public void setNameValidationError(String errorMessage) {
        setValidationError(nameGroup, nameHelpBlock, errorMessage);
    }

    @Override
    public void setKeyValidationError(String errorMessage) {
        setValidationError(keyGroup, keyHelpBlock, errorMessage);
    }

    private void setValidationError(HTMLDivElement groupElement, HTMLElement helpBlock, String errorMessage) {
        validation.hidden = false;
        org.jboss.errai.common.client.dom.HTMLElement erraiGroupElement = elemental2DomUtil.asHTMLElement(groupElement);
        DOMUtil.addEnumStyleName(erraiGroupElement, ValidationState.ERROR);
        helpBlock.textContent = errorMessage;
    }

    private void cancel() {
        presenter.notifyCancel();
    }

    private void add() {
        presenter.notifyAdd(name.value, key.value);
    }

    public void reset() {
        resetValidation();
        name.value = "";
        key.value = "";
    }
}
