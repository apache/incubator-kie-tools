/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.datasource.management.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.screens.datasource.management.events.DeleteDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.DeleteDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.NewDriverEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDataSourceEvent;
import org.kie.workbench.common.screens.datasource.management.events.UpdateDriverEvent;

@ApplicationScoped
public class DataSourceEventHelper {

    private Event<NewDataSourceEvent > newDataSourceEvent;

    private Event<UpdateDataSourceEvent > updateDataSourceEvent;

    private Event<DeleteDataSourceEvent > deleteDataSourceEvent;

    private Event<NewDriverEvent> newDriverEvent;

    private Event<UpdateDriverEvent > updateDriverEvent;

    private Event<DeleteDriverEvent > deleteDriverEvent;

    public DataSourceEventHelper( ) {
    }

    @Inject
    public DataSourceEventHelper( Event< NewDataSourceEvent > newDataSourceEvent,
                                  Event< UpdateDataSourceEvent > updateDataSourceEvent,
                                  Event< DeleteDataSourceEvent > deleteDataSourceEvent,
                                  Event< NewDriverEvent > newDriverEvent,
                                  Event< UpdateDriverEvent > updateDriverEvent,
                                  Event< DeleteDriverEvent > deleteDriverEvent ) {
        this.newDataSourceEvent = newDataSourceEvent;
        this.updateDataSourceEvent = updateDataSourceEvent;
        this.deleteDataSourceEvent = deleteDataSourceEvent;
        this.newDriverEvent = newDriverEvent;
        this.updateDriverEvent = updateDriverEvent;
        this.deleteDriverEvent = deleteDriverEvent;
    }

    public void fireCreateEvent( NewDataSourceEvent event ) {
        newDataSourceEvent.fire( event );
    }

    public void fireUpdateEvent( UpdateDataSourceEvent event ) {
        updateDataSourceEvent.fire( event );
    }

    public void fireDeleteEvent( DeleteDataSourceEvent event ) {
        deleteDataSourceEvent.fire( event );
    }

    public void fireCreateEvent( NewDriverEvent event ) {
        newDriverEvent.fire( event );
    }

    public void fireUpdateEvent( UpdateDriverEvent event ) {
        updateDriverEvent.fire( event );
    }

    public void fireDeleteEvent( DeleteDriverEvent event ) {
        deleteDriverEvent.fire( event );
    }
}