/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.commons.cluster;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ClusterJMSServiceTest {

    @Spy
    private ClusterJMSService clusterService = new ClusterJMSService();

    @Mock
    private Session session;

    @Mock
    private Destination destination;

    @Mock
    private MessageConsumer consumer;

    @Before
    public void setUp() throws JMSException {
        doReturn(session).when(clusterService).createConsumerSession();
        doReturn(destination).when(clusterService).createDestination(any(), any(), any());
        doReturn(consumer).when(session).createConsumer(any());
    }

    @Test(expected = RuntimeException.class)
    public void noConnectionModeShouldThrowException() {
        clusterService.connect();
    }

    @Test
    public void testSessionAlreadyCreated() throws Exception {

        String channelName = "channel";

        Consumer<Object> consumer = (Object o) -> {
        };

        clusterService.createConsumer(ClusterService.DestinationType.PubSub, channelName, Object.class, consumer);
        clusterService.createConsumer(ClusterService.DestinationType.PubSub, channelName, Object.class, consumer);

        verify(clusterService, times(1)).createConsumerSession();
        verify(clusterService, times(1)).createDestination(eq(ClusterService.DestinationType.PubSub), eq(channelName), eq(session));
    }

    @Test
    public void testDoNotSaveSession() throws Exception {

        doReturn(null).when(session).createConsumer(any());

        String channelName = "channel";

        Consumer<Object> consumer = (Object o) -> {
        };

        clusterService.createConsumer(ClusterService.DestinationType.PubSub, channelName, Object.class, consumer);
        clusterService.createConsumer(ClusterService.DestinationType.PubSub, channelName, Object.class, consumer);

        verify(clusterService, times(2)).createConsumerSession();
        verify(clusterService, times(2)).createDestination(eq(ClusterService.DestinationType.PubSub), eq(channelName), eq(session));
        verify(session, times(2)).close();
    }

}