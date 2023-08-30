/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.client.widgets.dialog;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLParagraphElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Dependent
@Templated
public class ConfirmationDialogImplView implements ConfirmationDialogImpl.View {

    private Command onYesAction;

    private Command onNoAction;

    private ConfirmationDialogImpl presenter;

    @DataField("header")
    private final HTMLDivElement header;

    @DataField("body")
    private final HTMLDivElement body;

    @DataField("footer")
    private final HTMLDivElement footer;

    @DataField("yes-button")
    private final HTMLButtonElement yesButton;

    @DataField("no-button")
    private final HTMLButtonElement noButton;

    @DataField("bold-description")
    private final HTMLParagraphElement boldDescription;

    @DataField("question")
    private final HTMLParagraphElement question;

    @Inject
    public ConfirmationDialogImplView(final HTMLDivElement header,
                                      final HTMLDivElement body,
                                      final HTMLDivElement footer,
                                      final HTMLButtonElement yesButton,
                                      final HTMLButtonElement noButton,
                                      final HTMLParagraphElement boldDescription,
                                      final HTMLParagraphElement question) {
        this.header = header;
        this.body = body;
        this.footer = footer;
        this.yesButton = yesButton;
        this.noButton = noButton;
        this.boldDescription = boldDescription;
        this.question = question;
    }

    @Override
    public void initialize(final String title,
                           final String boldDescription,
                           final String question,
                           final Command onYesAction,
                           final Command onNoAction) {
        this.onYesAction = onYesAction;
        this.onNoAction = onNoAction;
        this.header.textContent = title;
        this.boldDescription.textContent = boldDescription;
        this.question.textContent = question;
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public HTMLElement getBody() {
        return body;
    }

    @Override
    public HTMLElement getFooter() {
        return footer;
    }

    @Override
    public void init(final ConfirmationDialogImpl presenter) {
        this.presenter = presenter;
    }

    @EventHandler("yes-button")
    public void onYesButtonClick(final ClickEvent e) {
        presenter.hide();
        onYesAction.execute();
    }

    @EventHandler("no-button")
    public void onNoButtonClick(final ClickEvent e) {
        presenter.hide();
        onNoAction.execute();
    }
}
