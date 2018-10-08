/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ext.uberfire.social.activities.persistence;

import java.util.UUID;
import java.util.function.Consumer;

import org.ext.uberfire.social.activities.model.SocialActivitiesEvent;
import org.ext.uberfire.social.activities.model.SocialEventType;
import org.ext.uberfire.social.activities.model.SocialUser;
import org.ext.uberfire.social.activities.service.SocialEventTypeRepositoryAPI;
import org.ext.uberfire.social.activities.service.SocialUserPersistenceAPI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.commons.cluster.ClusterJMSService;
import org.uberfire.commons.cluster.ClusterService;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SocialClusterMessagingTest {

    @Mock
    private ClusterService clusterService;

    @Mock
    private SocialTimelineCacheClusterPersistence socialTimelineCacheClusterPersistence;

    @Mock
    private SocialUserPersistenceAPI socialUserPersistenceAPI;

    @Mock
    private SocialEventTypeRepositoryAPI socialEventTypeRepositoryAPI;

    @Spy
    @InjectMocks
    private SocialClusterMessaging socialClusterMessaging;

    @Captor
    private ArgumentCaptor<Consumer<SocialMessageWrapper>> socialMessageConsumerCaptor;

    @Before
    public void setUp() throws Exception {
        when(clusterService.isAppFormerClustered()).thenReturn(true);

        socialClusterMessaging.setup(clusterService,
                                     socialTimelineCacheClusterPersistence,
                                     socialUserPersistenceAPI);

        verify(clusterService).createConsumer(eq(ClusterJMSService.DestinationType.PubSub),
                                              eq(SocialClusterMessaging.CHANNEL_NAME),
                                              eq(SocialMessageWrapper.class),
                                              socialMessageConsumerCaptor.capture());
    }

    @Test
    public void testSocialPersistenceEvent_UpdateType() {
        final Consumer<SocialMessageWrapper> messageConsumer = socialMessageConsumerCaptor.getValue();
        final SocialMessageWrapper message = mock(SocialMessageWrapper.class);
        when(message.getNodeId()).thenReturn(UUID.randomUUID().toString());

        // mock persistence event
        when(message.getMessageType()).thenReturn(SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE);
        when(message.getSubMessageType()).thenReturn(SocialClusterMessage.UPDATE_TYPE_EVENT);

        final SocialActivitiesEvent socialActivitiesEvent = mock(SocialActivitiesEvent.class);
        when(message.getEvent()).thenReturn(socialActivitiesEvent);

        final String socialActivitiesEventType = "type";
        when(socialActivitiesEvent.getType()).thenReturn(socialActivitiesEventType);

        final SocialEventType foundType = mock(SocialEventType.class);
        when(socialEventTypeRepositoryAPI.findType(socialActivitiesEventType)).thenReturn(foundType);

        messageConsumer.accept(message);

        verify(socialTimelineCacheClusterPersistence).persist(any(SocialActivitiesEvent.class),
                                                              eq(foundType),
                                                              eq(false));
        verify(socialTimelineCacheClusterPersistence, never()).persist(any(SocialUser.class),
                                                                       any(SocialActivitiesEvent.class));
    }

    @Test
    public void testSocialPersistenceEvent_UpdateUser() {
        final Consumer<SocialMessageWrapper> messageConsumer = socialMessageConsumerCaptor.getValue();
        final SocialMessageWrapper message = mock(SocialMessageWrapper.class);
        when(message.getNodeId()).thenReturn(UUID.randomUUID().toString());

        // mock persistence event
        when(message.getMessageType()).thenReturn(SocialClusterMessage.SOCIAL_FILE_SYSTEM_PERSISTENCE);
        when(message.getSubMessageType()).thenReturn(SocialClusterMessage.UPDATE_USER_EVENT);

        final SocialActivitiesEvent socialActivitiesEvent = mock(SocialActivitiesEvent.class);
        when(message.getEvent()).thenReturn(socialActivitiesEvent);

        final SocialUser socialUser = mock(SocialUser.class);
        when(message.getUser()).thenReturn(socialUser);

        messageConsumer.accept(message);

        verify(socialTimelineCacheClusterPersistence, never()).persist(any(SocialActivitiesEvent.class),
                                                                       any(SocialEventType.class),
                                                                       anyBoolean());
        verify(socialTimelineCacheClusterPersistence).persist(eq(socialUser),
                                                              any(SocialActivitiesEvent.class));
    }
}
