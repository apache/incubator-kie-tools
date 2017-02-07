/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.shape.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.ShapeViewExtStub;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShapeImplTest {

    private ShapeViewExtStub view;
    private ShapeImpl<ShapeView> tested;

    @Before
    public void setup() throws Exception {
        this.view = spy(new ShapeViewExtStub());
        this.tested = new ShapeImpl<ShapeView>(view);
    }

    @Test
    public void testState() {
        tested.setUUID("uuid1");
        assertEquals(view,
                     tested.getShapeView());
        assertEquals("uuid1",
                     tested.getUUID());
    }

    @Test
    public void testAfterDraw() {
        tested.afterDraw();
        verify(view,
               times(1)).moveTitleToTop();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(view,
               times(1)).destroy();
    }
}
