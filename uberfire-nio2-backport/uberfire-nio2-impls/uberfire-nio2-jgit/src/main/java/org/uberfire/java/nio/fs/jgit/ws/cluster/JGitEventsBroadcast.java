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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;

public class JGitEventsBroadcast {

    private static final Logger LOGGER = LoggerFactory.getLogger(JGitEventsBroadcast.class);
    public static final String DEFAULT_APPFORMER_TOPIC = "default-appformer-topic";

    private List<Session> consumerSessions = new ArrayList<>();
    private String nodeId = UUID.randomUUID().toString();
    private ClusterParameters clusterParameters;
    private Consumer<WatchEventsWrapper> eventsPublisher;
    private Connection connection;

    public JGitEventsBroadcast(ClusterParameters clusterParameters,
                               Consumer<WatchEventsWrapper> eventsPublisher) {
        this.clusterParameters = clusterParameters;
        this.eventsPublisher = eventsPublisher;
        setupJMSConnection();
    }

    private void setupJMSConnection() {

        String jmsURL = clusterParameters.getJmsURL();
        String jmsUserName = clusterParameters.getJmsUserName();
        String jmsPassword = clusterParameters.getJmsPassword();
        ConnectionFactory factory = new ActiveMQConnectionFactory(jmsURL,
                                                                  jmsUserName,
                                                                  jmsPassword);

        try {
            connection = factory.createConnection();
            connection.setExceptionListener(new JMSExceptionListener());
            connection.start();
        } catch (Exception e) {
            LOGGER.error("Error connecting on JMS " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void createWatchServiceJMS(String topicName) {
        try {
            Session consumerSession = createConsumerSession();
            Destination topic = getTopic(topicName,
                                         consumerSession);
            MessageConsumer messageConsumer = consumerSession.createConsumer(topic);
            messageConsumer.setMessageListener(message -> topicMessageListener(message));
        } catch (Exception e) {
            LOGGER.error("Error creating JMS Watch Service: " + e.getMessage());
        }
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

    private Session createConsumerSession() throws JMSException {
        Session session = connection.createSession(false,
                                                   Session.AUTO_ACKNOWLEDGE);
        consumerSessions.add(session);
        return session;
    }

    public synchronized void broadcast(String fsName,
                                       Path watchable,
                                       List<WatchEvent<?>> events) {
        Session session = null;
        try {
            session = connection.createSession(false,
                                               Session.AUTO_ACKNOWLEDGE);
            Topic topic = getTopic(fsName,
                                   session);
            ObjectMessage objectMessage = session.createObjectMessage(new WatchEventsWrapper(nodeId,
                                                                                             fsName,
                                                                                             watchable,
                                                                                             events));
            MessageProducer messageProducer = session.createProducer(topic);
            messageProducer.send(objectMessage);
        } catch (JMSException e) {
            LOGGER.error("Exception on JMS broadcast: " + e.getMessage());
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    LOGGER.error("Exception on closing JMS session (this could trigger a leak) " + e.getMessage());
                }
            }
        }
    }

    private Topic getTopic(String fsName,
                           Session session) throws JMSException {
        String topicName = DEFAULT_APPFORMER_TOPIC;
        if (fsName.contains("/")) {
            topicName = fsName.substring(0,
                                         fsName.indexOf("/"));
        }
        return session.createTopic(topicName);
    }

    public void close() {
        try {
            for (Session s : consumerSessions) {
                s.close();
            }
            connection.close();
        } catch (JMSException e) {
            LOGGER.error("Exception closing JMS connection and consumerSessions: " + e.getMessage());
        }
    }

    private static class JMSExceptionListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            LOGGER.error("JMSException: " + e.getMessage());
        }
    }
}
