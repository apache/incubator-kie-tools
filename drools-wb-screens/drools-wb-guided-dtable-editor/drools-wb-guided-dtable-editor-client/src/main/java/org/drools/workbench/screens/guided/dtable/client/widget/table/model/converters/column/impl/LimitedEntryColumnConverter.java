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
package org.drools.workbench.screens.guided.dtable.client.widget.table.model.converters.column.impl;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.Dependent;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryCol;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTablePresenter;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.uberfire.ext.wires.core.grids.client.model.GridColumn;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseHeaderMetaData;

@Dependent
public class LimitedEntryColumnConverter extends BaseColumnConverterImpl {

    @Override
    public int priority() {
        return 1;
    }

    @Override
    public boolean handles( final BaseColumn column ) {
        return column instanceof LimitedEntryCol;
    }

    @Override
    public GridColumn<?> convertColumn( final BaseColumn column,
                                        final GuidedDecisionTablePresenter.Access access,
                                        final GuidedDecisionTableView gridWidget ) {
        final GridColumn<?> uiColumn = newBooleanColumn( makeHeaderMetaData( column ),
                                                         Math.max( column.getWidth(),
                                                                   DEFAULT_COLUMN_WIDTH ),
                                                         true,
                                                         !column.isHideColumn(),
                                                         access,
                                                         gridWidget );
        uiColumn.setMovable( false );
        return uiColumn;
    }

    @Override
    public List<GridColumn.HeaderMetaData> makeHeaderMetaData( final BaseColumn column ) {
        final String groupIdentifier = getGroupIdentifier( column );
        return new ArrayList<GridColumn.HeaderMetaData>() {{
            add( new BaseHeaderMetaData( column.getHeader(),
                                         groupIdentifier ) );
        }};
    }

    private String getGroupIdentifier( final BaseColumn column ) {
        if ( column instanceof ConditionCol52 ) {
            final Pattern52 parentColumn = model.getPattern( (ConditionCol52) column );
            return Pattern52.class.getName() + ":" + parentColumn.getBoundName();

        } else if ( column instanceof ActionCol52 ) {
            return ActionCol52.class.getName();

        }
        return LimitedEntryCol.class.getName() + ":" + model.getExpandedColumns().indexOf( column );
    }

}
