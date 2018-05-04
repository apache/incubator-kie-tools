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

package org.uberfire.backend.server.security.adapter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import static org.junit.Assert.*;

public class GroupAdapterAuthorizationSourceTest {

    GroupAdapterAuthorizationSource adapter = new GroupAdapterAuthorizationSource();

    @Test
    public void testConcurrency() throws Exception {
        final ExecutorService service = Executors.newFixedThreadPool(4);
        final CountDownLatch latch = new CountDownLatch(12);
        for (int i = 0; i < 12; i++) {
            service.submit(() -> {
                try {
                    adapter.collectEntitiesFromAdapters(null,
                                                        null);
                    latch.countDown();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            });
        }
        service.shutdown();
        assertTrue(latch.await(3,
                               TimeUnit.SECONDS));
    }
}
