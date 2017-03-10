/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.screens.library.client.widgets;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Document;
import org.jboss.errai.common.client.dom.HTMLElement;
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
    @DataField
    Label organizationalUnitsLabel;

    @Inject
    @DataField
    Select organizationalUnits;

    @Inject
    @DataField
    Select repositories;

    @Inject
    @DataField
    Label branchesLabel;

    @Inject
    @DataField
    Select branches;

    private LibraryToolbarPresenter presenter;

    @Override
    public void init(final LibraryToolbarPresenter presenter) {
        this.presenter = presenter;
        organizationalUnits.setOnchange(event -> presenter.onUpdateSelectedOrganizationalUnit());
        repositories.setOnchange(event -> presenter.onUpdateSelectedRepository());
        branches.setOnchange(event -> presenter.onUpdateSelectedBranch());
    }

    @Override
    public void setOrganizationalUnitLabel(final String label) {
        organizationalUnitsLabel.setTextContent(label + ": ");
    }

    @Override
    public void clearOrganizationalUnits() {
        DOMUtil.removeAllChildren(organizationalUnits);
    }

    @Override
    public void addOrganizationUnit(final String identifier) {
        organizationalUnits.add(createOption(identifier));
    }

    @Override
    public String getSelectedOrganizationalUnit() {
        return organizationalUnits.getValue();
    }

    @Override
    public void setSelectedOrganizationalUnit(final String identifier) {
        organizationalUnits.setValue(identifier);
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
        branches.getStyle()
                .setProperty("visibility",
                             getVisibility(visible));
        branchesLabel.getStyle()
                .setProperty("visibility",
                             getVisibility(visible));
        if (visible) {
            expand(branches);
            expand(branchesLabel);
        } else {
            shrink(branches);
            shrink(branchesLabel);
        }
    }

    private void shrink(final HTMLElement element) {
        element.getStyle()
                .setProperty("width",
                             "0px");
        element.getStyle()
                .setProperty("border",
                             "0px");
        element.getStyle()
                .setProperty("padding",
                             "0px");
    }

    private void expand(final HTMLElement element) {
        element.getStyle()
                .removeProperty("width");
        element.getStyle()
                .removeProperty("border");
        element.getStyle()
                .removeProperty("padding");
    }

    private String getVisibility(boolean visible) {
        if (visible) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    private Option createOption(String ou) {
        Option option = (Option) document.createElement("option");
        option.setText(ou);
        return option;
    }
}