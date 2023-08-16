/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.appformer.kogito.bridge.client.stateControl.registry.producer;

import org.appformer.client.stateControl.registry.impl.DefaultRegistryImpl;
import org.appformer.kogito.bridge.client.stateControl.registry.impl.KogitoCommandRegistry;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RegistryProducerTest {

    @Mock
    private KogitoCommandRegistry registry;

    private boolean envelopeEnabled = true;

    private CommandRegistryProducer producer;

    @Before
    public void init() {
        producer = new CommandRegistryProducer(() -> envelopeEnabled, () -> registry);
    }

    @Test
    public void testLookup() {
        Assertions.assertThat(producer.lookup())
                .isNotNull()
                .isInstanceOf(KogitoCommandRegistry.class)
                .isSameAs(registry);

        envelopeEnabled = false;

        Assertions.assertThat(producer.lookup())
                .isNotNull()
                .isInstanceOf(DefaultRegistryImpl.class);
    }
}
