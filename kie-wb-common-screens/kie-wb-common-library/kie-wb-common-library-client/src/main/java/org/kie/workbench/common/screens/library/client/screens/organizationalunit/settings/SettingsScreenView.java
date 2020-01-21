/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLUListElement;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class SettingsScreenView implements SettingsScreenPresenter.View,
                                           IsElement {

    private SettingsScreenPresenter presenter;

    @Inject
    @DataField("save")
    private HTMLButtonElement save;

    @Inject
    @DataField("reset")
    private HTMLButtonElement reset;

    @Inject
    @DataField("content")
    private HTMLDivElement content;

    @Inject
    @DataField("menu-items-container")
    private HTMLUListElement menuItemsContainer;

    @Override
    public void init(final SettingsScreenPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void enableActions(final boolean isEnabled) {
        save.disabled = !isEnabled;
        reset.disabled = !isEnabled;
    }

    @Override
    public HTMLElement getMenuItemsContainer() {
        return menuItemsContainer;
    }

    @Override
    public HTMLElement getContentContainer() {
        return content;
    }

    @EventHandler("save")
    public void save(final ClickEvent event) {
        presenter.save();
    }

    @EventHandler("reset")
    public void reset(final ClickEvent event) {
        presenter.reset();
    }
}