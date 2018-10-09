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

package org.kie.workbench.common.screens.library.client.settings.sections.dependencies;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DependenciesView implements DependenciesPresenter.View,
                                         IsElement {

    private DependenciesPresenter presenter;

    @Inject
    private TranslationService translationService;

    @Inject
    private Elemental2DomUtil elemental2DomUtil;

    @Inject
    @Named("tbody")
    @DataField("table")
    private HTMLTableSectionElement table;

    @Inject
    @DataField("add-dependency")
    private HTMLButtonElement addDependency;

    @Inject
    @DataField("add-from-repository")
    private HTMLButtonElement addFromRepository;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Override
    public void init(final DependenciesPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-dependency")
    public void add(final ClickEvent ignore) {
        presenter.addNewDependency();
    }

    @EventHandler("add-from-repository")
    public void addFromRepository(final ClickEvent ignore) {
        presenter.addFromRepository();
    }

    @Override
    public void add(final DependenciesItemPresenter.View itemView) {
        table.appendChild(itemView.getElement());
    }

    @Override
    public void setItems(final List<DependenciesItemPresenter.View> itemViews) {
        elemental2DomUtil.removeAllElementChildren(table);
        itemViews.forEach(this::add);
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
