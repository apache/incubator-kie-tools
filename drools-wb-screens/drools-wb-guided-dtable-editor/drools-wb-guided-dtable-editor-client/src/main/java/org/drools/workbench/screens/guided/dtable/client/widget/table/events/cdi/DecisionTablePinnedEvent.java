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
package org.drools.workbench.screens.guided.dtable.client.widget.table.events.cdi;

import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableModellerView;
import org.uberfire.commons.validation.PortablePreconditions;

public class DecisionTablePinnedEvent {

    private final GuidedDecisionTableModellerView.Presenter presenter;
    private final boolean isPinned;

    public DecisionTablePinnedEvent( final GuidedDecisionTableModellerView.Presenter presenter,
                                     final boolean isPinned ) {
        this.presenter = PortablePreconditions.checkNotNull( "presenter",
                                                             presenter );
        this.isPinned = isPinned;
    }

    public GuidedDecisionTableModellerView.Presenter getPresenter() {
        return presenter;
    }

    public boolean isPinned() {
        return isPinned;
    }

}
