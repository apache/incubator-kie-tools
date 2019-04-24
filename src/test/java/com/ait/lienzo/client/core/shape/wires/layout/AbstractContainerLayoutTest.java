/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package com.ait.lienzo.client.core.shape.wires.layout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.ait.lienzo.client.core.shape.IPrimitive;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.Text;
import com.ait.lienzo.client.core.shape.TextBoundsWrap;
import com.ait.lienzo.client.core.types.BoundingBox;
import com.ait.lienzo.test.LienzoMockitoTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(LienzoMockitoTestRunner.class)
public class AbstractContainerLayoutTest<T, C extends AbstractContainerLayout<T>>
{
    protected Object                  DEFAULT_LAYOUT = new Object();

    protected C tested;

    protected MultiPath               parent;

    protected Text           child;

    @Mock
    protected T                       currentLayout;

    protected Text           child2;

    @Before
    public void setUp()
    {
        parent = new MultiPath().rect(0,0,20,20);
        tested = spy(createInstance());
        child = spy(new Text("test"));
        child2 = new Text("test2");
    }

    protected T getDefaultLayoutForTest()
    {
        return (T) DEFAULT_LAYOUT;
    }

    protected C createInstance()
    {
        return (C) new AbstractContainerLayout<T>(parent)
        {
            @Override public T getDefaultLayout()
            {
                return getDefaultLayoutForTest();
            }
        };
    }

    @Test
    public void addDefaultLayout()
    {
        tested.add(child);
        final Object layout = tested.getLayout(child);
        assertEquals(layout, getDefaultLayoutForTest());
    }

    @Test
    public void add()
    {
        tested.add(child, currentLayout);
        final Object layout = tested.getLayout(child);
        assertEquals(layout, currentLayout);
    }

    @Test
    public void execute()
    {
        tested.add(child, currentLayout);
        tested.add(child2, currentLayout);
        tested.execute();
        verify(tested, times(2)).add(child, currentLayout);
        verify(tested, times(2)).add(child2, currentLayout);
    }

    @Test
    public void remove()
    {
        tested.add(child);
        assertNotNull(tested.getLayout(child));
        tested.remove(child);
        assertNull(tested.getLayout(child));
    }

    @Test
    public void clear()
    {
        tested.add(child);
        tested.add(child2);
        assertNotNull(tested.getLayout(child));
        assertNotNull(tested.getLayout(child2));
        tested.clear();
        assertNull(tested.getLayout(child));
        assertNull(tested.getLayout(child2));
    }

    @Test
    public void getDefaultLayout()
    {
        assertEquals(tested.getDefaultLayout(), getDefaultLayoutForTest());
    }

    @Test
    public void getParentBoundingBox()
    {
        assertEquals(tested.getParentBoundingBox(), parent.getBoundingBox());
    }
}