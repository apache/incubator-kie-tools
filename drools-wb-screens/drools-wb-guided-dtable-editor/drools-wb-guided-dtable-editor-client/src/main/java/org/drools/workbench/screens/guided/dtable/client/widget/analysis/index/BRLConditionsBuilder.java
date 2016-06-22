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

import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Utils.*;

public class BRLConditionsBuilder {


    private final Index                 index;
    private final ColumnUtilities       utils;
    private final List<DTCellValue52>   row;
    private final Rule                  rule;
    private final GuidedDecisionTable52 model;

    public BRLConditionsBuilder( final Index index,
                                 final GuidedDecisionTable52 model,
                                 final Rule rule,
                                 final List<DTCellValue52> row,
                                 final ColumnUtilities utils ) {
        this.index = PortablePreconditions.checkNotNull( "index", index );
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.rule = PortablePreconditions.checkNotNull( "rule", rule );
        this.utils = PortablePreconditions.checkNotNull( "utils", utils );
        this.row = PortablePreconditions.checkNotNull( "row", row );
    }

    public void buildConditions( final List<BRLConditionVariableColumn> childColumns ) {
        for ( final BRLConditionVariableColumn brlConditionVariableColumn : childColumns ) {

            buildCondition( brlConditionVariableColumn,
                            model.getExpandedColumns().indexOf( brlConditionVariableColumn ) );
        }
    }

    public void buildCondition( final BRLConditionVariableColumn conditionCol52,
                                final int columnIndex ) {
        if ( rowHasIndex( columnIndex,
                          row ) ) {
            rule.getConditions().add( buildCondition( conditionCol52,
                                                      row.get( columnIndex ) ) );
        }
    }

    private Condition buildCondition( final BRLConditionVariableColumn conditionColumn,
                                      final DTCellValue52 visibleCellValue ) {

        return new BRLConditionBuilder( index,
                                        utils,
                                        model,
                                        conditionColumn,
                                        getRealCellValue( conditionColumn,
                                                          visibleCellValue )
        ).build();

    }

}
