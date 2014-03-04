/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.workbench.screens.guided.dtable.client.widget.table.events;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.SetModelEvent;

/**
 * An event to set the underlying Guided Decision Table model in the table
 */
public class SetGuidedDecisionTableModelEvent extends SetModelEvent<GuidedDecisionTable52> {

    public SetGuidedDecisionTableModelEvent( GuidedDecisionTable52 model ) {
        super( model );
    }

    public static final GwtEvent.Type<SetModelEvent.Handler<GuidedDecisionTable52>> TYPE = new GwtEvent.Type<SetModelEvent.Handler<GuidedDecisionTable52>>();

    @Override
    public GwtEvent.Type<SetModelEvent.Handler<GuidedDecisionTable52>> getAssociatedType() {
        return TYPE;
    }

}
