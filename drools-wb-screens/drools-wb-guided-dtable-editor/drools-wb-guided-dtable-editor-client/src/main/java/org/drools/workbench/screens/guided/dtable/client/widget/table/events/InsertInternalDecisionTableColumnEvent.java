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

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.DynamicColumn;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertInternalColumnEvent;

/**
 * An event to insert a column in the table
 */
public class InsertInternalDecisionTableColumnEvent extends InsertInternalColumnEvent<BaseColumn> {

    public InsertInternalDecisionTableColumnEvent( List<DynamicColumn<BaseColumn>> columns,
                                                   List<List<CellValue<? extends Comparable<?>>>> columnsData,
                                                   int index,
                                                   boolean redraw ) {
        super( columns,
               columnsData,
               index,
               redraw );
    }

    public static final GwtEvent.Type<InsertInternalColumnEvent.Handler<BaseColumn>> TYPE = new GwtEvent.Type<InsertInternalColumnEvent.Handler<BaseColumn>>();

    @Override
    public GwtEvent.Type<InsertInternalColumnEvent.Handler<BaseColumn>> getAssociatedType() {
        return TYPE;
    }

}
