/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.home.client.widgets.home;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.Heading;
import org.jboss.errai.common.client.dom.Paragraph;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.home.client.widgets.shortcut.ShortcutPresenter;

@Templated
public class HomeView implements HomePresenter.View,
                                 IsElement {

    private HomePresenter presenter;

    @Inject
    @DataField("container")
    Div container;

    @Inject
    @Named("h1")
    @DataField("welcome")
    Heading welcome;

    @Inject
    @DataField("description")
    Paragraph description;

    @Inject
    @DataField("shortcuts")
    Div shortcuts;

    @Override
    public void init(final HomePresenter presenter) {
        this.presenter = presenter;
        presenter.setup();
    }

    @Override
    public void setWelcome(final String welcome) {
        this.welcome.setTextContent(welcome);
    }

    @Override
    public void setDescription(final String description) {
        this.description.setTextContent(description);
    }

    @Override
    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.container.getStyle().setProperty("background-image",
                                              "url(" + backgroundImageUrl + ")");
    }

    @Override
    public void addShortcut(final ShortcutPresenter shortcutPresenter) {
        this.shortcuts.appendChild(shortcutPresenter.getView().getElement());
    }
}
