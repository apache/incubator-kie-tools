/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.lockmanager;

import javax.enterprise.context.Dependent;

import org.drools.workbench.screens.guided.dtable.client.GuidedDecisionTable;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.uberfire.backend.vfs.ObservablePath;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.LockManagerImpl;
import org.uberfire.client.mvp.LockTarget;

@Dependent
@GuidedDecisionTable
public class GuidedDecisionTableLockManagerImpl extends LockManagerImpl implements GuidedDecisionTableLockManager {

    private GuidedDecisionTableModellerView.Presenter presenter;

    @Override
    public void init(final LockTarget lockTarget,
                     final GuidedDecisionTableModellerView.Presenter presenter) {
        this.presenter = presenter;
        init(lockTarget);
    }

    @Override
    public void fireChangeTitleEvent() {
        final Path path = getLockInfo().getFile();
        if (path == null) {
            return;
        }
        presenter.getActiveDecisionTable().ifPresent(dtPresenter -> {
            final ObservablePath dtPath = dtPresenter.getCurrentPath();
            if (dtPath == null) {
                return;
            }
            if (path.equals(dtPath)) {
                super.fireChangeTitleEvent();
            }
        });
    }
}
