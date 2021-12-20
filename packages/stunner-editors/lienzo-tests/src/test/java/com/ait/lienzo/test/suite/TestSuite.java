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

import com.ait.lienzo.test.BasicLienzoMockTest;
import com.ait.lienzo.test.LienzoCoreAttributesTest;
import com.ait.lienzo.test.PointsMockTest;
import com.ait.lienzo.test.PointsTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Lienzo testing suite.
 *
 * @author Roger Martinez
 * @since 1.0
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        BasicLienzoMockTest.class,
        LienzoCoreAttributesTest.class,
        PointsTest.class,
        PointsMockTest.class,
})
public class TestSuite {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
}