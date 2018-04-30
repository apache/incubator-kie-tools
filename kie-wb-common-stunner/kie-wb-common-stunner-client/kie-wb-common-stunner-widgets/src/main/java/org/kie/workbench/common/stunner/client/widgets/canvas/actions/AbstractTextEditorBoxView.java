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

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.kie.workbench.common.stunner.client.widgets.resources.i18n.StunnerWidgetsConstants;
import org.uberfire.mvp.Command;

public abstract class AbstractTextEditorBoxView<T extends TextEditorBoxView.Presenter> implements IsElement {

    protected final Command showCommand;
    protected final Command hideCommand;

    public T presenter;

    protected TranslationService translationService;

    @Inject
    @DataField
    protected Div editNameBox;

    @Inject
    @Named("i")
    @DataField
    private HTMLElement closeButton;

    @Inject
    @Named("i")
    @DataField
    private HTMLElement saveButton;

    protected AbstractTextEditorBoxView(Command showCommand, Command hideCommand, HTMLElement closeButton, HTMLElement saveButton) {
        this.showCommand = showCommand;
        this.hideCommand = hideCommand;
        this.closeButton = closeButton;
        this.saveButton = saveButton;
    }

    protected AbstractTextEditorBoxView() {
        this.showCommand = () -> this.getElement().getStyle().setProperty("display",
                                                                          "block");
        this.hideCommand = () -> this.getElement().getStyle().setProperty("display",
                                                                          "none");
    }

    public void initialize() {
        closeButton.setTitle(translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImpl_close));
        saveButton.setTitle(translationService.getTranslation(StunnerWidgetsConstants.NameEditBoxWidgetViewImpl_save));
    }

    @EventHandler("editNameBox")
    @SinkNative(Event.ONKEYDOWN | Event.ONMOUSEOVER)
    public void editNameBoxEsc(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONKEYDOWN:
                if (event.getKeyCode() == KeyCodes.KEY_ESCAPE) {
                    presenter.onClose();
                }
                break;
            case Event.ONMOUSEOVER:
                editNameBox.focus();
                break;
        }
    }

    public void setVisible() {
        showCommand.execute();
    }

    public void hide() {
        hideCommand.execute();
    }

    public boolean isVisible() {
        return !(getElement().getStyle().getPropertyValue("display")).equals("none");
    }

    @EventHandler("saveButton")
    public void onSave(ClickEvent clickEvent) {
        presenter.onSave();
    }

    @EventHandler("closeButton")
    public void onClose(ClickEvent clickEvent) {
        presenter.onClose();
    }

    protected void scheduleDeferredCommand(final Scheduler.ScheduledCommand command) {
        Scheduler.get().scheduleDeferred(command);
    }
}
