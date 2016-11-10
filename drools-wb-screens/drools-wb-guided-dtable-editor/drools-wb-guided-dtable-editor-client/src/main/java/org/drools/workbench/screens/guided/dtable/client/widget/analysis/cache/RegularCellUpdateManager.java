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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis.cache;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.builders.ActionBuilder;
import org.drools.workbench.services.verifier.api.client.index.Action;
import org.drools.workbench.services.verifier.api.client.index.Condition;
import org.drools.workbench.services.verifier.api.client.index.Index;
import org.drools.workbench.services.verifier.api.client.index.keys.Values;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

public class RegularCellUpdateManager
        extends CellUpdateManager {

    private final Values values;

    public RegularCellUpdateManager( final Index index,
                                     final GuidedDecisionTable52 model,
                                     final Coordinate coordinate ) {
        super( index,
               model,
               coordinate );

        values = getValue( model.getData()
                                   .get( coordinate.getRow() )
                                   .get( coordinate.getCol() ) );
    }

    @Override
    protected boolean updateAction( final Action action ) {
        final Values comparable = action.getValues();

        if ( values.isThereChanges( comparable ) ) {
            action.setValue( values );
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean updateCondition( final Condition condition ) {
        final Values oldValues = condition.getValues();


        if ( values == null && oldValues == null ) {
            return false;
        } else if ( values == null || oldValues == null ) {
            condition.setValue( values );
            return true;
        } else if ( values.isThereChanges( oldValues ) ) {
            condition.setValue( values );
            return true;
        } else {
            return false;
        }
    }

    private Values getValue( final DTCellValue52 cell ) {
        final Comparable value = ActionBuilder.getValue( cell );
        if ( value == null ) {
            return new Values();
        } else if ( value instanceof String && ( (String) value ).isEmpty() ) {
            return new Values();
        } else {
            final Values values = new Values();
            values.add( value );
            return values;
        }
    }
}
