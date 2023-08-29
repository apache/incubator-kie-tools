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


package org.kie.workbench.common.stunner.client.lienzo.util;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.test.LienzoMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(LienzoMockitoTestRunner.class)
public class ShapeViewUserDataEncoderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void testSetShapeViewChildrenIds() {
        String uuid = "uuid1";
        // Main group.
        Group mainGroup = new Group();
        Rectangle r1 = new Rectangle(50, 50);
        r1.setID("r1");
        Rectangle r2 = new Rectangle(50, 50);
        r2.setUserData("?someUserDataForR2");
        Rectangle r3 = new Rectangle(50, 50);
        mainGroup.add(r1);
        mainGroup.add(r2);
        mainGroup.add(r3);
        // Sub group.
        Group subGroup = new Group();
        Rectangle r11 = new Rectangle(50, 50);
        r11.setID("r11");
        Rectangle r22 = new Rectangle(50, 50);
        r22.setUserData("?someUserDataForR22");
        subGroup.add(r11);
        subGroup.add(r22);
        mainGroup.add(subGroup);
        // Test encoding ids.
        new ShapeViewUserDataEncoder().setShapeViewChildrenIds(uuid, mainGroup);
        // Id's assertions.
        assertEquals(uuid + "_r1", r1.getID());
        assertEquals(uuid + "?someUserDataForR2", r2.getID());
        assertEquals(uuid, r3.getID());
        assertEquals(uuid + "_r11", r11.getID());
        assertEquals(uuid + "?someUserDataForR22", r22.getID());
    }
}
