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

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.uberfire.commons.validation.PortablePreconditions;

public class IndexBuilder {

    private final Index           index;
    private final ColumnUtilities utils;
    private final GuidedDecisionTable52 model;

    public IndexBuilder( final GuidedDecisionTable52 model,
                         final ColumnUtilities utils ) {
        this.index = new Index();
        this.model = PortablePreconditions.checkNotNull( "model", model );
        this.utils = PortablePreconditions.checkNotNull( "utils", utils );
    }

    public Index build() {

        for ( final BaseColumn baseColumn : model.getExpandedColumns() ) {
            this.index.columns.add( new ColumnBuilder( model,
                                                       baseColumn ).build() );
        }

        buildRules();

        return this.index;
    }

    private void buildRules() {
        int index = 0;

        for ( final List<DTCellValue52> row : model.getData() ) {
            this.index.rules.add( new RuleBuilder( this.index,
                                                   model,
                                                   index,
                                                   row,
                                                   utils ).build() );
            index++;
        }
    }

}
