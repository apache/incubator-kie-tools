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

import java.lang.reflect.Proxy;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class CallerMock<T> implements Caller<T> {

    private T callerProxy;
    private RemoteCallback successCallBack;
    private ErrorCallback errorCallBack;

    public CallerMock( T t ) {
        callerProxy = (T) CallerProxy.newInstance( t );
    }

    @Override
    public T call() {
        final CallerProxy localProxy = ( (CallerProxy) Proxy.getInvocationHandler( callerProxy ) );
        localProxy.setSuccessCallBack( null );
        localProxy.setErrorCallBack( null );
        return callerProxy;
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback ) {
        final CallerProxy localProxy = ( (CallerProxy) Proxy.getInvocationHandler( callerProxy ) );
        localProxy.setSuccessCallBack( (RemoteCallback<Object>) remoteCallback );
        localProxy.setErrorCallBack( null );
        return callerProxy;
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback,
                   ErrorCallback<?> errorCallback ) {
        final CallerProxy localProxy = ( (CallerProxy) Proxy.getInvocationHandler( callerProxy ) );
        localProxy.setSuccessCallBack( (RemoteCallback<Object>) remoteCallback );
        localProxy.setErrorCallBack( (ErrorCallback<Object>) errorCallback );

        return callerProxy;
    }

}
