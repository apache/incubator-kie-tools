/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.dtable.client.widget.table.model;

import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;

public class GuidedDecisionTableUiCell<T> extends BaseGridCellValue<T> {

    //Does this cell represent "all other values" to those explicitly defined for the column
    private boolean isOtherwise = false;

    public GuidedDecisionTableUiCell( final T value ) {
        this( value,
              false );
    }

    public GuidedDecisionTableUiCell( final T value,
                                      final boolean isOtherwise ) {
        super( value );
        this.isOtherwise = isOtherwise;
    }

    public boolean isOtherwise() {
        return isOtherwise;
    }

    public void setOtherwise( final boolean isOtherwise ) {
        this.isOtherwise = isOtherwise;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof GuidedDecisionTableUiCell ) ) {
            return false;
        }
        if ( !super.equals( o ) ) {
            return false;
        }

        GuidedDecisionTableUiCell<?> that = (GuidedDecisionTableUiCell<?>) o;

        return isOtherwise == that.isOtherwise;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( isOtherwise ? 1 : 0 );
        result = ~~result;
        return result;
    }

}
