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


package org.kie.workbench.common.stunner.client.lienzo.wires;

import com.ait.lienzo.client.core.shape.wires.handlers.MouseEvent;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresCompositeControl;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class DelegateWiresCompositeControlTest {

    @Mock
    private WiresCompositeControl delegate;

    private DelegateWiresCompositeControl tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        tested = new DelegateWiresCompositeControl() {
            @Override
            protected WiresCompositeControl getDelegate() {
                return delegate;
            }
        };
    }

    @Test
    public void testControlMethods() {
        tested.execute();
        verify(delegate, times(1)).execute();
        tested.accept();
        verify(delegate, times(1)).accept();
        tested.isOutOfBounds(1d, 2d);
        verify(delegate, times(1)).isOutOfBounds(eq(1d), eq(2d));
        tested.clear();
        verify(delegate, times(1)).clear();
        tested.reset();
        verify(delegate, times(1)).reset();
        tested.onMoveStart(1d, 2d);
        verify(delegate, times(1)).onMoveStart(eq(1d), eq(2d));
        tested.onMove(3d, 4d);
        verify(delegate, times(1)).onMove(eq(3d), eq(4d));
        tested.onMoveComplete();
        verify(delegate, times(1)).onMoveComplete();
        final MouseEvent mouseEvent = mock(MouseEvent.class);
        tested.onMouseClick(mouseEvent);
        verify(delegate, times(1)).onMouseClick(eq(mouseEvent));
        tested.onMouseDown(mouseEvent);
        verify(delegate, times(1)).onMouseDown(eq(mouseEvent));
        tested.onMouseUp(mouseEvent);
        verify(delegate, times(1)).onMouseUp(eq(mouseEvent));
        tested.isAllowed();
        verify(delegate, times(1)).isAllowed();
    }

    @Test
    public void testDestroy() {
        tested.destroy();
        verify(delegate, times(1)).destroy();
    }
}
