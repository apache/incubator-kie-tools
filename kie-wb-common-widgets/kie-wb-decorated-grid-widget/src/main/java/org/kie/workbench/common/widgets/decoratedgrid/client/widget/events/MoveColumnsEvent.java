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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Represents an event to move one or more consecutive columns.
 */
public class MoveColumnsEvent extends GwtEvent<MoveColumnsEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onMoveColumns( MoveColumnsEvent event );

    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private final int sourceColumnIndex;
    private final int targetColumnIndex;
    private final int numberOfColumns;

    /**
     * Creates a Move Columns event.
     * @param sourceColumnIndex The index of the first column to move
     * @param targetColumnIndex The index to where the columns will be moved
     * @param numberOfColumns The number of columns to move
     */
    public MoveColumnsEvent( int sourceColumnIndex,
                             int targetColumnIndex,
                             int numberOfColumns ) {
        this.sourceColumnIndex = sourceColumnIndex;
        this.targetColumnIndex = targetColumnIndex;
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * Gets the index of the first column to move
     * @return sourceTargetIndex
     */
    public int getSourceColumnIndex() {
        return this.sourceColumnIndex;
    }

    /**
     * Gets the index to where the columns will be moved
     * @return targetColumnIndex
     */
    public int getTargetColumnIndex() {
        return this.targetColumnIndex;
    }

    /**
     * Gets the number of columns to move
     * @return numberOfColumns
     */
    public int getNumberOfColumns() {
        return this.numberOfColumns;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( MoveColumnsEvent.Handler handler ) {
        handler.onMoveColumns( this );
    }

}
