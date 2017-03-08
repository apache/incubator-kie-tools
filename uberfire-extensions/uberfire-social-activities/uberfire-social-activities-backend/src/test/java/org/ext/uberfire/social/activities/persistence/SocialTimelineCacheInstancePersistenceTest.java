/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.ext.uberfire.social.activities.persistence;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;

import static org.junit.Assert.*;

public class SocialTimelineCacheInstancePersistenceTest {

    SocialTimelineCacheInstancePersistence.SocialCacheControl cacheControl;
    Integer threshold;

    @Before
    public void setup() {
        //default
        threshold = 100;
        final SocialTimelineCacheInstancePersistence cacheInstancePersistence = new SocialTimelineCacheInstancePersistenceUnitTestWrapper();
        cacheControl = cacheInstancePersistence.new SocialCacheControl();
        assertTrue(PriorityDisposableRegistry.getDisposables().contains(cacheInstancePersistence));
    }

    @Test
    public void socialCacheControlTest() {
        assertFalse(cacheControl.needToPersist());
        registerEvents(threshold + 1);
        assertTrue(cacheControl.needToPersist());
        cacheControl.reset();
        assertFalse(cacheControl.needToPersist());
    }

    private void registerEvents(int numberOfEvents) {
        for (int i = 0; i < numberOfEvents; i++) {
            cacheControl.registerNewEvent();
            cacheControl.registerNewEvent();
            cacheControl.registerNewEvent();
        }
    }
}
