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
