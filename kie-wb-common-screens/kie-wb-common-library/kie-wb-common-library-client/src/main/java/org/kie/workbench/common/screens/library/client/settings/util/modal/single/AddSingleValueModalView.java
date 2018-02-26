/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.util.modal.single;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.HTMLLabelElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class AddSingleValueModalView implements AddSingleValueModal.View {

    @Inject
    @DataField("header")
    private HTMLDivElement header;

    @Inject
    @DataField("body")
    private HTMLDivElement body;

    @Inject
    @DataField("footer")
    private HTMLDivElement footer;

    @Inject
    @DataField("value")
    private HTMLInputElement value;

    @Inject
    @DataField("add-button")
    private HTMLButtonElement addButton;

    @Inject
    @DataField("cancel-button")
    private HTMLButtonElement cancelButton;

    @Inject
    @DataField("label")
    private HTMLLabelElement label;

    private AddSingleValueModal presenter;

    @Override
    public void init(final AddSingleValueModal presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-button")
    private void onAddClicked(final ClickEvent ignore) {
        presenter.add();
    }

    @EventHandler("cancel-button")
    private void onCancelClicked(final ClickEvent ignore) {
        presenter.cancel();
    }

    @Override
    public void clearForm() {
        value.value = "";
    }

    @Override
    public void focus() {
        value.focus();
    }

    @Override
    public String getValue() {
        return value.value;
    }

    @Override
    public String getHeader() {
        return header.textContent;
    }

    @Override
    public void setHeader(final String header) {
        this.header.textContent = header;
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
    public void setLabel(final String label) {
        this.label.textContent = label;
    }
}
