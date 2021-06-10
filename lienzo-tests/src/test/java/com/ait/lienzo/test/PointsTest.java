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

import static org.junit.Assert.assertEquals;

/**
 * Consider Point2D#toString method stubbed as no-op. So expect an empty js array to string as result,
 * but the test will run as no native/final problematic methods present.
 * <p>
 * If you need to specify custom mocking behavior for np-op methods:
 *
 * @author Roger Martinez
 * @See com.ait.lienzo.test.PointsMockTest
 * <p>
 * If you need to specify custom implementations:
 * @See com.ait.lienzo.test.stub.custom.StubPointsTest
 * @since 1.0
 */
@RunWith(LienzoMockitoTestRunner.class)
public class PointsTest {

    public class MyLienzo {

        private final Point2D p;

        public MyLienzo(final Point2D p) {
            this.p = p;
        }

        public Point2D test(final Point2D p) {
            return this.p.add(p);
        }
    }

    private MyLienzo myLienzo;

    @Before
    public void setup() {
        myLienzo = new MyLienzo(new Point2D(0, 0));
    }

    @Test
    public void test() {
        final Point2D p = myLienzo.test(new Point2D(0, 0));

        assertEquals(0, p.getX(), 0);

        assertEquals(0, p.getY(), 0);
    }
}
