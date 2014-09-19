/*
 * Copyright 2014 JBoss Inc
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

package org.kie.workbench.common.screens.explorer.client.widgets;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.repositories.Repository;

import javax.inject.Inject;

public class BranchSelector
        implements IsWidget, BranchSelectorView.Presenter {

    private BranchSelectorView view;
    private BranchChangeHandler branchChangeHandler;

    @Inject
    public BranchSelector(BranchSelectorView view) {
        this.view = view;
        view.setPresenter(this);
    }

    public void setRepository(Repository repository) {
        view.clear();

        view.setCurrentBranch(repository.getCurrentBranch());

        for (String branch : repository.getBranches()) {
            if (!branch.equals(repository.getCurrentBranch()) && !branch.equals("origin")) {
                view.addBranch(branch);
            }
        }
    }

    @Override
    public void onBranchSelected(String branch) {

        if (branchChangeHandler != null) {
            branchChangeHandler.onBranchSelected(branch);
        }
    }

    public void addBranchChangeHandler(BranchChangeHandler branchChangeHandler) {
        this.branchChangeHandler = branchChangeHandler;
    }

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
