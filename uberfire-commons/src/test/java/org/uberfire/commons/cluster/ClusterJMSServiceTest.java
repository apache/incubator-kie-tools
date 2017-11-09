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

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClusterJMSServiceTest {

    private ClusterService clusterService;
    private ActiveMQConnectionFactory factory;
    private Connection connection;
    private Session session1;
    private Session session2;

    @Before
    public void setup() throws JMSException {

        factory = mock(ActiveMQConnectionFactory.class);
        connection = mock(Connection.class);
        when(factory.createConnection()).thenReturn(connection);
        session1 = mock(Session.class);
        session2 = mock(Session.class);
        when(connection.createSession(eq(false),
                                      eq(Session.AUTO_ACKNOWLEDGE)))
                .thenReturn(session1, session2);
        clusterService = new ClusterJMSService() {
            @Override
            ActiveMQConnectionFactory createConnectionFactory(String jmsURL,
                                                              String jmsUserName,
                                                              String jmsPassword) {
                return factory;
            }
        };
    }

    @Test
    public void connectTest() throws JMSException {
        clusterService.connect();
        verify(connection).setExceptionListener(any());
        verify(connection).start();
    }

    @Test
    public void sessionConsumersCreatedShouldBeClosed() throws JMSException {
        clusterService.connect();

        clusterService.createConsumer(ClusterJMSService.DestinationType.PubSub,
                                      "dora_destination",
                                      Object.class,
                                         l -> {
                                         });
        clusterService.createConsumer(ClusterJMSService.DestinationType.PubSub,
                                      "dora_destination",
                                      Object.class,
                                         l -> {
                                         });

        clusterService.close();
        verify(session1).close();
        verify(session2).close();
        verify(connection).close();
    }
}