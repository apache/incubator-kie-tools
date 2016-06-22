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

import java.util.ArrayList;
import java.util.List;

import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.GuidedDecisionTable52;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.UpdateHandler;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Action;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.ActionBuilder;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Column;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Condition;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Fields;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Index;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.Rule;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.keys.Values;
import org.drools.workbench.screens.guided.dtable.client.widget.analysis.index.matchers.UUIDMatcher;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

public class UpdateManager {

    private static final int ROW_NUMBER_COLUMN  = 0;
    private static final int DESCRIPTION_COLUMN = 1;

    private       Index                 index;
    private final GuidedDecisionTable52 model;
    private final UpdateHandler         updateHandler;

    public UpdateManager( final Index index,
                          final GuidedDecisionTable52 model,
                          final UpdateHandler updateHandler ) {
        this.index = index;
        this.model = model;
        this.updateHandler = updateHandler;
    }

    public void update( final List<Coordinate> coordinates ) {

        final List<Coordinate> updatedCoordinates = new ArrayList<>();

        for ( final Coordinate coordinate : coordinates ) {
            if ( coordinate.getCol() != ROW_NUMBER_COLUMN
                    && coordinate.getCol() != DESCRIPTION_COLUMN ) {

                if ( new CellUpdateManager( coordinate ).update() ) {
                    updatedCoordinates.add( coordinate );
                }

                updateHandler.updateCoordinates( updatedCoordinates );
            }
        }
    }

    private class CellUpdateManager {

        private final Coordinate           coordinate;
        private final Column               column;
        private final Fields.FieldSelector select;
        private final Values values;

        public CellUpdateManager( final Coordinate coordinate ) {
            this.coordinate = coordinate;

            column = index.columns
                    .where( Column.index().is( coordinate.getCol() ) )
                    .select().first();

            select = index.rules
                    .where( Rule.index().is( coordinate.getRow() ) )
                    .select().patterns()
                    .where( UUIDMatcher.uuid().any() )
                    .select().fields()
                    .where( UUIDMatcher.uuid().any() )
                    .select();

            values = getValue( model.getData().get( coordinate.getRow() ).get( coordinate.getCol() ) );
        }

        /**
         * @return Returns true if the cell content was found and the value changed.
         */
        public boolean update() {

            if ( !updateCondition() ) {
                return updateAction();
            } else {
                return true;
            }
        }

        private boolean updateAction() {
            final Action action = select.actions()
                                        .where( Action.columnUUID().is( column.getUuidKey() ) )
                                        .select().first();

            if ( action != null ) {
                return updateAction( action );
            } else {
                return false;
            }
        }

        private boolean updateAction( final Action action ) {
            final Values comparable = action.getValues();

            if ( values.isThereChanges( comparable ) ) {
                action.setValue( values );
                return true;
            } else {
                return false;
            }
        }

        private boolean updateCondition() {

            final Condition condition = select.conditions()
                                              .where( Condition.columnUUID().is( column.getUuidKey() ) )
                                              .select().first();

            if ( condition != null ) {
                return updateCondition( condition );
            } else {
                return false;
            }
        }

        private boolean updateCondition( final Condition condition ) {
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
            } else if ( value instanceof String && (( String ) value).isEmpty() ) {
                return new Values();
            } else {
                final Values values = new Values();
                values.add( value );
                return values;
            }
        }
    }
}
