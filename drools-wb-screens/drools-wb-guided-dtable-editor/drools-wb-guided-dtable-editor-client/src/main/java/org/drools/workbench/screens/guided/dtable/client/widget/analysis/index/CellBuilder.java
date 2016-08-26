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
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

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
        this.index = PortablePreconditions.checkNotNull( "index", index );
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.columnIndex = PortablePreconditions.checkNotNull( "columnIndex", columnIndex );
        this.utils = PortablePreconditions.checkNotNull( "utils", utils );
        this.baseColumn = PortablePreconditions.checkNotNull( "baseColumn", baseColumn );
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


            if ( baseColumn instanceof BRLConditionVariableColumn ) {
                new BRLConditionsBuilder( index,
                                          model,
                                          rule,
                                          row,
                                          utils ).buildCondition( ( BRLConditionVariableColumn ) baseColumn,
                                                                  columnIndex );
            } else {
                new FieldConditionsBuilder( index,
                                            model,
                                            rule,
                                            row,
                                            utils,
                                            getPattern( rule ) ).buildCondition( ( ConditionCol52 ) baseColumn,
                                                                                 columnIndex );
            }
        }
    }

    private Pattern getPattern( final Rule rule ) {
        return resolvePattern( index,
                               rule,
                               model.getPattern( ( ConditionCol52 ) baseColumn ) );
    }
}
