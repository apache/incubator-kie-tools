/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.data;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Utility class to map an absolute row index to and from a merged row index. An
 * absolute row index represents a row in the underlying data that has not been
 * grouped into fewer rows. A merged row index represents a row in the
 * underlying data that has been grouped.
 */
public class RowMapper {

    private DynamicData data;

    public RowMapper( DynamicData data ) {
        this.data = data;
    }

    /**
     * Map an absolute row index to grouped row index
     * @param index
     * @return
     */
    public int mapToMergedRow( int index ) {
        int mergedRowIndex = 0;
        for ( DynamicDataRow row : data ) {
            index--;
            if ( row instanceof GroupedDynamicDataRow ) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
                index = index - ( groupedRow.getChildRows().size() - 1 );
            }
            if ( index < 0 ) {
                return mergedRowIndex;
            }
            mergedRowIndex++;
        }
        return mergedRowIndex;
    }

    /**
     * Map an grouped row index to an absolute row index
     * @param index
     * @return
     */
    public int mapToAbsoluteRow( int index ) {
        int absoluteRowIndex = 0;
        for ( int iRow = 0; iRow < index; iRow++ ) {
            DynamicDataRow row = data.get( iRow );
            absoluteRowIndex++;
            if ( row instanceof GroupedDynamicDataRow ) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
                absoluteRowIndex = absoluteRowIndex + ( groupedRow.getChildRows().size() - 1 );
            }
        }
        return absoluteRowIndex;
    }

    public SortedSet<Integer> mapToAllAbsoluteRows( int index ) {
        int absoluteRowIndex = 0;
        DynamicDataRow row = null;
        SortedSet<Integer> absoluteRowIndexes = new TreeSet<Integer>();
        for ( int iRow = 0; iRow <= index; iRow++ ) {
            row = data.get( iRow );
            absoluteRowIndex++;
            if ( row instanceof GroupedDynamicDataRow ) {
                GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
                absoluteRowIndex = absoluteRowIndex + ( groupedRow.getChildRows().size() - 1 );
            }
        }
        if ( row == null ) {
            return absoluteRowIndexes;
        }
        if ( row instanceof GroupedDynamicDataRow ) {
            GroupedDynamicDataRow groupedRow = (GroupedDynamicDataRow) row;
            int groupedRowsCount = groupedRow.getChildRows().size();
            for ( int iRow = 0; iRow < groupedRowsCount; iRow++ ) {
                absoluteRowIndexes.add( absoluteRowIndex + iRow - groupedRowsCount );
            }
        } else {
            absoluteRowIndexes.add( absoluteRowIndex - 1 );
        }
        return absoluteRowIndexes;

    }

}
