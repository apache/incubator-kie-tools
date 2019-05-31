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

package org.kie.workbench.common.stunner.core.client.session.impl;

import java.util.function.Consumer;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class InstanceUtilsTest {

    @Test
    public void testDestroy() {
        final ManagedInstance managedInstance = mock(ManagedInstance.class);
        final Object control = mock(Object.class);
        final Consumer<Object> consumer = mock(Consumer.class);

        InstanceUtils.destroy(managedInstance, control, consumer);

        verify(consumer, times(1)).accept(eq(control));
        verify(managedInstance, times(1)).destroy(eq(control));
    }
}