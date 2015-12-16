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
package org.drools.workbench.screens.guided.dtable.client.widget.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.CellValue;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.data.Coordinate;

/**
 * An event to signal that the UI has changed the underlying model data
 */
public class ValidateEvent extends GwtEvent<ValidateEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onValidate( ValidateEvent event );

    }

    public static Type<Handler> TYPE = new Type<Handler>();

    //Updated data: Key=Coordinate of start, Value=Cell-row data
    private final Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates;

    public ValidateEvent() {
        updates = Collections.emptyMap();
    }

    public ValidateEvent( Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> updates ) {
        this.updates = updates;
    }

    public ValidateEvent( final Coordinate coordinate,
                          final List<List<CellValue<? extends Comparable<?>>>> data ) {
        this.updates = new HashMap<Coordinate, List<List<CellValue<? extends Comparable<?>>>>>();
        this.updates.put( coordinate,
                          data );
    }

    public Map<Coordinate, List<List<CellValue<? extends Comparable<?>>>>> getUpdates() {
        return this.updates;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( ValidateEvent.Handler handler ) {
        handler.onValidate( this );
    }

}
