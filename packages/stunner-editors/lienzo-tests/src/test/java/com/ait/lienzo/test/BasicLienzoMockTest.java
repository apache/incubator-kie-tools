/*
 * Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
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

package com.ait.lienzo.test;

import com.ait.lienzo.client.core.shape.Layer;
import com.ait.lienzo.client.core.shape.Rectangle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Basic test that asserts the behavior of the <code>Layer</code> instead of its state.
 * <p>
 * What happens on this tests is:
 * <p>
 * 1.- By annotating the unit test with the <code>@RunWith( LienzoMockitoTestRunner.class )</code> the <code>Layer</code> class
 * is being processed and all native/final methods have been stripped, so can be mocked.
 * <p>
 * 2.- The Layer is mocked using regular Mockito annotations, so you can use regular Mockito API to verity it's behavior.
 * <p>
 * Note: You can either use gwt mocks, mockito spies and whatever other functionality, if you need so.
 *
 * @author Roger Martinez
 * @since 1.0
 */
@RunWith(LienzoMockitoTestRunner.class)
public class BasicLienzoMockTest {

    public class MyLienzo {

        private final Layer layer;

        private final Rectangle rectangle = new Rectangle(50, 50);

        public MyLienzo(final Layer layer) {
            this.layer = layer;
        }

        public void test() {
            rectangle.setFillColor("#0000FF");

            layer.add(rectangle);

            layer.draw();
        }

        public void test2() {
            final int w = layer.getWidth();

            rectangle.setX(w + 100);
        }

        public Rectangle getRectangle() {
            return rectangle;
        }
    }

    @Mock
    Layer layer;

    private MyLienzo myLienzo;

    @Before
    public void setup() {
        when(layer.getWidth()).thenReturn(300);

        myLienzo = new MyLienzo(layer);
    }

    @Test
    public void test() {
        myLienzo.test();

        verify(layer, times(1)).add(any(Rectangle.class));

        verify(layer, times(1)).draw();

        final String fColor = myLienzo.getRectangle().getFillColor();

        Assert.assertEquals("#0000FF", fColor);
    }

    @Test
    public void test2() {
        myLienzo.test2();

        verify(layer, times(1)).getWidth();

        Assert.assertEquals(400, myLienzo.getRectangle().getX(), 0);
    }

    /**
     * Method getFillAlpha can be mocked as the final modifier from original class has been removed.
     */
    @Test
    public void testMockFillAlhpaFinalMethod() {
        final Rectangle rrr = mock(Rectangle.class);

        when(rrr.getFillAlpha()).thenReturn(0.5d);

        Assert.assertEquals(0.5, rrr.getFillAlpha(), 0);
    }

    /**
     * Method uuid can be mocked as the final modifier from original class has been removed.
     */
    @Test
    public void testMockUUIDFinalMethod() {
        final Rectangle rrr = mock(Rectangle.class);

        when(rrr.uuid()).thenReturn("mockedUUID");

        Assert.assertEquals("mockedUUID", rrr.uuid());
    }
}
