/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.java.nio.fs.jgit.ws.cluster;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;

public class JGitEventsBroadcast {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitEventsBroadcast.class);
    public static final String DEFAULT_APPFORMER_TOPIC = "default-appformer-topic";

    private String nodeId = UUID.randomUUID().toString();
    private Consumer<WatchEventsWrapper> eventsPublisher;
    private final ClusterService clusterService;

    public JGitEventsBroadcast(ClusterService clusterService,
                               Consumer<WatchEventsWrapper> eventsPublisher) {
        this.clusterService = clusterService;
        this.eventsPublisher = eventsPublisher;
        setupJMSConnection();
    }

    private void setupJMSConnection() {
        clusterService.connect();
    }

    public void createWatchServiceJMS(String topicName) {
        clusterService.createConsumer(
                ClusterService.DestinationType.PubSub,
                getChannelName(topicName),
                WatchEventsWrapper.class,
                (we) -> {
                    if (!we.getNodeId().equals(nodeId)) {
                        eventsPublisher.accept(we);
                    }
                });
    }

    public synchronized void broadcast(String fsName,
                                       Path watchable,
                                       List<WatchEvent<?>> events) {
        clusterService.broadcast(ClusterService.DestinationType.PubSub,
                                 getChannelName(fsName),
                                 new WatchEventsWrapper(nodeId,
                                                        fsName,
                                                        watchable,
                                                        events));
    }

    private String getChannelName(String fsName) {
        String channelName = DEFAULT_APPFORMER_TOPIC;
        if (fsName.contains("/")) {
            channelName = fsName.substring(0,
                                           fsName.indexOf("/"));
        }
        return channelName;
    }

    public void close() {
        clusterService.close();
    }
}
