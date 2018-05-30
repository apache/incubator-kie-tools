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

package org.uberfire.commons.cluster;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterJMSService implements ClusterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterJMSService.class);

    private Connection connection;
    private ClusterParameters clusterParameters;
    private List<Session> consumerSessions = new ArrayList<>();

    public ClusterJMSService() {
        clusterParameters = loadParameters();
    }

    @Override
    public void connect() {
        try {
            final String jmsUserName = clusterParameters.getJmsUserName();
            final String jmsPassword = clusterParameters.getJmsPassword();
            final ConnectionFactory factory;
            switch (clusterParameters.getConnectionMode()) {
                case REMOTE:
                    final String jmsURL = clusterParameters.getProviderUrl();
                    factory = createRemoteConnectionFactory(jmsURL,
                                                            jmsUserName,
                                                            jmsPassword);
                    break;
                case JNDI:
                    final InitialContext context = new InitialContext(clusterParameters.getInitialContextFactory());
                    factory = createJNDIConnectionFactory(context);
                    break;
                default:
                    throw new RuntimeException("Error setting the cluster mode (should be defined as REMOTE or JNDI");
            }
            if (thereIsNoCredentials(jmsUserName, jmsPassword)) {
                connection = factory.createConnection();
            } else {
                connection = factory.createConnection(jmsUserName, jmsPassword);
            }
            connection.setExceptionListener(new JMSExceptionListener());
            connection.start();
        } catch (Exception e) {
            LOGGER.error("Error connecting on JMS " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private boolean thereIsNoCredentials(String jmsUserName, String jmsPassword) {
        return jmsUserName == null && jmsPassword == null;
    }

    ConnectionFactory createJNDIConnectionFactory(final InitialContext context) throws NamingException {
        return (ConnectionFactory) context.lookup(clusterParameters.getJmsConnectionFactoryJndiName());
    }

    ConnectionFactory createRemoteConnectionFactory(final String jmsURL,
                                                    final String jmsUserName,
                                                    final String jmsPassword) {
        return new ActiveMQConnectionFactory(jmsURL,
                                             jmsUserName,
                                             jmsPassword);
    }

    private ClusterParameters loadParameters() {
        return new ClusterParameters();
    }

    @Override
    public <T> void createConsumer(DestinationType type,
                                   String channel,
                                   Class<T> objectMessageClass,
                                   Consumer<T> listener) {
        try {
            Session session = createConsumerSession();
            Destination topic = createDestination(type,
                                                  channel,
                                                  session);
            MessageConsumer messageConsumer = session.createConsumer(topic);

            messageConsumer.setMessageListener(message -> {
                if (message instanceof ObjectMessage) {
                    try {
                        Serializable object = ((ObjectMessage) message).getObject();
                        if (objectMessageClass.isInstance(object)) {
                            listener.accept((T) object);
                        }
                    } catch (JMSException e) {
                        LOGGER.error("Exception receiving JMS message: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            LOGGER.error("Error creating JMS Watch Service: " + e.getMessage());
        }
    }

    @Override
    public synchronized void broadcast(DestinationType type,
                                       String channel,
                                       Serializable object) {

        Session session = null;
        try {
            session = connection.createSession(false,
                                               Session.AUTO_ACKNOWLEDGE);
            Destination destination = createDestination(type,
                                                        channel,
                                                        session);
            ObjectMessage objectMessage = session.createObjectMessage(object);
            MessageProducer messageProducer = session.createProducer(destination);
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

    private Destination createDestination(DestinationType type,
                                          String channel,
                                          Session session) throws JMSException {
        if (type.equals(DestinationType.LoadBalancer)) {
            return session.createQueue(channel);
        }
        return session.createTopic(channel);
    }

    private Session createConsumerSession() {
        try {
            Session session = connection.createSession(false,
                                                       Session.AUTO_ACKNOWLEDGE);
            consumerSessions.add(session);
            return session;
        } catch (JMSException e) {
            LOGGER.error("Error creating session " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAppFormerClustered() {
        return clusterParameters.isAppFormerClustered();
    }

    public static class JMSExceptionListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            LOGGER.error("JMSException: " + e.getMessage());
        }
    }

    @Override
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
}
