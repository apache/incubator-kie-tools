/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.metadata.backend.infinispan.provider;

import org.infinispan.client.hotrod.impl.RemoteCacheImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InfinispanPingServiceTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RemoteCacheImpl remoteCache;

    @Before
    public void setUp() {
        when(remoteCache.ping().isSuccess()).thenReturn(true);
    }

    @Test
    public void testPingSuccess() {
        {
            InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
            assertTrue(service.ping());
            service.stop();
        }

        {
            when(remoteCache.ping().isSuccess()).thenReturn(false);
            InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
            assertFalse(service.ping());
            service.stop();
        }
    }

    @Test
    public void testPingFailure() {
        when(remoteCache.ping().isSuccess()).thenThrow(new RuntimeException("error"));
        InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
        assertFalse(service.ping());
        service.stop();
    }

    @Test
    public void testExternalTimeoutVariable() {
        {
            InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
            int result = service.getTimeoutOrElse(InfinispanPingService.PING, 5);
            assertEquals(5, result);
        }

        {
            System.setProperty(InfinispanPingService.PING, String.valueOf(4));
            InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
            int result = service.getTimeoutOrElse(InfinispanPingService.PING, 5);
            assertEquals(4, result);
        }

        {
            System.setProperty(InfinispanPingService.PING, "");
            InfinispanPingService service = spy(new InfinispanPingService(remoteCache));
            int result = service.getTimeoutOrElse(InfinispanPingService.PING, 5);
            assertEquals(5, result);
        }
    }
}
