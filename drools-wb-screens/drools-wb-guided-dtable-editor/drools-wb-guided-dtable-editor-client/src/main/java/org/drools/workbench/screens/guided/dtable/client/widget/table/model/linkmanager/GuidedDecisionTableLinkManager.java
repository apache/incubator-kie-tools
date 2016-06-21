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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.linkmanager;

import java.util.Set;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;

/**
 * Definition of service to maintain links between Decision Tables.
 */
public interface GuidedDecisionTableLinkManager {

    /**
     * Create links between one Guided Decision Table and others. Existing links should be removed and re-created.
     * @param dtPresenter The Decision Table to link from.
     * @param otherDecisionTables All other potential Decision Tables that may be linked to.
     */
    void link( final GuidedDecisionTableView.Presenter dtPresenter,
               final Set<GuidedDecisionTableView.Presenter> otherDecisionTables );

}
