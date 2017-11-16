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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import org.jboss.errai.common.client.dom.Button;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class ContributorsListView implements ContributorsListPresenter.View,
                                             IsElement {

    private ContributorsListPresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("contributors-list")
    Div contributorsList;

    @Inject
    @DataField("filter-text")
    Input filterText;

    @Inject
    @DataField("edit")
    Button edit;

    @Override
    public void init(final ContributorsListPresenter presenter) {
        this.presenter = presenter;
        filterText.setAttribute("placeholder",
                                ts.getTranslation(LibraryConstants.Search));
        edit.setHidden(!presenter.userCanUpdateOrganizationalUnit());
    }

    @Override
    public void clearContributors() {
        contributorsList.setTextContent("");
    }

    @Override
    public void addContributor(HTMLElement contributor) {
        contributorsList.appendChild(contributor);
    }

    @Override
    public void clearFilterText() {
        this.filterText.setValue("");
    }

    @Override
    public String getOwnerRoleLabel() {
        return ts.format(LibraryConstants.Owner);
    }

    @Override
    public String getContributorRoleLabel() {
        return ts.format(LibraryConstants.Contributor);
    }

    @EventHandler("filter-text")
    public void filterTextChange(final KeyUpEvent event) {
        presenter.filterContributors(filterText.getValue());
    }

    @EventHandler("edit")
    public void edit(final ClickEvent event) {
        presenter.edit();
    }
}
