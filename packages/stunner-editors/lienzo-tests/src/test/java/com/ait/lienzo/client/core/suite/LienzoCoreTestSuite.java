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


package com.ait.lienzo.client.core.suite;

import com.ait.lienzo.client.core.image.ImageElementProxyTest;
import com.ait.lienzo.client.core.image.ImageProxyTest;
import com.ait.lienzo.client.core.image.ImageStripsTest;
import com.ait.lienzo.client.core.image.ImageTest;
import com.ait.lienzo.client.core.mediator.MouseWheelZoomMediatorTest;
import com.ait.lienzo.client.core.shape.BezierCurveTest;
import com.ait.lienzo.client.core.shape.BoundingBoxComputationsTest;
import com.ait.lienzo.client.core.shape.MultiPathTest;
import com.ait.lienzo.client.core.shape.OrthogonalPolyLineTest;
import com.ait.lienzo.client.core.shape.PolyLineTest;
import com.ait.lienzo.client.core.shape.TextBoundsWrapTest;
import com.ait.lienzo.client.core.shape.TextLineBreakWrapTest;
import com.ait.lienzo.client.core.types.BoundingBoxTest;
import com.ait.lienzo.client.core.util.GeometryTest;
import com.ait.lienzo.client.widget.panel.LienzoBoundsPanelTest;
import com.ait.lienzo.client.widget.panel.mediators.BoundaryTransformMediatorTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GeometryTest.class,
        BoundingBoxTest.class,
        BoundingBoxComputationsTest.class,
        ImageElementProxyTest.class,
        ImageProxyTest.class,
        ImageStripsTest.class,
        ImageTest.class,
        MultiPathTest.class,
        OrthogonalPolyLineTest.class,
        PolyLineTest.class,
        BezierCurveTest.class,
        TextBoundsWrapTest.class,
        TextLineBreakWrapTest.class,
        MouseWheelZoomMediatorTest.class,
        BoundaryTransformMediatorTest.class,
        LienzoBoundsPanelTest.class,
})
public class LienzoCoreTestSuite {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
}
