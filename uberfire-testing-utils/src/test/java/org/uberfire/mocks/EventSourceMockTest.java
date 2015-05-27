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