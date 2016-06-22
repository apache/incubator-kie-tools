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

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

public class FieldConditionBuilder {

    private final ConditionCol52 conditionColumn;
    private final Field          field;
    private final ValuesResolver valuesResolver;
    private final      Column         column;

    public FieldConditionBuilder( final Field field,
                                  final ColumnUtilities utils,
                                  final Column column,
                                  final ConditionCol52 conditionColumn,
                                  final DTCellValue52 realCellValue ) {
        this.field = PortablePreconditions.checkNotNull( "field", field );
        this.column = PortablePreconditions.checkNotNull( "column", column );
        this.conditionColumn = PortablePreconditions.checkNotNull( "conditionColumn", conditionColumn );
        valuesResolver = new ValuesResolver( PortablePreconditions.checkNotNull( "utils", utils ),
                                             conditionColumn,
                                             PortablePreconditions.checkNotNull( "realCellValue", realCellValue ) );
    }

    public Condition build() {
        return new FieldCondition( field,
                                   column,
                                   conditionColumn.getOperator(),
                                   valuesResolver.getValues() );
    }


}
