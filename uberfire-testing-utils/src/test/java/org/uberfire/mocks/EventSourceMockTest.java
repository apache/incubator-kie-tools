/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.mocks;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class EventSourceMockTest {

    @Mock
    EventSampleMock eventSampleMock;

    static class EventSampleMock extends EventSourceMock<EventSourceSample.EventSample> {

    }

    @Test
    public void eventFireTest() {
        EventSourceSample t = new EventSourceSample( eventSampleMock );
        t.fireEvent();
        verify( eventSampleMock, times( 1 ) ).fire( any( EventSourceSample.EventSample.class ) );
    }

    @Test
    public void eventTestWithoutFire() {
        EventSourceSample t = new EventSourceSample( eventSampleMock );
        verify( eventSampleMock, times( 0 ) ).fire( any( EventSourceSample.EventSample.class ) );
    }

    private class EventSourceSample {

        @Inject
        private Event<EventSample> event;

        public EventSourceSample( Event<EventSample> event ) {
            this.event = event;
        }

        public void fireEvent() {
            event.fire( new EventSample() );
        }

        private class EventSample {

        }
    }
}