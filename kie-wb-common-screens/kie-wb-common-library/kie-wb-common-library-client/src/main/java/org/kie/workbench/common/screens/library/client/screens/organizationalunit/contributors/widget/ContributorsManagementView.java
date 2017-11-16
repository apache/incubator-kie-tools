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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.widget;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.common.client.dom.UnorderedList;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;

@Templated
public class ContributorsManagementView implements ContributorsManagementPresenter.View,
                                                   IsElement {

    private ContributorsManagementPresenter presenter;

    @Inject
    private TranslationService ts;

    @Inject
    @DataField("filter")
    Input filter;

    @Inject
    @DataField("users")
    UnorderedList users;

    @Override
    public void init(final ContributorsManagementPresenter presenter) {
        this.presenter = presenter;
        filter.setAttribute("placeholder",
                            ts.format(LibraryConstants.Search));
    }

    @Override
    public String getFilterText() {
        return filter.getValue();
    }

    @Override
    public void clearFilter() {
        filter.setValue("");
    }

    @Override
    public void clearUsers() {
        users.setTextContent("");
    }

    @Override
    public void addUser(final ContributorsManagementListItemPresenter item) {
        users.appendChild(item.getView().getElement());
    }

    @EventHandler("filter")
    public void onFilterChange(final KeyUpEvent event) {
        presenter.filter();
    }
}
