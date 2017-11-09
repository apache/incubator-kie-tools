package org.uberfire.commons.cluster;

import java.io.Serializable;
import java.util.function.Consumer;

public interface ClusterService {

    void connect();

    <T> void createConsumer(DestinationType type,
                        String channel,
                        Class<T> clazz,
                        Consumer<T> listener);


    void broadcast(DestinationType type,
                   String channel,
                   Serializable object);

    boolean isAppFormerClustered();

    void close();

    enum DestinationType {
        PubSub,
        LoadBalancer
    }
}
