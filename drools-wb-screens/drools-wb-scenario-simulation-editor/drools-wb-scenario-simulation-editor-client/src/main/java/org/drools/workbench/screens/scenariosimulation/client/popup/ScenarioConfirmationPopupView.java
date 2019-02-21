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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.ParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ScenarioConfirmationPopupView extends AbstractScenarioConfirmationPopupView implements ScenarioConfirmationPopup {

    @Inject
    @DataField("text-warning")
    protected ParagraphElement textWarning;

    @Override
    public void show(final String mainTitleText,
                     final String mainQuestionText,
                     final String text1Text,
                     final String textQuestionText,
                     final String textWarningText,
                     final String okDeleteButtonText,
                     final Command okDeleteCommand) {
        conditionalShow(textWarning, textWarningText);
        show(mainTitleText, mainQuestionText, text1Text, textQuestionText, okDeleteButtonText, okDeleteCommand);
    }
}
