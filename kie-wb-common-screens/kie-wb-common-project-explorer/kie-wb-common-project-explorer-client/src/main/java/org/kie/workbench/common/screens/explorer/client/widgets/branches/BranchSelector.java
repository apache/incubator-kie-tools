/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.explorer.client.widgets.branches;

import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.guvnor.structure.repositories.Repository;
import org.kie.workbench.common.screens.explorer.client.widgets.ActiveContextItems;

public class BranchSelector
        implements IsWidget {

    private BranchSelectorView view;
    private ActiveContextItems activeContextItems;
    private BranchChangeHandler branchChangeHandler;

    public BranchSelector() {
    }

    @Inject
    public BranchSelector( final BranchSelectorView view,
                           final ActiveContextItems activeContextItems ) {
        this.view = view;
        this.activeContextItems = activeContextItems;
        view.setPresenter( this );
    }

    public void setRepository( final Repository repository ) {
        view.clear();

        if ( repository != null && repository.getBranches() != null && repository.getBranches().size() > 1 ) {

            view.setCurrentBranch( activeContextItems.getActiveBranch() );
            addBranches( repository );
            view.show();

        } else {
            view.hide();
        }

    }

    private void addBranches( final Repository repository ) {
        for ( String branch : repository.getBranches() ) {
            if ( !branch.equals( activeContextItems.getActiveBranch() ) && !branch.equals( "origin" ) ) {
                view.addBranch( branch );
            }
        }
    }

    public void onBranchSelected( String branch) {

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
