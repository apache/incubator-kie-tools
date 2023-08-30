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


package org.kie.workbench.common.stunner.client.lienzo.shape.view;

import java.util.Arrays;

import com.ait.lienzo.client.core.animation.IAnimationHandle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class MultipleAnimationHandleTest {

    @Mock
    private IAnimationHandle handle1;

    @Mock
    private IAnimationHandle handle2;

    private MultipleAnimationHandle tested;

    @Before
    public void setup() throws Exception {
        tested = new MultipleAnimationHandle(Arrays.asList(handle1, handle2));
    }

    @Test
    public void testRun() {
        tested.run();
        verify(handle1, times(1)).run();
        verify(handle2, times(1)).run();
        verify(handle1, never()).stop();
        verify(handle2, never()).stop();
    }

    @Test
    public void testStop() {
        tested.stop();
        verify(handle1, times(1)).stop();
        verify(handle2, times(1)).stop();
        verify(handle1, never()).run();
        verify(handle2, never()).run();
    }

    @Test
    public void testIsRunning() {
        when(handle1.isRunning()).thenReturn(false);
        when(handle2.isRunning()).thenReturn(false);
        assertFalse(tested.isRunning());
        when(handle1.isRunning()).thenReturn(true);
        when(handle2.isRunning()).thenReturn(false);
        assertTrue(tested.isRunning());
        when(handle1.isRunning()).thenReturn(false);
        when(handle2.isRunning()).thenReturn(true);
        assertTrue(tested.isRunning());
        when(handle1.isRunning()).thenReturn(true);
        when(handle2.isRunning()).thenReturn(true);
        assertTrue(tested.isRunning());
    }
}
