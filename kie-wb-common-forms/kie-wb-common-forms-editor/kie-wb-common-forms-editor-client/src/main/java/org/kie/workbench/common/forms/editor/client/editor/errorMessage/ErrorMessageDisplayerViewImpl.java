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

package org.kie.workbench.common.forms.editor.client.editor.errorMessage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import org.jboss.errai.common.client.dom.Anchor;
import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.RadioInput;
import org.jboss.errai.common.client.dom.Span;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.editor.client.resources.i18n.FormEditorConstants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.footers.ModalFooterOKButton;

@Templated
public class ErrorMessageDisplayerViewImpl implements ErrorMessageDisplayerView,
                                                      IsElement {

    @Inject
    @DataField
    private Div errorContainer;

    @Inject
    @DataField
    private Span errorMessageContainer;

    @Inject
    @DataField
    private Anchor showMoreAnchor;

    @Inject
    @DataField
    private RadioInput closeEditorRadio;

    @Inject
    @DataField
    private Span closeLabel;

    @Inject
    @DataField
    private Label continueRadioContainer;

    @Inject
    @DataField
    private RadioInput continueRadio;

    @Inject
    private TranslationService translationService;

    private Presenter presenter;

    private BaseModal modal;

    @PostConstruct
    public void init() {
        modal = new BaseModal();
        modal.setTitle(translationService.getTranslation(FormEditorConstants.ErrorMessageDisplayerViewImplTitle));
        modal.setClosable(false);
        modal.setBody(ElementWrapperWidget.getWidget(this.getElement()));
        modal.add(new ModalFooterOKButton(modal::hide));
        modal.addHideHandler(evt -> presenter.notifyClose());
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(String message) {
        errorMessageContainer.setInnerHTML(message);
        closeEditorRadio.setChecked(true);
        continueRadio.setChecked(false);
        modal.show();
    }

    @Override
    public void setSourceType(String sourceType) {
        if(sourceType == null) {
            closeLabel.setTextContent(translationService.getTranslation(FormEditorConstants.ErrorMessageDisplayerViewImplClose));
        } else {
            closeLabel.setTextContent(translationService.format(FormEditorConstants.ErrorMessageDisplayerViewImplCloseAndReview, sourceType));
        }
    }

    @Override
    public void displayShowMoreAnchor(boolean display) {
        showMoreAnchor.setHidden(!display);
    }

    @Override
    public void enableContinueButton(boolean enable) {
        continueRadio.setDisabled(!enable);

        if(enable) {
            DOMUtil.removeCSSClass(continueRadioContainer, "disabled");
        } else {
            DOMUtil.addCSSClass(continueRadioContainer, "disabled");
        }
    }



    @Override
    public void setShowMoreLabel(String label) {
        showMoreAnchor.setTitle(label);
        showMoreAnchor.setTextContent(label);
    }

    @Override
    public boolean isClose() {
        return closeEditorRadio.getChecked();
    }

    @EventHandler("showMoreAnchor")
    public void onShowMore(ClickEvent event) {
        presenter.notifyShowMorePressed();
    }
}
