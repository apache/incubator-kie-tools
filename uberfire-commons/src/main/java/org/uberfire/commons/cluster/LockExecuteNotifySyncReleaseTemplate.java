package org.uberfire.commons.cluster;

public abstract class LockExecuteNotifySyncReleaseTemplate<V> extends BaseLockExecuteNotifyReleaseTemplate<V> {

    @Override
    public void sendMessage( final ClusterService clusterService ) {
        clusterService.broadcastAndWait( getServiceId(), getMessageType(), buildContent(), timeOut() );
    }

    public int timeOut() {
        return 300;
    }

}
