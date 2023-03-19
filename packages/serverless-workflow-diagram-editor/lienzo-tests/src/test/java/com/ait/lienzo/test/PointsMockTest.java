/*
 *
 *    Copyright (c) 2017 Ahome' Innovation Technologies. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.ait.lienzo.test;

import com.ait.lienzo.client.core.types.Point2D;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Please before reading this:
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.PointsTest
 * <p>
 * As the Point2D stub has no final modifiers, most of the methods can be mocked as in this example.
 * <p>
 * As mocking any primitive you can test its behavior instead of its state.
 * <p>
 * If you need to specify custom implementations:
 * @See com.ait.lienzo.test.stub.custom.StubPointsTest
 * @since 1.0
 */
@RunWith(LienzoMockitoTestRunner.class)
public class PointsMockTest {

    public class MyLienzo {

        private final Point2D p;

        public MyLienzo(final Point2D p) {
            this.p = p;
        }

        public Point2D test(final Point2D p) {
            return this.p.add(p);
        }
    }

    @Mock
    Point2D p;

    private MyLienzo myLienzo;

    @Before
    public void setup() {
        when(p.getX()).thenReturn(0d);

        when(p.getY()).thenReturn(0d);

        myLienzo = new MyLienzo(p);
    }

    @Test
    public void test() {
        final Point2D p2 = mock(Point2D.class);

        when(p2.toString()).thenReturn("This is the point #2");

        doReturn(p2).when(p).add(any(Point2D.class));

        when(p.add(any(Point2D.class))).thenReturn(p2);

        final Point2D p = myLienzo.test(new Point2D(0, 0));

        assertEquals(0, p.getX(), 0);

        assertEquals(0, p.getY(), 0);
    }
}
