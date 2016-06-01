/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.commons.validation.PortablePreconditions;

public class RefreshActionsPanelEvent {

    private final GuidedDecisionTableView.Presenter presenter;
    private final List<ActionCol52> columns;

    public RefreshActionsPanelEvent( final GuidedDecisionTableView.Presenter presenter,
                                     final List<ActionCol52> columns ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter", presenter );
        this.columns = PortablePreconditions.checkNotNull( "columns", columns );
    }

    public GuidedDecisionTableView.Presenter getPresenter() {
        return presenter;
    }

    public List<ActionCol52> getColumns() {
        return columns;
    }

}
