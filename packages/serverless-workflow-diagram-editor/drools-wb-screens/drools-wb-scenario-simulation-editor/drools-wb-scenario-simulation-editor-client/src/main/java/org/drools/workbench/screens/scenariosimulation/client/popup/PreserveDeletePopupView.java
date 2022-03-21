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

import com.google.gwt.dom.client.LIElement;
import org.jboss.errai.common.client.dom.MouseEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.client.views.pfly.widgets.Button;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class PreserveDeletePopupView extends AbstractScenarioConfirmationPopupView implements PreserveDeletePopup {

    @Inject
    @DataField("option-1")
    protected LIElement option1;

    @Inject
    @DataField("option-2")
    protected LIElement option2;

    @Inject
    @DataField("ok-preserve-button")
    protected Button okPreserveButton;

    protected Command okPreserveCommand;

    @Override
    public void show(final String mainTitleText,
                     final String mainQuestionText,
                     final String text1Text,
                     final String textQuestionText,
                     final String option1Text,
                     final String option2Text,
                     final String okPreserveButtonText,
                     final String okDeleteButtonText,
                     final Command okPreserveCommand,
                     final Command okDeleteCommand) {
        option1.setInnerText(option1Text);
        option2.setInnerText(option2Text);
        okPreserveButton.setText(okPreserveButtonText);
        this.okPreserveCommand = okPreserveCommand;
        super.show(mainTitleText, mainQuestionText, text1Text, textQuestionText,
                   okDeleteButtonText,
                   okDeleteCommand);
    }

    @EventHandler("ok-preserve-button")
    public void onOkPreserveButton(final @ForEvent("click") MouseEvent event) {
        if (okPreserveCommand != null) {
            okPreserveCommand.execute();
        }
        hide();
    }
}
