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


package org.kie.workbench.common.stunner.core.graph.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.cannotControlPointsBeUpdated;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkAddControlPoint;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkDeleteControlPoint;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkUpdateControlPoint;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.isAddingControlPointIndexForbidden;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.isControlPointIndexInvalid;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.isControlPointInvalid;

@RunWith(MockitoJUnitRunner.class)
public class ControlPointValidationsTest {

    private ControlPoint controlPoint;
    private ControlPoint[] controlPoints;

    @Before
    public void setup() {
        controlPoint = ControlPoint.build(0, 0);
        controlPoints = new ControlPoint[]{controlPoint};
    }

    @Test
    public void testInvalidIndexes() {
        assertFalse(isControlPointIndexInvalid.test(0));
        assertFalse(isControlPointIndexInvalid.test(1));
        assertFalse(isControlPointIndexInvalid.test(1425));
        assertTrue(isControlPointIndexInvalid.test(-1));
        assertTrue(isControlPointIndexInvalid.test(-4543));
    }

    @Test
    public void testInvalidControlPoint() {
        assertFalse(isControlPointInvalid.test(controlPoint));
        controlPoint.setLocation(null);
        assertTrue(isControlPointInvalid.test(controlPoint));
    }

    @Test
    public void testInvalidControlPointAddIndex() {
        assertFalse(isAddingControlPointIndexForbidden.test(controlPoints, 1));
        assertFalse(isAddingControlPointIndexForbidden.test(controlPoints, 0));
        assertTrue(isAddingControlPointIndexForbidden.test(controlPoints, 2));
        assertTrue(isAddingControlPointIndexForbidden.test(controlPoints, 3));
        assertFalse(isAddingControlPointIndexForbidden.test(null, 0));
        assertTrue(isAddingControlPointIndexForbidden.test(null, 1));
        assertTrue(isAddingControlPointIndexForbidden.test(null, 2));
        assertTrue(isAddingControlPointIndexForbidden.test(null, 3));
    }

    @Test
    public void testCannotUpdateControlPoints() {
        ControlPoint controlPoint2 = ControlPoint.build(1, 1);
        ControlPoint[] controlPoints2 = new ControlPoint[]{controlPoint2};
        assertFalse(cannotControlPointsBeUpdated.test(controlPoints, controlPoints2));
        controlPoints2 = new ControlPoint[0];
        assertTrue(cannotControlPointsBeUpdated.test(controlPoints, controlPoints2));
        assertTrue(cannotControlPointsBeUpdated.test(controlPoints, null));
        assertTrue(cannotControlPointsBeUpdated.test(null, controlPoints2));
    }

    @Test
    public void testValidCheckAddControlPoint() {
        ControlPoint cp = ControlPoint.build(1, 1);
        checkAddControlPoint(controlPoints, cp, 0);
        checkAddControlPoint(controlPoints, cp, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCheckAddControlPoint() {
        ControlPoint cp = ControlPoint.build(1, 1);
        checkAddControlPoint(controlPoints, cp, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCheckAddControlPoint2() {
        ControlPoint cp = ControlPoint.build(1, 1);
        checkAddControlPoint(controlPoints, cp, 3);
    }

    @Test
    public void testValidCheckDeleteControlPoint() {
        checkDeleteControlPoint(controlPoints, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCheckDeleteControlPoint() {
        checkDeleteControlPoint(controlPoints, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCheckDeleteControlPoint2() {
        checkDeleteControlPoint(controlPoints, 2);
    }

    @Test
    public void testValidCheckUpdateControlPoints() {
        ControlPoint controlPoint2 = ControlPoint.build(1, 1);
        ControlPoint[] controlPoints2 = new ControlPoint[]{controlPoint2};
        checkUpdateControlPoint(controlPoints, controlPoints2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCheckUpdateControlPoints() {
        ControlPoint[] controlPoints2 = new ControlPoint[0];
        checkUpdateControlPoint(controlPoints, controlPoints2);
    }
}
