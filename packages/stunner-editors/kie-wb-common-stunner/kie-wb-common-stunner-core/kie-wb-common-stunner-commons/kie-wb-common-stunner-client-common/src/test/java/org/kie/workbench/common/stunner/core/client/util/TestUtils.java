/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.util;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class TestUtils {

    /**
     * Utility method for easily emulation of a service call unexpected exception. It can be used in your tests in this
     * way:
     * <p>
     * add a line like this before the service invocation:
     * prepareServiceCallerError(myService, myService, new Throwable("The message I want to produce"));
     * <p>
     * do the normal call to the service in your test:
     * myService.callSomeMethod(...)
     * <p>
     * when the service method is invoked the ErrorCallback with the desired Throwable will be invoked instead of the
     * RemoteCallback.
     * @param service the service class mock.
     * @param serviceCaller the service caller mock. Note that this service caller class is usually created on the tests
     * by doing:
     * <p>
     * serviceCaller = new CallerMock<>(service); at some point of the given test file.
     * <p>
     * But due to mockito needs it's also required that the serviceCaller instance is a mock. This can easily be done
     * with no interference with your tests by doing:
     * <p>
     * serviceCaller = spy(new CallerMock<>(service));
     * @param throwable The throwable element we want to emulate that was thrown by the test.
     */
    @SuppressWarnings("unchecked")
    public static <T> void prepareServiceCallerError(T service,
                                                     Caller<T> serviceCaller,
                                                     Throwable throwable) {
        doAnswer(new Answer<T>() {
            public T answer(InvocationOnMock invocation) {
                ErrorCallback callback = (ErrorCallback) invocation.getArguments()[1];
                callback.error(mock(Object.class),
                               throwable);
                return service;
            }
        }).when(serviceCaller).call(any(RemoteCallback.class),
                                    any(ErrorCallback.class));
    }
}
