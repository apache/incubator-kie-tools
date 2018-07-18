/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package com.ait.lienzo.client.core.suite;

import com.ait.lienzo.client.core.shape.AbstractOffsetMultiPointShapeTest;
import com.ait.lienzo.client.core.shape.wires.MagnetManagerTest;
import com.ait.lienzo.client.core.shape.wires.SelectionManagerTest;
import com.ait.lienzo.client.core.shape.wires.WiresConnectorTest;
import com.ait.lienzo.client.core.shape.wires.WiresContainerTest;
import com.ait.lienzo.client.core.shape.wires.WiresManagerTest;
import com.ait.lienzo.client.core.shape.wires.WiresShapeControlHandleListTest;
import com.ait.lienzo.client.core.shape.wires.WiresShapeTest;
import com.ait.lienzo.client.core.shape.wires.handlers.WiresShapeHandlerImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresCompositeControlImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresCompositeShapeHandlerTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresConnectorControlImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresControlPointHandlerImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresDockingControlImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresParentPickerControlImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeControlImplTest;
import com.ait.lienzo.client.core.shape.wires.handlers.impl.WiresShapeLocationControlImplTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Wires testing suite.
 * @author Roger Martinez
 * @since 1.0.0-RC2
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AbstractOffsetMultiPointShapeTest.class,
        MagnetManagerTest.class,
        SelectionManagerTest.class,
        WiresCompositeShapeHandlerTest.class,
        WiresConnectorControlImplTest.class,
        WiresConnectorTest.class,
        WiresContainerTest.class,
        WiresDockingControlImplTest.class,
        WiresManagerTest.class,
        WiresParentPickerControlImplTest.class,
        WiresShapeControlHandleListTest.class,
        WiresShapeControlImplTest.class,
        WiresShapeHandlerImplTest.class,
        WiresShapeLocationControlImplTest.class,
        WiresShapeTest.class,
        WiresCompositeControlImplTest.class,
        WiresControlPointHandlerImplTest.class
})
public class WiresTestSuite {

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
}
