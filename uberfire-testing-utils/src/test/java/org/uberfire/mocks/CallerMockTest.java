package org.uberfire.mocks;

import javax.inject.Inject;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CallerMockTest {

    SampleTarget sampleTarget;
    RemoteCallback<String> successCallBack;
    ErrorCallback<String> errorCallBack;
    private CallerMock<SampleTarget> callerMock;
    private CallerSampleClient callerSample;

    @Before
    public void setup() {

        sampleTarget = mock( SampleTarget.class );
        successCallBack = mock( RemoteCallback.class );
        errorCallBack = mock( ErrorCallback.class );
    }

    @Test
    public void callerSampleTest() {

        callerMock = new CallerMock<SampleTarget>( sampleTarget );
        callerSample = new CallerSampleClient( callerMock );

        callerSample.targetCall();
        verify( sampleTarget ).targetCall();

        verify( successCallBack, never() ).callback( anyString() );
        verify( errorCallBack, never() ).error( anyString(), any( SampleException.class ) );

    }

    @Test
    public void callerSampleCallBackSuccessTest() {
        when( sampleTarget.targetCall() ).thenReturn( "callback" );

        callerMock = new CallerMock<SampleTarget>( sampleTarget );
        callerSample = new CallerSampleClient( callerMock, successCallBack, errorCallBack );

        callerSample.targetCallWithSuccessCallBack();

        verify( sampleTarget ).targetCall();
        verify( successCallBack ).callback( "callback" );
        verify( errorCallBack, never() ).error( anyString(), any( SampleException.class ) );

    }

    @Test
    public void callerSampleCallBackErrorTest() throws SampleException {
        when( sampleTarget.targetCallWithCheckedException() ).thenThrow( SampleException.class );

        callerMock = new CallerMock<SampleTarget>( sampleTarget );
        callerSample = new CallerSampleClient( callerMock, successCallBack, errorCallBack );

        callerSample.targetCallWithSuccessAndErrorCallBackCheckedException();

        verify( sampleTarget ).targetCallWithCheckedException();
        verify( errorCallBack ).error( anyString(), any( SampleException.class ) );
        verify( successCallBack, never() ).callback( anyString() );

    }

    @Test
    public void callerSampleCallBackErrorbyRunTimeExceptionTest() {
        SampleTarget target = new SampleTarget() {
            @Override
            public String targetCall() {
                throw new  RuntimeException( );
            }

            @Override
            public String targetCallWithCheckedException() throws SampleException {
                return null;
            }
        };

        callerMock = new CallerMock<SampleTarget>( target );
        callerSample = new CallerSampleClient( callerMock, successCallBack, errorCallBack );

        callerSample.targetCallWithSuccessAndErrorCallBack();

        verify( successCallBack, never() ).callback( anyString() );
        verify( errorCallBack ).error( anyString(), any( RuntimeException.class ) );

    }

    private class CallerSampleClient {

        private RemoteCallback successCallBack;
        private ErrorCallback errorCallBack;
        private Caller<SampleTarget> caller;

        @Inject
        public CallerSampleClient( Caller<SampleTarget> caller ) {
            this.caller = caller;
        }

        public CallerSampleClient( CallerMock<SampleTarget> callerMock,
                                   RemoteCallback successCallBack,
                                   ErrorCallback errorCallBack ) {

            this.caller = callerMock;
            this.successCallBack = successCallBack;
            this.errorCallBack = errorCallBack;
        }

        public void targetCall() {
            caller.call().targetCall();
        }

        public void targetCallWithSuccessCallBack() {
            caller.call( successCallBack ).targetCall();
        }

        public void targetCallWithSuccessAndErrorCallBack()  {
            caller.call( successCallBack, errorCallBack ).targetCall();
        }

        public void targetCallWithSuccessAndErrorCallBackCheckedException() throws SampleException {
            caller.call( successCallBack, errorCallBack ).targetCallWithCheckedException();
        }

    }

    private class SampleException extends Exception {

    }

    private interface SampleTarget {

        public String targetCall();

        public String targetCallWithCheckedException() throws SampleException;

    }

}