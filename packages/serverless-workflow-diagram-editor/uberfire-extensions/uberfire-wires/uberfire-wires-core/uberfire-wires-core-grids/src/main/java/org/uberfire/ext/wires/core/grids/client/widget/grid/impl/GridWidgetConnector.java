/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.wires.core.grids.client.widget.grid.impl;

import org.uberfire.ext.wires.core.grids.client.model.GridColumn;

/**
 * Meta-data for a Connector between two "linked" GridWidgets.
 */
public class GridWidgetConnector {

    private GridColumn<?> sourceColumn;
    private GridColumn<?> targetColumn;

    public GridWidgetConnector(final GridColumn<?> sourceColumn,
                               final GridColumn<?> targetColumn) {
        this.sourceColumn = sourceColumn;
        this.targetColumn = targetColumn;
    }

    /**
     * Get the source Column from which the Connector originates.
     * @return
     */
    public GridColumn<?> getSourceColumn() {
        return sourceColumn;
    }

    /**
     * Get the target Column to which the Connector terminates.
     * @return
     */
    public GridColumn<?> getTargetColumn() {
        return targetColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GridWidgetConnector connector = (GridWidgetConnector) o;

        if (!sourceColumn.equals(connector.sourceColumn)) {
            return false;
        }
        if (!targetColumn.equals(connector.targetColumn)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = sourceColumn.hashCode();
        result = ~~result;
        result = 31 * result + targetColumn.hashCode();
        result = ~~result;
        return result;
    }
}
