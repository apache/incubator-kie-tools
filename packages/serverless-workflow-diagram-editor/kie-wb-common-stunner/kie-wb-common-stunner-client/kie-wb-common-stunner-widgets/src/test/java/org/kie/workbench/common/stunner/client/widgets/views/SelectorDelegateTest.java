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


package org.kie.workbench.common.stunner.client.widgets.views;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SelectorDelegateTest {

    @Mock
    private Selector delegate;

    private SelectorDelegate tested;

    @Before
    public void setup() throws Exception {
        tested = new SelectorDelegate() {
            @Override
            protected Selector getDelegate() {
                return delegate;
            }
        };
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAddItem() {
        Object item = mock(Object.class);
        tested.addItem(item);
        verify(delegate, times(1)).addItem(eq(item));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetSelecteItem() {
        Object item = mock(Object.class);
        tested.setSelectedItem(item);
        verify(delegate, times(1)).setSelectedItem(eq(item));
    }

    @Test
    public void testGetSelecteItem() {
        Object item = mock(Object.class);
        when(delegate.getSelectedItem()).thenReturn(item);
        assertEquals(item, tested.getSelectedItem());
    }

    @Test
    public void testClear() {
        tested.clear();
        verify(delegate, times(1)).clear();
    }

    @Test
    public void testOnValueChanged() {
        tested.onValueChanged();
        verify(delegate, times(1)).onValueChanged();
    }

    @Test
    public void testGetView() {
        SelectorView view = mock(SelectorView.class);
        when(delegate.getView()).thenReturn(view);
        assertEquals(view, tested.getView());
    }
}
