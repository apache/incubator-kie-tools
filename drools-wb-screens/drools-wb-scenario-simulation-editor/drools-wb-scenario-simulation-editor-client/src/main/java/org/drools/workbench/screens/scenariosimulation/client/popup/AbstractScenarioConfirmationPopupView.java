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

import javax.inject.Inject;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.dom.client.ParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.uberfire.mvp.Command;

public abstract class AbstractScenarioConfirmationPopupView extends AbstractScenarioPopupView implements AbstractScenarioConfirmationPopup {

    @DataField("main-question")
    protected HeadingElement mainQuestion = Document.get().createHElement(2);

    @Inject
    @DataField("text-1")
    protected ParagraphElement text1;

    @Inject
    @DataField("text-question")
    protected ParagraphElement textQuestion;

    @Override
    public void show(final String mainTitleText,
                     final String mainQuestionText,
                     final String text1Text,
                     final String textQuestionText,
                     final String okDeleteButtonText,
                     final Command okDeleteCommand) {
        super.show(mainTitleText, okDeleteButtonText, okDeleteCommand);
        conditionalShow(mainQuestion, mainQuestionText);
        conditionalShow(text1, text1Text);
        conditionalShow(textQuestion, textQuestionText);
    }

}
