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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.client.components.columns.Column;
import org.uberfire.ext.layout.editor.client.components.rows.RowDnDEvent;
import org.uberfire.ext.layout.editor.client.components.rows.RowDrop;

@ApplicationScoped
public class DnDManager {

    @Inject
    Event<RowDnDEvent> rowDnDEvent;

    private boolean isOnRowMove;
    private String rowIdBegin;

    private boolean isOnComponentMove;
    private LayoutComponent layoutComponentMove;
    private String rowId;
    private Column draggedColumn;

    public void beginRowMove(String rowIdBegin) {
        this.rowIdBegin = rowIdBegin;
        this.isOnRowMove = true;
    }

    public void endRowMove(String rowIdEnd,
                           RowDrop.Orientation orientation) {
        if (isOnRowMove) {
            rowDnDEvent.fire(new RowDnDEvent(rowIdBegin,
                                             rowIdEnd,
                                             orientation));
            isOnRowMove = false;
        }
    }

    public void dragEndMove() {
        this.isOnRowMove = false;
    }

    public Column getDraggedColumn() {
        return draggedColumn;
    }

    public void endComponentMove() {
        if (isOnComponentMove) {
            isOnComponentMove = false;
        }
    }

    public boolean isOnRowMove() {
        return isOnRowMove;
    }

    public boolean isOnComponentMove() {
        return isOnComponentMove;
    }

    public boolean canMoveRow() {
        return !isOnComponentMove();
    }

    public void dragComponent(LayoutComponent layoutComponentMove,
                              String rowId,
                              Column draggedColumn) {
        this.layoutComponentMove = layoutComponentMove;
        this.rowId = rowId;
        this.draggedColumn = draggedColumn;
        this.isOnComponentMove = true;
    }

    public String getRowId() {
        return rowId;
    }

    public void dragEndComponent() {
        this.isOnComponentMove = false;
    }

    public LayoutComponent getLayoutComponentMove() {
        return layoutComponentMove;
    }
}
