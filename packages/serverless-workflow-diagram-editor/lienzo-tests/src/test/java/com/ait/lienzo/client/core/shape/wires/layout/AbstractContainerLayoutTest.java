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

package com.ait.lienzo.client.core.shape.wires.layout;

import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractContainerLayoutTest<T, C extends AbstractContainerLayout<T>> {

    protected Object DEFAULT_LAYOUT = new Object();

    protected C tested;

    protected MultiPath parent;

    protected Text child;

    protected T currentLayout;

    protected Text child2;

    @Before
    public void setUp() {
        parent = new MultiPath().rect(0, 0, 20, 20);
        tested = spy(createInstance());
        child = spy(new Text("test"));
        child2 = new Text("test2");
        currentLayout = (T) mock(Object.class);
    }

    protected T getDefaultLayoutForTest() {
        return (T) DEFAULT_LAYOUT;
    }

    protected C createInstance() {
        return (C) new AbstractContainerLayout<Object>(parent) {
            @Override
            public Object getDefaultLayout() {
                return getDefaultLayoutForTest();
            }
        };
    }

    @Test
    public void addDefaultLayout() {
        tested.add(child);
        final Object layout = tested.getLayout(child);
        assertEquals(layout, getDefaultLayoutForTest());
    }

    @Test
    public void add() {
        tested.add(child, currentLayout);
        final Object layout = tested.getLayout(child);
        assertEquals(layout, currentLayout);
    }

    @Test
    public void execute() {
        tested.add(child, currentLayout);
        tested.add(child2, currentLayout);
        tested.execute();
        verify(tested, times(2)).add(eq(child), eq(currentLayout));
        verify(tested, times(2)).add(eq(child2), eq(currentLayout));
    }

    @Test
    public void remove() {
        tested.add(child);
        assertNotNull(tested.getLayout(child));
        tested.remove(child);
        assertNull(tested.getLayout(child));
    }

    @Test
    public void clear() {
        tested.add(child);
        tested.add(child2);
        assertNotNull(tested.getLayout(child));
        assertNotNull(tested.getLayout(child2));
        tested.clear();
        assertNull(tested.getLayout(child));
        assertNull(tested.getLayout(child2));
    }

    @Test
    public void getDefaultLayout() {
        assertEquals(tested.getDefaultLayout(), getDefaultLayoutForTest());
    }

    @Test
    public void getParentBoundingBox() {
        assertEquals(tested.getParentBoundingBox(), parent.getBoundingBox());
    }
}