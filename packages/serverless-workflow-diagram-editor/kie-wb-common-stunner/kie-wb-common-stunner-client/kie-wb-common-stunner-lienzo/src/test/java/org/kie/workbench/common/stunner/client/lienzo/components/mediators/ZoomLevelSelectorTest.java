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


package org.kie.workbench.common.stunner.client.lienzo.components.mediators;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ZoomLevelSelectorTest {

    @Mock
    private ZoomLevelSelector.View view;

    @Mock
    private Command onFitToSize;

    @Mock
    private Command onDecreaseLevel;

    @Mock
    private Command onIncreaseLevel;

    private ZoomLevelSelector tested;

    @Before
    public void setUp() {
        tested = new ZoomLevelSelector(view);
        tested.onIncreaseLevel(onIncreaseLevel);
        tested.onDecreaseLevel(onDecreaseLevel);
        tested.onScaleToFitSize(onFitToSize);
    }

    @Test
    public void testInit() {
        tested.init();
        verify(view, times(1)).init(eq(tested));
    }

    @Test
    public void testSetText() {
        tested.setText("hiya");
        verify(view, times(1)).setText(eq("hiya"));
    }

    @Test
    public void testAdd() {
        Command callback = mock(Command.class);
        tested.add("item1", callback);
        verify(view, times(1)).add(eq("item1"), eq(callback));
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(view, times(1)).clear();
    }

    @Test
    public void testSetEnabled() {
        tested.setEnabled(true);
        verify(view, times(1)).setEnabled(eq(true));
        tested.setEnabled(false);
        verify(view, times(1)).setEnabled(eq(false));
    }

    @Test
    public void testOnFitToSize() {
        tested.onScaleToFitSize();
        verify(onFitToSize, times(1)).execute();
    }

    @Test
    public void testOnIncreaseLevel() {
        tested.onIncreaseLevel();
        verify(onIncreaseLevel, times(1)).execute();
    }

    @Test
    public void testOnDecreaseLevel() {
        tested.onDecreaseLevel();
        verify(onDecreaseLevel, times(1)).execute();
    }

    @Test
    public void testGetElement() {
        elemental2.dom.HTMLElement w = mock(elemental2.dom.HTMLElement.class);
        when(view.getElement()).thenReturn(w);
        assertEquals(w, tested.getElement());
    }
}
