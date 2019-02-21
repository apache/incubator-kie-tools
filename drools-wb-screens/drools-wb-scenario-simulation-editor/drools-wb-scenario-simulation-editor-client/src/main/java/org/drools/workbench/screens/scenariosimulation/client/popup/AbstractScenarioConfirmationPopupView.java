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
package org.drools.workbench.screens.scenariosimulation.client.popup;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.uberfire.client.views.pfly.resources.i18n.Constants;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.client.views.pfly.widgets.Modal;
import org.uberfire.mvp.Command;

public abstract class AbstractScenarioConfirmationPopupView implements AbstractScenarioConfirmationPopup {

    @DataField("main-title")
    protected HeadingElement mainTitle = Document.get().createHElement(4);

    @DataField("main-question")
    protected HeadingElement mainQuestion = Document.get().createHElement(2);

    @Inject
    @DataField("text-1")
    protected ParagraphElement text1;

    @Inject
    @DataField("text-question")
    protected ParagraphElement textQuestion;

    @Inject
    @DataField("cancel-button")
    protected Button cancelButton;

    @Inject
    @DataField("ok-delete-button")
    protected Button okDeleteButton;

    @Inject
    @DataField("modal")
    protected Modal modal;

    @Inject
    protected TranslationService translationService;

    protected Command okDeleteCommand;

    @PostConstruct
    public void init() {
        cancelButton.setText(translationService.getTranslation(Constants.ConfirmPopup_Cancel));
    }

    @Override
    public void show(final String mainTitleText,
                     final String mainQuestionText,
                     final String text1Text,
                     final String textQuestionText,
                     final String okDeleteButtonText,
                     final Command okDeleteCommand) {
        this.okDeleteCommand = okDeleteCommand;
        conditionalShow(mainTitle, mainTitleText);
        conditionalShow(mainQuestion, mainQuestionText);
        conditionalShow(text1, text1Text);
        conditionalShow(textQuestion, textQuestionText);
        conditionalShow(okDeleteButton, okDeleteCommand, okDeleteButtonText);
        modal.show();
    }

    @Override
    public HTMLElement getElement() {
        return modal.getElement();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @EventHandler("ok-delete-button")
    public void onOkDeleteClick(final @ForEvent("click") MouseEvent event) {
        if (okDeleteCommand != null) {
            okDeleteCommand.execute();
        }
        hide();
    }

    @EventHandler("cancel-button")
    public void onCancelClick(final @ForEvent("click") MouseEvent event) {
        hide();
    }

    protected void conditionalShow(Element element, String innerText) {
        if (innerText != null) {
            element.setInnerHTML(innerText);
        } else {
            element.removeFromParent();
        }
    }

    protected void conditionalShow(Button button, Command command, String innerText) {
        if (command == null) {
            button.hide();
        } else {
            button.setText(innerText);
        }
    }
}
