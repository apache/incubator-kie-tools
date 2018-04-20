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

package org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.Element;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLHeadingElement;
import elemental2.dom.HTMLTableSectionElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.settings.sections.externaldataobjects.ExternalDataObjectsItemPresenter.View;

@Templated
public class ExternalDataObjectsView implements ExternalDataObjectsPresenter.View {

    @Inject
    @Named("tbody")
    @DataField("table")
    private HTMLTableSectionElement table;

    @Inject
    @DataField("add-button")
    private HTMLButtonElement addButton;

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    private ExternalDataObjectsPresenter presenter;

    @Override
    public void init(final ExternalDataObjectsPresenter presenter) {
        this.presenter = presenter;
    }

    @EventHandler("add-button")
    public void onAdd(final ClickEvent ignore) {
        presenter.openAddPopup();
    }

    @Override
    public void remove(final View view) {
        table.removeChild(view.getElement());
    }

    @Override
    public void add(final View view) {
        table.appendChild(view.getElement());
    }

    @Override
    public Element getImportsTable() {
        return table;
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }
}
