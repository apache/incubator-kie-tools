/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.plugin.client.perspective.editor.layout.editor.popups;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.Event;
import org.gwtbootstrap3.client.ui.Modal;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.TemplateWidgetMapper;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.ext.editor.commons.client.file.popups.CommonModalBuilder;
import org.uberfire.ext.plugin.client.resources.i18n.Constants;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;
import org.uberfire.ext.widgets.common.client.common.popups.ButtonPressed;

@Dependent
@Templated
public class EditHTMLView implements EditHTMLPresenter.View {

    private final TranslationService translationService;
    @Inject
    @DataField("body")
    Div body;
    @Inject
    @DataField("footer")
    Div footer;
    private EditHTMLPresenter presenter;
    private BaseModal modal;
    private ButtonPressed buttonPressed = ButtonPressed.CLOSE;

    @Inject
    public EditHTMLView(final TranslationService translationService) {
        super();
        this.translationService = translationService;
    }

    @Override
    public void init(final EditHTMLPresenter presenter) {
        this.presenter = presenter;
        modalSetup();
    }

    private void modalSetup() {
        body.appendChild(presenter.getHtmlEditorView().getElement());

        modal = new CommonModalBuilder()
                .addHeader(translationService.format(Constants.EditHTMLView_Title))
                .addBody(body)
                .addFooter(footer)
                .build();

        modal.addHiddenHandler(hiddenEvent -> {
            if (ButtonPressed.CLOSE.equals(buttonPressed)) {
                presenter.closeClick();
            }
            presenter.destroyHtmlEditor();
        });

        modal.setWidth("960px");
    }

    @Override
    public void show() {
        modal.show();
    }

    @Override
    public void hide() {
        modal.hide();
    }

    @Override
    public String getHtmlEditorPlaceHolder() {
        return translationService.format(Constants.EditHTMLView_HtmlPlaceHolder);
    }

    @Override
    public Modal getModal() {
        return modal;
    }

    @Override
    public HTMLElement getElement() {
        return (HTMLElement) TemplateWidgetMapper.get(modal).getElement().cast();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("okButton")
    public void okClick(final Event event) {
        buttonPressed = ButtonPressed.OK;
        presenter.okClick();
    }

    @SinkNative(Event.ONCLICK)
    @EventHandler("cancelButton")
    public void cancelClick(final Event event) {
        buttonPressed = ButtonPressed.CANCEL;
        presenter.cancelClick();
    }
}
