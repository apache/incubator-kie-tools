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

package org.guvnor.structure.repositories.impl.git.event;

import org.junit.Test;

import java.util.stream.IntStream;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class NotificationTypeTest {

    @Test
    public void testSuccess() {
        doTest(0, 0, NotificationType.SUCCESS);
    }

    @Test
    public void testWarning() {
        doTest(1, 30, NotificationType.WARNING);
    }

    @Test
    public void testError() {
        doTest(31, 255, NotificationType.ERROR);
    }

    private void doTest(int minValue, int maxValue, NotificationType expectedType) {
        IntStream.rangeClosed(minValue, maxValue)
                .forEach(exitCode -> {
                    assertTrue(expectedType.inRange(exitCode));
                    assertSame(expectedType, NotificationType.fromExitCode(exitCode));
                });
    }
}
