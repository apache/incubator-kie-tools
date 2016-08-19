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

import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache.util.HasIndex;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

public class BRLConditionBuilder {

    private final Index                      index;
    private final BRLConditionVariableColumn conditionColumn;
    private final ValuesResolver valuesResolver;
    private GuidedDecisionTable52 model;

    public BRLConditionBuilder( final Index index,
                                final ColumnUtilities utils,
                                final GuidedDecisionTable52 model,
                                final BRLConditionVariableColumn conditionColumn,
                                final DTCellValue52 realCellValue ) {
        this.index = PortablePreconditions.checkNotNull( "index", index );
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.conditionColumn = PortablePreconditions.checkNotNull( "conditionColumn", conditionColumn );
        valuesResolver = new ValuesResolver( PortablePreconditions.checkNotNull( "utils", utils ),
                                             conditionColumn,
                                             PortablePreconditions.checkNotNull( "realCellValue", realCellValue ) );
    }

    public BRLCondition build() {
        return new BRLCondition( getColumn(),
                                 valuesResolver.getValues() );
    }

    private Column getColumn() {
        return index.columns
                .where( HasIndex.index().is( model.getExpandedColumns().indexOf( conditionColumn ) ) )
                .select().first();
    }
}
