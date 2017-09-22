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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresScalableContainer;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.mockito.Mock;

import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DecoratedShapeViewTest {

    private static ViewEventType[] viewEventTypes = {};
    private final static MultiPath PATH = new MultiPath();

    private final double width = 5;
    private final double height = 5;

    @Mock
    private WiresTextDecorator textDecorator;

    private WiresScalableContainer container;

    private DecoratedShapeView<WiresShapeViewExt> tested;

    @Before
    public void setup() throws Exception {
        container = new WiresScalableContainer();
        this.tested = new DecoratedShapeView<WiresShapeViewExt>(viewEventTypes,
                                                                container,
                                                                PATH,
                                                                width,
                                                                height);
        WiresShapeViewExtTest.setPrivateField(WiresShapeViewExt.class,
                                              tested,
                                              "textViewDecorator",
                                              textDecorator);
    }

    @Test
    public void testTitle() {
        //setTitle should not throw an exception when called with a null argument
        tested.setTitle(null);
    }

    @Test
    public void testTextWrapBoundariesUpdatesOnRefresh() {
        tested.refresh();
        verify(textDecorator).setTextBoundaries(PATH.getBoundingBox());
    }

    @Test
    public void testTextWrapBoundariesUpdatesOnResize() {
        tested.resize(0,
                      0,
                      10,
                      10,
                      true);
        verify(textDecorator).setTextBoundaries(PATH.getBoundingBox());
    }

    @Test
    public void testTextWrapBoundariesUpdatesOnSetSize() {
        tested.setSize(10,
                       10);
        verify(textDecorator).setTextBoundaries(PATH.getBoundingBox());
    }
}
