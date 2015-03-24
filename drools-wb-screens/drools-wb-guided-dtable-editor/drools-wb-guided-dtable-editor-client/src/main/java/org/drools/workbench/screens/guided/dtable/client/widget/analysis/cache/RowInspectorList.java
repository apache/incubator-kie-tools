/*
 * Copyright 2015 JBoss Inc
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.drools.workbench.screens.guided.dtable.client.widget.analysis.RowInspector;

public class RowInspectorList
        extends ArrayList<RowInspector> {

    public Collection<RowInspector> getSortedList() {
        ArrayList<RowInspector> result = new ArrayList<RowInspector>( this );
        Collections.sort( result,
                          new Comparator<RowInspector>() {
                              @Override
                              public int compare( RowInspector rowInspector,
                                                  RowInspector other ) {
                                  return rowInspector.getRowIndex() - other.getRowIndex();
                              }
                          } );
        return result;
    }

    public RowInspector getRowInspector( int rowNumber ) {
        for ( RowInspector rowInspector : this ) {
            if ( rowInspector.getRowIndex() == rowNumber ) {
                return rowInspector;
            }
        }
        return null;
    }

    @Override
    public RowInspector set( int rowNumber,
                             RowInspector rowInspector ) {
        removeRowInspector( rowNumber );
        add( rowInspector );
        return rowInspector;
    }

    public RowInspector removeRowInspector( int rowNumber ) {
        RowInspector rowInspector = getRowInspector( rowNumber );
        super.remove( rowInspector );
        return rowInspector;
    }

    public void increaseRowNumbers( int startingRowNumber ) {
        for ( RowInspector rowInspector : this ) {
            if ( rowInspector.getRowIndex() >= startingRowNumber ) {
                rowInspector.setRowIndex( rowInspector.getRowIndex() + 1 );
            }
        }
    }

    public void decreaseRowNumbers( int startingRowNumber ) {
        for ( RowInspector rowInspector : this ) {
            if ( rowInspector.getRowIndex() > startingRowNumber ) {
                rowInspector.setRowIndex( rowInspector.getRowIndex() - 1 );
            }
        }
    }
}
