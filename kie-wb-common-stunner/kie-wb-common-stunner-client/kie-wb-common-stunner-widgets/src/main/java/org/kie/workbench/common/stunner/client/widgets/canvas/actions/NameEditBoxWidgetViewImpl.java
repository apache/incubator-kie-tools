/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;

@Templated
public class NameEditBoxWidgetViewImpl implements NameEditBoxWidgetView,
                                                  IsElement {

    private TranslationService translationService;

    @Inject
    @DataField
    private TextInput nameBox;

    @Inject
    @Named("i")
    @DataField
    private HTMLElement closeButton;

    @Inject
    @Named("i")
    @DataField
    private HTMLElement saveButton;

    private Presenter presenter;

    @Inject
    public NameEditBoxWidgetViewImpl(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostConstruct
    public void initialize() {
        nameBox.setAttribute("placeHolder",
                             translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImp_name));
        closeButton.setTitle(translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImpl_close));
        saveButton.setTitle(translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImpl_save));
    }

    @Override
    public void init(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void show(final String name) {
        nameBox.setValue(name);
        nameBox.setTextContent(name);
        this.getElement().getStyle().setProperty("display",
                                                 "block");
    }

    @Override
    public void hide() {
        this.getElement().getStyle().setProperty("display",
                                                 "none");
    }

    @EventHandler("nameBox")
    @SinkNative(Event.ONCHANGE | Event.ONKEYPRESS)
    public void onChangeName(Event event) {
        if (event.getTypeInt() == Event.ONCHANGE) {
            presenter.onChangeName(nameBox.getValue());
        } else if (event.getTypeInt() == Event.ONKEYPRESS) {
            presenter.onKeyPress(event.getKeyCode(),
                                 nameBox.getValue());
        }
    }

    @EventHandler("saveButton")
    public void onSave(ClickEvent clickEvent) {
        presenter.onSave();
    }

    @EventHandler("closeButton")
    public void onClose(ClickEvent clickEvent) {
        presenter.onClose();
    }
}
