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
package org.kie.workbench.common.widgets.decoratedgrid.client.widget.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * An event to notify a column was deleted
 */
public class AfterColumnDeleted extends GwtEvent<AfterColumnDeleted.Handler> {

    private final int firstColumnIndex;
    private final int numberOfColumns;

    public AfterColumnDeleted( final int firstColumnIndex,
                               final int numberOfColumns ) {

        this.firstColumnIndex = firstColumnIndex;
        this.numberOfColumns = numberOfColumns;
    }

    public static interface Handler
            extends
            EventHandler {

        void onAfterDeletedColumn( final AfterColumnDeleted event );
    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    public int getFirstColumnIndex() {
        return firstColumnIndex;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( final AfterColumnDeleted.Handler handler ) {
        handler.onAfterDeletedColumn( this );
    }

}
