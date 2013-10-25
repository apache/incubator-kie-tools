package org.uberfire.commons.cluster;

public abstract class LockExecuteNotifyAsyncReleaseTemplate<V> extends BaseLockExecuteNotifyReleaseTemplate<V> {

    @Override
    public void sendMessage( final ClusterService clusterService ) {
        clusterService.broadcast( getServiceId(), getMessageType(), buildContent() );
    }
}
