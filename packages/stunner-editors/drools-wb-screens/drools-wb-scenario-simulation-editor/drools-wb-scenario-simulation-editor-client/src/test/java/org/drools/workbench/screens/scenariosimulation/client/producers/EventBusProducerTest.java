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

package org.drools.workbench.screens.scenariosimulation.client.producers;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwt.event.shared.EventBus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(LienzoMockitoTestRunner.class)
public class EventBusProducerTest {

    private EventBusProducer eventBusProducer;

    @Before
    public void setup() {
        eventBusProducer = new EventBusProducer();
    }

    @Test
    public void getEventBus() {
        final EventBus retrieved = eventBusProducer.getEventBus();
        assertNotNull(retrieved);
    }
}