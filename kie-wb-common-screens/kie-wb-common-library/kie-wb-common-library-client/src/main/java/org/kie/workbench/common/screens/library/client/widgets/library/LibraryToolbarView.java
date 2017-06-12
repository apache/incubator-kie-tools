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
package org.kie.workbench.common.screens.library.client.widgets.library;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.Label;
import org.jboss.errai.common.client.dom.Option;
import org.jboss.errai.common.client.dom.Select;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class LibraryToolbarView implements LibraryToolbarPresenter.View,
                                           IsElement {

    @Inject
    Document document;

    @Inject
    @DataField("repositories-label")
    Label repositoriesLabel;

    @Inject
    @DataField
    Select repositories;

    @Inject
    @DataField("branches-label")
    Label branchesLabel;

    @Inject
    @DataField
    Select branches;

    private LibraryToolbarPresenter presenter;

    @Override
    public void init(final LibraryToolbarPresenter presenter) {
        this.presenter = presenter;
        repositories.setOnchange(event -> presenter.onUpdateSelectedRepository());
        branches.setOnchange(event -> presenter.onUpdateSelectedBranch());
    }

    @Override
    public void clearRepositories() {
        DOMUtil.removeAllChildren(repositories);
    }

    @Override
    public void addRepository(final String alias) {
        repositories.add(createOption(alias));
    }

    @Override
    public String getSelectedRepository() {
        return repositories.getValue();
    }

    @Override
    public void setSelectedRepository(final String alias) {
        repositories.setValue(alias);
    }

    @Override
    public void setRepositorySelectorVisibility(final boolean visible) {
        repositories.setHidden(!visible);
        repositoriesLabel.setHidden(!visible);
    }

    @Override
    public void clearBranches() {
        DOMUtil.removeAllChildren(branches);
    }

    @Override
    public void addBranch(String branchName) {
        branches.add(createOption(branchName));
    }

    @Override
    public String getSelectedBranch() {
        return branches.getValue();
    }

    @Override
    public void setSelectedBranch(String branchName) {
        branches.setValue(branchName);
    }

    @Override
    public void setBranchSelectorVisibility(final boolean visible) {
        branches.setHidden(!visible);
        branchesLabel.setHidden(!visible);
    }

    private Option createOption(String ou) {
        Option option = (Option) document.createElement("option");
        option.setText(ou);
        return option;
    }
}