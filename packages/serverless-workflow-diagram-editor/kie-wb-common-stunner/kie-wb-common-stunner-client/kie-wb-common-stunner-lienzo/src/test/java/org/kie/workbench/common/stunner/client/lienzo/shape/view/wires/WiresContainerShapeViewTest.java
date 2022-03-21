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

package org.kie.workbench.common.stunner.client.lienzo.shape.view.wires;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.shape.HasChildren;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;
import org.mockito.Mock;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class WiresContainerShapeViewTest {

    @Mock
    private WiresShapeControl control;

    @Test
    @SuppressWarnings("unchecked")
    public void checkDestructionDestroysChildren() {
        final WiresContainerShapeView parent = makeShape();
        final WiresContainerShapeView child1 = spy(makeShape());
        final WiresContainerShapeView child2 = spy(makeShape());

        parent.addChild(child1,
                        HasChildren.Layout.CENTER);
        parent.addChild(child2,
                        HasChildren.Layout.CENTER);

        parent.destroy();

        verify(child1).destroy();
        verify(child2).destroy();
    }

    private WiresContainerShapeView makeShape() {
        return new WiresContainerShapeView(new ViewEventType[]{},
                                           new MultiPath()) {
            @Override
            public WiresShapeControl getControl() {
                return control;
            }
        };
    }
}
