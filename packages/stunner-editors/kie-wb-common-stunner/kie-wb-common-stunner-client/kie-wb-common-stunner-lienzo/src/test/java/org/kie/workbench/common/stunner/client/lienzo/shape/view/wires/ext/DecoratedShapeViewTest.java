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


package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.ext;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresScalableContainer;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DecoratedShapeViewTest extends AbstractWiresShapeViewText {

    private static ViewEventType[] viewEventTypes = {};
    private final static MultiPath PATH = new MultiPath();

    private final double width = 5;
    private final double height = 5;

    private WiresScalableContainer container;

    @Before
    public void setup() throws Exception {
        container = new WiresScalableContainer();
        super.setUp();
    }

    @Override
    public WiresShapeViewExt createInstance() {
        return new DecoratedShapeView<>(viewEventTypes,
                                        container,
                                        PATH,
                                        width,
                                        height);
    }

    @Test
    public void testTextWrapBoundariesUpdatesOnRefresh() {
        tested.refresh();
        verify(textDecorator).update();
    }
}
