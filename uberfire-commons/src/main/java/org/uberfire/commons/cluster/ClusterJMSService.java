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
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterJMSService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterJMSService.class);

    private Connection connection;
    private ClusterParameters clusterParameters;
    private List<Session> consumerSessions = new ArrayList<>();

    public ClusterJMSService() {
        clusterParameters = loadParameters();
    }

    public void connect() {
        String jmsURL = clusterParameters.getJmsURL();
        String jmsUserName = clusterParameters.getJmsUserName();
        String jmsPassword = clusterParameters.getJmsPassword();
        ConnectionFactory factory = createConnectionFactory(jmsURL,
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

    ActiveMQConnectionFactory createConnectionFactory(String jmsURL,
                                                              String jmsUserName,
                                                              String jmsPassword) {
        return new ActiveMQConnectionFactory(jmsURL,
                                             jmsUserName,
                                             jmsPassword);
    }

    private ClusterParameters loadParameters() {
        return new ClusterParameters();
    }

    public void createConsumer(DESTINATION_TYPE type,
                               String destinationName,
                               MessageListener listener) {
        try {

            Session session = createConsumerSession();
            Destination topic = createDestination(type,
                                                  destinationName,
                                                  session);
            MessageConsumer messageConsumer = session.createConsumer(topic);
            messageConsumer.setMessageListener(listener);
        } catch (Exception e) {
            LOGGER.error("Error creating JMS Watch Service: " + e.getMessage());
        }
    }

    public synchronized void broadcast(DESTINATION_TYPE type,
                                       String destinationName,
                                       Serializable object) {

        Session session = null;
        try {
            session = connection.createSession(false,
                                               Session.AUTO_ACKNOWLEDGE);
            Destination destination = createDestination(type,
                                                        destinationName,
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

    private Destination createDestination(DESTINATION_TYPE type,
                                          String destinationName,
                                          Session session) throws JMSException {
        if (type.equals(DESTINATION_TYPE.QUEUE)) {
            return session.createQueue(destinationName);
        }
        return session.createTopic(destinationName);
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

    public boolean isAppFormerClustered() {
        return clusterParameters.isAppFormerClustered();
    }

    public static class JMSExceptionListener implements ExceptionListener {

        @Override
        public void onException(JMSException e) {
            LOGGER.error("JMSException: " + e.getMessage());
        }
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

    public enum DESTINATION_TYPE {
        TOPIC,
        QUEUE
    }
}
