/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.canvas.controls.event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class BuildCanvasShapeEventTest {

    @Mock
    AbstractCanvasHandler canvasHandler;

    BuildCanvasShapeEvent tested;

    @Before
    public void setup() throws Exception {
        canvasHandler = mock(AbstractCanvasHandler.class);
    }

    @Test
    public void testBuild() {
        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 0, 0);
        assertEquals(0, tested.getClientX(), 0d);
        assertEquals(0, tested.getClientY(), 0d);

        tested = new BuildCanvasShapeEvent(canvasHandler, null, null, 5, 15);
        assertEquals(5, tested.getClientX(), 0d);
        assertEquals(15, tested.getClientY(), 0d);
    }
}
