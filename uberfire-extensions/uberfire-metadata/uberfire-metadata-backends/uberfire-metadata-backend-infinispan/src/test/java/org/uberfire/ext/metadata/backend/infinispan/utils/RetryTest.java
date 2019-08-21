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

package org.uberfire.ext.metadata.backend.infinispan.utils;

import org.junit.Test;
import org.uberfire.ext.metadata.backend.infinispan.exceptions.RetryException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RetryTest {

    @Test
    public void testRetry() {

        {
            Retry retry = new Retry(5, () -> {
            });

            retry.run();

            assertEquals(5, retry.getRemainingRetries());
            assertTrue(retry.isFinished());
        }

        {
            Retry retry = new Retry(5, () -> {
                throw new RuntimeException("This should fail right here");
            });

            try {
                retry.run();
            } catch (Exception e) {
                assertTrue(e instanceof RetryException);
            }

            assertEquals(0, retry.getRemainingRetries());
            assertFalse(retry.isFinished());
        }
    }
}