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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;

/**
 * Popover for Decision Table columns.
 */
public interface ColumnHeaderPopOver {

    /**
     * Shows the Popover.
     * @param modellerView
     *         The Modeller View containing the Decision Table. Cannot be null.
     * @param dtPresenter
     *         The Decision Table. Cannot be null.
     * @param uiColumnIndex
     *         The index of the column within the Decision Table.
     */
    void show( final GuidedDecisionTableModellerView modellerView,
               final GuidedDecisionTableView.Presenter dtPresenter,
               final int uiColumnIndex );

    /**
     * Hides the Popover.
     */
    void hide();

}
