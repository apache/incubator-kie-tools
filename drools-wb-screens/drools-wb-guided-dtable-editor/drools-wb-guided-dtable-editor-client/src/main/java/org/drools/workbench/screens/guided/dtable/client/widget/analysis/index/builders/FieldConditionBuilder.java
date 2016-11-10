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

import org.drools.workbench.models.guided.dtable.shared.model.ConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.utilities.ColumnUtilities;
import org.drools.workbench.services.verifier.api.client.checks.util.NullEqualityOperator;
import org.drools.workbench.services.verifier.api.client.configuration.AnalyzerConfiguration;
import org.drools.workbench.services.verifier.api.client.index.Column;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Field;
import org.drools.workbench.services.verifier.api.client.index.FieldCondition;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.uberfire.commons.validation.PortablePreconditions;

public class FieldConditionBuilder {

    private final ConditionCol52 conditionColumn;
    private final Field field;
    private final ValuesResolver valuesResolver;
    private final Column column;
    private final AnalyzerConfiguration configuration;
    private final DTCellValue52 realCellValue;

    public FieldConditionBuilder( final Field field,
                                  final ColumnUtilities utils,
                                  final Column column,
                                  final ConditionCol52 conditionColumn,
                                  final DTCellValue52 realCellValue,
                                  final AnalyzerConfiguration configuration ) {
        this.field = PortablePreconditions.checkNotNull( "field",
                                                         field );
        this.column = PortablePreconditions.checkNotNull( "column",
                                                          column );
        this.conditionColumn = PortablePreconditions.checkNotNull( "conditionColumn",
                                                                   conditionColumn );
        this.configuration = PortablePreconditions.checkNotNull( "configuration",
                                                                 configuration );
        this.realCellValue = PortablePreconditions.checkNotNull( "realCellValue",
                                                                 realCellValue );
        this.valuesResolver = new ValuesResolver( PortablePreconditions.checkNotNull( "utils",
                                                                                      utils ),
                                                  conditionColumn,
                                                  this.realCellValue );
    }

    private Values resolveValues( final String operator ) {

        if ( NullEqualityOperator.contains( operator ) ) {
            if ( realCellValue.getBooleanValue() != null && realCellValue.getBooleanValue() ) {
                return Values.nullValue();
            } else {
                return new Values();
            }
        } else {
            return valuesResolver.getValues();
        }
    }

    private String resolveOperator( final String operator ) {
        if ( NullEqualityOperator.contains( operator ) ) {
            return NullEqualityOperator.resolveOperator( operator );
        } else {
            return operator;
        }
    }

    public Condition build() {
        return new FieldCondition( field,
                                   column,
                                   resolveOperator( conditionColumn.getOperator() ),
                                   resolveValues( conditionColumn.getOperator() ),
                                   configuration );
    }

}
