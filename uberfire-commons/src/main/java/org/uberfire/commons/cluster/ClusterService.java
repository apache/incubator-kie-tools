package org.uberfire.commons.cluster;

import org.uberfire.commons.lock.LockService;
import org.uberfire.commons.message.MessageService;

public interface ClusterService extends MessageService,
                                        LockService {

    void start();

    void dispose();
}
