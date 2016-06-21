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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ActionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Utils.*;

public class CellBuilder {

    private final Index                 index;
    private final GuidedDecisionTable52 model;
    private final int                   columnIndex;
    private final ColumnUtilities       utils;
    private final BaseColumn            baseColumn;

    public CellBuilder( final Index index,
                        final GuidedDecisionTable52 model,
                        final int columnIndex,
                        final ColumnUtilities utils,
                        final BaseColumn baseColumn ) {
        this.index = index;
        this.model = model;
        this.columnIndex = columnIndex;
        this.utils = utils;
        this.baseColumn = baseColumn;
    }

    public void build( final Rule rule,
                       final List<DTCellValue52> row ) {
        if ( baseColumn instanceof ActionCol52 ) {

            new ActionBuilder( index,
                               model,
                               rule,
                               row,
                               utils,
                               ( ActionCol52 ) baseColumn ).build();

        } else if ( baseColumn instanceof ConditionCol52 ) {

            final Pattern52 pattern52 = model.getPattern( ( ConditionCol52 ) baseColumn );

            final Pattern pattern = resolvePattern( rule,
                                                    pattern52 );
            new ConditionsBuilder( index,
                                   model,
                                   row,
                                   utils,
                                   pattern52,
                                   pattern ).buildCondition( ( ConditionCol52 ) baseColumn,
                                                             columnIndex );

        }
    }
}
