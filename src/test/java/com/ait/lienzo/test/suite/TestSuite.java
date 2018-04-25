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

package com.ait.lienzo.test.suite;

import com.ait.lienzo.client.core.shape.AbstractOffsetMultiPointShapeTest;
import com.ait.lienzo.client.core.shape.PolyLineTest;
import com.ait.lienzo.client.core.shape.TextBoundsWrapTest;
import com.ait.lienzo.client.core.shape.TextLineBreakWrapTest;
import com.ait.lienzo.client.core.shape.wires.MagnetManagerTest;
import com.ait.lienzo.client.core.shape.wires.SelectionManagerTest;
import com.ait.lienzo.client.core.types.BoundingBoxTest;
import com.ait.lienzo.client.widget.LienzoHandlerManagerTest;
import com.ait.lienzo.client.widget.LienzoPanelTest;
import com.ait.lienzo.test.BasicLienzoMockTest;
import com.ait.lienzo.test.BasicLienzoStateTest;
import com.ait.lienzo.test.JSOMockTest;
import com.ait.lienzo.test.PointsMockTest;
import com.ait.lienzo.test.PointsTest;
import com.ait.lienzo.test.stub.custom.StubPointsTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Lienzo testing suite.
 * @author Roger Martinez
 * @since 1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractOffsetMultiPointShapeTest.class,
        BasicLienzoMockTest.class,
        BasicLienzoStateTest.class,
        BoundingBoxTest.class,
        JSOMockTest.class,
        LienzoHandlerManagerTest.class,
        LienzoPanelTest.class,
        MagnetManagerTest.class,
        PointsMockTest.class,
        PointsTest.class,
        PolyLineTest.class,
        SelectionManagerTest.class,
        StubPointsTest.class,
        TextBoundsWrapTest.class,
        TextLineBreakWrapTest.class
})
public class TestSuite {

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }
}
