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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders;

import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.services.verifier.api.client.cache.util.HasIndex;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.Pattern;
import org.drools.workbench.services.verifier.api.client.index.Rule;
import org.uberfire.commons.validation.PortablePreconditions;

import static org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.Utils.*;

public class FieldConditionsBuilder {


    private final Index index;
    private final GuidedDecisionTable52 model;
    private final ColumnUtilities utils;
    private final List<DTCellValue52> row;
    private final Pattern pattern;
    private final Rule rule;
    private final AnalyzerConfiguration configuration;

    public FieldConditionsBuilder( final Index index,
                                   final GuidedDecisionTable52 model,
                                   final Rule rule,
                                   final List<DTCellValue52> row,
                                   final ColumnUtilities utils,
                                   final Pattern pattern,
                                   final AnalyzerConfiguration configuration ) {
        this.index = PortablePreconditions.checkNotNull( "index",
                                                         index );
        this.model = PortablePreconditions.checkNotNull( "model",
                                                         model );
        this.rule = PortablePreconditions.checkNotNull( "rule",
                                                        rule );
        this.utils = PortablePreconditions.checkNotNull( "utils",
                                                         utils );
        this.row = PortablePreconditions.checkNotNull( "row",
                                                       row );
        this.pattern = PortablePreconditions.checkNotNull( "pattern",
                                                           pattern );
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
    }

    public void buildConditions( final List<ConditionCol52> childColumns ) {
        for ( final ConditionCol52 conditionCol52 : childColumns ) {

            buildCondition( conditionCol52,
                            model.getExpandedColumns()
                                    .indexOf( conditionCol52 ) );
        }
    }

    public void buildCondition( final ConditionCol52 conditionCol52,
                                final int columnIndex ) {
        if ( rowHasIndex( columnIndex,
                          row ) ) {
            final Field field = resolveField( conditionCol52 );
            final Condition condition = buildCondition( field,
                                                        conditionCol52,
                                                        row.get( columnIndex ) );
            field.getConditions()
                    .add( condition );
            rule.getConditions()
                    .add( condition );
        }
    }

    private Field resolveField( final ConditionCol52 conditionCol52 ) {
        return Utils.resolveField( pattern,
                                   conditionCol52.getFieldType(),
                                   conditionCol52.getFactField(),
                                   configuration );
    }

    private Condition buildCondition( final Field field,
                                      final ConditionCol52 conditionColumn,
                                      final DTCellValue52 visibleCellValue ) {
        final Column column = index.columns
                .where( HasIndex.index()
                                .is( model.getExpandedColumns()
                                             .indexOf( conditionColumn ) ) )
                .select()
                .first();

        return new FieldConditionBuilder( field,
                                          utils,
                                          column,
                                          conditionColumn,
                                          getRealCellValue( conditionColumn,
                                                            visibleCellValue ),
                                          configuration
        ).build();

    }

}
