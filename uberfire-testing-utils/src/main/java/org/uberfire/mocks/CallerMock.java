package org.uberfire.mocks;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class CallerMock<T> implements Caller<T> {

    private T callerProxy;
    private RemoteCallback successCallBack;
    private ErrorCallback errorCallBack;

    public CallerMock( T t ) {
        callerProxy = (T) CallerProxy.newInstance( t, successCallBack, errorCallBack );
    }

    public CallerMock( T t,
                       RemoteCallback successCallBack,
                       ErrorCallback errorCallBack ) {
        this.successCallBack = successCallBack;
        this.errorCallBack = errorCallBack;
        callerProxy = (T) CallerProxy.newInstance( t, successCallBack, errorCallBack );
    }

    @Override
    public T call() {
        return callerProxy;
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback ) {
        return callerProxy;
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback,
                   ErrorCallback<?> errorCallback ) {
        return callerProxy;
    }

}
