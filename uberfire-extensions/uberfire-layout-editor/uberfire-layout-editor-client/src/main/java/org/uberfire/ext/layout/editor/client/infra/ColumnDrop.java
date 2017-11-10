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

package org.uberfire.ext.layout.editor.client.infra;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.api.ComponentDropType;
import org.uberfire.ext.layout.editor.client.components.columns.Column;

public class ColumnDrop {

    private final String endId;
    private final Orientation orientation;
    private LayoutComponent component;
    private ComponentDropType type;
    private Column oldColumn;

    public ColumnDrop(LayoutComponent component,
                      String endId,
                      Orientation orientation) {
        this.component = component;
        this.endId = endId;
        this.orientation = orientation;
        this.type = ComponentDropType.NEW;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public String getEndId() {
        return endId;
    }

    public LayoutComponent getComponent() {
        return component;
    }

    public boolean isASideDrop() {
        return getOrientation() == ColumnDrop.Orientation.LEFT ||
                getOrientation() == ColumnDrop.Orientation.RIGHT;
    }

    public boolean isALeftDrop() {
        return getOrientation() == ColumnDrop.Orientation.LEFT;
    }

    public boolean isADownDrop() {
        return getOrientation() == ColumnDrop.Orientation.DOWN;
    }

    public Column getOldColumn() {
        return oldColumn;
    }

    public boolean newComponent() {
        return type == ComponentDropType.NEW;
    }

    public ColumnDrop fromMove(Column oldColumn) {
        this.oldColumn = oldColumn;
        this.type = ComponentDropType.FROM_MOVE;
        return this;
    }

    public ComponentDropType getType() {
        return type;
    }

    public enum Orientation {
        LEFT,
        RIGHT,
        UP,
        DOWN
    }
}