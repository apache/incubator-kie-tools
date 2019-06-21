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
 *
 */

package org.kie.workbench.common.screens.library.client.widgets.example;

import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class ExampleProjectView implements ExampleProjectWidget.View {

    private ExampleProjectWidget presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("card")
    private HTMLDivElement card;

    @Inject
    @Named("h2")
    @DataField("name")
    private HTMLElement name;

    @Inject
    @Named("h5")
    @DataField("description")
    private HTMLElement description;

    @Inject
    @Named("div")
    @DataField("errors")
    private HTMLDivElement errors;

    @Inject
    @Named("span")
    @DataField("branch-selector")
    private HTMLElement branchSelector;

    @Override
    public void init(ExampleProjectWidget presenter) {
        this.presenter = presenter;
        branchSelector.title = ts.format(LibraryConstants.BranchSelectorClickToSelectBranches);
    }

    @Override
    public void setup(final String name,
                      final String description,
                      final HTMLElement errors) {
        setup(name,
              description,
              errors,
              false);
    }

    @Override
    public void setup(final String name,
                      final String description,
                      final HTMLElement errors,
                      final boolean showBranchSelector) {
        this.name.textContent = name;
        this.description.textContent = description;
        this.description.title = description;
        this.errors.appendChild(errors);
        this.branchSelector.hidden = !showBranchSelector;
    }

    @Override
    public void setActive() {
        this.card.classList.add("active");
    }

    @Override
    public void unsetActive() {
        this.card.classList.remove("active");
    }

    @Override
    public void setDisabled() {
        this.card.classList.add("disabled");
    }

    @Override
    public void changeBranchSelectorTitle(final List<String> branches) {
        branchSelector.title = ts.format(LibraryConstants.BranchSelectorSelectedBranches,
                                         String.join(", ",
                                                     branches));
    }

    @EventHandler("card")
    public void onCardClick(ClickEvent event) {
        this.presenter.click();
    }

    @EventHandler("branch-selector")
    public void onBranchSelectorClick(ClickEvent event) {
        event.stopPropagation();
        this.presenter.selectBranches();
    }
}
