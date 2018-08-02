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

package org.kie.workbench.common.dmn.client.widgets.layer;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementUpdatedEvent;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DMNGridLayerProducerTest {

    @Mock
    private DMNGridLayer layer;

    @Mock
    private CanvasHandler canvasHandler;

    @Mock
    private Element element;

    private DMNGridLayerProducer producer;

    @Before
    public void setup() {
        this.producer = new DMNGridLayerProducer() {
            @Override
            DMNGridLayer makeGridLayer() {
                return layer;
            }
        };
    }

    @Test
    public void testGetGridLayer() {
        assertThat(producer.getGridLayer()).isEqualTo(layer);

        //Check same instance is re-used
        assertThat(producer.getGridLayer()).isEqualTo(layer);
    }

    @Test
    public void testOnCanvasElementUpdatedEvent() {
        final CanvasElementUpdatedEvent event = new CanvasElementUpdatedEvent(canvasHandler, element);

        producer.onCanvasElementUpdatedEvent(event);

        verify(layer).batch();
    }
}
