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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.settings.sections.archetypes;

import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLDivElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLHeadingElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class ArchetypesSectionView implements ArchetypesSectionPresenter.View {

    @Inject
    @Named("h3")
    @DataField("title")
    private HTMLHeadingElement title;

    @Inject
    @DataField("table-container")
    private HTMLDivElement tableContainer;

    @Inject
    @Named("span")
    @DataField("description")
    private HTMLElement description;

    @Override
    public void init(final ArchetypesSectionPresenter presenter) {
        // Currently no need for setting the presenter
    }

    @Override
    public String getTitle() {
        return title.textContent;
    }

    @Override
    public void setTable(final HTMLElement element) {
        tableContainer.appendChild(element);
    }

    @Override
    public void showDescription(final boolean isVisible) {
        this.description.hidden = !isVisible;
    }
}
