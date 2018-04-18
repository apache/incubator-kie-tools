/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.generalsettings;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import elemental2.dom.MouseEvent;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.ForEvent;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class GitUrlsView implements GitUrlsPresenter.View {

    @Inject
    @DataField("url")
    private HTMLInputElement url;

    @Inject
    @Named("span")
    @DataField("copy-to-clipboard-button")
    private HTMLElement copyToClipboardButton;

    @Inject
    @DataField("protocol-select-container")
    private HTMLDivElement protocolSelectContainer;

    private GitUrlsPresenter presenter;

    @Override
    public void init(final GitUrlsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("copy-to-clipboard-button")
    public void onCopyToClipboardButtonClicked(@ForEvent("click") final MouseEvent e) {
        url.select();
        presenter.copyToClipboard();
    }

    @Override
    public void setUrl(final String url) {
        this.url.value = url;
    }

    @Override
    public HTMLElement getProtocolSelectContainer() {
        return protocolSelectContainer;
    }
}
