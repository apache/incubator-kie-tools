/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.ext.layout.editor.client.components.rows;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropType;
import org.uberfire.ext.layout.editor.client.components.columns.Column;

public class RowDrop {

    private final LayoutComponent component;
    private final String rowId;
    private final Orientation orientation;
    private String originRowOldColumnId;
    private ComponentDropType type;
    private Column oldColumn;

    public RowDrop(LayoutComponent component,
                   String rowId,
                   Orientation orientation) {
        this.component = component;
        this.rowId = rowId;
        this.orientation = orientation;
        this.type = ComponentDropType.NEW;
    }

    public String getRowId() {
        return rowId;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public LayoutComponent getComponent() {
        return component;
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    public ComponentDropType getType() {
        return type;
    }

    public boolean newComponent() {
        return type == ComponentDropType.NEW;
    }

    public RowDrop fromMove(String originRowOldColumnId,
                            Column oldColumn) {
        this.oldColumn = oldColumn;
        this.type = ComponentDropType.FROM_MOVE;
        this.originRowOldColumnId = originRowOldColumnId;
        return this;
    }

    public enum Orientation {
        BEFORE,
        AFTER
    }
}
