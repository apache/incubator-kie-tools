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

package org.uberfire.mocks;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith( MockitoJUnitRunner.class )
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
    public void callerSampleCallBackPrimitiveTypeTest() throws SampleException {
        when( sampleTarget.targetPrimitiveType() ).thenThrow( SampleException.class );

        callerMock = new CallerMock<SampleTarget>( sampleTarget );
        callerSample = new CallerSampleClient( callerMock, successCallBack, errorCallBack );

        callerSample.targetPrimitiveType();

        verify( sampleTarget ).targetPrimitiveType();
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

            @Override
            public long targetPrimitiveType() {
                return 0;
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

        public long targetPrimitiveType() {
            return caller.call( successCallBack, errorCallBack ).targetPrimitiveType();
        }

    }

    private class SampleException extends Exception {

    }

    private interface SampleTarget {

        String targetCall();

        String targetCallWithCheckedException() throws SampleException;

        long targetPrimitiveType();

    }

}