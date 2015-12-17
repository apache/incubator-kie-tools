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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;

import java.util.Set;

/**
 * An event to signal that the state of the selected cells needs to be updated.
 * The event itself does not contain details of what cells are selected as this
 * is maintained by the AbstractMergableGridWidget that handles single and
 * multiple cell selection.
 */
public class CellStateChangedEvent extends GwtEvent<CellStateChangedEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onCellStateChanged( CellStateChangedEvent event );
    }

    public static enum Operation {
        ADD,
        REMOVE
    }

    public static class CellStateOperation {

        public CellStateOperation( CellValue.CellState state,
                                   Operation operation ) {
            this.state = state;
            this.operation = operation;
        }

        private CellValue.CellState state;
        private Operation           operation;

        public CellValue.CellState getState() {
            return state;
        }

        public Operation getOperation() {
            return operation;
        }

    }

    public static final Type<Handler> TYPE = new Type<Handler>();

    //The new state
    private Set<CellStateOperation> states;

    public CellStateChangedEvent( Set<CellStateOperation> states ) {
        this.states = states;
    }

    public Set<CellStateOperation> getStates() {
        return this.states;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( CellStateChangedEvent.Handler handler ) {
        handler.onCellStateChanged( this );
    }

}
