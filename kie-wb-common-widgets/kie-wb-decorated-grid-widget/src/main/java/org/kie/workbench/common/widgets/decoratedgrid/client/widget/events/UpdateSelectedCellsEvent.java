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
 * An event to signal that the value of the selected cells needs to be updated.
 * The event itself does not contain details of what cells are selected as this
 * is maintained by the AbstractMergableGridWidget that handles single and
 * multiple cell selection.
 */
public class UpdateSelectedCellsEvent extends GwtEvent<UpdateSelectedCellsEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onUpdateSelectedCells( UpdateSelectedCellsEvent event );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    //The new value
    private Comparable<?> value;

    public UpdateSelectedCellsEvent( Comparable<?> value ) {
        this.value = value;
    }

    public Comparable<?> getValue() {
        return this.value;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( UpdateSelectedCellsEvent.Handler handler ) {
        handler.onUpdateSelectedCells( this );
    }

}
