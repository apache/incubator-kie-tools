package org.uberfire.mocks;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.jboss.errai.common.client.api.RemoteCallback;

public class SimpleCallerMock<T> implements Caller<T> {

    @Override
    public T call() {
        throw new UnsupportedOperationException( "mocking testing class" );
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback ) {
        throw new UnsupportedOperationException( "mocking testing class" );
    }

    @Override
    public T call( RemoteCallback<?> remoteCallback,
                   ErrorCallback<?> errorCallback ) {
        throw new UnsupportedOperationException( "mocking testing class" );
    }
}
