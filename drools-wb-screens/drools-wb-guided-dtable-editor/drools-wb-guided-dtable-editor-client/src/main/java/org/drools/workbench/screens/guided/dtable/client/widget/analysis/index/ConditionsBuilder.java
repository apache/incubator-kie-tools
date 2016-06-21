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

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.models.guided.dtable.shared.model.Pattern52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Utils.*;

public class ConditionsBuilder {


    private final Index                 index;
    private final GuidedDecisionTable52 model;
    private final ColumnUtilities utils;
    private final List<DTCellValue52> row;
    private final Pattern52           pattern52;
    private final Pattern               pattern;

    public ConditionsBuilder( final Index index,
                              final GuidedDecisionTable52 model,
                              final List<DTCellValue52> row,
                              final ColumnUtilities utils,
                              final Pattern52 pattern52,
                              final Pattern pattern ) {
        this.index = index;
        this.model = model;
        this.utils = utils;
        this.row = row;
        this.pattern52 = pattern52;
        this.pattern = pattern;
    }

    public void buildConditions() {

        for ( final ConditionCol52 conditionCol52 : pattern52.getChildColumns() ) {
            final int columnIndex = model.getExpandedColumns().indexOf( conditionCol52 );

            buildCondition( conditionCol52,
                            columnIndex );
        }
    }

    public void buildCondition( final ConditionCol52 conditionCol52,
                                final int columnIndex ) {
        if ( rowHasIndex( columnIndex,
                          row ) ) {
            final Field field = resolveField( conditionCol52 );
            field.getConditions().add( buildCondition( field,
                                                       conditionCol52,
                                                       row.get( columnIndex ) ) );
        }
    }

    private Field resolveField( final ConditionCol52 conditionCol52 ) {
        final Field field = Utils.resolveField( pattern,
                                                conditionCol52.getFieldType(),
                                                conditionCol52.getFactField() );
        return field;
    }

    private Condition buildCondition( final Field field,
                                      final ConditionCol52 conditionColumn,
                                      final DTCellValue52 visibleCellValue ) {

        return new ConditionBuilder(
                index,
                model,
                field,
                utils,
                conditionColumn,
                getRealCellValue( conditionColumn,
                                  visibleCellValue )
        ).build();

    }

}
