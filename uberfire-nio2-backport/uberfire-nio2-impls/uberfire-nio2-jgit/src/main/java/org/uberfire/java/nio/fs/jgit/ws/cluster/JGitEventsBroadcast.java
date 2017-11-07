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

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;

public class JGitEventsBroadcast {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitEventsBroadcast.class);
    public static final String DEFAULT_APPFORMER_TOPIC = "default-appformer-topic";

    private String nodeId = UUID.randomUUID().toString();
    private Consumer<WatchEventsWrapper> eventsPublisher;
    private final ClusterJMSService clusterJMSService;

    public JGitEventsBroadcast(ClusterJMSService clusterJMSService,
                               Consumer<WatchEventsWrapper> eventsPublisher) {
        this.clusterJMSService = clusterJMSService;
        this.eventsPublisher = eventsPublisher;
        setupJMSConnection();
    }

    private void setupJMSConnection() {
        clusterJMSService.connect();
    }

    public void createWatchServiceJMS(String topicName) {
        clusterJMSService.createConsumer(
                ClusterJMSService.DESTINATION_TYPE.TOPIC,
                getTopicName(topicName),
                message -> topicMessageListener(message));
    }

    private void topicMessageListener(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof WatchEventsWrapper) {
                    WatchEventsWrapper messageWrapper = (WatchEventsWrapper) object;
                    if (!messageWrapper.getNodeId().equals(nodeId)) {
                        eventsPublisher.accept(messageWrapper);
                    }
                }
            } catch (JMSException e) {
                LOGGER.error("Exception receiving JMS message: " + e.getMessage());
            }
        }
    }

    public synchronized void broadcast(String fsName,
                                       Path watchable,
                                       List<WatchEvent<?>> events) {
        clusterJMSService.broadcast(ClusterJMSService.DESTINATION_TYPE.TOPIC,
                                    getTopicName(fsName),
                                    new WatchEventsWrapper(nodeId,
                                                           fsName,
                                                           watchable,
                                                           events));
    }

    private String getTopicName(String fsName) {
        String topicName = DEFAULT_APPFORMER_TOPIC;
        if (fsName.contains("/")) {
            topicName = fsName.substring(0,
                                         fsName.indexOf("/"));
        }
        return topicName;
    }

    public void close() {
        clusterJMSService.close();
    }
}
