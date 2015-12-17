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
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.SortConfiguration;

import java.util.List;

/**
 * An event to sort the data
 */
public class SortDataEvent extends GwtEvent<SortDataEvent.Handler> {

    public static interface Handler
            extends
            EventHandler {

        void onSortData( SortDataEvent event );
    }

    public static Type<Handler> TYPE = new Type<Handler>();

    private List<SortConfiguration> sortConfiguration;

    public SortDataEvent( List<SortConfiguration> sortConfiguration ) {
        this.sortConfiguration = sortConfiguration;
    }

    public List<SortConfiguration> getSortConfiguration() {
        return this.sortConfiguration;
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch( SortDataEvent.Handler handler ) {
        handler.onSortData( this );
    }

}
