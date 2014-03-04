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
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertColumnEvent;

/**
 * An event to insert a Decision Table column
 */
public class InsertDecisionTableColumnEvent extends InsertColumnEvent<BaseColumn, DTCellValue52> {

    public InsertDecisionTableColumnEvent( List<BaseColumn> columns,
                                           List<List<DTCellValue52>> columnsData,
                                           int index,
                                           boolean redraw ) {
        super( columns,
               columnsData,
               index,
               redraw );
    }

    public InsertDecisionTableColumnEvent( List<BaseColumn> columns,
                                           List<List<DTCellValue52>> columnsData,
                                           int index ) {
        super( columns,
               columnsData,
               index );
    }

    public InsertDecisionTableColumnEvent( BaseColumn column,
                                           List<DTCellValue52> columnData,
                                           int index,
                                           boolean redraw ) {
        super( column,
               columnData,
               index,
               redraw );
    }

    public InsertDecisionTableColumnEvent( BaseColumn column,
                                           List<DTCellValue52> columnData,
                                           int index ) {
        super( column,
               columnData,
               index );
    }

    public static final GwtEvent.Type<InsertColumnEvent.Handler<BaseColumn, DTCellValue52>> TYPE = new GwtEvent.Type<InsertColumnEvent.Handler<BaseColumn, DTCellValue52>>();

    @Override
    public GwtEvent.Type<InsertColumnEvent.Handler<BaseColumn, DTCellValue52>> getAssociatedType() {
        return TYPE;
    }

}
