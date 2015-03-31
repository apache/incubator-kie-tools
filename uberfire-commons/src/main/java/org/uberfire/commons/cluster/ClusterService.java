package org.uberfire.commons.cluster;

import org.uberfire.commons.lifecycle.PriorityDisposable;
import org.uberfire.commons.lock.LockService;
import org.uberfire.commons.message.MessageHandlerResolver;
import org.uberfire.commons.message.MessageService;

public interface ClusterService extends MessageService,
                                        LockService,
                                        PriorityDisposable {

    void addMessageHandlerResolver( final MessageHandlerResolver resolver );

    void onStart( Runnable runnable );

    int getHoldCount();
}
