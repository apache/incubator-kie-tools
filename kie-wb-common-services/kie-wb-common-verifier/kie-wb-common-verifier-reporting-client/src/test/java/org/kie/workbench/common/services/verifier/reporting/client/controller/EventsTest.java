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

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.AppendRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.DeleteRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.InsertRowEvent;
import org.kie.workbench.common.widgets.decoratedgrid.client.widget.events.UpdateColumnDataEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventsTest {

    @Mock
    EventBus eventBus;

    @Mock
    AnalyzerControllerImpl analyzerController;

    Events events;

    @Before
    public void setUp() throws
                        Exception {
        events = new Events( eventBus,
                             analyzerController );
    }

    @Test
    public void setUpWorksOnlyOnce() throws
                                     Exception {
        events.setup();

        verify( eventBus ).addHandler( ValidateEvent.TYPE,
                                       analyzerController );

        reset( eventBus );

        events.setup();

        verify( eventBus,
                never() ).addHandler( ValidateEvent.TYPE,
                                      analyzerController );

    }

    @Test
    public void tearDownRemovesHandlers() throws
                                          Exception {

        final HandlerRegistration registration = mock( HandlerRegistration.class );
        when( eventBus.addHandler( ValidateEvent.TYPE,
                                   analyzerController ) ).thenReturn( registration );

        mockOtherEventHandlerRegistrations();

        events.setup();

        events.teardown();

        verify( registration ).removeHandler();
    }

    private void mockOtherEventHandlerRegistrations() {
        when( eventBus.addHandler(DeleteRowEvent.TYPE,
                                  analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
        when( eventBus.addHandler( AfterColumnDeleted.TYPE,
                                   analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
        when( eventBus.addHandler(UpdateColumnDataEvent.TYPE,
                                  analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
        when( eventBus.addHandler(AppendRowEvent.TYPE,
                                  analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
        when( eventBus.addHandler(InsertRowEvent.TYPE,
                                  analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
        when( eventBus.addHandler( AfterColumnInserted.TYPE,
                                   analyzerController ) ).thenReturn( mock( HandlerRegistration.class ) );
    }
}