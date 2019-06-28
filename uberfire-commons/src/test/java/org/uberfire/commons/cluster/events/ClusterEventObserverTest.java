/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.commons.cluster.events;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletionStage;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.EventMetadata;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.TypeLiteral;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.clusterapi.Clustered;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ClusterEventObserverTest {

    ClusterJMSService clusterService;
    ClusterEventObserver observer;

    @Before
    public void setup() {
        clusterService = mock(ClusterJMSService.class);
        observer = setupMock();
    }

    @Mock
    private EventSourceMock<Object> eventBus;

    @Test
    public void consumeTestOtherSenderNode() {
        observer.consumeMessage(eventBus,
                                new ClusterSerializedCDIMessageWrapper("wrongNode",
                                                                       "json",
                                                                       "fqcn"));
        verify(eventBus).fire(any());
    }

    @Test
    public void consumeTestMyMessages() {
        observer.consumeMessage(eventBus,
                                new ClusterSerializedCDIMessageWrapper(observer.getNodeId(),
                                                                       "json",
                                                                       "fqcn"));
        verify(eventBus,
               never()).fire(any());
    }

    @Test
    public void shouldBroadCastMessagesOnlyOnCluster() {
        when(clusterService.isAppFormerClustered()).thenReturn(false);

        observer.broadcast(new Object());

        verify(clusterService,
               never()).broadcast(any(),
                                  any(),
                                  any());
    }

    @Test
    public void shouldBroadCastMessages() {
        when(clusterService.isAppFormerClustered()).thenReturn(true);

        observer.broadcast(new EventTest());

        verify(clusterService).broadcast(any(),
                                         any(),
                                         any());
    }

    @Test
    public void shouldObserveThisEventTest() {
        EventMetadata eventMetadataMock = mock(EventMetadata.class);
        InjectionPoint injectionPointMock = mock(InjectionPoint.class);
        Bean beanMock = mock(Bean.class);
        when(eventMetadataMock.getInjectionPoint()).thenReturn(injectionPointMock);
        when(injectionPointMock.getBean()).thenReturn(beanMock);

        assertFalse(observer.shouldObserveThisEvent(new Object(),
                                                    null));
        assertTrue(observer.shouldObserveThisEvent(new EventTest(),
                                                   null));

        when(beanMock.getBeanClass()).thenReturn(Object.class);
        assertTrue(observer.shouldObserveThisEvent(new EventTest(),
                                                   eventMetadataMock));

        when(beanMock.getBeanClass()).thenReturn(observer.getClass());
        assertFalse(observer.shouldObserveThisEvent(new EventTest(),
                                                    eventMetadataMock));

        when(eventMetadataMock.getInjectionPoint()).thenReturn(null);
        assertTrue(observer.shouldObserveThisEvent(new EventTest(),
                                                   eventMetadataMock));
    }

    @Portable
    @Clustered
    public static class EventTest {

    }

    private ClusterEventObserver setupMock() {
        return new ClusterEventObserver() {
            @Override
            ClusterJMSService getClusterService() {
                return clusterService;
            }

            @Override
            Object fromJSON(ClusterSerializedCDIMessageWrapper message) {
                return new Object();
            }

            @Override
            String toJSON(Object event) {
                return "Dora";
            }
        };
    }

    // duplicated from uberfire test utils in order to avoid cyclic reference
    public class EventSourceMock<T> implements Event<T> {

        @Override
        public void fire(T event) {
            throw new UnsupportedOperationException("mocking testing class");
        }

        @Override
        public <U extends T> CompletionStage<U> fireAsync(U u) {
            return null;
        }

        @Override
        public <U extends T> CompletionStage<U> fireAsync(U u,
                                                          NotificationOptions notificationOptions) {
            return null;
        }

        @Override
        public Event<T> select(Annotation... qualifiers) {
            throw new UnsupportedOperationException("mocking testing class");
        }

        @Override
        public <U extends T> Event<U> select(Class<U> subtype,
                                             Annotation... qualifiers) {
            throw new UnsupportedOperationException("mocking testing class");
        }

        @Override
        public <U extends T> Event<U> select(TypeLiteral<U> subtype,
                                             Annotation... qualifiers) {
            return null;
        }
    }
}

