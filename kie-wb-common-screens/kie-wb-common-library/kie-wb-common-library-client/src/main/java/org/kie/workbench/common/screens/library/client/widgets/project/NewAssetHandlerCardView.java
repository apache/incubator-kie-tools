/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.project;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.uberfire.mvp.Command;

@Templated
public class NewAssetHandlerCardView implements NewAssetHandlerCardWidget.View,
                                                IsElement {

    private NewAssetHandlerCardWidget presenter;

    @Inject
    @DataField("card")
    private HTMLDivElement card;

    @Inject
    @DataField("circle")
    @Named("span")
    private HTMLElement circle;

    @Inject
    @DataField("title")
    @Named("h3")
    private HTMLHeadingElement title;

    @Inject
    @DataField("description")
    @Named("h5")
    private HTMLHeadingElement description;

    private Command command;

    @Override
    public void init(NewAssetHandlerCardWidget presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDescription(String description) {
        this.description.textContent = description;
    }

    @Override
    public void setTitle(String shortName) {
        this.title.textContent = shortName;
    }

    @Override
    public void setIcon(HTMLElement icon) {
        this.circle.appendChild(icon);
    }

    @Override
    public void setCommand(Command command) {
        this.command = command;
    }

    @EventHandler("card")
    public void onCardClick(ClickEvent event) {
        if (command != null) {
            this.command.execute();
        }
    }
}
