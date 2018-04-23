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
package org.kie.workbench.common.stunner.client.widgets.canvas.actions;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextArea;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.kie.workbench.common.stunner.core.client.canvas.controls.actions.MultiLineTextEditorBox;
import org.uberfire.mvp.Command;

@Templated(value = "TextEditorBox.html", stylesheet = "TextEditorBox.css")
@MultiLineTextEditorBox
public class TextEditorMultiLineBoxView
        extends AbstractTextEditorBoxView
        implements TextEditorBoxView,
                   IsElement {

    @Inject
    @DataField
    private TextArea nameField;

    @Inject
    public TextEditorMultiLineBoxView(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    TextEditorMultiLineBoxView(final TranslationService translationService,
                               final Div editNameBox,
                               final TextArea nameField,
                               final Command showCommand,
                               final Command hideCommand,
                               final HTMLElement closeButton,
                               final HTMLElement saveButton) {
        super(showCommand, hideCommand, closeButton, saveButton);
        this.translationService = translationService;
        this.nameField = nameField;
        super.editNameBox = editNameBox;
    }

    @PostConstruct
    public void initialize() {
        super.initialize();
        nameField.setAttribute("placeHolder",
                               translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImp_name));
    }

    @Override
    public void init(TextEditorBoxView.Presenter presenter) {
        super.presenter = presenter;
    }

    @Override
    public void show(final String name) {
        nameField.setValue(name);
        nameField.setTextContent(name);
        nameField.setRows(2);
        nameField.setCols(25);
        setVisible();
    }

    @EventHandler("nameField")
    @SinkNative(Event.ONCHANGE | Event.ONKEYPRESS)
    public void onChangeName(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONCHANGE:
                presenter.onChangeName(nameField.getValue());
                break;
            case Event.ONKEYPRESS:
                presenter.onKeyPress(event.getKeyCode(),
                                     event.getShiftKey(),
                                     nameField.getValue());
                break;
        }
    }
}
