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

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.kie.workbench.common.widgets.metadata.client.KieMultipleDocumentEditor;
import org.uberfire.client.mvp.LockManager;
import org.uberfire.client.mvp.LockTarget;

public interface GuidedDecisionTableLockManager extends LockManager {

    /**
     * Retrieves the latest lock information for the provided target and fires
     * events to update the corresponding UI.
     * @param lockTarget the {@link LockTarget} providing information about what to lock.
     * @param presenter the {@link GuidedDecisionTableModellerView.Presenter} container for the LockManager
     */
    void init( final LockTarget lockTarget,
               final GuidedDecisionTableModellerView.Presenter presenter );

    /**
     * Fires an event to update {@link KieMultipleDocumentEditor} editor title based on the documents lock status.
     */
    void fireChangeTitleEvent();

}
