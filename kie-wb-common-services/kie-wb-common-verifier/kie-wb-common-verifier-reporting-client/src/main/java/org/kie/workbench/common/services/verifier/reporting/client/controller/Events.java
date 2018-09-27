/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.services.verifier.reporting.client.controller;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;

public class Events {

    private final EventBus eventBus;
    private final AnalyzerControllerImpl analyzerController;

    private Set<HandlerRegistration> eventBusHandlerRegistrations = new HashSet<>();

    public Events(final EventBus eventBus,
                  final AnalyzerControllerImpl analyzerController) {
        this.eventBus = PortablePreconditions.checkNotNull("eventBus",
                                                           eventBus);
        this.analyzerController = PortablePreconditions.checkNotNull("analyzerController",
                                                                     analyzerController);
    }

    public void setup() {
        if (eventBusHandlerRegistrations.isEmpty()) {
            eventBusHandlerRegistrations.add(eventBus.addHandler(ValidateEvent.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(DeleteRowEvent.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(AfterColumnDeleted.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(UpdateColumnDataEvent.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(AppendRowEvent.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(InsertRowEvent.TYPE,
                                                                 analyzerController));
            eventBusHandlerRegistrations.add(eventBus.addHandler(AfterColumnInserted.TYPE,
                                                                 analyzerController));
        }
    }

    public void teardown() {

        for (final HandlerRegistration handlerRegistration : eventBusHandlerRegistrations) {
            handlerRegistration.removeHandler();
        }

        eventBusHandlerRegistrations.clear();
    }
}
