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

import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;

public class ControlPointValidations {

    public static Predicate<Integer> isControlPointIndexInvalid = index -> index < 0;

    public static Predicate<ControlPoint> isControlPointInvalid = cp -> null == cp.getLocation();

    public static BiPredicate<ControlPoint[], Integer> isAddingControlPointIndexForbidden =
            (cps, index) -> (null != cps && index > cps.length) || (null == cps && index != 0);

    public static BiPredicate<ControlPoint[], Integer> isDeletingControlPointIndexForbidden =
            (cps, index) -> null != cps && (index + 1) > cps.length;

    public static BiPredicate<ControlPoint[], ControlPoint[]> cannotControlPointsBeUpdated =
            (cps1, cps2) -> (null != cps1 && null == cps2) || (null == cps1 && null != cps2) || (null == cps1)
                    || (cps1.length != cps2.length);

    public static void checkAddControlPoint(final ControlPoint[] controlPoints,
                                            final ControlPoint controlPoint,
                                            final int index) {
        if (isControlPointIndexInvalid.test(index)) {
            throw new IllegalArgumentException("The given index [" + index + "] for the new CP is not valid.");
        }
        if (isAddingControlPointIndexForbidden.test(controlPoints, index)) {
            throw new IllegalArgumentException("Cannot add a new CP at the given index [" + index + "].");
        }
        if (isControlPointInvalid.test(controlPoint)) {
            throw new IllegalArgumentException("The given CP is not valid");
        }
    }

    public static void checkDeleteControlPoint(final ControlPoint[] controlPoints,
                                               final int index) {
        if (isControlPointIndexInvalid.test(index)) {
            throw new IllegalArgumentException("The given index [" + index + "] for the new CP is not valid.");
        }
        if (isDeletingControlPointIndexForbidden.test(controlPoints, index)) {
            throw new IllegalArgumentException("Cannot delete a new CP at the given index [" + index + "].");
        }
    }

    public static void checkUpdateControlPoint(final ControlPoint[] controlPoints1,
                                               final ControlPoint[] controlPoints2) {
        if (cannotControlPointsBeUpdated.test(controlPoints1,
                                              controlPoints2)) {
            throw new IllegalArgumentException("The control points cannot be updated, length differs " +
                                                       "[" + length(controlPoints1) +
                                                       ":" + length(controlPoints2) + "].");
        }
    }

    private static int length(final ControlPoint[] controlPoints) {
        return null != controlPoints ? controlPoints.length : 0;
    }
}
