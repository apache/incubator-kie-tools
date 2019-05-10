/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.widgets.example.branchselector;

import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.kie.workbench.common.screens.examples.model.ImportProject;
import org.kie.workbench.common.widgets.client.widget.KieMultipleSelectElement;
import org.kie.workbench.common.widgets.client.widget.KieSelectOption;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static java.util.stream.Collectors.toList;

public class BranchSelectorPopUpPresenter {

    public interface View extends UberElemental<BranchSelectorPopUpPresenter>,
                                  HasBusyIndicator {

        HTMLElement getBranchSelectorContainer();

        void show();

        void hide();

        void showError(final String errorMessage);

        String getMasterIsRequiredMessage();

        String getAtLeastMasterIsRequiredMessage();
    }

    private View view;

    private KieMultipleSelectElement branchSelector;

    ImportProject importProject;

    List<String> branches;

    Consumer<List<String>> branchesSelectedCallback;

    @Inject
    public BranchSelectorPopUpPresenter(final View view,
                                        final KieMultipleSelectElement branchSelector) {
        this.view = view;
        this.branchSelector = branchSelector;
    }

    public void setup(final ImportProject importProject,
                      final Consumer<List<String>> branchesSelectedCallback) {
        this.importProject = importProject;
        this.branchesSelectedCallback = branchesSelectedCallback;

        final List<String> allBranches = importProject.getAllBranches();
        allBranches.sort(String.CASE_INSENSITIVE_ORDER);
        view.init(this);

        this.branches = importProject.getSelectedBranches();
        this.branches.sort(String.CASE_INSENSITIVE_ORDER);
        if (!this.branches.contains("master")) {
            this.branches.add("master");
        }

        branchSelector.setup(view.getBranchSelectorContainer(),
                             allBranches.stream().map(branch -> new KieSelectOption(branch, branch)).collect(toList()),
                             this.branches,
                             this::setSelectedBranches);

        view.show();
    }

    public void defineBranches() {
        if (branches.isEmpty()) {
            view.showError(view.getAtLeastMasterIsRequiredMessage());
            return;
        }

        if (!branches.contains("master")) {
            view.showError(view.getMasterIsRequiredMessage());
            return;
        }

        importProject.setSelectedBranches(branches);
        view.hide();
        branchesSelectedCallback.accept(branches);
    }

    public void cancel() {
        view.hide();
    }

    public void setSelectedBranches(final List<String> branches) {
        this.branches = branches;
    }
}
