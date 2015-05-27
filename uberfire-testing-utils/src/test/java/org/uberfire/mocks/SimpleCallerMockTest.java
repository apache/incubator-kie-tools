package org.uberfire.mocks;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class SimpleCallerMockTest {

    @Mock
    CallerMockSample callerMockSample;

    static class CallerMockSample extends SimpleCallerMock<SampleTarget> {

    }

    @Test
    public void callerSampleTest() {
        when( callerMockSample.call() ).thenReturn( new SampleTargetImpl() );

        CallerSampleClient callerSample = new CallerSampleClient( callerMockSample );
        callerSample.simpleTargetCall();
        verify( callerMockSample, times( 1 ) ).call();
    }

    private class CallerSampleClient {

        private final Caller<SampleTarget> caller;

        public CallerSampleClient( Caller<SampleTarget> caller ) {
            this.caller = caller;
        }

        public void simpleTargetCall() {
            caller.call().targetCall();
        }

    }

    private interface SampleTarget {

        public String targetCall();

    }

    private class SampleTargetImpl implements SampleTarget {

        @Override
        public String targetCall() {
            return "targetCall";
        }
    }
}